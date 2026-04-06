package loja;

import comum.Configuracao;

public class LojaMain {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Uso: java loja.LojaMain <id_loja>");
            System.out.println("  id_loja: 1, 2 ou 3");
            return;
        }

        int lojaId = Integer.parseInt(args[0]);
        if (lojaId < 1 || lojaId > Configuracao.NUM_LOJAS) {
            System.out.println("ID da loja deve ser entre 1 e " + Configuracao.NUM_LOJAS);
            return;
        }

        int portaClientes = Configuracao.PORTAS_LOJAS[lojaId - 1];

        System.out.println("========================================");
        System.out.println("   LOJA " + lojaId + " - INICIANDO");
        System.out.println("========================================");

        Loja loja = new Loja(lojaId, portaClientes);
        loja.iniciar();

        System.out.println("========================================");

        Thread.currentThread().join();
    }
}
