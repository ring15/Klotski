package com.founq.sdk.klotski;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int START = 3;
    private static final int END = 6;
    private int current;

    private TextView mTimeText;
    private Button mBeforeBtn, mRestartBtn, mAfterBtn;

    private KlotskiView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTimeText = findViewById(R.id.tv_time);
        mBeforeBtn = findViewById(R.id.btn_before);
        mRestartBtn = findViewById(R.id.btn_restart);
        mAfterBtn = findViewById(R.id.btn_after);
        mView = findViewById(R.id.view);
        init();
    }

    private void init() {
        current = START;
        mView.setGameLevel(current);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_before:
                if (current > START) {
                    mView.setGameLevel(current - 1);
                    current = current - 1;
                } else {
                    Toast.makeText(this, "没有上一关", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_restart:
                mView.setGameLevel(current);
                break;
            case R.id.btn_after:
                if (current < END) {
                    mView.setGameLevel(current + 1);
                    current = current + 1;
                } else {
                    Toast.makeText(this, "没有下一关", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}