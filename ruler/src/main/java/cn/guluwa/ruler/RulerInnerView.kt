package cn.guluwa.ruler

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.*
import android.widget.EdgeEffect
import android.widget.OverScroller


/**
 * Created by guluwa on 2018/10/24.
 */
class RulerInnerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    companion object {

        /**
         * 默认最小刻度
         */
        private const val DEFAULT_MIN_NUM = 30

        /**
         * 默认最大刻度
         */
        private const val DEFAULT_MAX_NUM = 150

        /**
         * 默认刻度间隔
         */
        private const val DEFAULT_SCALE_INTERVAL = 15f

        /**
         * 指针默认宽度
         */
        private const val DEFAULT_INDICATOR_STROKE_WIDTH = 7f

        /**
         * 小刻度默认宽度
         */
        private const val DEFAULT_SMALL_SCALE_STROKE_WIDTH = 2f

        /**
         * 大刻度默认宽度
         */
        private const val DEFAULT_BIG_SCALE_STROKE_WIDTH = 3f

        /**
         * 尺子默认高度
         */
        private const val DEFAULT_VIEW_HEIGHT = 125f

        /**
         * 刻度数字默认大小
         */
        private const val DEFAULT_SCALE_TEXT_SIZE = 16f
    }

    /**
     * 默认背景颜色
     */
    private val DEFAULT_RULER_BG_COLOR = Color.parseColor("#f8f8f8")

    /**
     * 默认刻度颜色
     */
    private val DEFAULT_RULER_SCALE_COLOR = Color.parseColor("#dfdfdf")

    /**
     * 默认刻度文字颜色
     */
    private val DEFAULT_RULER_SCALE_TEXT_COLOR = Color.parseColor("#333333")

    /**
     * 默认边缘颜色
     */
    private val DEFAULT_RULER_EDGE_COLOR = Color.parseColor("#62cc74")

    /**
     * 最小刻度
     */
    private var mMinNum = DEFAULT_MIN_NUM

    /**
     * 最大刻度
     */
    private var mMaxNum = DEFAULT_MAX_NUM

    /**
     * 刻度间隔
     */
    private var mRulerScaleInterval = Utils.dip2px(context, DEFAULT_SCALE_INTERVAL)

    /**
     * 大刻度宽度
     */
    private var mRulerBigScaleStrokeWidth = Utils.dip2px(context, DEFAULT_BIG_SCALE_STROKE_WIDTH)

    /**
     * 小刻度宽度
     */
    private var mRulerSmallScaleStrokeWidth = Utils.dip2px(context, DEFAULT_SMALL_SCALE_STROKE_WIDTH)

    /**
     * 指针宽度
     */
    private var mRulerIndicatorStrokeWidth = Utils.dip2px(context, DEFAULT_INDICATOR_STROKE_WIDTH)

    /**
     * 背景颜色
     */
    private var mRulerBgColor = DEFAULT_RULER_BG_COLOR

    /**
     * 刻度颜色
     */
    private var mRulerScaleColor = DEFAULT_RULER_SCALE_COLOR

    /**
     * 边缘颜色
     */
    private var mRulerEdgeColor = DEFAULT_RULER_EDGE_COLOR

    /**
     * 刻度文字颜色
     */
    private var mRulerScaleTextColor = DEFAULT_RULER_SCALE_TEXT_COLOR

    /**
     * 刻度文字大小
     */
    private var mRulerScaleTextSize = Utils.sp2px(context, DEFAULT_SCALE_TEXT_SIZE)

    /**
     * 尺子最小（左）位置
     */
    private var mRulerMinPosition = 0

    /**
     * 尺子最大（右）位置
     */
    private var mRulerMaxPosition = 0


    /**
     * 背景画笔
     */
    private var mRulerBgPaint: Paint? = null

    /**
     * 小刻度画笔
     */
    private var mSmallScalePaint: Paint? = null

    /**
     * 大刻度画笔
     */
    private var mBigScalePaint: Paint? = null

    /**
     * 刻度文字画笔
     */
    private var mScaleTextPaint: Paint? = null

    /**
     * 左边缘
     */
    private var mLeftEdgeEffect: EdgeEffect? = null

    /**
     * 右边缘
     */
    private var mRightEdgeEffect: EdgeEffect? = null

    /**
     * 边缘长度
     */
    private var mEdgeLength = 0f

    /**
     * 控制滑动
     */
    private var mOverScroller: OverScroller? = null

    /**
     * 速度获取
     */
    private var mVelocityTracker: VelocityTracker? = null

    /**
     * 惯性最大速度
     */
    private var mMaximumVelocity: Int = 0

    /**
     * 惯性最小速度
     */
    private var mMinimumVelocity: Int = 0

    /**
     * 控件宽度
     */
    private var mViewWidth = 0

    /**
     * 控件高度
     */
    private var mViewHeight = Utils.dip2px(context, DEFAULT_VIEW_HEIGHT)

    /**
     * 屏幕宽度
     */
    private var mWindowWidth = 0

    /**
     * 当前刻度值
     */
    private var mCurrentScale = mMinNum.toFloat() + 20

    /**
     * 刻度变化监听器
     */
    var listener: OnScaleChangeListener? = null

    /**
     * 初始化
     */
    init {
        parseAttr(attrs)
        checkAPILevel()
        initOverScroll()
        initEdgeEffect()
        initCurrentScale()
    }

    /**
     * 参数初始化
     */
    private fun parseAttr(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RulerInnerView)
        mMinNum = typedArray.getInteger(R.styleable.RulerInnerView_min_num, DEFAULT_MIN_NUM)
        mMaxNum = typedArray.getInteger(R.styleable.RulerInnerView_max_num, DEFAULT_MAX_NUM)
        typedArray.recycle()
    }

    /**
     * OverScroll初始化
     */
    private fun initOverScroll() {
        mOverScroller = OverScroller(context)
        mVelocityTracker = VelocityTracker.obtain()
        mMaximumVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity
        mMinimumVelocity = ViewConfiguration.get(context).scaledMinimumFlingVelocity
    }

    /**
     * 边缘初始化
     */
    private fun initEdgeEffect() {
        mLeftEdgeEffect = EdgeEffect(context)
        mRightEdgeEffect = EdgeEffect((context))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mLeftEdgeEffect?.color = mRulerEdgeColor
            mRightEdgeEffect?.color = mRulerEdgeColor
        }
        mEdgeLength = mRulerScaleInterval * 10f
    }

    /**
     * 设置起始刻度
     */
    private fun initCurrentScale() {
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                scrollTo(scaleToScrollX(), 0)
            }
        })
    }

    //API小于18则关闭硬件加速，否则setAntiAlias()方法不生效
    private fun checkAPILevel() {
        if (Build.VERSION.SDK_INT < 18) {
            setLayerType(View.LAYER_TYPE_NONE, null)
        }
    }

    /**
     * 设置view宽高
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //宽度
        var size = MeasureSpec.getSize(widthMeasureSpec)
        var mode = MeasureSpec.getMode(widthMeasureSpec)
        mViewWidth =
                if (mode == MeasureSpec.AT_MOST) ((mMaxNum - mMinNum) * 10 * mRulerScaleInterval) else size
        //高度
        size = MeasureSpec.getSize(heightMeasureSpec)
        mode = MeasureSpec.getMode(heightMeasureSpec)
        if (mode != MeasureSpec.AT_MOST) mViewHeight = size
        setMeasuredDimension(mViewWidth, mViewHeight)

        initPaint()
    }

    /**
     * 画笔初始化
     */
    private fun initPaint() {

        mRulerBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//        mRulerBgPaint?.color = mRulerBgColor
        mRulerBgPaint?.color = Color.RED
        mRulerBgPaint?.style = Paint.Style.FILL

        mSmallScalePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mSmallScalePaint?.color = mRulerScaleColor
        mSmallScalePaint?.strokeWidth = mRulerSmallScaleStrokeWidth.toFloat()

        mBigScalePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBigScalePaint?.color = mRulerScaleColor
        mBigScalePaint?.strokeWidth = mRulerBigScaleStrokeWidth.toFloat()

        mScaleTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mScaleTextPaint?.color = mRulerScaleTextColor
        mScaleTextPaint?.textSize = mRulerScaleTextSize.toFloat()
    }

    /**
     * 获取尺子最小、最大位置
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mWindowWidth = Utils.getDisplayMetrics(context).widthPixels
        mRulerMinPosition = (mRulerIndicatorStrokeWidth - mWindowWidth) / 2
        mRulerMaxPosition = mViewWidth + mRulerMinPosition
    }

    /**
     * 绘画尺子
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

//        canvas?.drawRect(0f, 0f, mViewWidth.toFloat(), mViewHeight.toFloat(), mRulerBgPaint)

        //尺子主体内容
        if (mSmallScalePaint != null && mBigScalePaint != null && mScaleTextPaint != null) {
            for (i in 0..mMaxNum - mMinNum) {
                for (j in 0 until 10) {
                    if (j == 0) {
                        canvas?.drawLine(
                            (10f * i + j) * mRulerScaleInterval,
                            0f,
                            (10f * i + j) * mRulerScaleInterval,
                            mViewHeight / 2f,
                            mBigScalePaint!!
                        )
                        canvas?.drawText(
                            "${i + mMinNum}",
                            (10f * i + j) * mRulerScaleInterval - mScaleTextPaint!!.measureText("${i + mMinNum}") / 2,
                            mViewHeight * 3 / 4f,
                            mScaleTextPaint!!
                        )
                    } else {
                        if (i != mMaxNum - mMinNum)
                            canvas?.drawLine(
                                (10f * i + j) * mRulerScaleInterval,
                                0f,
                                (10f * i + j) * mRulerScaleInterval,
                                mViewHeight / 4f,
                                mSmallScalePaint!!
                            )
                    }
                }
            }
        }

        //左边缘
        if (mLeftEdgeEffect != null) {
            if (!mLeftEdgeEffect!!.isFinished) {
                val count = canvas!!.save()
                canvas.rotate(-90f)
                canvas.translate(-(mViewHeight / 2f), 0f)
                if (mLeftEdgeEffect!!.draw(canvas)) {
                    postInvalidate()
                }
                canvas.restoreToCount(count)
            } else {
                mLeftEdgeEffect?.finish()
            }
        }

        //右边缘
        if (mRightEdgeEffect != null) {
            if (!mRightEdgeEffect!!.isFinished) {
                val count = canvas!!.save()
                canvas.rotate(90f)
                canvas.translate(0f, -width.toFloat())
                if (mRightEdgeEffect!!.draw(canvas)) {
                    postInvalidate()
                }
                canvas.restoreToCount(count)
            } else {
                mRightEdgeEffect?.finish()
            }
        }
    }

    private var mMoveStartX = 0f

    /**
     * 触摸事件
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val currentX = event!!.x
        //开始速度检测
        if (mVelocityTracker == null) mVelocityTracker = VelocityTracker.obtain()
        mVelocityTracker?.addMovement(event)

        if (mOverScroller == null) mOverScroller = OverScroller(context)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!mOverScroller!!.isFinished) {
                    mOverScroller?.abortAnimation()
                }
                mMoveStartX = currentX
            }
            MotionEvent.ACTION_MOVE -> {
                scrollBy((mMoveStartX - currentX).toInt(), 0)
                mMoveStartX = currentX
            }
            MotionEvent.ACTION_UP -> {
                mVelocityTracker?.computeCurrentVelocity(1000, mMaximumVelocity.toFloat())
                val xVelocity = mVelocityTracker!!.xVelocity
                if (Math.abs(xVelocity) > mMinimumVelocity) {//滑动
                    fling(-xVelocity.toInt())
                } else {//返回最近刻度
                    scrollMoreNearScale()
                }
                releaseVelocityTracker()
                releaseEdgeEffects()
            }
            MotionEvent.ACTION_CANCEL -> {
                if (!mOverScroller!!.isFinished) {
                    mOverScroller?.abortAnimation()
                }
                scrollMoreNearScale()
                releaseVelocityTracker()
                releaseEdgeEffects()
            }
        }
        return true
    }

    /**
     * 惯性滑动
     */
    private fun fling(vX: Int) {
        mOverScroller?.fling(scrollX, 0, vX, 0, mRulerMinPosition, mRulerMaxPosition, 0, 0)
        postInvalidate()
    }

    override fun computeScroll() {
        // 先推断mScroller滚动是否完毕
        if (mOverScroller != null && mOverScroller!!.computeScrollOffset()) {
            // 这里调用View的scrollTo()完毕实际的滚动
            scrollTo(mOverScroller!!.currX, mOverScroller!!.currY)
            if (!mOverScroller!!.computeScrollOffset()) {
                scrollMoreNearScale()
            }
            // 必须调用该方法，否则不一定能看到滚动效果
            postInvalidate()
        }
    }

    /**
     * 滚动完成后 回弹到最近的刻度
     */
    private fun scrollMoreNearScale() {
        //渐变回弹
        mOverScroller?.startScroll(scrollX, 0, scaleToScrollX() - scrollX, 0, 500)
        postInvalidate()
        //立刻回弹
//        scrollTo(scaleToScrollX(), 0)
    }

    /**
     * VelocityTracker回收
     */
    private fun releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker?.recycle()
            mVelocityTracker = null
        }
    }

    /**
     * 重写滑动
     * 小于最小刻度 不能滑动 出现左边缘效果
     * 大于最大刻度 不能滑动 出现右边缘效果
     * 滑动结束 通知result显示
     */
    override fun scrollTo(x: Int, y: Int) {
        val temp =
            when {
                x < mRulerMinPosition -> {
                    showLeftEdgeEffect(x)
                    mRulerMinPosition
                }
                x > mRulerMaxPosition -> {
                    showRightEdgeEffect(x)
                    mRulerMaxPosition
                }
                else -> x
            }
        if (temp != scrollX)
            super.scrollTo(temp, y)
        mCurrentScale = scrollXToScale()
        listener?.change(currentAccurateScale() + mMinNum)
    }

    /**
     * 显示左边缘效果
     */
    private fun showLeftEdgeEffect(x: Int) {
        if (mOverScroller != null && mLeftEdgeEffect != null) {
            if (!mOverScroller!!.isFinished) {
                mLeftEdgeEffect!!.onAbsorb(mOverScroller!!.currVelocity.toInt())
                mOverScroller!!.abortAnimation()
            } else {
                mLeftEdgeEffect!!.onPull((mRulerMinPosition - x) / mEdgeLength * 3 + 0.3f)
                mLeftEdgeEffect!!.setSize(mViewHeight / 2, width)
            }
            postInvalidateOnAnimation()
        }
    }

    /**
     * 显示右边缘效果
     */
    private fun showRightEdgeEffect(x: Int) {
        if (mOverScroller != null && mRightEdgeEffect != null) {
            if (!mOverScroller!!.isFinished) {
                mRightEdgeEffect!!.onAbsorb(mOverScroller!!.currVelocity.toInt())
                mOverScroller!!.abortAnimation()
            } else {
                mRightEdgeEffect!!.onPull((x - mRulerMaxPosition) / mEdgeLength * 3 + 0.3f)
                mRightEdgeEffect!!.setSize(mViewHeight / 2, width)
            }
        }
    }

    private fun releaseEdgeEffects() {
        mLeftEdgeEffect?.onRelease()
        mRightEdgeEffect?.onRelease()
    }

    /**
     * 滑动偏移距离转刻度
     */
    private fun scrollXToScale(): Float {
        val scale = (scrollX - mRulerMinPosition) / (mRulerScaleInterval * 10f)
        return Math.round(scale * 100) / 100f
    }

    /**
     * 刻度转滑动距离
     */
    private fun scaleToScrollX(): Int {
        val currentAccurateScale = currentAccurateScale()
        return (currentAccurateScale * mRulerScaleInterval * 10 + mRulerMinPosition).toInt()
    }

    /**
     * 准确刻度值
     */
    private fun currentAccurateScale(): Float {
        val temp = Math.round(mCurrentScale * 10) / 10f
        return when {
            temp < 0 -> 0f
            temp > mMaxNum - mMinNum -> (mMaxNum - mMinNum).toFloat()
            else -> temp
        }
    }
}