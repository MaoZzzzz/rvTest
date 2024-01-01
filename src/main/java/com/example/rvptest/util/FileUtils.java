package com.example.rvptest.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @Description: 文件操作工具类
 * @Author: Mzz
 * @Date: 2023/12/10 12:41
 */
public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 存储位置所在文件夹
     */
    public static final String STORAGE_LOCATION_DIR = "/workdir/logs/";

    /**
     * @Description 追加字符串到文件中
     * @Param path 文件路径
     * @Param data 追加的数据
     * @Param encoder 编码格式
     * @Return none
     */
    public static void stringAppendFile(String fileName, String content) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(STORAGE_LOCATION_DIR + fileName, true);
            fos.write(content.getBytes());
            fos.write("\r\n".getBytes());
        } catch (IOException e) {
            logger.error("追加失败");
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    logger.error("关闭失败");
                }
            }
        }
    }
}
