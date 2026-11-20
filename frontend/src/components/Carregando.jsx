export default function Carregando({ texto = 'Carregando…' }) {
  return <div className="vazio" role="status">{texto}</div>
}
