import { useEffect, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { api } from '../api/client.js'
import { useAuth } from '../auth/AuthContext.jsx'
import Aviso from '../components/Aviso.jsx'

const CONTAS = [
  { email: 'yan@viaaja.com', perfil: 'Viajante' },
  { email: 'parceiro.hotel@viaaja.com', perfil: 'Parceiro' },
  { email: 'admin@viaaja.com', perfil: 'Administrador' }
]

export default function Entrar() {
  const [email, setEmail] = useState('yan@viaaja.com')
  const [senha, setSenha] = useState('viaaja2026')
  const [erro, setErro] = useState('')
  const [enviando, setEnviando] = useState(false)
  const [rotas, setRotas] = useState([])
  const { entrar } = useAuth()
  const navegar = useNavigate()
  const local = useLocation()

  useEffect(() => {
    api.get('/destinos').then(lista => setRotas(lista.slice(0, 4))).catch(() => setRotas([]))
  }, [])

  async function enviar(evento) {
    evento.preventDefault()
    setErro('')
    setEnviando(true)
    try {
      await entrar(email, senha)
      navegar(local.state?.de || '/', { replace: true })
    } catch (e) {
      setErro(e.status === 401 ? 'E-mail ou senha incorretos. Confira e tente de novo.' : e.message)
    } finally {
      setEnviando(false)
    }
  }

  return (
    <div className="tela-acesso">
      <section className="acesso-apresentacao">
        <div>
          <div className="marca-grande">vIAaja</div>
          <p className="frase">
            Compare ônibus, avião e carro, monte o roteiro dia a dia e combine a viagem com quem vai
            para o mesmo lugar.
          </p>
        </div>
        {rotas.length > 0 && (
          <ul className="rotas-exemplo" aria-label="Destinos disponíveis a partir de Salvador">
            {rotas.map(r => (
              <li key={r.id}>
                <span>Salvador para {r.cidade}</span>
                {r.distanciaReferenciaKm ? <span>{r.distanciaReferenciaKm} km</span> : <span />}
              </li>
            ))}
          </ul>
        )}
      </section>

      <section className="acesso-formulario">
        <h1>Entrar na sua conta</h1>
        <Aviso tipo="erro">{erro}</Aviso>
        <form onSubmit={enviar}>
          <div className="campo">
            <label htmlFor="email">E-mail</label>
            <input id="email" type="email" autoComplete="username" value={email} required
                   onChange={e => setEmail(e.target.value)} />
          </div>
          <div className="campo">
            <label htmlFor="senha">Senha</label>
            <input id="senha" type="password" autoComplete="current-password" value={senha} required
                   onChange={e => setSenha(e.target.value)} />
          </div>
          <button className="botao" type="submit" disabled={enviando}>
            {enviando ? 'Entrando…' : 'Entrar'}
          </button>
        </form>
        <div className="links">
          <Link to="/recuperar-senha">Esqueci minha senha</Link>
          <Link to="/cadastro">Criar conta</Link>
        </div>

        <div className="contas-demo">
          <div className="rotulo-pequeno">Contas de demonstração, todas com a senha viaaja2026</div>
          <table>
            <tbody>
              {CONTAS.map(c => (
                <tr key={c.email}>
                  <td>
                    <button type="button" onClick={() => { setEmail(c.email); setSenha('viaaja2026') }}>
                      {c.email}
                    </button>
                  </td>
                  <td className="texto-suave">{c.perfil}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  )
}
