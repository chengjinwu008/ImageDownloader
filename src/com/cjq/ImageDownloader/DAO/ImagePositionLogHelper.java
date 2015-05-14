package com.cjq.ImageDownloader.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by android on 2015/5/14.
 */
public class ImagePositionLogHelper extends SQLiteOpenHelper {
    private static final String DBNAME="image.db";
    private static final int VERSION=1;

    public ImagePositionLogHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ImagePositionLogUtil.TABLE_NAME+"(_id integer primary key autoincrement , url varchar(200) , image_name varchar(200), in_sample_size integer)");
        //新建2个索引
        db.execSQL("create index index_url on "+ImagePositionLogUtil.TABLE_NAME+"(url)");
        db.execSQL("create index index_image_name on "+ImagePositionLogUtil.TABLE_NAME+"(image_name)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop image_log if exists");
        onCreate(db);
    }
}
