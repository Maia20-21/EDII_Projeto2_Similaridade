/*
 * Projeto 2 | Verificador de Similaridade de Textos com Hash e AVL
 * Turma 04G
 *
 * Bruna Amorim Maia (RA 10431883)
 * Sofia de Oliveira Cavalcanti (RA 10723361)
 * Vinicius Pereira Rodrigues (RA 10729470)

 */

package src;

import java.util.List;
import java.util.ArrayList;

/**
 * Arvore AVL para ordenacao dos resultados de similaridade
 * Chave composta: similaridade (double) + nomes dos documentos (String)
 * Cada no armazena exatamente um resultado (sem listas internas)
 */
public class AVLTree {

    private class Node {
        double similaridade;
        String chaveComposta; // "docA|docB" para desempate
        Resultado resultado;
        int altura;
        Node esq;
        Node dir;

        Node(Resultado res) {
            this.similaridade = res.getSimilaridade();
            this.chaveComposta = res.getDoc1() + "|" + res.getDoc2();
            this.resultado = res;
            this.altura = 1;
        }
    }

    private Node raiz;

    // Contadores de rotacoes para analise experimental
    private int rotSimplesEsq = 0;
    private int rotSimplesDir = 0;
    private int rotDuplaEsq = 0;
    private int rotDuplaDir = 0;

    // --- Altura e Balanceamento ---

    private int altura(Node n) {
        return (n == null) ? 0 : n.altura;
    }

    private void atualizarAltura(Node n) {
        n.altura = 1 + Math.max(altura(n.esq), altura(n.dir));
    }

    // FB = Esquerda - Direita (invertido em relacao ao projeto anterior)
    private int fatorBalanceamento(Node n) {
        if (n == null) return 0;
        return altura(n.esq) - altura(n.dir);
    }

    // --- Rotacoes ---

    // Rotacao simples a esquerda (desbalanceamento a direita)
    private Node girarEsquerda(Node z) {
        Node y = z.dir;
        Node temp = y.esq;

        y.esq = z;
        z.dir = temp;

        atualizarAltura(z);
        atualizarAltura(y);

        rotSimplesEsq++;
        return y;
    }

    // Rotacao simples a direita (desbalanceamento a esquerda)
    private Node girarDireita(Node z) {
        Node y = z.esq;
        Node temp = y.dir;

        y.dir = z;
        z.esq = temp;

        atualizarAltura(z);
        atualizarAltura(y);

        rotSimplesDir++;
        return y;
    }

    // Rotacao dupla esquerda-direita
    private Node girarEsqDir(Node z) {
        z.esq = girarEsquerda(z.esq);
        rotDuplaEsq++;
        return girarDireita(z);
    }

    // Rotacao dupla direita-esquerda
    private Node girarDirEsq(Node z) {
        z.dir = girarDireita(z.dir);
        rotDuplaDir++;
        return girarEsquerda(z);
    }

    // --- Comparacao de chaves ---

    /**
     * Compara dois nos pela chave composta:
     * primeiro por similaridade, depois por nome dos documentos (desempate)
     * Retorno negativo = a < b, positivo = a > b, zero = iguais
     */
    private int comparar(double simA, String chaveA, double simB, String chaveB) {
        if (simA < simB) return -1;
        if (simA > simB) return 1;
        // Mesma similaridade -> desempata pelo nome dos documentos
        return chaveA.compareTo(chaveB);
    }

    // --- Insercao ---

    public void insert(Resultado res) {
        this.raiz = inserir(raiz, res);
    }

    private Node inserir(Node node, Resultado res) {
        if (node == null) {
            return new Node(res);
        }

        double novaSim = res.getSimilaridade();
        String novaChave = res.getDoc1() + "|" + res.getDoc2();

        int cmp = comparar(novaSim, novaChave, node.similaridade, node.chaveComposta);

        if (cmp < 0) {
            node.esq = inserir(node.esq, res);
        } else if (cmp > 0) {
            node.dir = inserir(node.dir, res);
        } else {
            // Chave identica (mesmo par, mesma similaridade) -> ignora duplicata
            return node;
        }

        atualizarAltura(node);
        int fb = fatorBalanceamento(node);

        // Caso Esquerdo-Esquerdo
        if (fb > 1 && comparar(novaSim, novaChave, node.esq.similaridade, node.esq.chaveComposta) < 0) {
            return girarDireita(node);
        }

        // Caso Direito-Direito
        if (fb < -1 && comparar(novaSim, novaChave, node.dir.similaridade, node.dir.chaveComposta) > 0) {
            return girarEsquerda(node);
        }

        // Caso Esquerdo-Direito
        if (fb > 1 && comparar(novaSim, novaChave, node.esq.similaridade, node.esq.chaveComposta) > 0) {
            return girarEsqDir(node);
        }

        // Caso Direito-Esquerdo
        if (fb < -1 && comparar(novaSim, novaChave, node.dir.similaridade, node.dir.chaveComposta) < 0) {
            return girarDirEsq(node);
        }

        return node;
    }

    // --- Travessias ---

    // Ordem decrescente (maior similaridade primeiro)
    // Percurso: Direita -> Raiz -> Esquerda
    public List<Resultado> getResultadosEmOrdemDecrescente() {
        List<Resultado> lista = new ArrayList<>();
        percorrerDecrescente(raiz, lista);
        return lista;
    }

    private void percorrerDecrescente(Node node, List<Resultado> lista) {
        if (node == null) return;
        percorrerDecrescente(node.dir, lista);
        lista.add(node.resultado);
        percorrerDecrescente(node.esq, lista);
    }

    // Ordem crescente (menor similaridade primeiro)
    public List<Resultado> getResultadosEmOrdemCrescente() {
        List<Resultado> lista = new ArrayList<>();
        percorrerCrescente(raiz, lista);
        return lista;
    }

    private void percorrerCrescente(Node node, List<Resultado> lista) {
        if (node == null) return;
        percorrerCrescente(node.esq, lista);
        lista.add(node.resultado);
        percorrerCrescente(node.dir, lista);
    }

    // Getters dos contadores
    public int getContSimplesEsquerda() { return rotSimplesEsq; }
    public int getContSimplesDireita() { return rotSimplesDir; }
    public int getContDuplaEsquerda() { return rotDuplaEsq; }
    public int getContDuplaDireita() { return rotDuplaDir; }
}
