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
| Erro no build: `backend/.mvn` não encontrado | Execute `mvn wrapper:wrapper` dentro de `cep-clima/backend` e tente novamente |
| `permission denied` no `mvnw` | Execute `chmod +x mvnw` dentro de `cep-clima/backend` |

---

## Autores

- Alysson Rychard
- Eduardo Serra
- Fabio Emidio
- Luis Felipe

---

## Contribuições por integrante

Resumo técnico do que cada membro entregou no repositório, para conferência na revisão do trabalho.

### Alysson Rychard

Estrutura inicial do repositório e implementação base do projeto **CEP + Clima**.

- Criou o backend Spring Boot (`CepClimaApplication`, `ClimaController`, `ClimaService`, `ApiExceptionHandler`, `WebConfig`) com integração às APIs ViaCEP, Nominatim e Open-Meteo.
- Desenvolveu o frontend (`index.html` com CSS, JavaScript e mapa Leaflet) e a documentação das APIs externas em `cep-clima/third-party/`.
- Configurou Docker (`Dockerfile` multi-estágio, `docker-compose.yaml`) e Maven Wrapper.
- Refatorou a arquitetura para servir API e interface no mesmo JAR Spring Boot, eliminando o contêiner nginx separado.
- Organizou o repositório com o exemplo `api/demospring01`, o laboratório `docs/Laboratorio-RaceCondition` e o diagrama `docs/Alysson.drawio`.

### Eduardo Serra

Refatoração do backend e laboratório de concorrência.

- Extraiu `MapaService` de `ClimaService`, concentrando validação de CEP, consulta ao ViaCEP e geocodificação via Nominatim.
- Criou `MapaController` com o endpoint `GET /mapa/{cep}` e adaptou `ClimaService` para reutilizar `MapaService`.
- Melhorou o frontend (validação de CEP e exibição do mapa) em `frontend/index.html` e `static/index.html`.
- Implementou o laboratório de race condition (`docs/Eduardo_Race_Condition/contador/ContadorThreads.java`) demonstrando incremento concorrente com e sem `synchronized`.
- Produziu o diagrama de arquitetura `docs/eduardo_cadiz_cepclima.drawio`.

### Luis Felipe

Documentação de arquitetura e testes automatizados.

- Escreveu `docs/arquitetura.md` — documento técnico com visão geral, componentes, fluxo de requisição, integrações externas, tratamento de erros, build Docker e limitações conhecidas.
- Criou quatro diagramas draw.io exportados em PNG: contexto, componentes, fluxo `GET /clima/{cep}` e pipeline de build (`docs/Felipe.drawio`).
- Adicionou testes unitários de validação de CEP em `MapaServiceTest` (formatos válidos e inválidos).
- Configurou Maven Wrapper (`.mvn/wrapper/maven-wrapper.properties`) e alinhou validação de CEP entre frontend e backend.

### Fabio Emidio

Tipagem, resiliência e observabilidade do backend.

- Substituiu `Map<String, Object>` por cinco Java Records (`EnderecoDto`, `CoordenadasDto`, `ClimaDto`, `MapaResponse`, `ClimaResponse`), garantindo type-safety nas respostas da API.
- Criou `RestClientConfig` com timeouts centralizados (5 s conexão, 10 s leitura) e injetou `RestClient.Builder` nos serviços.
- Habilitou CORS para `/mapa/**`, adicionou logging SLF4J nos serviços e integrou Spring Boot Actuator com health check no `docker-compose.yaml`.
- Melhorou o frontend com meta tags SEO e popup no marker do Leaflet.
- Documentou as alterações em `docs/mudanca-fabio.md` e diagrama `docs/fabio.drawio`.

---

## Licença

Distribuído sob a licença MIT. Consulte [LICENSE](LICENSE).

Instituição: [Faculdade ESUDA](https://esuda.edu.br) · Recife, PE
