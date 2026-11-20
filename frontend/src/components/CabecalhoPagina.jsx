export default function CabecalhoPagina({ titulo, descricao, children }) {
  return (
    <header className="cabecalho-pagina">
      <h1>{titulo}</h1>
      {descricao && <p className="descricao">{descricao}</p>}
      {children && <div className="acoes">{children}</div>}
    </header>
  )
}
