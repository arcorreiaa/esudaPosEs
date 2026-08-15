# esudaPosEs

Repositório de entregas e laboratórios do curso na **Faculdade ESUDA** — Recife, PE.

[![ESUDA](https://img.shields.io/badge/Faculdade-ESUDA-F19800?style=for-the-badge&logoColor=white)](https://esuda.edu.br)
[![Recife](https://img.shields.io/badge/Recife-PE-61b376?style=for-the-badge&logoColor=white)](https://esuda.edu.br)
[![License](https://img.shields.io/badge/License-MIT-F19800?style=for-the-badge)](LICENSE)

---

## Sobre

Este repositório reúne trabalhos, exemplos e laboratórios desenvolvidos no curso da **Faculdade ESUDA**, organizados por projeto. O principal entregável é o **CEP + Clima**, uma aplicação web e API REST que consulta um CEP brasileiro, localiza o endereço no mapa e retorna a temperatura máxima prevista para o dia.

O repositório também contém um exemplo de Spring Boot, um laboratório sobre condições de corrida e materiais de apoio sobre as APIs externas utilizadas.

---

## Requisitos

| Ferramenta | Obrigatório para |
|------------|------------------|
| [Docker Desktop](https://www.docker.com/products/docker-desktop/) | Rodar o CEP + Clima completo |
| [Java 17](https://adoptium.net/) | Rodar os projetos Java localmente |
| Maven Wrapper | Executar o backend sem Docker |
| Git | Clonar o repositório |

---

## Instalação e execução

```bash
git clone https://github.com/arcorreiaa/esudaPosEs.git
cd esudaPosEs/cep-clima
docker compose up --build
```

Acesse **http://localhost:8080** e teste com o CEP `50050-480` (Recife).

Endpoints disponíveis: `GET /clima/{cep}` e `GET /mapa/{cep}`. O CEP pode ser informado com ou sem hífen.

Para encerrar: `docker compose down`

Documentação detalhada: [cep-clima/README.md](cep-clima/README.md)

---

## Estrutura do repositório

```
esudaPosEs/
├── api/
│   └── demospring01/   # Exemplo Spring Boot
├── cep-clima/          # Entrega CEP + Clima
│   ├── backend/        # API Spring Boot e página servida pela aplicação
│   ├── frontend/       # HTML, CSS, JavaScript e logo
│   └── third-party/    # Documentação das APIs externas
└── docs/               # Diagrama e laboratório de concorrência
```

---

## Entregas

| Projeto | Descrição | Documentação |
|---------|-----------|--------------|
| CEP + Clima | CEP → endereço → coordenadas → temperatura máxima | [cep-clima/README.md](cep-clima/README.md) |
| Demo Spring | Projeto base Spring Boot | [api/demospring01](api/demospring01) |
| Race Condition | Laboratório de contadores compartilhados e concorrência | [docs/Laboratorio-RaceCondition](docs/Laboratorio-RaceCondition) |

---

## Solução de problemas

| Problema | Solução |
|----------|---------|
| `Cannot connect to the Docker daemon` | Inicie o Docker Desktop e aguarde ficar pronto |
| `port is already allocated` | Porta 8080 em uso — execute `docker compose down` ou libere a porta |
| Erro de conexão na página | Execute `docker compose up --build` dentro da pasta `cep-clima` |
| `permission denied` no `mvnw` | Execute `chmod +x mvnw` dentro de `cep-clima/backend` |

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
