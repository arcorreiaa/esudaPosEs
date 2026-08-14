# Race Condition em Java

Esse código mostra como funciona uma race condition em Java. A ideia é simples: 10 threads incrementam o mesmo contador 100.000 vezes cada uma, então o valor esperado é 1.000.000.

## Sem sincronização

Cada thread faz `contador++` sem proteção. Como esse passo não é atômico, as threads podem ler, somar e escrever ao mesmo tempo, e alguns incrementos acabam sendo perdidos. Por isso, o resultado pode variar de execução para execução.

## Com sincronização

Aqui o incremento fica dentro de um bloco `synchronized`. Só uma thread entra por vez, então o contador cresce corretamente e o valor final fica igual ao esperado.

## Como executar

```bash
javac ContadorThreads.java
java ContadorThreads
```

## Exemplo de saída

```text
=== TESTE SEM SINCRONIZAÇÃO ===
Valor esperado: 1000000
Valor obtido : 1000000

=== TESTE COM SINCRONIZAÇÃO ===
Valor esperado: 1000000
Valor obtido : 1000000
```

> O importante aqui é que, sem sincronização, o resultado não é garantido. Em algumas execuções pode dar menor que 1.000.000, em outras pode até dar exatamente o valor esperado, porque depende do escalonamento das threads.

Essa foi uma forma bem prática de perceber que, em programas multithread, compartilhar dados sem controle pode gerar resultados inconsistentes.