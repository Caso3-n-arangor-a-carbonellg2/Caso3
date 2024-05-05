package L8;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

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
		SecretKey key = keygenerator.generateKey();

		byte[] cifradoSim = cifradoSimétrico.cifrar(key, textoSim);
		System.out.println("Texto cifrado: " + cifradoSim);
		imprimir(cifradoSim);

		byte[] descifradoSim = cifradoSimétrico.descifrar(key, cifradoSim);
		String descifradoClaro = new String(descifradoSim, StandardCharsets.UTF_8);
		System.out.println("Texto descifrado: " + descifradoClaro);
	}

}
