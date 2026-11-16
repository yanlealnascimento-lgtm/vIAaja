# vIAaja

Plataforma web de planejamento de viagens com apoio de inteligencia artificial. O sistema
reune busca de passagens e hospedagens, comparacao de custo de deslocamento entre aviao,
onibus e carro, montagem de itinerarios dia a dia, avaliacoes de destinos e um espaco para
viajantes se encontrarem e planejarem juntos.

Projeto da disciplina Topicos Avancados em Programacao, Engenharia de Software, Universidade
Catolica do Salvador, semestre 2026.2. Professor Msc. Elton Figueiredo da Silva.

## Equipe

- Davi de Sa Luquini
- Erick de Souza Pereira
- Joao Henrique Santos Nascimento Souza
- Kauan Aparecido Costa Casemiro
- Yan Leal Nascimento (ScrumMaster)

## Tecnologias

Back-end em Java 17 com Spring Boot 3.3, Spring Data JPA, Spring Security com JWT, Spring
Cache e documentacao OpenAPI pelo springdoc. O banco e o H2 em memoria no perfil de
desenvolvimento e o PostgreSQL no perfil de producao. Front-end em React 18 com Vite e
react-router-dom, em JavaScript puro, consumindo a API por fetch.

## Estrutura do repositorio

```
backend/    API em Spring Boot, organizada por modulo de negocio
frontend/   aplicacao web em React
docs/c4/    modelo C4 nos quatro niveis, em Mermaid e em PNG
docs/telas/ capturas das telas e o script que as gera
```

O back-end esta dividido por modulo e nao por camada tecnica. Cada modulo tem o seu
`domain`, `repository`, `service` e `web`, o que mantem junto o que muda junto:

```
com.ucsal.viaaja
  common       excecoes de negocio, tratador de erros e DTOs compartilhados
  config       propriedades, seguranca, OpenAPI e configuracao geral
  identidade   usuarios, perfis, autenticacao JWT e privacidade
  catalogo     destinos, passagens, hospedagens, quartos e atividades
  comparador   comparacao de custo de deslocamento e as fontes de preco
  ia           porta ServicoIA, provedores, registro de chamadas e integracao
  itinerario   itinerarios, dias e itens
  avaliacao    avaliacoes de destinos e hospedagens
  social       convites entre viajantes e reunioes de planejamento
  notificacao  notificacoes internas
  gestao       painel do parceiro e painel administrativo
  seed         carga inicial de dados para desenvolvimento
```

## Como executar

### Back-end

Requer Java 17 e Maven 3.9 ou superior.

```
cd backend
mvn spring-boot:run
```

A API sobe em `http://localhost:8080` com o perfil `dev`, que usa o H2 em memoria e aplica a
carga inicial de dados. O console do H2 fica em `http://localhost:8080/h2-console` e a
documentacao interativa em `http://localhost:8080/swagger-ui.html`.

Para rodar com PostgreSQL:

```
export SPRING_PROFILES_ACTIVE=prod
export VIAAJA_DB_URL=jdbc:postgresql://localhost:5432/viaaja
export VIAAJA_DB_USUARIO=postgres
export VIAAJA_DB_SENHA=postgres
mvn spring-boot:run
```

### Front-end

Requer Node 18 ou superior.

```
cd frontend
npm install
npm run dev
```

A aplicacao sobe em `http://localhost:5173` e o Vite encaminha as chamadas de `/api` para o
back-end na porta 8080.

## Contas de demonstracao

A carga inicial cria as contas abaixo. A senha de todas e `viaaja2026`.

| E-mail | Perfil |
| --- | --- |
| yan@viaaja.com | Viajante |
| davi@viaaja.com | Viajante |
| erick@viaaja.com | Viajante |
| joao@viaaja.com | Viajante |
| kauan@viaaja.com | Viajante |
| parceiro.aereo@viaaja.com | Parceiro fornecedor |
| parceiro.onibus@viaaja.com | Parceiro fornecedor |
| parceiro.hotel@viaaja.com | Parceiro fornecedor |
| suporte@viaaja.com | Atendente de suporte |
| admin@viaaja.com | Administrador |

## Integracao com IA

A integracao e feita por uma porta, a interface `ServicoIA`, com duas implementacoes. A
padrao e o `SimuladorServicoIA`, que gera roteiros, resumos e traducoes localmente a partir
dos dados do proprio banco. Por isso o projeto roda inteiro sem nenhuma chave de API. Para
usar um provedor externo:

```
export VIAAJA_IA_PROVEDOR=openai
export VIAAJA_IA_CHAVE=sua-chave
```

Toda chamada, pelo simulador ou pelo provedor externo, e registrada em
`RegistroChamadaIA` com funcionalidade, tempo de resposta e status, e esses numeros
alimentam os indicadores do painel administrativo, atendendo ao RF-024.

## Escopo desta versao

Os requisitos de reserva efetiva, pagamento, emissao de comprovante e cancelamento
(RF-006, RF-008, RF-014, RF-015 e RF-016) ficaram fora desta entrega, conforme definido na
Unidade 1. O sistema vai ate a comparacao, o planejamento e o itinerario, e nao processa
pagamentos.

## Documentacao

O modelo C4 esta em `docs/c4`, com o fonte Mermaid e a imagem de cada um dos quatro niveis.
As capturas das telas estao em `docs/telas`, com a tabela que liga cada tela aos requisitos
que ela atende.
