package com.founq.sdk.klotski;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ring on 2020/8/24.
 */
public class KlotskiView extends View {

    private Context mContext;
    private int width;
    private int height;

    private Paint mTextPaint;
    private Paint mLinePaint;

    //困难等级
    private int level = 3;

    //存放的数组
    private List<Integer> mNumList = new ArrayList<>();
    private List<Integer> startList;

    public KlotskiView(Context context) {
        this(context, null);
    }

    public KlotskiView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KlotskiView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setBackgroundColor(mContext.getColor(R.color.purple));
        mTextPaint = new Paint();
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(DensityUtils.dipTopx(mContext, 25));
        mTextPaint.setColor(mContext.getColor(R.color.text_color));

        mLinePaint = new Paint();
        mLinePaint.setColor(mContext.getColor(R.color.background_color));
        changeView();
    }

    private int getMySize(int defaultSize, int measureSpec) {
        int mySize = defaultSize;

        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.UNSPECIFIED: {//如果没有指定大小，就设置为默认大小
                mySize = defaultSize;
                break;
            }
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY: {//如果测量模式是最大取值为size
                //我们将大小取最大值,你也可以取其他值
                mySize = size;
                break;
            }//如果是固定的大小，那就不要去改变它
        }
        return mySize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMySize(100, widthMeasureSpec);
        height = getMySize(100, heightMeasureSpec);

        if (width < height) {
            height = width;
        } else {
            width = height;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        addNumText(mNumList, canvas);
    }

    public void setGameLevel(int level) {
        this.level = level;
        changeView();
        invalidate();
    }

    private void changeView() {
        startList = new ArrayList<>();
        for (int i = 0; i < level * level; i++) {
            startList.add(i);
        }
        startList.add(startList.get(0));
        startList.remove(0);
        mNumList = randomNum(startList);
    }

    private void addNumText(List<Integer> list, Canvas canvas) {
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            int row = i / level;
            int column = i % level;
            int tX = (width - (level - 1) * DensityUtils.dipTopx(mContext, 4)) / (level * 2) +
                    column * (width - (level - 1) * DensityUtils.dipTopx(mContext, 4)) / level +
                    column * DensityUtils.dipTopx(mContext, 4);
            int tY = (height - (level - 1) * DensityUtils.dipTopx(mContext, 4)) / (level * 2) +
                    row * (height - (level - 1) * DensityUtils.dipTopx(mContext, 4)) / level +
                    row * DensityUtils.dipTopx(mContext, 4);
            int lX = column * (width - (level - 1) * DensityUtils.dipTopx(mContext, 4)) / level +
                    column * DensityUtils.dipTopx(mContext, 4);
            int lY = row * (height - (level - 1) * DensityUtils.dipTopx(mContext, 4)) / level +
                    row * DensityUtils.dipTopx(mContext, 4);
            int num = list.get(i);
            if (num != 0) {
                canvas.drawText(String.valueOf(num), tX, tY, mTextPaint);
            }
            if (column != 0) {
                canvas.drawRect(lX, lY, lX + DensityUtils.dipTopx(mContext, 4),
                        lY + (width - (level - 1) * DensityUtils.dipTopx(mContext, 4)) / level + column * DensityUtils.dipTopx(mContext, 4), mLinePaint);
            }
            if (row != 0) {
                canvas.drawRect(lX, lY, lX + (height - (level - 1) * DensityUtils.dipTopx(mContext, 4)) / level + row * DensityUtils.dipTopx(mContext, 4),
                        lY + DensityUtils.dipTopx(mContext, 4), mLinePaint);
            }
        }
    }

    private List<Integer> randomNum(List<Integer> numList) {
        List<Integer> temp = new ArrayList<>(numList);
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < numList.size(); i++) {
            int n = new Random().nextInt(temp.size());
            list.add(temp.get(n));
            temp.remove(n);
        }
        return list;
    }

    float x;
    float y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                direction(x, y, event.getX(), event.getY());
                break;
        }
        return true;
    }

    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int TOP = 2;
    private static final int DOWN = 3;

    private void direction(float startX, float startY, float endX, float endY) {
        float x = startX - endX;
        float y = startY - endY;
        int status;
        if (Math.abs(x) >= Math.abs(y) && x > 0) {
            status = LEFT;
        } else if (Math.abs(x) >= Math.abs(y) && x < 0) {
            status = RIGHT;
        } else if (Math.abs(x) < Math.abs(y) && y > 0) {
            status = TOP;
        } else if (Math.abs(x) < Math.abs(y) && y < 0) {
            status = DOWN;
        } else {
            return;
        }
        int position = 0;
        for (int i = 0; i < level * level; i++) {
            int row = i / level;
            int column = i % level;
            if (startX > (column * (width - (level - 1) * DensityUtils.dipTopx(mContext, 4)) / level +
                    column * DensityUtils.dipTopx(mContext, 4)) &&
                    startX < ((column + 1) * (width - (level - 1) * DensityUtils.dipTopx(mContext, 4)) / level +
                            column * DensityUtils.dipTopx(mContext, 4)) &&
                    startY > (row * (height - (level - 1) * DensityUtils.dipTopx(mContext, 4)) / level +
                            row * DensityUtils.dipTopx(mContext, 4)) &&
                    startY < ((row + 1) * (height - (level - 1) * DensityUtils.dipTopx(mContext, 4)) / level +
                            row * DensityUtils.dipTopx(mContext, 4))) {
                position = i;
                break;
            }
        }
        move(status, position);
    }

    private void move(int status, int position) {
        switch (status) {
            case LEFT:
                if (position % level > 0 && mNumList.get(position - 1) == 0) {
                    mNumList.set(position - 1, mNumList.get(position));
                    mNumList.set(position, 0);
                }
                break;
            case RIGHT:
                if (position % level < level - 1 && mNumList.get(position + 1) == 0) {
                    mNumList.set(position + 1, mNumList.get(position));
                    mNumList.set(position, 0);
                }
                break;
            case TOP:
                if (position / level > 0 && mNumList.get(position - level) == 0) {
                    mNumList.set(position - level, mNumList.get(position));
                    mNumList.set(position, 0);
                }
                break;
            case DOWN:
                if (position / level < level - 1 && mNumList.get(position + level) == 0) {
                    mNumList.set(position + level, mNumList.get(position));
                    mNumList.set(position, 0);
                }
                break;
        }
        invalidate();
        if (isEqual(mNumList, startList)) {
            Log.e("test", "成功");
        }
    }

    private boolean isEqual(List<Integer> fistList, List<Integer> secondList) {
        if (fistList == null || secondList == null || fistList.size() != secondList.size()) {
            return false;
        }
        boolean equal = true;
        for (int i = 0; i < fistList.size(); i++) {
            if (!fistList.get(i).equals(secondList.get(i))) {
                equal = false;
                break;
            }
        }
        return equal;
    }
}
