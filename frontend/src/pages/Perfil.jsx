import { useEffect, useState } from 'react'
import { api } from '../api/client.js'
import Aviso from '../components/Aviso.jsx'
import CabecalhoPagina from '../components/CabecalhoPagina.jsx'
import Carregando from '../components/Carregando.jsx'
import Vazio from '../components/Vazio.jsx'
import { dataHora, rotulo } from '../utils/formato.js'

export default function Perfil() {
  const [eu, setEu] = useState(null)
  const [notificacoes, setNotificacoes] = useState([])
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')
  const [form, setForm] = useState({ nome: '', telefone: '', cidadeAproximada: '' })
  const [privacidade, setPrivacidade] = useState({ visivelComoProximo: false, quemPodeConvidar: 'NINGUEM' })

  useEffect(() => { carregar() }, [])

  async function carregar() {
    try {
      const [dados, listaNotificacoes] = await Promise.all([api.get('/usuarios/eu'), api.get('/notificacoes')])
      setEu(dados)
      setNotificacoes(listaNotificacoes)
      setForm({
        nome: dados.nome || '',
        telefone: dados.telefone || '',
        cidadeAproximada: dados.cidadeAproximada || ''
      })
      setPrivacidade({
        visivelComoProximo: Boolean(dados.visivelComoProximo),
        quemPodeConvidar: dados.quemPodeConvidar || 'NINGUEM'
      })
    } catch (e) {
      setErro(e.message)
    }
  }

  async function salvarPerfil(evento) {
    evento.preventDefault()
    setErro('')
    setMensagem('')
    try {
      await api.put('/usuarios/eu', form)
      setMensagem('Dados pessoais salvos.')
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  async function salvarPrivacidade(evento) {
    evento.preventDefault()
    setErro('')
    setMensagem('')
    try {
      await api.put('/usuarios/eu/privacidade', privacidade)
      setMensagem('Preferências de privacidade salvas.')
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  async function marcarLida(id) {
    await api.patch(`/notificacoes/${id}/lida`)
    carregar()
  }

  if (!eu) return erro ? <Aviso tipo="erro">{erro}</Aviso> : <Carregando />

  const naoLidas = notificacoes.filter(n => !n.lida).length

  return (
    <>
      <CabecalhoPagina titulo="Meu perfil" descricao={eu.email} />

      <Aviso tipo="erro">{erro}</Aviso>
      <Aviso tipo="sucesso">{mensagem}</Aviso>

      <div className="grade duas" style={{ alignItems: 'start' }}>
        <div className="cartao">
          <h2>Dados pessoais</h2>
          <form onSubmit={salvarPerfil}>
            <div className="campo">
              <label htmlFor="pf-nome">Nome</label>
              <input id="pf-nome" value={form.nome} required minLength={3}
                     onChange={e => setForm(a => ({ ...a, nome: e.target.value }))} />
            </div>
            <div className="campo">
              <label htmlFor="pf-telefone">Telefone</label>
              <input id="pf-telefone" value={form.telefone}
                     onChange={e => setForm(a => ({ ...a, telefone: e.target.value }))} />
            </div>
            <div className="campo">
              <label htmlFor="pf-cidade">Cidade onde você mora</label>
              <input id="pf-cidade" value={form.cidadeAproximada}
                     onChange={e => setForm(a => ({ ...a, cidadeAproximada: e.target.value }))} />
              <span className="ajuda">Usada só para mostrar viajantes da mesma região.</span>
            </div>
            <button className="botao" type="submit">Salvar dados</button>
          </form>
        </div>

        <div className="cartao">
          <h2>Privacidade</h2>
          <form onSubmit={salvarPrivacidade}>
            <div className="campo">
              <label className="caixa-marcacao">
                <input type="checkbox" checked={privacidade.visivelComoProximo}
                       onChange={e => setPrivacidade(a => ({ ...a, visivelComoProximo: e.target.checked }))} />
                Aparecer para quem vai ao mesmo destino
              </label>
              <span className="ajuda">Os outros veem só seu nome e sua cidade, nunca a localização exata.</span>
            </div>
            <div className="campo">
              <label htmlFor="pf-convites">Quem pode me enviar convites</label>
              <select id="pf-convites" value={privacidade.quemPodeConvidar}
                      onChange={e => setPrivacidade(a => ({ ...a, quemPodeConvidar: e.target.value }))}>
                <option value="TODOS">Qualquer viajante</option>
                <option value="MESMO_DESTINO">Só quem vai ao mesmo destino</option>
                <option value="NINGUEM">Ninguém</option>
              </select>
            </div>
            <button className="botao" type="submit">Salvar privacidade</button>
          </form>
        </div>
      </div>

      <div className="cartao">
        <h2>Notificações{naoLidas > 0 && <span className="texto-suave"> ({naoLidas} não lidas)</span>}</h2>
        {notificacoes.length === 0 ? (
          <Vazio>Nenhuma notificação por enquanto.</Vazio>
        ) : (
          <ul className="lista-limpa">
            {notificacoes.map(n => (
              <li key={n.id} className={`notificacao${n.lida ? ' lida' : ''}`}>
                <div className="linha-titulo">
                  <div>
                    <span className="selo cinza">{rotulo('notificacao', n.tipo)}</span>{' '}
                    {n.mensagem}
                    <div className="rotulo-pequeno">{dataHora(n.data)}</div>
                  </div>
                  {!n.lida && (
                    <button className="botao-texto pequeno-texto" onClick={() => marcarLida(n.id)}>
                      Marcar como lida
                    </button>
                  )}
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </>
  )
}
