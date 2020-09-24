package com.example.demo.happy.step.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

/**
 * <p>
 * ResourceUtil
 * </p>
 *
 * @author zhenghao
 * @date 2020/9/24 15:17
 */
public class ResourceUtil {

    public static void closeResource(Reader r) {
        if (null != r) {
            try {
                r.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void closeResource(OutputStream r) {
        if (null != r) {
            try {
                r.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void closeResource(InputStream r) {
        if (null != r) {
            try {
                r.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
