package cn.guluwa.rainyviewlib

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

/**
 * Created by guluwa on 2018/10/13.
 */
class RainyView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    private val DEFAULT_SIZE = 300 //the default size if set "wrap_content"

    private val DEFAULT_LEFT_CLOUD_COLOR = Color.parseColor("#B0B0B0")

    private val DEFAULT_RIGHT_CLOUD_COLOR = Color.parseColor("#B0B0B0")

    private var mLeftCloudColor = DEFAULT_LEFT_CLOUD_COLOR

    private var mRightCloudColor = DEFAULT_RIGHT_CLOUD_COLOR

    private var mCloudPaint: Paint? = null

    private var mLeftCloudPath: Path? = null

    private var mViewWidth = 0

    private var mViewHeight = 0

    init {
        parseAttrs(attrs)
    }

    private fun parseAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RainyView)
        mLeftCloudColor = typedArray.getColor(R.styleable.RainyView_left_cloud_color, DEFAULT_LEFT_CLOUD_COLOR)
        mRightCloudColor = typedArray.getColor(R.styleable.RainyView_right_cloud_color, DEFAULT_RIGHT_CLOUD_COLOR)
        typedArray.recycle()
    }

    private fun initViewMeasure() {
        mCloudPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mCloudPaint?.color = mLeftCloudColor
        mLeftCloudPath = Path()
        mLeftCloudPath?.addRoundRect(
            RectF(0f, mViewHeight * 2 / 3f, mViewWidth.toFloat(), mViewHeight.toFloat()),
            dip2px(context, 15f).toFloat(),
            dip2px(context, 15f).toFloat(), Path.Direction.CCW
        )
        mLeftCloudPath?.addCircle(
            mViewWidth / 3f,
            mViewHeight * 2 / 3f,
            mViewHeight / 5f, Path.Direction.CCW
        )
        mLeftCloudPath?.addCircle(
            mViewWidth * 3 / 5f,
            mViewHeight * 3 / 5f,
            mViewHeight * 4 / 15f, Path.Direction.CCW
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //确定width大小
        var size = MeasureSpec.getSize(widthMeasureSpec)
        var mode = MeasureSpec.getMode(widthMeasureSpec)
        mViewWidth = size
        if (mode == MeasureSpec.AT_MOST)
            mViewWidth = DEFAULT_SIZE
        //确定height大小
        size = MeasureSpec.getSize(heightMeasureSpec)
        mode = MeasureSpec.getMode(heightMeasureSpec)
        mViewHeight = size
        if (mode == MeasureSpec.AT_MOST)
            mViewHeight = DEFAULT_SIZE

        setMeasuredDimension(mViewWidth, mViewHeight)

        initViewMeasure()
    }

    override fun onDraw(canvas: Canvas?) {
        if (mLeftCloudPath != null && mCloudPaint != null)
            canvas?.drawPath(mLeftCloudPath, mCloudPaint)
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}
