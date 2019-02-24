package com.taxialeairy.provider.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.taxialeairy.provider.Helper.LocaleUtils;
import com.taxialeairy.provider.Helper.SharedHelper;
import com.taxialeairy.provider.R;

public class ActivitySetting extends AppCompatActivity {
    private RadioButton radioEnglish, radioArabic;

    private LinearLayout lnrEnglish, lnrArabic, lnrHome, lnrWork;

    private int UPDATE_HOME_WORK = 1;


    private TextView txtHomeLocation, txtWorkLocation, txtDeleteWork, txtDeleteHome;
    ;

    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);
        init();
    }

    private void init() {

        radioEnglish = (RadioButton) findViewById(R.id.radioEnglish);
        radioArabic = (RadioButton) findViewById(R.id.radioArabic);
        lnrEnglish = (LinearLayout) findViewById(R.id.lnrEnglish);
        lnrArabic = (LinearLayout) findViewById(R.id.lnrArabic);

        backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if (SharedHelper.getKey(ActivitySetting.this, "language").equalsIgnoreCase("en")) {
            radioEnglish.setChecked(true);
        } else {
            radioArabic.setChecked(true);
        }

        lnrEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioArabic.setChecked(false);
                radioEnglish.setChecked(true);
            }
        });

        lnrArabic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioEnglish.setChecked(false);
                radioArabic.setChecked(true);
            }
        });

        radioArabic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    radioEnglish.setChecked(false);
                    SharedHelper.putKey(ActivitySetting.this, "language", "ar");
                    setLanguage();
                    //recreate();
                    startActivity(new Intent(ActivitySetting.this, MainActivity.class));
                }
            }
        });

        radioEnglish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    radioArabic.setChecked(false);
                    SharedHelper.putKey(ActivitySetting.this, "language", "en");
                    setLanguage();
                    startActivity(new Intent(ActivitySetting.this, MainActivity.class));
                    //recreate();
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }

    private void setLanguage() {
        String languageCode = SharedHelper.getKey(ActivitySetting.this, "language");
        LocaleUtils.setLocale(this, languageCode);
    }

}
