/*
 * Projeto 2 | Verificador de Similaridade de Textos com Hash e AVL
 * Turma 04G
 *
 * Bruna Amorim Maia (RA 10431883)
 * Sofia de Oliveira Cavalcanti (RA 10723361)
 * Vinícius Pereira Rodrigues (RA 10729470)

 */

package src;

import java.util.HashSet;
import java.util.Set;

/**
 * Logica matematica da comparaçao entre documentos
 * Implementa a metrica de Similaridade de Jaccard
 */

public class ComparadorDeDocumentos {

    /**
     * Indice de Jaccard: |A ∩ B| / |A ∪ B|
     * Resultado 1.0 = vocabularios identicos
     * Resultado 0.0 = nenhuma palavra em comum
     */
    public static double calcularSimilaridade(Documento d1, Documento d2) {
        HashTable<String, Integer> freq1 = d1.getFrequencias();
        HashTable<String, Integer> freq2 = d2.getFrequencias();

        // Monta os conjuntos de palavras de cada documento
        Set<String> palavrasA = new HashSet<>(freq1.getKeys());
        Set<String> palavrasB = new HashSet<>(freq2.getKeys());

        // Conta quantas palavras aparecem em ambos (interseçao)
        int intersecao = 0;
        for (String palavra : palavrasA) {
            if (palavrasB.contains(palavra)) {
                intersecao++;
            }
        }

        // Uniao = |A| + |B| - |A ∩ B|
        int uniao = palavrasA.size() + palavrasB.size() - intersecao;

        // Evita divisao por zero (documentos vazios)
        if (uniao == 0) {
            return 0.0;
        }

        return (double) intersecao / uniao;
    }
}