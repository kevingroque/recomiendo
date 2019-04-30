package com.roque.app.recomiendo.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.felix.bottomnavygation.BadgeIndicator;
import com.felix.bottomnavygation.BottomNav;
import com.felix.bottomnavygation.ItemNav;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.roque.app.recomiendo.R;
import com.roque.app.recomiendo.fragments.CategoriesFragment;
import com.roque.app.recomiendo.fragments.HomeFragment;
import com.roque.app.recomiendo.fragments.ProfileFragment;
import com.roque.app.recomiendo.fragments.RoutesFragment;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private BottomNav mBottomNav;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private String current_user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null){
                    //Set user data
                }else {
                    goLoginScreen();
                }
            }
        };


        initializeBottonNav();

        HomeFragment homeFragment = new HomeFragment();
        FragmentManager(homeFragment);
    }


    BottomNav.OnTabSelectedListener listener = new BottomNav.OnTabSelectedListener() {
        @Override
        public void onTabSelected(int position) {
            switch (position) {
                case 0:
                    HomeFragment homeFragment = new HomeFragment();
                    FragmentManager(homeFragment);
                    break;
                case 1:
                    CategoriesFragment categoriesFragment = new CategoriesFragment();
                    FragmentManager(categoriesFragment);
                    break;
                case 2:
                    Intent intent = new Intent(MainActivity.this, AddPlaceActivity.class);
                    startActivity(intent);
                    break;
                case 3:
                    RoutesFragment routesFragment = new RoutesFragment();
                    FragmentManager(routesFragment);
                    break;
                case 4:
                    ProfileFragment profileFragment = new ProfileFragment();
                    FragmentManager(profileFragment);
                    break;
            }
        }

        @Override
        public void onTabLongSelected(int position) {
            Toast.makeText(MainActivity.this, "Long posicao " + position, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

        //verify if the user is registered
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            goLoginScreen();
        } else {

            current_user_id = mFirebaseAuth.getCurrentUser().getUid();

            mFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        if(!task.getResult().exists()){
                            Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                            startActivity(setupIntent);
                            finish();
                        }
                    } else {
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();
                    }

                }
            });

        }

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null){
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }


    private void initializeBottonNav(){
        final BadgeIndicator badgeIndicator = new BadgeIndicator(this, android.R.color.holo_red_dark, android.R.color.white);

        mBottomNav = findViewById(R.id.bottomNav);
        mBottomNav.addItemNav(new ItemNav(this, R.drawable.ic_location_placeholder).addColorAtive(R.color.colorAccent).addBadgeIndicator(badgeIndicator));
        mBottomNav.addItemNav(new ItemNav(this, R.drawable.ic_list).addColorAtive(R.color.colorAccent));
        mBottomNav.addItemNav(new ItemNav(this, R.drawable.ic_plus).addColorAtive(R.color.colorAccent));
        mBottomNav.addItemNav(new ItemNav(this, R.drawable.ic_destination).addColorAtive(R.color.colorAccent));
        mBottomNav.addItemNav(new ItemNav(this, R.drawable.ic_profile).addColorAtive(R.color.colorAccent).isProfileItem().addProfileColorAtive(android.R.color.holo_blue_dark).addProfileColorInative(android.R.color.black));
        mBottomNav.setTabSelectedListener(listener);
        mBottomNav.build();
    }


    private void FragmentManager(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.container_layout, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null).commit();
    }

    private void goLoginScreen() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void logOut(final View view){
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Cerrar Sesión");
        alertDialog.setMessage("Esta seguro de que quiere cerrar sesion?");
        // Alert dialog button
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mFirebaseAuth.signOut();
                        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                if (status.isSuccess()){
                                    goLoginScreen();
                                }else{
                                    Toast.makeText(MainActivity.this,"Error al cerrrar sesion", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Alert dialog action goes here
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}
