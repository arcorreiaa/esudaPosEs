# Frontend — Interface web

Camada de apresentação. Página com campo de CEP, máscara automática e exibição dos resultados.

## Rodar com Docker (recomendado)

Na raiz de `cep-clima`:

```bash
docker compose up --build
```

Acesse **http://localhost:8080**

## Arquivos

| Arquivo | Função |
|---------|--------|
| `index.html` | Página principal (HTML + CSS + JS) |
| `nginx.conf` | Servidor estático + proxy `/clima/` → backend |
| `Dockerfile` | Imagem nginx:alpine |

## Como a página chama a API

O JavaScript usa `fetch('/clima/' + cep)`. O Nginx encaminha essa rota ao container `backend`.

## Personalizar

Edite `index.html` para alterar layout, cores ou textos. Não é necessário rebuild do backend.
