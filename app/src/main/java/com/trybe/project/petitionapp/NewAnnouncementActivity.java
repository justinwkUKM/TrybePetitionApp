package com.trybe.project.petitionapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class NewAnnouncementActivity extends AppCompatActivity {

    public static final String TAG = NewAnnouncementActivity.class.getSimpleName();
    private Toolbar newAnnouncementToolbar;
    private android.widget.ImageView imageViewAnnouncementCover;
    private android.support.design.widget.TextInputEditText etAnnouncementTitle;
    private android.support.design.widget.TextInputEditText etAnnouncementDescription;
    private Button btAddAnnouncement;
    private Uri announcementCoverImageUri = null;
    private ProgressBar progressBar;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    String randomName;
    private Bitmap compressedImageFile;
    private String postId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_comment);
        Intent i = getIntent();
        postId = i.getStringExtra("stPetitionPostId");
        this.etAnnouncementDescription = (TextInputEditText) findViewById(R.id.etAnnouncementDescription);
        this.etAnnouncementTitle = (TextInputEditText) findViewById(R.id.etAnnouncementTitle);
        this.imageViewAnnouncementCover = (ImageView) findViewById(R.id.imageViewAnnouncementCover);
        this.newAnnouncementToolbar = (Toolbar) findViewById(R.id.newAnnouncementToolbar);
        this.progressBar = findViewById(R.id.newAnnouncementProgressBar);
        progressBar.setVisibility(View.INVISIBLE);
        this.btAddAnnouncement = findViewById(R.id.btnAddAnnouncement);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();


        setSupportActionBar(newAnnouncementToolbar);
        getSupportActionBar().setTitle("New Announcement");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageViewAnnouncementCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bringImagePicker();
            }
        });

        btAddAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String stAnnouncementTitle = etAnnouncementTitle.getText().toString();
                final String stAnnouncementDescription = etAnnouncementDescription.getText().toString();
                if (!TextUtils.isEmpty(stAnnouncementTitle)
                        && !TextUtils.isEmpty(stAnnouncementDescription)
                        && announcementCoverImageUri != null) {
                    user_id = firebaseAuth.getCurrentUser().getUid();
                    progressBar.setVisibility(View.VISIBLE);

                    randomName = UUID.randomUUID().toString();
                    // randomName = random();

                    StorageReference image_path = storageReference.child("announcement_image").child(randomName + ".jpg");
                    image_path.putFile(announcementCoverImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                            final Uri download_uri = task.getResult().getDownloadUrl();

                            if (task.isSuccessful()) {
                                File newImageFile = new File(announcementCoverImageUri.getPath());
                                try {
                                    compressedImageFile = new Compressor(NewAnnouncementActivity.this)
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
                                UploadTask uploadTask = storageReference.child("announcement_image/thumbs").child(randomName + ".jpg").putBytes(thumb_data);
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Log.e(TAG, "Success");
                                        String downloadThumbUri = taskSnapshot.getDownloadUrl().toString();
                                        saveToFirestore(postId,download_uri,downloadThumbUri, user_id, stAnnouncementTitle, stAnnouncementDescription, randomName);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //error handling
                                        Log.e(TAG, e.getMessage());
                                    }
                                });
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                String error = task.getException().getMessage();
                                Log.e(TAG, error);
                            }
                        }
                    });
                } else {
                    //gotoMain();
                }
            }
        });
    }

    private void saveToFirestore(String postId, Uri download_uri, String downloadThumbUri, String user_id, String stPetitionTitle,
                                 String stPetitionDescription, String randomName) {
        Map<String, Object> petitionAnnouncementMap = new HashMap<>();
        petitionAnnouncementMap.put("announcement_cover_image_url", download_uri.toString());
        petitionAnnouncementMap.put("announcement_cover_image_thumb_url", downloadThumbUri);
        petitionAnnouncementMap.put("announcement_title", stPetitionTitle);
        petitionAnnouncementMap.put("announcement_desc", stPetitionDescription);
        petitionAnnouncementMap.put("announcement_timestamp", FieldValue.serverTimestamp());
        petitionAnnouncementMap.put("announcement_author", user_id);
        petitionAnnouncementMap.put("announcement_petition_id", postId);

        firebaseFirestore.collection("Petitions/" + this.postId + "/Announcements").add(petitionAnnouncementMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(NewAnnouncementActivity.this, "Announcement Added", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(NewAnnouncementActivity.this, MainNavigationActivity.class));
                    finish();
                } else {
                    String error = task.getException().getMessage();
                    Log.e(TAG, error);
                    Toast.makeText(NewAnnouncementActivity.this, "FireStore error " + error, Toast.LENGTH_SHORT).show();
                }

                progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void bringImagePicker() {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(512, 512)
                .setAspectRatio(1, 1)
                .start(NewAnnouncementActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                announcementCoverImageUri = result.getUri();
                imageViewAnnouncementCover.setImageURI(announcementCoverImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Log.e(TAG, error.getMessage());

            }
        }
    }
}

