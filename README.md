# Sistema de Estoque, Compras e Caixa

Trabalho A03 — Matemática Computacional / Lógica e Algoritmos
Professor: Daniel Henrique Matos de Paiva

## Integrantes

| Nome | RA |
|---|---|
| Lucas Diniz Silva | 326123671 |
| Bianca Eduarda Ferreira | 32616059 |
| Luis Gustavo Souza Pereira | 32622795 |

## O problema

Um pequeno mercado precisa controlar seus produtos, o estoque, a fila de clientes no caixa e as compras.
O programa simula esse fluxo no terminal: cadastrar produtos, organizar por departamento, atender clientes
em ordem de chegada, montar o carrinho (podendo desfazer operações), calcular o troco, atualizar o estoque
e descobrir quais produtos costumam ser comprados juntos.

Toda a lógica das estruturas de dados foi implementada manualmente (sem `ArrayList`, `LinkedList`,
`Stack`, `Queue` ou `HashMap` do Java), para que o raciocínio de cada estrutura fique visível.

## Como executar

Requisito: Java 17 ou superior (testado com Java 26).

**Windows (recomendado para a apresentação):**

```
run.bat
```

**Manualmente:**

```
javac -encoding UTF-8 -d out src/market/*.java
java -Dstdout.encoding=UTF-8 -cp out market.Main
```

O sistema já inicia com 12 produtos, 6 departamentos, relações de compra no grafo e 3 clientes na fila.
A opção **10 (Simulação automática)** executa o exemplo prático completo de uma vez.

## Estrutura do código

| Arquivo | Estrutura / responsabilidade |
|---|---|
| `ProductCatalog.java` | **Array** ordenado com códigos e preços + busca binária |
| `StockLinkedList.java` | **Lista encadeada** simples com os produtos do estoque |
| `OperationStack.java` | **Pilha** (encadeada) das operações do carrinho |
| `CartOperation.java` | Registro de uma operação (adição/remoção) empilhada |
| `Cart.java` | Carrinho de compras: itens em array + pilha para desfazer |
| `CustomerQueue.java` | **Fila** (encadeada) de clientes aguardando o caixa |
| `ProductGraph.java` | **Grafo** ponderado com matriz de adjacência |
| `CategoryTree.java` | **Árvore** genérica: Mercado → Departamento → Categoria → Produto |
| `Money.java` | Formatação monetária e decomposição do troco |
| `Market.java` | Reúne todas as estruturas e mantém a consistência entre elas |
| `Cashier.java` | Fluxo do caixa: atendimento, carrinho, pagamento, simulação |
| `InputReader.java` | Leitura e validação de todas as entradas do usuário |
| `Main.java` | Menu principal |

## Modelagem matemática e lógica

### 1. Array — tabela de códigos e preços (`ProductCatalog`)

Os códigos e preços ficam em dois vetores paralelos `codes[i]` e `prices[i]`, de capacidade fixa (50).
O vetor é mantido **ordenado pelo código**: na inserção, os elementos maiores são deslocados uma posição
para a direita (como no *insertion sort*), e o novo produto entra na posição correta.

Com o vetor ordenado, a busca usa **busca binária**: a cada passo compara-se o elemento do meio,
`meio = (inicio + fim) / 2`, e metade do intervalo é descartada. Como o intervalo é dividido por 2 a cada
iteração, o número máximo de comparações é ⌊log₂ n⌋ + 1. Para 50 produtos, no máximo 6 comparações,
contra até 50 em uma busca sequencial.

- Inserção: O(n) (deslocamento)
- Busca: O(log n)
- Tratamento de limites: o cadastro é bloqueado quando `size == capacidade`.

### 2. Lista encadeada — estoque (`StockLinkedList`)

Cada nó guarda um produto e a referência para o próximo nó. A lista mantém ponteiros para o
**início (head)** e o **fim (tail)**, então cadastrar um produto novo no fim é O(1).
Percorrendo a lista calculamos:

- Total de unidades: Σ quantidadeᵢ
- Valor total do estoque: Σ (quantidadeᵢ × preçoᵢ)
- Produtos com estoque baixo (quantidade ≤ 5)

### 3. Pilha — desfazer operações do carrinho (`OperationStack` + `Cart`)

Toda adição ou remoção no carrinho é empilhada (LIFO: *último a entrar, primeiro a sair*).
Ao desfazer, a operação do topo é desempilhada e a **operação inversa** é aplicada:

- desfazer uma ADIÇÃO de *q* unidades → remove *q* unidades
- desfazer uma REMOÇÃO de *q* unidades → devolve *q* unidades

Como cada operação é a inversa da outra, desfazer *k* vezes seguidas devolve o carrinho exatamente ao
estado de *k* operações atrás. `push` e `pop` são O(1).

### 4. Fila — clientes aguardando atendimento (`CustomerQueue`)

Fila encadeada com ponteiros de **frente** e **fim** (FIFO: *primeiro a entrar, primeiro a sair*).
Cada cliente recebe uma senha sequencial. `enqueue` insere no fim e `dequeue` retira da frente, ambos
O(1). Depois de cada compra o sistema mostra o próximo da fila e pergunta se ele deve ser chamado.

### 5. Grafo — produtos comprados juntos (`ProductGraph`)

Modelamos um **grafo não direcionado e ponderado** G = (V, E):

- **Vértices (V):** os produtos.
- **Arestas (E):** existe aresta entre dois produtos se eles já foram comprados juntos.
- **Peso w(u, v):** quantas vezes o par foi comprado junto.

