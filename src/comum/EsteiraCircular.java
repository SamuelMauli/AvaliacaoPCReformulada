package comum;

import java.util.concurrent.Semaphore;

public class EsteiraCircular {
    private Veiculo[] buffer;
    private int capacidade;
    private int inicio;
    private int fim;
    private Semaphore vazio;
    private Semaphore cheio;
    private Semaphore mutex;

    public EsteiraCircular(int capacidade) {
        this.buffer = new Veiculo[capacidade];
        this.capacidade = capacidade;
        this.inicio = 0;
        this.fim = 0;
        this.vazio = new Semaphore(capacidade, true);
        this.cheio = new Semaphore(0, true);
        this.mutex = new Semaphore(1, true);
    }

    public int inserir(Veiculo v) throws InterruptedException {
        vazio.acquire();
        mutex.acquire();
        int posicao = fim;
        buffer[fim] = v;
        fim = (fim + 1) % capacidade;
        mutex.release();
        cheio.release();
        return posicao;
    }

    public Veiculo remover() throws InterruptedException {
        cheio.acquire();
        mutex.acquire();
        Veiculo v = buffer[inicio];
        buffer[inicio] = null;
        inicio = (inicio + 1) % capacidade;
        mutex.release();
        vazio.release();
        return v;
    }

    public Veiculo tryRemover() {
        if (!cheio.tryAcquire()) {
            return null;
        }
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            cheio.release();
            Thread.currentThread().interrupt();
            return null;
        }
        Veiculo v = buffer[inicio];
        buffer[inicio] = null;
        inicio = (inicio + 1) % capacidade;
        mutex.release();
        vazio.release();
        return v;
    }

    public int getCapacidade() {
        return capacidade;
    }
}
