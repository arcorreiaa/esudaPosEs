# Problemas conhecidos

Catálogo de erros já enfrentados no projeto, com sintoma, causa e solução. Sempre que alguém esbarrar em um problema novo e resolver, o registro deve ser adicionado aqui.

O documento está dividido em duas partes. A primeira reúne os erros que ainda podem acontecer na máquina de qualquer pessoa, quase sempre por questão de ambiente. A segunda guarda o histórico de defeitos que já foram corrigidos no repositório, útil para quem estiver com um clone antigo e queira entender por que a versão dele se comporta de outro jeito.

---

## Parte 1: erros que podem ocorrer

### 1.1 O Docker não conecta ao daemon

**Sintoma**

```
unable to get image 'cep-clima-api-cep-clima': failed to connect to the docker API at
npipe:////./pipe/dockerDesktopLinuxEngine; check if the path is correct and if the daemon
is running: open //./pipe/dockerDesktopLinuxEngine: O sistema não pode encontrar o arquivo
especificado.
```

No Linux e no macOS a mensagem aparece como `Cannot connect to the Docker daemon`.

**Causa**

O comando `docker` não encontrou o motor do Docker. Ou o Docker Desktop está fechado, ou ainda está inicializando, ou está configurado em modo Windows containers.

Vale reforçar que o build nem chega a começar nesse caso. Nenhuma etapa do projeto é executada.

**Solução**

Abra o Docker Desktop e espere aparecer *Engine running* na barra de status. No Windows, clique com o botão direito no ícone da bandeja e confirme que está em **Linux containers**. Se aparecer a opção *Switch to Linux containers*, clique nela.

Para conferir antes de rodar o projeto:

```bash
docker info
```

Se esse comando responder com as informações do servidor, o daemon está no ar.

---

### 1.2 O Maven Wrapper não inicia

**Sintoma**

```
Get-Content : Não é possível localizar o caminho
'...\cep-clima\backend\.mvn\wrapper\maven-wrapper.properties' porque ele não existe.
No linha:54 caractere:21
...
Cannot start maven from wrapper
```

**Causa**

O `mvnw.cmd` precisa do arquivo `.mvn/wrapper/maven-wrapper.properties` para saber qual versão do Maven baixar. O arquivo está versionado no repositório desde o commit `7bb30e7`, de 15/08/2026. Antes dessa data o `.gitignore` tinha uma regra `.mvn/` que impedia o arquivo de ser enviado, então quem clonou antes disso não recebeu ele.

Em resumo, o clone está desatualizado.

**Solução**

Atualize o repositório:

```bash
git pull origin main
```

Se preferir começar do zero, apague a pasta e clone de novo:

```bash
git clone https://github.com/arcorreiaa/esudaPosEs.git
```

Para confirmar que resolveu, rode dentro de `cep-clima/backend`:

```bash
./mvnw -v          # Linux e macOS
.\mvnw.cmd -v      # Windows
```

A resposta esperada é a versão do Maven, algo como `Apache Maven 3.9.16`.

---

### 1.3 A porta 8080 já está em uso

**Sintoma**

```
Error response from daemon: ... port is already allocated
```

**Causa**

Outro processo ocupa a porta 8080. Pode ser uma execução anterior do próprio projeto que não foi encerrada, ou outra aplicação qualquer.

**Solução**

Se for uma execução antiga do projeto, encerre com:

```bash
cd cep-clima
docker compose down
```

Para descobrir quem está usando a porta:

```powershell
netstat -ano | findstr :8080     # Windows
lsof -i :8080                    # Linux e macOS
```

Se precisar mudar a porta em vez de liberar, altere o mapeamento no `cep-clima/docker-compose.yaml` para `"8081:8080"` e acesse `http://localhost:8081`.

---

### 1.4 Permissão negada ao executar o `mvnw`

**Sintoma**

```
bash: ./mvnw: Permission denied
```

**Causa**

O arquivo perdeu a permissão de execução, o que costuma acontecer ao copiar o projeto entre sistemas de arquivos diferentes.

**Solução**

```bash
cd cep-clima/backend
chmod +x mvnw
```

Só é necessário no Linux e no macOS. No Windows use o `mvnw.cmd`, que não depende disso.

---

### 1.5 A página abre mas não retorna dados

**Sintoma**

A interface carrega normalmente, porém a consulta de CEP falha ou fica sem resposta.

**Causa**

A aplicação depende de três APIs externas: ViaCEP, Nominatim (OpenStreetMap) e Open-Meteo. Se qualquer uma estiver fora do ar, lenta ou bloqueada pela rede, a consulta falha.

O backend tem timeout de 5 segundos para conexão e 10 para leitura, e devolve HTTP 502 quando alguma dessas APIs não responde.

