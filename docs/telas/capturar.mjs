import { createRequire } from 'module'
import { mkdirSync } from 'fs'
import { dirname, join } from 'path'
import { fileURLToPath } from 'url'

const { chromium } = createRequire(import.meta.url)('playwright')

const AQUI = dirname(fileURLToPath(import.meta.url))
const BASE = process.env.VIAAJA_URL || 'http://localhost:4173'
mkdirSync(AQUI, { recursive: true })

const destinos = [
  { id: 1, cidade: 'Porto Seguro', estado: 'BA', pais: 'Brasil', descricao: 'Praias, centro histórico e vida noturna no extremo sul da Bahia.', distanciaReferenciaKm: 707 },
  { id: 2, cidade: 'Lençóis', estado: 'BA', pais: 'Brasil', descricao: 'Porta de entrada da Chapada Diamantina, com trilhas, cachoeiras e grutas.', distanciaReferenciaKm: 410 },
  { id: 3, cidade: 'Ilhéus', estado: 'BA', pais: 'Brasil', descricao: 'Litoral sul da Bahia, com praias extensas e roteiro cultural ligado a Jorge Amado.', distanciaReferenciaKm: 462 },
  { id: 4, cidade: 'Recife', estado: 'PE', pais: 'Brasil', descricao: 'Capital pernambucana, com Recife Antigo, praias urbanas e forte cena cultural.', distanciaReferenciaKm: 839 }
]

const atividades = [
  { id: 11, nome: 'Passeio de escuna a Recife de Fora', categoria: 'passeio', custoMedio: 120.0 },
  { id: 12, nome: 'Centro Histórico de Porto Seguro', categoria: 'cultura', custoMedio: 0.0 },
  { id: 13, nome: 'Dia de praia em Taperapuan', categoria: 'praia', custoMedio: 80.0 },
  { id: 14, nome: 'Jantar na Passarela do Álcool', categoria: 'gastronomia', custoMedio: 95.0 }
]

const passagens = [
  { id: 101, modal: 'AVIAO', empresa: 'Azul', origem: 'Salvador', destino: 'Porto Seguro', dataPartida: '2026-12-05T06:40:00', dataChegada: '2026-12-05T07:45:00', duracaoMinutos: 65, numeroParadas: 0, assentosDisponiveis: 23, preco: 389.9 },
  { id: 102, modal: 'AVIAO', empresa: 'Gol', origem: 'Salvador', destino: 'Porto Seguro', dataPartida: '2026-12-05T11:15:00', dataChegada: '2026-12-05T12:25:00', duracaoMinutos: 70, numeroParadas: 0, assentosDisponiveis: 9, preco: 432.5 },
  { id: 103, modal: 'AVIAO', empresa: 'Latam', origem: 'Salvador', destino: 'Porto Seguro', dataPartida: '2026-12-05T17:50:00', dataChegada: '2026-12-05T20:10:00', duracaoMinutos: 140, numeroParadas: 1, assentosDisponiveis: 41, preco: 356.0 },
  { id: 104, modal: 'ONIBUS', empresa: 'Águia Branca', origem: 'Salvador', destino: 'Porto Seguro', dataPartida: '2026-12-04T21:30:00', dataChegada: '2026-12-05T08:10:00', duracaoMinutos: 640, numeroParadas: 3, assentosDisponiveis: 18, preco: 189.9 },
  { id: 105, modal: 'ONIBUS', empresa: 'Rota Transportes', origem: 'Salvador', destino: 'Porto Seguro', dataPartida: '2026-12-05T07:00:00', dataChegada: '2026-12-05T18:20:00', duracaoMinutos: 680, numeroParadas: 4, assentosDisponiveis: 27, preco: 174.5 }
]

const hospedagens = [
  { id: 201, nome: 'Pousada Arraial Vista Mar', categoria: 4, cidade: 'Porto Seguro', endereco: 'Rua do Mucuge, 410', comodidades: ['Wi-Fi', 'Café da manhã', 'Piscina', 'Ar-condicionado'], disponibilidade: 6, precoDiaria: 285.0, quartos: [] },
  { id: 202, nome: 'Hotel Costa do Descobrimento', categoria: 3, cidade: 'Porto Seguro', endereco: 'Av. Beira Mar, 1200', comodidades: ['Wi-Fi', 'Estacionamento', 'Café da manhã'], disponibilidade: 14, precoDiaria: 198.0, quartos: [] },
  { id: 203, nome: 'Resort Taperapuan', categoria: 5, cidade: 'Porto Seguro', endereco: 'Praia de Taperapuan, s/n', comodidades: ['Wi-Fi', 'Piscina', 'Academia', 'Pensão completa'], disponibilidade: 3, precoDiaria: 640.0, quartos: [] }
]

