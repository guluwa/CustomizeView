package com.hencoder.hencoderpracticedraw1.practice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.hencoder.hencoderpracticedraw1.R;

import java.util.ArrayList;
import java.util.List;

public class Practice11PieChartView extends View {

    private int mWidth, mHeight;
    private Paint mPaint;
    private Path mPath;
    private List<PieData> list;

    public Practice11PieChartView(Context context) {
        this(context, null);
    }

    public Practice11PieChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Practice11PieChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        list.add(new PieData(120, "Lollipop", R.color.color1));
        list.add(new PieData(35, "Marshmallow", R.color.color2));
        list.add(new PieData(5, "Froyo", R.color.color3));
        list.add(new PieData(15, "Gingerbread", R.color.color4));
        list.add(new PieData(10, "Ice Cream Sandwich", R.color.color5));
        list.add(new PieData(60, "Jelly Bean", R.color.color6));
        list.add(new PieData(115, "KitKat", R.color.color7));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        综合练习
//        练习内容：使用各种 Canvas.drawXXX() 方法画饼图

        canvas.translate(mWidth / 2, mHeight / 2);
        float angle = 180;
        for (int i = 0; i < list.size(); i++) {
            RectF rectF;
            if (i == 0) {
                rectF = new RectF(-275, -275, 225, 225);
            } else {
                rectF = new RectF(-250, -250, 250, 250);
            }
            mPaint.setColor(getResources().getColor(list.get(i).getColor()));
            canvas.drawArc(rectF, angle, list.get(i).getNum(), true, mPaint);
            angle += list.get(i).getNum();
            mPaint.setColor(getResources().getColor(R.color.color9));
            float lineAngle = (float) (angle - list.get(i).getNum() / 2.0);
            float sin = (float) Math.abs(Math.sin((lineAngle % 180) * Math.PI / 180));
            float cos = (float) Math.abs(Math.cos((lineAngle % 180) * Math.PI / 180));
            float mStartx, mStarty, mEndx, mEndy;
            mStartx = 250 * cos + (i == 0 ? 25 : 0);
            mStarty = 250 * sin + (i == 0 ? 25 : 0);
            mEndx = 280 * cos + (i == 0 ? 25 : 0);
            mEndy = 280 * sin + (i == 0 ? 25 : 0);
            if (lineAngle % 360 >= 270 && lineAngle % 360 < 360) {
                mStarty = -mStarty;
                mEndy = -mEndy;
            } else if (lineAngle % 360 >= 180 && lineAngle % 360 < 270) {
                mStartx = -mStartx;
                mStarty = -mStarty;
                mEndx = -mEndx;
                mEndy = -mEndy;
            } else if (lineAngle % 360 >= 90 && lineAngle % 360 < 180) {
                mStartx = -mStartx;
                mEndx = -mEndx;
            }
            canvas.drawLine(mStartx, mStarty, mEndx, mEndy, mPaint);
            if (mEndx < 0) {
                canvas.drawLine(mEndx, mEndy, mEndx - 50, mEndy, mPaint);
                mPaint.setColor(Color.WHITE);
                mPaint.setTextSize(20);
                canvas.drawText(list.get(i).getName(),
                        mEndx - 60 - list.get(i).getName().length() * 10,
                        mEndy,
                        mPaint);
            } else {
                canvas.drawLine(mEndx, mEndy, mEndx + 50, mEndy, mPaint);
                mPaint.setColor(Color.WHITE);
                mPaint.setTextSize(20);
                canvas.drawText(list.get(i).getName(),
                        mEndx + 60,
                        mEndy,
                        mPaint);
            }
            mPaint.setColor(Color.WHITE);
            mPaint.setTextSize(30);
            canvas.drawText("饼图", -30, 300, mPaint);
        }
    }

    private class PieData {
        private int num;
        private String name;
        @ColorRes
        int color;

        public PieData(int num, String name, @ColorRes int color) {
            this.num = num;
            this.name = name;
            this.color = color;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
