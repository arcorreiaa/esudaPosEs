# Contribuindo

Guia de contribuição do **esudaPosEs** — repositório de entregas e laboratórios do curso na Faculdade ESUDA.

---

## Preparando o ambiente

| Ferramenta | Obrigatório para |
|------------|------------------|
| [Docker Desktop](https://www.docker.com/products/docker-desktop/) | Rodar o CEP + Clima completo |
| [Java 17](https://adoptium.net/) | Rodar os projetos Java sem Docker |
| Git | Clonar o repositório |

```bash
git clone https://github.com/arcorreiaa/esudaPosEs.git
cd esudaPosEs/cep-clima
docker compose up --build
```

Acesse **http://localhost:8080** e teste com o CEP `50050-480` (Recife). Para encerrar: `docker compose down`.

> O Docker Desktop precisa estar aberto e com o status *Engine running* antes de qualquer comando `docker`.

---

## Fluxo de contribuição

1. Faça **fork** de `arcorreiaa/esudaPosEs`.
2. Crie uma branch a partir do `main` **atualizado**:
   ```bash
   git switch main
   git pull origin main
   git switch -c feat/minha-alteracao
   ```
3. Faça as alterações e os commits.
4. Envie para o seu fork e abra um **pull request** para `arcorreiaa/esudaPosEs:main`.

Sempre parta do `main` atualizado. Boa parte dos problemas de "não roda aqui" vem de clone desatualizado.

---

## Padrão de branches

| Prefixo | Quando usar |
|---------|-------------|
| `feat/` | Nova funcionalidade |
| `fix/` | Correção de defeito |
| `docs/` | Documentação e diagramas |
| `build/` | Docker, Maven, configuração de build |

---

## Padrão de commits

Seguimos [Conventional Commits](https://www.conventionalcommits.org/pt-br/): `tipo(escopo): descrição no imperativo`.

```
feat(cep-clima): adiciona endpoint de mapa
fix(cep): rejeita formatos invalidos
docs: documenta arquitetura e valida CEP
build(docker): remove dependencia do maven wrapper
```

Mantenha a descrição curta e no imperativo ("adiciona", não "adicionado"). Um commit por assunto — evite juntar correção e documentação no mesmo commit.

---

## Antes de abrir o pull request

Rode os testes do backend:

```bash
cd cep-clima/backend
./mvnw test          # Linux e macOS
.\mvnw.cmd test      # Windows
```

E valide o build completo:

```bash
cd cep-clima
docker compose up --build
docker compose ps    # a coluna de status deve mostrar "healthy"
```

Descreva no corpo do PR **o que** mudou e **por quê**. Se a alteração afeta o build ou a execução, diga como testar.

---

## Registrando problemas conhecidos

Se você travou em algum erro e conseguiu resolver, registre em [docs/problemas-conhecidos.md](docs/problemas-conhecidos.md) antes de seguir em frente. O formato de cada entrada é sintoma (com a mensagem de erro literal), causa e solução com os comandos.

Isso vale tanto para problemas de ambiente quanto para defeitos do projeto. No segundo caso, depois de corrigir no código, mova o registro para a seção de problemas já corrigidos explicando como foi resolvido, em vez de apagar a entrada. Quem estiver com um clone antigo vai precisar dessa informação.

---

## Estrutura do repositório

| Pasta | Conteúdo |
|-------|----------|
| `cep-clima/` | Entrega principal — API REST e interface web |
| `cep-clima/backend/` | Spring Boot; a interface web fica em `src/main/resources/static/` |
| `cep-clima/third-party/` | Documentação das APIs externas (ViaCEP, Nominatim, Open-Meteo) |
| `api/demospring01/` | Exemplo base de Spring Boot |
| `docs/` | Diagramas, documento de arquitetura e laboratório de concorrência |

### Onde editar a interface web

O frontend é servido pelo próprio JAR do Spring Boot. Edite diretamente:

```
cep-clima/backend/src/main/resources/static/index.html
```

Não crie cópias do `index.html` em outras pastas. O arquivo já existiu duplicado em `cep-clima/frontend/`, e as duas versões divergiram — o Docker servia uma e a execução local servia outra.

---

## Autores

- Alysson Rychard
- Eduardo Serra
- Fabio Emidio
- Luis Felipe

Instituição: [Faculdade ESUDA](https://esuda.edu.br) · Recife, PE
