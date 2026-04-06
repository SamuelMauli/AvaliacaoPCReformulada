package comum;

import java.io.Serializable;

public class Veiculo implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String cor;
    private String tipo;
    private int estacaoId;
    private int funcionarioId;
    private int posicaoEsteiraProducao;
    private int lojaId;
    private int posicaoEsteiraLoja;

    public Veiculo(int id, String cor, String tipo, int estacaoId, int funcionarioId) {
        this.id = id;
        this.cor = cor;
        this.tipo = tipo;
        this.estacaoId = estacaoId;
        this.funcionarioId = funcionarioId;
    }

    public int getId() { return id; }
    public String getCor() { return cor; }
    public String getTipo() { return tipo; }
    public int getEstacaoId() { return estacaoId; }
    public int getFuncionarioId() { return funcionarioId; }
    public int getPosicaoEsteiraProducao() { return posicaoEsteiraProducao; }
    public void setPosicaoEsteiraProducao(int posicao) { this.posicaoEsteiraProducao = posicao; }
    public int getLojaId() { return lojaId; }
    public void setLojaId(int lojaId) { this.lojaId = lojaId; }
    public int getPosicaoEsteiraLoja() { return posicaoEsteiraLoja; }
    public void setPosicaoEsteiraLoja(int posicao) { this.posicaoEsteiraLoja = posicao; }

    @Override
    public String toString() {
        return "Veiculo[id=" + id + ", cor=" + cor + ", tipo=" + tipo +
               ", estacao=" + estacaoId + ", funcionario=" + funcionarioId +
               ", posProducao=" + posicaoEsteiraProducao + "]";
    }
}
