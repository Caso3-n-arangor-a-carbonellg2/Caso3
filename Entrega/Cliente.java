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

        Socket socket = new Socket(nombrePuerto, numeroPuerto); // Conectar al servidor en el puerto 1234

        outServer = new DataOutputStream(socket.getOutputStream());
        inServer = new DataInputStream(socket.getInputStream());

        DataInputStream inConsola = new DataInputStream(System.in);

        SeguridadCliente seguridadCliente = new SeguridadCliente(inConsola, inServer,
                outServer);
        seguridadCliente.procesar();

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
