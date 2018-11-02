package cn.guluwa.rainyviewlib

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import java.util.*


/**
 * Created by guluwa on 2018/10/13.
 */
class RainyView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    companion object {
        /**
         * 默认大小
         */
        private const val DEFAULT_SIZE = 300

        /**
         * 默认同时可以存在的最大雨滴数量
         */
        private const val DEFAULT_DROP_MAX_NUMBER = 30

        /**
         * 雨滴默认创建时间间隔（毫秒）
         */
        private const val DEFAULT_DROP_CREATION_INTERVAL = 50

        /**
         * 雨滴默认最大长度
         */
        private const val DEFAULT_DROP_MAX_LENGTH = 50

        /**
         * 雨滴默认最小长度
         */
        private const val DEFAULT_DROP_MIN_LENGTH = 10

        /**
         * 雨滴默认宽度
         */
        private const val DEFAULT_DROP_SIZE = 15

        /**
         * 雨滴默认下落最大速度
         */
        private const val DEFAULT_DROP_MAX_SPEECH = 5f

        /**
         * 雨滴默认下落最小速度
         */
        private const val DEFAULT_DROP_MIN_SPEECH = 1f

        /**
         * 雨滴默认下落斜率
         */
        private const val DEFAULT_DROP_SLOPE = -3f

