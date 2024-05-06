import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Cliente {

    public static final int numeroPuerto = 1234;
    public static final String servidor = "localhost";

    public static void main(String args[]) throws Exception {

        Socket socket = null;
        DataOutputStream outServer = null;
        DataInputStream inServer = null;

        System.out.println("Cliente iniciado ...");

        try {
            socket = new Socket(servidor, numeroPuerto); // Conectar al servidor en el puerto 1234

            outServer = new DataOutputStream(socket.getOutputStream());
            inServer = new DataInputStream(socket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        DataInputStream inConsola = new DataInputStream(System.in);

        SeguridadCliente seguridadCliente = new SeguridadCliente(inConsola, inServer, outServer);
        seguridadCliente.procesar();

        // Cerrar todos los BufferedReaders y PrintReader
        inConsola.close();
        inServer.close();
        outServer.close();
        socket.close(); // Cerrar la conexión con el servidor

    }

}
