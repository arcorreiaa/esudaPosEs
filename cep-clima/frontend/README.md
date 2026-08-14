# Frontend — Interface web

Camada de apresentação: HTML, CSS e JavaScript.

## Arquivos

| Arquivo | Função |
|---------|--------|
| `index.html` | Página com campo de CEP, resultado e mapa |
| `esuda-logo.png` | Logo ESUDA |

## Como funciona

O Spring Boot serve esses arquivos em `http://localhost:8080` e a página chama a API com `fetch('/clima/' + cep)` no mesmo servidor.

O card de resultado mostra endereço e clima, e usa as coordenadas retornadas pela API para renderizar o mapa com Leaflet. Latitude e longitude não são exibidas no card.

## Editar a interface

Altere os arquivos nesta pasta. No Docker, o build copia automaticamente para o backend.

Rodando só o backend localmente, copie antes de iniciar:

```bash
cp index.html esuda-logo.png ../backend/src/main/resources/static/
cd ../backend && ./mvnw spring-boot:run
```

Ou use `docker compose up --build` na raiz de `cep-clima`.
