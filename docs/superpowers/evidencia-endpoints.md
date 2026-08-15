# Evidência empírica — comportamento real dos endpoints

**Data da coleta:** 2026-08-14
**Método:** aplicação em execução local, ~80 requisições reais
**Ambiente:** JAR `api-cep-clima-0.0.1-SNAPSHOT.jar` sob Java 26.0.2 (bytecode alvo 17), startup em 1.454 s

> Arquivo de trabalho. Fonte dos valores da seção 6 de `docs/arquitetura.md`. **Não vai para o PR.**

---

## Ressalva sobre o build

O Docker daemon **não subiu** nesta máquina durante a coleta. O build foi feito com uma
distribuição Maven já presente em `~/.m2/wrapper/dists/apache-maven-3.9.16/`, cache de uma
execução anterior do wrapper.

Esse caminho **não é reproduzível em clone limpo** e não deve ser citado como instrução na
documentação. O que ele comprova: o projeto compila e roda **sem nenhuma alteração no
código-fonte** — o único impedimento é o `.mvn/` ausente.

---

## Casos observados

| # | Requisição | Status | Corpo |
|---|-----------|--------|-------|
| 1 | `/clima/50050480` | 200 | JSON completo (abaixo) |
| 2 | `/clima/50050-480` | 200 | Idêntico ao caso 1 — aceita hífen |
| 3 | `/clima/123` | 400 | `{"message":"CEP inválido"}` |
| 4 | `/clima/99999999` | 404 | `{"message":"Localidade não encontrada"}` |
| 5 | `/clima/abcdefgh` | 400 | `{"message":"CEP inválido"}` |
| 6 | `/mapa/50050480` | 200 | JSON completo (abaixo) |
| 7 | `/mapa/99999999` | 404 | `{"message":"Localidade não encontrada"}` |
| 8 | `/` | 200 | `text/html`, 12185 bytes |
| 9 | `/clima/01001000` (Sé, SP) | 200 | Sucesso |
| 10 | `/clima/69931000` (Capixaba, AC) | 200 | Sucesso — logradouro e bairro vazios |
| 11 | `/clima/76993000` (Colorado do Oeste, RO) | 200 | Sucesso |
| 12 | `/clima/69750000` (São Gabriel da Cachoeira, AM) | 200 | Sucesso |
| 13 | `/clima/78890000` | 404 | CEP não existe no ViaCEP |
| 14 | `/clima/00000000` | 404 | `{"message":"Localidade não encontrada"}` |
| 15 | 40 requisições paralelas | 502 | `{"message":"Erro ao consultar API de mapa"}` |

## Corpos literais

`GET /clima/50050480` → 200:

```json
{"cep":"50050-480","endereco":{"logradouro":"Rua Almeida Cunha","bairro":"Santo Amaro","localidade":"Recife","uf":"PE"},"coordenadas":{"latitude":-8.0543368,"longitude":-34.8864607,"nome":"Rua Almeida Cunha, Santo Amaro, Recife, Pernambuco, Região Nordeste, 50050-480, Brasil"},"clima":{"data":"2026-08-14","temperatura_maxima_celsius":27.8}}
```

`GET /mapa/50050480` → 200:

```json
{"cep":"50050-480","endereco":{"logradouro":"Rua Almeida Cunha","bairro":"Santo Amaro","localidade":"Recife","uf":"PE"},"coordenadas":{"latitude":-8.0543368,"longitude":-34.8864607,"nome":"Rua Almeida Cunha, Santo Amaro, Recife, Pernambuco, Região Nordeste, 50050-480, Brasil"}}
```

Mensagens de erro observadas — as três únicas:

```json
{"message":"CEP inválido"}                    // 400
{"message":"Localidade não encontrada"}       // 404
{"message":"Erro ao consultar API de mapa"}   // 502
```

As mensagens `"CEP não encontrado"`, `"Erro ao consultar API do ViaCEP"` e
`"Erro ao consultar API de clima"` **nunca foram observadas**.

---

## Achado 1 — o 404 "CEP não encontrado" é código morto

O ViaCEP retorna o campo `erro` como **string**, não booleano:

```
$ curl https://viacep.com.br/ws/99999999/json/
HTTP 200   {"erro": "true"}
```

Portanto, em `MapaService.java:95`:

```java
Boolean.TRUE.equals(viaCep.get("erro"))   // "true" é String → sempre false
```

O `ResponseStatusException(NOT_FOUND, "CEP não encontrado")` da linha 96 nunca executa.

Cadeia completa, verificada ponta a ponta:

1. ViaCEP devolve 200 com `{"erro": "true"}`
2. A checagem falha; o fluxo segue com `localidade` e `uf` nulos
3. `String.join` renderiza nulos como a literal `"null"` → `nomeBusca = ", , null, null, "`
4. Essa query no Nominatim devolve `[]`
5. Dispara o 404 da linha 54, com a mensagem "Localidade não encontrada"

**Efeito:** o status final coincide com o documentado, então o usuário não percebe. Mas a
mensagem atribui ao Nominatim uma falha que é do CEP, e **cada CEP inexistente gasta uma
chamada desnecessária ao Nominatim** — serviço limitado a 1 req/s.

## Achado 2 — nenhum erro é registrado em log

O log da aplicação terminou a sessão com 22 linhas, **todas de startup**, mesmo depois de
50+ respostas 502. `ApiExceptionHandler` converte a exceção em JSON e não registra nada.
Uma falha de serviço externo em produção não deixaria rastro.

## Achado 3 — o Nominatim rate-limita agressivamente

- 30 requisições paralelas → 11 respostas 502
- 40 requisições paralelas → 40 respostas 502
- O IP seguiu bloqueado (HTTP 429, corpo HTML do Varnish) por **mais de 5 minutos**, com
  ViaCEP e Open-Meteo respondendo 200 normalmente no mesmo período

Sem cache, retry ou throttling, uso concorrente moderado derruba a funcionalidade.

## Achado 4 — o exemplo do README está desatualizado

`cep-clima/README.md` mostra `"logradouro": "Rua Bispo Cardoso Ayres"`, `"nome": "Recife"` e
coordenadas arredondadas. A resposta real traz `"Rua Almeida Cunha"`, `nome` com o
`display_name` completo do Nominatim e coordenadas com 7 casas decimais.

---

## Riscos NÃO reproduzidos

Três pontos suspeitos foram investigados e **não** produziram falha. Registrados aqui para
rastreabilidade; por decisão de escopo, entram na documentação apenas como robustez
defensiva, não como defeito.

| Local | Premissa | Por que não falhou |
|-------|----------|--------------------|
| `MapaService.java:53` | Lista do Nominatim nunca é `null` | O serviço devolve `[]` (HTTP 200), nunca corpo nulo. Status de erro vira `RestClientException` → 502 |
| `ClimaService.java:52` | `daily.time` nunca vem vazia | Com `forecast_days=1` o Open-Meteo sempre retorna um elemento |
| Ambos | Casts de `Map` têm o tipo esperado | Os formatos das três APIs se mostraram estáveis |

Nenhum HTTP 500 foi produzido em ~80 requisições, incluindo dois picos de alta concorrência.

## Observação adicional

CEPs rurais e pouco mapeados **não** são ponto fraco: 4 de 5 resolveram normalmente. Não foi
encontrado nenhum CEP que exista no ViaCEP e falhe no Nominatim.
