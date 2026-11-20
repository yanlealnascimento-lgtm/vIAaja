import { useEffect, useState } from 'react'
import { api } from '../api/client.js'
import Aviso from '../components/Aviso.jsx'
import Carregando from '../components/Carregando.jsx'
import Vazio from '../components/Vazio.jsx'
import { moeda } from '../utils/formato.js'

export default function PainelParceiro() {
  const [ofertas, setOfertas] = useState(null)
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')
  const [edicao, setEdicao] = useState({})

  useEffect(() => { carregar() }, [])

  async function carregar() {
    try {
      const lista = await api.get('/parceiro/ofertas')
      setOfertas(lista)
      const inicial = {}
      lista.forEach(o => {
        inicial[o.id] = { preco: o.preco, disponibilidade: o.disponibilidade }
      })
      setEdicao(inicial)
    } catch (e) {
      setErro(e.message)
      setOfertas([])
    }
  }

  function alterar(id, campo, valor) {
    setEdicao(anterior => ({ ...anterior, [id]: { ...anterior[id], [campo]: valor } }))
  }

  async function salvar(id) {
    setErro('')
    setMensagem('')
    try {
      await api.put(`/parceiro/ofertas/${id}`, {
        preco: Number(edicao[id].preco),
        disponibilidade: Number(edicao[id].disponibilidade)
      })
      setMensagem('Oferta atualizada.')
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  async function alternar(oferta) {
    setErro('')
    try {
      const acao = oferta.ativa ? 'desativar' : 'ativar'
      await api.patch(`/parceiro/ofertas/${oferta.id}/${acao}`)
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  if (ofertas === null) return <Carregando />

  return (
    <>
      <h1>Minhas ofertas</h1>
      <p style={{ marginTop: -6, color: 'var(--cinza-600)' }}>
        Mantenha precos e disponibilidade atualizados para aparecer nas buscas e no comparador.
      </p>

      <Aviso tipo="erro">{erro}</Aviso>
      <Aviso tipo="sucesso">{mensagem}</Aviso>

      <div className="cartao">
        {ofertas.length === 0 ? <Vazio>Nenhuma oferta cadastrada.</Vazio> : (
          <table>
            <thead>
              <tr>
                <th>Tipo</th>
                <th>Oferta</th>
                <th>Destino</th>
                <th>Preco</th>
                <th>Disponibilidade</th>
                <th>Situacao</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {ofertas.map(o => (
                <tr key={o.id}>
                  <td><span className="selo cinza">{o.tipo.toLowerCase()}</span></td>
                  <td>{o.titulo}</td>
                  <td>{o.destino}</td>
                  <td>
                    <input type="number" min="0" step="10" style={{ width: 120 }}
                           value={edicao[o.id]?.preco ?? ''}
                           onChange={e => alterar(o.id, 'preco', e.target.value)} />
                  </td>
                  <td>
                    <input type="number" min="0" style={{ width: 90 }}
                           value={edicao[o.id]?.disponibilidade ?? ''}
                           onChange={e => alterar(o.id, 'disponibilidade', e.target.value)} />
                  </td>
                  <td>
                    <span className={`selo ${o.ativa ? 'verde' : 'vermelho'}`}>
                      {o.ativa ? 'ativa' : 'inativa'}
                    </span>
                  </td>
                  <td>
                    <div className="linha-botoes">
                      <button className="botao pequeno" onClick={() => salvar(o.id)}>Salvar</button>
                      <button className="botao secundario pequeno" onClick={() => alternar(o)}>
                        {o.ativa ? 'Desativar' : 'Ativar'}
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      <div className="cartao">
        <h2>Resumo</h2>
        <div className="grade tres">
          <div>
            <div className="rotulo-pequeno">Total de ofertas</div>
            <div className="valor-grande">{ofertas.length}</div>
          </div>
          <div>
            <div className="rotulo-pequeno">Ativas</div>
            <div className="valor-grande">{ofertas.filter(o => o.ativa).length}</div>
          </div>
          <div>
            <div className="rotulo-pequeno">Menor preco</div>
            <div className="valor-grande">
              {ofertas.length ? moeda(Math.min(...ofertas.map(o => Number(o.preco)))) : '-'}
            </div>
          </div>
        </div>
      </div>
    </>
  )
}
