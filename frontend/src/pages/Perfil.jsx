import { useEffect, useState } from 'react'
import { api } from '../api/client.js'
import Aviso from '../components/Aviso.jsx'
import Carregando from '../components/Carregando.jsx'
import { dataHora } from '../utils/formato.js'

export default function Perfil() {
  const [eu, setEu] = useState(null)
  const [notificacoes, setNotificacoes] = useState([])
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')
  const [form, setForm] = useState({ nome: '', telefone: '', cidadeAproximada: '' })
  const [privacidade, setPrivacidade] = useState({
    visivelComoProximo: false, quemPodeConvidar: 'NINGUEM'
  })

  useEffect(() => { carregar() }, [])

  async function carregar() {
    try {
      const [dados, listaNotificacoes] = await Promise.all([
        api.get('/usuarios/eu'),
        api.get('/notificacoes')
      ])
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
      setMensagem('Perfil atualizado.')
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
      setMensagem('Preferencias de privacidade atualizadas.')
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  async function marcarLida(id) {
    await api.patch(`/notificacoes/${id}/lida`)
    carregar()
  }

  if (!eu) return <Carregando />

  return (
    <>
      <h1>Meu perfil</h1>

      <Aviso tipo="erro">{erro}</Aviso>
      <Aviso tipo="sucesso">{mensagem}</Aviso>

      <div className="grade duas">
        <div className="cartao">
          <h2>Dados pessoais</h2>
          <form onSubmit={salvarPerfil}>
            <div className="campo">
              <label>Nome</label>
              <input value={form.nome} required minLength={3}
                     onChange={e => setForm(a => ({ ...a, nome: e.target.value }))} />
            </div>
            <div className="campo">
              <label>E-mail</label>
              <input value={eu.email} disabled />
            </div>
            <div className="campo">
              <label>Telefone</label>
              <input value={form.telefone}
                     onChange={e => setForm(a => ({ ...a, telefone: e.target.value }))} />
            </div>
            <div className="campo">
              <label>Cidade aproximada</label>
              <input value={form.cidadeAproximada}
                     onChange={e => setForm(a => ({ ...a, cidadeAproximada: e.target.value }))} />
            </div>
            <button className="botao" type="submit">Salvar</button>
          </form>
        </div>

        <div className="cartao">
          <h2>Privacidade</h2>
          <p className="rotulo-pequeno">
            A proximidade usa apenas a cidade aproximada, nunca a localizacao exata (RN-011).
          </p>
          <form onSubmit={salvarPrivacidade}>
            <div className="campo">
              <label>
                <input type="checkbox" style={{ width: 'auto', marginRight: 8 }}
                       checked={privacidade.visivelComoProximo}
                       onChange={e => setPrivacidade(a => ({
                         ...a, visivelComoProximo: e.target.checked
                       }))} />
                Quero aparecer como viajante proximo
              </label>
            </div>
            <div className="campo">
              <label>Quem pode me enviar convites</label>
              <select value={privacidade.quemPodeConvidar}
                      onChange={e => setPrivacidade(a => ({
                        ...a, quemPodeConvidar: e.target.value
                      }))}>
                <option value="TODOS">Qualquer viajante</option>
                <option value="MESMO_DESTINO">Somente quem vai ao mesmo destino</option>
                <option value="NINGUEM">Ninguem</option>
              </select>
            </div>
            <button className="botao" type="submit">Salvar preferencias</button>
          </form>
        </div>
      </div>

      <div className="cartao">
        <h2>Notificacoes</h2>
        {notificacoes.length === 0 ? (
          <p style={{ color: 'var(--cinza-600)' }}>Nenhuma notificacao.</p>
        ) : (
          <ul className="lista-limpa">
            {notificacoes.map(n => (
              <li key={n.id} style={{ opacity: n.lida ? 0.55 : 1 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', gap: 12 }}>
                  <div>
                    <span className="selo cinza">{n.tipo.toLowerCase()}</span>{' '}
                    {n.mensagem}
                    <div className="rotulo-pequeno">{dataHora(n.data)}</div>
                  </div>
                  {!n.lida && (
                    <button className="botao secundario pequeno" onClick={() => marcarLida(n.id)}>
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
