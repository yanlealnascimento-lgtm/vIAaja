import { Link, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext.jsx'

const LINKS = [
  { para: '/', texto: 'Comparador', perfis: null },
  { para: '/busca', texto: 'Passagens e hotéis', perfis: null },
  { para: '/assistente', texto: 'Assistente', perfis: ['VIAJANTE'] },
  { para: '/itinerarios', texto: 'Itinerários', perfis: ['VIAJANTE'] },
  { para: '/destinos', texto: 'Destinos', perfis: null },
  { para: '/social', texto: 'Comunidade', perfis: ['VIAJANTE'] },
  { para: '/perfil', texto: 'Perfil', perfis: ['VIAJANTE'] },
  { para: '/parceiro', texto: 'Minhas ofertas', perfis: ['PARCEIRO'] },
  { para: '/admin', texto: 'Administração', perfis: ['ADMINISTRADOR'] }
]

export default function Layout({ children }) {
  const { usuario, autenticado, sair } = useAuth()
  const navegar = useNavigate()

  const visiveis = LINKS.filter(link => {
    if (!link.perfis) return true
    if (!autenticado) return false
    return link.perfis.includes(usuario.perfil)
  })

  function encerrar() {
    sair()
    navegar('/entrar')
  }

  return (
    <div className="layout">
      <header className="cabecalho">
        <div className="cabecalho-interno">
          <Link to="/" className="marca" aria-label="vIAaja, página inicial">vIAaja</Link>
          <nav className="navegacao" aria-label="Principal">
            {visiveis.map(link => (
              <NavLink
                key={link.para}
                to={link.para}
                end={link.para === '/'}
                className={({ isActive }) => (isActive ? 'ativo' : undefined)}
              >
                {link.texto}
              </NavLink>
            ))}
          </nav>
          <div className="area-usuario">
            {autenticado ? (
              <>
                <span className="nome-usuario">{usuario.nome}</span>
                <button type="button" className="botao secundario pequeno" onClick={encerrar}>Sair</button>
              </>
            ) : (
              <>
                <Link to="/entrar">Entrar</Link>
                <Link to="/cadastro" className="botao pequeno">Criar conta</Link>
              </>
            )}
          </div>
        </div>
      </header>
      <main className="conteudo">{children}</main>
    </div>
  )
}