const itinerarioDetalhe = {
  id: 1,
  titulo: 'Cinco dias em Porto Seguro',
  destino: 'Porto Seguro/BA',
  destinoId: 1,
  dataInicio: '2026-12-05',
  dataFim: '2026-12-09',
  orcamento: 2500.0,
  custoTotal: 1485.0,
  origem: 'SUGERIDO_IA',
  status: 'CONFIRMADO',
  interesses: ['praia', 'gastronomia', 'cultura'],
  dias: [
    {
      id: 1, data: '2026-12-05', ordem: 1, custoDoDia: 295.0,
      itens: [
        { id: 1, titulo: 'Chegada e acomodação', descricao: 'Transfer do aeroporto até a pousada no centro.', periodo: 'MANHA', horaInicio: '09:00:00', horaFim: '11:00:00', custoEstimado: 80.0, ordem: 1 },
        { id: 2, titulo: 'Centro Histórico de Porto Seguro', descricao: 'Caminhada pela Cidade Alta, igrejas e mirante.', periodo: 'TARDE', horaInicio: '14:00:00', horaFim: '17:00:00', custoEstimado: 0.0, ordem: 2 },
        { id: 3, titulo: 'Jantar na Passarela do Álcool', descricao: 'Comida regional e artesanato na feira.', periodo: 'NOITE', horaInicio: '19:30:00', horaFim: '22:00:00', custoEstimado: 215.0, ordem: 3 }
      ]
    },
    {
      id: 2, data: '2026-12-06', ordem: 2, custoDoDia: 420.0,
      itens: [
        { id: 4, titulo: 'Passeio de escuna a Recife de Fora', descricao: 'Saída do porto as 9h, com parada para mergulho livre.', periodo: 'MANHA', horaInicio: '09:00:00', horaFim: '15:00:00', custoEstimado: 240.0, ordem: 1 },
        { id: 5, titulo: 'Dia de praia em Taperapuan', descricao: 'Barracas de praia e pôr do sol.', periodo: 'TARDE', horaInicio: '16:00:00', horaFim: '18:30:00', custoEstimado: 180.0, ordem: 2 }
      ]
    },
    {
      id: 3, data: '2026-12-07', ordem: 3, custoDoDia: 310.0,
      itens: [
        { id: 6, titulo: 'Travessia para Arraial d’Ajuda', descricao: 'Balsa e tarde livre na Praia do Mucugê.', periodo: 'MANHA', horaInicio: '10:00:00', horaFim: '17:00:00', custoEstimado: 310.0, ordem: 1 }
      ]
    }
  ]
}

