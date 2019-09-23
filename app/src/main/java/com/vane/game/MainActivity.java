package com.vane.game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.vane.game.pintu.PuzzleActivity;
import com.vane.game.tzfe.EZFEActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onELSBCilck(View view) {

        startActivity(new Intent(this, EZFEActivity.class));
    }

    public void onPuzzleCilck(View view) {

        startActivity(new Intent(this, PuzzleActivity.class));
    }
}
