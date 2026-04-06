package loja;

import comum.Veiculo;
import comum.EsteiraCircular;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class AtendenteCliente extends Thread {
    private Socket socket;
    private int lojaId;
    private EsteiraCircular esteira;
    private PrintWriter logVenda;
    private Semaphore mutexLog;

    public AtendenteCliente(Socket socket, int lojaId, EsteiraCircular esteira,
                            PrintWriter logVenda, Semaphore mutexLog) {
        this.socket = socket;
        this.lojaId = lojaId;
        this.esteira = esteira;
        this.logVenda = logVenda;
        this.mutexLog = mutexLog;
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            int clienteId = in.readInt();
            String comando = (String) in.readObject();

            if ("COMPRAR".equals(comando)) {
                Veiculo v = esteira.remover();

                mutexLog.acquire();
                logVenda.println("VENDA CLIENTE | Veiculo ID: " + v.getId() +
                        " | Cor: " + v.getCor() +
                        " | Tipo: " + v.getTipo() +
                        " | Estacao: " + v.getEstacaoId() +
                        " | Funcionario: " + v.getFuncionarioId() +
                        " | Pos Producao: " + v.getPosicaoEsteiraProducao() +
                        " | Loja: " + lojaId +
                        " | Pos Esteira Loja: " + v.getPosicaoEsteiraLoja() +
                        " | Cliente: " + clienteId);
                logVenda.flush();
                mutexLog.release();

                out.writeObject(v);
                out.flush();

                System.out.println("[LOJA " + lojaId + "] Vendeu veiculo " +
                        v.getId() + " para cliente " + clienteId);
            }

            socket.close();
        } catch (Exception e) {
            // Conexao encerrada
        }
    }
}
