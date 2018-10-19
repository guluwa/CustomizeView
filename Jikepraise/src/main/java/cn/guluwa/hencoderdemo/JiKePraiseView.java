package cn.guluwa.hencoderdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by 俊康 on 2017/11/6.
 */

public class JiKePraiseView extends LinearLayout implements View.OnClickListener {

    private JiKePraiseImageView mJiKePraiseImageView;
    private JiKePraiseNumView mJiKePraiseNumView;
    private boolean isTrue;
    private int mTopMargin;
    private int num;
    private JiKePraiseImageView.PraiseListener listener;

    public JiKePraiseView(Context context) {
        this(context, null);
    }

    public JiKePraiseView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JiKePraiseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JiKePraiseView);
        isTrue = typedArray.getBoolean(R.styleable.JiKePraiseView_istrue, false);
        num = typedArray.getInt(R.styleable.JiKePraiseView_num, 0);
        initView();
    }

    private void initView() {
        removeAllViews();
        setClipChildren(false);
        setOrientation(HORIZONTAL);
        setOnClickListener(this);
        addImageView();
        addNumView();
    }

    private void addImageView() {
        mJiKePraiseImageView = new JiKePraiseImageView(getContext());
        mJiKePraiseImageView.setTrue(isTrue);
        YPoint yPoint = mJiKePraiseImageView.getmCirclePoint();
        mTopMargin = (int) (yPoint.getY() - Utils.sp2px(getContext(), 15) / 2);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (mTopMargin < 0) {
            layoutParams.topMargin = mTopMargin;
        }
        layoutParams.leftMargin = getPaddingLeft();
        layoutParams.topMargin += getPaddingTop();
        layoutParams.bottomMargin = getPaddingBottom();
        addView(mJiKePraiseImageView, layoutParams);
    }

    private void addNumView() {
        mJiKePraiseNumView = new JiKePraiseNumView(getContext());
        mJiKePraiseNumView.setNum(num);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (mTopMargin > 0) {
            layoutParams.topMargin = mTopMargin;
        }
        layoutParams.leftMargin = Utils.dip2px(getContext(), 5);
        layoutParams.topMargin += getPaddingTop() + Utils.dip2px(getContext(), 2);
        layoutParams.rightMargin = getPaddingRight();
        layoutParams.bottomMargin = getPaddingBottom();
        addView(mJiKePraiseNumView, layoutParams);
    }

    @Override
    public void onClick(View v) {
        isTrue = !isTrue;
        if (isTrue) {
            mJiKePraiseNumView.calculateNumChange(1);
        } else {
            mJiKePraiseNumView.calculateNumChange(-1);
        }
        mJiKePraiseImageView.startAnimation();
    }

    public JiKePraiseView setTrue(boolean aTrue) {
        isTrue = aTrue;
        mJiKePraiseImageView.setTrue(isTrue);
        return this;
    }

    public JiKePraiseView setNum(int num) {
        this.num = num;
        mJiKePraiseNumView.setNum(num);
        return this;
    }

    public void setListener(JiKePraiseImageView.PraiseListener listener) {
        this.listener = listener;
        mJiKePraiseImageView.setListener(listener);
    }
}
