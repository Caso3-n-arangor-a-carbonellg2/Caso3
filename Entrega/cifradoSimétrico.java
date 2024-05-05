package Entrega;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class cifradoSimétrico {
    private static final String ALGORITMO = "AES/CBC/PKCS5Padding";

    public static byte[] cifrar(SecretKey llave, String texto, byte[] iv) {
        byte[] textoCifrado;

        try {
            Cipher cifrador = Cipher.getInstance(ALGORITMO);
            byte[] textoClaro = texto.getBytes();

            IvParameterSpec ivParam = new IvParameterSpec(iv);

            cifrador.init(Cipher.ENCRYPT_MODE, llave, ivParam);
            textoCifrado = cifrador.doFinal(textoClaro);

            return textoCifrado;
        } catch (Exception e) {
            System.out.println("Excepción: " + e.getMessage());
            return null;
        }
    }

    public static byte[] descifrar(SecretKey llave, byte[] textoCifrado, byte[] iv) {
        byte[] textoClaro;

        try {
            Cipher cifrador = Cipher.getInstance(ALGORITMO);

            IvParameterSpec ivParam = new IvParameterSpec(iv);

            cifrador.init(Cipher.DECRYPT_MODE, llave, ivParam);
            textoClaro = cifrador.doFinal(textoCifrado);
        } catch (Exception e) {
            System.out.println("Excepción: " + e.getMessage());
            return null;
        }
        return textoClaro;
    }
}
