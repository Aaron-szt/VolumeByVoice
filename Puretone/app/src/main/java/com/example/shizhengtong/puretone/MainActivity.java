package com.example.shizhengtong.puretone;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;


public class MainActivity extends AppCompatActivity implements Runnable,View.OnClickListener{

    public static final String TAG = "MainActivity";

    AudioTrackManager audio;
    boolean isPlaySound;
    Thread thread;

    private AudioRecorder audioRecorder;
    //开始录音
    private Button startAudio;
    //结束录音
    private Button stopAudio;
    //播放录音
    private Button playAudio;
    //删除文件
    private Button deleteAudio;
    private ScrollView mScrollView;
    private TextView tv_audio_succeess;
    //pcm文件
    private File file;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
        audio = new AudioTrackManager();

        thread = new Thread(this);
        isPlaySound = true;
        thread.start();
        audioRecorder = new AudioRecorder(file);
    }

    //初始化View
    private void initView() {
        mScrollView = (ScrollView) findViewById(R.id.mScrollView);
        tv_audio_succeess = (TextView) findViewById(R.id.tv_audio_succeess);
        printLog("初始化成功");
        startAudio = (Button) findViewById(R.id.startAudio);
        startAudio.setOnClickListener(this);
        stopAudio = (Button) findViewById(R.id.stopAudio);
        stopAudio.setOnClickListener(this);
        playAudio = (Button) findViewById(R.id.playAudio);
        playAudio.setOnClickListener(this);
        deleteAudio = (Button) findViewById(R.id.deleteAudio);
        deleteAudio.setOnClickListener(this);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startAudio:
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        audio.start(400);
                        audioRecorder.StartRecord();
                        Log.e(TAG,"start");
                    }
                });
                thread.start();
                printLog("开始录音");
                ButtonEnabled(false, true, false);
                break;
            case R.id.stopAudio:
                audioRecorder.setRecording(false);
                ButtonEnabled(true, false, true);
                printLog("停止录音");
                audio.pause();
                break;
            case R.id.playAudio:
                audioRecorder.PlayRecord();
                ButtonEnabled(true, false, false);
                printLog("播放录音");
                break;
            case R.id.deleteAudio:
                deleFile();
                break;
        }
    }


    private void printLog(final String resultString) {
        tv_audio_succeess.post(new Runnable() {
            @Override
            public void run() {
                tv_audio_succeess.append(resultString + "\n");
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    //获取/失去焦点
    private void ButtonEnabled(boolean start, boolean stop, boolean play) {
        startAudio.setEnabled(start);
        stopAudio.setEnabled(stop);
        playAudio.setEnabled(play);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            isPlaySound=false;
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void run() {
        while (isPlaySound) {
            audio.play();
        }
    }


    //删除文件
    private void deleFile() {
        if(file == null){
            return;
        }
        file.delete();
        printLog("文件删除成功");
    }
}
