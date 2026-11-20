import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '../api/client.js'
import Aviso from '../components/Aviso.jsx'
import Carregando from '../components/Carregando.jsx'
import { data, moeda, hora } from '../utils/formato.js'

const VAZIO = {
  ordemDoDia: 1, titulo: '', descricao: '', periodo: 'Manha',
  horaInicio: '09:00', horaFim: '12:00', custoEstimado: 0
}

export default function ItinerarioDetalhe() {
  const { id } = useParams()
  const navegar = useNavigate()
  const [itinerario, setItinerario] = useState(null)
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')
  const [novoItem, setNovoItem] = useState(VAZIO)

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
      setNovoItem({ ...VAZIO, ordemDoDia: novoItem.ordemDoDia })
      setMensagem('Atividade adicionada.')
    } catch (e) {
      setErro(e.message)
    }
  }

  async function remover(itemId) {
    setErro('')
    try {
      setItinerario(await api.del(`/itinerarios/${id}/itens/${itemId}`))
    } catch (e) {
      setErro(e.message)
    }
  }

  async function confirmar() {
    setErro('')
    try {
      setItinerario(await api.patch(`/itinerarios/${id}/confirmar`))
      setMensagem('Itinerario confirmado.')
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

  if (!itinerario) return <Carregando />

  const acimaDoOrcamento = itinerario.orcamento
    && Number(itinerario.custoTotal) > Number(itinerario.orcamento)

  return (
    <>
      <h1>{itinerario.titulo}</h1>
      <p style={{ marginTop: -6, color: 'var(--cinza-600)' }}>
        {itinerario.destino} - {data(itinerario.dataInicio)} a {data(itinerario.dataFim)}
      </p>

      <Aviso tipo="erro">{erro}</Aviso>
      <Aviso tipo="sucesso">{mensagem}</Aviso>
      {acimaDoOrcamento && (
        <Aviso tipo="atencao">
          O custo estimado passou do orcamento de {moeda(itinerario.orcamento)}.
        </Aviso>
      )}

      <div className="cartao">
        <div className="grade tres">
          <div>
            <div className="rotulo-pequeno">Custo total</div>
            <div className="valor-grande">{moeda(itinerario.custoTotal)}</div>
          </div>
          <div>
            <div className="rotulo-pequeno">Orcamento</div>
            <div className="valor-grande">{moeda(itinerario.orcamento)}</div>
          </div>
          <div>
            <div className="rotulo-pequeno">Origem</div>
            <div style={{ marginTop: 6 }}>
              <span className="selo">
                {itinerario.origem === 'MANUAL' ? 'montado manualmente'
                  : itinerario.origem === 'SUGERIDO_IA' ? 'sugerido pela IA'
                  : 'sugerido pela IA e editado'}
              </span>
            </div>
          </div>
        </div>
        <div className="linha-botoes" style={{ marginTop: 16 }}>
          <button className="botao" onClick={confirmar}
                  disabled={itinerario.status === 'CONFIRMADO'}>
            {itinerario.status === 'CONFIRMADO' ? 'Confirmado' : 'Confirmar itinerario'}
          </button>
          <button className="botao perigo" onClick={excluir}>Excluir itinerario</button>
        </div>
      </div>

      <div className="cartao">
        <h2>Adicionar atividade</h2>
        <form onSubmit={adicionar}>
          <div className="grade tres">
            <div className="campo">
              <label>Dia</label>
              <select value={novoItem.ordemDoDia} onChange={e => alterar('ordemDoDia', e.target.value)}>
                {itinerario.dias.map(d => (
                  <option key={d.ordem} value={d.ordem}>Dia {d.ordem} - {data(d.data)}</option>
                ))}
              </select>
            </div>
            <div className="campo">
              <label>Titulo</label>
              <input value={novoItem.titulo} required onChange={e => alterar('titulo', e.target.value)} />
            </div>
            <div className="campo">
              <label>Periodo</label>
              <select value={novoItem.periodo} onChange={e => alterar('periodo', e.target.value)}>
                <option>Manha</option>
                <option>Tarde</option>
                <option>Noite</option>
              </select>
            </div>
            <div className="campo">
              <label>Hora de inicio</label>
              <input type="time" value={novoItem.horaInicio}
                     onChange={e => alterar('horaInicio', e.target.value)} />
            </div>
            <div className="campo">
              <label>Hora de fim</label>
              <input type="time" value={novoItem.horaFim}
                     onChange={e => alterar('horaFim', e.target.value)} />
            </div>
            <div className="campo">
              <label>Custo estimado</label>
              <input type="number" min="0" step="10" value={novoItem.custoEstimado}
                     onChange={e => alterar('custoEstimado', e.target.value)} />
            </div>
          </div>
          <div className="campo">
            <label>Descricao</label>
            <input value={novoItem.descricao} onChange={e => alterar('descricao', e.target.value)} />
          </div>
          <button className="botao" type="submit">Adicionar</button>
        </form>
      </div>

      <div className="cartao">
        <h2>Roteiro dia a dia</h2>
        {itinerario.dias.map(dia => (
          <div key={dia.id} className="dia-itinerario">
            <h3>Dia {dia.ordem} - {data(dia.data)} - {moeda(dia.custoDoDia)}</h3>
            {dia.itens.length === 0 ? (
              <p style={{ color: 'var(--cinza-600)', fontSize: 14 }}>Nenhuma atividade neste dia.</p>
            ) : dia.itens.map(item => (
              <div key={item.id} className="item-itinerario">
                <div>
                  <strong>{item.titulo}</strong>
                  <div className="rotulo-pequeno">
                    {item.periodo} - {hora(item.horaInicio)} as {hora(item.horaFim)}
                  </div>
                  <div style={{ fontSize: 13, color: 'var(--cinza-600)' }}>{item.descricao}</div>
                </div>
                <div style={{ textAlign: 'right', whiteSpace: 'nowrap' }}>
                  <div>{moeda(item.custoEstimado)}</div>
                  <button className="botao perigo pequeno" style={{ marginTop: 6 }}
                          onClick={() => remover(item.id)}>Remover</button>
                </div>
              </div>
            ))}
          </div>
        ))}
      </div>
    </>
  )
}
