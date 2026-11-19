import { NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext.jsx'

const LINKS = [
  { para: '/', texto: 'Comparador', perfis: null },
  { para: '/busca', texto: 'Buscas', perfis: null },
  { para: '/assistente', texto: 'Assistente', perfis: ['VIAJANTE'] },
  { para: '/itinerarios', texto: 'Itinerarios', perfis: ['VIAJANTE'] },
  { para: '/social', texto: 'Comunidade', perfis: ['VIAJANTE'] },
  { para: '/destinos', texto: 'Destinos', perfis: null },
  { para: '/perfil', texto: 'Perfil', perfis: ['VIAJANTE'] },
  { para: '/parceiro', texto: 'Minhas ofertas', perfis: ['PARCEIRO'] },
  { para: '/admin', texto: 'Administracao', perfis: ['ADMINISTRADOR'] }
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
        <span className="marca">vIAaja</span>
        <nav>
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
        <div className="usuario">
          {autenticado ? (
            <>
              <span>{usuario.nome}</span>
              <button className="botao secundario pequeno" onClick={encerrar}>Sair</button>
            </>
          ) : (
            <>
              <NavLink to="/entrar" style={{ color: '#cfdcf0' }}>Entrar</NavLink>
              <NavLink to="/cadastro" className="botao pequeno">Criar conta</NavLink>
            </>
          )}
        </div>
      </header>
      <main className="conteudo">{children}</main>
      <footer className="rodape">
        vIAaja - Topicos Avancados em Programacao - UCSAL - 2026.2
      </footer>
    </div>
  )
}
