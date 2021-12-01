package com.serviceprovider.smartserviceprovider.Services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.serviceprovider.smartserviceprovider.Activities.Constants;
import com.serviceprovider.smartserviceprovider.R;

import java.util.HashMap;

public class AddServices extends AppCompatActivity {



    private ImageButton backBtn;
    private SwitchCompat discountSwitch;
    private ImageView productIconIV;
    private TextView categoryTV;
    private EditText nameEt,descriptionEt,quantityEt,priceEt,discountNotePriceEt,discountedPriceEt;
    private Button addProductsBtn;
    private Uri image_uri;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    //permission constant
    private static final int CAMERA_REQUEST_CODE=200;
    private static final int STORAGE_REQUEST_CODE=300;
    //image pick constant
    private static final int IMAGE_PICK_GALLERY_CODE=400;
    private static final int IMAGE_PICK_CAMERA_CODE=500;
    //permission Array
    private String [] cameraPermissions;
    private String [] storagePermissions;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_services);


        backBtn=findViewById(R.id.backBtn);
        categoryTV=findViewById(R.id.categoryTV);
        productIconIV=findViewById(R.id.productIconIV);
        nameEt=findViewById(R.id.nameEt);
        descriptionEt=findViewById(R.id.descriptionEt);
        quantityEt=findViewById(R.id.quantityEt);
        priceEt=findViewById(R.id.priceEt);
        discountedPriceEt=findViewById(R.id.discountedPriceEt);
        discountNotePriceEt=findViewById(R.id.discountNotePriceEt);
        discountSwitch=findViewById(R.id.discountSwitch);
        addProductsBtn=findViewById(R.id.addProductsBtn);


        //unchecked, hide discountPriceEt,discountNoteEt
        discountedPriceEt.setVisibility(View.GONE);
        discountNotePriceEt.setVisibility(View.GONE);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        cameraPermissions=new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        // if discount switch is checked show discountPriceEt,discountNoteEt | if discount is not checked:hide discountPriceEt,discountNoteEt
        discountSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    //checked, hide discountPriceEt,discountNoteEt
                    discountedPriceEt.setVisibility(View.VISIBLE);
                    discountNotePriceEt.setVisibility(View.VISIBLE);
                }
                else {
                    //unchecked, hide discountPriceEt,discountNoteEt
                    discountedPriceEt.setVisibility(View.GONE);
                    discountNotePriceEt.setVisibility(View.GONE);
                }
            }
        });

        productIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog();
            }
        });
        categoryTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryDialog();
            }
        });
        addProductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //flow
                // 1 input Data
                // 2 add data db
                // 3 validate data
                inputDate();
            }
        });
    }

    private String sellerName,productDescription,productCategory,productQuantity,originalPrice,discountPrice,discountNote;
    private boolean discountAvailable=false;
    private void inputDate() {
        //input Data
        sellerName=nameEt.getText().toString().trim();
        productDescription=descriptionEt.getText().toString().trim();
        productCategory=categoryTV.getText().toString().trim();
        productQuantity=quantityEt.getText().toString().trim();
        originalPrice=priceEt.getText().toString().trim();
        discountAvailable=discountSwitch.isChecked();

        // validate Data
        if (TextUtils.isEmpty(sellerName)){
            Toast.makeText(this, "Name is Required", Toast.LENGTH_SHORT).show();
            return;//do not proceed further
        }
        if (TextUtils.isEmpty(productCategory)){
            Toast.makeText(this, "Category is Required", Toast.LENGTH_SHORT).show();
            return;//do not proceed further
        }
        if (TextUtils.isEmpty(originalPrice)){
            Toast.makeText(this, "Price is Required", Toast.LENGTH_SHORT).show();
            return;//do not proceed further
        }

        if (discountAvailable){
            // service with Discount
            discountPrice=discountedPriceEt.getText().toString().trim();
            discountNote=discountNotePriceEt.getText().toString().trim();
            if (TextUtils.isEmpty(discountPrice)){
                Toast.makeText(this, "Discount Price is Required", Toast.LENGTH_SHORT).show();
                return;//do not proceed further
            }
        }
        else {
            // service with discount
            discountNote="";
            discountPrice="0";

        }
        addProduct();
    }

    private void addProduct() {
        //  add data db
        progressDialog.setMessage("Add Service...");
        progressDialog.show();

        String timeTamp=""+System.currentTimeMillis();
        if (image_uri==null){
            //upload without Image

            //setup data to upload
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("productId",""+timeTamp);
            hashMap.put("sellerName",""+sellerName);
            hashMap.put("serviceDescription",""+productDescription);
            hashMap.put("serviceCategory",""+productCategory);
            hashMap.put("serviceQuantity",""+productQuantity);
            hashMap.put("serviceIcon","");//no image , set Image
            hashMap.put("originalPrice",""+originalPrice);
            hashMap.put("discountPrice",""+discountPrice);
            hashMap.put("discountNote",""+discountNote);
            hashMap.put("discountAvailable",""+discountAvailable);
            hashMap.put("timeTamp",""+timeTamp);
            hashMap.put("uid",""+firebaseAuth.getUid());
            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Service").child(timeTamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(AddServices.this, "Your is Service Added", Toast.LENGTH_SHORT).show();
                            clearData();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddServices.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
        else {
            //upload with Image

            // first upload image to storage
            // name and path  of image to be uploaded
            String filePathAndName="profile_images/"+""+ firebaseAuth.getUid();
            StorageReference storageReference= FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // image upload
                            //get  url of uploaded image
                            Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());

                            Uri downloadImageUri=uriTask.getResult();
                            if (uriTask.isSuccessful()){
                                //image url received now update db
                                //setup  data to update
                                HashMap<String,Object> hashMap=new HashMap<>();
                                hashMap.put("productId",""+timeTamp);
                                hashMap.put("serviceTitle",""+sellerName);
                                hashMap.put("serviceDescription",""+productDescription);
                                hashMap.put("serviceCategory",""+productCategory);
                                hashMap.put("serviceQuantity",""+productQuantity);
                                hashMap.put("serviceIcon",""+downloadImageUri);
                                hashMap.put("originalPrice",""+originalPrice);
                                hashMap.put("discountPrice",""+discountPrice);
                                hashMap.put("discountNote",""+discountNote);
                                hashMap.put("discountAvailable",""+discountAvailable);
                                hashMap.put("timeTamp",""+timeTamp);
                                hashMap.put("uid",""+firebaseAuth.getUid());
                                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).child("Service").child(timeTamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(AddServices.this, "Your is Service Added", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull  Exception e) {
                                                Toast.makeText(AddServices.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull  Exception e) {
                            //failed uploading image
                            progressDialog.dismiss();
                            Toast.makeText(AddServices.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void clearData() {
        //clear  data after uploading service
        nameEt.setText("");
        categoryTV.setText("");
        descriptionEt.setText("");
        quantityEt.setText("");
        priceEt.setText("");
        discountedPriceEt.setText("");
        discountNotePriceEt.setText("");
        productIconIV.setImageResource(R.drawable.ic_baseline_add_shopping_white);
        image_uri=null;
    }

    private void categoryDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Product Category").setItems(Constants.productCategories, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String category=Constants.productCategories[which];
                categoryTV.setText(category);
            }
        })
                .show();
    }

    private void showImagePickDialog() {
        // option to display in dialog
        String[] options={"Camera","Gallery"};
        // Dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Pick Image:")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        //handle item click
                        if (which==0){
                            //camera clicked
                            if (checkCameraPermission()){
                                //Camera Permission Allowed
                                pickFromCamera();
                            }
                            else {
                                //not allowed request
                                requestCameraPermission();
                            }
                        }
                        else {
                            //gallery clicked
                            if (checkStoragePermission()){
                                //Storage    Permission Allowed
                                pickFromGallery();
                            }
                            else {
                                //not allowed request
                                requestStoragePermission();

                            }
                        }
                    }
                })
                .show();
    }
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE);
    }
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions,CAMERA_REQUEST_CODE);
    }
    private boolean checkStoragePermission() {
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission
                .WRITE_EXTERNAL_STORAGE )==(PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private boolean checkCameraPermission() {
        boolean result=ContextCompat.checkSelfPermission(this,Manifest.permission
                .CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result1=ContextCompat.checkSelfPermission(this,Manifest.permission
                .WRITE_EXTERNAL_STORAGE )==(PackageManager.PERMISSION_GRANTED);

        return result && result1;

    }
    private void pickFromGallery() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }
    private void pickFromCamera() {
        ContentValues contentValues= new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp_Image Description");

        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){
                        //allowed permissions
                        pickFromCamera();
                    }
                    else {
                        //denied permissions
                        Toast.makeText(this, "Camera Permissions are necessary", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){

                    boolean storageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        //allowed permissions
                        pickFromGallery();

                    }
                    else {
                        //denied permissions
                        Toast.makeText(this, "Storage Permission is necessary", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==RESULT_OK){
            if (requestCode==IMAGE_PICK_GALLERY_CODE){
                // get picked image
                image_uri=data.getData();
                // set Imageview
                productIconIV.setImageURI(image_uri);
            }
            else if(requestCode==IMAGE_PICK_GALLERY_CODE){
                // set Imageview
                productIconIV.setImageURI(image_uri);
            }
            else if (requestCode==IMAGE_PICK_CAMERA_CODE){
                productIconIV.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}