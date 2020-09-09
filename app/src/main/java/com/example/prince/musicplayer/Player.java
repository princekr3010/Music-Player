package com.example.prince.musicplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class Player extends AppCompatActivity {

    static int index;
    TextView Title;
    MediaPlayer mp = new MediaPlayer();
    TextView Max_time;
    TextView Curr_time;
    Button PlayPauseB;
    ImageView Imv;
    Button NextB;
    Button PrevB;
    SeekBar Sbar;
    static int pi;

    android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Max_time = (TextView)findViewById(R.id.Max_time);
        Curr_time = (TextView)findViewById(R.id.C_time);
        Title = (TextView) findViewById(R.id.Title);
        Imv = (ImageView)findViewById(R.id.Imv);
        PlayPauseB = (Button)findViewById(R.id.PlayPauseB);
        NextB = (Button) findViewById(R.id.NextB);
        PrevB = (Button) findViewById(R.id.PrevB);
        Sbar = (SeekBar)findViewById(R.id.seekBar);
        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        Intent mIntent  = getIntent();
        index = mIntent.getIntExtra("index",0);
        if(mp.isPlaying())
        {
            mp.stop();
            mp.reset();
        }
        Play(index);


        PlayPauseB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mp.isPlaying())
                {
                    mp.pause();
                    PlayPauseB.setBackgroundResource(R.drawable.ic_play);
                }
                else
                {
                    mp.start();
                    PlayPauseB.setBackgroundResource(R.drawable.ic_pause);
                }
            }
        });

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                next(index);
            }
        });

        NextB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next(index);
            }
        });

        PrevB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                prev(index);

            }
        });

        Sbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Curr_time.setText(milliSecondToTimer(i*1000));

                 pi = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                mp.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(pi*1000);
                mp.start();
            }
        });


    }
    private void Play(int pos)
    {

        Log.d("index",""+pos);
        Title.setText(PlayList.arrayList.get(pos));

        try {
            mp.setDataSource(PlayList.songPath.get(pos));
            mp.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.start();
        Sbar.setMax(mp.getDuration()/1000);
        if(mp.isPlaying())
        {


            getSeekStatus();
            Max_time.setText(milliSecondToTimer(mp.getDuration()));


            mmr.setDataSource(PlayList.songPath.get(pos));
            byte[] data = mmr.getEmbeddedPicture();
            if(data!=null)
            {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                Imv.setImageBitmap(bitmap);
            }
            else{
                Imv.setImageResource(R.drawable.defaultlogo);
            }


        }



    }


    private void prev(int Index)
    {
        PlayPauseB.setBackgroundResource(R.drawable.ic_pause);
        mp.stop();
        mp.reset();


        if(index == 0) {
            index = PlayList.arrayList.size()-1;
            Play(index);
            Log.d("index",""+index);
        }
        else{
            index = (index-1)%PlayList.arrayList.size();
            Play(index);
            Log.d("index",""+index);
        }
    }

    private void next(int Index)
    {

        PlayPauseB.setBackgroundResource(R.drawable.ic_pause);
        mp.stop();
        mp.reset();
       // index = (index+1)%PlayList.arrayList.size();
        //Play(index);
        Log.d("index",""+index);
        if(index<PlayList.arrayList.size()-1) {
            index = index + 1;
            Play(index);
        }
        else{
            index =0;
            Play(index);
        }
    }

    private String milliSecondToTimer(long milliSeconds)
    {
        String timeString = "";
        String secondsString;

        int hours = (int)(milliSeconds/(1000*60*60));
        int minutes = (int)(milliSeconds%(1000*60*60))/(1000*60);
        int Seconds = (int)((milliSeconds%(1000*60*60))%(1000*60)/1000);

        if(hours>0)
        {
            timeString = hours+":";
        }
        if(Seconds<10)
        {
            secondsString = "0"+Seconds;
        }
        else {
            secondsString = ""+Seconds;
        }
        timeString = timeString+minutes+":"+secondsString;
        return timeString;
    }


    public void getSeekStatus()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int currentPosition = 0;
                int total = mp.getDuration();
                while(mp!=null&&currentPosition<total){
                    try {
                        Thread.sleep(1000);
                        currentPosition = mp.getCurrentPosition();

                    } catch (InterruptedException e) {
                        return;
                    }
                    Sbar.setProgress(currentPosition/1000);

                }
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
