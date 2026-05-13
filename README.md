# Projeto Prático 2 de Estrutura de Dados II
## Verificador de Similaridade de Textos com Hash e AVL

Sistema que identifica o grau de similaridade entre documentos textuais utilizando estruturas de dados manuais: Tabelas Hash (endereçamento aberto) e Árvores AVL.

### Informações do Grupo

| Integrante | RA |
| :--- | :--- |
| Bruna Amorim Maia | 10431883 |
| Sofia de Oliveira Cavalcanti | 10723361 |
| Vinícius Pereira Rodrigues | 10729470 |

**Turma:** `04G`

---

### Estrutura do Projeto

```
/ed2-proj2/
│
├── README.md
│
├── documentos/
│   ├── doc1.txt
│   └── doc2.txt
│
├── recursos/
│   └── stopwords_pt.txt
│
├── src/
│   ├── Main.java
│   ├── Documento.java
│   ├── HashTable.java
│   ├── AVLTree.java
│   ├── ComparadorDeDocumentos.java
│   └── Resultado.java
│
└── resultado.txt
```

- **`src/`**: Código-fonte Java.
- **`documentos/`**: Arquivos `.txt` a serem processados.
- **`recursos/`**: Arquivo auxiliar com lista de stop words.
- **`resultado.txt`**: Arquivo de saída gerado automaticamente com os resultados da análise.

---

### Detalhes da Implementação

| Componente | Descrição |
| :--- | :--- |
| **Tabela Hash** | Endereçamento aberto com sondagem linear. Funções de dispersão: DJB2 e FNV-1a. Redimensionamento automático (fator de carga > 0.7). |
| **Árvore AVL** | Chave composta (similaridade + nomes dos documentos). Balanceamento com rotações simples e duplas. |
| **Métrica** | Índice de Jaccard: \|A ∩ B\| / \|A ∪ B\| |
| **Pré-processamento** | Normalização (minúsculas + remoção de acentos), filtragem de stop words, filtro de tamanho mínimo (3 letras) e stemming básico em português. |

---

### Compilação

```bash
javac src/*.java
```

---

### Execução

```bash
java -cp src Main <diretorio_documentos> <limiar> <modo> [argumentos_opcionais]
```

#### Modos disponíveis:

**Lista:** Compara todos os arquivos e mostra pares com similaridade acima do limiar:
```bash
java -cp src Main documentos/ 0.0 lista
```
```bash
java -cp src Main documentos/ 0.5 lista
```

**Ranking:** Mostra os K pares mais similares:
```bash
java -cp src Main documentos/ 0.0 ranking 5
```

**Comparar:** Compara dois arquivos específicos:
```bash
java -cp src Main documentos/ 0.0 comparar doc1.txt doc4.txt
```

---

### Saída

O programa imprime os resultados no terminal e salva automaticamente no arquivo `resultado.txt`. Os valores de similaridade são exibidos em percentual (ex: `72.50%`).
