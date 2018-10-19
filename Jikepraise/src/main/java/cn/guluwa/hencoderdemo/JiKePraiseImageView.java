package cn.guluwa.hencoderdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by 俊康 on 2017/11/6.
 */

public class JiKePraiseImageView extends View {

    //圆圈颜色
    private static final int START_COLOR = Color.parseColor("#00e24d3d");
    private static final int END_COLOR = Color.parseColor("#ffe24d3d");

    //缩放动画的时间
    private static final int SCALE_DURATION_SMALL = 150;
    private static final int SCALE_DURATION = 200;
    //圆圈扩散动画的时间
    private static final int RADIUS_DURATION = 200;

    private static final float SCALE_SMALLER = 0.8f;
    private static final float SCALE_SMALL = 0.86f;
    private static final float SCALE_NORMAL = 1f;
    private static final float SCALE_BIG = 1.12f;

    private Paint mBitmapPaint, mCirclePaint;
    private Bitmap mFalseBitmap, mTrueBitmap, mShiningBitmap;
    private YPoint mBitmapPoint, mShiningPoint, mCirclePoint;
    private float mFalseWidth, mFalseHeight, mTrueWidth, mTrueHeight, mShiningWidth, mShiningHeight;
    private float mMaxRadius, mMinRadius, mRadius;
    private Path mCircleClipPath;
    private int mWidth, mHeight;
    private boolean isTrue;
    private long mLastStartTime;
    private int mClickCount, mEndCount;
    private AnimatorSet mTrueAnimatorSet;
    private PraiseListener listener;

    public JiKePraiseImageView(Context context) {
        this(context, null);
    }

    public JiKePraiseImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JiKePraiseImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    private void initView() {
        initBitmap();
        initCircle();
    }

    private void initBitmap() {
        //初始化paint
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);

