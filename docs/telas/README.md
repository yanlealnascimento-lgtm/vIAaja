# Telas do front-end

Capturas das telas da aplicacao web, usadas no item 13 do roteiro da Unidade 2.

| Arquivo | Tela | Requisitos atendidos |
| --- | --- | --- |
| `01-entrar.png` | Entrar | RF-002 |
| `02-cadastro.png` | Criar conta de viajante | RF-001, RN-001 |
| `03-comparador.png` | Comparador de custo de deslocamento | RF-007, RN-007 |
| `04-busca-passagens.png` | Busca de passagens com filtros e ordenacao | RF-003, RF-004 |
| `05-assistente.png` | Assistente de viagem em linguagem natural | RF-009, RF-012, RN-008 |
| `06-itinerarios.png` | Lista de itinerarios e geracao de roteiro pela IA | RF-010, RF-011, RN-008 |
| `07-itinerario-detalhe.png` | Itinerario dia a dia com edicao de itens | RF-011, RF-013 |
| `08-destinos.png` | Catalogo de destinos | RF-005 |
| `09-destino-detalhe.png` | Destino com atividades, avaliacoes e resumo gerado pela IA | RF-017, RF-018, RN-013, RN-014 |
| `10-social.png` | Comunidade de viajantes, convites e reunioes | RF-019, RF-020, RF-021, RN-010, RN-011, RN-012 |
| `11-perfil.png` | Perfil, preferencias de privacidade e notificacoes | RF-022, RF-023, RN-010 |
| `12-painel-parceiro.png` | Painel do parceiro fornecedor | RF-025 |
| `13-painel-admin.png` | Painel administrativo com indicadores de uso da IA | RF-024 |

## Como regerar as capturas

As telas foram capturadas com o Playwright, em resolucao de 1440 por 900 e fator de escala 2.
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
front-end e aponte o script para o endereco do servidor de desenvolvimento:

```
VIAAJA_URL=http://localhost:5173 node docs/telas/capturar.mjs
```
