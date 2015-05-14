package com.cjq.ImageDownloader.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.cjq.ImageDownloader.entities.BitmapFile;

/**
 * Created by android on 2015/5/14.
 */
public class ImagePositionLogUtil {

    public static final String TABLE_NAME = "image_log";
    private SQLiteOpenHelper helper;

    public ImagePositionLogUtil(Context context) {
        this.helper = new ImagePositionLogHelper(context);
    }

    public long insert(String url, String image_name, int in_sample_size) {
        SQLiteDatabase database = helper.getWritableDatabase();

        long id;
        ContentValues contentValues = new ContentValues();
        contentValues.put("url", url);
        contentValues.put("image_name", image_name);
        contentValues.put("in_sample_size", in_sample_size);
        id = database.insert(TABLE_NAME, null, contentValues);

        database.close();
        return id;
    }

    public void delete(String image_name) {
        SQLiteDatabase database = helper.getWritableDatabase();
        database.delete(TABLE_NAME, " image_name = ? ", new String[]{image_name});
//        database.close();
    }

    //×¢Òâ£¬imagenameÊÇÂ·¾¶
    public BitmapFile select(String url) {
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select _id,image_name,in_sample_size from " + TABLE_NAME + " where url = ?", new String[]{url});

        BitmapFile file;
        String image_name;
        int in_sample_size;

        if (cursor.getCount() <= 0) {
            image_name = null;
            file=null;
        } else if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            image_name = cursor.getString(1);
            file=new BitmapFile(image_name);
            in_sample_size = cursor.getInt(2);
            file.setInSampleSize(in_sample_size);
        } else {
            cursor.close();
            database.close();
            throw new RuntimeException("More than one item selected");
        }

        cursor.close();
        database.close();
        return file;
    }

}
