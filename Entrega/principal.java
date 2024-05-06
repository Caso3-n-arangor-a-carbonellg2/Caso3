package Entrega;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class principal {
    public final static String textoSim = "La nueva realidad";
    public final static String textoAsim = "La nueva realidad";

    public static void main(String[] args) throws NoSuchAlgorithmException {
        simetrico();
        System.out.println();
    }

    public static void imprimir(byte[] contenido) {
        int i = 0;
        for (; i < contenido.length - 1; i++) {
            System.out.print(contenido[i] + ", ");
        }
        System.out.println(contenido[i] + " ");
    }

    public static void simetrico() throws NoSuchAlgorithmException {
        System.out.println("Texto a cifrar: " + textoSim);
        imprimir(textoSim.getBytes());

        KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
        keygenerator.init(256);
        SecretKey key = keygenerator.generateKey();

        String mensajeCifradoString = new String(key.getEncoded().toString(), StandardCharsets.UTF_8);
        System.out.println("Llave generada: " + mensajeCifradoString);

        // Generar un vector de inicialización (IV) de 16 bytes para AES
        byte[] iv = new byte[16];
        // utiliza un iv predefinido
        for (int i = 0; i < 16; i++) {
            iv[i] = 0;
        }

        // Aquí puedes generar un IV aleatorio, o puedes utilizar un IV predefinido
        // para propósitos de este ejemplo, usaremos un IV de ceros
        // NOTA: En un entorno real, asegúrate de generar un IV único para cada cifrado
        // y transmitirlo junto con el mensaje cifrado
        // Ejemplo de generación de un IV aleatorio:
        // SecureRandom random = new SecureRandom();
        // random.nextBytes(iv);

        byte[] cifradoSim = cifradoSimétrico.cifrar(key, textoSim, iv);
        System.out.println("Texto cifrado: ");
        imprimir(cifradoSim);

        byte[] descifradoSim = cifradoSimétrico.descifrar(key, cifradoSim, iv);
        String descifradoClaro = new String(descifradoSim, StandardCharsets.UTF_8);
        System.out.println("Texto descifrado: " + descifradoClaro);
    }
}
