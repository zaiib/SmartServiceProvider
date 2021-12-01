package com.serviceprovider.smartserviceprovider.HomePage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.serviceprovider.smartserviceprovider.Activities.Constants;
import com.serviceprovider.smartserviceprovider.Adapters.AdapterServiceSeller;
import com.serviceprovider.smartserviceprovider.Models.ModelService;
import com.serviceprovider.smartserviceprovider.R;
import com.serviceprovider.smartserviceprovider.Services.AddServices;
import com.serviceprovider.smartserviceprovider.UserProfile.LoginActivity;
import com.serviceprovider.smartserviceprovider.UserProfile.ProfileEditSellerActivity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class MainSellerActivity extends AppCompatActivity {

    private MeowBottomNavigation bottom_navigation;
    private TextView nameTv,shopNameTv,addressTv,cityTv,tabProductTV,tabOrderTV,filteredServiceTv;
    private ImageButton logoutBtn,editProfileBtn,addProductBtn,filterServiceBtn;
    private ImageView profileIV;
    private RelativeLayout productsRl,ordersRl;
    private EditText searchSearchEt;
    private RecyclerView servicesRv;



    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelService> serviceList;
    private AdapterServiceSeller adapterServiceSeller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seller);


        bottom_navigation=findViewById(R.id.bottom_navigation);


        filteredServiceTv=findViewById(R.id.filteredServiceTv);
        filterServiceBtn=findViewById(R.id.filterServiceBtn);
        searchSearchEt=findViewById(R.id.searchSearchEt);
        servicesRv=findViewById(R.id.servicesRv);

        nameTv=findViewById(R.id.nameTv);
        tabProductTV=findViewById(R.id.tabProductTV);
        tabOrderTV=findViewById(R.id.tabOrderTV);
        logoutBtn=findViewById(R.id.logoutBtn);
        editProfileBtn=findViewById(R.id.editProfileBtn);
        shopNameTv=findViewById(R.id.shopNameTv);
        profileIV=findViewById(R.id.profileIV);
        addProductBtn=findViewById(R.id.addProductBtn);
        addressTv=findViewById(R.id.addressTv);
        cityTv=findViewById(R.id.cityTv);

        productsRl=findViewById(R.id.productsRl);
        ordersRl=findViewById(R.id.ordersRl);







        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        checkUser();
        showProductsUI();
        loadAllService();

        //Search
        searchSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                try {
                    adapterServiceSeller.getFilter().filter(s);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mke offline
                //sign out
                //go to login Activity
                makeMeOffline();
            }
        });
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open edit profile activity
                startActivity(new Intent(MainSellerActivity.this, ProfileEditSellerActivity.class));
            }
        });

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add the product
                startActivity(new Intent(MainSellerActivity.this, AddServices.class));
            }
        });

        tabProductTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load service
                showProductsUI();
            }
        });
        tabOrderTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load orders
                showOrdersUI();
            }
        });

        filterServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainSellerActivity.this);
                builder.setTitle("Choose Category:")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                String  selected=Constants.productCategories1[which];
                                filteredServiceTv.setText(selected);
                                if (selected.equals("All")){
                                    //  load all
                                    loadAllService();
                                }
                                else {
                                    //
                                    loadFilteredServices(selected);
                                }
                            }
                        })
                .show();
            }
        });
    }

    private void loadFilteredServices(String selected) {
        serviceList=new ArrayList<>();

        //get all service which are add by seller

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Service")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                        // before getting reset list
                        serviceList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){

                            String serviceCategory=""+ds.child("productCategory").getValue();
                            // if selected Category  reset list matches service category then add list

                            if (selected.equals(serviceCategory)){
                                ModelService modelService=ds.getValue(ModelService.class);
                                serviceList.add(modelService);
                            }

//                            ModelService modelService=ds.getValue(ModelService.class);
//                            serviceList.add(modelService);
                        }
                        //set Adapter
                        adapterServiceSeller=new AdapterServiceSeller(MainSellerActivity.this,serviceList);
                        // set Adapter
                        servicesRv.setAdapter(adapterServiceSeller);
                    }
                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private void loadAllService() {
        serviceList=new ArrayList<>();

        //get all service which are add by seller

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Service")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                        // before getting reset list
                        serviceList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelService modelService=ds.getValue(ModelService.class);
                            serviceList.add(modelService);
                        }
                        //set Adapter
                        adapterServiceSeller=new AdapterServiceSeller(MainSellerActivity.this,serviceList);
                        // set Adapter
                        servicesRv.setAdapter(adapterServiceSeller);
                    }
                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }


    private void showProductsUI() {
        //show service ui hide order ui
        productsRl.setVisibility(View.VISIBLE);
        ordersRl.setVisibility(View.GONE);

        tabProductTV.setTextColor(getResources().getColor(R.color.black));
        tabProductTV.setBackgroundResource(R.drawable.shape_rect_04);

        tabOrderTV.setTextColor(getResources().getColor(R.color.white));
        tabOrderTV.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showOrdersUI() {
        //show orders ui hide products ui
        productsRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);

        tabProductTV.setTextColor(getResources().getColor(R.color.white));
        tabProductTV.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabOrderTV.setTextColor(getResources().getColor(R.color.black));
        tabOrderTV.setBackgroundResource(R.drawable.shape_rect_04);


    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout,fragment)
                .commit();
    }

    private void makeMeOffline() {

        //after logging in , make user online
        progressDialog.setMessage("Logout...");

        HashMap<String,Object> hashMap= new HashMap<>();
        hashMap.put("online","false");

        //update value to db
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //update successfully
                        firebaseAuth.signOut();
                        checkUser();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed update
                        Toast.makeText(MainSellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void checkUser() {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if (user==null){
            startActivity(new Intent(MainSellerActivity.this, LoginActivity.class));
            finish();
        }
        else {
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds:dataSnapshot.getChildren()){
                            //get data from the database
                            String name=""+ds.child("name").getValue();
                            String shopName=""+ds.child("shopName").getValue();
                            String profileImage=""+ds.child("profileImage").getValue();
                            String address=""+ds.child("address").getValue();
                            String city=""+ds.child("city").getValue().toString();

                            // set  data to UI
                            nameTv.setText(name);
                            shopNameTv.setText(shopName);
                            addressTv.setText(address);
                            cityTv.setText(city);
                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_baseline_store_24).into(profileIV);

                            }
                            catch (Exception e){
                                profileIV.setImageResource(R.drawable.ic_baseline_store_24);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}