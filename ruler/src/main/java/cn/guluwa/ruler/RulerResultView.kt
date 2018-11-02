package cn.guluwa.ruler

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View


/**
 * Created by guluwa on 2018/10/25.
 */
class RulerResultView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr), OnScaleChangeListener {

    companion object {

        /**
         * 默认结果数字文字大小
         */
        private const val DEFAULT_RESULT_TEXT_SIZE = 45f

        /**
         * 默认结果单位文字大小
         */
        private const val DEFAULT_RESULT_UNIT_TEXT_SIZE = 15f

        /**
         * 默认View高度
         */
        private const val DEFAULT_VIEW_HEIGHT = 75f
    }

    /**
     * 默认View背景颜色
     */
    private val DEFAULT_BG_COLOR = Color.parseColor("#ffffff")

    /**
     * 默认结果数字文字颜色
     */
    private val DEFALUT_RULER_RESULT_TEXT_COLOR = Color.parseColor("#62cc74")

    /**
     * View背景颜色
     */
    private var mBgColor = DEFAULT_BG_COLOR

    /**
     * 结果数字文字颜色
     */
    private var mRulerResultTextColor = DEFALUT_RULER_RESULT_TEXT_COLOR

    /**
     * 结果数字画笔
     */
    private var mResultTextPaint: Paint? = null

    /**
     * 结果单位画笔
     */
    private var mResultUnitTextPaint: Paint? = null

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
    private var mCurrentScale = 0f

    /**
     * 刻度y坐标
     */
    private var scaleYPosition = 0f

    /**
     * 单位y坐标
     */
    private var scaleUnitYPosition = 0f

    /**
     * 初始化
     */
    init {
        parseAttr(attrs)
    }

    /**
     * 参数初始化
     */
    private fun parseAttr(attrs: AttributeSet?) {

    }

    /**
     * 设置view宽高
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mWindowWidth = Utils.getDisplayMetrics(context).widthPixels
        //宽度
        var size = MeasureSpec.getSize(widthMeasureSpec)
        var mode = MeasureSpec.getMode(widthMeasureSpec)
        mViewWidth = if (mode == MeasureSpec.AT_MOST) mWindowWidth else size
        //高度
        size = MeasureSpec.getSize(heightMeasureSpec)
        mode = MeasureSpec.getMode(heightMeasureSpec)
        if (mode != MeasureSpec.AT_MOST) mViewHeight = size

        setMeasuredDimension(mViewWidth, mViewHeight)

        initPaint()
        initTextYPosition()
    }

    /**
     * 画笔初始化
     */
    private fun initPaint() {

        mResultTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mResultTextPaint?.color = mRulerResultTextColor
        mResultTextPaint?.textSize = Utils.sp2px(context, DEFAULT_RESULT_TEXT_SIZE).toFloat()

        mResultUnitTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mResultUnitTextPaint?.color = mRulerResultTextColor
        mResultUnitTextPaint?.textSize = Utils.sp2px(context, DEFAULT_RESULT_UNIT_TEXT_SIZE).toFloat()
    }

    /**
     * 计算文字y坐标
     */
    private fun initTextYPosition() {
        //计算刻度文字Y坐标
        if (mResultTextPaint != null) {
            val fontMetrics = mResultTextPaint!!.fontMetrics
            scaleYPosition = paddingTop + (mViewHeight.toFloat() - fontMetrics.top - fontMetrics.bottom) / 2
        }
        //计算单位文字Y坐标
        if (mResultUnitTextPaint != null) {
            val fontMetrics = mResultUnitTextPaint!!.fontMetrics
            scaleUnitYPosition = paddingTop + (mViewHeight.toFloat() - fontMetrics.top - fontMetrics.bottom) / 3
        }
    }

    /**
     * 改变刻度
     */
    override fun change(scale: Float) {
        mCurrentScale = scale
        invalidate()
    }

    /**
     * 绘制View
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //背景
        canvas?.drawColor(mBgColor)

        //刻度值
        if (mResultTextPaint != null) {
            canvas?.drawText(
                "$mCurrentScale",
                (mWindowWidth - mResultTextPaint!!.measureText("$mCurrentScale")) / 2,
                scaleYPosition,
                mResultTextPaint!!
            )
        }

        //刻度单位
        if (mResultTextPaint != null && mResultUnitTextPaint != null) {
            canvas?.drawText(
                "kg",
                (mWindowWidth +
                        mResultTextPaint!!.measureText("$mCurrentScale") +
                        mResultUnitTextPaint!!.measureText("kg")) / 2,
                scaleUnitYPosition,
                mResultUnitTextPaint!!
            )
        }
    }
}