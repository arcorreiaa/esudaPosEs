# CEP + Clima

<img src="../../docs/assets/banner-cep-clima.svg" width="640" alt="CEP + Clima">

[![Java](https://img.shields.io/badge/Java_17-F19800?style=flat-square&logo=openjdk&logoColor=white)](https://adoptium.net)
[![Spring](https://img.shields.io/badge/Spring_Boot_4.1-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)](https://docker.com)
[![Recife](https://img.shields.io/badge/Recife-PE-61b376?style=flat-square&logoColor=white)](https://esuda.edu.br)

[Rodar](#rodar) · [Fluxo](#fluxo) · [API](#api) · [Problemas](#problemas)

### Rodar

Docker Desktop **aberto**. Na pasta `cep-clima`:

```bash
git clone https://github.com/arcorreiaa/esudaPosEs.git
cd esudaPosEs/cep-clima
docker compose up --build
```

| | |
|--|--|
| URL | http://localhost:8080 |
| CEP teste | `50050-480` (Recife) |
| Parar | `docker compose down` |

Só API: `cd backend && ./mvnw spring-boot:run`

### Fluxo

```
  USUÁRIO
     │
     ▼
  FRONTEND (nginx) ──GET /clima/{cep}──▶ BACKEND (Spring Boot)
     │                                        │
     │                                        ├──▶ ViaCEP (endereço)
     │                                        ├──▶ Open-Meteo (coords)
     │                                        └──▶ Open-Meteo (temp max)
     ▼
  RESULTADO na página
```

| Camada | Pasta |
|--------|-------|
| Frontend | `frontend/` |
| Backend | `backend/` |
| APIs externas | `third-party/` |

### API

`GET /clima/50050480`

```json
{
  "cep": "50050-480",
  "endereco": { "localidade": "Recife", "uf": "PE", "bairro": "Santo Amaro", "logradouro": "Rua Bispo Cardoso Ayres" },
  "coordenadas": { "latitude": -8.05, "longitude": -34.87, "nome": "Recife" },
  "clima": { "data": "2026-08-08", "temperatura_maxima_celsius": 30.2 }
}
```

### Problemas

| Erro | Fix |
|------|-----|
| `Cannot connect to Docker daemon` | Docker Desktop aberto e rodando |
| `port is already allocated` | Porta 8080 em uso — `docker compose down` |
| Erro de conexão na página | Rodar dentro de `cep-clima/` |
| `permission denied: mvnw` | `chmod +x backend/mvnw` |

[README raiz](../../README.md) · [esuda.edu.br](https://esuda.edu.br)
