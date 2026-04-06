package cliente;

import comum.Veiculo;
import java.util.ArrayList;
import java.util.List;

public class Garagem {
    private List<Veiculo> veiculos;

    public Garagem() {
        this.veiculos = new ArrayList<>();
    }

    public void guardar(Veiculo v) {
        veiculos.add(v);
    }

    public int tamanho() {
        return veiculos.size();
    }

    public List<Veiculo> getVeiculos() {
        return veiculos;
    }

    public void listar(int clienteId) {
        for (Veiculo v : veiculos) {
            System.out.println("  [CLIENTE " + clienteId + "] Garagem -> " + v);
        }
    }
}
