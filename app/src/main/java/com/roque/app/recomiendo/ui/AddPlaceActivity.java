package com.roque.app.recomiendo.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.roque.app.recomiendo.R;
import com.roque.app.recomiendo.models.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gun0912.tedbottompicker.TedRxBottomPicker;
import io.reactivex.disposables.Disposable;

public class AddPlaceActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_CODE_MAPS_RESULT = 1;

    private ImageView iv_image;
    private List<Uri> selectedUriList;
    private String districtSite, nameSite;

    private ViewGroup mSelectedImagesContainer;
    private Disposable multiImageDisposable;
    private RequestManager requestManager;
    private TextView mTxtNoImage;
    private EditText mNameInput, mOffersInput, mDescriptionInput;
    private Button mBtnCreateSite;
    private TextView mCategoryInput, mPriceRangeInput,mAddressInput;

    private double latitude;
    private double longitude;

    private StorageReference mStorageReference;
    private FirebaseAuth mFirebaseAuth;

    private FirebaseFirestore mFirestore;
    private DocumentSnapshot mLastVisible;
    private Boolean isFirstPageFirstLoad = true;
    private List<Category> mCategoryList = new ArrayList<>();
    private ArrayList<String> mCategoryNamesList = new ArrayList<>();
    private String[] listPriceRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        mFirestore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        mBtnCreateSite = findViewById(R.id.btn_addplace_create_site);
        mTxtNoImage = findViewById(R.id.txt_addplace_no_image);
        mNameInput = findViewById(R.id.edt_add_place_name);
        mOffersInput = findViewById(R.id.edt_add_place_offers);
        mCategoryInput = findViewById(R.id.edt_add_place_category);
        mPriceRangeInput = findViewById(R.id.edt_add_place_range_prices);
        mAddressInput = findViewById(R.id.edt_add_place_address);
        mDescriptionInput = findViewById(R.id.edt_add_place_description);
        iv_image = findViewById(R.id.iv_addplace_image);
        mSelectedImagesContainer = findViewById(R.id.selected_photos_container);
        requestManager = Glide.with(this);
        setRxMultiShowButton();
        getDataCategories();

        listPriceRange = getResources().getStringArray(R.array.price_range);

        mAddressInput.setOnClickListener(this);
        mBtnCreateSite.setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        if (multiImageDisposable != null && !multiImageDisposable.isDisposed()) {
            multiImageDisposable.dispose();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edt_add_place_address:
                Intent intent = new Intent(AddPlaceActivity.this, MapsActivity.class);
                startActivityForResult(intent, REQUEST_CODE_MAPS_RESULT);
                break;

            case R.id.btn_addplace_create_site:
                registerDataNewSite();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MAPS_RESULT){
            if (resultCode == RESULT_OK){
                String address = data.getStringExtra("address");
                latitude = data.getDoubleExtra("latitude", 0);
                longitude = data.getDoubleExtra("longitude", 0);
                districtSite = data.getStringExtra("districtSite");
                mAddressInput.setText(address);
            }

            if (resultCode == RESULT_CANCELED){
                Toast.makeText(AddPlaceActivity.this, "Not selected" , Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getDataCategories() {
        Query firstQuery = mFirestore.collection("Categories").orderBy("nameCategory", Query.Direction.DESCENDING).limit(10);
        firstQuery.addSnapshotListener(AddPlaceActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    if (isFirstPageFirstLoad) {
                        mLastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                        mCategoryList.clear();
                    }

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String categoryId = doc.getDocument().getId();
                            Category category = doc.getDocument().toObject(Category.class).withId(categoryId);

                            if (isFirstPageFirstLoad) {
                                mCategoryList.add(category);
                                mCategoryNamesList.add(category.getNameCategory());
                            } else {
                                mCategoryList.add(0, category);
                            }
                        }
                    }
                    isFirstPageFirstLoad = false;
                }
            }
        });


    }

    private void setRxMultiShowButton() {

        Button btnRxMultiShow = findViewById(R.id.btn_addplace_upload_images);
        btnRxMultiShow.setOnClickListener(view -> {
            PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {

                    multiImageDisposable = TedRxBottomPicker.with(AddPlaceActivity.this)
                            //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                            .setPeekHeight(1600)
                            .showTitle(false)
                            .setCompleteButtonText("Aceptar")
                            .setEmptySelectionText("No hay selección")
                            .setSelectedUriList(selectedUriList)
                            .showMultiImage()
                            .subscribe(uris -> {
                                selectedUriList = uris;
                                showUriList(uris);
                            }, Throwable::printStackTrace);


                }

                @Override
                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                    Toast.makeText(AddPlaceActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                }


            };

            checkPermission(permissionlistener);
        });

    }

    private void checkPermission(PermissionListener permissionlistener) {
        TedPermission.with(AddPlaceActivity.this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void showUriList(List<Uri> uriList) {
        // Remove all views before
        // adding the new ones.
        mSelectedImagesContainer.removeAllViews();

        mTxtNoImage.setVisibility(View.INVISIBLE);
        mSelectedImagesContainer.setVisibility(View.VISIBLE);

        int widthPixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        int heightPixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());

        for (Uri uri : uriList) {

            View imageHolder = LayoutInflater.from(this).inflate(R.layout.item_select_image, null);
            ImageView thumbnail = imageHolder.findViewById(R.id.img_itelemselect_select);

            requestManager
                    .load(uri.toString())
                    .apply(new RequestOptions().fitCenter())
                    .into(thumbnail);

            mSelectedImagesContainer.addView(imageHolder);

            thumbnail.setLayoutParams(new FrameLayout.LayoutParams(widthPixel, heightPixel));

        }

        if (uriList.size() == 0) {
            mTxtNoImage.setVisibility(View.VISIBLE);

        }

    }


    public void choosseCategoriesDialog(View view) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(AddPlaceActivity.this);
        mBuilder.setTitle("Elige una categoría");

        mBuilder.setSingleChoiceItems( mCategoryNamesList.toArray(new String[mCategoryNamesList.size()]), -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                mCategoryInput.setText(mCategoryNamesList.toArray(new String[mCategoryNamesList.size()])[item]);
                dialog.dismiss();
            }
        });


        mBuilder.setCancelable(false);

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    public void choossePriceRangeDialog(View view){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(AddPlaceActivity.this);
        mBuilder.setTitle("Elige un rango de precios");

        mBuilder.setSingleChoiceItems( listPriceRange, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                mPriceRangeInput.setText(listPriceRange[item]);
                dialog.dismiss();
            }
        });

        mBuilder.setCancelable(false);

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void registerDataNewSite(){

        nameSite = mNameInput.getText().toString();
        String offers = mOffersInput.getText().toString();
        String priceRange = mPriceRangeInput.getText().toString();
        String address = mAddressInput.getText().toString();
        String description = mDescriptionInput.getText().toString();
        String nameCategory = mCategoryInput.getText().toString();

        ArrayList<String> urlDownloadImages = new ArrayList<>();

        Map<String, Object> siteMap = new HashMap<>();


        for (int i = 0; i < urlDownloadImages.size(); i++){
            Log.d("TAG", "url_image: "+ urlDownloadImages.get(i));
        }

        if (!TextUtils.isEmpty(nameSite)){

            for (int i = 0; i< selectedUriList.size(); i++){
                StorageReference filepath = FirebaseStorage.getInstance().getReference().child("sites").child(nameSite.toLowerCase()).child("imagen_"+ i + ".jpg");
                filepath.putFile(selectedUriList.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                urlDownloadImages.add(uri.toString());
                            }
                        });
                        /*Uri downloadURL = taskSnapshot.getDownloadUrl();
                        urlDownloadImages.add(downloadURL.toString());*/
                        Toast.makeText(AddPlaceActivity.this, "Exitoso", Toast.LENGTH_SHORT).show();

                    }
                });
            }





            /*if ( !TextUtils.isEmpty(offers)
                    && !TextUtils.isEmpty(priceRange)
                    && !TextUtils.isEmpty(address)
                    && !TextUtils.isEmpty(description)
                    && !TextUtils.isEmpty(nameCategory)){

                //mSetupProgress.setVisibility(View.VISIBLE);


                siteMap.put("nameSite", nameSite);
                siteMap.put("offersSite", offers);
                siteMap.put("priceRangeSite", priceRange);
                siteMap.put("addressSite", address);
                siteMap.put("districtSite", districtSite);
                siteMap.put("descriptionSite", description);
                siteMap.put("categoryNameSite", nameCategory);
                siteMap.put("latitude", latitude);
                siteMap.put("longitude", longitude);
                siteMap.put("url_imagen", urlDownloadImages);

                mFirestore.collection("Sites").add(siteMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        //mProgress.dismiss();
                        if (task.isSuccessful()){
                            goMainScreen();
                        }else {
                            Toast.makeText(AddPlaceActivity.this, "Error al crear lugar", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }else{
                //mProgress.dismiss();
                Toast.makeText(AddPlaceActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
*/
        }


    }


    private void goMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
