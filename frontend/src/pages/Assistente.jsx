import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/client.js'
import Aviso from '../components/Aviso.jsx'
import CabecalhoPagina from '../components/CabecalhoPagina.jsx'
import { data, moeda, plural } from '../utils/formato.js'

const EXEMPLOS = [
  'Quero ir de Salvador para Porto Seguro em dezembro, 5 dias, duas pessoas, até 2.500 reais. Gosto de praia e comida.',
  'Natureza e trilha na Chapada Diamantina com 1.500 de orçamento, saindo no próximo feriado.',
  'Cultura e gastronomia em Recife com a família, quatro pessoas, uma semana em janeiro.'
]

export default function Assistente() {
  const [texto, setTexto] = useState(EXEMPLOS[0])
  const [interpretacao, setInterpretacao] = useState(null)
  const [recomendacoes, setRecomendacoes] = useState(null)
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(false)

  useEffect(() => {
    api.get('/ia/recomendacoes').then(setRecomendacoes).catch(() => setRecomendacoes(null))
  }, [])

  async function interpretar(evento) {
    evento.preventDefault()
    setErro('')
    setCarregando(true)
    setInterpretacao(null)
    try {
      setInterpretacao(await api.post('/ia/interpretar', { texto }))
    } catch (e) {
      setErro(e.status === 503
        ? 'O assistente não respondeu agora. Tente de novo em instantes; o comparador e as buscas continuam funcionando.'
        : e.message)
    } finally {
      setCarregando(false)
    }
  }

  return (
    <>
      <CabecalhoPagina
        titulo="Assistente de viagem"
        descricao="Descreva a viagem do jeito que você falaria com um amigo. O assistente separa destino, datas, orçamento e interesses para você revisar."
      />

      <div className="cartao">
        <form onSubmit={interpretar}>
          <div className="campo">
            <label htmlFor="texto">Sua viagem</label>
            <textarea id="texto" value={texto} maxLength={500} required rows={3}
                      onChange={e => setTexto(e.target.value)} />
            <span className="ajuda">{texto.length} de 500 caracteres</span>
          </div>
          <div className="linha-botoes">
            <button className="botao" type="submit" disabled={carregando}>
              {carregando ? 'Lendo o pedido…' : 'Entender pedido'}
            </button>
            <span className="rotulo-pequeno">Ou use um exemplo:</span>
            {EXEMPLOS.map((exemplo, indice) => (
              <button key={indice} type="button" className="botao-texto"
                      onClick={() => setTexto(exemplo)}>
                {['Praia', 'Chapada', 'Recife'][indice]}
              </button>
            ))}
          </div>
        </form>
      </div>

      <Aviso tipo="erro">{erro}</Aviso>

      {interpretacao && (
        <div className="cartao">
          <h2>O que o assistente entendeu</h2>
          {interpretacao.interpretacao && (
            <p className="descricao-secao">{interpretacao.interpretacao}</p>
          )}
          <div className="grade quatro">
            <Dado rotulo="Saindo de" valor={interpretacao.origem} />
            <Dado rotulo="Destino" valor={interpretacao.destino} />
            <Dado rotulo="Período"
                  valor={interpretacao.dataInicio
                    ? `${data(interpretacao.dataInicio)} a ${data(interpretacao.dataFim)}` : null} />
            <Dado rotulo="Orçamento"
                  valor={interpretacao.orcamento ? moeda(interpretacao.orcamento) : 'Não informado'} />
            <Dado rotulo="Pessoas" valor={plural(interpretacao.passageiros, 'pessoa', 'pessoas')} />
            <div className="dado-rotulado">
              <div className="rotulo-pequeno">Interesses</div>
              <div className="alternaveis">
                {(interpretacao.interesses || []).length === 0
                  ? <span className="valor">Nenhum citado</span>
                  : interpretacao.interesses.map(i => <span key={i} className="selo mar">{i}</span>)}
              </div>
            </div>
          </div>
          <div className="linha-botoes" style={{ marginTop: 24 }}>
            <Link className="botao secundario" to="/itinerarios">Montar roteiro com esses dados</Link>
            <Link className="botao secundario" to="/">Comparar o custo da ida</Link>
          </div>
        </div>
      )}

      {recomendacoes && (recomendacoes.recomendacoes || []).length > 0 && (
        <div className="cartao">
          <h2>Destinos que combinam com você</h2>
          <p className="descricao-secao">
            {recomendacoes.historico?.length
              ? `Com base nas viagens que você já planejou: ${recomendacoes.historico.join(', ')}.`
              : 'Você ainda não planejou nenhuma viagem, então estas são sugestões gerais.'}
          </p>
          <div className="alternaveis">
            {recomendacoes.recomendacoes.map(d => <span key={d} className="selo mar">{d}</span>)}
          </div>
        </div>
      )}
    </>
  )
}

function Dado({ rotulo, valor }) {
  return (
    <div className="dado-rotulado">
      <div className="rotulo-pequeno">{rotulo}</div>
      <div className="valor">{valor ?? 'Não informado'}</div>
    </div>
  )
}
