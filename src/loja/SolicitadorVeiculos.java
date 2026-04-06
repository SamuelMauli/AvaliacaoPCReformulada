package loja;

import comum.Veiculo;
import comum.EsteiraCircular;
import comum.Configuracao;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class SolicitadorVeiculos extends Thread {
    private int lojaId;
    private EsteiraCircular esteira;
    private PrintWriter logRecebimento;
    private Semaphore mutexLog;

    public SolicitadorVeiculos(int lojaId, EsteiraCircular esteira,
                               PrintWriter logRecebimento, Semaphore mutexLog) {
        this.lojaId = lojaId;
        this.esteira = esteira;
        this.logRecebimento = logRecebimento;
        this.mutexLog = mutexLog;
        setDaemon(true);
    }

    @Override
    public void run() {
        Socket socket = null;
        while (socket == null) {
            try {
                socket = new Socket(Configuracao.HOST_FABRICA, Configuracao.PORTA_FABRICA);
            } catch (Exception e) {
                System.out.println("[LOJA " + lojaId + "] Aguardando fabrica...");
                try { Thread.sleep(2000); } catch (InterruptedException ie) { return; }
            }
        }

        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeInt(lojaId);
            out.flush();

            System.out.println("[LOJA " + lojaId + "] Conectada a fabrica");

            while (!Thread.interrupted()) {
                out.writeObject("SOLICITAR");
                out.flush();

                Veiculo v = (Veiculo) in.readObject();
                int posicao = esteira.inserir(v);
                v.setPosicaoEsteiraLoja(posicao);
                v.setLojaId(lojaId);

                out.writeInt(posicao);
                out.flush();

                mutexLog.acquire();
                logRecebimento.println("RECEBIMENTO | Veiculo ID: " + v.getId() +
                        " | Cor: " + v.getCor() +
                        " | Tipo: " + v.getTipo() +
                        " | Estacao: " + v.getEstacaoId() +
                        " | Funcionario: " + v.getFuncionarioId() +
                        " | Pos Producao: " + v.getPosicaoEsteiraProducao() +
                        " | Loja: " + lojaId +
                        " | Pos Esteira Loja: " + posicao);
                logRecebimento.flush();
                mutexLog.release();

                System.out.println("[LOJA " + lojaId + "] Recebeu veiculo " +
                        v.getId() + " na posicao " + posicao);
            }
        } catch (Exception e) {
            System.out.println("[LOJA " + lojaId + "] Erro com fabrica: " + e.getMessage());
        }
    }
}