        private const val CLOUD_SCALE_RATIO = 0.85f
    }

    /**
     * 左边云默认颜色
     */
    private val DEFAULT_LEFT_CLOUD_COLOR = Color.parseColor("#B0B0B0")

    /**
     * 右边云默认颜色
     */
    private val DEFAULT_RIGHT_CLOUD_COLOR = Color.parseColor("#DFDFDF")

    /**
     * 雨滴默认颜色
     */
    private val DEFAULT_RAIN_COLOR = Color.parseColor("#80B9C5")

    /**
     * 左边云颜色
     */
    private var mLeftCloudColor = DEFAULT_LEFT_CLOUD_COLOR

    /**
     * 右边云颜色
     */
    private var mRightCloudColor = DEFAULT_RIGHT_CLOUD_COLOR

    /**
     * 雨滴颜色
     */
    private var mRainColor = DEFAULT_RAIN_COLOR

    /**
     * 同时可以存在的最大雨滴数量
     */
    private val mRainDropMaxNumber = DEFAULT_DROP_MAX_NUMBER

    /**
     * 雨滴创建时间间隔（毫秒）
     */
    private val mRainDropCreationInterval = DEFAULT_DROP_CREATION_INTERVAL

    /**
     * 雨滴默认最大长度
     */
    private val mRainDropMaxLength = DEFAULT_DROP_MAX_LENGTH

    /**
     * 雨滴默认最小长度
     */
    private val mRainDropMinLength = DEFAULT_DROP_MIN_LENGTH

    /**
     * 雨滴宽度
     */
    private val mRainDropSize = DEFAULT_DROP_SIZE

    /**
     * 雨滴下落最大速度
     */
    private val mRainDropMaxSpeed = DEFAULT_DROP_MAX_SPEECH

    /**
     * 雨滴下落最小速度
     */
    private val mRainDropMinSpeed = DEFAULT_DROP_MIN_SPEECH

    /**
     * 雨滴下落斜率
     */
    private val mRainDropSlope = DEFAULT_DROP_SLOPE

    /**
     * 左边云画笔
     */
    private var mLeftCloudPaint: Paint? = null

    /**
     * 右边云画笔
     */
    private var mRightCloudPaint: Paint? = null

    /**
     * 雨滴画笔
     */
    private var mRainPaint: Paint? = null

    /**
     * 左边云路径
     */
    private var mLeftCloudPath: Path? = null

    /**
     * 右边云路径
     */
    private var mRightCloudPath: Path? = null

    /**
     * 用于计算的path
     */
    private var mComputePath: Path? = null

    /**
     * 用于计算的matrix
     */
    private var mComputeMatrix: Matrix? = null

    /**
     * 左边云动画
     */
    private var mLeftCloudAnimator: ValueAnimator? = null

    /**
     * 右边云动画
     */
    private var mRightCloudAnimator: ValueAnimator? = null

    /**
     * 左边云动画值
     */
    private var mLeftCloudAnimatorValue = 0f

    /**
     * 右边云动画值
     */
    private var mRightCloudAnimatorValue = 0f

    /**
     * 左边云动画时间（用于动画 暂停 恢复）
     */
    private var mLeftCloudAnimatorTime: Long = 0

    /**
     * 右边云动画时间（用于动画 暂停 恢复）
     */
    private var mRightCloudAnimatorTime: Long = 0

    /**
     * 所有的雨滴对象
     */
    private var mRainDrops = mutableListOf<RainDropBean>()

    /**
     * 已移除的雨滴对象
     */
    private var mRemovedRainDrops = mutableListOf<RainDropBean>()

    /**
     * 雨滴对象回收栈
     */
    private var mRainDropRecycler = Stack<RainDropBean>()

    /**
     * 雨滴框 ？？？
     */
    private var mRainDropRect: RectF? = null

    private var mRainDropClipRect: RectF? = null

    /**
     * 最大平移距离
     */
    private var mMaxTranslationX = 0f

    /**
     * 控件宽度
     */
    private var mViewWidth = 0

    /**
     * 控件高度
     */
    private var mViewHeight = 0

    /**
     * 上一个雨滴对象创建时间
     */
    private var mRainDropCreationTime: Long = 0

    private val mOnlyRandom = Random() //the only random object

    private val mHandler = Handler()

    init {
        parseAttrs(attrs)
    }

    private fun parseAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RainyView)
        mLeftCloudColor = typedArray.getColor(R.styleable.RainyView_left_cloud_color, DEFAULT_LEFT_CLOUD_COLOR)
        mRightCloudColor = typedArray.getColor(R.styleable.RainyView_right_cloud_color, DEFAULT_RIGHT_CLOUD_COLOR)
        mRainColor = typedArray.getColor(R.styleable.RainyView_rain_color, DEFAULT_RAIN_COLOR)
        typedArray.recycle()
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

    private fun initViewMeasure() {
        //关闭硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        initPaints()
        initPaths()
    }

    private fun initPaints() {
        //左边云画笔初始化
        mLeftCloudPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mLeftCloudPaint?.color = mLeftCloudColor
        mLeftCloudPaint?.style = Paint.Style.FILL
        //右边云画笔初始化
        mRightCloudPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mRightCloudPaint?.color = mRightCloudColor
        mRightCloudPaint?.style = Paint.Style.FILL
        //雨滴画笔初始化
        mRainPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mRainPaint?.color = mRainColor
        mRainPaint?.style = Paint.Style.STROKE
        mRainPaint?.strokeCap = Paint.Cap.ROUND
        mRainPaint?.strokeWidth = mRainDropSize.toFloat()
    }

    private fun initPaths() {
        mLeftCloudPath = Path()
        mRightCloudPath = Path()
        mComputePath = Path()
        mRainDropRect = RectF()
        mRainDropClipRect = RectF()
        mComputeMatrix = Matrix()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        stopAnimation()

        //path初始化
        mLeftCloudPath?.reset()
        mRightCloudPath?.reset()

        //path计算
        val viewCenterX = mViewWidth / 2
        val minSize = Math.min(mViewHeight, mViewWidth)

        //左边云计算 path=================================================================================================================================
        val mLeftCloudWidth = minSize / 2.5f
        val mLeftCloudRoundHeight = mLeftCloudWidth / 3f
        //底部圆角矩形半径
        val mLeftCloudRoundRadius = mLeftCloudRoundHeight

        val mLeftCloudEndX =
            (mViewWidth - mLeftCloudWidth - mLeftCloudWidth * CLOUD_SCALE_RATIO / 2) / 2 + mLeftCloudWidth
        val mLeftCloudEndY = mViewHeight / 3f
        //添加底部圆角矩形
        mLeftCloudPath?.addRoundRect(
            RectF(
                mLeftCloudEndX - mLeftCloudWidth,
                mLeftCloudEndY - mLeftCloudRoundHeight,
                mLeftCloudEndX,
                mLeftCloudEndY
            ),
            mLeftCloudRoundRadius, mLeftCloudRoundRadius, Path.Direction.CW
        )

        val mLeftCloudCircleCenterY = mLeftCloudEndY - mLeftCloudRoundHeight
        val mLeftCloudLeftCircleCenterX = mLeftCloudEndX - mLeftCloudWidth + mLeftCloudRoundRadius
        val mLeftCloudRightCircleCenterX = mLeftCloudEndX - mLeftCloudRoundRadius
        //添加左边圆
        mLeftCloudPath?.addCircle(
            mLeftCloudLeftCircleCenterX,
            mLeftCloudCircleCenterY,
            mLeftCloudRoundRadius / 2f,
            Path.Direction.CW
        )
        //添加右边圆
        mLeftCloudPath?.addCircle(
            mLeftCloudRightCircleCenterX,
            mLeftCloudCircleCenterY,
            mLeftCloudRoundRadius * 3 / 4f,
            Path.Direction.CW
        )

        //右边云计算 path=================================================================================================================================
        val mRightCloudTranslateX = mLeftCloudWidth * 2 / 3f
        val mRightCloudCenterX = mRightCloudTranslateX + viewCenterX - mLeftCloudWidth / 2f
        val calculateRect = RectF()
        mLeftCloudPath?.computeBounds(calculateRect, false)

        //矩阵变化
        mComputeMatrix?.reset()
        //平移
        mComputeMatrix?.preTranslate(mRightCloudTranslateX, -calculateRect.height() * (1 - CLOUD_SCALE_RATIO) / 2)
        //缩放
        mComputeMatrix?.postScale(CLOUD_SCALE_RATIO, CLOUD_SCALE_RATIO, mRightCloudCenterX, mLeftCloudEndY)
        mLeftCloudPath?.transform(mComputeMatrix, mRightCloudPath)

        //雨滴范围计算 ===============================================================================================================
        val left = calculateRect.left + mLeftCloudRoundRadius
        mRightCloudPath?.computeBounds(calculateRect, false)
        val right = calculateRect.right
        val top = calculateRect.bottom
        mRainDropRect?.set(left, top, right, mViewHeight * 3 / 4f)
        mRainDropClipRect?.set(0f, mRainDropRect!!.top, mViewWidth.toFloat(), mRainDropRect!!.bottom)

        mMaxTranslationX = mLeftCloudRoundRadius / 2f

        startAnimations()
    }

    //设置云平移动画
    private fun startAnimations() {
        //左边云动画
        mLeftCloudAnimator = ValueAnimator.ofFloat(0f, 1f)
        mLeftCloudAnimator?.duration = 1000
        mLeftCloudAnimator?.interpolator = LinearInterpolator()
        mLeftCloudAnimator?.repeatCount = ValueAnimator.INFINITE
        mLeftCloudAnimator?.repeatMode = ValueAnimator.REVERSE
        mLeftCloudAnimator?.addUpdateListener {
            mLeftCloudAnimatorValue = it.animatedValue as Float
            invalidate()
        }
        mLeftCloudAnimator?.start()

        //右边云动画
        mRightCloudAnimator = ValueAnimator.ofFloat(1f, 0f)
        mRightCloudAnimator?.duration = 1000
        mRightCloudAnimator?.interpolator = LinearInterpolator()
        mRightCloudAnimator?.repeatCount = ValueAnimator.INFINITE
        mRightCloudAnimator?.repeatMode = ValueAnimator.REVERSE
        mRightCloudAnimator?.addUpdateListener {
            mRightCloudAnimatorValue = it.animatedValue as Float
            invalidate()
        }
        mRightCloudAnimator?.start()
        //开始飘落雨滴
        mHandler.post(mTask)
    }

    override fun onDraw(canvas: Canvas?) {
        //画雨滴 范围裁切
        if (mRainDropClipRect != null && mRainPaint != null) {
            canvas?.save()
            canvas?.clipRect(mRainDropClipRect)
            drawRainDrops(canvas!!)
            canvas.restore()
        }
        //先画右边云
        if (mRightCloudPath != null && mRightCloudPaint != null) {
            mComputeMatrix?.reset()
            mComputeMatrix?.postTranslate((mMaxTranslationX / 2f) * mRightCloudAnimatorValue, 0f)
            mRightCloudPath?.transform(mComputeMatrix, mComputePath)
            canvas?.drawPath(mComputePath, mRightCloudPaint)
        }
        //再画左边云
        if (mLeftCloudPath != null && mLeftCloudPaint != null) {
            mComputeMatrix?.reset()
            mComputeMatrix?.postTranslate(mMaxTranslationX * mLeftCloudAnimatorValue, 0f)
            mLeftCloudPath?.transform(mComputeMatrix, mComputePath)
            canvas?.drawPath(mComputePath, mLeftCloudPaint)
        }
    }

    private fun drawRainDrops(canvas: Canvas) {
        for (rainDrop in mRainDrops) {
            canvas.drawLine(
                rainDrop.pointX, rainDrop.pointY,
                if (rainDrop.slope > 0) rainDrop.pointX + rainDrop.xLength else rainDrop.pointX - rainDrop.xLength,
                rainDrop.pointY + rainDrop.yLength,
                mRainPaint
            )
        }
    }

    /**
     * The drop's handled task.
     * Call handler to schedule the task.
     */
    private val mTask = object : Runnable {
        override fun run() {
            createRainDrop()
            updateRainDropState()

            mHandler.postDelayed(this, 20)
        }
    }

    private fun createRainDrop() {
        if (mRainDrops.size >= mRainDropMaxNumber || mRainDropRect == null || mRainDropRect!!.isEmpty) {
            return
        }

        val current = System.currentTimeMillis()
        if (current - mRainDropCreationTime < mRainDropCreationInterval) {
            return
        }

        if (mRainDropMinLength > mRainDropMaxLength || mRainDropMinSpeed > mRainDropMaxSpeed) {
            throw IllegalArgumentException("The minimum value cannot be greater than the maximum value.")
        }

        mRainDropCreationTime = current

        val rainDrop = obtainRainDrop()
        rainDrop.slope = mRainDropSlope
        rainDrop.speedX = mRainDropMinSpeed + mOnlyRandom.nextFloat() * mRainDropMaxSpeed
        rainDrop.speedY = rainDrop.speedX * Math.abs(rainDrop.slope)

        val rainDropLength = mRainDropMinLength + mOnlyRandom.nextInt(mRainDropMaxLength - mRainDropMinLength)
        val degree = Math.toDegrees(Math.atan(rainDrop.slope.toDouble()))

        rainDrop.xLength = Math.abs(Math.cos(degree * Math.PI / 180) * rainDropLength).toFloat()
        rainDrop.yLength = Math.abs(Math.sin(degree * Math.PI / 180) * rainDropLength).toFloat()

        rainDrop.pointX = mRainDropRect!!.left +
                mOnlyRandom.nextInt(mRainDropRect!!.width().toInt()) //random x coordinate
        rainDrop.pointY = mRainDropRect!!.top - rainDrop.yLength //the fixed y coordinate

        mRainDrops.add(rainDrop)
    }

    private fun updateRainDropState() {
        mRemovedRainDrops.clear()

        for (rainDrop in mRainDrops) {
            if (rainDrop.pointY - rainDrop.yLength > mRainDropRect!!.bottom) {
                mRemovedRainDrops.add(rainDrop)
                recycle(rainDrop)
            } else {
                if (rainDrop.slope >= 0) {
                    rainDrop.pointX += rainDrop.speedX
                } else {
                    rainDrop.pointX -= rainDrop.speedX
                }
                rainDrop.pointY += rainDrop.speedY
            }
        }

        if (!mRemovedRainDrops.isEmpty()) {
            mRainDrops.removeAll(mRemovedRainDrops)
        }

        if (!mRainDrops.isEmpty()) {
            invalidate()
        }
    }

    private fun obtainRainDrop(): RainDropBean {
        return if (mRainDropRecycler.isEmpty()) {
            RainDropBean()
        } else mRainDropRecycler.pop()

    }

    private fun recycle(rainDrop: RainDropBean?) {
        if (rainDrop == null) {
            return
        }

        if (mRainDropRecycler.size >= mRainDropMaxNumber) {
            mRainDropRecycler.pop()
        }

        mRainDropRecycler.push(rainDrop)
    }

    fun startAnimation() {
        if (mLeftCloudAnimator != null && !mLeftCloudAnimator!!.isRunning) {
            mLeftCloudAnimator?.currentPlayTime = mLeftCloudAnimatorTime
            mLeftCloudAnimator?.start()
        }
        if (mRightCloudAnimator != null && !mRightCloudAnimator!!.isRunning) {
            mRightCloudAnimator?.currentPlayTime = mRightCloudAnimatorTime
            mRightCloudAnimator?.start()
        }
        mHandler.removeCallbacks(mTask)
        mHandler.post(mTask)
    }

    fun stopAnimation() {
        if (mLeftCloudAnimator != null && mLeftCloudAnimator!!.isRunning) {
            mLeftCloudAnimatorTime = mLeftCloudAnimator!!.currentPlayTime
            mLeftCloudAnimator?.cancel()
        }
        if (mRightCloudAnimator != null && mRightCloudAnimator!!.isRunning) {
            mRightCloudAnimatorTime = mRightCloudAnimator!!.currentPlayTime
            mRightCloudAnimator?.cancel()
        }
        mHandler.removeCallbacks(mTask)
    }

    fun releaseView() {
        stopAnimation()
        if (mLeftCloudAnimator != null) mLeftCloudAnimator?.removeAllUpdateListeners()
        if (mRightCloudAnimator != null) mRightCloudAnimator?.removeAllUpdateListeners()
        mRainDrops.clear()
        mRemovedRainDrops.clear()
        mRainDropRecycler.clear()
        mHandler
    }
}
