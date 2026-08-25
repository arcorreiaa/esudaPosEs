# Frontend — Interface web

Camada de apresentação: HTML, CSS e JavaScript.

## Onde estão os arquivos

O frontend é servido pelo próprio JAR do Spring Boot, a partir de:

```
cep-clima/backend/src/main/resources/static/
├── index.html          # Página com campo de CEP, resultado e mapa
└── esuda-logo.png      # Logo ESUDA
```

Edite diretamente esses arquivos. Não é necessário copiar nada.

## Por que esta pasta está vazia

O `index.html` e o `esuda-logo.png` existiam duplicados aqui e em `static/`. O Dockerfile copiava a cópia desta pasta por cima da de `static/`, e as duas versões acabaram divergindo:

- `static/index.html` recebeu as meta tags de SEO e o popup do marker no Leaflet
- `frontend/index.html` recebeu a alteração do texto do resultado

Como o Docker sobrescrevia uma com a outra, **rodar local e rodar no contêiner serviam páginas diferentes**. As duas versões foram mescladas em `static/`, a cópia foi removida e o `COPY` saiu do Dockerfile. Agora existe uma única fonte.

## Como funciona

O Spring Boot serve a página em `http://localhost:8080` e o JavaScript chama a API no mesmo servidor com `fetch('/clima/' + cep)`.

O card de resultado mostra endereço e clima, e usa as coordenadas retornadas pela API para renderizar o mapa com Leaflet. Latitude e longitude não são exibidas no card.

## Testar uma alteração

```bash
cd cep-clima/backend
./mvnw spring-boot:run        # Linux e macOS
.\mvnw.cmd spring-boot:run    # Windows
```

Ou, com Docker, na raiz de `cep-clima`:

```bash
docker compose up --build
```

Os dois caminhos servem exatamente o mesmo arquivo.
