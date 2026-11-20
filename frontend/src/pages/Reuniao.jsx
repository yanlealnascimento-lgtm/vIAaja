import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { api } from '../api/client.js'
import Aviso from '../components/Aviso.jsx'
import CabecalhoPagina from '../components/CabecalhoPagina.jsx'
import Carregando from '../components/Carregando.jsx'
import { rotulo } from '../utils/formato.js'

const IDIOMAS = ['en-US', 'es-ES', 'fr-FR', 'pt-BR']

export default function Reuniao() {
  const { salaId } = useParams()
  const navegar = useNavigate()
  const [reuniao, setReuniao] = useState(null)
  const [erro, setErro] = useState('')
  const [fala, setFala] = useState('')
  const [idioma, setIdioma] = useState('en-US')
  const [traduzindo, setTraduzindo] = useState(false)
  const [historico, setHistorico] = useState([])

  useEffect(() => { entrar() }, [salaId])

  async function entrar() {
    setErro('')
    try {
      setReuniao(await api.post(`/social/reunioes/${salaId}/entrar`))
    } catch (e) {
      setErro(e.message)
    }
  }

  async function traduzir(evento) {
    evento.preventDefault()
    setErro('')
    setTraduzindo(true)
    try {
      const resposta = await api.post(`/social/reunioes/${salaId}/traducao`, { fala, idiomaDestino: idioma })
      setHistorico(anterior => [{ ...resposta, idioma }, ...anterior])
      setFala('')
    } catch (e) {
      setErro(e.status === 503
        ? 'A tradução não respondeu agora. A conversa por vídeo continua normalmente.'
        : e.message)
    } finally {
      setTraduzindo(false)
    }
  }

  async function encerrar() {
    try {
      await api.post(`/social/reunioes/${salaId}/encerrar`)
      navegar('/social')
    } catch (e) {
      setErro(e.message)
    }
  }

  if (!reuniao && !erro) return <Carregando texto="Entrando na sala…" />

  return (
    <>
      <p className="pequeno-texto" style={{ marginBottom: 12 }}>
        <Link to="/social">Comunidade</Link>
      </p>
      <CabecalhoPagina
        titulo={reuniao ? `Conversa com ${reuniao.comQuem}` : 'Conversa'}
        descricao={reuniao ? `${rotulo('reuniao', reuniao.status)}. Idiomas da sala: ${reuniao.idiomas.map(i => rotulo('idioma', i)).join(' e ')}.` : null}
      />
      <Aviso tipo="erro">{erro}</Aviso>

      {reuniao && (
        <div className="grade duas" style={{ alignItems: 'start' }}>
          <div className="cartao">
            <h2>Vídeo</h2>
            <div className="area-video">
              <p>
                A imagem e o áudio vão direto de um navegador para o outro. O servidor só autoriza a entrada
                na sala e faz a conexão inicial entre os dois.
              </p>
            </div>
            <button className="botao perigo" onClick={encerrar}>Encerrar conversa</button>
          </div>

          <div className="cartao">
            <h2>Tradução simultânea</h2>
            <p className="descricao-secao">
              A tradução ajuda a conversa, mas pode errar nomes, gírias e números. Confirme o que for importante.
            </p>
            <form onSubmit={traduzir}>
              <div className="campo">
                <label htmlFor="r-fala">O que você quer dizer</label>
                <textarea id="r-fala" rows={2} value={fala} required onChange={e => setFala(e.target.value)} />
              </div>
              <div className="linha-botoes">
                <select aria-label="Traduzir para" value={idioma} onChange={e => setIdioma(e.target.value)} style={{ width: 'auto' }}>
                  {IDIOMAS.map(i => <option key={i} value={i}>Para {rotulo('idioma', i).toLowerCase()}</option>)}
                </select>
                <button className="botao" type="submit" disabled={traduzindo}>
                  {traduzindo ? 'Traduzindo…' : 'Traduzir'}
                </button>
              </div>
            </form>

            {historico.length > 0 && (
              <ul className="lista-limpa" style={{ marginTop: 24 }}>
                {historico.map((h, indice) => (
                  <li key={indice} className="traducao-item">
                    <div className="original">{h.original}</div>
                    <div className="traduzido" lang={h.idioma}>{h.traducao}</div>
                    <div className="rotulo-pequeno">{rotulo('idioma', h.idioma)}</div>
                  </li>
                ))}
              </ul>
            )}
          </div>
        </div>
      )}
    </>
  )
}
