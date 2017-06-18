package com.example.pear;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pear.gson.User;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.CookieStore;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class LoginRegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText account;
    private EditText password;
    private Button login;
    private Button register;
    private ProgressDialog progressDialog;
    private Checkable rememberPass;
    private CircleImageView heardImage;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private String tempAccount;
    private String tempPassword;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        initView();//控件实例化
//        ActionBar actionBar = getSupportActionBar();//将ActionBar隐藏掉
//        if (actionBar != null) {
//            actionBar.hide();
//        }

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        initRememberPass();

        login.setOnClickListener(this);
        register.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.but_login:
                tempAccount = account.getText().toString();
                tempPassword = password.getText().toString();
                showProgressDialog();
                findUser(tempAccount, tempPassword);
                break;

            case R.id.but_register:
                Toast.makeText(this, "用户注册开发中", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    private void findUser(String tempAccount, String tempPassword) {

        editor = pref.edit();
        if (rememberPass.isChecked()) {//检查复选框是否被选中
            editor.putBoolean("remember_password", true);
            editor.putString("account", tempAccount);
            editor.putString("password", tempPassword);
        } else {
            editor.clear();
        }
        editor.apply();

//        CookieJarImpl cookieJar=new CookieJarImpl(new PersistentCookieStore(getApplicationContext()));
//        OkHttpClient client=new OkHttpClient.Builder()
//                .cookieJar(cookieJar)
//                .build();//直接在Application中撸上这段代码，这样就可以在应用初始化的时候实现cookie持久化



        OkHttpUtils
                .post()
                .url("http://119.29.77.37/Fungrouping/Home/User/login")
                .addParams("phone", tempAccount)
                .addParams("password", tempPassword)
                .build()
                .execute(new Callback<User>() {

                    @Override
                    public User parseNetworkResponse(Response response, int id) throws Exception {
                        String get = response.body().string();
                        Log.e("LoginRegisterActivity", "后台返回的数据为：" + get);
                        user = new Gson().fromJson(get, User.class);
                        return user;
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(final User response, int id) {
                        Log.e("LoginRegisterActivity", "解析结果为：" + response.getError_code());
                        if (response.getCode() == 20000 || response.getError_code() != 40000) {
                            editor = pref.edit();
                            if (rememberPass.isChecked()) {
                                editor.putString("heard_path", user.getResponse().getHead_path());
                            } else {
                                editor.clear();
                            }
                            editor.apply();
                            Intent toMainActivity = new Intent(LoginRegisterActivity.this, MainActivity.class);
                            closeProgressDialog();
                            startActivity(toMainActivity);
                            Toast.makeText(LoginRegisterActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        } else if (response.getCode() != 20000)
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginRegisterActivity.this, "密码或账号错误", Toast.LENGTH_SHORT).show();
                                }
                            });
                    }

                });
//        CookieStore upImage=cookieJar.getCookieStore();
//        String cookie=upImage.getCookies().toString();
//        Log.e("MainActivity","cookie为："+cookie);

    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(LoginRegisterActivity.this);
            progressDialog.setMessage("正在登陆...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void initRememberPass() {
        boolean isRemember = pref.getBoolean("remember_password", false);
        if (isRemember) {
            account.setText(pref.getString("account", ""));
            password.setText(pref.getString("password", ""));
            Glide.with(this).load(getResources().getString(R.string.base_url) + pref.getString("heard_path", "")).into(heardImage);//显示头像
            rememberPass.setChecked(true);
        }
    }

    private void initView() {//控件实例化
        account = (EditText) findViewById(R.id.edit_account);
        password = (EditText) findViewById(R.id.edit_password);
        login = (Button) findViewById(R.id.but_login);
        register = (Button) findViewById(R.id.but_register);
        rememberPass = (Checkable) findViewById(R.id.remember_pass);
        heardImage = (CircleImageView) findViewById(R.id.heard_image);
    }
}
