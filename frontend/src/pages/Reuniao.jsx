import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '../api/client.js'
import Aviso from '../components/Aviso.jsx'
import Carregando from '../components/Carregando.jsx'

const IDIOMAS = [
  { codigo: 'en-US', nome: 'Ingles' },
  { codigo: 'es-ES', nome: 'Espanhol' },
  { codigo: 'fr-FR', nome: 'Frances' },
  { codigo: 'pt-BR', nome: 'Portugues' }
]

export default function Reuniao() {
  const { salaId } = useParams()
  const navegar = useNavigate()
  const [reuniao, setReuniao] = useState(null)
  const [erro, setErro] = useState('')
  const [fala, setFala] = useState('')
  const [idioma, setIdioma] = useState('en-US')
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
    try {
      const resposta = await api.post(`/social/reunioes/${salaId}/traducao`, {
        fala, idiomaDestino: idioma
      })
      setHistorico(anterior => [{ ...resposta, idioma }, ...anterior])
      setFala('')
    } catch (e) {
      setErro(e.status === 503
        ? 'A traducao simultanea esta indisponivel. A chamada continua funcionando.'
        : e.message)
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

  if (!reuniao && !erro) return <Carregando texto="Entrando na sala..." />

  return (
    <>
      <h1>Reuniao por video</h1>
      <Aviso tipo="erro">{erro}</Aviso>

      {reuniao && (
        <>
          <div className="cartao">
            <h2>Conversa com {reuniao.comQuem}</h2>
            <p className="rotulo-pequeno">Sala {reuniao.salaId} - {reuniao.status.toLowerCase()}</p>
            <div style={{
              background: 'var(--cinza-900)', color: '#8da2bf', borderRadius: 'var(--raio)',
              height: 260, display: 'flex', alignItems: 'center', justifyContent: 'center',
              textAlign: 'center', padding: 20, marginBottom: 14
            }}>
              Area de video. A transmissao acontece diretamente entre os navegadores,
              e o servidor cuida apenas da sinalizacao e da permissao de entrada.
            </div>
            <button className="botao perigo" onClick={encerrar}>Encerrar reuniao</button>
          </div>

          <div className="cartao">
            <h2>Traducao simultanea</h2>
            <Aviso tipo="atencao">
              A traducao e um apoio e pode conter imprecisoes (RN-014).
            </Aviso>
            <form onSubmit={traduzir}>
              <div className="grade duas">
                <div className="campo">
                  <label>Fala</label>
                  <input value={fala} required onChange={e => setFala(e.target.value)} />
                </div>
                <div className="campo">
                  <label>Traduzir para</label>
                  <select value={idioma} onChange={e => setIdioma(e.target.value)}>
                    {IDIOMAS.map(i => <option key={i.codigo} value={i.codigo}>{i.nome}</option>)}
                  </select>
                </div>
              </div>
              <button className="botao" type="submit">Traduzir</button>
            </form>

            {historico.length > 0 && (
              <ul className="lista-limpa" style={{ marginTop: 16 }}>
                {historico.map((h, indice) => (
                  <li key={indice}>
                    <div className="rotulo-pequeno">{h.idioma}</div>
                    <div style={{ color: 'var(--cinza-600)', fontSize: 14 }}>{h.original}</div>
                    <strong>{h.traducao}</strong>
                  </li>
                ))}
              </ul>
            )}
          </div>
        </>
      )}
    </>
  )
}
