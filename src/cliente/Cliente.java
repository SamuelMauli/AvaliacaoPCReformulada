package cliente;

import comum.Veiculo;
import comum.Configuracao;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

public class Cliente extends Thread {
    private int id;
    private Garagem garagem;
    private Random random;

    public Cliente(int id) {
        this.id = id;
        this.garagem = new Garagem();
        this.random = new Random();
    }

    @Override
    public void run() {
        int numCompras = random.nextInt(
                Configuracao.MAX_COMPRAS - Configuracao.MIN_COMPRAS + 1) + Configuracao.MIN_COMPRAS;

        System.out.println("[CLIENTE " + id + "] Vai comprar " + numCompras + " veiculos");

        for (int i = 0; i < numCompras; i++) {
            try {
                Thread.sleep((long) (random.nextDouble() * 3000 + 500));

                int lojaIndex = random.nextInt(Configuracao.NUM_LOJAS);
                int portaLoja = Configuracao.PORTAS_LOJAS[lojaIndex];
                int lojaId = lojaIndex + 1;

                Socket socket = null;
                int tentativas = 0;
                while (socket == null && tentativas < 10) {
                    try {
                        socket = new Socket(Configuracao.HOST_FABRICA, portaLoja);
                    } catch (Exception e) {
                        tentativas++;
                        Thread.sleep(1000);
                    }
                }

                if (socket == null) {
                    System.out.println("[CLIENTE " + id + "] Nao conseguiu conectar a loja " + lojaId);
                    continue;
                }

                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                out.writeInt(id);
                out.writeObject("COMPRAR");
                out.flush();

                Veiculo v = (Veiculo) in.readObject();
                garagem.guardar(v);

                System.out.println("[CLIENTE " + id + "] Comprou veiculo " + v.getId() +
                        " (" + v.getCor() + " " + v.getTipo() + ") na loja " + lojaId +
                        " [garagem: " + garagem.tamanho() + "/" + numCompras + "]");

                socket.close();
            } catch (Exception e) {
                System.out.println("[CLIENTE " + id + "] Erro na compra: " + e.getMessage());
            }
        }

        System.out.println("[CLIENTE " + id + "] FINALIZADO - " + garagem.tamanho() + " veiculos na garagem:");
        garagem.listar(id);
    }
}
