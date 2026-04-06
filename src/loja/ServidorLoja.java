package loja;

import comum.EsteiraCircular;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class ServidorLoja extends Thread {
    private int porta;
    private int lojaId;
    private EsteiraCircular esteira;
    private PrintWriter logVenda;
    private Semaphore mutexLog;

    public ServidorLoja(int porta, int lojaId, EsteiraCircular esteira,
                        PrintWriter logVenda, Semaphore mutexLog) {
        this.porta = porta;
        this.lojaId = lojaId;
        this.esteira = esteira;
        this.logVenda = logVenda;
        this.mutexLog = mutexLog;
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(porta);
            System.out.println("[LOJA " + lojaId + "] Servidor de clientes na porta " + porta);
            while (!Thread.interrupted()) {
                Socket socket = serverSocket.accept();
                AtendenteCliente atendente = new AtendenteCliente(socket, lojaId,
                        esteira, logVenda, mutexLog);
                atendente.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
