package br.gov.mme.auth.util;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.swing.text.MaskFormatter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.text.ParseException;
import java.util.Base64;
import java.util.Locale;
import java.util.Random;

public final class Util {

    public static final String CELLPHONE_PATTERN = "(##) # ####-####";

    public static final int CELLPHONE_SIZE = 9;

    public static final String TELEPHONE_PATTERN = "(##) ####-####";

    private static final int ITERATION_COUNT = 19;

    public static final String CHARSET_UTF_8 = "UTF-8";

    public static final Locale LOCALE_BR = new Locale("pt", "BR");

    private final static byte[] SALT = { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32, (byte) 0x56, (byte) 0x34,
            (byte) 0xE3, (byte) 0x03 };

    private static final String SIM = "SIM";

    /**
     * Construtor privado para garantir o singleton.
     */
    private Util() {

    }

    /**
     * Verifica se o valor reported está vazio.
     *
     * @param value
     * @return
     */
    public static boolean isEmpty(final String value) {
        return StringUtils.isEmpty(value);
    }


    /**
     * Remove the non-numeric names from the 'value' entered.
     *
     * @param value
     * @return
     */
    public static String getOnlyNumber(String value) {

        if (!Util.isEmpty(value)) {
            value = value.replaceAll("[^\\d]", "");
        }
        return value;
    }

    /**
     * Realiza a decriptação do valor segundo a senha informada. <br>
     * O algoritimo e criptografia 'PBEWithMD5AndDES', utiliza o mecanismo 'DES' com
     * uma chave gerada pelo hash MD5 da 'senha', combinado com uma cadeia de
     * 'SALT'.
     *
     * @param value
     * @param password
     * @return
     */
    public static String encrypt(final String value, final String password) {

        SecretKey key = null;
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), SALT, ITERATION_COUNT);

        try {
            key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
            Cipher ecipher = Cipher.getInstance(key.getAlgorithm());
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(SALT, ITERATION_COUNT);
            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);

            byte[] utf8 = value.getBytes(CHARSET_UTF_8);
            byte[] enc = ecipher.doFinal(utf8);

            return Base64.getEncoder().encodeToString(enc);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Falha ao realiza a decriptação do valor.", e);
        }
    }

    /**
     * Realiza a decriptação do valor segundo a senha informada. <br>
     * O algoritimo e criptografia 'PBEWithMD5AndDES', utiliza o mecanismo 'DES' com
     * uma chave gerada pelo hash MD5 do 'senha', combinado com uma cadeia de
     * 'SALT'.
     *
     * @param value
     * @param password
     * @return
     */
    public static String decrypt(final String value, final String password) {

        SecretKey key = null;
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), SALT, ITERATION_COUNT);

        try {
            key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
            Cipher dcipher = Cipher.getInstance(key.getAlgorithm());

            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(SALT, ITERATION_COUNT);
            dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

            byte[] dec = Base64.getDecoder().decode(value);
            byte[] utf8 = dcipher.doFinal(dec);

            return new String(utf8, CHARSET_UTF_8);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Falha ao realiza a decriptação do valor.", e);
        }
    }

    /**
     * Returns the formatted value according to the informed pattern.
     *
     * @param value
     * @param pattern
     * @return
     */
    public static String getFormattedValue(final String value, final String pattern) {

        if (Util.isEmpty(value)) {
            return value;
        }

        try {
            MaskFormatter formatter = new MaskFormatter(pattern);
            formatter.setValueContainsLiteralCharacters(Boolean.FALSE);
            return formatter.valueToString(value);
        } catch (ParseException e) {
            throw new RuntimeException("Falha ao realiza a formação do valor.", e);
        }
    }

    /**
     * Returns random number 6 digits.
     *
     * @return
     */
    public static String getRandomNumberString() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }


    /**
     * Returns validacaoEnumSimNao
     * @param comp
     * @return
     */
    public static Boolean isSim(String comp) {
        return SIM.equals(comp.toUpperCase().trim());
    }

    public static File convertByteArrayToFile(byte[] byteArray, String fileName) throws IOException {
        File file = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(byteArray);
        }
        return file;
    }

    /**
     * Checks if the entered Mail is valid.
     *
     * @param mail
     * @return
     */
    public static boolean isValidMail(final String mail) {
        boolean valid = Boolean.TRUE;

        try {
            new InternetAddress(mail).validate();
        } catch (AddressException e) {
            valid = Boolean.FALSE;
        }
        return valid;
    }
}
