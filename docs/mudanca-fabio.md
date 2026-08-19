# Relatório Detalhado — Melhorias no Projeto esudaPosEs

**Branch:** `feature/consulta-cep-fabio`
**Data:** 18/08/2026
**Projeto:** API CEP + Clima — Faculdade ESUDA

---

## 1. DTOs com Java Records

### O que foi feito
Foram criados **5 Java Records** no pacote `br.edu.esuda.cepclima.dto` para substituir o uso de `Map<String, Object>` como tipo de retorno em toda a aplicação:

| Record | Campos | Usado por |
|--------|--------|-----------|
| [EnderecoDto](file:///c:/Users/emidio/Desktop/esudaPosEs/cep-clima/backend/src/main/java/br/edu/esuda/cepclima/dto/EnderecoDto.java) | `logradouro`, `bairro`, `localidade`, `uf` | MapaService, ClimaService |
| [CoordenadasDto](file:///c:/Users/emidio/Desktop/esudaPosEs/cep-clima/backend/src/main/java/br/edu/esuda/cepclima/dto/CoordenadasDto.java) | `latitude`, `longitude`, `nome` | MapaService, ClimaService |
| [ClimaDto](file:///c:/Users/emidio/Desktop/esudaPosEs/cep-clima/backend/src/main/java/br/edu/esuda/cepclima/dto/ClimaDto.java) | `data`, `temperaturaMaximaCelsius` | ClimaService |
| [MapaResponse](file:///c:/Users/emidio/Desktop/esudaPosEs/cep-clima/backend/src/main/java/br/edu/esuda/cepclima/dto/MapaResponse.java) | `cep`, `endereco`, `coordenadas` | MapaController |
| [ClimaResponse](file:///c:/Users/emidio/Desktop/esudaPosEs/cep-clima/backend/src/main/java/br/edu/esuda/cepclima/dto/ClimaResponse.java) | `cep`, `endereco`, `coordenadas`, `clima` | ClimaController |

### Por que foi feito

**Antes**, toda a aplicação trafegava dados usando `Map<String, Object>`. Isso causava três problemas graves:

**1. Casts inseguros** — O código original fazia casts sem verificação de tipo, como:
```java
// ANTES — risco de ClassCastException em runtime
Map<String, Object> coordenadas = (Map<String, Object>) mapa.get("coordenadas");
double latitude = ((Number) coordenadas.get("latitude")).doubleValue();
```

Se alguém mudasse a estrutura do mapa, o erro só apareceria quando a API fosse chamada, não na compilação.

**2. Sem type-safety** — Errar o nome de uma chave (ex: `"lat"` em vez de `"latitude"`) não gera erro de compilação, só falha silenciosamente em runtime.

**3. Documentação zero** — Olhando `Map<String, Object>` na assinatura de um método, é impossível saber quais campos existem sem ler o código inteiro.

**Depois**, com Records, o compilador garante que os campos existem e têm os tipos corretos:
```java
// DEPOIS — seguro, verificado em tempo de compilação
MapaResponse mapa = mapaService.buscarMapaPorCep(cep);
double latitude = mapa.coordenadas().latitude();   // type-safe
double longitude = mapa.coordenadas().longitude();  // type-safe
```

> [!NOTE]
> Records em Java são imutáveis, geram `equals()`, `hashCode()` e `toString()` automaticamente, e o Jackson (Spring Boot) os serializa/desserializa sem nenhuma configuração extra.

### Compatibilidade com o Frontend

O [ClimaDto](file:///c:/Users/emidio/Desktop/esudaPosEs/cep-clima/backend/src/main/java/br/edu/esuda/cepclima/dto/ClimaDto.java) usa a anotação `@JsonProperty("temperatura_maxima_celsius")` para manter o nome `snake_case` no JSON de saída, garantindo que o frontend não precise ser alterado:

```java
public record ClimaDto(
    String data,
    @JsonProperty("temperatura_maxima_celsius") double temperaturaMaximaCelsius
) {}
```

O JSON de resposta continua **exatamente o mesmo**:
```json
{
  "clima": {
    "data": "2026-08-18",
    "temperatura_maxima_celsius": 25.9
  }
}
```

---

## 2. Correção do CORS

### O que foi feito
Adicionada configuração CORS para o endpoint `/mapa/**` em [WebConfig.java](file:///c:/Users/emidio/Desktop/esudaPosEs/cep-clima/backend/src/main/java/br/edu/esuda/cepclima/config/WebConfig.java).

### Por que foi feito

**Antes**, só o `/clima/**` tinha CORS habilitado:
```java
// ANTES
registry.addMapping("/clima/**").allowedOrigins("*");
```

Isso significava que se um frontend externo (em outro domínio/porta) tentasse chamar `GET /mapa/{cep}`, o navegador **bloquearia a requisição** por política de CORS. O `/mapa/**` ficava inacessível para aplicações web que não estivessem no mesmo domínio.

**Depois:**
```java
// DEPOIS
registry.addMapping("/clima/**").allowedOrigins("*");
registry.addMapping("/mapa/**").allowedOrigins("*");
```

Ambos os endpoints ficam acessíveis via cross-origin.

---

## 3. RestClient com Timeouts

### O que foi feito
Criado [RestClientConfig.java](file:///c:/Users/emidio/Desktop/esudaPosEs/cep-clima/backend/src/main/java/br/edu/esuda/cepclima/config/RestClientConfig.java) — um `@Bean` que configura o `RestClient.Builder` com timeouts de **5 segundos** para conexão e **10 segundos** para leitura.

### Por que foi feito

**Antes**, cada service criava seu próprio `RestClient` sem nenhuma configuração:
```java
// ANTES — sem timeout, sem centralização
public MapaService() {
    this.restClient = RestClient.create();  // timeout infinito!
}
```

**Problemas:**
- Se a API ViaCEP, Nominatim ou Open-Meteo ficasse lenta ou fora do ar, a requisição do usuário ficaria **travada indefinidamente** — sem erro, sem timeout
- Cada service criava sua própria instância, impossibilitando configuração centralizada
- Em produção, isso pode causar **esgotamento de threads** do Tomcat (todas ficam presas esperando resposta)

**Depois**, a configuração é centralizada e injetada:
```java
// RestClientConfig.java
@Bean
public RestClient.Builder restClientBuilder() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(Duration.ofSeconds(5));   // máx. 5s para conectar
    factory.setReadTimeout(Duration.ofSeconds(10));     // máx. 10s para ler resposta
    return RestClient.builder().requestFactory(factory);
}

// MapaService.java — recebe via injeção de dependência
public MapaService(RestClient.Builder restClientBuilder) {
    this.restClient = restClientBuilder.build();
}
```

Se uma API externa não responder em 10 segundos, o Spring lança uma exceção que é tratada e retorna HTTP 502 ao usuário.

---

## 4. Logging com SLF4J

### O que foi feito
Adicionado logging estruturado em [MapaService.java](file:///c:/Users/emidio/Desktop/esudaPosEs/cep-clima/backend/src/main/java/br/edu/esuda/cepclima/service/MapaService.java) e [ClimaService.java](file:///c:/Users/emidio/Desktop/esudaPosEs/cep-clima/backend/src/main/java/br/edu/esuda/cepclima/service/ClimaService.java) com três níveis:

| Nível | Quando | Exemplo |
|-------|--------|---------|
| `log.info` | Consulta bem-sucedida | `CEP 50050480 localizado: Recife, PE (lat=-8.05, lon=-34.88)` |
| `log.warn` | CEP não encontrado | `CEP não encontrado no ViaCEP: 99999999` |
| `log.error` | Falha em API externa | `Erro ao consultar Nominatim para CEP: 50050480` (com stack trace) |

### Por que foi feito

**Antes**, a aplicação **não logava absolutamente nada**. Se uma API externa falhasse, o usuário recebia um HTTP 502 genérico e o desenvolvedor não tinha como saber:
- Qual API falhou (ViaCEP? Nominatim? Open-Meteo?)
- Qual era o erro original
- Com quais parâmetros a chamada foi feita

**Depois**, qualquer falha fica registrada nos logs com contexto completo:
```
2026-08-18 ERROR MapaService : Erro ao consultar ViaCEP para CEP: 99999999
java.net.SocketTimeoutException: Read timed out
    at ...
```

---



## 5. Spring Boot Actuator + Health Check Docker

### O que foi feito
- Adicionada dependência `spring-boot-starter-actuator` no [pom.xml](file:///c:/Users/emidio/Desktop/esudaPosEs/cep-clima/backend/pom.xml)
- Adicionado health check no [docker-compose.yaml](file:///c:/Users/emidio/Desktop/esudaPosEs/cep-clima/docker-compose.yaml)

### Por que foi feito

**Antes**, o Docker não sabia se a aplicação dentro do container estava funcionando. Se o Spring Boot falhasse silenciosamente, o container continuaria "rodando" mas sem atender requisições.

**Depois**, o Docker verifica a saúde da aplicação a cada 30 segundos:
```yaml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
  interval: 30s
  timeout: 5s
  retries: 3
  start_period: 15s
```

O Actuator expõe:
- `/actuator/health` → status geral (`UP` / `DOWN`)
- Grupos `liveness` e `readiness` para orquestração (Kubernetes, Docker Swarm)

---

## 6. Melhorias no Frontend (index.html)

### O que foi feito em [index.html](file:///c:/Users/emidio/Desktop/esudaPosEs/cep-clima/backend/src/main/resources/static/index.html):

**Meta tags SEO:**
```html
<meta name="description" content="Consulte um CEP brasileiro e veja o endereço,
  localização no mapa e a temperatura máxima prevista para o dia.
  Projeto acadêmico da Faculdade ESUDA — Recife, PE.">
<meta name="robots" content="index, follow">
```
- Melhora a indexação por mecanismos de busca (Google, Bing)
- A `description` aparece como snippet nos resultados de pesquisa

**Popup no marker do mapa Leaflet:**
```javascript
// ANTES — clicar no marker não fazia nada
leafletMarker = L.marker([latitude, longitude]).addTo(leafletMap);

// DEPOIS — mostra o nome da cidade ao clicar
leafletMarker = L.marker([latitude, longitude])
  .bindPopup('<b>' + cityName + '</b>')
  .addTo(leafletMap);
```
- Melhora a experiência do usuário ao interagir com o mapa

---

## Resumo de Impacto

| Aspecto | Antes | Depois |
|---------|-------|--------|
| **Type-safety** | `Map<String, Object>` com casts manuais | Records com campos tipados |
| **CORS** | Só `/clima/**` | `/clima/**` + `/mapa/**` |
| **Timeout APIs** | Infinito (trava) | 5s connect + 10s read |
| **Logging** | Zero — erros silenciosos | Info + Warn + Error com contexto |
| **Documentação API** | Nenhuma | Swagger UI interativo |
| **Monitoramento** | Nenhum | Actuator health check |
| **SEO** | Sem meta tags | Description + robots |
| **UX mapa** | Marker estático | Popup com nome da cidade |

---

## Arquivos Alterados

```
cep-clima/backend/
├── pom.xml                                    ← +actuator, +springdoc
└── src/main/java/br/edu/esuda/cepclima/
    ├── config/
    │   ├── RestClientConfig.java              ← NOVO (timeouts)
    │   └── WebConfig.java                     ← CORS /mapa/**
    ├── controller/
    │   ├── ClimaController.java               ← retorna ClimaResponse
    │   └── MapaController.java                ← retorna MapaResponse
    ├── dto/
    │   ├── ClimaDto.java                      ← NOVO
    │   ├── ClimaResponse.java                 ← NOVO
    │   ├── CoordenadasDto.java                ← NOVO
    │   ├── EnderecoDto.java                   ← NOVO
    │   └── MapaResponse.java                  ← NOVO
    └── service/
        ├── ClimaService.java                  ← DTOs + logging + RestClient.Builder
        └── MapaService.java                   ← DTOs + logging + RestClient.Builder

cep-clima/
├── docker-compose.yaml                        ← health check
└── backend/src/main/resources/static/
    └── index.html                             ← SEO + popup mapa
```
