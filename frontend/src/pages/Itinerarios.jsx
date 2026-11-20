import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/client.js'
import Aviso from '../components/Aviso.jsx'
import Carregando from '../components/Carregando.jsx'
import Vazio from '../components/Vazio.jsx'
import { data, moeda, hora, daquiA } from '../utils/formato.js'

const INTERESSES = ['gastronomia', 'natureza', 'cultura', 'vida noturna', 'praia', 'aventura', 'familia']

export default function Itinerarios() {
  const [lista, setLista] = useState(null)
  const [destinos, setDestinos] = useState([])
  const [erro, setErro] = useState('')
  const [form, setForm] = useState({
    titulo: 'Minha proxima viagem',
    destinoId: '',
    dataInicio: daquiA(40),
    dataFim: daquiA(44),
    orcamento: 2000,
    interesses: ['cultura', 'gastronomia']
  })
  const [sugestao, setSugestao] = useState(null)
  const [gerando, setGerando] = useState(false)
  const [salvando, setSalvando] = useState(false)
  const [mensagem, setMensagem] = useState('')

  useEffect(() => { recarregar() }, [])

  async function recarregar() {
    try {
      const [itinerarios, listaDestinos] = await Promise.all([
        api.get('/itinerarios'),
        api.get('/destinos')
      ])
      setLista(itinerarios)
      setDestinos(listaDestinos)
      if (listaDestinos.length && !form.destinoId) {
        setForm(a => ({ ...a, destinoId: String(listaDestinos[0].id) }))
      }
    } catch (e) {
      setErro(e.message)
      setLista([])
    }
  }

  function alterar(campo, valor) {
    setForm(anterior => ({ ...anterior, [campo]: valor }))
  }

  function alternarInteresse(interesse) {
    setForm(anterior => ({
      ...anterior,
      interesses: anterior.interesses.includes(interesse)
        ? anterior.interesses.filter(i => i !== interesse)
        : [...anterior.interesses, interesse]
    }))
  }

  async function criarManual() {
    setErro('')
    setMensagem('')
    try {
      await api.post('/itinerarios', {
        ...form,
        destinoId: Number(form.destinoId),
        orcamento: Number(form.orcamento)
      })
      setMensagem('Itinerario criado em branco. Abra para montar os dias.')
      recarregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  async function gerarComIA() {
    setErro('')
    setMensagem('')
    setSugestao(null)
    setGerando(true)
    try {
      const dados = await api.post('/itinerarios/sugestoes', {
        destinoId: Number(form.destinoId),
        dataInicio: form.dataInicio,
        dataFim: form.dataFim,
        orcamento: Number(form.orcamento),
        interesses: form.interesses
      })
      setSugestao(dados)
    } catch (e) {
      setErro(e.status === 503
        ? 'A IA esta indisponivel no momento. Voce ainda pode montar o roteiro manualmente.'
        : e.message)
    } finally {
      setGerando(false)
    }
  }

  async function aceitarSugestao() {
    setSalvando(true)
    setErro('')
    try {
      await api.post('/itinerarios/sugestoes/aceitar', {
        titulo: form.titulo,
        destinoId: Number(form.destinoId),
        dataInicio: form.dataInicio,
        dataFim: form.dataFim,
        orcamento: Number(form.orcamento),
        interesses: form.interesses,
        roteiro: sugestao
      })
      setSugestao(null)
      setMensagem('Roteiro salvo. Abra o itinerario para editar os itens.')
      recarregar()
    } catch (e) {
      setErro(e.message)
    } finally {
      setSalvando(false)
    }
  }

  return (
    <>
      <h1>Itinerarios</h1>

      <Aviso tipo="erro">{erro}</Aviso>
      <Aviso tipo="sucesso">{mensagem}</Aviso>

      <div className="cartao">
        <h2>Novo itinerario</h2>
        <div className="grade tres">
          <div className="campo">
            <label>Titulo</label>
            <input value={form.titulo} onChange={e => alterar('titulo', e.target.value)} />
          </div>
          <div className="campo">
            <label>Destino</label>
            <select value={form.destinoId} onChange={e => alterar('destinoId', e.target.value)}>
              {destinos.map(d => (
                <option key={d.id} value={d.id}>{d.cidade}/{d.estado}</option>
              ))}
            </select>
          </div>
          <div className="campo">
            <label>Inicio</label>
            <input type="date" value={form.dataInicio} onChange={e => alterar('dataInicio', e.target.value)} />
          </div>
          <div className="campo">
            <label>Fim</label>
            <input type="date" value={form.dataFim} onChange={e => alterar('dataFim', e.target.value)} />
          </div>
          <div className="campo">
            <label>Orcamento</label>
            <input type="number" min="0" step="100" value={form.orcamento}
                   onChange={e => alterar('orcamento', e.target.value)} />
          </div>
        </div>

        <div className="campo">
          <label>Interesses</label>
          <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
            {INTERESSES.map(i => (
              <button key={i} type="button"
                      className={`botao pequeno ${form.interesses.includes(i) ? '' : 'secundario'}`}
                      onClick={() => alternarInteresse(i)}>
                {i}
              </button>
            ))}
          </div>
        </div>

        <div className="linha-botoes">
          <button className="botao" onClick={gerarComIA} disabled={gerando || !form.destinoId}>
            {gerando ? 'Gerando sugestao...' : 'Gerar roteiro com IA'}
          </button>
          <button className="botao secundario" onClick={criarManual} disabled={!form.destinoId}>
            Criar em branco
          </button>
        </div>
      </div>

      {sugestao && (
        <div className="cartao">
          <h2>{sugestao.titulo}</h2>
          <Aviso tipo="atencao">
            {sugestao.observacao || 'Sugestao gerada automaticamente. Revise antes de salvar.'}
          </Aviso>
          <p className="rotulo-pequeno">
            Custo estimado pela IA: {moeda(sugestao.custoEstimadoTotal)}
          </p>

          {sugestao.dias.map(dia => (
            <div key={dia.ordem} className="dia-itinerario">
              <h3>Dia {dia.ordem} - {data(dia.data)}</h3>
              {dia.atividades.map((a, indice) => (
                <div key={indice} className="item-itinerario">
                  <div>
                    <strong>{a.titulo}</strong>
                    <div className="rotulo-pequeno">
                      {a.periodo} - {hora(a.horaInicio)} as {hora(a.horaFim)}
                    </div>
                    <div style={{ fontSize: 13, color: 'var(--cinza-600)' }}>{a.descricao}</div>
                  </div>
                  <div style={{ whiteSpace: 'nowrap' }}>{moeda(a.custoEstimado)}</div>
                </div>
              ))}
            </div>
          ))}

          <div className="linha-botoes">
            <button className="botao" onClick={aceitarSugestao} disabled={salvando}>
              {salvando ? 'Salvando...' : 'Aceitar e salvar'}
            </button>
            <button className="botao secundario" onClick={gerarComIA} disabled={gerando}>
              Gerar outra
            </button>
            <button className="botao perigo" onClick={() => setSugestao(null)}>Descartar</button>
          </div>
        </div>
      )}

      <div className="cartao">
        <h2>Meus itinerarios</h2>
        {lista === null ? <Carregando /> : lista.length === 0 ? (
          <Vazio>Voce ainda nao tem itinerarios salvos.</Vazio>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Titulo</th>
                <th>Destino</th>
                <th>Periodo</th>
                <th>Custo</th>
                <th>Origem</th>
                <th>Situacao</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {lista.map(i => (
                <tr key={i.id}>
                  <td>{i.titulo}</td>
                  <td>{i.destino}</td>
                  <td>{data(i.dataInicio)} a {data(i.dataFim)}</td>
                  <td>{moeda(i.custoTotal)}</td>
                  <td>
                    <span className={`selo ${i.origem === 'MANUAL' ? 'cinza' : ''}`}>
                      {i.origem === 'MANUAL' ? 'manual'
                        : i.origem === 'SUGERIDO_IA' ? 'IA' : 'IA editado'}
                    </span>
                  </td>
                  <td>
                    <span className={`selo ${i.status === 'CONFIRMADO' ? 'verde' : 'amarelo'}`}>
                      {i.status.toLowerCase()}
                    </span>
                  </td>
                  <td><Link to={`/itinerarios/${i.id}`}>Abrir</Link></td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </>
  )
}
