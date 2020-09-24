package com.example.demo.happy.step.utils;

import org.springframework.util.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.*;

/**
 * ZipUtil
 */
public class ZipUtil {

    private static final Charset USE_CHARSETS = StandardCharsets.ISO_8859_1;

    /**
     * 功能：使用gzip进行压缩，然后再用Base64进行编码
     *
     * @return 返回压缩后字符串
     * @author 20160827
     */
    @SuppressWarnings("restriction")
    public static String gzipB64(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(out);
        ) {
            gzip.write(str.getBytes(USE_CHARSETS));
            gzip.close();
            return new String(CryptUtils.base64Encrypt(out.toByteArray()), USE_CHARSETS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用gzip进行解压缩
     * 先对压缩数据进行BASE64解码。再进行Gzip解压
     *
     * @param compressedStr 压缩字符串
     * @return 返回解压字符串
     */
    @SuppressWarnings("restriction")
    public static String unGzipB64(String compressedStr) {
        if (StringUtils.isEmpty(compressedStr)) {
            return null;
        }
        String decompressed = null;
        byte[] base64Encrypt = CryptUtils.base64Decrypt(compressedStr.getBytes(USE_CHARSETS));
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ByteArrayInputStream in = new ByteArrayInputStream(base64Encrypt);
             GZIPInputStream gis = new GZIPInputStream(in)) {
            byte[] buffer = new byte[1024];
            int offset;
            while ((offset = gis.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            decompressed = out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return decompressed;
    }

    /**
     * 使用zip进行压缩
     *
     * @param str 压缩前的文本
     * @return 返回压缩后的文本
     */
    @SuppressWarnings("restriction")
    public static String zip(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(out);
        ) {
            zos.putNextEntry(new ZipEntry("0"));
            zos.write(str.getBytes());
            zos.closeEntry();
            return new String(CryptUtils.base64Encrypt(out.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用zip进行解压缩 压缩后的文本
     *
     * @return 解压后的字符串
     */
    @SuppressWarnings("restriction")
    public static String unZip(String compressedStr) {
        if (StringUtils.isEmpty(compressedStr)) {
            return compressedStr;
        }
        String decompressed = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ByteArrayInputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(compressedStr));
        ) {
            ZipInputStream zin = new ZipInputStream(in);
            zin.getNextEntry();
            byte[] buffer = new byte[1024];
            int offset;
            while ((offset = zin.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            decompressed = out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return decompressed;
    }

    /**
     * 功能：使用gzip进行压缩
     *
     * @return 返回压缩后字符串
     * @author 20160827
     */
    @SuppressWarnings("restriction")
    public static String gzip(String primStr) {
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
             GZIPOutputStream gout = new GZIPOutputStream(bout)) {
            //我这里用字节流输出的，所以转正byte[]
            //缓存数据用的字节数组流
            //压缩用的是这个流
            gout.write(primStr.getBytes());//把b写入到缓冲区中，也就是ByteArrayOutputStream
            gout.close(); //关闭流，也就是把数据全都刷到字节数组流中
            return new String(bout.toByteArray(), USE_CHARSETS);
        } catch (IOException e) {
            e.printStackTrace();//这个字节数组流关闭之后还能用，不用担心，从他里面把压缩好的数据拿出来，还是放在byte[]中
        }
        return null;
    }


    /**
     * 使用gzip进行解压缩
     *
     * @param compressedStr 压缩字符串
     * @return 返回解压字符串
     */
    @SuppressWarnings("restriction")
    public static String unGzip(String compressedStr) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ByteArrayInputStream bis = new ByteArrayInputStream(compressedStr.getBytes(USE_CHARSETS));
             GZIPInputStream gis = new GZIPInputStream(bis)) {
            int len;
            byte[] b1 = new byte[1024];
            while ((len = gis.read(b1)) != -1) {
                bos.write(b1, 0, len);
            }
            bos.close();
            return bos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * gzip压缩
     */
    public static String compress(String primStr) {
        if (StringUtils.isEmpty(primStr)) {
            return primStr;
        }

        try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
             GZIPOutputStream gout = new GZIPOutputStream(bout)
        ) {
            gout.write(primStr.getBytes());
            gout.close();
            return new BASE64Encoder().encode(bout.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * gzip解压
     */
    public static String uncompress(String compressedStr) {
        if (StringUtils.isEmpty(compressedStr)) {
            return null;
        }
        String decompressed = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(new BASE64Decoder().decodeBuffer(compressedStr)));) {
            byte[] buffer = new byte[1024];
            int offset;
            while ((offset = gis.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            decompressed = out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return decompressed;
    }

    public static void main(String[] args) {
        String str = "abc123,./><!@#$AAA";
        System.out.println(str);
        System.out.println("=================zip=================");
        String zip = zip(str);
        System.out.println(zip);
        System.out.println(unZip(zip));
        System.out.println("\n==================gzip================");
        String gzip = gzip(str);
        System.out.println(gzip);
        System.out.println(unGzip(gzip));
        System.out.println("\n==================gzip64================");
        String gzipB64 = gzipB64(str);
        System.out.println(gzipB64);
        System.out.println(unGzipB64(gzipB64));
        System.out.println("\n==================gzip2================");
        gzipB64 = compress(str);
        System.out.println(gzipB64);
        System.out.println(uncompress(gzipB64));
    }
}
