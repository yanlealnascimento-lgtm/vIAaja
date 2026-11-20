import { useEffect, useState } from 'react'
import { api } from '../api/client.js'
import Aviso from '../components/Aviso.jsx'
import CabecalhoPagina from '../components/CabecalhoPagina.jsx'
import Carregando from '../components/Carregando.jsx'
import Vazio from '../components/Vazio.jsx'
import { dataHora, data, rotulo } from '../utils/formato.js'

const ABAS = [
  ['indicadores', 'Visão geral'],
  ['parceiros', 'Parceiros'],
  ['usuarios', 'Usuários'],
  ['avaliacoes', 'Avaliações'],
  ['ia', 'Uso da IA']
]

export default function PainelAdmin() {
  const [aba, setAba] = useState('indicadores')
  const [indicadores, setIndicadores] = useState(null)
  const [usuarios, setUsuarios] = useState([])
  const [parceiros, setParceiros] = useState([])
  const [avaliacoes, setAvaliacoes] = useState([])
  const [chamadas, setChamadas] = useState([])
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')

  useEffect(() => { carregar() }, [])

  async function carregar() {
    setErro('')
    try {
      const [i, u, p, a, c] = await Promise.all([
        api.get('/admin/indicadores'),
        api.get('/admin/usuarios'),
        api.get('/admin/parceiros'),
        api.get('/admin/avaliacoes'),
        api.get('/admin/chamadas-ia')
      ])
      setIndicadores(i)
      setUsuarios(u)
      setParceiros(p)
      setAvaliacoes(a)
      setChamadas(c)
    } catch (e) {
      setErro(e.message)
    }
  }

  async function aprovar(parceiro) {
    setMensagem('')
    try {
      await api.patch(`/admin/parceiros/${parceiro.id}/aprovar`)
      setMensagem(`${parceiro.nome} aprovado. As ofertas dele já podem aparecer nas buscas.`)
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  async function ocultar(avaliacao) {
    setMensagem('')
    try {
      await api.patch(`/admin/avaliacoes/${avaliacao.id}/ocultar`)
      setMensagem(`Avaliação de ${avaliacao.autor} ocultada.`)
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  if (!indicadores) return erro ? <Aviso tipo="erro">{erro}</Aviso> : <Carregando />

  return (
    <>
      <CabecalhoPagina titulo="Administração" descricao="Aprovação de parceiros, moderação de avaliações e acompanhamento do uso da IA." />

      <Aviso tipo="erro">{erro}</Aviso>
      <Aviso tipo="sucesso">{mensagem}</Aviso>

      <div className="abas" role="tablist">
        {ABAS.map(([chave, texto]) => (
          <button key={chave} role="tab" aria-selected={aba === chave} onClick={() => setAba(chave)}>
            {texto}
            {chave === 'parceiros' && indicadores.parceirosPendentes > 0 && ` (${indicadores.parceirosPendentes})`}
          </button>
        ))}
      </div>

      {aba === 'indicadores' && (
        <>
          <div className="indicadores">
            <Indicador rotulo="Usuários" valor={indicadores.totalUsuarios} />
            <Indicador rotulo="Parceiros" valor={indicadores.totalParceiros} />
            <Indicador rotulo="Aguardando aprovação" valor={indicadores.parceirosPendentes} />
            <Indicador rotulo="Avaliações publicadas" valor={indicadores.totalAvaliacoes} />
          </div>
          <div className="indicadores">
            <Indicador rotulo="Chamadas à IA" valor={indicadores.chamadasIaTotais} />
            <Indicador rotulo="Nas últimas 24 horas" valor={indicadores.chamadasIaUltimas24h} />
            <Indicador rotulo="Com falha" valor={indicadores.chamadasIaComFalha} />
          </div>
          <div className="cartao">
            <h2>Uso da IA por recurso</h2>
            {(indicadores.chamadasPorFuncionalidade || []).length === 0 ? (
              <Vazio>Nenhuma chamada registrada ainda.</Vazio>
            ) : (
              <table>
                <thead>
                  <tr><th>Recurso</th><th className="num">Chamadas</th><th className="num">Tempo médio</th></tr>
                </thead>
                <tbody>
                  {indicadores.chamadasPorFuncionalidade.map(f => (
                    <tr key={f.funcionalidade}>
                      <td>{rotulo('funcionalidadeIA', f.funcionalidade)}</td>
                      <td className="num">{f.chamadas}</td>
                      <td className="num">{Math.round(f.tempoMedioMs)} ms</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </>
      )}

      {aba === 'parceiros' && (
        <div className="cartao">
          <div className="tabela-rolagem">
            <table>
              <thead>
                <tr><th>Parceiro</th><th>CNPJ</th><th>Ramo</th><th>Situação</th><th></th></tr>
              </thead>
              <tbody>
                {parceiros.map(p => (
                  <tr key={p.id}>
                    <td>
                      <div style={{ fontWeight: 500 }}>{p.nome}</div>
                      <div className="rotulo-pequeno">{p.razaoSocial}</div>
                    </td>
                    <td className="num">{p.cnpj}</td>
                    <td>{rotulo('parceiro', p.tipo)}</td>
                    <td>
                      <span className={`selo ${p.aprovado ? 'verde' : 'amarelo'}`}>
                        {p.aprovado ? 'Aprovado' : 'Aguardando'}
                      </span>
                    </td>
                    <td style={{ textAlign: 'right' }}>
                      {!p.aprovado && <button className="botao pequeno" onClick={() => aprovar(p)}>Aprovar</button>}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {aba === 'usuarios' && (
        <div className="cartao">
          <div className="tabela-rolagem">
            <table>
              <thead>
                <tr><th>Nome</th><th>E-mail</th><th>Perfil</th><th>Conta</th></tr>
              </thead>
              <tbody>
                {usuarios.map(u => (
                  <tr key={u.id}>
                    <td>{u.nome}</td>
                    <td>{u.email}</td>
                    <td>{rotulo('perfil', u.perfil)}</td>
                    <td>
                      <span className={`selo ${u.ativo ? 'verde' : 'amarelo'}`}>
                        {u.ativo ? 'Confirmada' : 'E-mail não confirmado'}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {aba === 'avaliacoes' && (
        <div className="cartao">
          {avaliacoes.length === 0 ? <Vazio>Nenhuma avaliação publicada.</Vazio> : (
            <ul className="lista-limpa">
              {avaliacoes.map(a => (
                <li key={a.id}>
                  <div className="linha-titulo">
                    <div>
                      <strong>{a.autor}</strong>
                      <span className="rotulo-pequeno"> sobre {a.destino}, <span className="nota">{a.nota}</span> de 5, em {data(a.data)}</span>
                    </div>
                    <button className="botao perigo pequeno" onClick={() => ocultar(a)}>Ocultar</button>
                  </div>
                  <p style={{ margin: '4px 0 0' }}>{a.comentario}</p>
                </li>
              ))}
            </ul>
          )}
        </div>
      )}

      {aba === 'ia' && (
        <div className="cartao">
          <h2>Últimas chamadas</h2>
          {chamadas.length === 0 ? <Vazio>Nenhuma chamada registrada.</Vazio> : (
            <div className="tabela-rolagem">
              <table>
                <thead>
                  <tr><th>Recurso</th><th>Provedor</th><th className="num">Tempo</th><th>Resultado</th><th>Quando</th></tr>
                </thead>
                <tbody>
                  {chamadas.map(c => (
                    <tr key={c.id}>
                      <td>{rotulo('funcionalidadeIA', c.funcionalidade)}</td>
                      <td>{c.provedor === 'openai' ? 'API externa' : 'Simulador local'}</td>
                      <td className="num">{c.tempoRespostaMs} ms</td>
                      <td>
                        <span className={`selo ${c.status === 'SUCESSO' ? 'verde' : 'vermelho'}`}>
                          {rotulo('statusIA', c.status)}
                        </span>
                      </td>
                      <td className="num">{dataHora(c.data)}</td>
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

function Indicador({ rotulo: texto, valor }) {
  return (
    <div className="indicador">
      <div className="rotulo-pequeno">{texto}</div>
      <div className="valor-grande">{valor}</div>
    </div>
  )
}
