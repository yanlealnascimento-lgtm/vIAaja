import { useState } from 'react'
import { api } from '../api/client.js'
import Aviso from '../components/Aviso.jsx'
import CabecalhoPagina from '../components/CabecalhoPagina.jsx'
import { data, moeda, nomeModal, daquiA, plural } from '../utils/formato.js'

export default function Comparador() {
  const [form, setForm] = useState({
    origem: 'Salvador',
    destino: 'Porto Seguro',
    data: daquiA(35),
    passageiros: 2
  })
  const [resultado, setResultado] = useState(null)
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(false)

  function alterar(campo, valor) {
    setForm(anterior => ({ ...anterior, [campo]: valor }))
  }

  async function comparar(evento) {
    evento.preventDefault()
    setErro('')
    setCarregando(true)
    setResultado(null)
    try {
      const dados = await api.post('/comparacoes', {
        ...form,
        passageiros: Number(form.passageiros)
      })
      setResultado(dados)
    } catch (e) {
      setErro(e.message)
    } finally {
      setCarregando(false)
    }
  }

  const opcoes = resultado
    ? [...resultado.opcoes].sort((a, b) => {
        if (a.disponivel !== b.disponivel) return a.disponivel ? -1 : 1
        return Number(a.custoTotal) - Number(b.custoTotal)
      })
    : []

  return (
    <>
      <CabecalhoPagina
        titulo="Quanto custa chegar lá"
        descricao="Informe a rota e veja, lado a lado, o custo total de ir de avião, de ônibus ou com o seu carro."
      />

      <div className="cartao">
        <form onSubmit={comparar}>
          <div className="grade quatro">
            <div className="campo">
              <label htmlFor="origem">Saindo de</label>
              <input id="origem" value={form.origem} required
                     onChange={e => alterar('origem', e.target.value)} />
            </div>
            <div className="campo">
              <label htmlFor="destino">Indo para</label>
              <input id="destino" value={form.destino} required
                     onChange={e => alterar('destino', e.target.value)} />
            </div>
            <div className="campo">
              <label htmlFor="data">Data da ida</label>
              <input id="data" type="date" value={form.data} required
                     onChange={e => alterar('data', e.target.value)} />
            </div>
            <div className="campo">
              <label htmlFor="passageiros">Passageiros</label>
              <input id="passageiros" type="number" min="1" max="9" value={form.passageiros} required
                     onChange={e => alterar('passageiros', e.target.value)} />
            </div>
          </div>
          <div className="linha-botoes">
            <button className="botao" type="submit" disabled={carregando}>
              {carregando ? 'Comparando…' : 'Comparar custos'}
            </button>
          </div>
        </form>
      </div>

      <Aviso tipo="erro">{erro}</Aviso>

      {resultado && (
        <section className="quadro" aria-label="Resultado da comparação">
          <div className="quadro-rota">
            <span className="cidade">{resultado.origem}</span>
            <span className="trajeto" aria-hidden="true" />
            <span className="cidade">{resultado.destino}</span>
          </div>
          <div className="quadro-info">
            {plural(resultado.passageiros, 'passageiro', 'passageiros')}, ida em {data(resultado.data)}.
            Valores totais para o grupo.
          </div>
          {opcoes.map(opcao => (
            <div
              key={opcao.modal}
              className={`quadro-linha${opcao.maisBarata ? ' mais-barata' : ''}${!opcao.disponivel ? ' indisponivel' : ''}`}
            >
              <div className="modal">
                {nomeModal(opcao.modal)}
                {opcao.maisBarata && <span className="marca-barata">Mais barato</span>}
              </div>
              <div className="detalhe">
                {opcao.disponivel ? opcao.detalhe : 'Nenhuma oferta encontrada para essa data.'}
                {opcao.estimado && <span className="selo">Estimativa</span>}
              </div>
              <div className="preco">{opcao.disponivel ? moeda(opcao.custoTotal) : '-'}</div>
            </div>
          ))}
        </section>
      )}

      {resultado?.opcoes.some(o => o.estimado) && (
        <p className="pequeno-texto texto-suave">
          O valor de carro é uma estimativa de combustível e pedágio, calculada pela distância de
          referência até o destino e por preços médios. Os demais valores vêm das ofertas cadastradas
          pelos parceiros.
        </p>
      )}
    </>
  )
}