const fixtures = {
  'GET /usuarios/eu': { id: 5, nome: 'Yan Leal Nascimento', email: 'yan@viaaja.com', telefone: '(71) 99999-0001', perfil: 'VIAJANTE', ativo: true, cidadeAproximada: 'Salvador', visivelComoProximo: true, quemPodeConvidar: 'MESMO_DESTINO' },
  'GET /notificacoes': [
    { id: 1, tipo: 'CONVITE', mensagem: 'Erick de Souza Pereira convidou voce para planejar uma viagem a Porto Seguro.', lida: false, data: '2026-11-10T18:24:00' },
    { id: 2, tipo: 'ITINERARIO', mensagem: 'Seu itinerario Cinco dias em Porto Seguro foi confirmado.', lida: true, data: '2026-11-09T10:02:00' }
  ],
  'GET /destinos': destinos,
  'GET /destinos/1': destinos[0],
  'GET /destinos/1/atividades': atividades,
  'GET /avaliacoes/destino/1': [
    { id: 1, nota: 5, comentario: 'A escuna para Recife de Fora valeu cada centavo, mas va no primeiro horario.', autor: 'Davi de Sa Luquini', destino: 'Porto Seguro/BA', data: '2026-10-28T19:10:00' },
    { id: 2, nota: 4, comentario: 'Centro histórico muito bonito. A subida é cansativa no sol do meio-dia.', autor: 'Kauan Aparecido Costa Casemiro', destino: 'Porto Seguro/BA', data: '2026-10-21T08:45:00' },
    { id: 3, nota: 3, comentario: 'As barracas da praia de Taperapuan estavam cheias e o serviço demorou.', autor: 'João Henrique Santos Nascimento Souza', destino: 'Porto Seguro/BA', data: '2026-10-14T16:30:00' }
  ],
  'GET /avaliacoes/destino/1/media': { destinoId: 1, media: 4.0 },
  'GET /avaliacoes/destino/1/resumo': {
    pontosPositivos: ['Passeios de barco bem avaliados', 'Centro histórico preservado', 'Boa oferta de restaurantes'],
    pontosNegativos: ['Praias cheias na alta temporada', 'Transporte interno caro'],
    totalAvaliacoesConsideradas: 3,
    geradoEm: '2026-11-11T14:05:00'
  },
  'GET /ia/status': { provedor: 'openai' },
  'GET /ia/recomendacoes': {
    histórico: ['Porto Seguro/BA', 'Lençóis/BA'],
    recomendacoes: ['Ilhéus/BA', 'Recife/PE', 'Maragogi/AL']
  },
  'GET /itinerarios': [
    { id: 1, titulo: 'Cinco dias em Porto Seguro', destino: 'Porto Seguro/BA', destinoId: 1, dataInicio: '2026-12-05', dataFim: '2026-12-09', orcamento: 2500.0, custoTotal: 1485.0, origem: 'SUGERIDO_IA', status: 'CONFIRMADO', interesses: ['praia', 'gastronomia'], dias: [] },
    { id: 2, titulo: 'Fim de semana na Chapada', destino: 'Lençóis/BA', destinoId: 2, dataInicio: '2027-01-15', dataFim: '2027-01-18', orcamento: 1200.0, custoTotal: 760.0, origem: 'EDITADO_APOS_IA', status: 'RASCUNHO', interesses: ['trilha', 'natureza'], dias: [] }
  ],
  'GET /itinerarios/1': itinerarioDetalhe,
  'GET /social/viajantes': [
    { id: 6, nome: 'Davi de Sa Luquini', cidadeAproximada: 'Salvador' },
    { id: 7, nome: 'Erick de Souza Pereira', cidadeAproximada: 'Lauro de Freitas' },
    { id: 8, nome: 'João Henrique Santos Nascimento Souza', cidadeAproximada: 'Salvador' }
  ],
  'GET /social/convites/recebidos': [
    { id: 31, status: 'ENVIADO', mensagem: 'Vamos dividir a hospedagem em Porto Seguro?', remetente: 'Erick de Souza Pereira', destinatario: 'Yan Leal Nascimento', destino: 'Porto Seguro/BA', dataEnvio: '2026-11-10T18:24:00' }
  ],
  'GET /social/convites/enviados': [
    { id: 32, status: 'ACEITO', mensagem: 'Topa planejar a Chapada junto?', remetente: 'Yan Leal Nascimento', destinatario: 'Kauan Aparecido Costa Casemiro', destino: 'Lençóis/BA', dataEnvio: '2026-11-08T09:12:00' },
    { id: 33, status: 'RECUSADO', mensagem: 'Vai em dezembro para Ilhéus?', remetente: 'Yan Leal Nascimento', destinatario: 'Davi de Sa Luquini', destino: 'Ilhéus/BA', dataEnvio: '2026-11-05T21:40:00' }
  ],
  'GET /social/reunioes': [
    { id: 41, salaId: 'sala-chapada-2027', status: 'AGENDADA', traducaoAtiva: true, idiomas: ['pt-BR', 'en-US'], dataHora: '2026-11-11T20:00:00', comQuem: 'Kauan Aparecido Costa Casemiro' }
  ],
  'GET /parceiro/ofertas': [
    { id: 101, tipo: 'PASSAGEM', titulo: 'Azul 6h40 Salvador para Porto Seguro', destino: 'Porto Seguro/BA', preco: 389.9, disponibilidade: 23, ativa: true },
    { id: 103, tipo: 'PASSAGEM', titulo: 'Latam 17h50 Salvador para Porto Seguro', destino: 'Porto Seguro/BA', preco: 356.0, disponibilidade: 41, ativa: true },
    { id: 201, tipo: 'HOSPEDAGEM', titulo: 'Pousada Arraial Vista Mar', destino: 'Porto Seguro/BA', preco: 285.0, disponibilidade: 6, ativa: true },
    { id: 203, tipo: 'HOSPEDAGEM', titulo: 'Resort Taperapuan', destino: 'Porto Seguro/BA', preco: 640.0, disponibilidade: 0, ativa: false }
  ],
  'GET /admin/indicadores': {
    totalUsuarios: 10, totalParceiros: 3, parceirosPendentes: 1, totalAvaliacoes: 12,
    chamadasIaTotais: 47, chamadasIaUltimas24h: 9, chamadasIaComFalha: 2,
    chamadasPorFuncionalidade: [
      { funcionalidade: 'geracao-itinerario', chamadas: 21, tempoMedioMs: 842 },
      { funcionalidade: 'resumo-avaliacoes', chamadas: 14, tempoMedioMs: 515 },
      { funcionalidade: 'assistente-linguagem-natural', chamadas: 8, tempoMedioMs: 377 },
      { funcionalidade: 'traducao-simultanea', chamadas: 4, tempoMedioMs: 268 }
    ]
  },
  'GET /admin/usuarios': [
    { id: 5, nome: 'Yan Leal Nascimento', email: 'yan@viaaja.com', telefone: '(71) 99999-0001', perfil: 'VIAJANTE', ativo: true },
    { id: 6, nome: 'Davi de Sa Luquini', email: 'davi@viaaja.com', telefone: '(71) 99999-0002', perfil: 'VIAJANTE', ativo: true },
    { id: 7, nome: 'Erick de Souza Pereira', email: 'erick@viaaja.com', telefone: '(71) 99999-0003', perfil: 'VIAJANTE', ativo: true },
    { id: 9, nome: 'Transportes Aéreos Bahia', email: 'parceiro.aereo@viaaja.com', telefone: '(71) 3333-1000', perfil: 'PARCEIRO', ativo: true },
    { id: 12, nome: 'Suporte vIAaja', email: 'suporte@viaaja.com', telefone: '(71) 3333-2000', perfil: 'SUPORTE', ativo: true }
  ],
  'GET /admin/parceiros': [
    { id: 9, nome: 'Transportes Aéreos Bahia', razaoSocial: 'Transportes Aéreos Bahia LTDA', cnpj: '12.345.678/0001-90', tipo: 'COMPANHIA_AEREA', aprovado: true },
    { id: 10, nome: 'Rodoviária Litoral Sul', razaoSocial: 'Litoral Sul Viação SA', cnpj: '23.456.789/0001-01', tipo: 'VIACAO_RODOVIARIA', aprovado: true },
    { id: 11, nome: 'Rede Pousadas do Descobrimento', razaoSocial: 'Descobrimento Hoteis ME', cnpj: '34.567.890/0001-12', tipo: 'HOTELARIA', aprovado: false }
  ],
  'GET /admin/avaliacoes': [
    { id: 1, nota: 5, comentario: 'A escuna para Recife de Fora valeu cada centavo.', autor: 'Davi de Sa Luquini', destino: 'Porto Seguro/BA', data: '2026-10-28T19:10:00' },
    { id: 3, nota: 3, comentario: 'As barracas da praia estavam cheias e o serviço demorou.', autor: 'João Henrique Santos Nascimento Souza', destino: 'Porto Seguro/BA', data: '2026-10-14T16:30:00' }
  ],
  'GET /admin/chamadas-ia': [
    { id: 47, funcionalidade: 'geracao-itinerario', provedor: 'openai', tempoRespostaMs: 812, status: 'SUCESSO', data: '2026-11-11T14:05:00' },
    { id: 46, funcionalidade: 'resumo-avaliacoes', provedor: 'openai', tempoRespostaMs: 498, status: 'SUCESSO', data: '2026-11-11T13:42:00' },
    { id: 45, funcionalidade: 'geracao-itinerario', provedor: 'openai', tempoRespostaMs: 1503, status: 'INDISPONIVEL', data: '2026-11-11T11:20:00' },
    { id: 44, funcionalidade: 'traducao-simultanea', provedor: 'openai', tempoRespostaMs: 254, status: 'SUCESSO', data: '2026-11-10T20:15:00' }
  ],
  'POST /comparacoes': {
    id: 1, origem: 'Salvador', destino: 'Porto Seguro', data: '2026-12-05', passageiros: 2,
    consultadoEm: '2026-11-12T09:30:00', modalMaisBarato: 'ONIBUS',
    opcoes: [
      { modal: 'AVIAO', custoTotal: 712.0, estimado: false, disponivel: true, detalhe: 'Latam, 1 parada, 2h20 de voo', maisBarata: false },
      { modal: 'ONIBUS', custoTotal: 349.0, estimado: false, disponivel: true, detalhe: 'Rota Transportes, 11h20 de viagem', maisBarata: true },
      { modal: 'CARRO', custoTotal: 918.47, estimado: true, disponivel: true, detalhe: 'Ida e volta, 1414 km: combustível R$ 784,14 e pedágio R$ 134,33. Valor por veículo, não muda com o número de passageiros', maisBarata: false }
    ]
  },
  'POST /ia/interpretar': {
    origem: 'Salvador', destino: 'Porto Seguro', dataInicio: '2026-12-01', dataFim: '2026-12-05',
    orcamento: 2500.0, passageiros: 2, interesses: ['praia', 'gastronomia'],
    interpretacao: 'Viagem de cinco dias de Salvador para Porto Seguro em dezembro, para duas pessoas, com orçamento de até R$ 2.500 e foco em praia e gastronomia.'
  },
  'POST /itinerarios/sugestoes': {
    titulo: 'Quatro dias em Porto Seguro', destino: 'Porto Seguro', custoEstimadoTotal: 1180.0,
    observacao: 'Este roteiro é uma sugestão. Confirme preços e horários antes de viajar.',
    dias: [
      { ordem: 1, data: '2026-12-29', atividades: [
        { periodo: 'MANHA', horaInicio: '09:00', horaFim: '12:00', titulo: 'Centro Histórico de Porto Seguro', descricao: 'Caminhada pela Cidade Alta, igrejas coloniais e mirante.', custoEstimado: 0 },
        { periodo: 'TARDE', horaInicio: '14:30', horaFim: '18:00', titulo: 'Praia de Taperapuan', descricao: 'Barracas com estrutura completa e pôr do sol na orla.', custoEstimado: 80 },
        { periodo: 'NOITE', horaInicio: '20:00', horaFim: '22:30', titulo: 'Passarela do Descobrimento', descricao: 'Jantar com culinária baiana e artesanato local.', custoEstimado: 120 } ] },
      { ordem: 2, data: '2026-12-30', atividades: [
        { periodo: 'MANHA', horaInicio: '08:30', horaFim: '13:00', titulo: 'Passeio de escuna a Recife de Fora', descricao: 'Piscinas naturais com mergulho livre na maré baixa.', custoEstimado: 160 },
        { periodo: 'NOITE', horaInicio: '19:30', horaFim: '22:00', titulo: 'Jantar em Arraial d’Ajuda', descricao: 'Travessia de balsa e restaurantes na Rua do Mucugê.', custoEstimado: 140 } ] },
      { ordem: 3, data: '2026-12-31', atividades: [
        { periodo: 'MANHA', horaInicio: '10:00', horaFim: '13:00', titulo: 'Praia do Espelho', descricao: 'Falésias coloridas e mar calmo, cerca de uma hora de carro.', custoEstimado: 200 },
        { periodo: 'NOITE', horaInicio: '22:00', horaFim: '01:00', titulo: 'Réveillon na orla', descricao: 'Queima de fogos na Passarela, chegue cedo para garantir lugar.', custoEstimado: 0 } ] },
      { ordem: 4, data: '2027-01-01', atividades: [
        { periodo: 'TARDE', horaInicio: '13:00', horaFim: '17:00', titulo: 'Praia de Taperapuan', descricao: 'Dia livre para descansar antes da volta.', custoEstimado: 80 } ] }
    ]
  },
  'POST /social/reunioes/sala-chapada-2027/entrar': { id: 41, salaId: 'sala-chapada-2027', status: 'EM_ANDAMENTO', traducaoAtiva: true, idiomas: ['pt-BR', 'en-US'], dataHora: '2026-11-11T20:00:00', comQuem: 'Kauan Aparecido Costa Casemiro' },
  'POST /social/reunioes/sala-chapada-2027/traducao': { original: 'Que tal a gente ficar num hotel perto do Recife Antigo e jantar fora na virada?', traducao: "How about we stay at a hotel near Old Recife and dine out on New Year's Eve?", aviso: 'A tradução é um apoio e pode conter imprecisões' },
  'POST /auth/login': { token: 'token-de-demonstracao', tipo: 'Bearer', expiraEmSegundos: 86400, usuarioId: 5, nome: 'Yan Leal Nascimento', perfil: 'VIAJANTE' }
}

