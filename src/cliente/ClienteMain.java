package cliente;

import comum.Configuracao;

public class ClienteMain {
    public static void main(String[] args) throws Exception {
        System.out.println("========================================");
        System.out.println("   CLIENTES - INICIANDO");
        System.out.println("========================================");

        Thread[] clientes = new Thread[Configuracao.NUM_CLIENTES];

        for (int i = 0; i < Configuracao.NUM_CLIENTES; i++) {
            clientes[i] = new Cliente(i);
            clientes[i].start();
            Thread.sleep(100);
        }

        System.out.println("[CLIENTES] " + Configuracao.NUM_CLIENTES + " clientes ativos");
        System.out.println("========================================");

        for (Thread cliente : clientes) {
            cliente.join();
        }

        System.out.println("========================================");
        System.out.println("   TODOS OS CLIENTES FINALIZARAM");
        System.out.println("========================================");
    }
}
