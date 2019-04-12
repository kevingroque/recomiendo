package com.roque.app.recomiendo.activities;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.hsalf.smilerating.SmileRating;
import com.roque.app.recomiendo.R;

import java.util.ArrayList;
import java.util.HashMap;

import static android.support.constraint.Constraints.TAG;


public class SitesDetailsActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener,
        SmileRating.OnSmileySelectionListener, SmileRating.OnRatingSelectedListener{

    private ImageView mCoverPhoto;
    private TextView mTxtNameSite;
    private SliderLayout mImagesSlider;
    private TextView mTxtCategory;
    private TextView mTxtDescription;
    private TextView mTxtAddress;
    private SmileRating mSmileRating, mSmileRatingPrice, mSmileRatingFood,mSmileRatingService ,mSmileRatingSecurity;
    private FloatingActionButton mFabRating;

    private GoogleMap mGoogleMap;
    private SupportMapFragment mSupportMapFragment;

    private HashMap<String, String> mHashMapPhotos;

    private String siteId, nameSite, categorySite,descriptionSite, addressSite, districtSite, phoneSite, url_index_0;
    private double latitudeSite, longitudeSite, ratingSite;
    private int ratingPrice, ratingFood, ratingService;
    private ArrayList<String> urlList = new ArrayList<>();
    private Bundle args, extras;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sites_details);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        mTxtNameSite = (TextView) findViewById(R.id.txt_sitesdetails_namesite);
        mImagesSlider = (SliderLayout) findViewById(R.id.slider_sitesdetails_cover);
        mTxtCategory = (TextView) findViewById(R.id.txt_sitesdetails_category);
        mTxtDescription = (TextView) findViewById(R.id.txt_sitedetails_description);
        mTxtAddress = (TextView) findViewById(R.id.txt_sitedetails_address);
        mSmileRating = (SmileRating) findViewById(R.id.ratingView_detailsfragment_levelsecurity);
        mSmileRatingPrice = (SmileRating) findViewById(R.id.ratingView_dialograting_levelprice);
        mSmileRatingFood = (SmileRating) findViewById(R.id.ratingView_dialograting_levelfood);
        mSmileRatingService = (SmileRating) findViewById(R.id.ratingView_dialograting_levelservice);
        mSmileRatingSecurity = (SmileRating) findViewById(R.id.ratingView_detailsfragment_levelsecurity);
        mFabRating = (FloatingActionButton) findViewById(R.id.fab_sitesdetails_recommend);
        mImagesSlider = (SliderLayout) findViewById(R.id.slider_sitesdetails_cover);

        //GET Extras
        getExtras();
        //SEND DATA TO FRAGMENT
        sendDataToFragments();

        initializePhotoSlider();



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
        //mSmileRatingPrice.setOnRatingSelectedListener(this);
        //mSmileRatingFood.setOnRatingSelectedListener(this);
        //mSmileRatingService.setOnRatingSelectedListener(this);
        //mSmileRatingSecurity.setOnRatingSelectedListener(this);

        mSmileRating.setTypeface(Typeface.DEFAULT);


        mTxtDescription.setText(descriptionSite);
        mTxtAddress.setText(addressSite);
        mTxtNameSite.setText(nameSite);
        mTxtCategory.setText(categorySite);



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
            url_index_0 = extras.getString("URL_INDEX_0");
            latitudeSite = extras.getDouble("LATITUDE_SITE");
            longitudeSite = extras.getDouble("LONGITUDE_SITE");
            ratingSite = extras.getDouble("RATING_SITE");
            ratingPrice = extras.getInt("RATING_PREICE_SITE");
            ratingFood = extras.getInt("RATING_FOOD_SITE");
            ratingService = extras.getInt("RATING_SERVICE_SITE");
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
        args.putInt("RATING_PREICE_SITE", ratingPrice);
        args.putInt("RATING_FOOD_SITE", ratingFood);
        args.putInt("RATING_SERVICE_SITE", ratingService);
    }

    public void closeDetailsView(View view){
        finish();
    }

    public void showRatingDialog(View view){

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_rating_site);

        ImageButton dialogBtnClose = (ImageButton) dialog.findViewById(R.id.btn_dialograting_close);
        Button dialogButtonOk = (Button) dialog.findViewById(R.id.btn_dialograting_rating);
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
                Toast.makeText(getApplicationContext(), "Success process!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
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
