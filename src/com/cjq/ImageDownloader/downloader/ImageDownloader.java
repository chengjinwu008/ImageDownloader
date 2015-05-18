package com.cjq.ImageDownloader.downloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import com.cjq.CircleProgress.view.CircleProgress;
import com.cjq.DownLoader.utils.Downloader;
import com.cjq.DownLoader.utils.ProgressLogUtil;
import com.cjq.ImageDownloader.DAO.ImagePositionLogUtil;
import com.cjq.ImageDownloader.entities.BitmapFile;

import java.io.File;
import java.io.IOException;

/**
 * Created by android on 2015/5/14.
 */
public class ImageDownloader {

    private static final long TIME_FOR_CACHE = 3600 * 24 * 1000;//默认缓存时间
    private ProgressLogUtil progressLogUtil;
    private static final int THREAD_SIZE = 3;
    private BitmapFile file;
    private ImagePositionLogUtil logUtil;
    public Handler mHandler= new Handler(Looper.getMainLooper());
    private static final String folder = "MyCache";
    public static final String cacheDir = Environment.getExternalStorageDirectory() + File.separator + folder;

    public ImageDownloader(Context context) {
        progressLogUtil = new ProgressLogUtil(context);
        logUtil = new ImagePositionLogUtil(context);
    }

    private interface ImageDownloaderListener {
        void loadFinish();
    }

    public interface LoadImageListener {
        void loadFinish(Bitmap bitmap);
    }

    public void loadUrlPicBitmap(String imageURL, int width, int height, LoadImageListener listener, CircleProgress progressBar) {
        loadImageCache(progressBar, imageURL, cacheDir, width, height, new ImageDownloaderListener() {
            @Override
            public void loadFinish() {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = file.getInSampleSize();
                Bitmap res = BitmapFactory.decodeFile(file.getPath(), options);
                if (listener != null)
                    listener.loadFinish(res);
            }
        });
    }

    public void clearCache(){
        File cacheFile = new File(cacheDir);
        File[] files = cacheFile.listFiles();
        for(File f:files){
            String path = f.getPath();
            logUtil.delete(path);
            f.delete();
        }
    }

    private void loadImageCache(CircleProgress progressBar, String imageURL, String cacheDir, int width, int height, ImageDownloaderListener listener) {

        file = logUtil.select(imageURL);
        if (file == null || !file.exists() || System.currentTimeMillis()-file.lastModified()<TIME_FOR_CACHE) {
            if(file!=null){
                logUtil.delete(file.getPath());
                if(file.exists()){
                    file.delete();//如果缓存时间超过了，则删除缓存
                }
            }
            Downloader downloader = new Downloader(imageURL, cacheDir, progressLogUtil, THREAD_SIZE, new Downloader.DownloaderListener() {
                @Override
                public void onPrepareFinished(Downloader downloader) {
                    try {
                        progressBar.setMax(downloader.getFileLength());
                        file = new BitmapFile(downloader.getFile().getPath());
                        downloader.download();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onDownloading(int downloaded_size) {
                    //下载中，可以显示进度条
                    if (progressBar != null)
                        progressBar.setProgress(downloaded_size);
                }

                @Override
                public void onDownloadFinished() {
                    //下载完毕后，首先要在数据库记录
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(file.getPath(), options);
                    int in_sample_size = getInSampleSize(options, width, height);
                    logUtil.insert(imageURL, file.getPath(), in_sample_size);
                    if (progressBar != null)
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    file.setInSampleSize(in_sample_size);
                    if (listener != null)
                        listener.loadFinish();
                }
            });
            downloader.downloadPrepare();
        } else {
            if (progressBar != null)
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            if (listener != null)
                listener.loadFinish();
        }
    }

    private int getInSampleSize(BitmapFactory.Options options, int width, int height) {
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;

        int inSampleSize = 1;

        if (outWidth / inSampleSize > width || outHeight / inSampleSize > height) {
            inSampleSize *= 2;
        }

        return inSampleSize;
    }

}
