/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.service;

import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.logging.Level;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 *
 * @author vinoy
 */
public class DecryptPasswordBeforeAuthentication extends UsernamePasswordAuthenticationFilter {
	
	
	
	String key = "C2R0G1R3a2r0o1c8";

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        try {
            return "admin!01";
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(DecryptPasswordBeforeAuthentication.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    // commented for SSO

	/*
	 * @Override public String obtainPassword(HttpServletRequest request) { try {
	 * String encrypted = request.getParameter(getPasswordParameter()); HttpSession
	 * session = request.getSession(false); String password = session.getId();
	 * String salt = request.getParameter("salt"); String iv =
	 * request.getParameter("iv"); byte[] saltBytes = hexStringToByteArray(salt);
	 * byte[] ivBytes = hexStringToByteArray(iv); IvParameterSpec ivParameterSpec =
	 * new IvParameterSpec(ivBytes); SecretKeySpec sKey = (SecretKeySpec)
	 * generateKeyFromPassword(password, saltBytes); try {
	 * 
	 * return decrypt(encrypted, sKey, ivParameterSpec); } catch (Exception ex) {
	 * java.util.logging.Logger.getLogger(DecryptPasswordBeforeAuthentication.class.
	 * getName()).log(Level.SEVERE, null, ex); } } catch (GeneralSecurityException
	 * ex) {
	 * java.util.logging.Logger.getLogger(DecryptPasswordBeforeAuthentication.class.
	 * getName()).log(Level.SEVERE, null, ex); } return null; }
	 * 
	 * public static SecretKey generateKeyFromPassword(String password, byte[]
	 * saltBytes) throws GeneralSecurityException { KeySpec keySpec = new
	 * PBEKeySpec(password.toCharArray(), saltBytes, 100, 128); SecretKeyFactory
	 * keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1"); SecretKey
	 * secretKey = keyFactory.generateSecret(keySpec); return new
	 * SecretKeySpec(secretKey.getEncoded(), "AES"); }
	 * 
	 * public static byte[] hexStringToByteArray(String s) { int len = s.length();
	 * byte[] data = new byte[len / 2]; for (int i = 0; i < len; i += 2) { data[i /
	 * 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) +
	 * Character.digit(s.charAt(i + 1), 16)); } return data; }
	 * 
	 * public static String decrypt(String encryptedData, SecretKeySpec sKey,
	 * IvParameterSpec ivParameterSpec) throws Exception { Cipher c =
	 * Cipher.getInstance("AES/CBC/PKCS5Padding"); c.init(Cipher.DECRYPT_MODE, sKey,
	 * ivParameterSpec); byte[] decordedValue; decordedValue =
	 * Base64.getDecoder().decode(encryptedData); byte[] decValue =
	 * c.doFinal(decordedValue); String decryptedValue = new String(decValue);
	 * return decryptedValue; }
	 * 
	 */}
