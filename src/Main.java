import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Main {

    private Servidor[] servers;
    private Cliente[] clientes;

    public void generador(int numberOfDelegates) throws NoSuchAlgorithmException {
        servers = new Servidor[numberOfDelegates];
        clientes = new Cliente[numberOfDelegates];

        for (int i = 0; i < numberOfDelegates; i++) {
            servers[i] = new Servidor(8000 + i);
            clientes[i] = new Cliente(servers[i], 8000 + i);
        }
    }

    public void delegados() throws InterruptedException {
        for (int i = 0; i < servers.length; i++) {
            servers[i].start();
            clientes[i].start();
            Thread.sleep(5000);
        }
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Ingrese el número de clientes que desea crear:");
        int numberOfClients = scanner.nextInt();

        main.generador(numberOfClients);
        main.delegados();

        scanner.close();
    }
}