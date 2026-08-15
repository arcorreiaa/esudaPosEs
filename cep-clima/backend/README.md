# Backend — API REST

Camada de aplicação. Recebe o CEP, orquestra as APIs externas e devolve JSON.

## Endpoint

```
GET /clima/{cep}
GET /mapa/{cep}
```

Serve a API e a página web (`src/main/resources/static/`).

## Rodar localmente

```bash
cd cep-clima/backend
./mvnw spring-boot:run
```

Página: **http://localhost:8080** · API: **http://localhost:8080/clima/50050480**

Exemplo (mapa): **http://localhost:8080/mapa/50050480**

## Docker

Na raiz de `cep-clima`:

```bash
docker compose up --build
```

## Estrutura do código

```
backend/src/main/java/br/edu/esuda/cepclima/
├── CepClimaApplication.java      # entrada Spring Boot
├── config/WebConfig.java         # CORS (dev)
├── controller/
│   ├── ClimaController.java      # GET /clima/{cep}
│   ├── MapaController.java       # GET /mapa/{cep}
│   └── ApiExceptionHandler.java  # erros padronizados
└── service/
    ├── ClimaService.java         # Open-Meteo Forecast (clima)
    └── MapaService.java          # ViaCEP + Nominatim (coordenadas)
```

## Dependências principais

- `spring-boot-starter-webmvc`
- Java 17

## Testes

Linux e macOS:

```bash
./mvnw test
```

Windows:

```powershell
.\mvnw.cmd test
```

Os testes automatizados verificam a validação dos formatos aceitos para CEP.
