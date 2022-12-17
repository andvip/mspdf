package com.example.xxkt.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.AbsSeekBar
import com.example.xxkt.R
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * 自定义评分条 主要针对大尺寸，图片拉伸或者显示不全问题
 */
class RatingbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    AbsSeekBar(context, attrs, defStyleAttr) {

    // 正常、半个和选中的星星
    private var mStarNormal: Bitmap? = null
    private var mStarHalf: Bitmap? = null
    private var mStarSelected: Bitmap? = null

    //星星的总数
    private var mStartTotalNumber = 5

    //选中的星星个数
    private var mSelectedNumber: Float = 0.toFloat()

    // 星星之间的间距
    private val mStartDistance: Int

    // 是否画满
    private var mStatus = Status.FULL

    // 星星的宽高
    private val mStarWidth: Float
    private val mStarHeight: Float

    // 星星选择变化的回调
    private val mOnStarChangeListener: OnStarChangeListener? = null

    // 是不是要画满,默认不画半个的
    private var isFull: Boolean = true

    //是否可以选择
    private var isSelect: Boolean = true

    // 画笔
    private val mPaint = Paint()

    val selectStarNum: Float?
        get() = mSelectedNumber

    // 用于判断是绘制半个，还是全部
    private enum class Status {
        FULL, HALF
    }

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.RatingbarView)

        // 未选中的图片资源
        val starNormalId = array.getResourceId(R.styleable.RatingbarView_starEmptyRes, 0)
        if (starNormalId == 0) {
            throw IllegalArgumentException("请设置属性 starNormal")
        }
        mStarNormal = BitmapFactory.decodeResource(resources, starNormalId)
        // 选中一半的图片资源
        val starHalfId = array.getResourceId(R.styleable.RatingbarView_starHalfRes, 0)
        if (starHalfId != 0) {
            mStarHalf = BitmapFactory.decodeResource(resources, starHalfId)
        }
        // 选中全部的图片资源
        val starSelectedId = array.getResourceId(R.styleable.RatingbarView_starSelectedRes, 0)
        if (starSelectedId == 0) {
            throw IllegalArgumentException("请设置属性 starSelected")
        }
        mStarSelected = BitmapFactory.decodeResource(resources, starSelectedId)
        // 如果没设置一半的图片资源，就用全部的代替
        if (starHalfId == 0) {
            mStarHalf = mStarSelected
        }

        mStartTotalNumber =
            array.getInt(R.styleable.RatingbarView_startTotalNumber, mStartTotalNumber)
        mSelectedNumber = array.getFloat(R.styleable.RatingbarView_selectedNumber, mSelectedNumber)
        mStartDistance = array.getDimension(R.styleable.RatingbarView_starDistance, 0f).toInt()
        mStarWidth = array.getDimension(R.styleable.RatingbarView_starWidth, 0f)
        mStarHeight = array.getDimension(R.styleable.RatingbarView_starHeight, 0f)
        isFull = array.getBoolean(R.styleable.RatingbarView_starIsFull, true)
        isSelect = array.getBoolean(R.styleable.RatingbarView_starIsSelect, true)
        array.recycle()

        // 如有指定宽高，获取最大值 去改变星星的大小（星星是正方形）
        val starWidth = max(mStarWidth, mStarHeight).toInt()
        if (starWidth > 0) {
            mStarNormal = resetBitmap(mStarNormal, starWidth)
            mStarSelected = resetBitmap(mStarSelected, starWidth)
            mStarHalf = resetBitmap(mStarHalf, starWidth)
        }

        // 计算一半还是全部（小数部分小于等于0.5就只是显示一半）
        if (!isFull) {
            if ((mSelectedNumber * 10).toInt() % 10 in 1..4) {
                mStatus = Status.HALF
            } else {
                mStatus = Status.FULL
            }
        }
        if (isFull) {
            setStepSize(1f)
        } else {
            setStepSize(0.5f)
        }
        initAccess()
    }

    fun canUserSetProgress(): Boolean {
        return !isIndeterminate && isEnabled
    }

    private fun initAccess() {
        accessibilityDelegate = object : AccessibilityDelegate() {
            override fun onInitializeAccessibilityNodeInfo(
                host: View?,
                info: AccessibilityNodeInfo?
            ) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                info?.text = "星级评分条"
                info?.isClickable = true
                info?.isEnabled = true
                if (canUserSetProgress()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Log.d("RatingbarView", "addAction-$progress")
                        info?.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_PROGRESS)
                    }
                }

                val ratio = getProgressPerStar()
                val progressInStars: Int = (progress / ratio).toInt()
