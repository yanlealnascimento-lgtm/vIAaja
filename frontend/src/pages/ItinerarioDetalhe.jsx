import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { api } from '../api/client.js'
import Aviso from '../components/Aviso.jsx'
import CabecalhoPagina from '../components/CabecalhoPagina.jsx'
import Carregando from '../components/Carregando.jsx'
import RoteiroDias from '../components/RoteiroDias.jsx'
import { data, dataExtensa, moeda, rotulo } from '../utils/formato.js'

const NOVO_ITEM = {
  ordemDoDia: 1, titulo: '', descricao: '', periodo: 'MANHA',
  horaInicio: '09:00', horaFim: '12:00', custoEstimado: 0
}

export default function ItinerarioDetalhe() {
  const { id } = useParams()
  const navegar = useNavigate()
  const [itinerario, setItinerario] = useState(null)
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')
  const [novoItem, setNovoItem] = useState(NOVO_ITEM)
  const [confirmandoExclusao, setConfirmandoExclusao] = useState(false)

  useEffect(() => { carregar() }, [id])

  async function carregar() {
    try {
      setItinerario(await api.get(`/itinerarios/${id}`))
    } catch (e) {
      setErro(e.message)
    }
  }

  function alterar(campo, valor) {
    setNovoItem(anterior => ({ ...anterior, [campo]: valor }))
  }

  async function adicionar(evento) {
    evento.preventDefault()
    setErro('')
    setMensagem('')
    try {
      const atualizado = await api.post(`/itinerarios/${id}/itens`, {
        ...novoItem,
        ordemDoDia: Number(novoItem.ordemDoDia),
        custoEstimado: Number(novoItem.custoEstimado)
      })
      setItinerario(atualizado)
      setNovoItem({ ...NOVO_ITEM, ordemDoDia: novoItem.ordemDoDia })
      setMensagem(`Atividade incluída no dia ${novoItem.ordemDoDia}.`)
    } catch (e) {
      setErro(e.message)
    }
  }

  async function remover(itemId) {
    setErro('')
    setMensagem('')
    try {
      setItinerario(await api.del(`/itinerarios/${id}/itens/${itemId}`))
      setMensagem('Atividade removida.')
    } catch (e) {
      setErro(e.message)
    }
  }

  async function confirmar() {
    setErro('')
    try {
      setItinerario(await api.patch(`/itinerarios/${id}/confirmar`))
      setMensagem('Itinerário confirmado.')
    } catch (e) {
      setErro(e.message)
    }
  }

  async function excluir() {
    setErro('')
    try {
      await api.del(`/itinerarios/${id}`)
      navegar('/itinerarios')
    } catch (e) {
      setErro(e.message)
    }
  }

  if (!itinerario) {
    return erro ? <Aviso tipo="erro">{erro}</Aviso> : <Carregando />
  }

  const acimaDoOrcamento = itinerario.orcamento
    && Number(itinerario.custoTotal) > Number(itinerario.orcamento)
  const confirmado = itinerario.status === 'CONFIRMADO'

  return (
    <>
      <p className="pequeno-texto" style={{ marginBottom: 12 }}>
        <Link to="/itinerarios">Minhas viagens</Link>
      </p>
      <CabecalhoPagina
        titulo={itinerario.titulo}
        descricao={`${itinerario.destino}, de ${data(itinerario.dataInicio)} a ${data(itinerario.dataFim)}.`}
      />

      <Aviso tipo="erro">{erro}</Aviso>
      <Aviso tipo="sucesso">{mensagem}</Aviso>
      {acimaDoOrcamento && (
        <Aviso tipo="atencao">
          O custo estimado passou do orçamento de {moeda(itinerario.orcamento)}. Remova ou troque
          alguma atividade para voltar ao limite.
        </Aviso>
      )}

      <div className="indicadores">
        <div className="indicador">
          <div className="rotulo-pequeno">Custo estimado</div>
          <div className="valor-grande">{moeda(itinerario.custoTotal)}</div>
        </div>
        <div className="indicador">
          <div className="rotulo-pequeno">Orçamento</div>
          <div className="valor-grande">{moeda(itinerario.orcamento)}</div>
        </div>
        <div className="indicador">
          <div className="rotulo-pequeno">Como foi montado</div>
          <div className="dado-rotulado"><div className="valor">{rotulo('origemItinerario', itinerario.origem)}</div></div>
        </div>
        <div className="indicador">
          <div className="rotulo-pequeno">Situação</div>
          <div className="dado-rotulado"><div className="valor">{rotulo('statusItinerario', itinerario.status)}</div></div>
        </div>
      </div>

      <div className="cartao">
        <div className="linha-titulo" style={{ marginBottom: 8 }}>
          <h2 style={{ margin: 0 }}>Roteiro dia a dia</h2>
          <div className="linha-botoes">
            <button className="botao pequeno" onClick={confirmar} disabled={confirmado}>
              {confirmado ? 'Confirmado' : 'Confirmar itinerário'}
            </button>
          </div>
        </div>
        <RoteiroDias dias={itinerario.dias} aoRemover={remover} />
      </div>

      <div className="cartao">
        <h2>Incluir atividade</h2>
        <form onSubmit={adicionar}>
          <div className="grade tres">
            <div className="campo">
              <label htmlFor="a-dia">Dia</label>
              <select id="a-dia" value={novoItem.ordemDoDia} onChange={e => alterar('ordemDoDia', e.target.value)}>
                {itinerario.dias.map(d => (
                  <option key={d.ordem} value={d.ordem}>Dia {d.ordem}, {dataExtensa(d.data)}</option>
                ))}
              </select>
            </div>
            <div className="campo">
              <label htmlFor="a-titulo">Atividade</label>
              <input id="a-titulo" value={novoItem.titulo} required placeholder="Ex.: passeio de escuna"
                     onChange={e => alterar('titulo', e.target.value)} />
            </div>
            <div className="campo">
              <label htmlFor="a-periodo">Período</label>
              <select id="a-periodo" value={novoItem.periodo} onChange={e => alterar('periodo', e.target.value)}>
                <option value="MANHA">Manhã</option>
                <option value="TARDE">Tarde</option>
                <option value="NOITE">Noite</option>
              </select>
            </div>
            <div className="campo">
              <label htmlFor="a-inicio">Começa às</label>
              <input id="a-inicio" type="time" value={novoItem.horaInicio}
                     onChange={e => alterar('horaInicio', e.target.value)} />
            </div>
            <div className="campo">
              <label htmlFor="a-fim">Termina às</label>
              <input id="a-fim" type="time" value={novoItem.horaFim}
                     onChange={e => alterar('horaFim', e.target.value)} />
            </div>
            <div className="campo">
              <label htmlFor="a-custo">Custo estimado</label>
              <input id="a-custo" type="number" min="0" step="10" value={novoItem.custoEstimado}
                     onChange={e => alterar('custoEstimado', e.target.value)} />
            </div>
          </div>
          <div className="campo" style={{ marginTop: 18 }}>
            <label htmlFor="a-descricao">Observação</label>
            <input id="a-descricao" value={novoItem.descricao} placeholder="Opcional"
                   onChange={e => alterar('descricao', e.target.value)} />
          </div>
          <button className="botao" type="submit">Incluir atividade</button>
        </form>
      </div>

      <div className="linha-botoes">
        {confirmandoExclusao ? (
          <>
            <span className="pequeno-texto">Excluir esta viagem e todas as atividades? Isso não pode ser desfeito.</span>
            <button className="botao perigo pequeno" onClick={excluir}>Sim, excluir</button>
            <button className="botao secundario pequeno" onClick={() => setConfirmandoExclusao(false)}>Cancelar</button>
          </>
        ) : (
          <button className="botao perigo pequeno" onClick={() => setConfirmandoExclusao(true)}>Excluir itinerário</button>
        )}
      </div>
    </>
  )
}
