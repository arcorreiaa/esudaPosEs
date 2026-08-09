# esudaPosEs

Repositório de entregas e laboratórios do curso na **Faculdade ESUDA** — Recife, PE.

[![ESUDA](https://img.shields.io/badge/Faculdade-ESUDA-F19800?style=for-the-badge&logoColor=white)](https://esuda.edu.br)
[![Recife](https://img.shields.io/badge/Recife-PE-61b376?style=for-the-badge&logoColor=white)](https://esuda.edu.br)
[![License](https://img.shields.io/badge/License-MIT-F19800?style=for-the-badge)](LICENSE)

---

## Sobre

Este repositório reúne trabalhos desenvolvidos pelos alunos, organizados por projeto. O principal entregável é o **CEP + Clima**: consulta de endereço e temperatura a partir de um CEP brasileiro.

---

## Requisitos

| Ferramenta | Obrigatório para |
|------------|------------------|
| [Docker Desktop](https://www.docker.com/products/docker-desktop/) | Rodar o projeto completo |
| [Java 17](https://adoptium.net/) | Rodar apenas o backend sem Docker |
| Git | Clonar o repositório |

---

## Instalação e execução

```bash
git clone https://github.com/arcorreiaa/esudaPosEs.git
cd esudaPosEs/cep-clima
docker compose up --build
```

Acesse **http://localhost:8080** e teste com o CEP `50050-480` (Recife).

Para encerrar: `docker compose down`

Documentação detalhada: [cep-clima/README.md](cep-clima/README.md)

---

## Estrutura do repositório

```
esudaPosEs/
├── cep-clima/          # Entrega CEP + Clima (frontend, backend, docker)
├── api/demospring01/   # Exemplo Spring Boot
└── docs/               # Materiais e laboratórios
```

---

## Entregas

| Projeto | Descrição | Documentação |
|---------|-----------|--------------|
| CEP + Clima | CEP → endereço → temperatura máxima | [cep-clima/README.md](cep-clima/README.md) |
| Demo Spring | Projeto base Spring Boot | [api/demospring01](api/demospring01) |

---

## Solução de problemas

| Problema | Solução |
|----------|---------|
| `Cannot connect to the Docker daemon` | Inicie o Docker Desktop e aguarde ficar pronto |
| `port is already allocated` | Porta 8080 em uso — execute `docker compose down` ou libere a porta |
| Comando falha | Confirme que está dentro da pasta `cep-clima` |

---

## Autores

- Alysson Rychard
- Eduardo Serra
- Fabio Emidio
- Luis Felipe

---

## Licença

Distribuído sob a licença MIT. Consulte [LICENSE](LICENSE).

Instituição: [Faculdade ESUDA](https://esuda.edu.br) · Recife, PE
