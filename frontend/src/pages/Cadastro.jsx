import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api } from '../api/client.js'
import Aviso from '../components/Aviso.jsx'

const VAZIO = {
  nome: '', email: '', senha: '', cpf: '', telefone: '',
  dataNascimento: '', cidadeAproximada: ''
}

export default function Cadastro() {
  const [dados, setDados] = useState(VAZIO)
  const [erro, setErro] = useState('')
  const [sucesso, setSucesso] = useState('')
  const [enviando, setEnviando] = useState(false)
  const navegar = useNavigate()

  function alterar(campo, valor) {
    setDados(anterior => ({ ...anterior, [campo]: valor }))
  }

  async function enviar(evento) {
    evento.preventDefault()
    setErro('')
    setSucesso('')
    setEnviando(true)
    try {
      await api.post('/auth/cadastro', dados)
      setSucesso('Conta criada. Confirme o e-mail pelo link enviado e depois entre com sua senha.')
      setTimeout(() => navegar('/entrar'), 3000)
    } catch (e) {
      const campos = e.corpo?.campos?.map(c => `${c.campo}: ${c.mensagem}`).join('; ')
      setErro(campos || e.message)
    } finally {
      setEnviando(false)
    }
  }

  return (
    <div className="tela-estreita">
      <div className="cartao" style={{ padding: 32 }}>
        <h1 style={{ marginBottom: 6 }}>Criar conta</h1>
        <p className="texto-suave" style={{ marginBottom: 24 }}>
          Para planejar viagens, salvar roteiros e conversar com outros viajantes. É preciso ter 18 anos ou mais.
        </p>
        <Aviso tipo="erro">{erro}</Aviso>
        <Aviso tipo="sucesso">{sucesso}</Aviso>
        <form onSubmit={enviar}>
          <div className="campo">
            <label htmlFor="nome">Nome completo</label>
            <input id="nome" autoComplete="name" value={dados.nome} required minLength={3}
                   onChange={e => alterar('nome', e.target.value)} />
          </div>
          <div className="campo">
            <label htmlFor="email">E-mail</label>
            <input id="email" type="email" autoComplete="email" value={dados.email} required
                   onChange={e => alterar('email', e.target.value)} />
          </div>
          <div className="campo">
            <label htmlFor="senha">Senha</label>
            <input id="senha" type="password" autoComplete="new-password" value={dados.senha} required minLength={8}
                   onChange={e => alterar('senha', e.target.value)} />
            <span className="ajuda">Mínimo de 8 caracteres.</span>
          </div>
          <div className="grade duas" style={{ gap: 18 }}>
            <div className="campo">
              <label htmlFor="cpf">CPF</label>
              <input id="cpf" inputMode="numeric" value={dados.cpf} required pattern="[0-9]{11}"
                     title="Digite os 11 números do CPF, sem pontos ou traço"
                     onChange={e => alterar('cpf', e.target.value.replace(/\D/g, ''))} />
              <span className="ajuda">Só os 11 números.</span>
            </div>
            <div className="campo">
              <label htmlFor="nascimento">Data de nascimento</label>
              <input id="nascimento" type="date" value={dados.dataNascimento} required
                     onChange={e => alterar('dataNascimento', e.target.value)} />
            </div>
            <div className="campo">
              <label htmlFor="telefone">Telefone</label>
              <input id="telefone" type="tel" autoComplete="tel" value={dados.telefone} required minLength={10}
                     onChange={e => alterar('telefone', e.target.value)} />
            </div>
            <div className="campo">
              <label htmlFor="cidade">Cidade onde mora</label>
              <input id="cidade" value={dados.cidadeAproximada}
                     onChange={e => alterar('cidadeAproximada', e.target.value)} />
            </div>
          </div>
          <button className="botao" type="submit" disabled={enviando} style={{ width: '100%', marginTop: 24 }}>
            {enviando ? 'Criando conta…' : 'Criar conta'}
          </button>
        </form>
        <p className="pequeno-texto" style={{ marginTop: 18, marginBottom: 0 }}>
          Já tem conta? <Link to="/entrar">Entrar</Link>
        </p>
      </div>
    </div>
  )
}
