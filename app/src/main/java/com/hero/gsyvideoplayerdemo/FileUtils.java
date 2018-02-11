package com.hero.gsyvideoplayerdemo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * context.getExternalCacheDir()  、 context.getCacheDir()
 * 相同点：
 * 1、相同点：都可以做app缓存目录。
 * 2、app卸载后，两个目录下的数据都会被清空。
 * 不同点：
 * 1、目录的路径不同。前者的目录存在外部SD卡上的。后者的目录存在app的内部存储上。
 * 2、前者的路径在手机里可以直接看到。后者的路径需要root以后，用Root Explorer 文件管理器才能看到。
 */

public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();

    /**
     * 生成 图片文件   在sd卡
     */
    public static File createImageFile(String dirName) {
        File appDir = null;
        try {
            appDir = new File(Environment.getExternalStorageDirectory(), dirName);
            if (!appDir.exists()) {
                appDir.mkdir();
            }
        } catch (Exception e) {

        }
        String fileNmae = System.currentTimeMillis() + ".png";
        return new File(appDir, fileNmae);
    }

    /**
     * 生成 图片文件    在缓存目录中
     */
    public static File createImageFile(Context mContext, String dirName) {
        File appDir = null;
        try {
            appDir = new File(mContext.getCacheDir().getPath(), dirName);
            if (!appDir.exists()) {
                appDir.mkdir();
            }
        } catch (Exception e) {

        }
        String fileNmae = System.currentTimeMillis() + ".png";
        return new File(appDir, fileNmae);
    }

    /**
     * 在SD卡上创建一个文件夹
     */

    public static String createDirFile(String saveDir) {
        String savePath = "";
        try {
            // 下载位置
            File downloadFile = new File(Environment.getExternalStorageDirectory(), saveDir);
            if (!downloadFile.mkdirs()) {
                downloadFile.createNewFile();
            }
            savePath = downloadFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return savePath;
    }

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }

    /**
     * 生成 APP内部文件夹
     */
    public static File createAppDirFile(Context mContext, String dirName) {
        File appDir = null;
        try {
            appDir = new File(mContext.getExternalCacheDir().getPath(), dirName);
            if (!appDir.exists()) {
                appDir.mkdir();
            }
        } catch (Exception e) {
        }
        return appDir;
    }

    /**
     * 生成 APP内部文件夹
     */
    public static String createAppDirFilePath(Context mContext, String dirName) {
        File appDir = null;
        String filePath = "";
        try {
            appDir = new File(mContext.getCacheDir().getPath(), dirName);
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            filePath = appDir.getAbsolutePath();
        } catch (Exception e) {
        }
        return filePath;
    }

    /**
     * 生成 APP内部文件夹
     */
    public static String createAppdirFilePath(Context mContext, String dirName) {
        File appDir = null;
        String filePath = "";
        try {
            appDir = new File(mContext.getCacheDir().getPath(), dirName);
            if (!appDir.mkdirs()) {
                appDir.mkdir();
            }
            filePath = appDir.getAbsolutePath();
        } catch (Exception e) {
        }
        return filePath;
    }

    /**
     * 对文件重命名
     *
     * @param filePath 文件的路径     reName  新文件名  不含后缀
     */
    public static String chageFileName(String filePath, String reName) {
        try {
            File file = new File(filePath);
            //前面路径必须一样才能修改成功
            String path = filePath.substring(0, filePath.lastIndexOf("/") + 1) + reName + filePath.substring(filePath.lastIndexOf("."), filePath.length());
            File newFile = new File(path);
            file.renameTo(newFile);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
            return filePath;
        }
    }

    /**
     * oldPath: 图片缓存的路径
     * newPath: 图片缓存copy的路径
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int byteRead;
            File oldFile = new File(oldPath);
            if (oldFile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteRead = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteRead);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制文件操作出错");
            e.printStackTrace();
        }
    }

    //判断文件是否存在
    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //判断网络文件大小
    public static int getHttpfileLength(String fileUrl) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(fileUrl).openConnection();
            int contentLength = connection.getContentLength();
            return contentLength;
        } catch (Exception e) {
            return 0;
        }
    }

    //把文件插入系统图库
    public static void saveImageToGallery(Context context, File file, String title) {
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), title, null);
            //        没有立刻显示在图库中，而我们需要立刻更新系统图库以便让用户可以立刻查看到这张图片。更新系统图库的方法
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //把文件插入系统图库
    public static void saveImageToGallery(Context context, String fileAbsolutePath, String title) {
        // 其次把文件插入到系统图库
        try {
            //调用系统提供的插入图库的方法
            //title、description参数只是插入数据库中的字段，真实的图片名称系统会自动分配
            //MediaStore.Images.Media.insertImage(getContentResolver(), "image path", "title", "description");
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    fileAbsolutePath, title, null);
            // 最后通知图库更新
//        没有立刻显示在图库中，而我们需要立刻更新系统图库以便让用户可以立刻查看到这张图片。更新系统图库的方法
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileAbsolutePath)));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //删除文件
    public static void delFile(String fileAbsolutePath) {
        try {
            File file = new File(fileAbsolutePath);
            if (file.isFile()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //删除文件
    public static void delFile(File file) {
        try {
            if (file.isFile()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //删除文件夹和文件夹里面的文件
    public static void deleteDir(String fileAbsolutePath) {
        File dir = new File(fileAbsolutePath);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDir(file.getAbsolutePath()); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    /**
     * @param url
     * @return 从下载连接中解析出文件名
     */
    public static String getFileNameFromUrl(String url) {
        if (url != null) {
            return url.substring(url.lastIndexOf("/") + 1);
        }
        return "";
    }
}
