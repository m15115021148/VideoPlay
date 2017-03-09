package com.geek.videoplay;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;

public class VideoPlayActivity extends AppCompatActivity {
    private VideoView videoView1;//视频播放控件
    private String file;//视频路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        videoView1 = (VideoView) findViewById(R.id.videoView1);
        Bundle bundle = getIntent().getExtras();
        file = bundle.getString("path");//获得拍摄的短视频保存地址
        videoView1.setVideoPath(file);
        videoView1.start();
        videoView1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView1.canPause();
    }

}
