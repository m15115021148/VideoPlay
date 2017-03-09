package com.geek.videoplay.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/9.
 */

public class FileUtil {

    /**
     * 拷贝一个文件,srcFile源文件，destFile目标文件
     *
     * @param srcFile
     * @param destFile
     * @throws IOException
     */
    public static boolean copyFileTo(File srcFile, File destFile) throws IOException {
        if (srcFile.isDirectory() || destFile.isDirectory()){
            return false;// 判断是否是文件
        }else {
            FileInputStream fis = new FileInputStream(srcFile);
            FileOutputStream fos = new FileOutputStream(destFile);
            int readLen = 0;
            byte[] buf = new byte[1024];
            while ((readLen = fis.read(buf)) >0) {
                fos.write(buf, 0, readLen);
            }
            fos.flush();
            fos.close();
            fis.close();
            return true;
        }

    }

    /**
     * 遍历文件夹下所以的文件夹
     *
     * @param path 文件路径
     * @return 返回文件
     */
    public static List<File> getListFiles(String path) {
        List<File> list = new ArrayList<File>();
        File file = new File(path);
        File[] fileArray = file.listFiles();
        if (fileArray==null){
            return list;
        }
        if (fileArray.length == 0) {
            return list;
        } else {
            // 遍历该目录
            for (File currentFile : fileArray) {
                if (currentFile.isDirectory()) {
                    // 文件则
                    list.add(currentFile);
                } else {
                    // 递归
                    getListFiles(currentFile);
                }
            }
        }
        return list;
    }

    /**
     * 遍历文件夹下所以的文件
     *
     * @param file 文件夹
     * @return 返回文件的路径
     */
    public static List<String> getListFiles(File file) {
        List<String> list = new ArrayList<String>();
        File[] fileArray = file.listFiles();
        if (fileArray==null){
            return null;
        }
        if (fileArray.length == 0) {
            return null;
        } else {
            // 遍历该目录
            for (File currentFile : fileArray) {
                if (currentFile.isFile()) {
                    // 文件则
                    list.add(currentFile.getPath());
                } else {
                    // 递归
                    getListFiles(currentFile);
                }
            }
        }
        return list;
    }

}
