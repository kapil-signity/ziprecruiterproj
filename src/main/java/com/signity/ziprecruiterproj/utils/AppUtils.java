package com.signity.ziprecruiterproj.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.signity.ziprecruiterproj.configs.CommandArguments;
import com.signity.ziprecruiterproj.models.LoginCredential;
import com.signity.ziprecruiterproj.models.ParsedDataModal;
import com.signity.ziprecruiterproj.models.PropertyFile;

@Component
public class AppUtils {
	// 8-byte Salt
	byte[] salt = { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32, (byte) 0x56, (byte) 0x35, (byte) 0xE3,
			(byte) 0x03 };
	// Iteration count
	int iterationCount = 19;
	public static String secretKey = "signitySecretKey";

	@Autowired
	private Gson gson;

	/**
	 * Get Publisher Placements Map
	 * 
	 * @param
	 */
	public Map<String, PropertyFile> getSourceDataMap() {
		Map<String, PropertyFile> map = null;
		try {
			File file = new File("config.json");
			Type typeOfHashMap = new TypeToken<Map<String, PropertyFile>>() {//
			}.getType();
			return gson.fromJson(new FileReader(file), typeOfHashMap);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * Get Login Credentials Configs
	 * 
	 * @param
	 */
	public LoginCredential getLoginCredentials() {
		LoginCredential loginCredential = null;
		try {
			File file = new File("loginCredential.json");
			Type typeOfHashMap = new TypeToken<LoginCredential>() {//
			}.getType();
			return gson.fromJson(new FileReader(file), typeOfHashMap);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return loginCredential;
	}

	/**
	 * Get Month to select in Report Dropdown as String
	 * 
	 * @param
	 * @return String
	 */
	public String getMonthToSelect() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, CommandArguments.endDate);
		SimpleDateFormat sfd = new SimpleDateFormat("MMM yyyy");
		return sfd.format(cal.getTime());
	}

	/**
	 * Get Start Date of Month
	 * @return
	 */
	public Date getStartDate() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, CommandArguments.endDate);
		cal.set(Calendar.DAY_OF_MONTH,CommandArguments.startDate==0?1:CommandArguments.startDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		SimpleDateFormat sdfInput = new SimpleDateFormat("MM/dd/yy");
		try {
			return sdfInput.parse(sdfInput.format(cal.getTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Get End Date until data required
	 * @return
	 */
	public Date getEndDate() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, CommandArguments.endDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		SimpleDateFormat sdfInput = new SimpleDateFormat("MM/dd/yy");
		try {
			return sdfInput.parse(sdfInput.format(cal.getTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * Get Event Date Object for Modal by @param dateString
	 * 
	 * @param dateString
	 * @return Date
	 */
	public Date getEventDateByString(String dateString) {
		SimpleDateFormat sdfInput = new SimpleDateFormat("MM/dd/yy");
		Date dateIn = null;
		try {
			dateIn = sdfInput.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateIn;
	}

	/**
	 * Save ParsedDataModalList as File
	 * 
	 * @param parsedDataModals
	 *            list of parsedDataModal for File
	 * @param fileName
	 *            name of file to store
	 * @return void
	 */
	public void saveJsonData(List<ParsedDataModal> parsedDataModals, String fileName) {
		try (FileWriter fr = new FileWriter(fileName, true);) {
			fr.write(gson.toJson(parsedDataModals));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save ParsedDataModalList as File
	 * 
	 * @param parsedDataModals
	 *            list of parsedDataModal for File
	 * @param fileName
	 *            name of file to store
	 * @return void
	 */
	public void saveRedshiftData(List<ParsedDataModal> parsedDataModals, String fileName) {

		try (FileWriter fstream = new FileWriter(fileName, true); BufferedWriter out = new BufferedWriter(fstream);) {
			for (ParsedDataModal data : parsedDataModals) {
				out.write(gson.toJson(data));
				out.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Delete the file of given path
	 * @param path File Absolute path to Delete
	 *
	 */
	public void deleteFileIfExist(String path){
		File file=new File(path);
		if(file.exists()){
			file.delete();
		}
	}
	/**
	 * removeNonDigitChars
	 * 
	 * @param input
	 *            alphanumeric charsequence
	 * @return string numeric string
	 */
	public String removeNonDigitChars(String input) {
		return input.replaceAll("[^\\d.]", "");
	}

	 public static void main(String[] args) throws Exception {

	 AppUtils cryptoUtil = new AppUtils();
	 String plain = "Tarzon The Wonder car";
	 String enc = cryptoUtil.encrypt( plain);
	 System.out.println("Original text: " + plain);
	 System.out.println("Encrypted text: " + enc);
	 String plainAfter = cryptoUtil.decrypt( enc);
	 System.out.println("Original text after decryption: " + plainAfter);
	 }

	/**
	 *
	 * @param secretKey
	 *            Key used to encrypt data
	 * @param plainText
	 *            Text input to be encrypted
	 * @return Returns encrypted text
	 */
	public String encrypt(String plainText) throws Exception {
		// Key generation for enc and desc
		KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt, iterationCount);
		SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
		// Prepare the parameter to the ciphers
		AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

		// Enc process
		Cipher ecipher = Cipher.getInstance(key.getAlgorithm());
		ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
		String charSet = "UTF-8";
		byte[] in = plainText.getBytes(charSet);
		byte[] out = ecipher.doFinal(in);
		String encStr = new String(Base64.getEncoder().encode(out));
		return encStr;
	}

	/**
	 * @param secretKey
	 *            Key used to decrypt data
	 * @param encryptedText
	 *            encrypted text input to decrypt
	 * @return Returns plain text after decryption
	 */
	public String decrypt(String encryptedText) throws Exception {
		// Key generation for enc and desc
		KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt, iterationCount);
		SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
		// Prepare the parameter to the ciphers
		AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
		// Decryption process; same key will be used for decr
		Cipher dcipher = Cipher.getInstance(key.getAlgorithm());
		dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
		byte[] enc = Base64.getDecoder().decode(encryptedText);
		byte[] utf8 = dcipher.doFinal(enc);
		String charSet = "UTF-8";
		String plainStr = new String(utf8, charSet);
		return plainStr;
	}
}
