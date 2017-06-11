package com.yl.yuanlu.pocketresume;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.gson.reflect.TypeToken;
import com.yl.yuanlu.pocketresume.Model.BasicInfo;
import com.yl.yuanlu.pocketresume.Utils.ModelUtils;
import com.yl.yuanlu.pocketresume.Utils.PermissionUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by LUYUAN on 5/31/2017.
 */

public class BasicInfoEditActivity extends AppCompatActivity {

    @BindView(R.id.basicinfo_edit_user_picture_layout) FrameLayout basicinfoImageLayout;
    @BindView(R.id.basicinfo_edit_user_picture) ImageView basicinfoUserImage;
    @BindView(R.id.basicinfo_edit_name) EditText basicinfoName;
    @BindView(R.id.basicinfo_edit_email) EditText basicinfoEmail;

    public static final String KEY_BASICINFO = "basic_info";
    private static int REQ_CODE_PICK_IMAGE = 1;
    private static final String GOOGLE_PHOTO_AUTHORITY = "com.google.android.apps.photos.contentprovider";

    private BasicInfo basicInfo;
    private Bitmap bmp;
    private Uri pickedImageUriWithAuthority;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basicinfo_edit);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Basic Information");

        basicInfo = ModelUtils.toObject(getIntent().getStringExtra(KEY_BASICINFO), new TypeToken<BasicInfo>(){});

        setupUI();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        else if(item.getItemId() == R.id.edit_save) {
            saveAndExit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQ_CODE_PICK_IMAGE && resultCode==RESULT_OK && data!=null) {
            Uri pickedImageUri = data.getData();
            getImageUrlWithAuthority(pickedImageUri);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch(requestCode) {
                case PermissionUtils.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE :
                    pickImage();
                    break;
                case PermissionUtils.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE :
                    pickedImageUriWithAuthority = writeToTempImageAndGetPathUri();
                    displayUserPicture();
                    break;
            }

        }
    }

    private void setupUI() {
        if(basicInfo == null) {
            basicInfo = new BasicInfo();
        }
        else {
            basicinfoUserImage.setImageURI(basicInfo.userPictureUri);
            basicinfoUserImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

            basicinfoName.setText(basicInfo.name);
            basicinfoEmail.setText(basicInfo.email);

            basicinfoImageLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(PermissionUtils.permissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE, BasicInfoEditActivity.this)) {
                        pickImage();
                    }
                    else {
                        PermissionUtils.requestReadExternalStoragePermission(BasicInfoEditActivity.this);
                    }
                }
            });
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ_CODE_PICK_IMAGE);
    }

    private void getImageUrlWithAuthority(Uri pickedImageUri) {
        InputStream is = null;
        String pickedImageUriAuthority = pickedImageUri.getAuthority();
        if(pickedImageUriAuthority != null) {
//            Log.i("Yuan DBG : ", pickedImageUri.getAuthority());
            if(!pickedImageUriAuthority.contains(GOOGLE_PHOTO_AUTHORITY)) {
                pickedImageUriWithAuthority = pickedImageUri;
                displayUserPicture();
            }
            else {
                try {
                    is = getContentResolver().openInputStream(pickedImageUri);
                    bmp = BitmapFactory.decodeStream(is);
                    if(PermissionUtils.permissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE, BasicInfoEditActivity.this)) {
                        pickedImageUriWithAuthority = writeToTempImageAndGetPathUri();
                        displayUserPicture();
                    }
                    else {
                        PermissionUtils.requestWriteExternalStoragePermission(BasicInfoEditActivity.this);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private Uri writeToTempImageAndGetPathUri() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        return Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bmp, "User_Picture_Local", null));
    }

    private void displayUserPicture() {
        if(pickedImageUriWithAuthority!=null) {
            basicInfo.userPictureUri = pickedImageUriWithAuthority;
            Bitmap pickedImageBM;
            try {
                pickedImageBM = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pickedImageUriWithAuthority);
                basicinfoUserImage.setImageBitmap(pickedImageBM);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveAndExit() {
        basicInfo.name = String.valueOf(basicinfoName.getText());
        basicInfo.email = String.valueOf(basicinfoEmail.getText());
        Intent resultIntent = new Intent();
        resultIntent.putExtra(KEY_BASICINFO, ModelUtils.toString(basicInfo, new TypeToken<BasicInfo>(){}));
        setResult(RESULT_OK, resultIntent);
        finish();
    }

}