function responder(caminho, metodo) {
  const semQuery = caminho.split('?')[0]
  const chave = `${metodo} ${semQuery}`
  if (chave in fixtures) return fixtures[chave]

  if (metodo === 'GET' && semQuery === '/busca/passagens') {
    return { conteudo: passagens, pagina: 0, tamanho: 20, totalElementos: passagens.length, totalPaginas: 1, ultima: true }
  }
  if (metodo === 'GET' && semQuery === '/busca/hospedagens') {
    return { conteudo: hospedagens, pagina: 0, tamanho: 20, totalElementos: hospedagens.length, totalPaginas: 1, ultima: true }
  }
  return null
}

const telas = [
  { arquivo: '01-entrar.png', rota: '/entrar', autenticado: false },
  { arquivo: '02-cadastro.png', rota: '/cadastro', autenticado: false },
  { arquivo: '03-comparador.png', rota: '/', acao: 'comparar' },
  { arquivo: '04-busca-passagens.png', rota: '/busca', acao: 'buscar' },
  { arquivo: '05-assistente.png', rota: '/assistente', acao: 'interpretar' },
  { arquivo: '06-itinerarios.png', rota: '/itinerarios' },
  { arquivo: '07-itinerario-detalhe.png', rota: '/itinerarios/1' },
  { arquivo: '08-destinos.png', rota: '/destinos' },
  { arquivo: '09-destino-detalhe.png', rota: '/destinos/1' },
  { arquivo: '10-social.png', rota: '/social', acao: 'procurar' },
  { arquivo: '11-perfil.png', rota: '/perfil' },
  { arquivo: '12-painel-parceiro.png', rota: '/parceiro', perfil: 'PARCEIRO' },
  { arquivo: '13-painel-admin.png', rota: '/admin', perfil: 'ADMINISTRADOR' },
  { arquivo: '14-sugestao-roteiro.png', rota: '/itinerarios', acao: 'sugerir' },
  { arquivo: '15-resumo-avaliacoes.png', rota: '/destinos/1', acao: 'resumir' },
  { arquivo: '16-traducao-conversa.png', rota: '/reuniao/sala-chapada-2027', acao: 'traduzir' }
]

