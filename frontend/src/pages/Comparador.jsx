import { useState } from 'react'
import { api } from '../api/client.js'
import Aviso from '../components/Aviso.jsx'
import { moeda, nomeModal, daquiA } from '../utils/formato.js'

export default function Comparador() {
  const [form, setForm] = useState({
    origem: 'Salvador',
    destino: 'Porto Seguro',
    data: daquiA(35),
    passageiros: 2
  })
  const [resultado, setResultado] = useState(null)
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(false)

  function alterar(campo, valor) {
    setForm(anterior => ({ ...anterior, [campo]: valor }))
  }

  async function comparar(evento) {
    evento.preventDefault()
    setErro('')
    setCarregando(true)
    setResultado(null)
    try {
      const dados = await api.post('/comparacoes', {
        ...form,
        passageiros: Number(form.passageiros)
      })
      setResultado(dados)
    } catch (e) {
      setErro(e.message)
    } finally {
      setCarregando(false)
    }
  }

  return (
    <>
      <h1>Quanto custa chegar la</h1>
      <p style={{ marginTop: -6, color: 'var(--cinza-600)' }}>
        Compare o custo total por aviao, onibus e carro em uma unica tela.
      </p>

      <div className="cartao">
        <form onSubmit={comparar}>
          <div className="grade tres">
            <div className="campo">
              <label htmlFor="origem">Origem</label>
              <input id="origem" value={form.origem} required
                     onChange={e => alterar('origem', e.target.value)} />
            </div>
            <div className="campo">
              <label htmlFor="destino">Destino</label>
              <input id="destino" value={form.destino} required
                     onChange={e => alterar('destino', e.target.value)} />
            </div>
            <div className="campo">
              <label htmlFor="data">Data da ida</label>
              <input id="data" type="date" value={form.data} required
                     onChange={e => alterar('data', e.target.value)} />
            </div>
            <div className="campo">
              <label htmlFor="passageiros">Passageiros</label>
              <input id="passageiros" type="number" min="1" max="9" value={form.passageiros} required
                     onChange={e => alterar('passageiros', e.target.value)} />
            </div>
          </div>
          <button className="botao" type="submit" disabled={carregando}>
            {carregando ? 'Consultando as fontes...' : 'Comparar custos'}
          </button>
        </form>
      </div>

      <Aviso tipo="erro">{erro}</Aviso>

      {carregando && (
        <div className="cartao">
          <p>Consultando passagem aerea, rodoviaria e estimativa de carro...</p>
        </div>
      )}

      {resultado && (
        <>
          <div className="cartao">
            <h2>Resultado para {resultado.origem} ate {resultado.destino}</h2>
            <p className="rotulo-pequeno">
              {resultado.passageiros} passageiro(s) - consultado agora
            </p>
            <div className="grade tres" style={{ marginTop: 14 }}>
              {resultado.opcoes.map(opcao => (
                <div key={opcao.modal}
                     className={`opcao-modal ${opcao.maisBarata ? 'vencedora' : ''} ${!opcao.disponivel ? 'indisponivel' : ''}`}>
                  <div className="rotulo-pequeno">{nomeModal(opcao.modal)}</div>
                  <div className="valor-grande">
                    {opcao.disponivel ? moeda(opcao.custoTotal) : 'Indisponivel'}
                  </div>
                  <div style={{ marginTop: 8, display: 'flex', gap: 6, flexWrap: 'wrap' }}>
                    {opcao.maisBarata && <span className="selo verde">Mais barata</span>}
                    {opcao.estimado && <span className="selo amarelo">Estimativa</span>}
                    {!opcao.disponivel && <span className="selo cinza">Sem resultado</span>}
                  </div>
                  <p style={{ fontSize: 13, color: 'var(--cinza-600)', marginBottom: 0 }}>
                    {opcao.detalhe}
                  </p>
                </div>
              ))}
            </div>
          </div>

          {resultado.opcoes.some(o => o.estimado) && (
            <Aviso tipo="atencao">
              O custo de carro e uma estimativa de combustivel e pedagio, calculada por distancia
              de referencia e valores medios (RN-007).
            </Aviso>
          )}
        </>
      )}
    </>
  )
}
