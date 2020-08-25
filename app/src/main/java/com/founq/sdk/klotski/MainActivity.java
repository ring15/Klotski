package com.founq.sdk.klotski;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private static final int START = 3;
    private static final int END = 6;
    private int current;

    private TextView mTimeText;
    private Button mBeforeBtn, mRestartBtn, mAfterBtn;

    private KlotskiView mView;
    private TimingHandler mTimingHandler;
    private ThreadSafe mThread;

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
        mTimingHandler = new TimingHandler(this);
        current = START;
        mView.setCallback(new KlotskiCallback() {
            @Override
            public void onStart() {
                if (mThread != null) {
                    mThread.exit = true;
                    mThread = null;
                }
                startTiming();
            }

            @Override
            public void onSuccess() {
                if (mThread != null) {
                    mThread.exit = true;
                    mThread = null;
                }
                if (current < END) {
                    AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("挑战成功")
                            .setMessage("用时：" + mTimeText.getText() + "，是否进行下一关？")
                            .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    current = current + 1;
                                    mView.startGame(current);
                                }
                            })
                            .show();
                } else {
                    AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("挑战成功")
                            .setMessage("用时：" + mTimeText.getText() + "，全部通过！")
                            .show();
                }
            }
        });
        mView.startGame(current);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_before:
                if (current > START) {
                    current = current - 1;
                    mView.startGame(current);
                } else {
                    Toast.makeText(this, "没有上一关", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_restart:
                mView.startGame(current);
                break;
            case R.id.btn_after:
                if (current < END) {
                    current = current + 1;
                    mView.startGame(current);
                } else {
                    Toast.makeText(this, "没有下一关", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mThread != null) {
            mThread.exit = true;
            mThread = null;
        }
    }

    /**
     * 启动倒计时
     */
    public void startTiming() {
        mThread = new ThreadSafe();
        mThread.start();
    }

    public class ThreadSafe extends Thread {

        public volatile boolean exit = false;

        public void run() {
            for (int i = 0; !exit && !MainActivity.this.isFinishing() && !MainActivity.this.isDestroyed(); i++) {
                Message message = new Message();
                message.arg1 = i;
                Log.e("test1", String.valueOf(i));
                mTimingHandler.sendMessage(message);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            exit = false;
        }
    }

    private static class TimingHandler extends Handler {
        WeakReference<MainActivity> mWeakReference;

        TimingHandler(MainActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = mWeakReference.get();
            int time = msg.arg1;
            if (activity != null) {
                activity.mTimeText.setText(formatTime(time));
            }
        }

        private String formatTime(int time) {
            int hour = time / 60 / 60 % 60;
            int minute = time / 60 % 60;
            int second = time % 60;
            String hourString = hour > 10 ? ("" + hour) : ("0" + hour);
            String minuteString = minute > 10 ? ("" + minute) : ("0" + minute);
            String secondString = second > 10 ? ("" + second) : ("0" + second);
            return hourString + ":" + minuteString + ":" + secondString;
        }
    }
}