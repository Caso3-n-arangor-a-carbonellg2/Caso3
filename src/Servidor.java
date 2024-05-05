import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Servidor extends Thread{
    public static final int PUERTO = 3400;
    public static PublicKey LLPublica;
    private static PrivateKey LLPrivada;
    
    int port;



    public  Servidor(int port) throws NoSuchAlgorithmException{
        KeyPairGenerator crearLlave = KeyPairGenerator.getInstance("RSA");
		crearLlave.initialize(2048);
		KeyPair keyPair = crearLlave.generateKeyPair();
		LLPrivada = keyPair.getPrivate();
        LLPublica =keyPair.getPublic();
        
        this.port = port;
        
    }
    public String desencriptar (String texto, SecretKey llave, byte[] vector) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(vector);
        cipher.init(Cipher.DECRYPT_MODE, llave, ivSpec);
        byte[] decodedValue = Base64.getDecoder().decode(texto);
        byte[] decrypted = cipher.doFinal(decodedValue);
        return new String(decrypted);
    }
    public String encriptar(String texto, Key llave, byte[] vector) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        
        IvParameterSpec ivSpec = new IvParameterSpec(vector);
        cipher.init(Cipher.ENCRYPT_MODE, llave, ivSpec);
        byte[] encrypted = cipher.doFinal(texto.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public byte[] calcularHMac(Key llaveCifrado, String mensaje) throws NoSuchAlgorithmException, InvalidKeyException{
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(llaveCifrado);
        byte [] hmac = mac.doFinal(mensaje.getBytes());

        return hmac;


    }

    public void run(){
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Iniciando servidor en el puerto: " + port);
            Boolean bandera = true;
            Socket clientSocket = serverSocket.accept();//acepto conexion
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            String inicializacion = (String) in.readObject();

            //2
            byte[] reto = (byte[]) in.readObject();
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(LLPrivada);
            signature.update(reto);
            out.writeObject(reto);

            
            KeyPairGenerator par = KeyPairGenerator.getInstance("DH");
            par.initialize(1024);
            KeyPair serverKeyPair = par.generateKeyPair();
            DHPublicKey serverPublicKey = (DHPublicKey) serverKeyPair.getPublic();
            BigInteger g = serverPublicKey.getParams().getG();
            BigInteger p = serverPublicKey.getParams().getP();
            BigInteger gxmodp = serverPublicKey.getY();
            Random rnd = new Random();

            // añadimos el limite al que puede estar
            Integer limite = p.subtract(new BigInteger("1")).intValue();
            int numeroXInt = rnd.nextInt(limite);
            BigInteger X = BigInteger.valueOf(numeroXInt);

            System.out.println("checkpoint");
            SecureRandom random = new SecureRandom();
            byte[] vector = new byte[16];
            random.nextBytes(vector);

            out.writeObject(g);
            out.writeObject(p);
            out.writeObject(gxmodp);
            out.writeObject(vector);
            signature.update(g.toByteArray());
            signature.update(p.toByteArray());
            signature.update(gxmodp.toByteArray());
            byte[] intento = signature.sign();
            out.writeObject(intento);
            //aca
            BigInteger numeroGElevadoALaY = (BigInteger)in.readObject();
            // elevar a la x el numero que nos dieron elevado a la y
            BigInteger numeroFinal = numeroGElevadoALaY.modPow(X , p);
            System.out.println("Llave Maestra Servidor: " + numeroFinal);
            
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] digestWithSHA512 = digest.digest(numeroFinal.toByteArray());
            
            // Con los primeros 256 bits sacar la llave para encriptar
            byte[] bytesSimetrica = Arrays.copyOfRange(digestWithSHA512, 0, 32); // Primeros 256 bits

            // con los ultimos 256 bits sacar la llave para hacer el HMAC
            byte[] bytesHash = Arrays.copyOfRange(digestWithSHA512, 32, 64);      // Últimos 256 bits

            // pasamos a llaves los bytes de la llave Simetrica
            SecretKeySpec llaveSimetrica = new SecretKeySpec(bytesSimetrica, "AES");

            // pasamos a llave los bytes de la llave para Hash
            SecretKeySpec llaveHash =  new SecretKeySpec(bytesHash, "HMACSHA256");


            // Le decimos al cliente que continue con el proceso
            out.writeObject("CONTINUAR");


            ///AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA


             
             String loginCifrado = (String) in.readObject();
             String contraseñaCifrada = (String)  in.readObject();
             String loginHashEncriptado = (String) in.readObject();
             String contraseñaHashEncriptada = (String) in.readObject();

             String login = desencriptar(loginCifrado, llaveSimetrica, vector);
             String contraseña = desencriptar(contraseñaCifrada, llaveSimetrica, vector);
             String loginHash =  desencriptar(loginHashEncriptado, llaveSimetrica, vector);
             String contraseñaHash = desencriptar(contraseñaHashEncriptada, llaveSimetrica, vector);
           
            byte[] loginVerificadoBytes = calcularHMac(llaveHash, login);
            String loginVerificadoString = Base64.getEncoder().encodeToString(loginVerificadoBytes);

           
            byte[] contraseñaVerificadaBytes = calcularHMac(llaveHash, contraseña);
            String contraseñaVerificadaString = Base64.getEncoder().encodeToString(contraseñaVerificadaBytes);

            String numeroCifrado = (String) in.readObject();
            String numeroHash = (String) in.readObject();


           
            String numeroDescifrado = desencriptar(numeroCifrado, llaveSimetrica, vector);

            byte[] numeroDescifradoHashBytes = calcularHMac(llaveHash, numeroDescifrado);

            String numeroDescifradoHashString = Base64.getEncoder().encodeToString(numeroDescifradoHashBytes);


         
            Integer numeroRespuesta = Integer.parseInt(numeroDescifrado)- 1;

           
            String numeroRespuestaCifrado = encriptar(String.valueOf(numeroRespuesta), llaveSimetrica, vector);

            
            byte[] numeroRespuestaBytes = calcularHMac(llaveHash, String.valueOf(numeroRespuesta));
            String numeroRespuestaHash = Base64.getEncoder().encodeToString(numeroRespuestaBytes);

            out.writeObject(numeroRespuestaCifrado);
            out.writeObject(numeroRespuestaHash);
            clientSocket.close();



        }
        catch (Exception e) {
            // TODO: handle exception
        }
    }
    

}


           
    

            
            

           