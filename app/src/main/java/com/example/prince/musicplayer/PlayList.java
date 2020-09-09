package com.example.prince.musicplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class PlayList extends AppCompatActivity {

    static int p;
    private static final int MY_PERMISSION_REQUEST = 1;
    public static ArrayList<String> arrayList;
    public static ArrayList<String> songPath = new ArrayList<>();
    public  ArrayList<String> songSinger = new ArrayList<>();
    ListView listView;
    ArrayAdapter<String> adapter;

    static int resId;
    String Path;
    ImageView PlayB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        if(ContextCompat.checkSelfPermission(PlayList.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(PlayList.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(PlayList.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST);
            }
            else
            {
                ActivityCompat.requestPermissions(PlayList.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST);
            }
        } else {
            doStuff();
        }

        if((getIntent().getFlags()& getIntent().FLAG_ACTIVITY_BROUGHT_TO_FRONT)!=0)
        {
            finish();
            return;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.PlayerActivity)
        {
            Intent myIntent = new Intent(PlayList.this, Player.class);
            startActivity(myIntent);

        }
        return super.onOptionsItemSelected(item);
    }

    public  void doStuff(){
        listView = (ListView) findViewById(R.id.Playlist);
        arrayList = new ArrayList<String>();
        getMusic();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                p = i;
               //Toast.makeText(getApplicationContext(),p,Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(PlayList.this, Player.class);
                myIntent.putExtra("index", p);
                startActivity(myIntent);
                /*String songName = arrayList.get(i).toString();
                startActivity(new Intent(getApplicationContext(),Player.class)
                .putExtra("songs",songPath).putExtra("songname",songName)
                .putExtra("pos",i));*/
            }
        });

    }
    public Void getMusic() {
        ContentResolver contentResolver =getContentResolver();
        Uri SongUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(SongUri, null, null, null,null);
        if(songCursor != null &&songCursor.moveToFirst()){
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            do{
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                Path = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                arrayList.add(currentTitle );
                songSinger.add(currentArtist);
                songPath.add(Path);
            }while (songCursor.moveToNext());
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_REQUEST: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(PlayList.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission granted" , Toast.LENGTH_SHORT).show();
                        doStuff();
                    }
                } else{
                    Toast.makeText(this, "No Persmission granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;

            }
        }
    }




}
