package com.wang.android70;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.oginotihiro.cropview.CropView;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ClipeImageActivity extends AppCompatActivity {
    private CropView cropView;
    private Button yes,no;
    private String iamgePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clipe_image);
        initView();
        initData();
        onMyClick();
    }

    protected void initView() {
        cropView = (CropView) findViewById(R.id.cropView);
        yes = (Button) findViewById(R.id.clipe_yes);
        no = (Button) findViewById(R.id.clipe_no);
    }

    protected void initData() {
        iamgePath = getIntent().getStringExtra("imagePath");
        if(!TextUtils.isEmpty(iamgePath)){
            File file = new File(iamgePath);
            CropView cropView = this.cropView.asSquare();
            this.cropView.of(Uri.fromFile(file))
                    /*裁剪框的大小*/
                  .withAspect(50, 150)
                    /*输出图片的大小*/
                  .withOutputSize(515, 900)
                  .initialize(this);
        }
    }

    protected void onMyClick() {
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap output = cropView.getOutput();
                if(output!=null){
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    output.compress(Bitmap.CompressFormat.JPEG,100,bos);
                    byte[] bytes = bos.toByteArray();
                    Intent intent = new Intent(ClipeImageActivity.this,MainActivity.class);
                    intent.putExtra("byte",bytes);
                    setResult(70,intent);
                    finish();
               }
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
