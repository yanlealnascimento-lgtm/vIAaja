import { useEffect, useState } from 'react'
import { api, parametros } from '../api/client.js'
import Aviso from '../components/Aviso.jsx'
import Carregando from '../components/Carregando.jsx'
import Vazio from '../components/Vazio.jsx'
import { dataHora } from '../utils/formato.js'

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
        api.get('/admin/parceiros' + parametros({})),
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

  async function aprovar(id) {
    try {
      await api.patch(`/admin/parceiros/${id}/aprovar`)
      setMensagem('Parceiro aprovado.')
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  async function ocultar(id) {
    try {
      await api.patch(`/admin/avaliacoes/${id}/ocultar`)
      setMensagem('Avaliacao ocultada.')
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  if (!indicadores) return <Carregando />

  const abas = [
    ['indicadores', 'Indicadores'],
    ['parceiros', 'Parceiros'],
    ['usuarios', 'Usuarios'],
    ['avaliacoes', 'Moderacao'],
    ['ia', 'Chamadas de IA']
  ]

  return (
    <>
      <h1>Administracao</h1>

      <Aviso tipo="erro">{erro}</Aviso>
      <Aviso tipo="sucesso">{mensagem}</Aviso>

      <div className="linha-botoes" style={{ marginBottom: 16 }}>
        {abas.map(([chave, texto]) => (
          <button key={chave} className={`botao ${aba === chave ? '' : 'secundario'}`}
                  onClick={() => setAba(chave)}>{texto}</button>
        ))}
      </div>

      {aba === 'indicadores' && (
        <>
          <div className="grade tres">
            <Indicador rotulo="Usuarios" valor={indicadores.totalUsuarios} />
            <Indicador rotulo="Parceiros" valor={indicadores.totalParceiros} />
            <Indicador rotulo="Parceiros pendentes" valor={indicadores.parceirosPendentes} />
            <Indicador rotulo="Avaliacoes" valor={indicadores.totalAvaliacoes} />
            <Indicador rotulo="Chamadas de IA" valor={indicadores.chamadasIaTotais} />
            <Indicador rotulo="Chamadas nas 24h" valor={indicadores.chamadasIaUltimas24h} />
            <Indicador rotulo="Falhas de IA" valor={indicadores.chamadasIaComFalha} />
          </div>
          <div className="cartao">
            <h2>Chamadas de IA por funcionalidade</h2>
            {(indicadores.chamadasPorFuncionalidade || []).length === 0 ? (
              <Vazio>Nenhuma chamada registrada ainda.</Vazio>
            ) : (
              <table>
                <thead>
                  <tr><th>Funcionalidade</th><th>Chamadas</th><th>Tempo medio</th></tr>
                </thead>
                <tbody>
                  {indicadores.chamadasPorFuncionalidade.map(f => (
                    <tr key={f.funcionalidade}>
                      <td>{f.funcionalidade}</td>
                      <td>{f.chamadas}</td>
                      <td>{f.tempoMedioMs} ms</td>
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
          <h2>Parceiros fornecedores</h2>
          <table>
            <thead>
              <tr><th>Nome</th><th>Razao social</th><th>CNPJ</th><th>Tipo</th><th>Situacao</th><th></th></tr>
            </thead>
            <tbody>
              {parceiros.map(p => (
                <tr key={p.id}>
                  <td>{p.nome}</td>
                  <td>{p.razaoSocial}</td>
                  <td>{p.cnpj}</td>
                  <td><span className="selo cinza">{p.tipo.toLowerCase()}</span></td>
                  <td>
                    <span className={`selo ${p.aprovado ? 'verde' : 'amarelo'}`}>
                      {p.aprovado ? 'aprovado' : 'pendente'}
                    </span>
                  </td>
                  <td>
                    {!p.aprovado && (
                      <button className="botao pequeno" onClick={() => aprovar(p.id)}>Aprovar</button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {aba === 'usuarios' && (
        <div className="cartao">
          <h2>Usuarios da plataforma</h2>
          <table>
            <thead>
              <tr><th>Nome</th><th>E-mail</th><th>Perfil</th><th>Situacao</th></tr>
            </thead>
            <tbody>
              {usuarios.map(u => (
                <tr key={u.id}>
                  <td>{u.nome}</td>
                  <td>{u.email}</td>
                  <td><span className="selo cinza">{u.perfil.toLowerCase()}</span></td>
                  <td>
                    <span className={`selo ${u.ativo ? 'verde' : 'amarelo'}`}>
                      {u.ativo ? 'ativo' : 'nao confirmado'}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {aba === 'avaliacoes' && (
        <div className="cartao">
          <h2>Moderacao de avaliacoes</h2>
          {avaliacoes.length === 0 ? <Vazio>Nenhuma avaliacao publicada.</Vazio> : (
            <table>
              <thead>
                <tr><th>Autor</th><th>Destino</th><th>Nota</th><th>Comentario</th><th></th></tr>
              </thead>
              <tbody>
                {avaliacoes.map(a => (
                  <tr key={a.id}>
                    <td>{a.autor}</td>
                    <td>{a.destino}</td>
                    <td>{a.nota}</td>
                    <td>{a.comentario}</td>
                    <td>
                      <button className="botao perigo pequeno" onClick={() => ocultar(a.id)}>
                        Ocultar
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}

      {aba === 'ia' && (
        <div className="cartao">
          <h2>Ultimas chamadas a API de IA</h2>
          {chamadas.length === 0 ? <Vazio>Nenhuma chamada registrada.</Vazio> : (
            <table>
              <thead>
                <tr><th>Funcionalidade</th><th>Provedor</th><th>Tempo</th><th>Status</th><th>Quando</th></tr>
              </thead>
              <tbody>
                {chamadas.map(c => (
                  <tr key={c.id}>
                    <td>{c.funcionalidade}</td>
                    <td>{c.provedor}</td>
                    <td>{c.tempoRespostaMs} ms</td>
                    <td>
                      <span className={`selo ${c.status === 'SUCESSO' ? 'verde' : 'vermelho'}`}>
                        {c.status.toLowerCase()}
                      </span>
                    </td>
                    <td>{dataHora(c.data)}</td>
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

function Indicador({ rotulo, valor }) {
  return (
    <div className="cartao" style={{ marginBottom: 0 }}>
      <div className="rotulo-pequeno">{rotulo}</div>
      <div className="valor-grande">{valor}</div>
    </div>
  )
}
