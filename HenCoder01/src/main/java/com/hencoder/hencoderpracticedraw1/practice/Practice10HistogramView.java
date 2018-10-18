package com.hencoder.hencoderpracticedraw1.practice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.hencoder.hencoderpracticedraw1.R;

import java.util.ArrayList;
import java.util.List;

public class Practice10HistogramView extends View {

    private int mWidth, mHeight;
    private Paint mPaint;
    private Path mPath;
    private List<HistogramData> list;

    public Practice10HistogramView(Context context) {
        this(context, null);
    }

    public Practice10HistogramView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Practice10HistogramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        initPath();
        initData();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(2);
        mPaint.setAntiAlias(true);
    }

    private void initPath() {

    }

    private void initData() {
        list = new ArrayList<>();
        list.add(new HistogramData("Froyo", 5, 170));
        list.add(new HistogramData("GB", 30, 290));
        list.add(new HistogramData("ICS", 30, 410));
        list.add(new HistogramData("JB", 230, 530));
        list.add(new HistogramData("KitKat", 400, 650));
        list.add(new HistogramData("L", 450, 770));
        list.add(new HistogramData("M", 200, 890));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        综合练习
//        练习内容：使用各种 Canvas.drawXXX() 方法画直方图
        canvas.drawLine(100, 100, 100, 600, mPaint);
        canvas.drawLine(100, 600, 1000, 600, mPaint);
        mPaint.setTextSize(45);
        mPaint.setColor(Color.WHITE);
        canvas.drawText("我的直方图", mWidth / 2 - 112, 700, mPaint);
        for (int i = 0; i < list.size(); i++) {
            mPaint.setColor(Color.WHITE);
            mPaint.setTextSize(30);
            canvas.drawText(
                    list.get(i).getName(),
                    list.get(i).getX() - (list.get(i).getName().length() * 30 / 2 > 50 ? 50 : list.get(i).getName().length() * 30 / 2),
                    650,
                    mPaint);
            Rect rect = new Rect(
                    list.get(i).getX() - 50,
                    600 - list.get(i).getNum(),
                    list.get(i).getX() + 50,
                    598);
            mPaint.setColor(getResources().getColor(R.color.color8));
            canvas.drawRect(rect, mPaint);
        }
    }

    private class HistogramData {

        String name;
        int num;
        int x;

        public HistogramData(String name, int num, int x) {
            this.name = name;
            this.num = num;
            this.x = x;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }
    }
}
