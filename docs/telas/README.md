# Telas do front-end

Capturas das telas da aplicação web, usadas no item 13 do roteiro da Unidade 2.

| Arquivo | Tela | Requisitos atendidos |
| --- | --- | --- |
| `01-entrar.png` | Entrar | RF-002 |
| `02-cadastro.png` | Criar conta de viajante | RF-001, RN-001 |
| `03-comparador.png` | Comparador de custo de deslocamento | RF-004, RN-007, RNF-012 |
| `04-busca-passagens.png` | Busca de passagens e hospedagens com filtros e ordenação | RF-005, RF-007, RF-009 |
| `05-assistente.png` | Assistente de viagem em linguagem natural e recomendações | RF-003, RF-012, RN-008, RN-009 |
| `06-itinerarios.png` | Nova viagem e lista de itinerários | RF-010, RF-011 |
| `07-itinerario-detalhe.png` | Itinerário dia a dia com inclusão e remoção de atividades | RF-011 |
| `08-destinos.png` | Catálogo de destinos | apoio ao RF-013 e ao RF-021 |
| `09-destino-detalhe.png` | Destino com atividades e avaliações | RF-021, RN-013 |
| `10-social.png` | Descoberta de viajantes, convites e conversas | RF-017, RF-018, RF-019, RN-010, RN-011, RN-012 |
| `11-perfil.png` | Perfil, preferências de privacidade e notificações | RF-022, RF-025, RN-010, RN-011 |
| `12-painel-parceiro.png` | Painel do parceiro fornecedor | RF-023 |
| `13-painel-admin.png` | Painel administrativo com indicadores de uso da IA | RF-024 |
| `14-sugestao-roteiro.png` | Roteiro sugerido pela IA, antes de salvar | RF-010, RN-008 |
| `15-resumo-avaliacoes.png` | Resumo das avaliações gerado pela IA | RF-013, RN-013 |
| `16-traducao-conversa.png` | Tradução simultânea durante a conversa | RF-020, RN-014 |

## Como regerar as capturas

As telas foram capturadas com o Playwright, em resolução de 1440 por 900 e fator de escala 2.
O script `capturar.mjs` intercepta as chamadas para `/api` e devolve respostas no mesmo formato
dos DTOs do back-end, de modo que as capturas podem ser refeitas sem subir o banco.

```
cd frontend
npm install
npm run build
npx vite preview --port 4173
```

Em outro terminal:

```
npm install -g playwright
node docs/telas/capturar.mjs
```

Para capturar com o back-end real no ar, suba a API na porta 8080, rode `npm run dev` no
front-end e aponte o script para o endereço do servidor de desenvolvimento:

```
VIAAJA_URL=http://localhost:5173 node docs/telas/capturar.mjs
```
