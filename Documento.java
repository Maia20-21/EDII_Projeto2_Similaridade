/*
 * Projeto 2 | Verificador de Similaridade de Textos com Hash e AVL
 * Turma 04G
 *
 * Bruna Amorim Maia (RA 10431883)
 * Sofia de Oliveira Cavalcanti (RA 10723361)
 * Vinicius Pereira Rodrigues (RA 10729470)

 */

package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.Set;

/**
 * Representa um arquivo de texto processado
 * Leitura, normalizacao, stemming basico e contagem de palavras
 */
public class Documento {

    private String nomeArquivo;
    private HashTable<String, Integer> frequencias;

    // Stop words carregadas uma unica vez em memoria
    private static Set<String> stopWords = new HashSet<>();

    // Tamanho minimo de token para ser considerado
    private static final int TAMANHO_MINIMO = 3;

    public Documento(String caminhoArquivo) {
        this.nomeArquivo = new java.io.File(caminhoArquivo).getName();
        this.frequencias = new HashTable<>(503); // primo para melhor distribuicao
        try {
            processarArquivo(caminhoArquivo);
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + caminhoArquivo);
            e.printStackTrace();
        }
    }

    public static void carregarStopWords(String caminhoStopWords) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoStopWords))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                stopWords.add(linha.trim().toLowerCase());
            }
        } catch (IOException e) {
            System.err.println("Aviso: nao foi possivel carregar Stop Words de " + caminhoStopWords);
        }
    }

    // Pipeline de processamento do texto
    private void processarArquivo(String caminhoArquivo) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                // 1. Minusculas
                String texto = linha.toLowerCase();

                // 2. Remove acentos (normalizacao Unicode NFD + regex)
                texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
                texto = texto.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

                // 3. Mantem apenas letras, numeros e espacos
                texto = texto.replaceAll("[^a-z0-9 ]", "");

                // 4. Tokenizacao por espacos
                String[] tokens = texto.split("\\s+");

                contarFrequencias(tokens);
            }
        }
    }

    private void contarFrequencias(String[] tokens) {
        for (String token : tokens) {
            // Ignora tokens vazios, curtos demais ou stop words
            if (token.length() < TAMANHO_MINIMO) continue;
            if (stopWords.contains(token)) continue;

            // Aplica stemming basico antes de contar
            String radical = stemmingSimples(token);

            Integer count = frequencias.get(radical);
            if (count == null) {
                frequencias.put(radical, 1);
            } else {
                frequencias.put(radical, count + 1);
            }
        }
    }

    /**
     * Stemming basico para portugues
     * Remove sufixos comuns para agrupar palavras da mesma familia
     * Ex: "programacao" -> "program", "correndo" -> "corr"
     */
    private String stemmingSimples(String palavra) {
        // So aplica em palavras com tamanho suficiente para nao destruir o radical
        if (palavra.length() <= 5) return palavra;

        // Sufixos nominais (do maior para o menor)
        String[] sufixosNominais = {
            "amentos", "imentos", "amento", "imento",
            "acoes", "ucoes", "adores", "edores",
            "acao", "ucao", "ador", "edor",
            "antes", "entes", "ancia", "encia",
            "mente", "avel", "ivel", "oso", "osa",
            "ista", "ismo", "dade", "idade"
        };

        for (String sufixo : sufixosNominais) {
            if (palavra.endsWith(sufixo) && palavra.length() - sufixo.length() >= 3) {
                return palavra.substring(0, palavra.length() - sufixo.length());
            }
        }

        // Sufixos verbais
        String[] sufixosVerbais = {
            "ariam", "eriam", "iriam",
            "ando", "endo", "indo",
            "aram", "eram", "iram",
            "ava", "iam", "ara",
            "ar", "er", "ir"
        };

        for (String sufixo : sufixosVerbais) {
            if (palavra.endsWith(sufixo) && palavra.length() - sufixo.length() >= 3) {
                return palavra.substring(0, palavra.length() - sufixo.length());
            }
        }

        return palavra;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public HashTable<String, Integer> getFrequencias() {
        return frequencias;
    }
}
