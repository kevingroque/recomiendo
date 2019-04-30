package com.roque.app.recomiendo.ui;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;
import com.roque.app.recomiendo.R;
import com.roque.app.recomiendo.adapters.CommentsAdapter;
import com.roque.app.recomiendo.models.Comment;
import com.roque.app.recomiendo.models.Rate;;
import com.xw.repo.BubbleSeekBar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;


public class SitesDetailsActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener,
        SmileRating.OnSmileySelectionListener, SmileRating.OnRatingSelectedListener{

    private ImageView mCoverPhoto;
    private TextView mTxtNameSite;
    private SliderLayout mImagesSlider;
    private TextView mTxtCategory;
    private TextView mTxtDescription;
    private TextView mTxtAddress;
    private SmileRating mSmileRating;
    private FloatingActionButton mFabRating;

    private GoogleMap mGoogleMap;
    private SupportMapFragment mSupportMapFragment;

    private StorageReference mStorageReference;
    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseAuth mFirebaseAuth;

    private HashMap<String, String> mHashMapPhotos;

    private String siteId, nameSite, categorySite,descriptionSite, addressSite, districtSite, phoneSite, currentUserId;
    private double latitudeSite, longitudeSite, ratingSite;
    private ArrayList<String> urlList;
    private List<Rate> rateList;
    private ArrayList<Integer> valuesRatingSecurity;
    private Bundle args, extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sites_details);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();

        currentUserId = mFirebaseAuth.getCurrentUser().getUid();

        urlList = new ArrayList<>();
        rateList = new ArrayList<>();
        valuesRatingSecurity = new ArrayList<>();

        mTxtNameSite = (TextView) findViewById(R.id.txt_sitesdetails_namesite);
        mImagesSlider = (SliderLayout) findViewById(R.id.slider_sitesdetails_cover);
        mTxtCategory = (TextView) findViewById(R.id.txt_sitesdetails_category);
        mTxtDescription = (TextView) findViewById(R.id.txt_sitedetails_description);
        mTxtAddress = (TextView) findViewById(R.id.txt_sitedetails_address);
        mSmileRating = (SmileRating) findViewById(R.id.ratingView_detailsfragment_levelsecurity);
        mFabRating = (FloatingActionButton) findViewById(R.id.fab_sitesdetails_recommend);
        mImagesSlider = (SliderLayout) findViewById(R.id.slider_sitesdetails_cover);

        //GET Extras
        getExtras();
        //SEND DATA TO FRAGMENT
        sendDataToFragments();

        initializePhotoSlider();

        getCategoryName();

        getRatingData();

        //Support Fragment
        mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment_sitesdetails_location);
        mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
                final LatLng latLng = new LatLng(latitudeSite, longitudeSite);


                MarkerOptions markerOptions = new MarkerOptions();

                markerOptions.position(latLng)
                        .title(nameSite)
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

                Marker marker = mGoogleMap.addMarker(markerOptions);

                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
            }
        });

        mSmileRating.setOnSmileySelectionListener(this);
        mSmileRating.setOnRatingSelectedListener(this);
        mSmileRating.setSelectedSmile(BaseRating.GOOD);
        mSmileRating.setTypeface(Typeface.DEFAULT);


        mTxtDescription.setText(descriptionSite);
        mTxtAddress.setText(addressSite);
        mTxtNameSite.setText(nameSite);



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void initializePhotoSlider(){
        mHashMapPhotos = new HashMap<String, String>();

        for (int i = 0; i < urlList.size(); i++) {
            mHashMapPhotos.put(String.valueOf(i), urlList.get(i));
        }

        mImagesSlider.stopAutoCycle();

        for (String name : mHashMapPhotos.keySet()){
            TextSliderView textSliderView = new TextSliderView(SitesDetailsActivity.this);

            textSliderView
                    .description(name)
                    .image(mHashMapPhotos.get(name))
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .setOnSliderClickListener(this);

            textSliderView.bundle(new Bundle());

            textSliderView.getBundle()
                    .putString("extra",name);

            mImagesSlider.addSlider(textSliderView);

        }

        mImagesSlider.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        mImagesSlider.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));
        mImagesSlider.addOnPageChangeListener(SitesDetailsActivity.this);
    }

    private void getExtras(){
        extras = getIntent().getExtras();
        if(extras != null) {
            siteId = extras.getString("SITE_ID");
            nameSite = extras.getString("NAME_SITE");
            categorySite = extras.getString("CATEGORY_ID");
            descriptionSite = extras.getString("DESCRIPTION_SITE");
            addressSite = extras.getString("ADDRESS_SITE");
            districtSite = extras.getString("DISTRICT_SITE");
            phoneSite = extras.getString("PHONE_SITE");
            latitudeSite = extras.getDouble("LATITUDE_SITE");
            longitudeSite = extras.getDouble("LONGITUDE_SITE");
            ratingSite = extras.getDouble("RATING_SITE");
            urlList = extras.getStringArrayList("URL_ARRAY");
        }
    }

    private void sendDataToFragments(){
        args = new Bundle();
        args.putStringArrayList("URL_ARRAY", urlList);
        args.putString("NAME_SITE", nameSite);
        args.putString("DESCRIPTION_SITE", descriptionSite);
        args.putString("ADDRESS_SITE", addressSite);
        args.putString("DISTRICT_SITE", districtSite);
        args.putString("PHONE_SITE", phoneSite);
        args.putDouble("LATITUDE_SITE", latitudeSite);
        args.putDouble("LONGITUDE_SITE", longitudeSite);
    }

    private void getCategoryName(){
        mFirebaseFirestore.collection("Categories").document(categorySite).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){

                        String getName = task.getResult().getString("nameCategory");

                        mTxtCategory.setText(getName);

                    }else {
                        Toast.makeText(SitesDetailsActivity.this, "(FIRESTORE Retrieve Error)", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }


    private void sendRatingToDatabase(int ratingPrice, int ratingFood, int ratingService, int ratingSecurity){

        Map<String, Object> ratingMap = new HashMap<>();
        ratingMap.put("userId", currentUserId);
        ratingMap.put("siteId", siteId);
        ratingMap.put("ratingValuePrice", ratingPrice);
        ratingMap.put("ratingValueFood", ratingFood);
        ratingMap.put("ratingValueService", ratingService);
        ratingMap.put("ratingValueSecurity", ratingSecurity);

        mFirebaseFirestore.collection("Sites/" + siteId + "/rating").add(ratingMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                if(!task.isSuccessful()){
                    Toast.makeText(SitesDetailsActivity.this, "Error Send Rating : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getRatingData(){
        mFirebaseFirestore.collection("Sites/" + siteId + "/rating")
                .addSnapshotListener(SitesDetailsActivity.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (!documentSnapshots.isEmpty()) {
                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    String ratingID = doc.getDocument().getId();
                                    Rate rates = doc.getDocument().toObject(Rate.class);
                                    rateList.add(rates);
                                }
                            }
                        }
                    }
                });
    }


    private void sendCommentToDatabase(String comment, final EditText mComentMensajeTxt){

        Date currentTime = Calendar.getInstance().getTime();

        Map<String, Object> commentMap = new HashMap<>();
        commentMap.put("userId", currentUserId);
        commentMap.put("siteId", siteId);
        commentMap.put("comment", comment);
        commentMap.put("timestamp", currentTime);


        mFirebaseFirestore.collection("Sites/" + siteId + "/comments").add(commentMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                if(!task.isSuccessful()){
                    Toast.makeText(SitesDetailsActivity.this, "Error Send Comment : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }else {
                    mComentMensajeTxt.setText("");
                }
            }
        });

    }

    private void getCommentsData(final CommentsAdapter commentsAdapter, final ArrayList<Comment> commentsList){

        Query commentsQuery = mFirebaseFirestore.collection("Sites/" + siteId + "/comments").orderBy("timestamp", Query.Direction.ASCENDING).limit(10);

        commentsQuery.addSnapshotListener(SitesDetailsActivity.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (!documentSnapshots.isEmpty()) {
                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    String commentId = doc.getDocument().getId();
                                    Comment comments = doc.getDocument().toObject(Comment.class);
                                    commentsList.add(comments);
                                    commentsAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });
    }

    public void closeDetailsView(View view){
        finish();
    }

    public void showRatingDialog(View view){

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_rating_site);


        BubbleSeekBar mRatingPrice =  dialog.findViewById(R.id.seek_bar_dialograting_level_price);
        BubbleSeekBar mRatingFood = dialog.findViewById(R.id.seek_bar_dialograting_level_food);
        BubbleSeekBar mRatingService = dialog.findViewById(R.id.seek_bar_dialograting_level_service);
        BubbleSeekBar mRatingSecurity = dialog.findViewById(R.id.seek_bar_dialograting_level_security);
        ImageButton dialogBtnClose = dialog.findViewById(R.id.btn_dialograting_close);
        Button dialogButtonOk = dialog.findViewById(R.id.btn_dialograting_rating);



        // Click cancel to dismiss android custom dialog box
        dialogBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Cancel process!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        // Your android custom dialog ok action
        // Action for custom dialog ok button click
        dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRatingToDatabase(0,0,0,0);
                Toast.makeText(getApplicationContext(), "Success process!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void showCommentsDialog(View view){
        final Dialog dialog = new Dialog(this, R.style.fullScreenDialog);
        dialog.setContentView(R.layout.dialog_comments);


        ImageButton dialogBtnClose = (ImageButton) dialog.findViewById(R.id.img_dialogcomments_close);
        ImageButton dialogBtnSend = (ImageButton) dialog.findViewById(R.id.btn_comment_post);
        final EditText dialogComment = (EditText) dialog.findViewById(R.id.edt_comment_message);
        RecyclerView dialogCommentsList = (RecyclerView) dialog.findViewById(R.id.rv_comment_list);
        ArrayList<Comment> commentsList;
        CommentsAdapter mCommentsAdapter;

        //RecyclerView Firebase List
        commentsList = new ArrayList<>();
        mCommentsAdapter = new CommentsAdapter(commentsList);
        dialogCommentsList.setHasFixedSize(true);
        dialogCommentsList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        dialogCommentsList.setAdapter(mCommentsAdapter);

        getCommentsData(mCommentsAdapter, commentsList);


        dialogBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String comment = dialogComment.getText().toString();
               sendCommentToDatabase(comment, dialogComment);
            }
        });



        dialog.show();

    }



    @Override
    protected void onStop() {
        mImagesSlider.stopAutoCycle();
        super.onStop();
    }

    //Slider Image Methods
    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this,slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //Smile Rating Methods
    @Override
    public void onRatingSelected(int level, boolean reselected) {

    }

    @Override
    public void onSmileySelected(int smiley, boolean reselected) {

        switch (smiley) {
            case SmileRating.BAD:
                Log.i(TAG, "Bad");
                break;
            case SmileRating.GOOD:
                Log.i(TAG, "Good");
                break;
            case SmileRating.GREAT:
                Log.i(TAG, "Great");
                break;
            case SmileRating.OKAY:
                Log.i(TAG, "Okay");
                break;
            case SmileRating.TERRIBLE:
                Log.i(TAG, "Terrible");
                break;
            case SmileRating.NONE:
                Log.i(TAG, "None");
                break;
        }

    }

}
