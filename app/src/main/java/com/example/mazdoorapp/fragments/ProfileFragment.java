package com.example.mazdoorapp.fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.mazdoorapp.SessionManager;
import com.example.mazdoorapp.UserInfoModel;
import com.example.mazdoorapp.R;
import com.example.mazdoorapp.databinding.FragmentProfileBinding;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileFragment extends Fragment {
    private static final int REQUEST_CODE = 1;

    private Bitmap bitmap;
    String downloadUrl;
    String userId;
    UserInfoModel model;
    String selectedUserType = " ";
    String serviceType = " ";

    String latitude, longitude = "";
    String userName, phoneNumber, city, description;
    private ArrayAdapter aa;
    private ArrayAdapter userTypeAdapter;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ProgressDialog dialog;
    SessionManager sessionManager;
    String userTypeD;

    Uri uri;
    FragmentProfileBinding binding;
    String[] services = {"Carpenter", "Electrician", "Handiman", "Painter", "Sweeper", " Vehicle Mechanic", "Launderer", "Ac Technician", "Mason", "Plumber", "Interior Designer"};
    String[] userType = {"User", "Provider"};
    private com.google.android.gms.location.LocationRequest locationRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);


        sessionManager = new SessionManager(getContext());

        databaseReference = FirebaseDatabase.getInstance().getReference("userInfo");
        storageReference = FirebaseStorage.getInstance().getReference("userInfo");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Toast.makeText(getContext(), "" + userId, Toast.LENGTH_SHORT).show();

        if (sessionManager.fetchService() != null) {
            databaseReference.child(sessionManager.fetchService()).child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.child("verified").exists()) {
                        boolean decision = (snapshot.child("verified").getValue(Boolean.class));
                        if (decision) {

                            binding.editprofileLayout.getRoot().setVisibility(View.GONE);
                            binding.profileLayout.getRoot().setVisibility(View.VISIBLE);
                            binding.approvallayout.getRoot().setVisibility(View.GONE);
                        } else {

                            binding.editprofileLayout.getRoot().setVisibility(View.GONE);
                            binding.profileLayout.getRoot().setVisibility(View.GONE);
                            binding.approvallayout.getRoot().setVisibility(View.VISIBLE);
                            binding.approvallayout.btnContactAdmin.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String contact = "+92 323 8858040"; // use country code with your phone number
                                    String url = "https://api.whatsapp.com/send?phone=" + contact;
                                    try {
                                        PackageManager pm = getActivity().getPackageManager();
                                        pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(url));
                                        startActivity(i);
                                    } catch (PackageManager.NameNotFoundException e) {
                                        Toast.makeText(getActivity(), "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            databaseReference.child(sessionManager.fetchService()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(userId).exists()) {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            UserInfoModel data = snap.getValue(UserInfoModel.class);
                            binding.profileLayout.btneditProfile.setVisibility(View.GONE);
                            binding.profileLayout.txtuserteypeProfile.setText(data.getUserType());
                            binding.profileLayout.txtDescripionProfile.setText(data.getDescription());
                            binding.profileLayout.txtLocationProfile.setText(data.getCity());
                            binding.profileLayout.txtphoneNumberProfile.setText(data.getPhonenumber());
                            binding.profileLayout.txtusernameProfile.setText(data.getName());
                            userTypeD = data.getUserType();


                            Picasso.get().load(data.getImage()).placeholder(R.drawable.placeholder).into(binding.profileLayout.profileImage);
                        }


                    } else {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(getContext(), "Session Manager Value is null", Toast.LENGTH_SHORT).show();
        }

        binding.profileLayout.btneditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditLayout();


            }
        });


        return binding.getRoot();
    }

    private void openEditLayout() {


        aa = new ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, services);
        dialog = new ProgressDialog(requireContext());
        dialog.setMessage("Please wait....");
        dialog.setCancelable(false);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeAdapter = new ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, userType);
        userTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        binding.editprofileLayout.btnSpinnerUser.setAdapter(userTypeAdapter);
        binding.editprofileLayout.btnSpinner.setAdapter(aa);


        binding.editprofileLayout.btnSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                serviceType = services[i];

                sessionManager.saveService(services[i]);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });
        binding.profileLayout.getRoot().setVisibility(View.GONE);
        binding.editprofileLayout.getRoot().setVisibility(View.VISIBLE);
