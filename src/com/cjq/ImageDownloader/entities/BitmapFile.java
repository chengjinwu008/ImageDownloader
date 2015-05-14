package com.cjq.ImageDownloader.entities;

import java.io.File;
import java.net.URI;

/**
 * Created by android on 2015/5/14.
 */
public class BitmapFile extends File {

    private int inSampleSize;

    public BitmapFile(String path) {
        super(path);
    }

    public BitmapFile(String dirPath, String name) {
        super(dirPath, name);
    }

    public BitmapFile(URI uri) {
        super(uri);
    }

    public BitmapFile(File dir, String name) {
        super(dir, name);
    }


    public int getInSampleSize() {
        return inSampleSize;
    }

    public void setInSampleSize(int inSampleSize) {
        this.inSampleSize = inSampleSize;
    }
}
