package fabrica;

import comum.Veiculo;
import comum.EsteiraCircular;
import java.io.PrintWriter;
import java.util.concurrent.Semaphore;

public class Funcionario extends Thread {
    private int id;
    private int estacaoId;
    private Ferramenta ferramentaEsquerda;
    private Ferramenta ferramentaDireita;
    private EsteiraCircular esteira;
    private EstoquePecas estoque;
    private EsteiraAbastecimento esteiraAbastecimento;
    private GeradorVeiculo gerador;
    private Semaphore veiculosProntos;
    private PrintWriter logProducao;
    private Semaphore mutexLog;

    public Funcionario(int id, int estacaoId, Ferramenta ferramentaEsquerda,
                       Ferramenta ferramentaDireita, EsteiraCircular esteira,
                       EstoquePecas estoque, EsteiraAbastecimento esteiraAbastecimento,
                       GeradorVeiculo gerador, Semaphore veiculosProntos,
                       PrintWriter logProducao, Semaphore mutexLog) {
        this.id = id;
        this.estacaoId = estacaoId;
        this.ferramentaEsquerda = ferramentaEsquerda;
        this.ferramentaDireita = ferramentaDireita;
        this.esteira = esteira;
        this.estoque = estoque;
        this.esteiraAbastecimento = esteiraAbastecimento;
        this.gerador = gerador;
        this.veiculosProntos = veiculosProntos;
        this.logProducao = logProducao;
        this.mutexLog = mutexLog;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                esteiraAbastecimento.solicitar();
                estoque.consumir();
                esteiraAbastecimento.liberar();

                Ferramenta primeira, segunda;
                if (ferramentaEsquerda.getId() < ferramentaDireita.getId()) {
                    primeira = ferramentaEsquerda;
                    segunda = ferramentaDireita;
                } else {
                    primeira = ferramentaDireita;
                    segunda = ferramentaEsquerda;
                }

                primeira.pegar();
                segunda.pegar();

                Thread.sleep((long) (Math.random() * 500 + 200));

                Veiculo v = gerador.gerar(estacaoId, id);
                int posicao = esteira.inserir(v);
                v.setPosicaoEsteiraProducao(posicao);

                mutexLog.acquire();
                logProducao.println("PRODUCAO | Veiculo ID: " + v.getId() +
                        " | Cor: " + v.getCor() +
                        " | Tipo: " + v.getTipo() +
                        " | Estacao: " + estacaoId +
                        " | Funcionario: " + id +
                        " | Posicao Esteira: " + posicao);
                logProducao.flush();
                mutexLog.release();

                veiculosProntos.release();

                System.out.println("[FABRICA] Veiculo " + v.getId() +
                        " produzido - Estacao " + estacaoId + " Func " + id +
                        " Pos " + posicao);

                segunda.soltar();
                primeira.soltar();

                Thread.sleep((long) (Math.random() * 300 + 100));

            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