const navegador = await chromium.launch()

for (const tela of telas) {
  const contexto = await navegador.newContext({
    viewport: { width: 1440, height: 900 },
    deviceScaleFactor: 2,
    locale: 'pt-BR',
    timezoneId: 'America/Bahia'
  })

  await contexto.route('**/api/**', async rota => {
    const requisicao = rota.request()
    const caminho = new URL(requisicao.url()).pathname.replace(/^\/api/, '') +
      (new URL(requisicao.url()).search || '')
    const corpo = responder(caminho, requisicao.method())
    if (corpo === null) {
      await rota.fulfill({ status: 204, body: '' })
      return
    }
    await rota.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify(corpo)
    })
  })

  if (tela.autenticado !== false) {
    const perfil = tela.perfil || 'VIAJANTE'
    const nome = perfil === 'ADMINISTRADOR' ? 'Administração vIAaja'
      : perfil === 'PARCEIRO' ? 'Transportes Aéreos Bahia'
        : 'Yan Leal Nascimento'
    await contexto.addInitScript(([p, n]) => {
      localStorage.setItem('viaaja.token', 'token-de-demonstracao')
      localStorage.setItem('viaaja.usuario', JSON.stringify({ id: 5, nome: n, perfil: p }))
    }, [perfil, nome])
  }

  const pagina = await contexto.newPage()
  await pagina.goto(BASE + tela.rota, { waitUntil: 'networkidle' })

  if (tela.acao === 'comparar') {
    await pagina.getByRole('button', { name: /comparar/i }).first().click()
  }
  if (tela.acao === 'buscar') {
    await pagina.getByRole('button', { name: /buscar/i }).first().click()
  }
  if (tela.acao === 'interpretar') {
    await pagina.getByRole('button', { name: /entender pedido/i }).first().click()
  }
  if (tela.acao === 'procurar') {
    await pagina.getByRole('button', { name: /procurar/i }).first().click()
  }
  if (tela.acao === 'sugerir') {
    await pagina.getByRole('button', { name: /sugerir roteiro/i }).first().click()
  }
  if (tela.acao === 'traduzir') {
    await pagina.getByLabel('O que você quer dizer').fill('Que tal a gente ficar num hotel perto do Recife Antigo e jantar fora na virada?')
    await pagina.getByRole('button', { name: /^traduzir$/i }).first().click()
  }
  if (tela.acao === 'resumir') {
    await pagina.getByRole('button', { name: /resumir avaliações/i }).first().click()
  }

  await pagina.waitForTimeout(900)
  await pagina.screenshot({ path: join(AQUI, tela.arquivo), fullPage: true })
  console.log('capturada', tela.arquivo)
  await contexto.close()
}

await navegador.close()
