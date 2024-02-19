package lime.utils.other.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class CipherEncryption {

    private SecretKeySpec secretKey;
    public static boolean passCheck = false;

    public CipherEncryption(String keyEncryption) {
        MessageDigest sha = null;
        byte[] key;
        try {
            key = keyEncryption.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        }
        catch (Exception e) {
            secretKey = null;
        }
    }

    public String encrypt(String strToEncrypt)
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8"))).replaceAll("&", "%").replaceAll("=", "!").replaceAll("/", "_").replaceAll("\\+", "-");
        }
        catch (Exception e)
        {
        }
        return null;
    }

    public String decrypt(String strToDecrypt)
    {
        try
        {
            passCheck = true;
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt.replaceAll("%", "&").replaceAll("!", "=").replaceAll("_", "/").replaceAll("-", "\\+"))));
        }
        catch (Exception e)
        {
        }
        return null;
    }
}
