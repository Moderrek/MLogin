package pl.moderr.mlogin;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class ModerrEncrypt {

    private static SecretKeySpec secretKey;
    private static byte[] key;

    public static void setKey(final String myKey) {
        MessageDigest sha = null;
        try {
            ModerrEncrypt.key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            ModerrEncrypt.key = sha.digest(ModerrEncrypt.key);
            ModerrEncrypt.key = Arrays.copyOf(ModerrEncrypt.key, 16);
            ModerrEncrypt.secretKey = new SecretKeySpec(ModerrEncrypt.key, "AES");
        }
        catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(final String strToEncrypt, final String secret) {
        try {
            setKey(secret);
            final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(1, ModerrEncrypt.secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        }
        catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
            return null;
        }
    }

    public static String decrypt(final String strToDecrypt, final String secret) {
        try {
            setKey(secret);
            final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(2, ModerrEncrypt.secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        }
        catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
            return null;
        }
    }

}
