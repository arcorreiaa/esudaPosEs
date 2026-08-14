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

### 2. OpenStreetMap Nominatim

| | |
|---|---|
| **Função** | Converte endereço do CEP em latitude/longitude |
| **URL** | `https://nominatim.openstreetmap.org/search` |
| **Parâmetros** | `format=jsonv2`, `limit=1`, `countrycodes=br`, `q=<endereco completo>` |
| **Documentação** | [nominatim.openstreetmap.org](https://nominatim.openstreetmap.org/) |
| **Autenticação** | Não requer |
| **Campos usados** | `lat`, `lon`, `display_name` |

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
  → ViaCEP          → logradouro + bairro + localidade + UF
  → Nominatim       → latitude + longitude
  → Forecast        → temperatura máxima
  → JSON consolidado
```

## Observações

- Todas as APIs são **gratuitas** e **sem chave**.
- O projeto depende de internet para funcionar.
- Falhas externas retornam HTTP `502` ao cliente.
