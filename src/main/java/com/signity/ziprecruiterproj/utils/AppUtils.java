package com.signity.ziprecruiterproj.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.signity.ziprecruiterproj.models.LoginCredential;
import com.signity.ziprecruiterproj.models.PropertyFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Map;

@Component
public class AppUtils {
    // 8-byte Salt
    byte[] salt = {
            (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
            (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
    };
    // Iteration count
    int iterationCount = 19;

    @Autowired
    private Gson gson;

    public Map<String, PropertyFile> getSourceDataMap(){
        Map<String, PropertyFile> map=null;
        try {
            File file=getFileObject("config.json");
            Type typeOfHashMap = new TypeToken<Map<String, PropertyFile>>() { }.getType();
            return gson.fromJson(new FileReader(file),typeOfHashMap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return map;
    }

    private File getFileObject(String fileName) {
        /*
         * This code will get the file from the resources folder
         */
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource(fileName).getFile());
    }


    public LoginCredential getLoginCredentials(){
        LoginCredential loginCredential=null;
        try {
            File file=getFileObject("loginCredential.json");
            Type typeOfHashMap = new TypeToken<LoginCredential>() { }.getType();
            return gson.fromJson(new FileReader(file),typeOfHashMap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return loginCredential;
    }


    public static void main(String[] args) throws Exception {
        AppUtils cryptoUtil=new AppUtils();
        String key="ezeon8547";
        String plain="This is an important message";
        String enc=cryptoUtil.encrypt(key, plain);
        System.out.println("Original text: "+plain);
        System.out.println("Encrypted text: "+enc);
        String plainAfter=cryptoUtil.decrypt(key, enc);
        System.out.println("Original text after decryption: "+plainAfter);
    }

    /**
     *
     * @param secretKey Key used to encrypt data
     * @param plainText Text input to be encrypted
     * @return Returns encrypted text
     */
    public String encrypt(String secretKey, String plainText)
            throws Exception{
        //Key generation for enc and desc
        KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt, iterationCount);
        SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
        // Prepare the parameter to the ciphers
        AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

        //Enc process
        Cipher ecipher = Cipher.getInstance(key.getAlgorithm());
        ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
        String charSet = "UTF-8";
        byte[] in = plainText.getBytes(charSet);
        byte[] out = ecipher.doFinal(in);
        String encStr = new String(Base64.getEncoder().encode(out));
        return encStr;
    }

    /**
     * @param secretKey Key used to decrypt data
     * @param encryptedText encrypted text input to decrypt
     * @return Returns plain text after decryption
     */
    public String decrypt(String secretKey, String encryptedText)
            throws Exception{
        //Key generation for enc and desc
        KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt, iterationCount);
        SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
        // Prepare the parameter to the ciphers
        AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
        //Decryption process; same key will be used for decr
        Cipher dcipher = Cipher.getInstance(key.getAlgorithm());
        dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        byte[] enc = Base64.getDecoder().decode(encryptedText);
        byte[] utf8 = dcipher.doFinal(enc);
        String charSet = "UTF-8";
        String plainStr = new String(utf8, charSet);
        return plainStr;
    }
}
