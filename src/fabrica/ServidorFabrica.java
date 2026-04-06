package fabrica;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class ServidorFabrica extends Thread {
    private int porta;
    private EstacaoProducao[] estacoes;
    private Semaphore veiculosProntos;
    private Semaphore mutexRetirada;
    private PrintWriter logVenda;
    private Semaphore mutexLogVenda;

    public ServidorFabrica(int porta, EstacaoProducao[] estacoes, Semaphore veiculosProntos,
                           PrintWriter logVenda, Semaphore mutexLogVenda) {
        this.porta = porta;
        this.estacoes = estacoes;
        this.veiculosProntos = veiculosProntos;
        this.mutexRetirada = new Semaphore(1, true);
        this.logVenda = logVenda;
        this.mutexLogVenda = mutexLogVenda;
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(porta);
            System.out.println("[FABRICA] Servidor iniciado na porta " + porta);
            while (!Thread.interrupted()) {
                Socket socket = serverSocket.accept();
                AtendenteLoja atendente = new AtendenteLoja(socket, estacoes,
                        veiculosProntos, mutexRetirada, logVenda, mutexLogVenda);
                atendente.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
