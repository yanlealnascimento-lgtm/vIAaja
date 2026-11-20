import { useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/client.js'
import Aviso from '../components/Aviso.jsx'

export default function RecuperarSenha() {
  const [email, setEmail] = useState('')
  const [token, setToken] = useState('')
  const [novaSenha, setNovaSenha] = useState('')
  const [mensagem, setMensagem] = useState('')
  const [erro, setErro] = useState('')

  async function solicitar(evento) {
    evento.preventDefault()
    setErro('')
    try {
      const resposta = await api.post('/auth/recuperar-senha', { email })
      setMensagem(resposta.mensagem)
    } catch (e) {
      setErro(e.message)
    }
  }

  async function redefinir(evento) {
    evento.preventDefault()
    setErro('')
    try {
      const resposta = await api.post('/auth/redefinir-senha', { token, novaSenha })
      setMensagem(resposta.mensagem)
    } catch (e) {
      setErro(e.message)
    }
  }

  return (
    <div className="centro-tela">
      <div className="cartao">
        <h1>Recuperar acesso</h1>
        <Aviso tipo="erro">{erro}</Aviso>
        <Aviso tipo="sucesso">{mensagem}</Aviso>
        <form onSubmit={solicitar}>
          <div className="campo">
            <label htmlFor="email">E-mail cadastrado</label>
            <input id="email" type="email" value={email} required
                   onChange={e => setEmail(e.target.value)} />
          </div>
          <button className="botao" type="submit">Enviar link</button>
        </form>
      </div>

      <div className="cartao">
        <h2>Redefinir com o token</h2>
        <p className="rotulo-pequeno">O token aparece no log do back-end durante o desenvolvimento.</p>
        <form onSubmit={redefinir}>
          <div className="campo">
            <label htmlFor="token">Token</label>
            <input id="token" value={token} required onChange={e => setToken(e.target.value)} />
          </div>
          <div className="campo">
            <label htmlFor="nova">Nova senha</label>
            <input id="nova" type="password" value={novaSenha} required minLength={8}
                   onChange={e => setNovaSenha(e.target.value)} />
          </div>
          <button className="botao secundario" type="submit">Redefinir senha</button>
        </form>
        <p style={{ marginTop: 14, fontSize: 14 }}><Link to="/entrar">Voltar para o login</Link></p>
      </div>
    </div>
  )
}