        //初始化图片bitmap
        mFalseBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_messages_like_unselected);
        mTrueBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_messages_like_selected);
        mShiningBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_messages_like_selected_shining);

        mFalseWidth = mFalseBitmap.getWidth();
        mFalseHeight = mFalseBitmap.getHeight();
        mTrueWidth = mTrueBitmap.getWidth();
        mTrueHeight = mTrueBitmap.getHeight();
        mShiningWidth = mShiningBitmap.getWidth();
        mShiningHeight = mShiningBitmap.getHeight();

        //计算图片绘制坐标
        mBitmapPoint = new YPoint();
        mShiningPoint = new YPoint();

        mShiningPoint.setX(getPaddingLeft() + Utils.dip2px(getContext(), 2));
        mShiningPoint.setY(getPaddingTop());
        mBitmapPoint.setX(getPaddingLeft());
        mBitmapPoint.setY(getPaddingTop() + Utils.dip2px(getContext(), 8));
    }

    private void initCircle() {
        //初始化paint
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(Utils.dip2px(getContext(), 2));

        //初始化圆坐标
        mCirclePoint = new YPoint();
        mCirclePoint.setX(mBitmapPoint.getX() + mFalseWidth / 2);
        mCirclePoint.setY(mBitmapPoint.getY() + mFalseHeight / 2);

        //计算圆最大最小半径
        mMaxRadius = Math.max(mCirclePoint.getX() - getPaddingLeft(), mCirclePoint.getY() - getPaddingTop());
        mMinRadius = Utils.dip2px(getContext(), 8);

        //初始化mCircleClipPath
        mCircleClipPath = new Path();
        mCircleClipPath.addCircle(mCirclePoint.getX(), mCirclePoint.getY(), mMaxRadius, Path.Direction.CW);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size, mode;
        size = MeasureSpec.getSize(widthMeasureSpec);
        mode = MeasureSpec.getMode(widthMeasureSpec);

        if (mode == MeasureSpec.EXACTLY) {//match_parent或准确值
            mWidth = size;
        } else {//比较最左边，最右边，得到最大半径
            float left = Math.min(mShiningPoint.getX(), mBitmapPoint.getX());
            float right = Math.max(mShiningPoint.getX() + mShiningWidth, mBitmapPoint.getX() + mTrueWidth);
            mWidth = (int) (right - left);
        }

        size = MeasureSpec.getSize(heightMeasureSpec);
        mode = MeasureSpec.getMode(heightMeasureSpec);

        if (mode == MeasureSpec.EXACTLY) {
            mHeight = size;
        } else {//比较最高点，最低点，得到最大半径
            float top = Math.min(mShiningPoint.getY(), mBitmapPoint.getY());
            float bottom = Math.max(mShiningPoint.getY() + mShiningHeight, mBitmapPoint.getY() + mTrueHeight);
            mHeight = (int) (bottom - top);
        }

        setMeasuredDimension(
                mWidth + getPaddingLeft() + getPaddingRight(),
                mHeight + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isTrue) {
            canvas.drawBitmap(mTrueBitmap, mBitmapPoint.getX(), mBitmapPoint.getY(), mBitmapPaint);
            if (mCircleClipPath != null) {
                canvas.save();
                canvas.clipPath(mCircleClipPath);
                canvas.drawBitmap(mShiningBitmap, mShiningPoint.getX(), mShiningPoint.getY(), mBitmapPaint);
                canvas.restore();
                canvas.drawCircle(mCirclePoint.getX(), mCirclePoint.getY(),
                        mRadius - Utils.dip2px(getContext(), 1), mCirclePaint);
            }
        } else {
            canvas.drawBitmap(mFalseBitmap, mBitmapPoint.getX(), mBitmapPoint.getY(), mBitmapPaint);
        }
    }

    //动画
    public void startAnimation() {
        mClickCount++;
        boolean isFastClick = false;
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - mLastStartTime < 300) {
            isFastClick = true;
        }
        mLastStartTime = currentTimeMillis;
        if (isTrue) {
            if (isFastClick) {
                startFastClickAnimation();
                return;
            }
            startFalseAnimation();
            mClickCount = 0;//0表示没点赞
        } else {
            if (mTrueAnimatorSet != null) {
                mClickCount = 0;//0表示没点赞
            } else {
                startTrueAnimation();
                mClickCount = 1;//1表示点赞
            }
        }
        mEndCount = mClickCount;
    }

    private void startTrueAnimation() {
        //图片灰色先变小，再变大，变小过程中上面散开的点出现
        ObjectAnimator falseScaleAnimator = ObjectAnimator.ofFloat(this, "scaleFalseBitmap",
                SCALE_NORMAL, SCALE_SMALLER);
        falseScaleAnimator.setDuration(SCALE_DURATION_SMALL);
        falseScaleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isTrue = true;
            }
        });

        //圆圈动画
        ObjectAnimator circleAnimator = ObjectAnimator.ofFloat(this, "radius",
                mMinRadius, mMaxRadius);
        circleAnimator.setDuration(RADIUS_DURATION);
        circleAnimator.setInterpolator(new LinearInterpolator());

        //彩色图片变大
        ObjectAnimator trueScaleAnimator = ObjectAnimator.ofFloat(this, "scaleTrueBitmap",
                SCALE_SMALLER, SCALE_BIG, SCALE_NORMAL);
        trueScaleAnimator.setDuration(SCALE_DURATION);
        trueScaleAnimator.setInterpolator(new AccelerateInterpolator());
        mTrueAnimatorSet = new AnimatorSet();
        mTrueAnimatorSet.play(trueScaleAnimator).with(circleAnimator).after(falseScaleAnimator);
        mTrueAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mTrueAnimatorSet = null;
                if (listener != null)
                    listener.praiseFinish();
            }
        });
        mTrueAnimatorSet.start();
    }

    private void startFalseAnimation() {
        //灰色图片从小变大
        isTrue = false;
        ObjectAnimator falseScaleAnimator = ObjectAnimator.ofFloat(this, "scaleFalseBitmap",
                SCALE_SMALLER, SCALE_NORMAL);
        falseScaleAnimator.setDuration(SCALE_DURATION_SMALL);
        falseScaleAnimator.setInterpolator(new AccelerateInterpolator());
        falseScaleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setScaleFalseBitmap(SCALE_NORMAL);
                if (listener != null)
                    listener.cancelFinish();
            }
        });
        falseScaleAnimator.start();
    }

    private void startFastClickAnimation() {
        //图片变小
        ObjectAnimator scaleAnimator = ObjectAnimator.ofFloat(this, "scaleTrueBitmap",
                SCALE_SMALL, SCALE_NORMAL);
        scaleAnimator.setDuration(SCALE_DURATION);
        scaleAnimator.setInterpolator(new OvershootInterpolator());

        ObjectAnimator circleAnimator = ObjectAnimator.ofFloat(this, "radius",
                mMinRadius, mMaxRadius);
        circleAnimator.setDuration(RADIUS_DURATION);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleAnimator).with(circleAnimator);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mEndCount++;
                if (mEndCount != mClickCount)
                    return;
                if (mEndCount % 2 == 0) {
                    startFalseAnimation();
                } else {
                    if (listener != null)
                        listener.praiseFinish();
                }
            }
        });
        animatorSet.start();
    }

    private void setScaleTrueBitmap(float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        mTrueBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_messages_like_selected);
        mTrueBitmap = Bitmap.createBitmap(mTrueBitmap, 0, 0,
                mTrueBitmap.getWidth(), mTrueBitmap.getHeight(), matrix, true);
        postInvalidate();
    }

    private void setScaleFalseBitmap(float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        mFalseBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_messages_like_unselected);
        mFalseBitmap = Bitmap.createBitmap(mFalseBitmap, 0, 0,
                mFalseBitmap.getWidth(), mFalseBitmap.getHeight(), matrix, true);
        postInvalidate();
    }

    private void setScaleShiningBitmap(float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        mShiningBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_messages_like_selected_shining);
        mShiningBitmap = Bitmap.createBitmap(mShiningBitmap, 0, 0,
                mShiningBitmap.getWidth(), mShiningBitmap.getHeight(), matrix, true);
        postInvalidate();
    }

    private void setRadius(float radius) {
        mRadius = radius;
        mCircleClipPath = new Path();
        mCircleClipPath.addCircle(mCirclePoint.getX(), mCirclePoint.getY(), radius, Path.Direction.CW);
        float fraction = (mMaxRadius - radius) / (mMaxRadius - mMinRadius);
        mCirclePaint.setColor((int) Utils.evaluate(fraction, START_COLOR, END_COLOR));
        postInvalidate();
    }

    public void setTrue(boolean aTrue) {
        isTrue = aTrue;
        mClickCount = isTrue ? 1 : 0;
        mEndCount = mClickCount;
        postInvalidate();
    }

    public YPoint getmCirclePoint() {
        return mCirclePoint;
    }

    public void setListener(PraiseListener listener) {
        this.listener = listener;
    }

    public interface PraiseListener {
        void praiseFinish();

        void cancelFinish();
    }
}
