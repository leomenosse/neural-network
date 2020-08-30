package com.company;

public class Main {

    public static void main(String[] args) {
	// write your code here
        CSV arq = new CSV("treinamento.csv");
        CSV teste = new CSV("teste.csv");
        RedeNeural net = new RedeNeural(arq, "Logística");
        net.test(teste);
    }
}
