package com.trybe.project.petitionapp.views.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.trybe.project.petitionapp.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


/**
 * Created by Waqas Khalid Obeidy on 29/3/2018.
 */

public class AccountSetupActivity extends AppCompatActivity {

    public static final String TAG = "AccountSetupActivity";
    CircleImageView profileImage;
    private Uri imageURI = null;
    private Uri mainImageURI = null;
    private Uri downloadedImageUri =null;
    private EditText etName;
    private EditText etEmail;
    private Button btSave;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ProgressBar setupProgress;
    private String name;
    private String email;
    private String user_id;
    private boolean isSuccessUpload = false;
    private boolean isChanged = false;
    private Bitmap compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);
        Toolbar toolbar = findViewById(R.id.setupAccountActivityToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        profileImage = findViewById(R.id.profile_image_petitions);
        etEmail = findViewById(R.id.etEmailAS);
        etName = findViewById(R.id.etNameAS);
        btSave = findViewById(R.id.btSaveAccountSettings);
        setupProgress = findViewById(R.id.progressBarAS);
        setupProgress.setVisibility(View.VISIBLE);
        btSave.setEnabled(false);
        retrieveFromFirestore();

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                    if (ContextCompat.checkSelfPermission(AccountSetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                        //Toast.makeText(AccountSetupActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(AccountSetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }else{
                        bringImagePicker();

                    }
                }else{
                    bringImagePicker();
                }
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = etName.getText().toString();
                email = etEmail.getText().toString();
                setupProgress.setVisibility(View.VISIBLE);

                if (isChanged) {

                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && mainImageURI != null) {
                        user_id = firebaseAuth.getCurrentUser().getUid();
                        setupProgress.setVisibility(View.VISIBLE);

                        /*StorageReference image_path = storageReference.child("profile_image").child(user_id + ".jpg");
                        image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {
                                    Uri download_uri = task.getResult().getDownloadUrl();

                                    saveToFirestore(download_uri, user_id, name, email);

                                    setupProgress.setVisibility(View.INVISIBLE);
                                    if (isSuccessUpload) {
                                        gotoMain();
                                    }

                                } else {
                                    setupProgress.setVisibility(View.INVISIBLE);
                                    String error = task.getException().getMessage();
                                }
                            }
                        });*/

                        File newImageFile = new File(mainImageURI.getPath());
                        try {
                            compressedImageFile = new Compressor(AccountSetupActivity.this)
                                    .setMaxHeight(100)
                                    .setMaxWidth(100)
                                    .setQuality(5)
                                    .compressToBitmap(newImageFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] thumb_data = baos.toByteArray();

                        UploadTask uploadTask = storageReference.child("profile_image").child(user_id + ".jpg").putBytes(thumb_data);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //Log.e(TAG, "Success");
                                Uri downloadThumbUri = taskSnapshot.getDownloadUrl();
                                saveToFirestore(downloadThumbUri, user_id, name, email);
                                setupProgress.setVisibility(View.INVISIBLE);
                                if (isSuccessUpload) {
                                    gotoMain();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //error handling
                                setupProgress.setVisibility(View.INVISIBLE);
                                //Log.e(TAG, e.getMessage());
                            }
                        });


                    } else {
                        gotoMain();
                    }
                }else{
                    saveToFirestore(downloadedImageUri, user_id, name, email);
                }
            }
        });

    }

    private void gotoMain() {
        startActivity(new Intent(AccountSetupActivity.this, MainNavigationActivity.class));
        finish();
    }

    private void saveToFirestore(Uri download_uri, String user_id,String user_name,String user_email) {
        Map<String, String> userMap = new HashMap<>();
        userMap.put("user_name",user_name);
        userMap.put("user_email",user_email);
        userMap.put("user_profile_image",download_uri.toString());

        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    //Toast.makeText(AccountSetupActivity.this, "Settings Updated", Toast.LENGTH_SHORT).show();
                    isSuccessUpload = true;
                    gotoMain();
                }else{
                    isSuccessUpload = false;
                    String error = task.getException().getMessage();
                    //Log.e(TAG,error);
                    //Toast.makeText(AccountSetupActivity.this, "FireStore error "+error, Toast.LENGTH_SHORT).show();
                }

                setupProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void bringImagePicker() {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(AccountSetupActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageURI = null;
                imageURI = result.getUri();
                mainImageURI = imageURI;
                profileImage.setImageURI(imageURI);
                isChanged = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //saveData();
    }

    private void saveData() {
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser==null){
            updateUI(currentUser);
        }else{
            for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                if (user.getProviderId().equals("facebook.com")||user.getProviderId().equals("google.com")) {
                    name = firebaseAuth.getCurrentUser().getDisplayName();
                    email = firebaseAuth.getCurrentUser().getEmail();
                    imageURI = firebaseAuth.getCurrentUser().getPhotoUrl();
                    String id = firebaseAuth.getCurrentUser().getUid();
                    saveToFirestore(imageURI, id, name, email);
                    imageURI=null;
                    //use glide to download and set the image
                    //profileImage.setImageURI(imageURI);
                    Log.w("AccountSetupActivity", "//Log.ed in through Social Media");
                }else{
                    Log.w("AccountSetupActivity", "Not //Log.ed in through Social Media");
                }
            }

        }

    }

    private void retrieveFromFirestore() {
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        //Toast.makeText(AccountSetupActivity.this, "Data Exist", Toast.LENGTH_SHORT).show();
                        name = task.getResult().getString("user_name");
                        email = task.getResult().getString("user_email");
                        String image_url = task.getResult().getString("user_profile_image");
                        downloadedImageUri = Uri.parse(image_url);
                        etName.setText(name);
                        etEmail.setText(email);

                        //mainImageURI= Uri.parse(image_url);

                        RequestOptions placeHolderRequest = new RequestOptions();
                        placeHolderRequest.placeholder(R.drawable.ic_email_black_24dp);
                        Glide.with(AccountSetupActivity.this).setDefaultRequestOptions(placeHolderRequest).load(image_url).into(profileImage);
                    }
                    else{
                        //Toast.makeText(AccountSetupActivity.this, "Data doesn't Exist", Toast.LENGTH_SHORT).show();
                        saveData();
                        retrieveFromFirestore();
                    }

                }else{

                    String error = task.getException().getMessage();
                    //Log.e(TAG,error);
                    //Toast.makeText(AccountSetupActivity.this, "firebaseFirestore error "+error, Toast.LENGTH_SHORT).show();
                }
                setupProgress.setVisibility(View.INVISIBLE);
                btSave.setEnabled(true);
            }
        });
    }

    private void updateUI(FirebaseUser currentUser) {
        startActivity(new Intent(AccountSetupActivity.this, LoginActivity.class));
        Toast.makeText(this, "You are logged out", Toast.LENGTH_SHORT).show();
        finish();
    }

}
