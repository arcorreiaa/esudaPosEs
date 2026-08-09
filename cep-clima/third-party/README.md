# Third-party — APIs externas

Camada de integração com serviços de terceiros. O backend consome estas APIs; não há código nesta pasta — apenas documentação.

## Serviços utilizados

### 1. ViaCEP

| | |
|---|---|
| **Função** | Retorna endereço completo a partir do CEP |
| **URL** | `https://viacep.com.br/ws/{cep}/json/` |
| **Exemplo** | `https://viacep.com.br/ws/50050480/json/` |
| **Documentação** | [viacep.com.br](https://viacep.com.br/) |
| **Autenticação** | Não requer |
| **Campos usados** | `cep`, `logradouro`, `bairro`, `localidade`, `uf` |

### 2. Open-Meteo Geocoding

| | |
|---|---|
| **Função** | Converte nome da cidade em latitude/longitude |
| **URL** | `https://geocoding-api.open-meteo.com/v1/search` |
| **Parâmetros** | `name`, `count=1`, `language=pt`, `countryCode=BR` |
| **Documentação** | [open-meteo.com/en/docs/geocoding-api](https://open-meteo.com/en/docs/geocoding-api) |
| **Autenticação** | Não requer |
| **Campos usados** | `latitude`, `longitude`, `name` |

### 3. Open-Meteo Forecast

| | |
|---|---|
| **Função** | Previsão de temperatura máxima do dia |
| **URL** | `https://api.open-meteo.com/v1/forecast` |
| **Parâmetros** | `latitude`, `longitude`, `daily=temperature_2m_max`, `forecast_days=1`, `timezone=auto` |
| **Documentação** | [open-meteo.com/en/docs](https://open-meteo.com/en/docs) |
| **Autenticação** | Não requer |
| **Campos usados** | `daily.time[0]`, `daily.temperature_2m_max[0]` |

## Fluxo de integração

```
CEP
  → ViaCEP          → localidade + UF
  → Geocoding       → latitude + longitude
  → Forecast        → temperatura máxima
  → JSON consolidado
```

## Observações

- Todas as APIs são **gratuitas** e **sem chave**.
- O projeto depende de internet para funcionar.
- Falhas externas retornam HTTP `502` ao cliente.
