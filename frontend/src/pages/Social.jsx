import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api, parametros } from '../api/client.js'
import Aviso from '../components/Aviso.jsx'
import Vazio from '../components/Vazio.jsx'
import { dataHora } from '../utils/formato.js'

export default function Social() {
  const [cidade, setCidade] = useState('')
  const [viajantes, setViajantes] = useState([])
  const [recebidos, setRecebidos] = useState([])
  const [enviados, setEnviados] = useState([])
  const [reunioes, setReunioes] = useState([])
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')

  useEffect(() => { carregar() }, [])

  async function carregar() {
    try {
      const [r, e, m] = await Promise.all([
        api.get('/social/convites/recebidos'),
        api.get('/social/convites/enviados'),
        api.get('/social/reunioes')
      ])
      setRecebidos(r)
      setEnviados(e)
      setReunioes(m)
    } catch (err) {
      setErro(err.message)
    }
  }

  async function descobrir(evento) {
    if (evento) evento.preventDefault()
    setErro('')
    try {
      setViajantes(await api.get('/social/viajantes' + parametros({ cidade })))
    } catch (e) {
      setErro(e.message)
    }
  }

  async function convidar(destinatarioId) {
    setErro('')
    setMensagem('')
    try {
      await api.post('/social/convites', {
        destinatarioId,
        mensagem: 'Oi! Quero trocar dicas sobre essa viagem.'
      })
      setMensagem('Convite enviado.')
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  async function aceitar(conviteId) {
    setErro('')
    try {
      await api.post(`/social/convites/${conviteId}/aceitar`, { idiomas: ['pt-BR', 'en-US'] })
      setMensagem('Convite aceito. A sala de reuniao foi criada.')
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  async function recusar(conviteId) {
    setErro('')
    try {
      await api.patch(`/social/convites/${conviteId}/recusar`)
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  return (
    <>
      <h1>Comunidade de viajantes</h1>

      <Aviso tipo="erro">{erro}</Aviso>
      <Aviso tipo="sucesso">{mensagem}</Aviso>

      <div className="cartao">
        <h2>Descobrir viajantes</h2>
        <p className="rotulo-pequeno">
          Aparecem apenas viajantes que ativaram a visibilidade no perfil (RN-010).
        </p>
        <form onSubmit={descobrir} className="linha-botoes" style={{ marginBottom: 14 }}>
          <input placeholder="Cidade do destino" value={cidade}
                 onChange={e => setCidade(e.target.value)} style={{ maxWidth: 300 }} />
          <button className="botao" type="submit">Buscar</button>
        </form>

        {viajantes.length === 0 ? (
          <Vazio>Nenhum viajante encontrado ate agora.</Vazio>
        ) : (
          <table>
            <thead>
              <tr><th>Nome</th><th>Cidade</th><th></th></tr>
            </thead>
            <tbody>
              {viajantes.map(v => (
                <tr key={v.id}>
                  <td>{v.nome}</td>
                  <td>{v.cidadeAproximada || '-'}</td>
                  <td>
                    <button className="botao pequeno" onClick={() => convidar(v.id)}>
                      Enviar cutucada
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      <div className="grade duas">
        <div className="cartao">
          <h2>Convites recebidos</h2>
          {recebidos.length === 0 ? <Vazio>Nenhum convite.</Vazio> : (
            <ul className="lista-limpa">
              {recebidos.map(c => (
                <li key={c.id}>
                  <strong>{c.remetente}</strong> <span className="selo">{c.status.toLowerCase()}</span>
                  <div style={{ fontSize: 14 }}>{c.mensagem}</div>
                  <div className="rotulo-pequeno">{dataHora(c.dataEnvio)}</div>
                  {c.status === 'ENVIADO' && (
                    <div className="linha-botoes" style={{ marginTop: 8 }}>
                      <button className="botao pequeno" onClick={() => aceitar(c.id)}>Aceitar</button>
                      <button className="botao secundario pequeno"
                              onClick={() => recusar(c.id)}>Recusar</button>
                    </div>
                  )}
                </li>
              ))}
            </ul>
          )}
        </div>

        <div className="cartao">
          <h2>Convites enviados</h2>
          {enviados.length === 0 ? <Vazio>Nenhum convite enviado.</Vazio> : (
            <ul className="lista-limpa">
              {enviados.map(c => (
                <li key={c.id}>
                  <strong>{c.destinatario}</strong> <span className="selo">{c.status.toLowerCase()}</span>
                  <div className="rotulo-pequeno">{dataHora(c.dataEnvio)}</div>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>

      <div className="cartao">
        <h2>Minhas reunioes</h2>
        {reunioes.length === 0 ? <Vazio>Nenhuma reuniao agendada.</Vazio> : (
          <table>
            <thead>
              <tr><th>Com quem</th><th>Situacao</th><th>Idiomas</th><th>Quando</th><th></th></tr>
            </thead>
            <tbody>
              {reunioes.map(r => (
                <tr key={r.id}>
                  <td>{r.comQuem}</td>
                  <td><span className="selo">{r.status.toLowerCase()}</span></td>
                  <td>{r.idiomas.join(', ')}</td>
                  <td>{dataHora(r.dataHora)}</td>
                  <td>
                    {r.status !== 'ENCERRADA' && (
                      <Link className="botao pequeno" to={`/reuniao/${r.salaId}`}>Entrar</Link>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </>
  )
}
