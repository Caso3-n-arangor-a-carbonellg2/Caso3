import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SeguridadCliente {

    int id;

    String nombreArchivo;

    BigInteger P;
    BigInteger G;
    int g;
    BigInteger x;
    BigInteger y;
    BigInteger Gx;
    BigInteger Gy;
    BigInteger z;

    IvParameterSpec iv;

    Key llaveSimetricaParaCifrar;
    Key llaveSimetricaParaHMAC;

    public static final int numeroPuerto = 1234;
    public static final String servidor = "localhost";

    DataInputStream inConsola;
    DataInputStream inServer;
    DataOutputStream outServer;

    boolean ejecutar;

    PublicKey llavePublicaServer;
    static final String llavePublicaServerSrt = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCkFB6c5xyfKjSdTXlJApRdGaKCP/QhmtZMwKGDW8vBXz+NlcFO6IFdAN7B39NVFhAV+zhiyT9jnBJy300rtN1CfwxTbSqPNFbAXbLmqdMtoN+D6Dh27UhDWIeFilq7m5XMkUlXyaWi5DMq1J+FgBFFE3WvWepEcfNc8iozP1t62QIDAQAB";

    public static final BigInteger RETO = new BigInteger(256, new SecureRandom());;

    SeguridadCliente(DataInputStream inConsolaP,
            DataInputStream inServerP,
            DataOutputStream outServerP) {

        this.inConsola = inConsolaP;
        this.inServer = inServerP;
        this.outServer = outServerP;

        this.ejecutar = true;
    }

    /**
     * public static void procesar (BufferedReader inConsola, BufferedReader
     * inServer, PrintWriter outServer){
     * }
     * 
     * @throws Exception
     */
    public void procesar() throws Exception {

        String fromServer = null;

        obtenerLlaveAsimetrica();

        // Paso 1
        System.out.println("Se envia al servidor \"Secure init\" con mensaje: \"RETO\"");
        this.outServer.writeUTF("SECURE INIT" + "," + SeguridadCliente.RETO.toString()); // Se envia la primera
        // instruccion

        // Paso 3
        String firmaRETO = null;
        firmaRETO = this.inServer.readUTF();

        // Paso 4
        boolean isValid = verificarFirmaConexion(firmaRETO); // Se verifica si la firma coincide con lo que se envio
        if (isValid) {
            this.outServer.writeUTF("OK");
        } else {
            this.outServer.writeUTF("ERROR");
            throw new SignatureException("La firma digital no es válida."); // El cliente se detiene si el digest es
                                                                            // incorrecto
        }

        System.out.println("La firma que paso el servidor esta correcto y los datos son consistentes");

        // Paso 8
        fromServer = null;
        fromServer = this.inServer.readUTF();
        this.G = new BigInteger(fromServer);

        fromServer = this.inServer.readUTF();
        this.P = new BigInteger(fromServer);

        fromServer = this.inServer.readUTF();
        this.Gx = new BigInteger(fromServer);

        fromServer = this.inServer.readUTF();
        this.iv = new IvParameterSpec(Base64.getDecoder().decode(fromServer));

        String GPGxFirmado = this.inServer.readUTF();
        String GPGxCliente = this.G.toString() + "," + this.P.toString() + "," + this.Gx.toString(); // GPGx = G,P,G^x
        System.out.println("Se rreciben lso numero primos.");

        // Paso 9
        boolean isValidDiffie = verificarFirmaDiffie(GPGxFirmado, GPGxCliente);
        if (isValidDiffie) {
            this.outServer.writeUTF("OK");
        } else {
            this.outServer.writeUTF("ERROR");
            throw new SignatureException("La firma digital no es válida."); // El cliente se detiene si el los numero
                                                                            // recibidos no son consistentes con la
                                                                            // firma
        }

        // Paso 10
        this.y = new BigInteger(256, new SecureRandom());
        this.Gy = this.G.modPow(this.y, this.P);

        this.outServer.writeUTF(this.Gy.toString(g)); // Se calcula G^y mod P

        // Paso 11.a
        this.z = this.Gx.modPow(this.y, this.P);

        byte[] bytesDeZ = z.toByteArray();
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        byte[] hash = digest.digest(bytesDeZ); // Es de tamanio 512 bits, ppor tanto 64 Bytes
        byte[] primeraMitasHash = new byte[hash.length / 2]; // De tamanio 32 Bytes, porque es la mitad del tamanio del
                                                             // Hash
        byte[] SegundaMitasHash = new byte[hash.length / 2];

        for (int i = 0; i < hash.length; i++) {
            if (i < (hash.length / 2)) {
                primeraMitasHash[i] = hash[i];
            } else {
                SegundaMitasHash[i - (hash.length / 2)] = hash[i];
            }
        }

        this.llaveSimetricaParaCifrar = generarLlaveSecreta(primeraMitasHash);
        this.llaveSimetricaParaHMAC = generarLlaveSecreta(SegundaMitasHash);

        System.out.println("Se generaron las llaves secretas para cifrar y para el HMAC");

        // Paso 12
        fromServer = null;
        fromServer = inServer.readUTF();
        if (!fromServer.equals("CONTINUAR")) {
            throw new SignatureException("Se deberia haber pasado \"CONTINUAR\""); // El cliente se detiene si el los
                                                                                   // numero recibidos no son
                                                                                   // consistentes con la firma
        }
        if ("CONTINUAR".equals(fromServer)) {
            // Convertir los bytes cifrados a String para enviar
            String loginCifrado = Base64.getEncoder()
                    .encodeToString(Servidor.cifrar("login", llaveSimetricaParaCifrar, iv.getIV()));
            String passwordCifrado = Base64.getEncoder()
                    .encodeToString(Servidor.cifrar("password", llaveSimetricaParaCifrar, iv.getIV()));

            outServer.writeUTF(loginCifrado); // Enviar login cifrado
            outServer.writeUTF(passwordCifrado); // Enviar contraseña cifrada

            // Recibir verificación de login y contraseña
            String verificationResult = inServer.readUTF();
            if ("OK".equals(verificationResult)) {
                // Convertir los bytes cifrados a String para enviar
                String consultaCifrada = Base64.getEncoder()
                        .encodeToString(Servidor.cifrar("consulta", llaveSimetricaParaCifrar, iv.getIV()));
                outServer.writeUTF(consultaCifrada); // Enviar consulta cifrada

                // Recibir HMAC de consulta y verificar
                String hmacConsulta = inServer.readUTF();
                byte[] hmacConsultaBytes = Base64.getDecoder().decode(hmacConsulta);
                if (Arrays.equals(generarHMAC("consulta".getBytes()), hmacConsultaBytes)) {
                    // Continuar con la comunicación
                } else {
                    // Terminar la conexión
                }
            }
        }
    }

    public SecretKey generarLlaveSecreta(byte[] listaBytes) {

        SecretKey llaveSecreta = new SecretKeySpec(listaBytes, 0, listaBytes.length, "AES");
        return llaveSecreta;
    }

    public byte[] generarHMAC(byte[] textoBytes) throws InvalidKeyException, NoSuchAlgorithmException {

        Mac hMac = Mac.getInstance("HmacSHA256");
        hMac.init(this.llaveSimetricaParaHMAC);
        byte[] hmacBytes = hMac.doFinal(textoBytes);
        return hmacBytes;
    }

    public boolean verificarFirmaDiffie(String aVerificar, String certificado)
            throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        byte[] listaBytes = Base64.getDecoder().decode(aVerificar);
        Signature signature2 = null;
        boolean isValid = false;

        signature2 = Signature.getInstance("SHA256withRSA");
        signature2.initVerify(this.llavePublicaServer);
        signature2.update(certificado.getBytes());
        isValid = signature2.verify(listaBytes);
        return isValid;
    }

    public boolean verificarFirmaConexion(String aVerificar)
            throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        byte[] listaBytes = Base64.getDecoder().decode(aVerificar);
        Signature signature2 = null;
        boolean isValid = false;

        signature2 = Signature.getInstance("SHA256withRSA");
        signature2.initVerify(this.llavePublicaServer);
        signature2.update(RETO.toByteArray());
        isValid = signature2.verify(listaBytes);
        return isValid;
    }

    public void detener() {
        this.ejecutar = false;
    }

    public void salirPrograma() {
        System.out.println("SEl servidor respondio algo incorrecto y se genero un error");
        System.exit(-1);
    }

    /**
     * @throws IOException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    private void obtenerLlaveAsimetrica() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {

        // Creacion de llave publica
        byte[] bytesLlavePublica = Base64.getDecoder().decode(llavePublicaServerSrt);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        this.llavePublicaServer = keyFactory.generatePublic(new X509EncodedKeySpec(bytesLlavePublica));

    }

}
