import { dataExtensa, hora, moeda, rotulo } from '../utils/formato.js'

export default function RoteiroDias({ dias, aoRemover }) {
  return (
    <div>
      {dias.map(dia => {
        const atividades = dia.itens || dia.atividades || []
        const custoDia = dia.custoDoDia ?? atividades.reduce((soma, a) => soma + Number(a.custoEstimado || 0), 0)
        return (
          <section key={dia.id || dia.ordem} className="roteiro-dia">
            <div className="cabeca-dia">
              <div className="numero-dia">Dia {dia.ordem}</div>
              <div className="rotulo-pequeno">{dataExtensa(dia.data)}</div>
              <div className="rotulo-pequeno num">{moeda(custoDia)}</div>
            </div>
            <div>
              {atividades.length === 0 ? (
                <p className="texto-suave pequeno-texto" style={{ margin: '8px 0' }}>
                  Nenhuma atividade neste dia ainda.
                </p>
              ) : atividades.map((a, indice) => (
                <div key={a.id || indice} className="atividade">
                  <div className="horario">
                    {hora(a.horaInicio)}–{hora(a.horaFim)}
                    {a.periodo && <span className="periodo">{rotulo('periodo', a.periodo)}</span>}
                  </div>
                  <div>
                    <div className="titulo-atividade">{a.titulo}</div>
                    {a.descricao && <div className="descricao-atividade">{a.descricao}</div>}
                  </div>
                  <div className="custo">
                    <div className="num">{Number(a.custoEstimado) > 0 ? moeda(a.custoEstimado) : 'Grátis'}</div>
                    {aoRemover && a.id && (
                      <button type="button" className="botao-texto pequeno-texto" onClick={() => aoRemover(a.id)}>
                        Remover
                      </button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </section>
        )
      })}
    </div>
  )
}
