package com.roque.app.recomiendo.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.view.View;
import android.widget.Button;

import com.roque.app.recomiendo.R;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;

public class AddPlaceActivity extends AppCompatActivity {

    int preMin = -1;
    int preMax = -1;

    private TextInputLayout mTilCategories;
    private TextInputEditText mEdtCategories;
    private TextInputEditText mEdtAddress;
    private RangeSeekBar mRangePrices;
    private Button mBtnCreateSite;
    private RecyclerView mRvImagesToUpload;

    String[] listItems;
    boolean[] checkedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        mTilCategories = (TextInputLayout) findViewById(R.id.til_addplace_categories);
        mEdtCategories = (TextInputEditText) findViewById(R.id.edt_addplace_categories);
        mEdtAddress = (TextInputEditText) findViewById(R.id.edt_addplace_addresssite);
        mBtnCreateSite = (Button) findViewById(R.id.btn_sitesdetails_createsite);
        mRvImagesToUpload = (RecyclerView) findViewById(R.id.rv_sitesdeils_phototoupload);
        mRangePrices = (RangeSeekBar) findViewById(R.id.rangeSeekbar_addplace_rangeprices);
        mRangePrices.setNotifyWhileDragging(true);

        listItems = getResources().getStringArray(R.array.shopping_item);
        checkedItems = new boolean[listItems.length];

        final RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(this);
        seekBar.setRangeValues(0, 100);

        seekBar.setNotifyWhileDragging(true);

        mRangePrices.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {

            }
        });

        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {

                int diff = maxValue - minValue;

                if (diff == 39 || diff < 40) {
                    bar.setEnabled(false);
                    if(minValue != preMin){
                        seekBar.setSelectedMinValue(preMin);
                    }
                    else if(maxValue != preMax){
                        seekBar.setSelectedMaxValue(preMax);
                    }
                    AlertDialog.Builder alert = new AlertDialog.Builder(AddPlaceActivity.this);
                    alert.setNegativeButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            seekBar.setEnabled(true);
                        }
                    });
                    alert.setCancelable(false);
                    alert.setMessage(Html.fromHtml("You cant move below 40!!")).show();

                } else {
                    preMin = minValue;
                    preMax = maxValue;
                }

            }
        });


    }


    public void choosseCategoriesDialog(View view){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(AddPlaceActivity.this);
        mBuilder.setTitle("Escoge");
        mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if(isChecked){
                    mUserItems.add(position);
                }else{
                    mUserItems.remove((Integer.valueOf(position)));
                }
            }
        });

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = "";
                for (int i = 0; i < mUserItems.size(); i++) {
                    item = item + listItems[mUserItems.get(i)];
                    if (i != mUserItems.size() - 1) {
                        item = item + ", ";
                    }
                }
                mEdtCategories.setText(item);
            }
        });

        mBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        mBuilder.setNeutralButton("Limpiar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                    mUserItems.clear();
                    mEdtCategories.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }


}
