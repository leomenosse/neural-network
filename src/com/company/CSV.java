package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class CSV {

    private int[][] dados;
    private int qtdAtributos;
    private int qtdClasses;
    private int linhas;
    private int colunas;

    public CSV(String nomeArquivo){
        readFile(nomeArquivo);
        qtdAtributos = colunas - 1;
        qtdClasses = numberOfDifferentClasses();
    }

    /**
     * Lê o arquivo csv e armazena os valores na matriz "dados". Note que a primeira linha é ignorada
     * pois neste trabalho os arquivos sempre terão cabeçalho
     * @param nomeArquivo Path com o nome do arquivo csv a ser lido
     */
    private void readFile(String nomeArquivo){
        ArrayList<ArrayList<Integer>> arrayList = new ArrayList<>();

        try{
            File arquivo = new File(nomeArquivo);
            Scanner reader = new Scanner(arquivo);
            reader.nextLine(); //pula o cabeçalho

            while(reader.hasNextLine()){
                String[] linha = reader.nextLine().split(",");
                ArrayList<Integer> valoresLinha = new ArrayList<>();

                for (String valor: linha) { //array to array list
                    valoresLinha.add(Integer.parseInt(valor));
                }
                arrayList.add(valoresLinha);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado");
            e.printStackTrace();
        }

        this.dados = toMatrix(arrayList);
    }

    /**
     * Gera uma matriz de inteiros a partir de um ArrayList de ArrayList<Integer>.
     * Além disso, calcula quantas linhas e colunas o csv tem (não considera o cabeçalho)
     * @param arrayList ArrayList de ArrayList<Integer> na forma de uma matriz
     * @return Matriz de inteiros
     */
    private int[][] toMatrix(ArrayList<ArrayList<Integer>> arrayList){
        this.linhas = arrayList.size();
        this.colunas = arrayList.get(0).size();
        int[][] matriz = new int[linhas][colunas];

        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                matriz[i][j] = arrayList.get(i).get(j);
            }
        }

        return matriz;
    }

    /**
     * @return Quantidade de classes diferentes presentes no arquivo
     */
    private int numberOfDifferentClasses(){
        Set<Integer> set = new HashSet<>();

        for (int i = 0; i < linhas; i++) {
            set.add(dados[i][colunas - 1]);
        }

        return set.size();
    }

    public int[][] getDados() {
        return dados;
    }

    public int getQtdAtributos() {
        return qtdAtributos;
    }

    public int getQtdClasses() {
        return qtdClasses;
    }
}
