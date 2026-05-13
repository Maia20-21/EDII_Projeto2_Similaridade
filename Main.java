/*
 * Projeto 2 | Verificador de Similaridade de Textos com Hash e AVL
 * Turma 04G
 *
 * Bruna Amorim Maia (RA 10431883)
 * Sofia de Oliveira Cavalcanti (RA 10723361)
 * Vinicius Pereira Rodrigues (RA 10729470)

 */

package src;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe principal
 * Fluxo: leitura -> comparacao -> insercao na AVL -> saida
 */
public class Main {

    public static void main(String[] args) {
        // === Validacao dos argumentos ===
        if (args.length < 3) {
            System.out.println("Uso: java Main <diretorio> <limiar> <modo> [opcionais]");
            System.out.println("Modos: lista, ranking <k>, comparar <doc1> <doc2>");
            return;
        }

        String diretorioDocs = args[0];
        double limiar;
        try {
            limiar = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Erro: limiar deve ser um numero decimal (ex: 0.7)");
            return;
        }
        String modo = args[2].toLowerCase();

        StringBuilder relatorio = new StringBuilder();

        try {
            // === Carregamento das Stop Words ===
            File stopWordsFile = new File("recursos/stopwords_pt.txt");
            if (stopWordsFile.exists()) {
                Documento.carregarStopWords(stopWordsFile.getPath());
            } else {
                System.out.println("AVISO: recursos/stopwords_pt.txt nao encontrado");
            }

            // === Leitura dos documentos ===
            List<Documento> documentos = carregarDocumentos(diretorioDocs);
            if (documentos.size() < 2) {
                System.out.println("Erro: minimo 2 documentos para comparacao");
                return;
            }

            // === Execucao por modo ===

            // --- MODO COMPARAR (dois arquivos especificos) ---
            if (modo.equals("comparar")) {
                if (args.length < 5) {
                    System.out.println("Erro: modo comparar requer dois nomes de arquivo");
                    return;
                }
                String nomeDoc1 = args[3];
                String nomeDoc2 = args[4];

                Documento d1 = buscarDocumento(documentos, nomeDoc1);
                Documento d2 = buscarDocumento(documentos, nomeDoc2);

                if (d1 != null && d2 != null) {
                    double sim = ComparadorDeDocumentos.calcularSimilaridade(d1, d2);
                    relatorio.append("========================================\n");
                    relatorio.append("  VERIFICADOR DE SIMILARIDADE\n");
                    relatorio.append("========================================\n");
                    relatorio.append("Par: " + d1.getNomeArquivo() + " <-> " + d2.getNomeArquivo() + "\n");
                    relatorio.append("Similaridade: " + String.format("%.2f%%", sim * 100) + "\n");
                    relatorio.append("Metrica: Jaccard\n");
                    relatorio.append("========================================\n");
                } else {
                    relatorio.append("Erro: um ou ambos os documentos nao foram encontrados\n");
                }

            } else {
                // --- MODO LISTA ou RANKING (todos contra todos) ---

                AVLTree avl = new AVLTree();
                int totalPares = 0;

                // Compara cada par unico de documentos
                for (int i = 0; i < documentos.size(); i++) {
                    for (int j = i + 1; j < documentos.size(); j++) {
                        Documento d1 = documentos.get(i);
                        Documento d2 = documentos.get(j);

                        double sim = ComparadorDeDocumentos.calcularSimilaridade(d1, d2);
                        Resultado res = new Resultado(d1.getNomeArquivo(), d2.getNomeArquivo(), sim);
                        avl.insert(res);
                        totalPares++;
                    }
                }

                // Cabecalho do relatorio
                relatorio.append("========================================\n");
                relatorio.append("  VERIFICADOR DE SIMILARIDADE\n");
                relatorio.append("========================================\n");
                relatorio.append("Documentos processados: " + documentos.size() + "\n");
                relatorio.append("Pares comparados: " + totalPares + "\n");
                relatorio.append("Metrica: Jaccard\n");

                // Estatisticas da AVL
                relatorio.append("\n--- Rotacoes da AVL ---\n");
                relatorio.append("Simples Esquerda: " + avl.getContSimplesEsquerda() + "\n");
                relatorio.append("Simples Direita:  " + avl.getContSimplesDireita() + "\n");
                relatorio.append("Dupla Esquerda:   " + avl.getContDuplaEsquerda() + "\n");
                relatorio.append("Dupla Direita:    " + avl.getContDuplaDireita() + "\n");
                relatorio.append("----------------------------------------\n\n");

                List<Resultado> resultados = avl.getResultadosEmOrdemDecrescente();

                if (modo.equals("lista")) {
                    relatorio.append("Pares com similaridade >= " + String.format("%.0f%%", limiar * 100) + ":\n\n");
                    int pos = 1;
                    for (Resultado r : resultados) {
                        if (r.getSimilaridade() >= limiar) {
                            relatorio.append("  " + pos + ". " + r.toString() + "\n");
                            pos++;
                        }
                    }

                } else if (modo.equals("ranking")) {
                    int k = 5;
                    if (args.length >= 4) {
                        try {
                            k = Integer.parseInt(args[3]);
                        } catch (NumberFormatException e) {
                            System.out.println("Aviso: valor de K invalido, usando padrao 5");
                        }
                    }
                    relatorio.append("Top " + k + " pares mais semelhantes (>= " + String.format("%.0f%%", limiar * 100) + "):\n\n");
                    int count = 0;
                    for (Resultado r : resultados) {
                        if (r.getSimilaridade() >= limiar) {
                            count++;
                            relatorio.append("  " + count + ". " + r.toString() + "\n");
                        }
                        if (count >= k) break;
                    }
                }

                relatorio.append("\n========================================\n");
            }

            // === Saida ===
            String outputFinal = relatorio.toString();
            System.out.print(outputFinal);

            try (PrintWriter out = new PrintWriter(new FileWriter("resultado.txt"))) {
                out.print(outputFinal);
                System.out.println("(Resultado salvo em 'resultado.txt')");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Carrega todos os .txt do diretorio informado
    private static List<Documento> carregarDocumentos(String diretorio) {
        List<Documento> lista = new ArrayList<>();
        File pasta = new File(diretorio);
        File[] arquivos = pasta.listFiles();

        if (arquivos != null) {
            for (File arq : arquivos) {
                if (arq.isFile() && arq.getName().endsWith(".txt")) {
                    lista.add(new Documento(arq.getPath()));
                }
            }
        }
        return lista;
    }

    // Busca documento pelo nome na lista carregada
    private static Documento buscarDocumento(List<Documento> docs, String nome) {
        for (Documento d : docs) {
            if (d.getNomeArquivo().equals(nome)) {
                return d;
            }
        }
        return null;
    }
}
