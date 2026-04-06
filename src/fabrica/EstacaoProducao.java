package fabrica;

import comum.EsteiraCircular;
import comum.Configuracao;
import java.io.PrintWriter;
import java.util.concurrent.Semaphore;

public class EstacaoProducao {
    private int id;
    private EsteiraCircular esteira;
    private Ferramenta[] ferramentas;
    private Funcionario[] funcionarios;

    public EstacaoProducao(int id, EstoquePecas estoque, EsteiraAbastecimento esteiraAbastecimento,
                           GeradorVeiculo gerador, Semaphore veiculosProntos,
                           PrintWriter logProducao, Semaphore mutexLog) {
        this.id = id;
        this.esteira = new EsteiraCircular(Configuracao.CAPACIDADE_ESTEIRA_ESTACAO);
        this.ferramentas = new Ferramenta[Configuracao.FUNCIONARIOS_POR_ESTACAO];
        this.funcionarios = new Funcionario[Configuracao.FUNCIONARIOS_POR_ESTACAO];

        for (int i = 0; i < Configuracao.FUNCIONARIOS_POR_ESTACAO; i++) {
            ferramentas[i] = new Ferramenta(i);
        }

        for (int i = 0; i < Configuracao.FUNCIONARIOS_POR_ESTACAO; i++) {
            Ferramenta esquerda = ferramentas[i];
            Ferramenta direita = ferramentas[(i + 1) % Configuracao.FUNCIONARIOS_POR_ESTACAO];
            funcionarios[i] = new Funcionario(i, id, esquerda, direita, esteira,
                    estoque, esteiraAbastecimento, gerador, veiculosProntos,
                    logProducao, mutexLog);
        }
    }

    public void iniciar() {
        for (Funcionario f : funcionarios) {
            f.start();
        }
    }

    public EsteiraCircular getEsteira() {
        return esteira;
    }

    public int getId() {
        return id;
    }
}
