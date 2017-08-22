package com.wang.android70;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.wang.android70.utils.MeImageTools;

import java.io.File;

import permissions.dispatcher.NeedsPermission;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button myButton,button2;
    ImageView imageView;
    String mCurrentPath ="";
    String[] permissons ={ Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private String absolutePath;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myButton = (Button) findViewById(R.id.button);
        imageView = (ImageView) findViewById(R.id.imageView);
        button2 = (Button) findViewById(R.id.button2);
        myButton.setOnClickListener(this);
        button2.setOnClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/AAAAAA/" + System.currentTimeMillis() + ".jpg");
                file.getParentFile().mkdirs();
                absolutePath = file.getAbsolutePath();
                Uri uri =null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//7.0及以上
                    Uri uriForFile = FileProvider.getUriForFile(this, "com.wang.android70.fileProvider", file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
                    intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                }
                startActivityForResult(intent, 100);
                break;
            case R.id.button2:
                //Android4.4之后对选择相册图片后返回的图片uri不再是图片的路径
                // content://com.android.providers.media.documents/document/image%3A301185
                // 如果使用的是Action是PICK那还是返回的图片路径.
                //Android4.4之前content://media/external/images/media/301185
                Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 200);
                break;
        }
    }
    @NeedsPermission(Manifest.permission.CAMERA)
    void showCamera(){

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            startCrop(file.getAbsolutePath());
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);
        }
        if (requestCode == 200 && resultCode == RESULT_OK) {
            if (data != null) {
                Uri data1 = data.getData();
                String path = MeImageTools.getPath(this, data1);
                startCrop(path);
            }
        }
        if(requestCode == 400 && resultCode ==70){
            byte[] bytes = data.getByteArrayExtra("byte");
            if(bytes!= null){
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if(bitmap != null){
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
    private void startCrop(String imagePath){
         Intent intent = new Intent(this,ClipeImageActivity.class);
         intent.putExtra("imagePath",imagePath);
         startActivityForResult(intent,400);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
