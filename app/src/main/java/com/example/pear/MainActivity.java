package com.example.pear;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.pear.gson.CodeResponse;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private static final String TAG = "MainActivity";

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private CircleImageView heardImage;
    private ProgressDialog progressDialog;

    private Uri imageUri;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        initToolBar();
        initDrawerLayout();
        initNavigationView();

    }

    private void initNavigationView() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_call);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {//item点击事件的监听
                switch (item.getItemId()) {
                    case R.id.nav_call:
                        Toast.makeText(MainActivity.this, "You click Call", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_friends:
                        Toast.makeText(MainActivity.this, "You click Friends", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_location:
                        Toast.makeText(MainActivity.this, "You click Location", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_mail:
                        Toast.makeText(MainActivity.this, "You click Mail", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_setting:
                        Toast.makeText(MainActivity.this, "You click Setting", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        heardImage = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.icon_image);
        heardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog
                        .Builder(MainActivity.this)
                        .setTitle("   选择上传图片的方式")
                        .setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        selectImageFromCamera();
                                        break;
                                    case 1:
                                        selectImageFromAlbum();
                                        break;
                                }
                            }
                        }).show();
            }
        });

    }

    private void selectImageFromAlbum() {//从相册中获取照片
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);//运行时权限申请
        } else {
            openAlbum();
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");//打开相册
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    private void selectImageFromCamera() {//调用摄像头获取图片
        File outputImage = new File(getExternalCacheDir(), "heard_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.pear.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
            Log.e(TAG, "imageUri1的路径为：" + imageUri.toString());
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//指定图片的输出路径
        startActivityForResult(intent, TAKE_PHOTO);
    }

    private void initDrawerLayout() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_launcher_round);
        } else {
            Log.e("MainActivity", "actionBar对象获取失败");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.out_login:
                Toast.makeText(this, "退出登录", Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO://TAKE_PHOTO必须是常数
                if (resultCode == RESULT_OK) {
                    sendImageByOkhttp(getExternalCacheDir().getPath(),TAKE_PHOTO);
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //判断手机版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        //4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;

        }
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri=data.getData();
        String imagePath=getImagePath(uri,null);
        if (imagePath==null){
            Log.e(TAG, "imagePath为空");
        }else {
            sendImageByOkhttp(imagePath,CHOOSE_PHOTO);
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的Uri，则通过document id 处理
            Log.e(TAG, "1");
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            Log.e(TAG,"2");
            //如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            Log.e(TAG,"3");
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        if (imagePath==null){
            Log.e(TAG,"imagePath为空");
        }else{
            sendImageByOkhttp(imagePath,CHOOSE_PHOTO);
            //handleImagePath(imagePath);
        }

    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过uri和selection来获取图片的真实路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {//权限选择的回调方法
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "你拒绝了此项权限\n导致头像无法上传", Toast.LENGTH_LONG).show();
                }
                break;
            default:
        }
    }

    private void sendImageByOkhttp(String imagePath,int type) {
        File tempImage=null;
        if (type==TAKE_PHOTO){
            tempImage=new File(imagePath,"heard_image.jpg");
        }else if (type==CHOOSE_PHOTO){
            tempImage=new File(imagePath);
        }
        if (tempImage.exists()) {
            //如果图片存在就用OKhttp上传
            Log.e(TAG, "tempImage的路径为：" + tempImage.toString());//tempImage的路径为：/storage/emulated/0/Android/data/com.example.pear/cache/heard_image.jpg
            showProgressDialog();
            OkHttpUtils
                    .post()
                    .url(getResources().getString(R.string.base_url) + "/Home/User/uploadAvatar")
                    .addFile("new_heard_image", "heard_image.jpg", tempImage)
                    .build()
                    .execute(new Callback<CodeResponse>() {
                        @Override
                        public CodeResponse parseNetworkResponse(Response response, int id) throws Exception {
                            String rep = response.body().string();
                            CodeResponse codeJson = new Gson().fromJson(rep, CodeResponse.class);
                            Log.e(TAG, "后台返回的数据为：" + rep);
                            Log.e(TAG, "CodeResponse中携带的信息为：" + codeJson.getMsg());
                            return codeJson;
                        }

                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(final CodeResponse response, int id) {
                            if (response.getCode() == 20000) {
                                editor = pref.edit();
                                editor.putString("heard_path", response.getResponse());
                                editor.apply();
                                        closeProgressDialog();
                                        Toast.makeText(MainActivity.this, "图片上传成功", Toast.LENGTH_SHORT).show();

                            } else {
                                        closeProgressDialog();
                                        Toast.makeText(MainActivity.this, "上传失败,\n" + response.getMsg(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Log.e(TAG, "tempImage不存在");
        }


    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("头像正在上传...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
}