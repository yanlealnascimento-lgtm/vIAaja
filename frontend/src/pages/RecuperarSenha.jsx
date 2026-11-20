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
    setMensagem('')
    try {
      await api.post('/auth/recuperar-senha', { email })
      setMensagem('Se esse e-mail estiver cadastrado, enviamos um código para redefinir a senha.')
    } catch (e) {
      setErro(e.message)
    }
  }

  async function redefinir(evento) {
    evento.preventDefault()
    setErro('')
    setMensagem('')
    try {
      await api.post('/auth/redefinir-senha', { token, novaSenha })
      setMensagem('Senha alterada. Você já pode entrar com a nova senha.')
    } catch (e) {
      setErro(e.message)
    }
  }

  return (
    <div className="tela-estreita">
      <div className="cartao" style={{ padding: 32 }}>
        <h1 style={{ marginBottom: 6 }}>Recuperar acesso</h1>
        <p className="texto-suave" style={{ marginBottom: 24 }}>
          Informe o e-mail da conta. Você vai receber um código para criar uma senha nova.
        </p>
        <Aviso tipo="erro">{erro}</Aviso>
        <Aviso tipo="sucesso">{mensagem}</Aviso>
        <form onSubmit={solicitar}>
          <div className="campo">
            <label htmlFor="email">E-mail</label>
            <input id="email" type="email" autoComplete="email" value={email} required
                   onChange={e => setEmail(e.target.value)} />
          </div>
          <button className="botao" type="submit">Enviar código</button>
        </form>

        <h2 style={{ marginTop: 36 }}>Já tenho o código</h2>
        <form onSubmit={redefinir}>
          <div className="campo">
            <label htmlFor="token">Código recebido</label>
            <input id="token" value={token} required onChange={e => setToken(e.target.value)} />
          </div>
          <div className="campo">
            <label htmlFor="nova">Nova senha</label>
            <input id="nova" type="password" autoComplete="new-password" value={novaSenha} required minLength={8}
                   onChange={e => setNovaSenha(e.target.value)} />
            <span className="ajuda">Mínimo de 8 caracteres.</span>
          </div>
          <button className="botao secundario" type="submit">Salvar nova senha</button>
        </form>
        <p className="pequeno-texto" style={{ marginTop: 24, marginBottom: 0 }}>
          <Link to="/entrar">Voltar para a entrada</Link>
        </p>
      </div>
    </div>
  )
}