//        binding.editprofileLayout.profileImageEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openGallery();
//
//            }
//        });
        locationRequest = com.google.android.gms.location.LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        binding.editprofileLayout.btnAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();


            }
        });

        binding.editprofileLayout.btnAddForApproval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (binding.editprofileLayout.etNameProfile.getText().toString().isEmpty()) {
                    binding.editprofileLayout.etNameProfile.setError("Enter your name please");
                    binding.editprofileLayout.etNameProfile.requestFocus();
                } else if (binding.editprofileLayout.etPhoneProfile.getText().toString().isEmpty()) {
                    binding.editprofileLayout.etPhoneProfile.setError("Enter your phone number");
                    binding.editprofileLayout.etPhoneProfile.requestFocus();

                } else if (binding.editprofileLayout.etCityProfile.getText().toString().isEmpty()) {
                    binding.editprofileLayout.etCityProfile.setError("Enter your city");
                    binding.editprofileLayout.etCityProfile.requestFocus();
                } else if (binding.editprofileLayout.etDescription.getText().toString().isEmpty()) {
                    binding.editprofileLayout.etDescription.setError("Enter about yourself");
                    binding.editprofileLayout.etDescription.requestFocus();
                } else if (longitude.isEmpty()) {
                    Toast.makeText(requireContext(), "Kindly press the Add Location Button for your location", Toast.LENGTH_SHORT).show();

                } else {

                    userName = binding.editprofileLayout.etNameProfile.getText().toString();
                    phoneNumber = binding.editprofileLayout.etPhoneProfile.getText().toString();
                    city = binding.editprofileLayout.etCityProfile.getText().toString();
                    description = binding.editprofileLayout.etDescription.getText().toString();

       uploadData();


                }
            }
        });


    }

    private void UploadImage() {
        dialog.show();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
//        byte[] finalImage = baos.toByteArray();
        final StorageReference filePath;
        filePath = storageReference.child(uri.getLastPathSegment() + "jpg");
 filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
     @Override
     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
         filePath.getDownloadUrl().addOnSuccessListener(uri1 -> {
             downloadUrl=String.valueOf(uri1);
             uploadData();
         });
     }
 });

    }


    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select any Image"), REQUEST_CODE);
    }

    private void uploadData() {


        final DatabaseReference filePath;
        filePath = databaseReference.child(serviceType);

        model = new UserInfoModel(downloadUrl, userName, phoneNumber, city, description, latitude, longitude, "Provider", serviceType, userId, false);

        filePath.child(userId).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                dialog.dismiss();
                Toast.makeText(requireContext(), "Data Added", Toast.LENGTH_SHORT).show();
                binding.approvallayout.getRoot().setVisibility(View.VISIBLE);
                binding.editprofileLayout.getRoot().setVisibility(View.GONE);
                binding.profileLayout.getRoot().setVisibility(View.GONE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void getCurrentLocation() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(requireContext())
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(requireContext())
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() > 0) {
                                        binding.editprofileLayout.txtlocation.setVisibility(View.VISIBLE);
                                        int index = locationResult.getLocations().size() - 1;
                                        latitude = String.valueOf(locationResult.getLocations().get(index).getLatitude());
                                        longitude = String.valueOf(locationResult.getLocations().get(index).getLongitude());


//                                        AddressText.setText("Latitude: "+ latitude + "\n" + "Longitude: "+ longitude);

                                        binding.editprofileLayout.txtlocation.setText("Latitude: " + latitude + "\n" + "Longitude: " + longitude);
                                        Toast.makeText(requireContext(), "Latitude: " + latitude + "\n" + "Longitude: " + longitude, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, Looper.getMainLooper());

                } else {
                    turnOnGPS();
                }

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void turnOnGPS() {


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(requireContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(requireContext(), "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(getActivity(), 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    getCurrentLocation();

                } else {

                    turnOnGPS();
                }
            }
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE  && data != null) {
             uri = data.getData();


//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            //binding.editprofileLayout.profileImageEdit.setImageURI(uri);
          //  binding.editprofileLayout.profileImageEdit.setImageBitmap(bitmap);
        }
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {

                getCurrentLocation();
            }
        }
    }


}