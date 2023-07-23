package com.example.mazdoorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.mazdoorapp.databinding.ActivityShopKeeperBinding;

public class ShopKeeperActivity extends AppCompatActivity {
    private ActivityShopKeeperBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityShopKeeperBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}