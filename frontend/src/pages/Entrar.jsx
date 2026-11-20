import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext.jsx'
import Aviso from '../components/Aviso.jsx'

export default function Entrar() {
  const [email, setEmail] = useState('yan@viaaja.com')
  const [senha, setSenha] = useState('viaaja2026')
  const [erro, setErro] = useState('')
  const [enviando, setEnviando] = useState(false)
  const { entrar } = useAuth()
  const navegar = useNavigate()
  const local = useLocation()

  async function enviar(evento) {
    evento.preventDefault()
    setErro('')
    setEnviando(true)
    try {
      await entrar(email, senha)
      navegar(local.state?.de || '/', { replace: true })
    } catch (e) {
      setErro(e.status === 401 ? 'E-mail ou senha invalidos' : e.message)
    } finally {
      setEnviando(false)
    }
  }

  return (
    <div className="centro-tela">
      <div className="cartao">
        <h1>Entrar</h1>
        <Aviso tipo="erro">{erro}</Aviso>
        <form onSubmit={enviar}>
          <div className="campo">
            <label htmlFor="email">E-mail</label>
            <input id="email" type="email" value={email} required
                   onChange={e => setEmail(e.target.value)} />
          </div>
          <div className="campo">
            <label htmlFor="senha">Senha</label>
            <input id="senha" type="password" value={senha} required
                   onChange={e => setSenha(e.target.value)} />
          </div>
          <button className="botao" type="submit" disabled={enviando}>
            {enviando ? 'Entrando...' : 'Entrar'}
          </button>
        </form>
        <p style={{ marginTop: 16, fontSize: 14 }}>
          <Link to="/recuperar-senha">Esqueci minha senha</Link>
          {' | '}
          <Link to="/cadastro">Criar conta</Link>
        </p>
      </div>
      <div className="cartao">
        <h3>Contas de demonstracao</h3>
        <ul className="lista-limpa" style={{ fontSize: 14 }}>
          <li>yan@viaaja.com - viajante</li>
          <li>parceiro.hotel@viaaja.com - parceiro fornecedor</li>
          <li>admin@viaaja.com - administrador</li>
        </ul>
        <p className="rotulo-pequeno" style={{ marginTop: 10 }}>Senha de todas: viaaja2026</p>
      </div>
    </div>
  )
}
