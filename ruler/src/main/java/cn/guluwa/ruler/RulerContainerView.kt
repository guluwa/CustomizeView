package cn.guluwa.ruler

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.ViewGroup

/**
 * Created by guluwa on 2018/10/25.
 */
class RulerContainerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    ViewGroup(context, attrs, defStyleAttr) {

    companion object {

        /**
         * 指针默认宽度
         */
        private const val DEFAULT_INDICATOR_STROKE_WIDTH = 7f
    }

    /**
     * 指针默认颜色
     */
    private val DEFAULT_RULER_INDICATOR_COLOR = Color.parseColor("#62cc74")

    /**
     * 指针颜色
     */
    private var mRulerIndicatorColor = DEFAULT_RULER_INDICATOR_COLOR

    /**
     * 指针画笔
     */
    private var mIndicatorPaint: Paint? = null

    /**
     * 屏幕宽度
     */
    private var mWindowWidth = 0

    /**
     * 内部尺子对象
     */
    var mInnerRuler: RulerInnerView? = null

    /**
     * 控件高度
     */
    private var mViewHeight = 0

    /**
     * 指针宽度
     */
    private var mRulerIndicatorStrokeWidth = Utils.dip2px(context, DEFAULT_INDICATOR_STROKE_WIDTH)

    init {
        initInnerRuler()
    }

    /**
     * View初始化
     */
    private fun initInnerRuler() {
        mInnerRuler = RulerInnerView(context)
        //设置全屏，加入InnerRuler
        mInnerRuler?.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        addView(mInnerRuler)
        //设置ViewGroup可画
        setWillNotDraw(false)
        initPaint()
    }

    /**
     * 初始化画笔
     */
    private fun initPaint() {
        mIndicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mIndicatorPaint?.color = mRulerIndicatorColor
        mIndicatorPaint?.strokeWidth = mRulerIndicatorStrokeWidth.toFloat()
        mIndicatorPaint?.strokeCap = Paint.Cap.ROUND
        mWindowWidth = Utils.getDisplayMetrics(context).widthPixels
    }

    /**
     * 测量子view高宽 给view设置宽高
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mInnerRuler != null) {
            measureChild(mInnerRuler, widthMeasureSpec, heightMeasureSpec)
            setMeasuredDimension(mInnerRuler!!.measuredWidth, mInnerRuler!!.measuredHeight)
        } else
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }

    /**
     * 获取view高度
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mViewHeight = h
    }

    /**
     * 在dispatchDraw之后画指针
     */
    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        if (mIndicatorPaint != null)
            canvas?.drawLine(
                (mWindowWidth - mRulerIndicatorStrokeWidth) / 2f,
                0f,
                (mWindowWidth - mRulerIndicatorStrokeWidth) / 2f,
                mViewHeight / 2f,
                mIndicatorPaint!!
            )
    }

    /**
     * 子View排列
     */
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (changed)
            mInnerRuler?.layout(0, 0, r - l, b - t)
    }
}