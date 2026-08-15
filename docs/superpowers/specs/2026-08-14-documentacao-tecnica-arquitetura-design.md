# Design — Documentação técnica de arquitetura (`docs/arquitetura.md`)

**Data:** 2026-08-14
**Autor da contribuição:** Luis Felipe
**Repositório:** `arcorreiaa/esudaPosEs`
**Branch:** `docs/arquitetura-tecnica`

---

## 1. Problema

O repositório tem boa documentação **de usuário** — cinco READMEs em cascata (raiz, `cep-clima`, `backend`, `frontend`, `third-party`) com instruções de execução, tabelas de troubleshooting e descrição das APIs externas.

Não tem documentação **técnica**. Especificamente, não existe nenhum documento que responda:

- Como uma requisição atravessa o sistema, do CEP digitado até o JSON de resposta
- Qual a responsabilidade de cada componente e de quem ele depende
- Por que o sistema precisa de três APIs externas encadeadas
- Como os erros nascem, se propagam e viram resposta HTTP
- Quais as limitações conhecidas da implementação atual

Existem dois arquivos `.drawio` em `docs/` (`Alysson.drawio` e `eduardo_cadiz_cepclima.drawio`) sem nenhum texto que explique o que modelam — diagramas órfãos são o sintoma mais direto dessa lacuna.

Há ainda uma divergência entre documentação e realidade: os READMEs prometem os códigos HTTP 400/404/502, mas caminhos não tratados no código produzem 500.

## 2. Objetivo

Entregar **um** documento — `docs/arquitetura.md` — que sirva como referência técnica do projeto CEP + Clima, integrando o diagrama draw.io da implementação.

### Critérios de sucesso

1. Um leitor que nunca viu o projeto entende o fluxo completo sem precisar abrir o código
2. Todo comportamento afirmado no documento foi verificado contra a aplicação em execução, não inferido por leitura
3. O documento não duplica conteúdo dos READMEs existentes — referencia-os
4. As limitações conhecidas ficam registradas e viram insumo para issues

## 3. Escopo

### Dentro

- Arquivo único `docs/arquitetura.md`
- Diagrama de sequência em Mermaid (versionado em texto, renderiza no GitHub)
- Espaço reservado para o diagrama draw.io da implementação, a ser fornecido

### Fora

- Especificação OpenAPI
- ADRs (registros de decisão arquitetural)
- Qualquer alteração em código-fonte
- Qualquer alteração nos READMEs existentes
- Correção dos defeitos identificados na review — vão para issues separadas

**Justificativa do escopo enxuto:** um PR que só adiciona um arquivo é trivial de revisar e não conflita com o trabalho dos outros três autores.

## 4. Estrutura do documento

| # | Seção | Conteúdo |
|---|-------|----------|
| 1 | Visão geral | O que o sistema faz, em três frases. Registrar a decisão estruturante: um único artefato Spring Boot serve a API **e** o frontend estático — não existe servidor web separado |
| 2 | Diagrama de arquitetura | Espaço reservado para o draw.io. Recebe o PNG exportado embutido e o link para o `.drawio` editável |
| 3 | Componentes | Tabela de responsabilidade e dependências das sete classes. Destacar `MapaService` como peça central: é ele que consome ViaCEP **e** Nominatim, e `ClimaService` depende dele |
| 4 | Fluxo de uma requisição | Diagrama de sequência Mermaid + narrativa de `GET /clima/{cep}`: validação → ViaCEP → montagem da query textual → Nominatim → Open-Meteo → JSON consolidado |
| 5 | Integrações externas | Apenas o que é decisão arquitetural: por que três APIs, exigência de `User-Agent` do Nominatim, limite de 1 req/s da política de uso, ausência de cache. Referencia `third-party/README.md` em vez de repetir os campos |
| 6 | Tratamento de erros | Como `ResponseStatusException` + `ApiExceptionHandler` produzem o JSON de erro. Tabela do comportamento **real**, com nota explícita nos caminhos onde escapa 500 não previsto |
| 7 | Build e execução | Como o Dockerfile multi-stage monta o artefato, e o ponto que hoje impede o build a partir de um clone limpo — descrito como fato verificável |
| 8 | Limitações conhecidas | Ausência de testes e CI, `index.html` duplicado, ausência de cache e de timeouts |

