package cn.guluwa.hencoderdemo;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 俊康 on 2017/10/28.
 */

public class JiKePraiseNumView extends View {

    private Paint paint;
    private int num;
    private int mWidth, mHeight;
    private int mTextSize;
    private String[] mStrArray;
    private Point[] mPointArray;
    private float mMaxOffsetY, mMinOffsetY, mOldOffsetY, mNewOffsetY, mFraction;
    private boolean isAdd;
    private int mTextColor, mEndTextColor;

    public JiKePraiseNumView(Context context) {
        this(context, null);
    }

    public JiKePraiseNumView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JiKePraiseNumView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        initData();
        initPaint();
    }

    private void initData() {
        mStrArray = new String[3];
        mPointArray = new Point[3];
        mPointArray[0] = new Point();
        mPointArray[1] = new Point();
        mPointArray[2] = new Point();
        mTextSize = 15;
        mMinOffsetY = 0;
        mMaxOffsetY = Utils.sp2px(getContext(), mTextSize);
        mTextColor = Color.parseColor("#cccccc");
        mEndTextColor = Color.argb(0, Color.red(mTextColor), Color.green(mTextColor), Color.blue(mTextColor));
        calculateNumChange(0);
    }

    private void initPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(Utils.sp2px(getContext(), mTextSize));
        paint.setColor(mTextColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size, mode;
        size = MeasureSpec.getSize(widthMeasureSpec);
        mode = MeasureSpec.getMode(widthMeasureSpec);

        Rect textBounds = new Rect();
        paint.getTextBounds(String.valueOf(num), 0, String.valueOf(num).length(), textBounds);

        if (mode == MeasureSpec.EXACTLY) {//match_parent或准确值
            mWidth = size;
        } else {
            mWidth = (int) paint.measureText(String.valueOf(num));
        }

        size = MeasureSpec.getSize(heightMeasureSpec);
        mode = MeasureSpec.getMode(heightMeasureSpec);

        if (mode == MeasureSpec.EXACTLY) {
            mHeight = size;
        } else {
            mHeight = textBounds.bottom - textBounds.top;
        }

        setMeasuredDimension(
                mWidth + getPaddingLeft() + getPaddingRight(),
                mHeight + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        calculatePositionChange();
    }

    public void setNum(int num) {
        this.num = num;
        calculateNumChange(0);
        requestLayout();
    }

    private void calculatePositionChange() {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float y = getPaddingTop() + (mHeight - fontMetrics.top - fontMetrics.bottom) / 2;

        mPointArray[0].x = getPaddingLeft();
        mPointArray[0].y = (int) y;
        mPointArray[1].x = mPointArray[2].x = (int) (getPaddingLeft() + mStrArray[0].length() * paint.measureText(String.valueOf(num)) / String.valueOf(num).length());
        mPointArray[1].y = (int) (y - mOldOffsetY);
        mPointArray[2].y = (int) (y - mNewOffsetY);
    }

    public void calculateNumChange(int type) {// -1,0,1
        if (type == 0) {
            mStrArray[0] = String.valueOf(num);
            mStrArray[1] = "";
            mStrArray[2] = "";
            return;
        }

        String oldStr = String.valueOf(num);
        String newStr = String.valueOf(num + type);

        int i;
        for (i = 0; i < oldStr.length(); i++) {
            char oldc = oldStr.charAt(i);
            char newc = newStr.charAt(i);
            if (oldc != newc) {
                break;
            }
        }
        mStrArray[0] = i == 0 ? "" : String.valueOf(num).substring(0, i);
        mStrArray[1] = String.valueOf(num).substring(i);
        num += type;
        mStrArray[2] = String.valueOf(num).substring(i);
        startAnimation(type == 1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //不变的部分
        paint.setColor(mTextColor);
        canvas.drawText(mStrArray[0], mPointArray[0].x, mPointArray[0].y, paint);

        //变化前的部分
        paint.setColor((Integer) Utils.evaluate(mFraction, mEndTextColor, mTextColor));
        canvas.drawText(mStrArray[1], mPointArray[1].x, mPointArray[1].y, paint);

        //变化后的部分
        paint.setColor((Integer) Utils.evaluate(mFraction, mTextColor, mEndTextColor));
        canvas.drawText(mStrArray[2], mPointArray[2].x, mPointArray[2].y, paint);
    }

    private void startAnimation(boolean isAdd) {
        this.isAdd = isAdd;
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "textOffsetY", 0, isAdd ? mMaxOffsetY : -mMaxOffsetY);
        animator.start();
    }

    public void setTextOffsetY(float offsetY) {
        this.mOldOffsetY = offsetY;//变大是从[0,1]，变小是[0,-1]
        if (isAdd) {//从下到上[-1,0]
            this.mNewOffsetY = offsetY - mMaxOffsetY;
        } else {//从上到下[1,0]
            this.mNewOffsetY = mMaxOffsetY + offsetY;
        }
        mFraction = (mMaxOffsetY - Math.abs(mOldOffsetY)) / (mMaxOffsetY - mMinOffsetY);
        calculatePositionChange();
        postInvalidate();
    }

    public float getTextOffsetY() {
        return mMinOffsetY;
    }
}
