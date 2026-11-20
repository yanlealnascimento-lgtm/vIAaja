import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api, parametros } from '../api/client.js'
import Aviso from '../components/Aviso.jsx'
import CabecalhoPagina from '../components/CabecalhoPagina.jsx'
import Vazio from '../components/Vazio.jsx'
import { dataHora, rotulo } from '../utils/formato.js'

function corConvite(status) {
  return { ACEITO: 'verde', RECUSADO: 'vermelho', EXPIRADO: 'cinza' }[status] || 'amarelo'
}

export default function Social() {
  const [cidade, setCidade] = useState('')
  const [viajantes, setViajantes] = useState(null)
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

  async function convidar(viajante) {
    setErro('')
    setMensagem('')
    try {
      await api.post('/social/convites', {
        destinatarioId: viajante.id,
        mensagem: 'Oi! Vi que vamos para o mesmo destino. Quer trocar dicas e combinar a viagem?'
      })
      setMensagem(`Convite enviado para ${viajante.nome}.`)
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  async function aceitar(conviteId) {
    setErro('')
    try {
      await api.post(`/social/convites/${conviteId}/aceitar`, { idiomas: ['pt-BR', 'en-US'] })
      setMensagem('Convite aceito. A sala de conversa já está disponível em Minhas conversas.')
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  async function recusar(conviteId) {
    setErro('')
    try {
      await api.patch(`/social/convites/${conviteId}/recusar`)
      setMensagem('Convite recusado.')
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  return (
    <>
      <CabecalhoPagina
        titulo="Comunidade"
        descricao="Encontre quem vai para o mesmo destino, troque convites e converse por vídeo com tradução simultânea."
      />

      <Aviso tipo="erro">{erro}</Aviso>
      <Aviso tipo="sucesso">{mensagem}</Aviso>

      <div className="cartao">
        <h2>Quem vai para o mesmo lugar</h2>
        <p className="descricao-secao">
          Só aparecem viajantes que escolheram ficar visíveis no perfil. A busca usa a cidade aproximada,
          nunca a localização exata.
        </p>
        <form onSubmit={descobrir} className="linha-busca" style={{ marginBottom: 18 }}>
          <input aria-label="Cidade do destino" placeholder="Cidade do destino, ou vazio para todos" value={cidade}
                 onChange={e => setCidade(e.target.value)} />
          <button className="botao" type="submit">Procurar</button>
        </form>

        {viajantes === null ? null : viajantes.length === 0 ? (
          <Vazio>Ninguém visível para esse destino agora.</Vazio>
        ) : (
          <table>
            <thead>
              <tr><th>Viajante</th><th>Cidade</th><th></th></tr>
            </thead>
            <tbody>
              {viajantes.map(v => (
                <tr key={v.id}>
                  <td>{v.nome}</td>
                  <td className="texto-suave">{v.cidadeAproximada || 'Não informada'}</td>
                  <td style={{ textAlign: 'right' }}>
                    <button className="botao secundario pequeno" onClick={() => convidar(v)}>
                      Enviar convite
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
          {recebidos.length === 0 ? <Vazio>Nenhum convite recebido.</Vazio> : (
            <ul className="lista-limpa">
              {recebidos.map(c => (
                <li key={c.id}>
                  <div className="linha-titulo">
                    <strong>{c.remetente}</strong>
                    <span className={`selo ${corConvite(c.status)}`}>{rotulo('convite', c.status)}</span>
                  </div>
                  {c.mensagem && <p style={{ margin: '4px 0' }}>{c.mensagem}</p>}
                  <div className="rotulo-pequeno">
                    {c.destino ? `Sobre ${c.destino}, ` : ''}{dataHora(c.dataEnvio)}
                  </div>
                  {c.status === 'ENVIADO' && (
                    <div className="linha-botoes" style={{ marginTop: 10 }}>
                      <button className="botao pequeno" onClick={() => aceitar(c.id)}>Aceitar</button>
                      <button className="botao secundario pequeno" onClick={() => recusar(c.id)}>Recusar</button>
                    </div>
                  )}
                </li>
              ))}
            </ul>
          )}
        </div>

        <div className="cartao">
          <h2>Convites enviados</h2>
          {enviados.length === 0 ? <Vazio>Você ainda não convidou ninguém.</Vazio> : (
            <ul className="lista-limpa">
              {enviados.map(c => (
                <li key={c.id}>
                  <div className="linha-titulo">
                    <strong>{c.destinatario}</strong>
                    <span className={`selo ${corConvite(c.status)}`}>{rotulo('convite', c.status)}</span>
                  </div>
                  <div className="rotulo-pequeno">
                    {c.destino ? `Sobre ${c.destino}, ` : ''}{dataHora(c.dataEnvio)}
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>

      <div className="cartao">
        <h2>Minhas conversas</h2>
        {reunioes.length === 0 ? (
          <Vazio>Quando um convite for aceito, a sala de conversa aparece aqui.</Vazio>
        ) : (
          <div className="tabela-rolagem">
            <table>
              <thead>
                <tr><th>Com quem</th><th>Situação</th><th>Idiomas</th><th>Criada em</th><th></th></tr>
              </thead>
              <tbody>
                {reunioes.map(r => (
                  <tr key={r.id}>
                    <td>{r.comQuem}</td>
                    <td><span className={`selo ${r.status === 'ENCERRADA' ? 'cinza' : 'mar'}`}>{rotulo('reuniao', r.status)}</span></td>
                    <td>{r.idiomas.map(i => rotulo('idioma', i)).join(' e ')}</td>
                    <td className="num">{dataHora(r.dataHora)}</td>
                    <td style={{ textAlign: 'right' }}>
                      {r.status !== 'ENCERRADA' && (
                        <Link className="botao pequeno" to={`/reuniao/${r.salaId}`}>Entrar na sala</Link>
                      )}
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
