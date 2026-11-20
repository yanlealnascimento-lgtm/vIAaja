import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api, parametros } from '../api/client.js'
import Aviso from '../components/Aviso.jsx'
import CabecalhoPagina from '../components/CabecalhoPagina.jsx'
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
      <CabecalhoPagina
        titulo="Destinos"
        descricao="Cidades com ofertas cadastradas. Abra uma delas para ver as atividades e o que os viajantes acharam."
      />

      <form onSubmit={buscar} className="linha-busca" style={{ marginBottom: 28 }}>
        <input aria-label="Filtrar por cidade" placeholder="Filtrar por cidade" value={cidade}
               onChange={e => setCidade(e.target.value)} />
        <button className="botao secundario" type="submit">Filtrar</button>
      </form>

      <Aviso tipo="erro">{erro}</Aviso>

      {lista === null ? <Carregando /> : lista.length === 0 ? (
        <Vazio>Nenhuma cidade encontrada com esse nome.</Vazio>
      ) : (
        <div className="grade duas">
          {lista.map(d => (
            <article key={d.id} className="cartao cartao-destino">
              <h2>{d.cidade}</h2>
              <div className="estado">{d.estado}, {d.pais}</div>
              <p>{d.descricao}</p>
              <div className="rodape-cartao">
                <span className="rotulo-pequeno num">
                  {d.distanciaReferenciaKm ? `${d.distanciaReferenciaKm} km de Salvador` : ''}
                </span>
                <Link to={`/destinos/${d.id}`}>Ver atividades e avaliações</Link>
              </div>
            </article>
          ))}
        </div>
      )}
    </>
  )
}
