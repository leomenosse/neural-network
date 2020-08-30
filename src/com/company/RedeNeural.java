package com.company;

public class RedeNeural {

    private CSV arquivo;
    private Matrix entrada;
    private Matrix pesosEntradaOculta;
    private Matrix netOculta;
    private Matrix saidaOculta;
    private Matrix pesosOcultaSaida;
    private Matrix errosOculta;
    private Matrix netSaida;
    private Matrix obtido;
    private Matrix desejado;
    private Matrix errosSaida;
    private Matrix matrizConfusao;
    private String funcTransferencia;
    private double txAprendizado;
    private int acertos;
    private int qtdRegistros;

    private int qtdNeuroniosEntrada;
    private int qtdNeuroniosOculta;
    private int qtdNeuroniosSaida;

    public RedeNeural(CSV arquivo, String funcTransferencia){
        this.arquivo = arquivo;
        this.qtdNeuroniosEntrada = arquivo.getQtdAtributos();
        this.qtdNeuroniosSaida = arquivo.getQtdClasses();
        this.qtdNeuroniosOculta = (int) Math.sqrt(qtdNeuroniosEntrada * qtdNeuroniosSaida);
        this.funcTransferencia = funcTransferencia;
        txAprendizado = Double.MIN_VALUE;
        inicializarPesos(-0.5, 0.5);
        fit();

    }

    public void fit(){
        for (int j = 0; j < 20; j++) {
            System.out.println();

            for (int i = 0; i < arquivo.getDados().length; i++) {
                inicializarEntrada(arquivo.getDados()[i]);
                inicializarDesejado(arquivo.getDados()[i]);
                gerarSaidasOculta();
                gerarObtido();
                encontrarErrosDaSaida();
                encontrarErrosOculta();
//            System.out.println("Depois da atualização dos pesos da saida");
                atualizarPesos(pesosOcultaSaida, errosSaida, saidaOculta);
//            System.out.println("Depois da atualização dos pesos da oculta");
                atualizarPesos(pesosEntradaOculta, errosOculta, entrada);
                System.out.println("Erro da rede é " + erroRede());

            }
        }
    }

    public void inicializarEntrada(int[] novaEntrada){
        double[][] vetor = new double[qtdNeuroniosEntrada][1];
        for (int i = 0; i < vetor.length; i++) {
            vetor[i][0] = novaEntrada[i];
        }
        entrada = new Matrix(vetor); //criei um vetor coluna com os valores de uma entrada
    }

    public void inicializarDesejado(int[] novaEntrada){
        int classe = novaEntrada[novaEntrada.length - 1]; //1, 2, 3, 4, 5
        double[][] vetor = new double[arquivo.getQtdClasses()][1]; //vetor coluna
        for (int i = 0; i < vetor.length; i++) {
            if(funcTransferencia.equals("Logística")){
                vetor[i][0] = 0; //entre 0 e 1
            }
            else if(funcTransferencia.equals("Hiperbólica")){
                vetor[i][0] = -1;//entre -1 e 1
            }
        }
        vetor[classe - 1][0] = 1; //se a classe correta for 4, coloca "1" no índice 3
        desejado = new Matrix(vetor);
//        desejado.show();
    }

    public void inicializarPesos(double min, double max){
        pesosEntradaOculta = new Matrix(Matrix.random(qtdNeuroniosOculta, qtdNeuroniosEntrada, min, max));
        pesosOcultaSaida = new Matrix(Matrix.random(qtdNeuroniosSaida, qtdNeuroniosOculta, min, max));
    }

    public void encontrarErrosDaSaida(){
        double[][] vetor = new double[arquivo.getQtdClasses()][1];
        for (int i = 0; i < vetor.length; i++) {
            if(funcTransferencia.equals("Logística")){
                //(desejado - obtido) * [(obtido) * (1 - obtido)]
                vetor[i][0] = (desejado.get(i, 0) - obtido.get(i, 0)) * (netSaida.get(i, 0) * (1 - netSaida.get(i, 0)));
            }
            else if(funcTransferencia.equals("Hiperbólica")){
                //(desejado - obtido) * (1 - obtido²)
                vetor[i][0] = (desejado.get(i, 0) - obtido.get(i, 0)) * (1 - (netSaida.get(i, 0) * netSaida.get(i, 0)));
            }
        }
        errosSaida = new Matrix(vetor);
    }

    public void gerarSaidasOculta(){
        netOculta = pesosEntradaOculta.times(entrada);
        saidaOculta = netOculta.ativacao(funcTransferencia);
    }

