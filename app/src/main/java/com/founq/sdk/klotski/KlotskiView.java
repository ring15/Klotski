package com.founq.sdk.klotski;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
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

    //移动状态
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int TOP = 2;
    private static final int DOWN = 3;

    private Context mContext;
    private int width;
    private int height;

    private Paint mTextPaint;
    private Paint mLinePaint;

    //间隔
    private float lineHeight;

    //困难等级
    private int level = 3;

    //存放的数组
    private List<Integer> mNumList = new ArrayList<>();
    //初始数组
    private List<Integer> startList;

    public KlotskiView(Context context) {
        this(context, null);
    }

    public KlotskiView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KlotskiView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.KlotskiView, defStyleAttr, 0);
        int numColor = array.getColor(R.styleable.KlotskiView_numColor, Color.BLACK);
        float numSize = array.getDimension(R.styleable.KlotskiView_numSize, DensityUtils.dipTopx(context, 25));
        int background = array.getColor(R.styleable.KlotskiView_viewBackgroundColor, Color.WHITE);
        int lineColor = array.getColor(R.styleable.KlotskiView_lineColor, Color.RED);
        lineHeight = array.getDimension(R.styleable.KlotskiView_lineHeight, DensityUtils.dipTopx(context, 4));

        mContext = context;
        setBackgroundColor(background);

        mTextPaint = new Paint();
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(numSize);
        mTextPaint.setColor(numColor);

        mLinePaint = new Paint();
        mLinePaint.setColor(lineColor);

        //生成新数组
        createArray();
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

        //要一个正方形，所以，修改宽高到一致
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
        drawTextAndLine(mNumList, canvas);
    }

    //设置游戏难度，重新生成数组并重新绘制
    public void setGameLevel(int level) {
        this.level = level;
        createArray();
        invalidate();
    }

    //生成初始数组和随机数组
    private void createArray() {
        startList = new ArrayList<>();
        for (int i = 0; i < level * level; i++) {
            startList.add(i);
        }
        startList.add(startList.get(0));
        startList.remove(0);
        mNumList = randomNum(startList);
    }

    //获取随机数组
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

    //绘制文字和间隔线
    private void drawTextAndLine(List<Integer> list, Canvas canvas) {
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            //获取数组中第i个在华容道的位置
            int row = i / level;
            int column = i % level;
            //第row行，第column列对应的横纵坐标
            float blockWidth = (width - (level - 1) * lineHeight) / level;//每一个方块的宽度（总宽度-间隔对应的宽度）/方块个数
            float blockHeight = (height - (level - 1) * lineHeight) / level;//每一个方块的高度（总高度-间隔对应的宽度）/方块个数
            float tX = blockWidth / 2 + column * blockWidth + column * lineHeight;
            float tY = blockHeight / 2 + row * blockHeight + row * lineHeight;
            //获取间隔的位置
            float lX = column * blockWidth + column * lineHeight;
            float lY = row * blockHeight + row * lineHeight;
            int num = list.get(i);
            if (num != 0) {
                canvas.drawText(String.valueOf(num), tX, tY, mTextPaint);
            }
            if (column != 0) {
                canvas.drawRect(lX, lY, lX + lineHeight,
                        lY + blockWidth + column * lineHeight, mLinePaint);
            }
            if (row != 0) {
                canvas.drawRect(lX, lY, lX + blockHeight + row * lineHeight,
                        lY + lineHeight, mLinePaint);
            }
        }
    }

    //触摸事件按下时的坐标
    private float x;
    private float y;

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

    //通过坐标判断移动方向，并确定移动方块在数组中的位置
    private void direction(float startX, float startY, float endX, float endY) {
        //获取坐标差值
        float x = startX - endX;
        float y = startY - endY;
        int status;
        //若横坐标变化量>纵坐标变化量，则为左右移动，反之为上下移动；横坐标初始位置值比结束位置值大，为向左移动；纵坐标初始位置值比结束位置值大，为向上移动；
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
        int position = -1;
        for (int i = 0; i < level * level; i++) {
            int row = i / level;
            int column = i % level;
            float blockWidth = (width - (level - 1) * lineHeight) / level;//每一个方块的宽度（总宽度-间隔对应的宽度）/方块个数
            float blockHeight = (height - (level - 1) * lineHeight) / level;//每一个方块的高度（总高度-间隔对应的宽度）/方块个数
            //判断初始位置的横纵坐标在哪个区间
            if (startX > (column * blockWidth + column * lineHeight) &&
                    startX < ((column + 1) * blockWidth + column * lineHeight) &&
                    startY > (row * blockHeight + row * lineHeight) &&
                    startY < ((row + 1) * blockHeight + row * lineHeight)) {
                position = i;
                break;
            }
        }
        move(status, position);
    }

    //判断是否能移动，能移动则重新绘制
    private void move(int status, int position) {
        switch (status) {
            case LEFT:
                if (position % level > 0 && mNumList.get(position - 1) == 0) {
                    repaint(position - 1, position);
                }
                break;
            case RIGHT:
                if (position % level < level - 1 && mNumList.get(position + 1) == 0) {
                    repaint(position + 1, position);
                }
                break;
            case TOP:
                if (position / level > 0 && mNumList.get(position - level) == 0) {
                    repaint(position - level, position);
                }
                break;
            case DOWN:
                if (position / level < level - 1 && mNumList.get(position + level) == 0) {
                    repaint(position + level, position);
                }
                break;
        }
    }

    //更新数组，重新绘制，并判断游戏是否成功
    private void repaint(int zeroPosition, int position) {
        mNumList.set(zeroPosition, mNumList.get(position));
        mNumList.set(position, 0);
        invalidate();
        if (isEqual(mNumList, startList)) {
            Log.e("test", "成功");
        }
    }

    //判断两个数组是否完全相等
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
