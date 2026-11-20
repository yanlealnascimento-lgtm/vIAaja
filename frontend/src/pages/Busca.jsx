import { useState } from 'react'
import { api, parametros } from '../api/client.js'
import Aviso from '../components/Aviso.jsx'
import CabecalhoPagina from '../components/CabecalhoPagina.jsx'
import Vazio from '../components/Vazio.jsx'
import { moeda, dataHora, duracao, nomeModal, daquiA, plural } from '../utils/formato.js'

export default function Busca() {
  const [aba, setAba] = useState('passagens')

  return (
    <>
      <CabecalhoPagina
        titulo="Passagens e hotéis"
        descricao="Ofertas publicadas pelos parceiros, com filtros por preço, paradas e categoria."
      />
      <div className="abas" role="tablist">
        <button role="tab" aria-selected={aba === 'passagens'} onClick={() => setAba('passagens')}>
          Passagens
        </button>
        <button role="tab" aria-selected={aba === 'hospedagens'} onClick={() => setAba('hospedagens')}>
          Hospedagens
        </button>
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

  function alterar(campo, valor) {
    setForm(anterior => ({ ...anterior, [campo]: valor }))
  }

  async function buscar(evento) {
    evento.preventDefault()
    setErro('')
    setCarregando(true)
    try {
      setPagina(await api.get('/busca/passagens' + parametros(form)))
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
          <div className="grade quatro">
            <div className="campo">
              <label htmlFor="p-origem">Saindo de</label>
              <input id="p-origem" value={form.origem} required onChange={e => alterar('origem', e.target.value)} />
            </div>
            <div className="campo">
              <label htmlFor="p-destino">Indo para</label>
              <input id="p-destino" value={form.destino} required onChange={e => alterar('destino', e.target.value)} />
            </div>
            <div className="campo">
              <label htmlFor="p-data">Data</label>
              <input id="p-data" type="date" value={form.data} required onChange={e => alterar('data', e.target.value)} />
            </div>
            <div className="campo">
              <label htmlFor="p-passageiros">Passageiros</label>
              <input id="p-passageiros" type="number" min="1" max="9" value={form.passageiros}
                     onChange={e => alterar('passageiros', e.target.value)} />
            </div>
            <div className="campo">
              <label htmlFor="p-modal">Meio de transporte</label>
              <select id="p-modal" value={form.modal} onChange={e => alterar('modal', e.target.value)}>
                <option value="">Avião e ônibus</option>
                <option value="AVIAO">Só avião</option>
                <option value="ONIBUS">Só ônibus</option>
              </select>
            </div>
            <div className="campo">
              <label htmlFor="p-preco">Preço máximo</label>
              <input id="p-preco" type="number" min="0" step="10" placeholder="Sem limite" value={form.precoMaximo}
                     onChange={e => alterar('precoMaximo', e.target.value)} />
            </div>
            <div className="campo">
              <label htmlFor="p-paradas">Paradas, no máximo</label>
              <input id="p-paradas" type="number" min="0" max="5" placeholder="Qualquer" value={form.paradasMaximas}
                     onChange={e => alterar('paradasMaximas', e.target.value)} />
            </div>
            <div className="campo">
              <label htmlFor="p-ordem">Ordenar por</label>
              <select id="p-ordem" value={form.ordenarPor} onChange={e => alterar('ordenarPor', e.target.value)}>
                <option value="preco">Menor preço</option>
                <option value="duracao">Menor duração</option>
                <option value="partida">Horário de partida</option>
                <option value="paradas">Menos paradas</option>
              </select>
            </div>
          </div>
          <div className="linha-botoes">
            <button className="botao" type="submit" disabled={carregando}>
              {carregando ? 'Buscando…' : 'Buscar passagens'}
            </button>
          </div>
        </form>
      </div>

      <Aviso tipo="erro">{erro}</Aviso>

      {pagina && (
        <div className="cartao">
          <h2>{plural(pagina.totalElementos, 'passagem encontrada', 'passagens encontradas')}</h2>
          {pagina.conteudo.length === 0 ? (
            <Vazio>Nenhuma passagem com esses filtros. Tente outra data ou aumente o preço máximo.</Vazio>
          ) : (
            <div className="tabela-rolagem">
              <table>
                <thead>
                  <tr>
                    <th>Transporte</th>
                    <th>Empresa</th>
                    <th>Partida</th>
                    <th>Chegada</th>
                    <th className="num">Duração</th>
                    <th className="num">Paradas</th>
                    <th className="num">Preço</th>
                  </tr>
                </thead>
                <tbody>
                  {pagina.conteudo.map(p => (
                    <tr key={p.id}>
                      <td>{nomeModal(p.modal)}</td>
                      <td>{p.empresa}</td>
                      <td className="num">{dataHora(p.dataPartida)}</td>
                      <td className="num">{dataHora(p.dataChegada)}</td>
                      <td className="num">{duracao(p.duracaoMinutos)}</td>
                      <td className="num">{p.numeroParadas === 0 ? 'Direto' : p.numeroParadas}</td>
                      <td className="num"><strong>{moeda(p.preco)}</strong></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
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

  function alterar(campo, valor) {
    setForm(anterior => ({ ...anterior, [campo]: valor }))
  }

  async function buscar(evento) {
    evento.preventDefault()
    setErro('')
    setCarregando(true)
    try {
      setPagina(await api.get('/busca/hospedagens' + parametros(form)))
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
          <div className="grade quatro">
            <div className="campo">
              <label htmlFor="h-cidade">Cidade</label>
              <input id="h-cidade" value={form.cidade} required onChange={e => alterar('cidade', e.target.value)} />
            </div>
            <div className="campo">
              <label htmlFor="h-hospedes">Hóspedes</label>
              <input id="h-hospedes" type="number" min="1" max="9" value={form.hospedes}
                     onChange={e => alterar('hospedes', e.target.value)} />
            </div>
            <div className="campo">
              <label htmlFor="h-preco">Diária de até</label>
              <input id="h-preco" type="number" min="0" step="10" placeholder="Sem limite" value={form.precoMaximo}
                     onChange={e => alterar('precoMaximo', e.target.value)} />
            </div>
            <div className="campo">
              <label htmlFor="h-categoria">Categoria</label>
              <select id="h-categoria" value={form.categoriaMinima} onChange={e => alterar('categoriaMinima', e.target.value)}>
                <option value="">Qualquer</option>
                <option value="3">3 estrelas ou mais</option>
                <option value="4">4 estrelas ou mais</option>
                <option value="5">5 estrelas</option>
              </select>
            </div>
            <div className="campo">
              <label htmlFor="h-ordem">Ordenar por</label>
              <select id="h-ordem" value={form.ordenarPor} onChange={e => alterar('ordenarPor', e.target.value)}>
                <option value="preco">Menor diária</option>
                <option value="categoria">Mais estrelas</option>
              </select>
            </div>
          </div>
          <div className="linha-botoes">
            <button className="botao" type="submit" disabled={carregando}>
              {carregando ? 'Buscando…' : 'Buscar hospedagens'}
            </button>
          </div>
        </form>
      </div>

      <Aviso tipo="erro">{erro}</Aviso>

      {pagina && (
        <>
          <h2>{plural(pagina.totalElementos, 'hospedagem encontrada', 'hospedagens encontradas')}</h2>
          {pagina.conteudo.length === 0 ? (
            <Vazio>Nenhuma hospedagem com esses filtros. Tente outra categoria ou aumente a diária.</Vazio>
          ) : (
            <div className="grade duas">
              {pagina.conteudo.map(h => (
                <article key={h.id} className="cartao hospedagem">
                  <div className="linha-titulo">
                    <h3>{h.nome}</h3>
                    <span className="selo">{h.categoria} estrelas</span>
                  </div>
                  <p className="pequeno-texto texto-suave">{h.endereco}</p>
                  <div className="alternaveis" style={{ marginBottom: 16 }}>
                    {h.comodidades.map(c => <span key={c} className="selo cinza">{c}</span>)}
                  </div>
                  <div className="linha-titulo">
                    <span className="preco-diaria">{moeda(h.precoDiaria)}</span>
                    <span className="rotulo-pequeno">por diária, {plural(h.disponibilidade, 'quarto livre', 'quartos livres')}</span>
                  </div>
                </article>
              ))}
            </div>
          )}
        </>
      )}
    </>
  )
}
