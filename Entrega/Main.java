package Entrega;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public void Generador(int numServs) throws NoSuchAlgorithmException {

        final List<Servidor> servers = new ArrayList<>();
        final List<Cliente> clients = new ArrayList<>();
        for (int i = 0; i < numServs; i++) {
            Servidor servidor = new Servidor(1234 + i);
            clients.add(new Cliente(servidor, 1234 + i));
            servers.add(servidor);
        }
    }

    public void Delegados() throws InterruptedException {

        final List<Servidor> servers = new ArrayList<>();
        final List<Cliente> clients = new ArrayList<>();
        for (int i = 0; i < servers.size(); i++) {
            servers.get(i).start();
            Thread.sleep(1000);
            clients.get(i).start();
            Thread.sleep(2000);
            Thread.sleep(2000);
            System.out.println("");
        }
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();

        try (Scanner scan = new Scanner(System.in)) {
            System.out.println("");
            System.out.print("Número de Servidores y Clientes para ejecutar: ");
            System.out.println("");

            String numberOfDelegates = scan.nextLine();
            main.Generador(Integer.parseInt(numberOfDelegates));
            main.Delegados();
        }
    }
}