A representação é uma **matriz de adjacência** `weights[n][n]`. Como o grafo é não direcionado, a
matriz é **simétrica**: `weights[i][j] = weights[j][i]`, e a diagonal principal não é usada
(sem laços).

**Combinatória aplicada:** a cada compra finalizada com *k* produtos diferentes, todos os pares possíveis
são reforçados. O número de pares é a combinação

  C(k, 2) = k! / (2! (k − 2)!) = k(k − 1) / 2

Exemplo: uma compra com 3 produtos distintos gera C(3,2) = 3 relações; com 1 produto, C(1,2) = 0.
Esse valor aparece no cupom como "Relações registradas no grafo".

Outras métricas calculadas:

- **Grau do vértice:** número de produtos diferentes que já foram comprados junto com ele.
- **Número máximo de arestas:** n(n − 1)/2.
- **Densidade:** |E| / (n(n − 1)/2), entre 0 (nenhuma relação) e 1 (todos relacionados com todos).
- **Recomendação:** os vizinhos de um produto, ordenados pelo peso da aresta em ordem decrescente
  (*selection sort*). É a base de sistemas de recomendação do tipo "quem comprou isto também comprou…".

### 6. Árvore — departamentos e categorias (`CategoryTree`)

Árvore genérica (n-ária) com 4 níveis:

```
Mercado (raiz, nível 0)
└── Departamento (nível 1)
    └── Categoria (nível 2)
        └── Produto (folha, nível 3)
```

Usamos a representação **primeiro filho / próximo irmão**: cada nó aponta para o primeiro filho e para o
próximo irmão, o que permite qualquer quantidade de filhos sem vetor de tamanho fixo. Os filhos são
inseridos em **ordem alfabética**.

As operações são **recursivas**:

- **Impressão:** percurso em pré-ordem (visita o nó e depois cada filho), com recuo proporcional à profundidade.
- **Contagem de produtos:** produtos(nó) = (1 se o nó é folha-produto, senão 0) + Σ produtos(filhoᵢ).
- **Altura:** altura(nó) = 0 se não tem filhos; senão 1 + máx(altura(filhoᵢ)).

### 7. Cálculo do troco (`Money`)

Os valores são guardados em **centavos inteiros** (R$ 28,99 → 2899). Assim evitamos o erro de
arredondamento do ponto flutuante (em `double`, 0,1 + 0,2 ≠ 0,3).

Troco = valor pago − total. A decomposição em cédulas e moedas usa o **algoritmo guloso**: para cada
valor, do maior ao menor (R$ 200, 100, 50, 20, 10, 5, 2, 1, 0,50, 0,25, 0,10, 0,05, 0,01):

  quantidade = troco ÷ valor (divisão inteira)
  troco = troco mod valor (resto)

Para o sistema monetário brasileiro (um sistema *canônico*), a estratégia gulosa sempre fornece o menor
número possível de cédulas e moedas. Como existe a moeda de R$ 0,01, o resto final é sempre zero.

Exemplo da simulação: total R$ 62,44, pago R$ 100,00 → troco R$ 37,56 =
20 + 10 + 5 + 2 + 0,50 + 0,05 + 0,01.

## Tratamento de erros e consistência dos dados

| Situação | Tratamento |
|---|---|
| Texto onde se espera número | Mensagem de erro e nova leitura (`NumberFormatException`) |
| Número fora do intervalo (opção de menu, código, quantidade) | Intervalo mínimo/máximo validado |
| Valor monetário mal formatado ou com mais de 2 casas | Validado por expressão regular; aceita `12,50` ou `12.50` |
| Campo de texto vazio ou muito longo | Rejeitado |
| Código de produto duplicado | Cadastro recusado |
| Catálogo cheio (limite do array) | Cadastro bloqueado |
| Carrinho cheio (limite do array) | Adição recusada |
| Quantidade maior que o estoque (considerando o que já está no carrinho) | Recusada |
| Desfazer com a pilha vazia | Mensagem "Não há operações para desfazer" |
| Atender com a fila vazia | Mensagem "Não há clientes na fila" |
| Pagamento menor que o total | Recusado, informa quanto falta |
| Finalizar com carrinho vazio | Recusado |
| Reposição acima do limite de estoque | Recusada |
| Índice inválido no carrinho | `IndexOutOfBoundsException` com mensagem |
| Divisão por zero na densidade do grafo | Verificado antes (`possibleEdges == 0`) |
| Venda consistente | O estoque de **todos** os itens é verificado antes de baixar qualquer um; o carrinho só é baixado no estoque ao finalizar (cancelar não altera nada) |
| Fim da entrada (Ctrl+Z / Ctrl+D) | Encerra o sistema sem travar |

## Roteiro sugerido para a demonstração

1. **Opção 4:** mostrar o array ordenado de códigos e preços.
2. **Opção 5 → 1:** mostrar a árvore de departamentos (altura e contagem recursiva).
3. **Opção 7:** mostrar a fila de clientes.
4. **Opção 9:** mostrar a matriz de adjacência do grafo.
5. **Opção 10:** simulação completa: chama o cliente da fila, monta o carrinho, desfaz o último item
   (pilha), calcula o troco, atualiza o estoque e chama o próximo cliente.
6. **Opção 9 de novo:** mostrar que os pesos das arestas aumentaram (ex.: Arroz–Feijão foi de 10 para 11).
7. **Opção 8:** atendimento manual, digitando entradas inválidas de propósito para mostrar a validação.
