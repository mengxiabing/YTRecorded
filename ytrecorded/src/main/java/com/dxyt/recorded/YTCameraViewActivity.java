package com.dxyt.recorded;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.dxyt.recorded.listener.ClickListener;
import com.dxyt.recorded.listener.YTCameraListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import static com.dxyt.recorded.Config.VIDEO_RESULT;


public class YTCameraViewActivity extends AppCompatActivity {
    YTCameraView ytCameraView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ytcamera_view);


        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
        initUI();


    }

    private void initUI() {
        ytCameraView = (YTCameraView) findViewById(R.id.jcameraview);
        //设置视频保存路径
        ytCameraView.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                Intent intent = new Intent();
                //把返回数据存入Intent
                intent.putExtra("result", "");
                //设置返回数据
                YTCameraViewActivity.this.setResult(Config.RECORD_VIDEO, intent);//RESULT_OK为自定义常量
                //关闭Activity
                YTCameraViewActivity.this.finish();
            }
        });

        ytCameraView.setSaveVideoPath(Environment.getExternalStorageDirectory().getPath() + File.separator);
        //JCameraView监听
        ytCameraView.setJCameraLisenter(new YTCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                //获取图片bitmap
                Log.i("JCameraView===>>>>", "bitmap = " + bitmap.getWidth());
                YTCameraViewActivity.this.finish();
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {
                Intent intent = new Intent();
                //把返回数据存入Intent
                intent.putExtra(VIDEO_RESULT, url);
                //设置返回数据

                String date = "picture_" + String.valueOf(System.currentTimeMillis());
                String path = "/sdcard/DCIM/Camera/" + date+".png";
                saveBitmap(firstFrame, path);
                intent.putExtra(Config.PICTURE_RESULT, path);
                YTCameraViewActivity.this.setResult(Config.RECORD_VIDEO, intent);//RESULT_OK为自定义常量
                YTCameraViewActivity.this.finish();

            }

            @Override
            public void quit() {
                finish();
            }

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //全屏显示
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
            ytCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ytCameraView.onPause();
    }

    public  void saveBitmap(Bitmap bmp,String path){
        File file = new File(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 80;
        bmp.compress(Bitmap.CompressFormat.PNG, options, baos);
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            options -= 10;
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
