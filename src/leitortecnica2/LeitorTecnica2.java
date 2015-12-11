/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leitortecnica2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LeitorTecnica2 {

    public List<String> filePaths = new ArrayList<>();

    public static void main(String[] args) {
        LeitorTecnica2 lt2 = new LeitorTecnica2();
        try {
            lt2.leDados("nohup.out");
        } catch (IOException ex) {
            Logger.getLogger(LeitorTecnica2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void leDados(String filePath) throws FileNotFoundException, IOException {
        List<StringBuilder> listaGeracoes = new ArrayList<>();
        String linhaLida;
        int numGeracao = -1;
        int contadorRepeticao = 0;

        try (FileReader fr = new FileReader(filePath); BufferedReader br = new BufferedReader(fr)) {
            while (br.ready()) {
                if (numGeracao == 499) {
                    contadorRepeticao++;
                    if (contadorRepeticao % 10 == 0) {
                        imprime(contadorRepeticao, listaGeracoes);
                        listaGeracoes = new ArrayList<>();
                    }
                    numGeracao = -1;
                }
                linhaLida = br.readLine();
                if (linhaLida.contains("Geracao")) {
                    numGeracao++;
                    atualizaListaGeracoes(listaGeracoes, numGeracao, br);
                }
            }
            br.close();
            fr.close();
        }
    }

    public void imprime(int contadorRepeticao, List<StringBuilder> geracoes) {
        int index;
        deletaArquivoExistente("tec2" + contadorRepeticao);
        for (StringBuilder sb : geracoes) {
            index = sb.lastIndexOf(",");
            sb.replace(index, index + 1, "\n");
            try {
                imprimeArquivo("tec2" + contadorRepeticao + ".csv", sb.toString());
            } catch (IOException ex) {
                Logger.getLogger(LeitorTecnica2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void atualizaListaGeracoes(List<StringBuilder> listaGeracoes, int numGeracao, final BufferedReader br) throws IOException {
        StringBuilder sb;
        try {
            sb = listaGeracoes.get(numGeracao);
        } catch (IndexOutOfBoundsException e) {
            sb = new StringBuilder();
            listaGeracoes.add(sb);
        }
        br.readLine();
        br.readLine();
        br.readLine();
        br.readLine();
        br.readLine();
        String linhaLida = br.readLine();
        linhaLida = formataStringDados(linhaLida);
        sb.append(linhaLida).append(",");
    }

    public String formataStringDados(String linhaLida) {
        linhaLida = linhaLida.replaceAll("[a-zA-Z:\\s]", "");
        String[] dados = linhaLida.split("-");
        linhaLida = dados[0] + "," + dados[1];
        return linhaLida;
    }

    private void deletaArquivoExistente(String newFile) {
        File arquivo = new File(newFile + ".csv");
        if (arquivo.exists()) {
            arquivo.delete();
        }
    }

    private void imprimeArquivo(String fileName, String texto) throws IOException {
        try (FileWriter fw = new FileWriter(fileName, true); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(texto);
            bw.close();
            fw.close();
        }
    }

    public List<String> fileTreePrinter(File initialPath, int initialDepth) {
        int depth = initialDepth++;
        if (initialPath.exists()) {
            File[] contents = initialPath.listFiles();
            for (File content : contents) {
                if (content.isDirectory()) {
                    fileTreePrinter(content, initialDepth + 1);
                } else {
                    if (content.getName().contains("BRST")) {
                        filePaths.add(content.toString());
                    }
                }
            }
        }
        return filePaths;
    }
}
