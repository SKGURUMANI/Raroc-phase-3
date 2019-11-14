/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.util;

import atrix.common.service.DecryptPasswordBeforeAuthentication;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;
import java.util.Map;
import java.util.*;
import java.util.TreeMap;
import java.util.logging.Level;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Amolraj
 */
public class PasswordWrappedRequest extends HttpServletRequestWrapper {

    public PasswordWrappedRequest(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        // posted values a et b
        if (name.equals("password")) {
            String[] values = super.getParameterValues(name);
            String pass = obtainPass(this,values[0]);
            return pass;
        }
        // other cases
        return super.getParameter(name);
    }

    protected String obtainPass(HttpServletRequest request, String data) {
        try {
            String encrypted = data;
            HttpSession session = request.getSession(false);
            String password = session.getId();
            String salt = request.getParameter("salt");
            String iv = request.getParameter("iv");
            byte[] saltBytes = hexStringToByteArray(salt);
            byte[] ivBytes = hexStringToByteArray(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
            SecretKeySpec sKey = (SecretKeySpec) generateKeyFromPassword(password, saltBytes);
            try {
                return decrypt(encrypted, sKey, ivParameterSpec);
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(DecryptPasswordBeforeAuthentication.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (GeneralSecurityException ex) {
            java.util.logging.Logger.getLogger(DecryptPasswordBeforeAuthentication.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static SecretKey generateKeyFromPassword(String password, byte[] saltBytes)
            throws GeneralSecurityException {
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), saltBytes, 100, 128);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey secretKey = keyFactory.generateSecret(keySpec);
        return new SecretKeySpec(secretKey.getEncoded(), "AES");
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String decrypt(String encryptedData, SecretKeySpec sKey,
            IvParameterSpec ivParameterSpec) throws Exception {
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, sKey, ivParameterSpec);
        byte[] decordedValue;
        decordedValue = Base64.getDecoder().decode(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

}
