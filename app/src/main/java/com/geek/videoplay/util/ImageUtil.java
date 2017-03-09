package com.geek.videoplay.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @desc  图片压缩处理
 * Created by chenmeng on 2017/2/17.
 */

public class ImageUtil {

    /**
     * 把bitmap转换成String
     * @param filePath
     * @return
     */
    public static String bitmapToString(String filePath) {
        Bitmap bm = getSmallBitmap(filePath);//已压缩
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    /**
     * 根据路径获得图片并压缩，返回bitmap用于显示
     * @param filePath
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 计算图片的缩放值
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 将bitmap保存为jpg格式图片 到文件夹下
     * @param path
     * @param mBitmap
     */
    public static String saveBitmap(String path,Bitmap mBitmap,String names)  {
        if (mBitmap==null || TextUtils.isEmpty(path)){
            return null;
        }
        File f = new File(path,names);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            return f.getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 递归删除该文件夹下的所有文件
     *
     * @param folder
     */
    public static void deleteFolder(File folder) {
        File[] fileArray = folder.listFiles();
        if (fileArray==null){
            return;
        }
        if (fileArray.length == 0) {
            // 空文件夹则直接删除
//            folder.delete();
        } else {
            // 遍历该目录
            for (File currentFile : fileArray) {
                if (currentFile.exists() && currentFile.isFile()) {
                    // 文件则直接删除
                    currentFile.delete();
                } else {
                    // 递归删除
                    deleteFolder(currentFile);
                }
            }
//            folder.delete();
        }
    }

    /**
     * 删除文件
     * @param filePath
     * @return
     */
    public static boolean deleteFilePath(String filePath) {
        File file = new File(filePath);
        if (file == null || !file.exists() || file.isDirectory())
            return false;
        file.delete();
        return true;
    }
}
