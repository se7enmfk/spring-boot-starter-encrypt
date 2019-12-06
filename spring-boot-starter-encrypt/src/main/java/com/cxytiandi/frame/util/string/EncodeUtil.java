package com.cxytiandi.frame.util.string;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * 密码、文件的加密与解密 <br/>
 * 支持的算法有：MD5、SHA-256、SHA-512、SHA-384 <br/>
 *
 */

public class EncodeUtil {

   public final static String MD5 = "MD5";
   public final static String NONE = "NONE";
   public final static String SHA_256 = "SHA-256";
   public final static String SHA_512 = "SHA-512";
   public final static String SHA_384 = "SHA-384";

   /**
   * 加密算法
   * @param raw 明文
   * @param algorithm 加密算法名称
   * @return 密文
   */
   public static String encodeString(String raw, String alg) throws NoSuchAlgorithmException {
       String enc;
       if (alg == null || MD5.equals(alg)) {
    	   enc = DigestUtils.md5Hex(raw);
       } else if (NONE.equals(alg)) {
    	   enc = raw;
       } else if (SHA_256.equals(alg)) {
    	   enc = DigestUtils.sha256Hex(raw);
       } else if (SHA_384.equals(alg)) {
    	   enc = DigestUtils.sha384Hex(raw);
       } else if (SHA_512.equals(alg)) {
    	   enc = DigestUtils.sha512Hex(raw);
       } else {
    	   enc = DigestUtils.md5Hex(raw);
       }
       return enc;
   }

   /**
    * 加密密码算法，默认的加密算法是SHA_512
    * @param raw 明文
    * @return String 密文
    */
    public static String encodePassword(String raw) {
        try {
            if (raw != null && !"".equals(raw)) {
                return encodeString(raw, SHA_512);
            } else
                return null;
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException("Security error: " + nsae);
        }
    }
    /**
     * 判断明文和密文是不是相等，默认的加密算法是SHA_512
	 * @param raw 明文
	 * @param enc 密文
     */
    public static boolean isValid(String raw, String enc) {
        if (raw != null && !"".equals(raw)) {
        	System.out.println(encodePassword(raw));
            return enc.equals(encodePassword(raw));
        } else
            return false;
    }

   /**
   * 加密文件算法
   * @param filename 加密的文件名
   * @param algorithm  加密算法
   */
   public static void encodeFile(String filename, String algorithm) {
       byte[] b = new byte[1024 * 4];
       int len = 0;
       FileInputStream fis = null;
       FileOutputStream fos = null;
       try {
           MessageDigest md = MessageDigest.getInstance(algorithm);
           fis = new FileInputStream(filename);
           while ((len = fis.read(b)) != -1) {
               md.update(b, 0, len);
           }
           byte[] digest = md.digest();
           StringBuffer fileNameBuffer = new StringBuffer(128).append(filename).append(".").append(algorithm);
           fos = new FileOutputStream(fileNameBuffer.toString());
           OutputStream encodedStream = new Base64OutputStream(fos);
           encodedStream.write(digest);
           encodedStream.flush();
           encodedStream.close();
       } catch (Exception e) {
           System.out.println("Error computing Digest: " + e);
       } finally {
           try {
               if (fis != null)
                   fis.close();
           } catch (Exception ignored) {
           }
           try {
               if (fos != null)
                   fos.close();
           } catch (Exception ignored) {
           }
       }
   }

    public static void main(String[] args) throws NoSuchAlgorithmException {
     //   System.out.println(EncodeUtil.encodePassword("123456"));
     //   System.out.println(EncodeUtil.encodeString("123456", EncodeUtil.SHA_512));

     //   System.out.println(EncodeUtil.isValid("123456", EncodeUtil.encodePassword("123456")));
    }
}
