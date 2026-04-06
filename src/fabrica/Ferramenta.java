package fabrica;

import java.util.concurrent.Semaphore;

public class Ferramenta {
    private int id;
    private Semaphore semaforo;

    public Ferramenta(int id) {
        this.id = id;
        this.semaforo = new Semaphore(1, true);
    }

    public void pegar() throws InterruptedException {
        semaforo.acquire();
    }

    public void soltar() {
        semaforo.release();
    }

    public int getId() {
        return id;
    }
}
