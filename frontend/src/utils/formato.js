export function moeda(valor) {
  if (valor === null || valor === undefined || valor === '') return '-'
  return Number(valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
}

export function data(valor) {
  if (!valor) return '-'
  const texto = String(valor)
  const somenteData = /^\d{4}-\d{2}-\d{2}$/.test(texto)
  return new Date(somenteData ? `${texto}T12:00:00` : texto).toLocaleDateString('pt-BR')
}

export function dataExtensa(valor) {
  if (!valor) return '-'
  return new Date(`${String(valor).slice(0, 10)}T12:00:00`).toLocaleDateString('pt-BR', {
    weekday: 'short', day: '2-digit', month: 'short'
  })
}

export function dataHora(valor) {
  if (!valor) return '-'
  return new Date(valor).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' })
}

export function hora(valor) {
  if (!valor) return '-'
  return String(valor).slice(0, 5)
}

export function duracao(minutos) {
  if (minutos === null || minutos === undefined) return '-'
  const h = Math.floor(minutos / 60)
  const m = minutos % 60
  return `${h}h${String(m).padStart(2, '0')}`
}

export function plural(quantidade, singular, pluralTexto) {
  return `${quantidade} ${Number(quantidade) === 1 ? singular : pluralTexto}`
}

const ROTULOS = {
  modal: { AVIAO: 'Avião', ONIBUS: 'Ônibus', CARRO: 'Carro' },
  periodo: { MANHA: 'Manhã', TARDE: 'Tarde', NOITE: 'Noite' },
  convite: { ENVIADO: 'Aguardando resposta', ACEITO: 'Aceito', RECUSADO: 'Recusado', EXPIRADO: 'Expirado' },
  reuniao: { AGENDADA: 'Agendada', EM_ANDAMENTO: 'Em andamento', ENCERRADA: 'Encerrada' },
  statusItinerario: { RASCUNHO: 'Rascunho', CONFIRMADO: 'Confirmado' },
  origemItinerario: { MANUAL: 'Montado por você', SUGERIDO_IA: 'Sugerido pela IA', EDITADO_APOS_IA: 'IA, com ajustes seus' },
  notificacao: { CONTA: 'Conta', CONVITE: 'Convite', REUNIAO: 'Reunião', ITINERARIO: 'Itinerário', PRECO: 'Preço', SISTEMA: 'Aviso' },
  oferta: { PASSAGEM: 'Passagem', HOSPEDAGEM: 'Hospedagem', ATIVIDADE: 'Atividade' },
  perfil: { VIAJANTE: 'Viajante', PARCEIRO: 'Parceiro', SUPORTE: 'Suporte', ADMINISTRADOR: 'Administrador' },
  parceiro: { COMPANHIA_AEREA: 'Companhia aérea', VIACAO_RODOVIARIA: 'Viação rodoviária', HOTELARIA: 'Hotelaria', AGENCIA_ATIVIDADES: 'Atividades e passeios' },
  idioma: { 'pt-BR': 'Português', 'en-US': 'Inglês', 'es-ES': 'Espanhol', 'fr-FR': 'Francês' },
  funcionalidadeIA: {
    'assistente-linguagem-natural': 'Assistente',
    'geracao-itinerario': 'Roteiro',
    'resumo-avaliacoes': 'Resumo de avaliações',
    recomendacoes: 'Recomendações',
    'traducao-simultanea': 'Tradução'
  },
  statusIA: { SUCESSO: 'Sucesso', INDISPONIVEL: 'Indisponível', ERRO: 'Erro' }
}

export function rotulo(tipo, valor) {
  if (valor === null || valor === undefined) return '-'
  const mapa = ROTULOS[tipo] || {}
  const chave = String(valor)
  return mapa[chave] || mapa[chave.toUpperCase()] || chave
}

export function nomeModal(modal) {
  return rotulo('modal', modal)
}

export function hoje() {
  return new Date().toISOString().slice(0, 10)
}

export function daquiA(dias) {
  const d = new Date()
  d.setDate(d.getDate() + dias)
  return d.toISOString().slice(0, 10)
}
