package com.example.pear;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;

public class TempActivity extends AppCompatActivity {

    @BindView(R.id.button_1)
    Button button1;
    @BindView(R.id.textView_1)
    TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        butterknife.ButterKnife.bind(this);
    }

    @OnClick(R.id.button_1)
    public void onViewClicked() {
    }
}
