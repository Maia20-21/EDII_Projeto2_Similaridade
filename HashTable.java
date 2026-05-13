/*
 * Projeto 2 | Verificador de Similaridade de Textos com Hash e AVL
 * Turma 04G
 *
 * Bruna Amorim Maia (RA 10431883)
 * Sofia de Oliveira Cavalcanti (RA 10723361)
 * Vinícius Pereira Rodrigues (RA 10729470)

 */

package src;

import java.util.ArrayList;
import java.util.List;

/**
 * Tabela Hash -> colisoes por Endereçamento Aberto utilizando Sondagem Linear
 * Armazena o vocabulario e a contagem de termos de cada documento processado
 *
 * Fator de carga > 0.7: tabela redimensionada 2x a capacidade
 */

public class HashTable<K, V> {

    // Marcador: posiçoes que tiveram elementos removidos
    //   >> sondagem linear não pare prematuramente ao buscar 
    //      um elemento inserido depois de uma remoçao

    private static final Object APAGADO = new Object();

    private K[] chaves;
    private V[] valores;
    private Object[] marcadores; // guarda APAGADO
    private int tamanho;
    private int capacidade;
    private int funcHashEscolhida = 1;

    private static final double FATOR_CARGA_MAX = 0.7;

    @SuppressWarnings("unchecked")
    public HashTable(int capacidadeInicial) {
        this.capacidade = proximoPrimo(capacidadeInicial);
        this.chaves = (K[]) new Object[capacidade];
        this.valores = (V[]) new Object[capacidade];
        this.marcadores = new Object[capacidade];
        this.tamanho = 0;
    }

    public void setFuncHash(int escolha) {
        this.funcHashEscolhida = escolha;
    }

    // ==================== Funções de Dispersão ====================

    /**
     * Função Hash 1 - DJB2: usa o multiplicador 33 e operação XOR
     */

    private int hashDJB2(K key) {
        String s = key.toString();
        long hash = 5381;
        for (int i = 0; i < s.length(); i++) {
            hash = ((hash << 5) + hash) ^ s.charAt(i); // hash * 33 ^ c
        }
        return Math.abs((int) (hash % capacidade));
    }

    /**
     * Funçao Hash 2 - FNV-1a (Fowler–Noll–Vo): comparação experimental
     * Usa offset basis e prime definidos para 32 bits
     */

    private int hashFNV1a(K key) {
        String s = key.toString();
        int hash = 0x811c9dc5; // offset basis FNV-1a 32-bit
        int prime = 0x01000193; // FNV prime 32-bit
        for (int i = 0; i < s.length(); i++) {
            hash ^= s.charAt(i);
            hash *= prime;
        }
        return Math.abs(hash % capacidade);
    }

    /** Seleciona a funçao de dispersao ativa */
    private int calcularIndice(K key) {
        if (funcHashEscolhida == 1) return hashDJB2(key);
        else return hashFNV1a(key);
    }

    // ==================== Operações Principais ====================

    /**
     * Insere ou atualiza um par chave-valor
     * Utiliza sondagem linear para encontrar posiçao livre em caso de colisao
     * Dispara redimensionamento se o fator de carga exceder o limite
     */

    public void put(K key, V value) {
        // Verifica se precisa expandir a tabela antes de inserir
        if ((double) (tamanho + 1) / capacidade > FATOR_CARGA_MAX) {
            redimensionar();
        }

        int indice = calcularIndice(key);

        // Sondagem linear: percorre posições sequenciais até achar vaga
        while (true) {

            // Posiçao vazia ou marcada como apagada -> pode inserir
            if (chaves[indice] == null || marcadores[indice] == APAGADO) {
                chaves[indice] = key;
                valores[indice] = value;
                marcadores[indice] = null;
                tamanho++;
                return;
            }

            // Chave ja existe -> atualiza o valor
            if (chaves[indice].equals(key)) {
                valores[indice] = value;
                return;
            }

            // Avança para a proxima posiçao (circular)
            indice = (indice + 1) % capacidade;
        }
    }

    /**
     * Busca o valor associado a uma chave
     * Retorna null se a chave não estiver na tabela
     */

    public V get(K key) {
        int indice = calcularIndice(key);

        while (true) {
            // Posiçao completamente vazia -> chave nao existe
            if (chaves[indice] == null && marcadores[indice] != APAGADO) {
                return null;
            }

            // Encontrou a chave (& ≠ apagada)
            if (chaves[indice] != null && marcadores[indice] != APAGADO
                    && chaves[indice].equals(key)) {
                return valores[indice];
            }

            // Continua a sondagem
            indice = (indice + 1) % capacidade;
        }
    }

    /**
     * Retorna todas as chaves ativas da tabela
     * Necessário para o comparador montar o vocabulário unificado
     */
    public List<K> getKeys() {
        List<K> lista = new ArrayList<>();
        for (int i = 0; i < capacidade; i++) {
            if (chaves[i] != null && marcadores[i] != APAGADO) {
                lista.add(chaves[i]);
            }
        }
        return lista;
    }

    public int size() {
        return tamanho;
    }

    // ==================== Redimensionamento ====================

    /**
     * 2x a capacidade e reinsere todos os elementos existentes
     * Fator de carga > 0.7: chamado automaticamente
     */

    @SuppressWarnings("unchecked")
    private void redimensionar() {
        int novaCapacidade = proximoPrimo(capacidade * 2);

        K[] chavesAntigas = chaves;
        V[] valoresAntigos = valores;
        Object[] marcadoresAntigos = marcadores;
        int capacidadeAntiga = capacidade;

        // Reinicializa os arrays internos com a nova capacidade
        this.capacidade = novaCapacidade;
        this.chaves = (K[]) new Object[novaCapacidade];
        this.valores = (V[]) new Object[novaCapacidade];
        this.marcadores = new Object[novaCapacidade];
        this.tamanho = 0;

        // Reinsere cada elemento válido (recalculando os índices)
        for (int i = 0; i < capacidadeAntiga; i++) {
            if (chavesAntigas[i] != null && marcadoresAntigos[i] != APAGADO) {
                put(chavesAntigas[i], valoresAntigos[i]);
            }
        }
    }

    // ==================== Utilitários ====================

    /**
     * Encontra o próximo número primo >= n
     * Usar capacidade prima melhora a distribuição da sondagem linear
     */
    private int proximoPrimo(int n) {
        if (n <= 2) return 2;
        if (n % 2 == 0) n++;
        while (!ehPrimo(n)) {
            n += 2;
        }
        return n;
    }

    private boolean ehPrimo(int n) {
        if (n < 2) return false;
        if (n == 2 || n == 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }
}