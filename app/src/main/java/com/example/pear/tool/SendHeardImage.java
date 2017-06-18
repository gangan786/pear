package com.example.pear.tool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.example.pear.gson.CodeResponse;

import java.io.File;
import java.io.IOException;

/**
 * Created by 陈淦 on 2017/6/11.向后台发送照片
 */

public class SendHeardImage extends Activity {

    private static CodeResponse codeResponse;
    public static int TAKE_PHOTO = 1;

    private  Context context;
    private  Activity activity;
    private static Uri imageUrl;

    public SendHeardImage(Activity activity, Context context) {
        this.context = context;
        this.activity = activity;
    }

    public  CodeResponse sendImageFromCanera() {
        File outputImage = new File(context.getExternalCacheDir(), "output_image");
        try {
            outputImage.createNewFile();
            if (outputImage.exists()) {
                outputImage.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= 24) {
            imageUrl = FileProvider.getUriForFile(context, "com.example.pear.fileprovider", outputImage);
        } else {
            imageUrl = Uri.fromFile(outputImage);
        }
        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE+CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUrl);
        activity.startActivityForResult(intent,TAKE_PHOTO);

        return null;
    }
}
