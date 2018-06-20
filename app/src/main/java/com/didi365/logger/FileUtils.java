package com.didi365.logger;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.text.format.Formatter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();

    /**
     * 获取SD卡的路径
     *
     * @return
     */
    public static String getSDCardPath() {
        Debug.d(TAG, "getSDCardPath is run");
        File file;
        Debug.d(TAG, "existSDCard=" + existSDCard());
        if (existSDCard()) {
            file = new File(Environment.getExternalStorageDirectory().getPath());
            return file.exists() ? file.getPath() : "/mnt/sdcard";
        } else {
            file = new File("/mnt/sdcard-ext");
            return file.exists() ? file.getAbsolutePath() : null;
        }
    }

    /**
     * 检测是否有SD卡
     *
     * @return boolean
     */
    public static boolean existSDCard() {
        Debug.d(TAG, "existSDCard is run");
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static boolean makeDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            return file.mkdirs();
        } else {
            return true;
        }
    }

    //删除文件
    public static void deleteFile(String... paths) {
        File file = null;
        for (String path : paths) {
            file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static void copyFromAssets(Context context, String sourceFilePath, String destFilePath) throws IOException {
        File file = new File(destFilePath);
        if (!file.exists()) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = context.getAssets().open(sourceFilePath);
                fos = new FileOutputStream(destFilePath);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                    }
                }
            }
        }
    }

    public static File getVideoCacheDir(Context context) {
        File cacheFolder = new File(context.getExternalCacheDir(), "video-cache");
        if (!cacheFolder.exists()) {
            cacheFolder.mkdirs();
        }
        return cacheFolder;
    }

    public static void cleanVideoCacheDir(Context context) throws IOException {
        File videoCacheDir = getVideoCacheDir(context);
        cleanDirectory(videoCacheDir);
    }

    private static void cleanDirectory(File file) throws IOException {
        if (!file.exists()) {
            return;
        }
        File[] contentFiles = file.listFiles();
        if (contentFiles != null) {
            for (File contentFile : contentFiles) {
                delete(contentFile);
            }
        }
    }

    private static void delete(File file) throws IOException {
        if (file.isFile() && file.exists()) {
            deleteOrThrow(file);
        } else {
            cleanDirectory(file);
            deleteOrThrow(file);
        }
    }

    /**
     * 递归删除文件和文件夹, 增加不需要删除的文件路径
     * 要删除的根目录
     */
    public static void deleteFolder(File folder, String noDeletePath) {
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files == null) {
                return;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteFolder(files[i], noDeletePath);
                } else {
                    if (!TextUtils.equals(noDeletePath, files[i].getAbsolutePath())) {
                        files[i].delete();
                    }
                }
            }
        }
    }

    private static void deleteOrThrow(File file) throws IOException {
        if (file.exists()) {
            boolean isDeleted = file.delete();
            if (!isDeleted) {
                throw new IOException(String.format("File %s can't be deleted", file.getAbsolutePath()));
            }
        }
    }

    /**
     * 获得SD卡总大小
     *
     * @return
     */
    private String getSDTotalSize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    public static long getSDAvailableSize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return blockSize * availableBlocks;
    }

    private String getSDAvailableSizeStr(Context context) {
        return Formatter.formatFileSize(context, getSDAvailableSize(context));
    }

    /**
     * 获得机身内存总大小
     *
     * @return
     */
    private String getRomTotalSize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }

    /**
     * 获得机身可用内存
     *
     * @return
     */
    private String getRomAvailableSize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(context, blockSize * availableBlocks);
    }

    /**
     * 创建文件
     * @param path
     * @param fileName
     * @return
     */
    public static File checkFile(String path, String fileName) {
        FileUtils.deleteFile(path + "/" + fileName);
        File file = null;
        try {
            file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
                Debug.d(TAG, "文件夹不存在,创建文件夹");
            }
            file = new File(file, "/" + fileName);
            if (file.exists()) {
                file.delete();
                Debug.d(TAG, "删除文件");
            }
            //创建文件
            file.createNewFile();
            Debug.d(TAG, "创建文件");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    //写数据到文件
    public static void writDate2File(File file, String... string) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, true));
            for (String s : string) {
                bw.write(s);
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
