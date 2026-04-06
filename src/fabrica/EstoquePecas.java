package fabrica;

import java.util.concurrent.Semaphore;

public class EstoquePecas {
    private Semaphore pecasDisponiveis;
    private Semaphore mutex;
    private int quantidade;
    private int capacidade;

    public EstoquePecas(int capacidade) {
        this.capacidade = capacidade;
        this.quantidade = capacidade;
        this.pecasDisponiveis = new Semaphore(capacidade, true);
        this.mutex = new Semaphore(1, true);
    }

    public void consumir() throws InterruptedException {
        pecasDisponiveis.acquire();
        mutex.acquire();
        quantidade--;
        mutex.release();
    }

    public void reabastecer() throws InterruptedException {
        mutex.acquire();
        if (quantidade < capacidade) {
            quantidade++;
            mutex.release();
            pecasDisponiveis.release();
        } else {
            mutex.release();
        }
    }

    public int getQuantidade() throws InterruptedException {
        mutex.acquire();
        int q = quantidade;
        mutex.release();
        return q;
    }
}
