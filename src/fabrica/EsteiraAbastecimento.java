package fabrica;

import java.util.concurrent.Semaphore;

public class EsteiraAbastecimento {
    private Semaphore permissoes;

    public EsteiraAbastecimento(int capacidade) {
        this.permissoes = new Semaphore(capacidade, true);
    }

    public void solicitar() throws InterruptedException {
        permissoes.acquire();
    }

    public void liberar() {
        permissoes.release();
    }
}
