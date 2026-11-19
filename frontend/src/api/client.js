const BASE = import.meta.env.VITE_API_URL || ''

export class ApiError extends Error {
  constructor(status, corpo) {
    super(corpo?.mensagem || 'Falha na comunicacao com o servidor')
    this.status = status
    this.corpo = corpo
  }
}

function token() {
  return localStorage.getItem('viaaja.token')
}

async function requisitar(metodo, caminho, corpo) {
  const cabecalhos = { 'Content-Type': 'application/json' }
  const jwt = token()
  if (jwt) cabecalhos.Authorization = `Bearer ${jwt}`

  const resposta = await fetch(`${BASE}/api${caminho}`, {
    method: metodo,
    headers: cabecalhos,
    body: corpo === undefined ? undefined : JSON.stringify(corpo)
  })

  if (resposta.status === 204) return null

  const texto = await resposta.text()
  const dados = texto ? JSON.parse(texto) : null

  if (!resposta.ok) {
    if (resposta.status === 401) {
      localStorage.removeItem('viaaja.token')
      localStorage.removeItem('viaaja.usuario')
    }
    throw new ApiError(resposta.status, dados)
  }
  return dados
}

export const api = {
  get: (caminho) => requisitar('GET', caminho),
  post: (caminho, corpo) => requisitar('POST', caminho, corpo),
  put: (caminho, corpo) => requisitar('PUT', caminho, corpo),
  patch: (caminho, corpo) => requisitar('PATCH', caminho, corpo),
  del: (caminho) => requisitar('DELETE', caminho)
}

export function parametros(objeto) {
  const busca = new URLSearchParams()
  Object.entries(objeto).forEach(([chave, valor]) => {
    if (valor !== undefined && valor !== null && valor !== '') busca.append(chave, valor)
  })
  const texto = busca.toString()
  return texto ? `?${texto}` : ''
}