    public void gerarObtido(){
        netSaida = pesosOcultaSaida.times(saidaOculta);
        obtido = netSaida.ativacao(funcTransferencia);
//        System.out.println("Obtido");
//        obtido.show();
//        System.out.println("Erros da saída");
//        errosSaida.show();
    }

    public void encontrarErrosOculta(){
        double[][] vetor = new double[qtdNeuroniosOculta][1];
        errosOculta = pesosOcultaSaida.times(errosSaida);
        for (int i = 0; i < vetor.length; i++) {
            if(funcTransferencia.equals("Logística")){
                vetor[i][0] = errosOculta.get(i, 0) * (netOculta.get(i, 0) * (1 - netOculta.get(i, 0)));
            }
            else if(funcTransferencia.equals("Hiperbólica")){
                vetor[i][0] = errosOculta.get(i, 0) * (1 - (netOculta.get(i, 0) * netOculta.get(i, 0)));
            }
        }
        errosOculta = new Matrix(vetor);
//        System.out.println("Erros oculta");
//        errosOculta.show();
    }

    public void atualizarPesos(Matrix pesos, Matrix erros, Matrix saida){
        for (int i = 0; i < pesos.getM(); i++) {
            for (int j = 0; j < pesos.getN(); j++) {
                double novoValor = pesos.get(i, j) + txAprendizado * erros.get(i, 0) * saida.get(j, 0);
//                System.out.println(erros.get(i, 0));
                pesos.set(i, j, novoValor);
            }
        }
//        pesos.show();
    }

    public double erroRede(){
        double soma = 0;
        for (int i = 0; i < errosSaida.getM(); i++) {
            soma += Math.pow(errosSaida.get(i, 0), 2);
        }
        return soma / 2;
    }

    public void test(CSV arquivo){
        this.arquivo = arquivo;
        acertos = 0;
        qtdRegistros = arquivo.getDados().length;
        matrizConfusao = new Matrix(Matrix.random(arquivo.getQtdClasses(), arquivo.getQtdClasses(), 0, 0));
        matrizConfusao.show();

        for (int i = 0; i < arquivo.getDados().length; i++) {
//            System.out.println("Linha "+(i+1));
            inicializarEntrada(arquivo.getDados()[i]);
            inicializarDesejado(arquivo.getDados()[i]);
            gerarSaidasOculta();
            gerarObtido();
            decidirEntreSaidas();
            gerarMatrizConfusao();
            if(acertouPrevisao()){
                acertos++;
//                System.out.println("Acertou");
            }
//            else System.out.println("Errou");
        }
        matrizConfusao.show();

        System.out.printf("Acurácia: %d de %d\n", acertos, qtdRegistros);
        System.out.printf("Taxa de acerto: %f\n", (acertos / 1.0) / qtdRegistros);
    }

    public void decidirEntreSaidas(){
        double[][] valoresObtidos = new double[arquivo.getQtdClasses()][1];
        double max = Double.MIN_VALUE;
        double indexMax = -1; //índice do máximo valor

        for (int i = 0; i < valoresObtidos.length; i++) {
            valoresObtidos[i][0] = obtido.get(i, 0);
            if(valoresObtidos[i][0] > max){
                max = valoresObtidos[i][0];
                indexMax = i;
            }
        }

        for (int i = 0; i < valoresObtidos.length; i++) {
            if(funcTransferencia.equals("Logística")){
                if(i == indexMax) valoresObtidos[i][0] = 1;
                else valoresObtidos[i][0] = 0;
            }
            else if(funcTransferencia.equals("Hiperbólica")){
                if(i == indexMax) valoresObtidos[i][0] = 1;
                else valoresObtidos[i][0] = -1;
            }
        }

//        desejado.show();
//        System.out.println();
//        obtido.show();
        obtido = new Matrix(valoresObtidos);
    }

    public boolean acertouPrevisao(){
        for (int i = 0; i < obtido.getM(); i++) {
            if(obtido.get(i, 0) != desejado.get(i, 0)){
                System.out.println("erro em "+i);
                return false;
            }
        }
        return true;
    }

    public void gerarMatrizConfusao(){
        int x = -1, y = -1;
        for (int i = 0; i < desejado.getM(); i++) {
            if(desejado.get(i, 0) == 1){
                x = i;
            }
            if(obtido.get(i, 0) == 1){
                y = i;
            }
        }
        matrizConfusao.set(x, y, matrizConfusao.get(x, y) + 1);
    }

}
