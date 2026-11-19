export function moeda(valor) {
  if (valor === null || valor === undefined) return '-'
  return Number(valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
}

export function data(valor) {
  if (!valor) return '-'
  return new Date(valor).toLocaleDateString('pt-BR')
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
  return `${h}h ${String(m).padStart(2, '0')}min`
}

export function nomeModal(modal) {
  return { AVIAO: 'Aviao', ONIBUS: 'Onibus', CARRO: 'Carro' }[modal] || modal
}

export function hoje() {
  return new Date().toISOString().slice(0, 10)
}

export function daquiA(dias) {
  const d = new Date()
  d.setDate(d.getDate() + dias)
  return d.toISOString().slice(0, 10)
}
