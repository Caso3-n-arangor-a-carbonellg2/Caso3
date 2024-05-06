package Entrega;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
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
            System.out.println("Hilos iniciados...");
        }
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();

        try (Scanner scan = new Scanner(System.in)) {
            System.out.println("Número de Servidores y Clientes para ejecutar: ");

            String numConexiones = scan.nextLine();
            main.Generador(Integer.parseInt(numConexiones));
            main.Delegados();
        }

        System.out.println("Iniciando cliente...");

        String nombrePuerto = "localhost";
        int numeroPuerto = 1234;
        try (Socket socket = new Socket(nombrePuerto, numeroPuerto)) {
            DataOutputStream outServer = new DataOutputStream(socket.getOutputStream());
            DataInputStream inServer = new DataInputStream(socket.getInputStream());

            DataInputStream inConsola = new DataInputStream(System.in);

            SeguridadCliente seguridadCliente = new SeguridadCliente(inConsola, inServer,
                    outServer);
            seguridadCliente.procesar();
        }
    }
}