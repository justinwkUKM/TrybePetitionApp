package com.trybe.project.petitionapp.views.activities;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
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
import com.trybe.project.petitionapp.R;
import com.trybe.project.petitionapp.others.DatePickerFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class NewPetitionActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    public static final String TAG = NewPetitionActivity.class.getSimpleName();
    private Toolbar newPetitionToolbar;
    private android.widget.ImageView imageViewPetitionCover;
    private android.support.design.widget.TextInputEditText etPetitionTitle;
    private android.support.design.widget.TextInputEditText etPetitionDescription;
    private android.support.design.widget.TextInputEditText etPetitionTargetSupporters;
    private android.support.design.widget.TextInputEditText etPetitionStartDate;
    private android.support.design.widget.TextInputEditText etPetitionStopDate;
    private Button btAddPetition;
    private Uri petitionCoverImageUri = null;
    private ProgressBar progressBar;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    String randomName;
    private Bitmap compressedImageFile;
    private boolean etStartClicked, etStopClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_petition);
        this.etPetitionStopDate = (TextInputEditText) findViewById(R.id.etPetitionStopDate);
        this.etPetitionStartDate = (TextInputEditText) findViewById(R.id.etPetitionStartDate);
        this.etPetitionTargetSupporters = (TextInputEditText) findViewById(R.id.etPetitionTargetSupporters);
        this.etPetitionDescription = (TextInputEditText) findViewById(R.id.etPetitionDescription);
        this.etPetitionTitle = (TextInputEditText) findViewById(R.id.etPetitionTitle);
        this.imageViewPetitionCover = (ImageView) findViewById(R.id.imageViewPetitionCover);
        this.newPetitionToolbar = (Toolbar) findViewById(R.id.newPetitionToolbar);
        this.progressBar = findViewById(R.id.newPetitionProgressBar);
        progressBar.setVisibility(View.INVISIBLE);
        this.btAddPetition = findViewById(R.id.btnAddPetition);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();


        setSupportActionBar(newPetitionToolbar);
        getSupportActionBar().setTitle("New Petition");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageViewPetitionCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bringImagePicker();
            }
        });

        btAddPetition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String stPetitionTitle = etPetitionTitle.getText().toString();
                final String stPetitionDescription = etPetitionDescription.getText().toString();
                final String stPetitionTargetSupporters = etPetitionTargetSupporters.getText().toString();
                final String stPetitionStartDate = etPetitionStartDate.getText().toString();
                final String stPetitionStopDate = etPetitionStopDate.getText().toString();
                if (!TextUtils.isEmpty(stPetitionTitle)
                        && !TextUtils.isEmpty(stPetitionDescription)
                        && !TextUtils.isEmpty(stPetitionTargetSupporters)
                        && !TextUtils.isEmpty(stPetitionStartDate)
                        && !TextUtils.isEmpty(stPetitionStopDate)
                        && petitionCoverImageUri != null) {
                    user_id = firebaseAuth.getCurrentUser().getUid();
                    progressBar.setVisibility(View.VISIBLE);

                    randomName = UUID.randomUUID().toString();
                    // randomName = random();

                    StorageReference image_path = storageReference.child("petition_image").child(randomName + ".jpg");
                    image_path.putFile(petitionCoverImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                            final Uri download_uri = task.getResult().getDownloadUrl();

                            if (task.isSuccessful()) {
                                File newImageFile = new File(petitionCoverImageUri.getPath());
                                try {
                                    compressedImageFile = new Compressor(NewPetitionActivity.this)
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

                                UploadTask uploadTask = storageReference.child("petition_image/thumbs").child(randomName + ".jpg").putBytes(thumb_data);
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Log.e(TAG, "Success");
                                        String downloadThumbUri = taskSnapshot.getDownloadUrl().toString();
                                        saveToFirestore(download_uri, downloadThumbUri, user_id, stPetitionTitle, stPetitionDescription, stPetitionTargetSupporters, stPetitionStartDate, stPetitionStopDate, randomName);
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
                            }
                        }
                    });
                } else {
                    //gotoMain();
                }
            }
        });

        etPetitionStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment fragment = new DatePickerFragment();
                fragment.show(getSupportFragmentManager(), "Starting Date");
                etStartClicked = true;
            }
        });

        etPetitionStopDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment fragment = new DatePickerFragment();
                fragment.show(getSupportFragmentManager(), "Ending Date");
                etStopClicked = true;
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar cal = new GregorianCalendar(year, month, day);
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        if (etStartClicked) {
            etPetitionStartDate.setText(dateFormat.format(cal.getTime()));
            etStartClicked=false;
        } else if (etStopClicked) {
            etPetitionStopDate.setText(dateFormat.format(cal.getTime()));
            etStopClicked=false;
        }
    }


    private void saveToFirestore(Uri download_uri, String downloadThumbUri, String user_id, String stPetitionTitle,
                                 String stPetitionDescription, String stPetitionTargetSupporters,
                                 String stPetitionStartDate, String stPetitionStopDate,
                                 String randomName) {
        Map<String, Object> petitionMap = new HashMap<>();
        petitionMap.put("petition_cover_image_url", download_uri.toString());
        petitionMap.put("petition_cover_image_thumb_url", downloadThumbUri);
        petitionMap.put("petition_title", stPetitionTitle);
        petitionMap.put("petition_desc", stPetitionDescription);
        petitionMap.put("petition_target_supporters", stPetitionTargetSupporters);
        petitionMap.put("petition_start_date", stPetitionStartDate);
        petitionMap.put("petition_stop_date", stPetitionStopDate);
        petitionMap.put("petition_timestamp", FieldValue.serverTimestamp());
        petitionMap.put("petition_author", user_id);

        firebaseFirestore.collection("Petitions").add(petitionMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(NewPetitionActivity.this, "Petition Added", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(NewPetitionActivity.this, MainNavigationActivity.class));
                    finish();
                } else {
                    String error = task.getException().getMessage();
                    Log.e(TAG, error);
                    Toast.makeText(NewPetitionActivity.this, "FireStore error " + error, Toast.LENGTH_SHORT).show();
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
                .start(NewPetitionActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                petitionCoverImageUri = result.getUri();
                imageViewPetitionCover.setImageURI(petitionCoverImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Log.e(TAG, error.getMessage());

            }
        }
    }


}
