# Arquitetura — CEP + Clima

Documentação técnica do projeto CEP + Clima, desenvolvido no curso da Faculdade ESUDA.

Para instalar e executar, veja o [README do projeto](../cep-clima/README.md). Este documento
descreve **como o sistema funciona por dentro** e por que foi construído assim.

## Índice

1. [Visão geral](#1-visão-geral)
2. [Diagrama de arquitetura](#2-diagrama-de-arquitetura)
3. [Componentes](#3-componentes)
4. [Fluxo de uma requisição](#4-fluxo-de-uma-requisição)
5. [Integrações externas](#5-integrações-externas)
6. [Tratamento de erros](#6-tratamento-de-erros)
7. [Build e execução](#7-build-e-execução)
8. [Limitações conhecidas](#8-limitações-conhecidas)

---

## 6. Tratamento de erros

Os serviços não devolvem código de erro nem `null` quando algo falha: lançam
`ResponseStatusException`, carregando o status HTTP e a mensagem. `ApiExceptionHandler`,
anotado com `@RestControllerAdvice`, intercepta essas exceções em qualquer controller e as
converte num corpo JSON uniforme:

```json
{ "message": "CEP inválido" }
```

A vantagem do arranjo é que os controllers não têm nenhum `try/catch` — apenas delegam.
Falhas de rede nas chamadas externas são capturadas como `RestClientException` dentro de
cada serviço e reempacotadas como `502 Bad Gateway`, o que distingue "o serviço externo
falhou" de "o pedido do usuário estava errado".

### Comportamento observado

Medido com a aplicação em execução, ~80 requisições reais:

| Requisição | Status | Corpo |
|-----------|--------|-------|
| `/clima/50050480` | `200` | JSON com `cep`, `endereco`, `coordenadas` e `clima` |
| `/clima/50050-480` | `200` | Idêntico — o hífen é aceito |
| `/clima/123` | `400` | `{"message":"CEP inválido"}` |
| `/clima/abcdefgh` | `400` | `{"message":"CEP inválido"}` |
| `/clima/99999999` | `404` | `{"message":"Localidade não encontrada"}` |
| `/mapa/50050480` | `200` | Mesmo JSON, sem o bloco `clima` |
| `/mapa/99999999` | `404` | `{"message":"Localidade não encontrada"}` |
| `/` | `200` | Página HTML |
| Muitas requisições simultâneas | `502` | `{"message":"Erro ao consultar API de mapa"}` |

Apenas três mensagens de erro chegam ao cliente na prática: `CEP inválido`,
`Localidade não encontrada` e `Erro ao consultar API de mapa`.

### O 404 de CEP inexistente não vem de onde parece

`MapaService` tenta detectar CEP inexistente logo após consultar o ViaCEP:

```java
if (viaCep == null || Boolean.TRUE.equals(viaCep.get("erro"))) {
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CEP não encontrado");
}
```

Só que o ViaCEP devolve esse campo como **texto**, não como booleano:

```
$ curl https://viacep.com.br/ws/99999999/json/
HTTP 200   {"erro": "true"}
```

`Boolean.TRUE.equals("true")` é `false`, então essa condição nunca se cumpre e a mensagem
`"CEP não encontrado"` nunca chega a ser enviada. O que acontece de fato:

1. O ViaCEP responde `200` com `{"erro": "true"}`
2. A verificação não dispara; o fluxo segue com `localidade` e `uf` nulos
3. A busca textual é montada como `", , null, null, "` — `String.join` escreve nulos como a palavra `null`
4. O Nominatim não encontra nada e devolve lista vazia
5. Aí sim nasce o `404`, com a mensagem `"Localidade não encontrada"`

O status final coincide com o esperado, então o usuário não percebe. As consequências são
outras: a mensagem atribui ao mapa uma falha que é do CEP, e **cada CEP inexistente consome
uma chamada desnecessária ao Nominatim** — um serviço limitado a uma requisição por segundo.

### Nenhum erro é registrado

`ApiExceptionHandler` converte a exceção em JSON e não escreve nada no log. Durante a
verificação, o log da aplicação terminou com 22 linhas — todas de inicialização — mesmo
depois de mais de cinquenta respostas `502`. Uma indisponibilidade de serviço externo em
produção não deixaria rastro nenhum para diagnóstico.

---

## 7. Build e execução

O `Dockerfile` usa build em dois estágios. O primeiro parte de uma imagem com JDK, copia o
`pom.xml`, o código-fonte e — este é o ponto importante — copia `frontend/index.html` e
`frontend/esuda-logo.png` para `src/main/resources/static/`, embutindo o frontend no JAR. O
segundo estágio parte de uma imagem apenas com JRE e recebe só o JAR produzido, o que reduz
bastante a imagem final.

O `docker-compose.yaml` declara `context: .` na raiz de `cep-clima/`, e não em `backend/`,
justamente porque o build precisa enxergar as duas pastas ao mesmo tempo.

### A cópia do frontend

`index.html` existe em dois lugares do repositório: `frontend/index.html` e
`backend/src/main/resources/static/index.html`. O build Docker sobrescreve o segundo com o
primeiro — portanto **o Docker sempre serve a versão de `frontend/`**.

Executando pelo Maven, sem Docker, essa cópia não acontece e o Spring serve o arquivo que
estiver em `static/`. Como as duas cópias podem divergir — e atualmente divergem — os dois
modos de execução podem apresentar telas diferentes.

### Estado do build a partir de um clone limpo

O `.gitignore` inclui `.mvn/`, então o diretório do Maven Wrapper nunca foi versionado. O
`Dockerfile`, por sua vez, executa `COPY backend/.mvn .mvn`.

Verificado em clone limpo do repositório:

```
$ ls -a cep-clima/backend
.dockerignore  .gitattributes  Dockerfile  README.md  mvnw  mvnw.cmd  pom.xml  src

$ ./mvnw -v
./mvnw: line 117: ./.mvn/wrapper/maven-wrapper.properties: No such file or directory
```

Nessa condição, tanto `docker compose up --build` quanto `./mvnw spring-boot:run` falham.
Versionar `backend/.mvn/wrapper/maven-wrapper.properties` resolve os dois casos.

Vale registrar o que a verificação comprovou: o código-fonte **compila e roda sem nenhuma
alteração**. O impedimento é apenas o arquivo de configuração ausente, não o projeto.

---

## 8. Limitações conhecidas

Registradas para quem for continuar o projeto. Não são defeitos ocultos — são consequências
conhecidas do escopo acadêmico.

| Limitação | Efeito |
|-----------|--------|
| Sem testes automatizados; o build usa `-DskipTests` | Nenhuma regressão é detectada automaticamente |
| Sem integração contínua | Nada valida um pull request antes do merge |
| Sem timeout nas chamadas HTTP | Uma API externa lenta prende a thread que atende a requisição |
| Sem cache nem controle de taxa | Consultas repetidas geram chamadas novas e consomem a cota do Nominatim |
| Sem registro de erros em log | Falhas externas não deixam rastro para diagnóstico |
| `index.html` duplicado | As duas cópias divergem; Docker e Maven servem telas diferentes |
| Respostas modeladas como `Map` | O contrato da API não existe de forma verificável em lugar nenhum |
| Dependência total de serviços externos | Sem internet, ou com qualquer das três APIs fora, o sistema não responde |

### Robustez defensiva

Três pontos do código assumem que a resposta externa veio bem-formada: a lista devolvida
pelo Nominatim nunca é nula (`MapaService`), as listas de previsão nunca vêm vazias
(`ClimaService`) e os campos aninhados sempre têm o tipo esperado no cast.

Nenhuma dessas premissas falhou durante a verificação — o Nominatim devolve lista vazia em
vez de corpo nulo, e o Open-Meteo com `forecast_days=1` sempre retorna um elemento. São
pontos de robustez a endurecer, não defeitos observados.

### Fragilidade sob concorrência

O Nominatim aplica limite de uso de forma agressiva. Na verificação, quarenta requisições
simultâneas resultaram em `502` para todas, e o endereço IP permaneceu bloqueado por mais de
cinco minutos — enquanto ViaCEP e Open-Meteo seguiam respondendo normalmente. Sem cache,
repetição ou controle de taxa, uso concorrente moderado derruba a funcionalidade.

---

[README do projeto](../cep-clima/README.md) · [README do repositório](../README.md) · [Faculdade ESUDA](https://esuda.edu.br)
