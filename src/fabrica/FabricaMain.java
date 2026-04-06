package fabrica;

import comum.Configuracao;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.concurrent.Semaphore;

public class FabricaMain {
    public static void main(String[] args) throws Exception {
        System.out.println("========================================");
        System.out.println("   FABRICA DE VEICULOS - INICIANDO");
        System.out.println("========================================");

        PrintWriter logProducao = new PrintWriter(new FileWriter("log_producao.txt"), true);
        PrintWriter logVenda = new PrintWriter(new FileWriter("log_venda_loja.txt"), true);
        Semaphore mutexLogProducao = new Semaphore(1, true);
        Semaphore mutexLogVenda = new Semaphore(1, true);

        EstoquePecas estoque = new EstoquePecas(Configuracao.CAPACIDADE_ESTOQUE);
        EsteiraAbastecimento esteiraAbastecimento = new EsteiraAbastecimento(
                Configuracao.CAPACIDADE_ESTEIRA_ABASTECIMENTO);
        GeradorVeiculo gerador = new GeradorVeiculo();
        Semaphore veiculosProntos = new Semaphore(0, true);

        FornecedorPecas fornecedor = new FornecedorPecas(estoque);
        fornecedor.start();
        System.out.println("[FABRICA] Fornecedor de pecas ativo");

        EstacaoProducao[] estacoes = new EstacaoProducao[Configuracao.NUM_ESTACOES];
        for (int i = 0; i < Configuracao.NUM_ESTACOES; i++) {
            estacoes[i] = new EstacaoProducao(i, estoque, esteiraAbastecimento,
                    gerador, veiculosProntos, logProducao, mutexLogProducao);
            estacoes[i].iniciar();
            System.out.println("[FABRICA] Estacao " + i + " ativa com " +
                    Configuracao.FUNCIONARIOS_POR_ESTACAO + " funcionarios");
        }

        ServidorFabrica servidor = new ServidorFabrica(Configuracao.PORTA_FABRICA,
                estacoes, veiculosProntos, logVenda, mutexLogVenda);
        servidor.start();

        System.out.println("[FABRICA] Aguardando conexoes de lojas na porta " +
                Configuracao.PORTA_FABRICA);
        System.out.println("========================================");

        Thread.currentThread().join();
    }
}
