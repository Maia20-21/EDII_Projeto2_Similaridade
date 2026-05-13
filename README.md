# Projeto Pratico 2 de Estrutura de Dados II
## Verificador de Similaridade de Textos com Hash e AVL

Sistema que identifica o grau de similaridade entre documentos textuais utilizando estruturas de dados manuais: Tabelas Hash (endereçamento aberto) e Arvores AVL.

### Informacoes do Grupo

| Integrante | RA |
| :--- | :--- |
| Bruna Amorim Maia | 10431883 |
| Sofia de Oliveira Cavalcanti | 10723361 |
| Vinicius Pereira Rodrigues | 10729470 |

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

- **`src/`**: Codigo-fonte Java.
- **`documentos/`**: Arquivos `.txt` a serem processados.
- **`recursos/`**: Arquivo auxiliar com lista de stop words.
- **`resultado.txt`**: Arquivo de saida gerado automaticamente com os resultados da analise.

---

### Detalhes da Implementacao

| Componente | Descricao |
| :--- | :--- |
| **Tabela Hash** | Endereçamento aberto com sondagem linear. Funcoes de dispersao: DJB2 e FNV-1a. Redimensionamento automatico (fator de carga > 0.7). |
| **Arvore AVL** | Chave composta (similaridade + nomes dos documentos). Balanceamento com rotacoes simples e duplas. |
| **Metrica** | Indice de Jaccard: \|A ∩ B\| / \|A ∪ B\| |
| **Pre-processamento** | Normalizacao (minusculas + remocao de acentos), filtragem de stop words, filtro de tamanho minimo (3 letras) e stemming basico em portugues. |

---

### Compilacao

```bash
javac src/*.java
```

---

### Execucao

```bash
java -cp src Main <diretorio_documentos> <limiar> <modo> [argumentos_opcionais]
```

#### Modos disponiveis:

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

**Comparar:** Compara dois arquivos especificos:
```bash
java -cp src Main documentos/ 0.0 comparar doc1.txt doc4.txt
```

---

### Saida

O programa imprime os resultados no terminal e salva automaticamente no arquivo `resultado.txt`. Os valores de similaridade sao exibidos em percentual (ex: `72.50%`).
