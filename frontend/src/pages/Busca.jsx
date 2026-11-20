import { useState } from 'react'
import { api, parametros } from '../api/client.js'
import Aviso from '../components/Aviso.jsx'
import Vazio from '../components/Vazio.jsx'
import { moeda, dataHora, duracao, nomeModal, daquiA } from '../utils/formato.js'

export default function Busca() {
  const [aba, setAba] = useState('passagens')

  return (
    <>
      <h1>Buscas</h1>
      <div className="linha-botoes" style={{ marginBottom: 16 }}>
        <button className={`botao ${aba === 'passagens' ? '' : 'secundario'}`}
                onClick={() => setAba('passagens')}>Passagens</button>
        <button className={`botao ${aba === 'hospedagens' ? '' : 'secundario'}`}
                onClick={() => setAba('hospedagens')}>Hospedagens</button>
      </div>
      {aba === 'passagens' ? <BuscaPassagens /> : <BuscaHospedagens />}
    </>
  )
}

function BuscaPassagens() {
  const [form, setForm] = useState({
    origem: 'Salvador', destino: 'Porto Seguro', data: daquiA(35),
    passageiros: 1, modal: '', precoMaximo: '', paradasMaximas: '', ordenarPor: 'preco'
  })
  const [pagina, setPagina] = useState(null)
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(false)
  const [buscou, setBuscou] = useState(false)

  function alterar(campo, valor) {
    setForm(anterior => ({ ...anterior, [campo]: valor }))
  }

  async function buscar(evento) {
    evento.preventDefault()
    setErro('')
    setCarregando(true)
    try {
      const dados = await api.get('/busca/passagens' + parametros(form))
      setPagina(dados)
      setBuscou(true)
    } catch (e) {
      setErro(e.message)
    } finally {
      setCarregando(false)
    }
  }

  return (
    <>
      <div className="cartao">
        <form onSubmit={buscar}>
          <div className="grade tres">
            <div className="campo">
              <label>Origem</label>
              <input value={form.origem} required onChange={e => alterar('origem', e.target.value)} />
            </div>
            <div className="campo">
              <label>Destino</label>
              <input value={form.destino} required onChange={e => alterar('destino', e.target.value)} />
            </div>
            <div className="campo">
              <label>Data</label>
              <input type="date" value={form.data} required onChange={e => alterar('data', e.target.value)} />
            </div>
            <div className="campo">
              <label>Passageiros</label>
              <input type="number" min="1" max="9" value={form.passageiros}
                     onChange={e => alterar('passageiros', e.target.value)} />
            </div>
            <div className="campo">
              <label>Modal</label>
              <select value={form.modal} onChange={e => alterar('modal', e.target.value)}>
                <option value="">Todos</option>
                <option value="AVIAO">Aviao</option>
                <option value="ONIBUS">Onibus</option>
              </select>
            </div>
            <div className="campo">
              <label>Preco maximo</label>
              <input type="number" min="0" step="10" value={form.precoMaximo}
                     onChange={e => alterar('precoMaximo', e.target.value)} />
            </div>
            <div className="campo">
              <label>Paradas no maximo</label>
              <input type="number" min="0" max="5" value={form.paradasMaximas}
                     onChange={e => alterar('paradasMaximas', e.target.value)} />
            </div>
            <div className="campo">
              <label>Ordenar por</label>
              <select value={form.ordenarPor} onChange={e => alterar('ordenarPor', e.target.value)}>
                <option value="preco">Preco</option>
                <option value="duracao">Chegada</option>
                <option value="partida">Partida</option>
                <option value="paradas">Paradas</option>
              </select>
            </div>
          </div>
          <button className="botao" type="submit" disabled={carregando}>
            {carregando ? 'Buscando...' : 'Buscar passagens'}
          </button>
        </form>
      </div>

      <Aviso tipo="erro">{erro}</Aviso>

      {buscou && pagina && (
        <div className="cartao">
          <h2>{pagina.totalElementos} resultado(s)</h2>
          {pagina.conteudo.length === 0 ? (
            <Vazio>Nenhuma passagem encontrada com esses filtros.</Vazio>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>Modal</th>
                  <th>Empresa</th>
                  <th>Partida</th>
                  <th>Chegada</th>
                  <th>Duracao</th>
                  <th>Paradas</th>
                  <th>Preco</th>
                </tr>
              </thead>
              <tbody>
                {pagina.conteudo.map(p => (
                  <tr key={p.id}>
                    <td>{nomeModal(p.modal)}</td>
                    <td>{p.empresa}</td>
                    <td>{dataHora(p.dataPartida)}</td>
                    <td>{dataHora(p.dataChegada)}</td>
                    <td>{duracao(p.duracaoMinutos)}</td>
                    <td>{p.numeroParadas}</td>
                    <td><strong>{moeda(p.preco)}</strong></td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}
    </>
  )
}

function BuscaHospedagens() {
  const [form, setForm] = useState({
    cidade: 'Porto Seguro', hospedes: 2, precoMaximo: '', categoriaMinima: '', ordenarPor: 'preco'
  })
  const [pagina, setPagina] = useState(null)
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(false)
  const [buscou, setBuscou] = useState(false)

  function alterar(campo, valor) {
    setForm(anterior => ({ ...anterior, [campo]: valor }))
  }

  async function buscar(evento) {
    evento.preventDefault()
    setErro('')
    setCarregando(true)
    try {
      const dados = await api.get('/busca/hospedagens' + parametros(form))
      setPagina(dados)
      setBuscou(true)
    } catch (e) {
      setErro(e.message)
    } finally {
      setCarregando(false)
    }
  }

  return (
    <>
      <div className="cartao">
        <form onSubmit={buscar}>
          <div className="grade tres">
            <div className="campo">
              <label>Cidade</label>
              <input value={form.cidade} required onChange={e => alterar('cidade', e.target.value)} />
            </div>
            <div className="campo">
              <label>Hospedes</label>
              <input type="number" min="1" max="9" value={form.hospedes}
                     onChange={e => alterar('hospedes', e.target.value)} />
            </div>
            <div className="campo">
              <label>Preco maximo da diaria</label>
              <input type="number" min="0" step="10" value={form.precoMaximo}
                     onChange={e => alterar('precoMaximo', e.target.value)} />
            </div>
            <div className="campo">
              <label>Categoria minima</label>
              <select value={form.categoriaMinima} onChange={e => alterar('categoriaMinima', e.target.value)}>
                <option value="">Qualquer</option>
                <option value="3">3 estrelas ou mais</option>
                <option value="4">4 estrelas ou mais</option>
                <option value="5">5 estrelas</option>
              </select>
            </div>
            <div className="campo">
              <label>Ordenar por</label>
              <select value={form.ordenarPor} onChange={e => alterar('ordenarPor', e.target.value)}>
                <option value="preco">Preco</option>
                <option value="categoria">Categoria</option>
              </select>
            </div>
          </div>
          <button className="botao" type="submit" disabled={carregando}>
            {carregando ? 'Buscando...' : 'Buscar hospedagens'}
          </button>
        </form>
      </div>

      <Aviso tipo="erro">{erro}</Aviso>

      {buscou && pagina && (
        <div className="cartao">
          <h2>{pagina.totalElementos} resultado(s)</h2>
          {pagina.conteudo.length === 0 ? (
            <Vazio>Nenhuma hospedagem encontrada com esses filtros.</Vazio>
          ) : (
            <div className="grade duas">
              {pagina.conteudo.map(h => (
                <div key={h.id} className="opcao-modal">
                  <h3>{h.nome}</h3>
                  <div className="rotulo-pequeno">{h.cidade} - {h.categoria} estrelas</div>
                  <p style={{ fontSize: 13, color: 'var(--cinza-600)' }}>{h.endereco}</p>
                  <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap', marginBottom: 10 }}>
                    {h.comodidades.map(c => <span key={c} className="selo cinza">{c}</span>)}
                  </div>
                  <div className="valor-grande">{moeda(h.precoDiaria)}</div>
                  <div className="rotulo-pequeno">por diaria</div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </>
  )
}
