package com.geek.videoplay.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

/**
 * @desc 调用系统功能 拍照  视频 录音
 * Created by chenmeng on 2016/12/6.
 */
public class SystemFunUtil {
    private static final String TAG = SystemFunUtil.class.getName();
    private Activity context;
    private static String[] filePathColumn;//查询字段
    private static String sortOrder;//降序
    public static final int SYSTEM_IMAGE = 0;//图片
    public static final int SYSTEM_VIDEO = 1;//视频
    public static final int SYSTEM_VOICE = 2;//音频
    public static final int SYSTEM_IMAGE_PHONE = 3;//本地相册
    public static final String APP_FILE_NAMES = "SystemResources";
    private File imgFile;//保存图片的文件

    public SystemFunUtil(Activity context) {
        this.context = context;
    }

    public File getImgFile() {
        return imgFile;
    }

    public void setImgFile(File imgFile) {
        this.imgFile = imgFile;
    }


    /**
     * 创建根目录 自动区分是否在sd卡 还是内部储存
     * @param fileName 根目录 名称
     * @return File
     */
    public File createRootDirectory(String fileName){
        String filePath =  Environment.getExternalStorageDirectory().toString() + "/";
        File file = new File(filePath +  fileName);
        file.mkdir();
        return file;
    }

    /**
     * 打开系统自带的功能
     * 图片 视频  音频
     *
     * @param type 类别
     * @param flag 返回标识符
     */
    public void openCamera(int type, int flag) {
        try {
            switch (type) {
                case SYSTEM_IMAGE://拍摄图片
                    Intent img = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    FileNames name = new FileNames();
                    File appFile = createRootDirectory(APP_FILE_NAMES);
                    File file = new File(appFile.getPath(),name.getImageName());
                    this.setImgFile(file);
                    img.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    context.startActivityForResult(img, flag);
                    break;
                case SYSTEM_VIDEO://视频
                    Intent video = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    // 设置视频大小
                    video.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 768000);
                    // 设置视频时间 毫秒单位
                    video.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 45000);
                    context.startActivityForResult(video, flag);
                    break;
                case SYSTEM_VOICE://音频
                    Intent voice = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                    context.startActivityForResult(voice, flag);
                    break;
                case SYSTEM_IMAGE_PHONE://本地相册
                    Intent getAlbum = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    getAlbum.setType("image/*");
                    context.startActivityForResult(getAlbum, flag);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "打开系统功能失败!");
            e.printStackTrace();
        }
    }

    /**
     * 播放系统类型的资源
     *
     * @param path 资源的路径
     * @param type 类型
     * @return MediaPlayer
     */
    public MediaPlayer playerMedia(String path, int type) {
        switch (type) {
            case SYSTEM_IMAGE_PHONE:
            case SYSTEM_IMAGE:
                Intent img = new Intent(Intent.ACTION_VIEW);
                img.setDataAndType(Uri.fromFile(new File(path)), "image/*");
                context.startActivity(img);
                break;
            case SYSTEM_VIDEO:
                Intent video = new Intent(Intent.ACTION_VIEW);
                video.setDataAndType(Uri.parse(path), "video/*");
                context.startActivity(video);
                break;
            case SYSTEM_VOICE:
                MediaPlayer mMediaPlayer = null;
                try {
                    mMediaPlayer = new MediaPlayer();
                    mMediaPlayer.setDataSource(path);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return mMediaPlayer;
            default:
                break;
        }
        return null;
    }

    /**
     * 将资源uri转化为绝对路径（图片、音频、视频）
     *
     * @param uri uri
     * @return String
     */
    public String UriToPath(Uri uri, int type) {
        if (uri == null) {
            return null;
        }
        String scheme = uri.getScheme();//uri类型
        if (scheme.equals("file")) {
            String path = uri.getPath();
            return path;
        } else if (scheme.equals("content")) {
            switch (type) {
                case SYSTEM_IMAGE:
                    filePathColumn = new String[]{MediaStore.Images.Media.DATA};
                    sortOrder = MediaStore.Images.Media.DATA + " DESC";
                    break;
                case SYSTEM_VIDEO:
                    filePathColumn = new String[]{MediaStore.Video.Media.DATA};
                    sortOrder = MediaStore.Video.Media.DATA + " DESC";
                    break;
                case SYSTEM_VOICE:
                    filePathColumn = new String[]{MediaStore.Audio.Media.DATA};
                    sortOrder = MediaStore.Audio.Media.DATA + " DESC";
                    break;
                default:
                    break;
            }

            Cursor cursor = context.getContentResolver().query(uri,
                    filePathColumn, null, null, sortOrder);

            if (cursor == null) {
                return null;
            }
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            // 得到绝对路径
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        } else {
            return null;
        }
    }

    /**
     * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
     * @param imageUri
     */
    @TargetApi(19)
    public String getImageAbsolutePath(Uri imageUri) {
        if (context == null || imageUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[] { split[1] };
                return getDataColumn(contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    private String getDataColumn(Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

}
