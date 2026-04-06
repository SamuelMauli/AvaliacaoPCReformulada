package loja;

import comum.EsteiraCircular;
import comum.Configuracao;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Semaphore;

public class Loja {
    private int id;
    private int portaClientes;
    private EsteiraCircular esteira;
    private PrintWriter logRecebimento;
    private PrintWriter logVenda;
    private Semaphore mutexLogRecebimento;
    private Semaphore mutexLogVenda;

    public Loja(int id, int portaClientes) throws IOException {
        this.id = id;
        this.portaClientes = portaClientes;
        this.esteira = new EsteiraCircular(Configuracao.CAPACIDADE_ESTEIRA_LOJA);
        this.logRecebimento = new PrintWriter(new FileWriter("log_loja_" + id + "_recebimento.txt"), true);
        this.logVenda = new PrintWriter(new FileWriter("log_loja_" + id + "_venda.txt"), true);
        this.mutexLogRecebimento = new Semaphore(1, true);
        this.mutexLogVenda = new Semaphore(1, true);
    }

    public void iniciar() {
        SolicitadorVeiculos solicitador = new SolicitadorVeiculos(id, esteira,
                logRecebimento, mutexLogRecebimento);
        solicitador.start();

        ServidorLoja servidor = new ServidorLoja(portaClientes, id, esteira,
                logVenda, mutexLogVenda);
        servidor.start();

        System.out.println("[LOJA " + id + "] Iniciada - porta clientes: " + portaClientes);
    }

    public int getId() {
        return id;
    }

    public EsteiraCircular getEsteira() {
        return esteira;
    }
}
