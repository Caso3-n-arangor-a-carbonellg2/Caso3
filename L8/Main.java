package L8;

import javax.crypto.KeyGenerator;

public class Main {

    public static void main(String[] args) {
        // Reciba por teclado la entrada de un texto.

        // Imprima el texto recibido por el teclado.

        // Imprima texto claro en byte []. Utilice el método getBytes() de la clase
        // String para convertir el mensaje a byte[]. Para imprimir el contenido del
        // byte[] utilice el método imprimir().
    }

    private final static String ALGORITMO = "AES";

    public static void imprimir(byte[] contenido) {
        int i = 0;
        for (; i < contenido.length - 1; i++) {
            System.out.print(contenido[i] + ", ");
        }
        System.out.println(contenido[i] + " ");
    }

    KeyGenerator keygenerator = KeyGenerator.getInstance(ALGORITMO);
    SecretKey key = keygenerator.generateKey();

}
