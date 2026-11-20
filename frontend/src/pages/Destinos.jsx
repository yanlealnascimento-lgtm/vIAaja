import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api, parametros } from '../api/client.js'
import Aviso from '../components/Aviso.jsx'
import Carregando from '../components/Carregando.jsx'
import Vazio from '../components/Vazio.jsx'

export default function Destinos() {
  const [lista, setLista] = useState(null)
  const [cidade, setCidade] = useState('')
  const [erro, setErro] = useState('')

  useEffect(() => { buscar() }, [])

  async function buscar(evento) {
    if (evento) evento.preventDefault()
    setErro('')
    try {
      setLista(await api.get('/destinos' + parametros({ cidade })))
    } catch (e) {
      setErro(e.message)
      setLista([])
    }
  }

  return (
    <>
      <h1>Destinos</h1>
      <div className="cartao">
        <form onSubmit={buscar} className="linha-botoes">
          <input placeholder="Filtrar por cidade" value={cidade}
                 onChange={e => setCidade(e.target.value)} style={{ maxWidth: 320 }} />
          <button className="botao" type="submit">Filtrar</button>
        </form>
      </div>

      <Aviso tipo="erro">{erro}</Aviso>

      {lista === null ? <Carregando /> : lista.length === 0 ? (
        <Vazio>Nenhum destino encontrado.</Vazio>
      ) : (
        <div className="grade duas">
          {lista.map(d => (
            <div key={d.id} className="cartao">
              <h2>{d.cidade}/{d.estado}</h2>
              <p style={{ color: 'var(--cinza-600)' }}>{d.descricao}</p>
              {d.distanciaReferenciaKm && (
                <p className="rotulo-pequeno">
                  Distancia de referencia: {d.distanciaReferenciaKm} km
                </p>
              )}
              <Link className="botao secundario pequeno" to={`/destinos/${d.id}`}>
                Ver atividades e avaliacoes
              </Link>
            </div>
          ))}
        </div>
      )}
    </>
  )
}
