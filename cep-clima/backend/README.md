# Backend — API REST

Camada de aplicação. Recebe o CEP, orquestra as APIs externas e devolve JSON.

## Endpoint

```
GET /clima/{cep}
```

## Rodar localmente

```bash
cd cep-clima/backend
./mvnw spring-boot:run
```

Porta padrão: **8080**

## Testar

```bash
curl http://localhost:8080/clima/50050480
```

## Estrutura do código

```
backend/src/main/java/br/edu/esuda/cepclima/
├── CepClimaApplication.java      # entrada Spring Boot
├── config/WebConfig.java         # CORS (dev)
├── controller/
│   ├── ClimaController.java      # GET /clima/{cep}
│   └── ApiExceptionHandler.java  # erros padronizados
└── service/
    └── ClimaService.java         # ViaCEP + Open-Meteo
```

## Dependências principais

- `spring-boot-starter-webmvc`
- Java 17

## Docker

Build isolado (usado pelo `docker-compose` na raiz):

```bash
docker build -t cep-clima-backend .
```
