import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import { api } from '../api/client.js'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(null)
  const [carregando, setCarregando] = useState(true)

  useEffect(() => {
    const salvo = localStorage.getItem('viaaja.usuario')
    if (salvo) setUsuario(JSON.parse(salvo))
    setCarregando(false)
  }, [])

  async function entrar(email, senha) {
    const dados = await api.post('/auth/login', { email, senha })
    localStorage.setItem('viaaja.token', dados.token)
    const perfil = {
      id: dados.usuarioId,
      nome: dados.nome,
      perfil: dados.perfil
    }
    localStorage.setItem('viaaja.usuario', JSON.stringify(perfil))
    setUsuario(perfil)
    return perfil
  }

  function sair() {
    localStorage.removeItem('viaaja.token')
    localStorage.removeItem('viaaja.usuario')
    setUsuario(null)
  }

  const valor = useMemo(() => ({
    usuario,
    carregando,
    autenticado: Boolean(usuario),
    entrar,
    sair
  }), [usuario, carregando])

  return <AuthContext.Provider value={valor}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const contexto = useContext(AuthContext)
  if (!contexto) throw new Error('useAuth precisa estar dentro de AuthProvider')
  return contexto
}
