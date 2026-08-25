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

## Interface web

A página servida em `http://localhost:8080` fica em `src/main/resources/static/index.html`. É a única cópia, então edite ela diretamente. Ver [../frontend/README.md](../frontend/README.md).

## Problemas conhecidos

O catálogo completo de erros já enfrentados no projeto, com sintoma, causa e solução, está em [docs/problemas-conhecidos.md](../../docs/problemas-conhecidos.md).

Os que mais aparecem ao rodar o backend:

| Erro | Onde está documentado |
|------|-----------------------|
| `Cannot start maven from wrapper` | [item 1.2](../../docs/problemas-conhecidos.md#12-o-maven-wrapper-não-inicia) |
| `port is already allocated` na 8080 | [item 1.3](../../docs/problemas-conhecidos.md#13-a-porta-8080-já-está-em-uso) |
| `Permission denied` no `mvnw` | [item 1.4](../../docs/problemas-conhecidos.md#14-permissão-negada-ao-executar-o-mvnw) |
| A página abre mas não retorna dados | [item 1.5](../../docs/problemas-conhecidos.md#15-a-página-abre-mas-não-retorna-dados) |

Se você resolver um problema que ainda não está lá, registre no catálogo em vez de deixar a solução só no histórico do WhatsApp.

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
