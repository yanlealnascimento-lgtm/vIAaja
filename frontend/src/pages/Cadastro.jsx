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
      setSucesso('Conta criada. Veja o link de confirmacao no console do back-end e depois entre.')
      setTimeout(() => navegar('/entrar'), 2500)
    } catch (e) {
      const campos = e.corpo?.campos?.map(c => `${c.campo}: ${c.mensagem}`).join(' | ')
      setErro(campos || e.message)
    } finally {
      setEnviando(false)
    }
  }

  return (
    <div className="centro-tela">
      <div className="cartao">
        <h1>Criar conta de viajante</h1>
        <Aviso tipo="erro">{erro}</Aviso>
        <Aviso tipo="sucesso">{sucesso}</Aviso>
        <form onSubmit={enviar}>
          <div className="campo">
            <label htmlFor="nome">Nome completo</label>
            <input id="nome" value={dados.nome} required minLength={3}
                   onChange={e => alterar('nome', e.target.value)} />
          </div>
          <div className="campo">
            <label htmlFor="email">E-mail</label>
            <input id="email" type="email" value={dados.email} required
                   onChange={e => alterar('email', e.target.value)} />
          </div>
          <div className="campo">
            <label htmlFor="senha">Senha (minimo 8 caracteres)</label>
            <input id="senha" type="password" value={dados.senha} required minLength={8}
                   onChange={e => alterar('senha', e.target.value)} />
          </div>
          <div className="campo">
            <label htmlFor="cpf">CPF (somente numeros)</label>
            <input id="cpf" value={dados.cpf} required pattern="[0-9]{11}"
                   onChange={e => alterar('cpf', e.target.value)} />
          </div>
          <div className="campo">
            <label htmlFor="telefone">Telefone</label>
            <input id="telefone" value={dados.telefone} required minLength={10}
                   onChange={e => alterar('telefone', e.target.value)} />
          </div>
          <div className="campo">
            <label htmlFor="nascimento">Data de nascimento</label>
            <input id="nascimento" type="date" value={dados.dataNascimento} required
                   onChange={e => alterar('dataNascimento', e.target.value)} />
          </div>
          <div className="campo">
            <label htmlFor="cidade">Cidade aproximada</label>
            <input id="cidade" value={dados.cidadeAproximada}
                   onChange={e => alterar('cidadeAproximada', e.target.value)} />
          </div>
          <button className="botao" type="submit" disabled={enviando}>
            {enviando ? 'Enviando...' : 'Criar conta'}
          </button>
        </form>
        <p style={{ marginTop: 14, fontSize: 14 }}>
          Ja tem conta? <Link to="/entrar">Entrar</Link>
        </p>
      </div>
    </div>
  )
}
