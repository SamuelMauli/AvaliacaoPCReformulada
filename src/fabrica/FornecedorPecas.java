package fabrica;

public class FornecedorPecas extends Thread {
    private EstoquePecas estoque;

    public FornecedorPecas(EstoquePecas estoque) {
        this.estoque = estoque;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(50);
                estoque.reabastecer();
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