//                val temp: Int = (ceil(progressInStars.toDouble()) * ratio).toInt()
                Log.d("RatingbarView", "temp-$progress-$max-$mStartTotalNumber-$progressInStars")
                info?.text = "$progressInStars" + "颗星"
                setSelectedNumber(progressInStars)
            }
        }
    }

    fun setStepSize(stepSize: Float) {
        if (stepSize <= 0) {
            return
        }
        val newMax: Float = mStartTotalNumber / stepSize
        val newProgress = (newMax / max * progress).toInt()
        Log.d("RatingbarView", "newMax-$newMax-$max-$progress-$newProgress")
        max = newMax.toInt()
        progress = newProgress
    }

    private fun getProgressPerStar(): Float {
        return if (mStartTotalNumber > 0) {
            1f * max / mStartTotalNumber
        } else {
            1f
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // 用正常的一个星星图片去测量高
        val height = paddingTop + paddingBottom + (mStarNormal?.height ?: 0)
        // 宽 = 星星的宽度*总数 + 星星的间距*（总数-1） +padding
        val width =
            paddingLeft + paddingRight + (mStarNormal?.width
                ?: 0) * mStartTotalNumber + mStartDistance * (mStartTotalNumber - 1)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        // 循环绘制
        for (i in 0 until mStartTotalNumber) {
            var left = paddingLeft.toFloat()
            // 从第二个星星开始，给它设置星星的间距
            if (i > 0) {
                left = (paddingLeft + i * ((mStarNormal?.width ?: 0) + mStartDistance)).toFloat()
            }
            val top = paddingTop.toFloat()
            // 绘制选中的星星
            if (i < mSelectedNumber) {
                // 比当前选中的数量小
                if (i < mSelectedNumber - 1) {
                    mStarSelected?.let {
                        canvas.drawBitmap(it, left, top, mPaint)
                    }
                } else {
                    // 在这里判断是不是要绘制满的
                    if (mStatus == Status.FULL) {
                        mStarSelected?.let {
                            canvas.drawBitmap(it, left, top, mPaint)
                        }
                    } else if (mStarHalf != null) {
                        mStarHalf?.let {
                            canvas.drawBitmap(it, left, top, mPaint)
                        }
                    }
                }
            } else if (mStarNormal != null) {
                // 绘制正常的星星
                mStarNormal?.let {
                    canvas.drawBitmap(it, left, top, mPaint)
                }
            }

        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isSelect) return super.onTouchEvent(event)
        when (event.action) {
            //减少绘制
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                // 获取用户触摸的x位置
                val x = event.x
                // 一个星星占的宽度
                val startWidth = width / mStartTotalNumber
                // 计算用户触摸星星的位置
                var position = (x / startWidth + 1).toInt()
                Log.d("RatingbarView", "position-$position")
                if (position <= 0) {
                    position = 0
                }
                if (position > mStartTotalNumber) {
                    position = mStartTotalNumber
                }
                // 计算绘制的星星是不是满的
                val result = x - startWidth * (position - 1)
                Log.d("RatingbarView", "result-$result")
                var status: Status
                // 结果大于一半就是满的
                if (result > startWidth * 0.5f) {
                    // 满的
                    status = Status.FULL
                } else {
                    // 一半的
                    status = Status.HALF
                }
                if (isFull) {
                    status = Status.FULL
                }
                //减少绘制
                if (mSelectedNumber != position.toFloat() || status != mStatus) {
                    mSelectedNumber = position.toFloat()
                    mStatus = status
                    progress = (mSelectedNumber * getProgressPerStar()).roundToInt()
                    invalidate()
                    if (mOnStarChangeListener != null) {
                        position = (mSelectedNumber - 1).toInt()
                        Log.d("RatingbarView", "position2-$position")
                        // 选中的数量：满的就回调（1.0这种），一半就（0.5这种）
                        val selectedNumber = if (status == Status.FULL)
                            mSelectedNumber
                        else
                            mSelectedNumber - 0.5f

                        Log.d("RatingbarView", "selectedNumber-$selectedNumber")
                        mOnStarChangeListener.OnStarChanged(
                            selectedNumber,
                            if (position <= 0) 0 else position
                        )
                    }
                }
            }
        }
        return true
    }

    //  回调监听（选中的数量，位置）
    interface OnStarChangeListener {
        fun OnStarChanged(selectedNumber: Float, position: Int)
    }

    /**
     * 如果用户设置了图片的宽高，就重新设置图片
     */
    fun resetBitmap(bitMap: Bitmap?, startWidth: Int): Bitmap? {
        // 得到新的图片
        if (bitMap == null) return null else
            return Bitmap.createScaledBitmap(bitMap, startWidth, startWidth, true)
    }

    /**
     * 设置选中星星的数量
     */
    fun setSelectedNumber(selectedNumber: Int) {
        if (selectedNumber in 0..mStartTotalNumber) {
            this.mSelectedNumber = selectedNumber.toFloat()
            progress = (mSelectedNumber * getProgressPerStar()).roundToInt()
            invalidate()
        }
    }

    /**
     * 设置是否可以选择
     */
    fun setSelect(isSelect: Boolean) {
        this.isSelect = isSelect
    }

    /**
     * 设置星星的总数量
     */
    fun setStartTotalNumber(startTotalNumber: Int) {
        if (startTotalNumber > 0) {
            this.mStartTotalNumber = startTotalNumber
            progress = (mSelectedNumber * getProgressPerStar()).roundToInt()
            invalidate()
        }

    }
}