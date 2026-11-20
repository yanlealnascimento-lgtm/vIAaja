# Modelo C4 do vIAaja

Este diretorio reune os quatro niveis do modelo C4 do projeto. Cada nivel tem o arquivo
fonte em Mermaid (`.mmd`), que pode ser editado e reaproveitado, e a imagem gerada em PNG,
que e a versao usada na documentacao.

Para regerar as imagens depois de alterar um fonte:

```
npm install -g @mermaid-js/mermaid-cli
mmdc -i c4-nivel-1-contexto.mmd -o c4-nivel-1-contexto.png -b white -s 2
```

## Nivel 1: Contexto

`c4-nivel-1-contexto.mmd` / `c4-nivel-1-contexto.png`

Mostra o vIAaja como uma caixa unica e o que existe em volta dele. Os quatro perfis de
usuario identificados na Unidade 1 aparecem como atores: o viajante, que e o usuario
principal, o parceiro fornecedor, que mantem as ofertas, o atendente de suporte e o
administrador. Do lado dos sistemas externos ficam o provedor de IA, usado para sugerir
roteiros, resumir avaliacoes e traduzir textos, e o servico de e-mail, usado nas mensagens
de confirmacao de conta e de recuperacao de senha.

## Nivel 2: Conteineres

`c4-nivel-2-conteineres.mmd` / `c4-nivel-2-conteineres.png`

Abre a caixa do vIAaja e mostra as partes executaveis. A aplicacao web em React roda no
navegador e conversa apenas com a API; a API em Spring Boot concentra as regras de negocio
e e a unica que fala com o banco e com os sistemas externos; o banco guarda todo o estado
da plataforma; a documentacao OpenAPI publica o contrato dos endpoints no Swagger UI. Em
desenvolvimento o banco e o H2 em memoria e em producao e o PostgreSQL, o que e resolvido
por perfil do Spring e nao muda o codigo.

## Nivel 3: Componentes

`c4-nivel-3-componentes.mmd` / `c4-nivel-3-componentes.png`

Abre o conteiner da API. A requisicao entra pelo componente de seguranca, que valida o
token JWT e o perfil, segue para os controladores REST e deles para os servicos de
aplicacao. Dois pontos merecem atencao porque explicam decisoes de projeto: a comparacao de
custos nao conhece as fontes de preco concretas, ela depende da porta `FonteDePreco` e
recebe a lista de implementacoes por injecao, de modo que incluir um novo modal nao exige
alterar o servico; e a integracao com IA depende da porta `ServicoIA`, que tem um simulador
local como implementacao padrao e um cliente do provedor externo que so e ativado por
configuracao. Por isso o sistema roda inteiro sem nenhuma chave de API.

## Nivel 4: Codigo

`c4-nivel-4-codigo.mmd` / `c4-nivel-4-codigo.png`

Desce ao nivel de classes em um recorte do sistema, o componente de comparacao de custos,
que implementa o RF-004 e a regra RN-007. O diagrama mostra o controlador, o servico, a
interface `FonteDePreco` com as tres implementacoes, as entidades `ComparacaoCusto` e
`OpcaoDeslocamento`, o enum `ModalTransporte` e o repositorio. As assinaturas sao as mesmas
que estao no codigo-fonte.