**Solução**

Confira os logs do contêiner, onde a falha aparece identificada por serviço:

```bash
cd cep-clima
docker compose logs -f
```

Teste as APIs diretamente para descobrir qual está com problema:

```bash
curl https://viacep.com.br/ws/50050480/json/
curl "https://nominatim.openstreetmap.org/search?format=jsonv2&q=Recife"
```

Se estiver em rede corporativa ou com VPN, verifique se o acesso a esses domínios está liberado.

---

## Parte 2: problemas já corrigidos

Os itens abaixo não devem mais acontecer em um clone atualizado. Ficam registrados porque explicam o comportamento de versões anteriores e porque documentam decisões de estrutura do projeto.

### 2.1 O build Docker falhava em `COPY backend/.mvn`

**Sintoma**

```
=> ERROR [api-cep-clima build 4/8] COPY backend/.mvn .mvn
failed to solve: failed to compute cache key: ... "/backend/.mvn": not found
```

**Causa**

O Dockerfile usava o Maven Wrapper para compilar, e o `.mvn` não estava versionado por causa da regra `.mvn/` no `.gitignore`.

**Como foi corrigido**

Duas mudanças. Primeiro a regra saiu do `.gitignore` e o wrapper passou a ser versionado, no commit `7bb30e7`. Depois o estágio de build do Dockerfile trocou o wrapper pela imagem `maven:3.9-eclipse-temurin-17`, de forma que o build não dependa mais desses arquivos. Hoje o Dockerfile não tem nenhum `COPY` do `.mvn`.

---

### 2.2 O contêiner ficava marcado como `unhealthy`

**Sintoma**

O `docker compose ps` mostrava `Up X minutes (unhealthy)`, mesmo com a aplicação respondendo normalmente em `http://localhost:8080`.

**Causa**

O healthcheck do `docker-compose.yaml` chama `curl` para bater em `/actuator/health`, mas a imagem `eclipse-temurin:17-jre` não vem com `curl` instalado. O teste falhava sempre, sem relação com o estado real da aplicação.

**Como foi corrigido**

O `curl` passou a ser instalado no estágio final do Dockerfile. O status correto agora é `Up X minutes (healthy)`.

---

### 2.3 A página no Docker era diferente da página local

**Sintoma**

Rodando com `mvnw spring-boot:run` a página tinha as meta tags de SEO e o popup ao clicar no marcador do mapa. Rodando com `docker compose up` essas duas coisas sumiam, e em compensação aparecia um texto diferente no card de resultado.

**Causa**

O `index.html` existia em duas cópias, uma em `cep-clima/frontend/` e outra em `cep-clima/backend/src/main/resources/static/`. O Dockerfile copiava a primeira por cima da segunda durante o build. Como as duas foram editadas em momentos diferentes, cada uma acumulou melhorias que a outra não tinha, e o resultado dependia de como você rodasse o projeto.

**Como foi corrigido**

As duas versões foram mescladas em `static/index.html`, que passou a ser a única fonte. A cópia em `frontend/` foi removida e os `COPY frontend/...` saíram do Dockerfile.

**Como evitar que volte**

Edite sempre `cep-clima/backend/src/main/resources/static/index.html`. Não crie cópias do frontend em outras pastas. O Spring Boot serve essa pasta nativamente, então local e Docker usam o mesmo arquivo.

---

### 2.4 O `mvnw` do `api/demospring01` não iniciava

**Sintoma**

O mesmo `Cannot start maven from wrapper` descrito no item 1.2, porém dentro de `api/demospring01`.

**Causa**

O módulo tinha `mvnw` e `mvnw.cmd` versionados, mas nunca teve o `.mvn/wrapper/maven-wrapper.properties`.

**Como foi corrigido**

O arquivo foi adicionado ao repositório.

---

### 2.5 O `docker build` do `api/demospring01` falhava

**Sintoma**

```
failed to compute cache key: "/dist": not found
```

**Causa**

O Dockerfile do módulo era um `FROM nginx:latest` copiando uma pasta `./dist`, que não existe em projeto Spring Boot e ainda por cima está listada no `.gitignore`.

**Como foi corrigido**

O Dockerfile foi reescrito como build multi-stage Java, no mesmo padrão do `cep-clima`.

---

## Como registrar um problema novo

Ao resolver algo que travou você, acrescente uma entrada na Parte 1 seguindo o formato dos itens acima: sintoma com a mensagem de erro literal, causa e solução com os comandos.

Se o problema for um defeito do projeto e você corrigiu no código, mova o registro para a Parte 2 explicando como foi corrigido, em vez de apagar. O histórico ajuda quem estiver com um clone antigo.
