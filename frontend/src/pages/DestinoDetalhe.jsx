import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { api } from '../api/client.js'
import { useAuth } from '../auth/AuthContext.jsx'
import Aviso from '../components/Aviso.jsx'
import CabecalhoPagina from '../components/CabecalhoPagina.jsx'
import Carregando from '../components/Carregando.jsx'
import Vazio from '../components/Vazio.jsx'
import { moeda, data, plural } from '../utils/formato.js'

export default function DestinoDetalhe() {
  const { id } = useParams()
  const { autenticado, usuario } = useAuth()
  const [destino, setDestino] = useState(null)
  const [atividades, setAtividades] = useState([])
  const [avaliacoes, setAvaliacoes] = useState([])
  const [media, setMedia] = useState(null)
  const [resumo, setResumo] = useState(null)
  const [resumindo, setResumindo] = useState(false)
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
    setResumindo(true)
    try {
      setResumo(await api.get(`/avaliacoes/destino/${id}/resumo`))
    } catch (e) {
      setErro(e.status === 503
        ? 'O resumo não pôde ser gerado agora. As avaliações completas continuam listadas abaixo.'
        : e.message)
    } finally {
      setResumindo(false)
    }
  }

  async function avaliar(evento) {
    evento.preventDefault()
    setErro('')
    setMensagem('')
    try {
      await api.post('/avaliacoes', { destinoId: Number(id), nota: Number(nota), comentario })
      setComentario('')
      setResumo(null)
      setMensagem('Avaliação publicada. Obrigado por contar como foi.')
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  if (!destino) {
    return erro ? <Aviso tipo="erro">{erro}</Aviso> : <Carregando />
  }

  return (
    <>
      <p className="pequeno-texto" style={{ marginBottom: 12 }}>
        <Link to="/destinos">Destinos</Link>
      </p>
      <CabecalhoPagina titulo={`${destino.cidade}, ${destino.estado}`} descricao={destino.descricao} />

      <Aviso tipo="erro">{erro}</Aviso>
      <Aviso tipo="sucesso">{mensagem}</Aviso>

      <div className="cartao">
        <h2>O que fazer</h2>
        {atividades.length === 0 ? <Vazio>Nenhuma atividade cadastrada para esta cidade.</Vazio> : (
          <div className="tabela-rolagem">
            <table>
              <thead>
                <tr><th>Atividade</th><th>Tipo</th><th className="num">Custo médio</th></tr>
              </thead>
              <tbody>
                {atividades.map(a => (
                  <tr key={a.id}>
                    <td>{a.nome}</td>
                    <td><span className="selo cinza">{a.categoria}</span></td>
                    <td className="num">{Number(a.custoMedio) > 0 ? moeda(a.custoMedio) : 'Grátis'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      <div className="cartao">
        <div className="linha-titulo" style={{ marginBottom: 16 }}>
          <h2 style={{ margin: 0 }}>
            Avaliações
            {media ? <span className="texto-suave"> <span className="nota">{Number(media).toFixed(1)}</span> de 5</span> : null}
          </h2>
          {avaliacoes.length > 0 && (
            <button className="botao secundario pequeno" onClick={gerarResumo} disabled={resumindo}>
              {resumindo ? 'Resumindo…' : 'Resumir avaliações'}
            </button>
          )}
        </div>

        {resumo && (
          <>
            <div className="resumo-ia">
              <div>
                <h3>Pontos positivos</h3>
                <ul>{resumo.pontosPositivos.map((p, i) => <li key={i}>{p}</li>)}</ul>
              </div>
              <div>
                <h3>Pontos negativos</h3>
                {resumo.pontosNegativos.length === 0
                  ? <p className="texto-suave">Nenhum ponto negativo citado.</p>
                  : <ul>{resumo.pontosNegativos.map((p, i) => <li key={i}>{p}</li>)}</ul>}
              </div>
            </div>
            <p className="pequeno-texto texto-suave" style={{ marginTop: -8 }}>
              Resumo gerado automaticamente a partir de {plural(resumo.totalAvaliacoesConsideradas, 'avaliação', 'avaliações')}.
            </p>
          </>
        )}

        {avaliacoes.length === 0 ? (
          <Vazio>Ninguém avaliou esta cidade ainda.</Vazio>
        ) : (
          <ul className="lista-limpa">
            {avaliacoes.map(a => (
              <li key={a.id}>
                <div className="linha-titulo">
                  <strong>{a.autor}</strong>
                  <span className="rotulo-pequeno"><span className="nota">{a.nota}</span> de 5, em {data(a.data)}</span>
                </div>
                <p style={{ margin: '4px 0 0' }}>{a.comentario}</p>
              </li>
            ))}
          </ul>
        )}
      </div>

      {ehViajante && (
        <div className="cartao">
          <h2>Conte como foi</h2>
          <p className="descricao-secao">
            Só quem tem um itinerário para {destino.cidade} pode avaliar, para que as opiniões venham de quem
            planejou ir.
          </p>
          <form onSubmit={avaliar}>
            <div className="campo" style={{ maxWidth: 200 }}>
              <label htmlFor="d-nota">Nota</label>
              <select id="d-nota" value={nota} onChange={e => setNota(e.target.value)}>
                {[5, 4, 3, 2, 1].map(n => <option key={n} value={n}>{n} de 5</option>)}
              </select>
            </div>
            <div className="campo">
              <label htmlFor="d-comentario">Comentário</label>
              <textarea id="d-comentario" value={comentario} required maxLength={1000}
                        onChange={e => setComentario(e.target.value)} />
            </div>
            <button className="botao" type="submit">Publicar avaliação</button>
          </form>
        </div>
      )}
    </>
  )
}
