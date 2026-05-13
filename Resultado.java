/*
 * Projeto 2 | Verificador de Similaridade de Textos com Hash e AVL
 * Turma 04G
 *
 * Bruna Amorim Maia (RA 10431883)
 * Sofia de Oliveira Cavalcanti (RA 10723361)
 * Vinicius Pereira Rodrigues (RA 10729470)

 */

package src;

/**
 * DTO que armazena o par de documentos comparados
 * e o grau de similaridade entre eles
 */
public class Resultado {

    private final String doc1;
    private final String doc2;
    private final double similaridade;

    public Resultado(String doc1, String doc2, double similaridade) {
        this.doc1 = doc1;
        this.doc2 = doc2;
        this.similaridade = similaridade;
    }

    public String getDoc1() {
        return doc1;
    }

    public String getDoc2() {
        return doc2;
    }

    public double getSimilaridade() {
        return similaridade;
    }

    // Formato: "docA <-> docB | 85.30%"
    @Override
    public String toString() {
        return doc1 + " <-> " + doc2 + " | " + String.format("%.2f%%", similaridade * 100);
    }
}
