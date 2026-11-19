import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from './AuthContext.jsx'

export default function RotaProtegida({ children, perfis }) {
  const { autenticado, usuario, carregando } = useAuth()
  const local = useLocation()

  if (carregando) return null
  if (!autenticado) return <Navigate to="/entrar" state={{ de: local.pathname }} replace />
  if (perfis && !perfis.includes(usuario.perfil)) return <Navigate to="/" replace />

  return children
}
