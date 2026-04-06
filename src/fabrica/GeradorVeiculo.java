package fabrica;

import comum.Veiculo;
import java.util.concurrent.Semaphore;

public class GeradorVeiculo {
    private int proximoId;
    private Semaphore mutex;
    private static final String[] CORES = {"VERMELHO", "VERDE", "AZUL"};
    private static final String[] TIPOS = {"SUV", "SEDAN"};

    public GeradorVeiculo() {
        this.proximoId = 1;
        this.mutex = new Semaphore(1, true);
    }

    public Veiculo gerar(int estacaoId, int funcionarioId) throws InterruptedException {
        mutex.acquire();
        int id = proximoId++;
        String cor = CORES[(id - 1) % 3];
        String tipo = TIPOS[(id - 1) % 2];
        mutex.release();
        return new Veiculo(id, cor, tipo, estacaoId, funcionarioId);
    }
}
