package com.cjq.ImageDownloader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.cjq.ImageDownloader.downloader.ImageDownloader;
import com.example.ImageDownloader.R;

/**
 * Created by android on 2015/5/14.
 */
public class MainActivity extends Activity {

    private Handler mHandler = new Handler();
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ImageDownloader imageDownloader = new ImageDownloader(this);

        imageView = (ImageView) findViewById(R.id.image);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);

        imageDownloader.loadUrlPicBitmap("https://www.baidu.com/img/bd_logo1.png", (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics()), new ImageDownloader.LoadImageListener() {
                    @Override
                    public void loadFinish(Bitmap bitmap) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bitmap);
                            }
                        });
                    }
                },progressBar
        );
    }
}
