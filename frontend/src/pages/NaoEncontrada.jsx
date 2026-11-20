import { Link } from 'react-router-dom'
import CabecalhoPagina from '../components/CabecalhoPagina.jsx'

export default function NaoEncontrada() {
  return (
    <CabecalhoPagina titulo="Página não encontrada" descricao="Esse endereço não existe ou foi removido.">
      <Link className="botao" to="/">Ir para o comparador</Link>
    </CabecalhoPagina>
  )
}
