package Entrega;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Cliente {

    static Servidor servidor;
    static int numeroPuerto;
    static String nombrePuerto = "localhost";

    public Cliente(Servidor servidor, int numeroPuerto) {
        Cliente.servidor = servidor;
        Cliente.numeroPuerto = numeroPuerto;
    }

    DataOutputStream outServer = null;
    DataInputStream inServer = null;

    public void run() throws Exception {

        System.out.println("Iniciando cliente...");

        Socket socket = new Socket(nombrePuerto, numeroPuerto); 

        outServer = new DataOutputStream(socket.getOutputStream());
        inServer = new DataInputStream(socket.getInputStream());

        DataInputStream inConsola = new DataInputStream(System.in);

        SeguridadCliente seguridadCliente = new SeguridadCliente(inConsola, inServer,
                outServer);
        seguridadCliente.ejecutar();

        // Cerrar todos los hilos
        inConsola.close();
        inServer.close();
        outServer.close();
        socket.close(); // Cerrar la conexión

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'start'");
    }

    public void start() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'start'");
    }

}
