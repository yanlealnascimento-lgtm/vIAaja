import { Link } from 'react-router-dom'

export default function NaoEncontrada() {
  return (
    <div className="cartao">
      <h1>Pagina nao encontrada</h1>
      <p>O endereco acessado nao existe na plataforma.</p>
      <Link className="botao" to="/">Voltar ao comparador</Link>
    </div>
  )
}
