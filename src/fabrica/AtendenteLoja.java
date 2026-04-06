package fabrica;

import comum.Veiculo;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.io.PrintWriter;
import java.util.concurrent.Semaphore;

public class AtendenteLoja extends Thread {
    private Socket socket;
    private EstacaoProducao[] estacoes;
    private Semaphore veiculosProntos;
    private Semaphore mutexRetirada;
    private PrintWriter logVenda;
    private Semaphore mutexLogVenda;

    public AtendenteLoja(Socket socket, EstacaoProducao[] estacoes,
                         Semaphore veiculosProntos, Semaphore mutexRetirada,
                         PrintWriter logVenda, Semaphore mutexLogVenda) {
        this.socket = socket;
        this.estacoes = estacoes;
        this.veiculosProntos = veiculosProntos;
        this.mutexRetirada = mutexRetirada;
        this.logVenda = logVenda;
        this.mutexLogVenda = mutexLogVenda;
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            int lojaId = in.readInt();
            System.out.println("[FABRICA] Loja " + lojaId + " conectada");

            while (true) {
                String comando = (String) in.readObject();
                if (!"SOLICITAR".equals(comando)) break;

                veiculosProntos.acquire();
                mutexRetirada.acquire();

                Veiculo v = null;
                for (int i = 0; i < estacoes.length; i++) {
                    v = estacoes[i].getEsteira().tryRemover();
                    if (v != null) break;
                }

                mutexRetirada.release();

                if (v != null) {
                    out.writeObject(v);
                    out.flush();
                    out.reset();

                    int posicaoLoja = in.readInt();
                    v.setLojaId(lojaId);
                    v.setPosicaoEsteiraLoja(posicaoLoja);

                    mutexLogVenda.acquire();
                    logVenda.println("VENDA LOJA | Veiculo ID: " + v.getId() +
                            " | Cor: " + v.getCor() +
                            " | Tipo: " + v.getTipo() +
                            " | Estacao: " + v.getEstacaoId() +
                            " | Funcionario: " + v.getFuncionarioId() +
                            " | Pos Producao: " + v.getPosicaoEsteiraProducao() +
                            " | Loja: " + lojaId +
                            " | Pos Esteira Loja: " + posicaoLoja);
                    logVenda.flush();
                    mutexLogVenda.release();

                    System.out.println("[FABRICA] Veiculo " + v.getId() +
                            " vendido para Loja " + lojaId);
                }
            }
        } catch (Exception e) {
            System.out.println("[FABRICA] Conexao com loja encerrada");
        }
    }
}
