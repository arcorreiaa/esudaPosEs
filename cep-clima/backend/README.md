# Backend — API REST

Camada de aplicação. Recebe o CEP, orquestra as APIs externas e devolve JSON.

## Endpoint

```
GET /clima/{cep}
```

Serve a API e a página web (`src/main/resources/static/`).

## Rodar localmente

```bash
cd cep-clima/backend
./mvnw spring-boot:run
```

Página: **http://localhost:8080** · API: **http://localhost:8080/clima/50050480**

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
│   └── ApiExceptionHandler.java  # erros padronizados
└── service/
    └── ClimaService.java         # ViaCEP + Open-Meteo
```

## Dependências principais

- `spring-boot-starter-webmvc`
- Java 17

## Problema conhecido

Ao executar o Docker, pode ocorrer o erro abaixo durante o build:

```text
=> ERROR [api-cep-clima build 4/8] COPY backend/.mvn .mvn         0.0s
------
 > [api-cep-clima build 4/8] COPY backend/.mvn .mvn:
------
failed to solve: failed to compute cache key: failed to calculate checksum of ref ...: "/backend/.mvn": not found
```

Esse erro acontece quando a pasta `.mvn` (usada pelo Maven Wrapper) não existe dentro de `backend`.

### Como resolver

1. Entre na pasta `backend`.
2. Execute o comando abaixo para gerar os arquivos do Maven Wrapper:

```bash
mvn wrapper:wrapper
```

3. Após a geração da pasta `.mvn` e dos arquivos `mvnw`/`mvnw.cmd` (se necessário), rode novamente:

```bash
docker compose up --build
```

Com isso, a etapa `COPY backend/.mvn .mvn` do Dockerfile passa a encontrar os arquivos esperados e o build deve seguir normalmente.