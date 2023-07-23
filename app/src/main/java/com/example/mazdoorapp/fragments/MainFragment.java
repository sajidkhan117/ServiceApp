package com.example.mazdoorapp.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mazdoorapp.R;
import com.example.mazdoorapp.databinding.FragmentMainBinding;


public class MainFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentMainBinding binding=FragmentMainBinding.inflate(inflater,container,false);

        binding.btntalktoUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  +923184382456
                String contact = "+923184382456"; // use country code with your phone number

                String url = "https://api.whatsapp.com/send?phone=" + contact;
                try {
                    PackageManager pm = requireContext().getPackageManager();
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                   requireContext().startActivity(i);
                } catch (PackageManager.NameNotFoundException e) {
                   requireContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
//                String url = "https://api.whatsapp.com/send?phone=" + contact;
//                try {
//                    PackageManager pm = requireContext().getPackageManager();
//                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setData(Uri.parse(url));
//                    startActivity(i);
//                } catch (PackageManager.NameNotFoundException e) {
//                    Toast.makeText(getContext(), "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
//                }
            }
        });




        return binding.getRoot();
    }
}