import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/client.js'
import Aviso from '../components/Aviso.jsx'
import CabecalhoPagina from '../components/CabecalhoPagina.jsx'
import Carregando from '../components/Carregando.jsx'
import RoteiroDias from '../components/RoteiroDias.jsx'
import Vazio from '../components/Vazio.jsx'
import { data, moeda, daquiA, rotulo } from '../utils/formato.js'

const INTERESSES = ['praia', 'gastronomia', 'cultura', 'natureza', 'vida noturna', 'aventura', 'família']

export default function Itinerarios() {
  const [lista, setLista] = useState(null)
  const [destinos, setDestinos] = useState([])
  const [erro, setErro] = useState('')
  const [form, setForm] = useState({
    titulo: 'Minha próxima viagem',
    destinoId: '',
    dataInicio: daquiA(40),
    dataFim: daquiA(43),
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

  function corpoBase() {
    return {
      destinoId: Number(form.destinoId),
      dataInicio: form.dataInicio,
      dataFim: form.dataFim,
      orcamento: Number(form.orcamento),
      interesses: form.interesses
    }
  }

  async function criarManual() {
    setErro('')
    setMensagem('')
    try {
      await api.post('/itinerarios', { ...corpoBase(), titulo: form.titulo })
      setMensagem('Itinerário criado. Abra-o na lista abaixo para incluir as atividades de cada dia.')
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
      setSugestao(await api.post('/itinerarios/sugestoes', corpoBase()))
    } catch (e) {
      setErro(e.status === 503
        ? 'A sugestão automática não respondeu agora. Tente de novo ou crie o itinerário em branco e monte você mesmo.'
        : e.message)
    } finally {
      setGerando(false)
    }
  }

  async function salvarSugestao() {
    setSalvando(true)
    setErro('')
    try {
      await api.post('/itinerarios/sugestoes/aceitar', { ...corpoBase(), titulo: form.titulo, roteiro: sugestao })
      setSugestao(null)
      setMensagem('Roteiro salvo. Você pode abrir o itinerário para ajustar horários e atividades.')
      recarregar()
    } catch (e) {
      setErro(e.message)
    } finally {
      setSalvando(false)
    }
  }

  return (
    <>
      <CabecalhoPagina
        titulo="Itinerários"
        descricao="Escolha destino, datas e o que você gosta de fazer. Peça uma sugestão de roteiro ou comece um itinerário em branco."
      />

      <Aviso tipo="erro">{erro}</Aviso>
      <Aviso tipo="sucesso">{mensagem}</Aviso>

      <div className="cartao">
        <h2>Nova viagem</h2>
        <div className="grade tres">
          <div className="campo">
            <label htmlFor="i-titulo">Nome da viagem</label>
            <input id="i-titulo" value={form.titulo} onChange={e => alterar('titulo', e.target.value)} />
          </div>
          <div className="campo">
            <label htmlFor="i-destino">Destino</label>
            <select id="i-destino" value={form.destinoId} onChange={e => alterar('destinoId', e.target.value)}>
              {destinos.map(d => (
                <option key={d.id} value={d.id}>{d.cidade}, {d.estado}</option>
              ))}
            </select>
          </div>
          <div className="campo">
            <label htmlFor="i-orcamento">Orçamento total</label>
            <input id="i-orcamento" type="number" min="0" step="100" value={form.orcamento}
                   onChange={e => alterar('orcamento', e.target.value)} />
          </div>
          <div className="campo">
            <label htmlFor="i-inicio">Chegada</label>
            <input id="i-inicio" type="date" value={form.dataInicio} onChange={e => alterar('dataInicio', e.target.value)} />
          </div>
          <div className="campo">
            <label htmlFor="i-fim">Volta</label>
            <input id="i-fim" type="date" value={form.dataFim} onChange={e => alterar('dataFim', e.target.value)} />
          </div>
        </div>

        <div className="campo" style={{ marginTop: 20 }}>
          <span className="rotulo-campo" id="rotulo-interesses">O que você gosta de fazer</span>
          <div className="alternaveis" role="group" aria-labelledby="rotulo-interesses">
            {INTERESSES.map(i => (
              <button key={i} type="button" className="alternavel"
                      aria-pressed={form.interesses.includes(i)}
                      onClick={() => alternarInteresse(i)}>
                {i}
              </button>
            ))}
          </div>
        </div>

        <div className="linha-botoes">
          <button className="botao" onClick={gerarComIA} disabled={gerando || !form.destinoId}>
            {gerando ? 'Montando sugestão…' : 'Sugerir roteiro'}
          </button>
          <button className="botao secundario" onClick={criarManual} disabled={!form.destinoId}>
            Criar em branco
          </button>
        </div>
      </div>

      {sugestao && (
        <div className="cartao">
          <div className="linha-titulo">
            <h2>{sugestao.titulo || 'Roteiro sugerido'}</h2>
            {sugestao.custoEstimadoTotal != null && (
              <span className="rotulo-pequeno">
                Custo estimado <strong className="num" style={{ color: 'var(--tinta)' }}>{moeda(sugestao.custoEstimadoTotal)}</strong>
              </span>
            )}
          </div>
          <p className="descricao-secao">
            {sugestao.observacao || 'Roteiro sugerido automaticamente. Confira horários e preços antes de viajar.'}
          </p>

          <RoteiroDias dias={sugestao.dias} />

          <div className="linha-botoes" style={{ marginTop: 20 }}>
            <button className="botao" onClick={salvarSugestao} disabled={salvando}>
              {salvando ? 'Salvando…' : 'Salvar roteiro'}
            </button>
            <button className="botao secundario" onClick={gerarComIA} disabled={gerando}>
              {gerando ? 'Montando…' : 'Sugerir outro'}
            </button>
            <button className="botao perigo" onClick={() => setSugestao(null)}>Descartar</button>
          </div>
        </div>
      )}

      <div className="cartao">
        <h2>Minhas viagens</h2>
        {lista === null ? <Carregando /> : lista.length === 0 ? (
          <Vazio>Nenhuma viagem salva ainda. Preencha o formulário acima para começar a primeira.</Vazio>
        ) : (
          <div className="tabela-rolagem">
            <table>
              <thead>
                <tr>
                  <th>Viagem</th>
                  <th>Destino</th>
                  <th>Período</th>
                  <th className="num">Custo</th>
                  <th>Como foi montado</th>
                  <th>Situação</th>
                </tr>
              </thead>
              <tbody>
                {lista.map(i => (
                  <tr key={i.id}>
                    <td><Link to={`/itinerarios/${i.id}`}>{i.titulo}</Link></td>
                    <td>{i.destino}</td>
                    <td className="num">{data(i.dataInicio)} a {data(i.dataFim)}</td>
                    <td className="num">{moeda(i.custoTotal)}</td>
                    <td>
                      <span className={`selo ${i.origem === 'MANUAL' ? 'cinza' : 'mar'}`}>
                        {rotulo('origemItinerario', i.origem)}
                      </span>
                    </td>
                    <td>
                      <span className={`selo ${i.status === 'CONFIRMADO' ? 'verde' : 'amarelo'}`}>
                        {rotulo('statusItinerario', i.status)}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </>
  )
}
