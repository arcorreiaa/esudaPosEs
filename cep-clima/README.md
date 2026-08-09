# CEP + Clima

API e interface web que consulta um **CEP brasileiro** e retorna o endereço e a **temperatura máxima prevista para o dia** na localidade.

Desenvolvido no contexto do curso na **Faculdade ESUDA** — Recife, PE.

[![Java](https://img.shields.io/badge/Java-17-F19800?style=for-the-badge&logo=openjdk&logoColor=white)](https://adoptium.net)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://docker.com)
[![Recife](https://img.shields.io/badge/Recife-PE-61b376?style=for-the-badge&logoColor=white)](https://esuda.edu.br)

---

## Funcionalidades

- Busca de endereço por CEP via [ViaCEP](https://viacep.com.br)
- Conversão da cidade em coordenadas via [Open-Meteo Geocoding](https://open-meteo.com/en/docs/geocoding-api)
- Consulta de temperatura máxima via [Open-Meteo Forecast](https://open-meteo.com/en/docs)
- Página web para consulta interativa
- Endpoint REST em JSON

---

## Requisitos

- Docker Desktop instalado e em execução
- Ou Java 17 + Maven Wrapper (apenas backend)

---

## Execução com Docker

```bash
git clone https://github.com/arcorreiaa/esudaPosEs.git
cd esudaPosEs/cep-clima
docker compose up --build
```

| Item | Valor |
|------|-------|
| URL | http://localhost:8080 |
| CEP de teste | `50050-480` (Santo Amaro, Recife) |
| Parar | `docker compose down` |

---

## Execução sem Docker (somente API)

```bash
cd backend
./mvnw spring-boot:run
```

```bash
curl http://localhost:8080/clima/50050480
```

---

## Arquitetura

```
Usuário
   │
   ▼
Spring Boot (página + API)
   │
   ├── GET /              → frontend (index.html)
   ├── GET /clima/{cep}   → API REST
   │
   ├── ViaCEP
   ├── Open-Meteo Geocoding
   └── Open-Meteo Forecast
```

---

## Estrutura do projeto

```
cep-clima/
├── docker-compose.yaml
├── frontend/           # HTML, CSS, JS (servido pelo Spring Boot)
├── backend/            # API Spring Boot
└── third-party/        # Documentação das APIs externas
```

| Camada | Pasta | Documentação |
|--------|-------|--------------|
| Frontend | `frontend/` | [frontend/README.md](frontend/README.md) |
| Backend | `backend/` | [backend/README.md](backend/README.md) |
| Third-party | `third-party/` | [third-party/README.md](third-party/README.md) |

---

## API REST

**Endpoint:** `GET /clima/{cep}`

**Exemplo:** `http://localhost:8080/clima/50050480`

O CEP pode ser enviado com ou sem hífen (8 dígitos).

**Resposta (200):**

```json
{
  "cep": "50050-480",
  "endereco": {
    "logradouro": "Rua Bispo Cardoso Ayres",
    "bairro": "Santo Amaro",
    "localidade": "Recife",
    "uf": "PE"
  },
  "coordenadas": {
    "latitude": -8.05,
    "longitude": -34.87,
    "nome": "Recife"
  },
  "clima": {
    "data": "2026-08-08",
    "temperatura_maxima_celsius": 30.2
  }
}
```

**Erros:**

| HTTP | Situação |
|------|----------|
| 400 | CEP inválido |
| 404 | CEP ou localidade não encontrada |
| 502 | Falha em serviço externo |

---

## Solução de problemas

| Problema | Solução |
|----------|---------|
| `Cannot connect to the Docker daemon` | Abra o Docker Desktop |
| `port is already allocated` | Libere a porta 8080 ou execute `docker compose down` |
| Erro de conexão na página | Execute `docker compose up` dentro de `cep-clima/` |
| `permission denied` no `mvnw` | `chmod +x backend/mvnw` |

---

## Stack

Spring Boot 4.1 · Java 17 · HTML/CSS/JS · Docker

---

[README do repositório](../README.md) · [Faculdade ESUDA](https://esuda.edu.br)
