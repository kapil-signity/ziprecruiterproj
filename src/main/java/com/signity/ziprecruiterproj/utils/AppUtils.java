package com.signity.ziprecruiterproj.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.signity.ziprecruiterproj.configs.CommandArguments;
import com.signity.ziprecruiterproj.models.LoginCredential;
import com.signity.ziprecruiterproj.models.ParsedDataModal;
import com.signity.ziprecruiterproj.models.PropertyFile;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.*;
import java.lang.reflect.Type;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

@Component
public class AppUtils {
    // 8-byte Salt
    byte[] salt = {
            (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
            (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
    };
    // Iteration count
    int iterationCount = 19;
    public static String secretKey="signitySecretKey";

    @Autowired
    private Gson gson;

    /**
     * Get Publisher Placements Map
     * @param
     */
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

    /**
     * Get File obj Instance
     * @param
     */
    private File getFileObject(String fileName) {
        /*
         * This code will get the file from the resources folder
         */
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource(fileName).getFile());
    }

    /**
     * Get Login Credentials Configs
     * @param
     */
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

    /**
     * Get Month to select in Report Dropdown as String
     * @param
     * @return String
     */
    public String getMonthToSelect() {
        Integer endDate=CommandArguments.endDate;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR,endDate);
        SimpleDateFormat sfd=new SimpleDateFormat("MMM yyyy");
        return sfd.format(cal.getTime());
    }

    /**
     * Get Event Date Object for Modal by @param dateString
     * @param dateString
     * @return Date
     */
    public Date getEventDateByString(String dateString){
        SimpleDateFormat sdfInput=new SimpleDateFormat("MM/dd/yy");
        Date dateIn= null;
        try {
            dateIn = sdfInput.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateIn;
    }

    /**
     * Save ParsedDataModalList as File
     * @param parsedDataModals list of parsedDataModal for File
     * @param fileName name of file to store
     * @return void
     */
    public void saveJsonData(List<ParsedDataModal> parsedDataModals, String fileName){
        try {
            FileWriter fr=new FileWriter(fileName,true);
            fr.write(gson.toJson(parsedDataModals));
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Save ParsedDataModalList as File
     * @param parsedDataModals list of parsedDataModal for File
     * @param fileName name of file to store
     * @return void
     */
    public void saveRedshiftData(List<ParsedDataModal> parsedDataModals, String fileName){
        try {
            FileWriter fstream=new FileWriter(fileName,true);
            BufferedWriter out = new BufferedWriter(fstream);
            for(ParsedDataModal data:parsedDataModals){
                out.write(gson.toJson(data));
                out.newLine();
            }
            out.close();
            fstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * removeNonDigitChars
     * @param input alphanumeric charsequence
     * @return string numeric string
     */
    public String removeNonDigitChars(String input){
        return input.replaceAll("[^\\d.]","");
    }


    public static void main(String[] args) throws Exception {
        AppUtils cryptoUtil=new AppUtils();
        String plain="sarasota97";
        String enc=cryptoUtil.encrypt(secretKey, plain);
        System.out.println("Original text: "+plain);
        System.out.println("Encrypted text: "+enc);
        String plainAfter=cryptoUtil.decrypt(secretKey, enc);
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