### Princípio de organização

O documento é **orientado ao fluxo da requisição**, não a camadas.

Foi a alternativa escolhida entre três:

- **Orientado ao fluxo** (escolhida) — para um sistema cujo valor inteiro está na orquestração de três APIs encadeadas, o fluxo *é* a arquitetura. Cada componente aparece no momento em que entra em cena.
- **Orientado a camadas, estilo C4** — mais formal, mas com apenas sete classes as camadas ficam quase vazias, e `backend/README.md` já lista a estrutura de pastas.
- **Referência de componentes** — vira javadoc em markdown, envelhece mal e duplica o código.

Da abordagem C4 aproveita-se somente a abertura: uma seção de contexto antes de descer ao fluxo.

### Regra anti-duplicação

O documento **linka** para os READMEs existentes em vez de repetir seu conteúdo. Este repositório já sofre com duplicação: `index.html` existe em duas cópias que divergiram. Documentação duplicada diverge pelo mesmo mecanismo.

## 5. Decisões

### 5.1 Documentar o comportamento real, não o pretendido

A seção de erros descreve o que a API faz hoje, marcando explicitamente onde escapa um 500 não previsto pelo contrato documentado.

**Por quê:** documentação aspiracional que afirma um contrato não cumprido tem valor negativo — induz o leitor ao erro. As lacunas registradas viram insumo direto para as issues.

### 5.2 Verificação empírica antes de escrever

O comportamento dos endpoints é verificado com a aplicação em execução antes de a seção 6 ser escrita.

**Por quê:** documentar códigos HTTP por leitura de código é exatamente o tipo de inferência que deixou passar o problema do `.mvn`. Todo código HTTP afirmado no documento precisa ter sido observado.

Para subir a aplicação localmente é necessário contornar a ausência de `.mvn/` no repositório. O contorno é local e não é commitado — o defeito em si vira issue.

### 5.3 Mermaid para o diagrama de sequência

**Por quê:** renderiza nativamente no GitHub, é versionado em texto (revisável em diff) e não exige ferramenta externa para editar. Complementa o draw.io em vez de competir com ele: o draw.io mostra a arquitetura estática, o Mermaid mostra a dinâmica temporal.

### 5.4 Espaço reservado para o draw.io

O documento é escrito por completo agora, com a seção 2 marcada. O diagrama é encaixado quando fornecido.

**Por quê:** desbloqueia a escrita imediatamente e mantém as duas peças independentes.

## 6. Achados de review que viram issues

Registrados aqui para rastreabilidade. **Não** são corrigidos neste PR.

| Severidade | Achado |
|------------|--------|
| Bloqueante | `.gitignore` ignora `.mvn/`, mas o `Dockerfile` faz `COPY backend/.mvn .mvn`. Em clone limpo, tanto `docker compose up --build` quanto `./mvnw` falham |
| Alta | `CONTRIBUTING.md` contém o README de outro projeto (template LaTeX UPE-PPGEC) |
| Alta | `index.html` duplicado em `frontend/` e `backend/src/main/resources/static/`, com as cópias já divergentes |
| Alta | Nenhum teste no `cep-clima`; `Dockerfile` usa `-DskipTests`; sem CI |
| Média | `RestClient.create()` sem timeout em ambos os services |
| Média | `MapaService:53` — `resultados.isEmpty()` sem checagem de null → NPE → 500 |
| Média | `ClimaService:52` — `datas.get(0)` sem checagem de lista vazia → 500 |
| Média | CORS mapeado só em `/clima/**`; `/mapa/**` ficou de fora |
| Média | Query textual do Nominatim inclui o CEP e gera vírgulas vazias quando campos vêm em branco |
| Baixa | `docs/` mistura diagramas com código de dois laboratórios redundantes de race condition |
| Baixa | Indentação quebrada no bloco de arquitetura de `cep-clima/README.md:70` |

## 7. Entrega

Um PR contra `main` adicionando `docs/arquitetura.md`, mais o `.drawio` e seu PNG exportado quando fornecidos.

**Nota:** este arquivo de design é artefato de processo. Não deve entrar no PR para o repositório compartilhado.
