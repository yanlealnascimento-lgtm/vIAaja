import { useEffect, useState } from 'react'
import { api } from '../api/client.js'
import Aviso from '../components/Aviso.jsx'
import { data, moeda } from '../utils/formato.js'

const EXEMPLOS = [
  'uma viagem economica para o litoral em julho, para duas pessoas',
  'quero natureza e trilha na chapada com 1500 de orcamento',
  'cultura e gastronomia em Recife para a familia'
]

export default function Assistente() {
  const [texto, setTexto] = useState(EXEMPLOS[0])
  const [interpretacao, setInterpretacao] = useState(null)
  const [recomendacoes, setRecomendacoes] = useState(null)
  const [provedor, setProvedor] = useState('')
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(false)

  useEffect(() => {
    api.get('/ia/status').then(r => setProvedor(r.provedor)).catch(() => setProvedor('indisponivel'))
    api.get('/ia/recomendacoes').then(setRecomendacoes).catch(() => setRecomendacoes(null))
  }, [])

  async function interpretar(evento) {
    evento.preventDefault()
    setErro('')
    setCarregando(true)
    setInterpretacao(null)
    try {
      const dados = await api.post('/ia/interpretar', { texto })
      setInterpretacao(dados)
    } catch (e) {
      setErro(e.status === 503
        ? 'Os recursos inteligentes estao temporariamente fora do ar. As demais funcoes seguem disponiveis.'
        : e.message)
    } finally {
      setCarregando(false)
    }
  }

  return (
    <>
      <h1>Assistente de viagem</h1>
      <p style={{ marginTop: -6, color: 'var(--cinza-600)' }}>
        Escreva o que voce procura em texto livre. Provedor de IA ativo: <strong>{provedor}</strong>
      </p>

      <div className="cartao">
        <form onSubmit={interpretar}>
          <div className="campo">
            <label htmlFor="texto">O que voce procura</label>
            <textarea id="texto" value={texto} maxLength={500} required
                      onChange={e => setTexto(e.target.value)} />
          </div>
          <div className="linha-botoes">
            <button className="botao" type="submit" disabled={carregando}>
              {carregando ? 'Interpretando...' : 'Interpretar pedido'}
            </button>
            {EXEMPLOS.map((exemplo, indice) => (
              <button key={indice} type="button" className="botao secundario pequeno"
                      onClick={() => setTexto(exemplo)}>
                Exemplo {indice + 1}
              </button>
            ))}
          </div>
        </form>
      </div>

      <Aviso tipo="erro">{erro}</Aviso>

      {interpretacao && (
        <div className="cartao">
          <h2>Parametros identificados</h2>
          <div className="grade tres">
            <Campo rotulo="Destino" valor={interpretacao.destino} />
            <Campo rotulo="Inicio" valor={data(interpretacao.dataInicio)} />
            <Campo rotulo="Fim" valor={data(interpretacao.dataFim)} />
            <Campo rotulo="Orcamento" valor={interpretacao.orcamento ? moeda(interpretacao.orcamento) : 'nao informado'} />
            <Campo rotulo="Pessoas" valor={interpretacao.passageiros} />
          </div>
          <div style={{ marginTop: 12 }}>
            <div className="rotulo-pequeno">Interesses</div>
            <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap', marginTop: 6 }}>
              {(interpretacao.interesses || []).map(i => <span key={i} className="selo">{i}</span>)}
            </div>
          </div>
          {interpretacao.interpretacao && (
            <Aviso tipo="info">{interpretacao.interpretacao}</Aviso>
          )}
        </div>
      )}

      {recomendacoes && (
        <div className="cartao">
          <h2>Recomendacoes para voce</h2>
          <p className="rotulo-pequeno">
            Baseadas no seu historico de itinerarios: {recomendacoes.historico?.length
              ? recomendacoes.historico.join(', ') : 'ainda sem historico'}
          </p>
          <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap', marginTop: 10 }}>
            {(recomendacoes.recomendacoes || []).map(d => <span key={d} className="selo">{d}</span>)}
          </div>
        </div>
      )}
    </>
  )
}

function Campo({ rotulo, valor }) {
  return (
    <div>
      <div className="rotulo-pequeno">{rotulo}</div>
      <div style={{ fontWeight: 600 }}>{valor ?? '-'}</div>
    </div>
  )
}
