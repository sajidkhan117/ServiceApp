package com.example.mazdoorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.mazdoorapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
Handler handler;
ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               binding.btngetStarted.setVisibility(View.VISIBLE);
               binding.btngetStarted.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       startActivity(new Intent(MainActivity.this,LoginActivity.class));
                   }
               });
            }
        },5000);
    }
}