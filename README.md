# esudaPosEs

<img src="docs/assets/banner-esuda.svg" width="640" alt="esudaPosEs">

[![ESUDA](https://img.shields.io/badge/ESUDA-F19800?style=flat-square&logoColor=white)](https://esuda.edu.br)
[![Recife](https://img.shields.io/badge/Recife-PE-61b376?style=flat-square&logoColor=white)](https://esuda.edu.br)
[![MIT](https://img.shields.io/badge/MIT-F19800?style=flat-square)](LICENSE)

[Entregas](#entregas) · [Rodar](#rodar) · [Problemas](#problemas)

### Entregas

| Projeto | Descrição | Docs |
|---------|-----------|------|
| CEP + Clima | CEP → endereço → clima | [cep-clima/README.md](cep-clima/README.md) |
| Demo Spring | Base Spring Boot | [api/demospring01](api/demospring01) |

### Rodar

```bash
git clone https://github.com/arcorreiaa/esudaPosEs.git
cd esudaPosEs/cep-clima
docker compose up --build
```

→ **http://localhost:8080** · CEP: `50050-480`

### Autores

Alysson Rychard · Eduardo Serra · Fabio Emidio · Luis Felipe

### Problemas

| Erro | Fix |
|------|-----|
| Docker daemon | Abrir Docker Desktop |
| Porta 8080 ocupada | `docker compose down` ou matar processo na 8080 |
| Pasta errada | `cd cep-clima` antes do compose |

[esuda.edu.br](https://esuda.edu.br) · [LICENSE](LICENSE)
