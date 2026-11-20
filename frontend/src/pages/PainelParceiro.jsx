import { useEffect, useState } from 'react'
import { api } from '../api/client.js'
import Aviso from '../components/Aviso.jsx'
import CabecalhoPagina from '../components/CabecalhoPagina.jsx'
import Carregando from '../components/Carregando.jsx'
import Vazio from '../components/Vazio.jsx'
import { moeda, rotulo } from '../utils/formato.js'

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

  function mudou(oferta) {
    const atual = edicao[oferta.id]
    return atual && (Number(atual.preco) !== Number(oferta.preco)
      || Number(atual.disponibilidade) !== Number(oferta.disponibilidade))
  }

  async function salvar(oferta) {
    setErro('')
    setMensagem('')
    try {
      await api.put(`/parceiro/ofertas/${oferta.id}`, {
        preco: Number(edicao[oferta.id].preco),
        disponibilidade: Number(edicao[oferta.id].disponibilidade)
      })
      setMensagem(`${oferta.titulo}: preço e disponibilidade salvos.`)
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  async function alternar(oferta) {
    setErro('')
    setMensagem('')
    try {
      await api.patch(`/parceiro/ofertas/${oferta.id}/${oferta.ativa ? 'desativar' : 'ativar'}`)
      setMensagem(oferta.ativa
        ? `${oferta.titulo} saiu das buscas.`
        : `${oferta.titulo} voltou a aparecer nas buscas.`)
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  if (ofertas === null) return <Carregando />

  const ativas = ofertas.filter(o => o.ativa)
  const precos = ativas.map(o => Number(o.preco))

  return (
    <>
      <CabecalhoPagina
        titulo="Minhas ofertas"
        descricao="Mantenha preço e disponibilidade em dia. Só as ofertas ativas aparecem nas buscas e no comparador."
      />

      <Aviso tipo="erro">{erro}</Aviso>
      <Aviso tipo="sucesso">{mensagem}</Aviso>

      <div className="indicadores">
        <div className="indicador">
          <div className="rotulo-pequeno">Ofertas cadastradas</div>
          <div className="valor-grande">{ofertas.length}</div>
        </div>
        <div className="indicador">
          <div className="rotulo-pequeno">Ativas nas buscas</div>
          <div className="valor-grande">{ativas.length}</div>
        </div>
        <div className="indicador">
          <div className="rotulo-pequeno">Menor preço ativo</div>
          <div className="valor-grande">{precos.length ? moeda(Math.min(...precos)) : '-'}</div>
        </div>
      </div>

      <div className="cartao">
        {ofertas.length === 0 ? <Vazio>Você ainda não tem ofertas cadastradas.</Vazio> : (
          <div className="tabela-rolagem">
            <table>
              <thead>
                <tr>
                  <th>Oferta</th>
                  <th>Destino</th>
                  <th>Preço</th>
                  <th>Disponível</th>
                  <th>Situação</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {ofertas.map(o => (
                  <tr key={o.id}>
                    <td>
                      <div style={{ fontWeight: 500 }}>{o.titulo}</div>
                      <div className="rotulo-pequeno">{rotulo('oferta', o.tipo)}</div>
                    </td>
                    <td>{o.destino}</td>
                    <td>
                      <input type="number" min="0" step="10" aria-label={`Preço de ${o.titulo}`}
                             style={{ width: 120, height: 36 }}
                             value={edicao[o.id]?.preco ?? ''}
                             onChange={e => alterar(o.id, 'preco', e.target.value)} />
                    </td>
                    <td>
                      <input type="number" min="0" aria-label={`Disponibilidade de ${o.titulo}`}
                             style={{ width: 90, height: 36 }}
                             value={edicao[o.id]?.disponibilidade ?? ''}
                             onChange={e => alterar(o.id, 'disponibilidade', e.target.value)} />
                    </td>
                    <td>
                      <span className={`selo ${o.ativa ? 'verde' : 'cinza'}`}>{o.ativa ? 'Ativa' : 'Pausada'}</span>
                    </td>
                    <td>
                      <div className="linha-botoes" style={{ justifyContent: 'flex-end', flexWrap: 'nowrap' }}>
                        <button className="botao pequeno" onClick={() => salvar(o)} disabled={!mudou(o)}>Salvar</button>
                        <button className="botao secundario pequeno" onClick={() => alternar(o)}>
                          {o.ativa ? 'Pausar' : 'Reativar'}
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </>
  )
}
