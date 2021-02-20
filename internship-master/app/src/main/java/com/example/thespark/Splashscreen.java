package com.example.thespark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import static java.lang.Thread.sleep;

public class Splashscreen extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        getSupportActionBar().hide();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    sleep(3000);
                    startActivity(new Intent(Splashscreen.this,MainActivity.class));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}