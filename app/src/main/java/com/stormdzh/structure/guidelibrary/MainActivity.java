package com.stormdzh.structure.guidelibrary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.stormdzh.structure.libguide.HightLIghtGuideLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView guide_01, guide_02, guide_03, showGuide;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        guide_01 = findViewById(R.id.guide_01);
        guide_02 = findViewById(R.id.guide_02);
        guide_03 = findViewById(R.id.guide_03);
        showGuide = findViewById(R.id.showGuide);


        guide_01.setOnClickListener(this);
        guide_02.setOnClickListener(this);
        guide_03.setOnClickListener(this);
        showGuide.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.guide_01:
                Toast.makeText(this, "点击了引导1", Toast.LENGTH_LONG).show();
                break;
            case R.id.guide_02:
                Toast.makeText(this, "点击了引导2", Toast.LENGTH_LONG).show();
                break;
            case R.id.guide_03:
                Toast.makeText(this, "点击了引导3", Toast.LENGTH_LONG).show();
                break;
            case R.id.showGuide:
                showGuide();
                break;
        }
    }

    private void showGuide() {
        int i = index % 3;
        index = index + 1;
        View targetView = null;
        if (i == 0) {
            targetView = guide_01;
            HightLIghtGuideLayout.builder(this)
                    .addHighLightGuidView(targetView)
                    .setHighLightStyle(HightLIghtGuideLayout.VIEWSTYLE_CIRCLE)
                    .setHandText("引导1提示！")
                    .setHandLocation(HightLIghtGuideLayout.HAND_RIGHT)
                    .setStyleType(HightLIghtGuideLayout.STYLE_LIGHT)
                    .show();
        } else if (i == 1) {
            targetView = guide_02;
            HightLIghtGuideLayout.builder(this)
                    .addHighLightGuidView(targetView)
                    .setHighLightStyle(HightLIghtGuideLayout.VIEWSTYLE_CIRCLE)
                    .setHandText("引导2提示！")
                    .setHandLocation(HightLIghtGuideLayout.HAND_LEFT)
                    .setStyleType(HightLIghtGuideLayout.STYLE_LIGHT)
                    .show();
        } else if (i == 2) {
            targetView = guide_03;
            HightLIghtGuideLayout.builder(this)
                    .addHighLightGuidView(targetView)
                    .setHandText("")
                    .setHandLocation(HightLIghtGuideLayout.HAND_RIGHT)
                    .setStyleType(HightLIghtGuideLayout.STYLE_PIC)
                    .show();
        }


    }
}
