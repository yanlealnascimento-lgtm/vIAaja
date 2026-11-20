import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { api } from '../api/client.js'
import { useAuth } from '../auth/AuthContext.jsx'
import Aviso from '../components/Aviso.jsx'
import Carregando from '../components/Carregando.jsx'
import Vazio from '../components/Vazio.jsx'
import { moeda, dataHora } from '../utils/formato.js'

export default function DestinoDetalhe() {
  const { id } = useParams()
  const { autenticado, usuario } = useAuth()
  const [destino, setDestino] = useState(null)
  const [atividades, setAtividades] = useState([])
  const [avaliacoes, setAvaliacoes] = useState([])
  const [media, setMedia] = useState(null)
  const [resumo, setResumo] = useState(null)
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')
  const [nota, setNota] = useState(5)
  const [comentario, setComentario] = useState('')

  const ehViajante = autenticado && usuario.perfil === 'VIAJANTE'

  useEffect(() => { carregar() }, [id])

  async function carregar() {
    try {
      const [d, a, av, m] = await Promise.all([
        api.get(`/destinos/${id}`),
        api.get(`/destinos/${id}/atividades`),
        api.get(`/avaliacoes/destino/${id}`),
        api.get(`/avaliacoes/destino/${id}/media`)
      ])
      setDestino(d)
      setAtividades(a)
      setAvaliacoes(av)
      setMedia(m.media)
    } catch (e) {
      setErro(e.message)
    }
  }

  async function gerarResumo() {
    setErro('')
    setResumo(null)
    try {
      setResumo(await api.get(`/avaliacoes/destino/${id}/resumo`))
    } catch (e) {
      setErro(e.status === 503
        ? 'O resumo inteligente esta indisponivel. As avaliacoes continuam listadas abaixo.'
        : e.message)
    }
  }

  async function avaliar(evento) {
    evento.preventDefault()
    setErro('')
    setMensagem('')
    try {
      await api.post('/avaliacoes', {
        destinoId: Number(id),
        nota: Number(nota),
        comentario
      })
      setComentario('')
      setMensagem('Avaliacao registrada.')
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  if (!destino) return <Carregando />

  return (
    <>
      <h1>{destino.cidade}/{destino.estado}</h1>
      <p style={{ marginTop: -6, color: 'var(--cinza-600)' }}>{destino.descricao}</p>

      <Aviso tipo="erro">{erro}</Aviso>
      <Aviso tipo="sucesso">{mensagem}</Aviso>

      <div className="cartao">
        <h2>Atividades cadastradas</h2>
        {atividades.length === 0 ? <Vazio>Nenhuma atividade cadastrada.</Vazio> : (
          <table>
            <thead>
              <tr><th>Atividade</th><th>Categoria</th><th>Custo medio</th></tr>
            </thead>
            <tbody>
              {atividades.map(a => (
                <tr key={a.id}>
                  <td>{a.nome}</td>
                  <td><span className="selo cinza">{a.categoria}</span></td>
                  <td>{moeda(a.custoMedio)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      <div className="cartao">
        <h2>Avaliacoes {media ? `- media ${Number(media).toFixed(1)}` : ''}</h2>
        <div className="linha-botoes" style={{ marginBottom: 12 }}>
          <button className="botao secundario" onClick={gerarResumo}
                  disabled={avaliacoes.length === 0}>
            Resumir com IA
          </button>
        </div>

        {resumo && (
          <div className="grade duas" style={{ marginBottom: 16 }}>
            <div className="opcao-modal">
              <div className="rotulo-pequeno">Pontos positivos</div>
              <ul className="lista-limpa">
                {resumo.pontosPositivos.map((p, i) => <li key={i}>{p}</li>)}
              </ul>
            </div>
            <div className="opcao-modal">
              <div className="rotulo-pequeno">Pontos negativos</div>
              <ul className="lista-limpa">
                {resumo.pontosNegativos.map((p, i) => <li key={i}>{p}</li>)}
              </ul>
            </div>
          </div>
        )}

        {avaliacoes.length === 0 ? <Vazio>Ainda sem avaliacoes.</Vazio> : (
          <ul className="lista-limpa">
            {avaliacoes.map(a => (
              <li key={a.id}>
                <strong>{a.autor}</strong> <span className="selo">{a.nota}/5</span>
                <div style={{ fontSize: 14 }}>{a.comentario}</div>
                <div className="rotulo-pequeno">{dataHora(a.data)}</div>
              </li>
            ))}
          </ul>
        )}
      </div>

      {ehViajante && (
        <div className="cartao">
          <h2>Deixar minha opiniao</h2>
          <p className="rotulo-pequeno">
            So e possivel avaliar um destino que esteja em um itinerario seu (RN-013).
          </p>
          <form onSubmit={avaliar}>
            <div className="campo">
              <label>Nota</label>
              <select value={nota} onChange={e => setNota(e.target.value)}>
                {[5, 4, 3, 2, 1].map(n => <option key={n} value={n}>{n}</option>)}
              </select>
            </div>
            <div className="campo">
              <label>Comentario</label>
              <textarea value={comentario} required maxLength={1000}
                        onChange={e => setComentario(e.target.value)} />
            </div>
            <button className="botao" type="submit">Publicar avaliacao</button>
          </form>
        </div>
      )}
    </>
  )
}
