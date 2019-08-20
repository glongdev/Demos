package com.glong.demo.kotlinview

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.core.animation.addListener
import androidx.core.view.children
import org.jetbrains.anko.dip
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 主页星球
 * @author guolong
 * @since 2019/8/18
 */
private const val START_ANGLE = 270f
private const val SCALE_PX_ANGLE = 0.2f//px 转化为angle的比例  ps:一定要给设置一个转换，不然旋转的太欢了
private const val AUTO_SWEEP_ANGLE = 0.1f//自动旋转角度

class StarGroupViewKotlin : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    // 旋转的角度
    private var sweepAngle = 0f
    private var pathRadius = 0f

    /**
     * 自动滚动
     */
    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            sweepAngle += AUTO_SWEEP_ANGLE
            sweepAngle %= 360 // 取个模 防止sweepAngle爆表
            Log.d("guolong", "auto , sweepAngle == $sweepAngle")
            layoutChildren()
            postDelayed(this, 16)
        }
    }

    private val velocityAnim = ValueAnimator() // 滑动结束后的动画

    // 容器距离左上右下的距离
    private var padding: Int = context.dip(80)
    // 根据设计将子View整体上移
    private val childTranslateY = -context.dip(20)

    init {
        //通过isChildDrawingOrderEnable 动态改变子View的绘制顺序
        isChildrenDrawingOrderEnabled = true
//        postDelayed(autoScrollRunnable, 400)

        velocityAnim.apply {
            this.duration = 1000
            this.interpolator = DecelerateInterpolator()
            this.addUpdateListener {
                val value = it.animatedValue as Float
                sweepAngle += (value * SCALE_PX_ANGLE) // 乘以SCALE_PX_ANGLE是因为如果不乘 转得太欢了
                layoutChildren()
            }
            this.addListener(onEnd = {
                postDelayed(autoScrollRunnable, 16)
            })
        }
    }

    override fun onLayout(b: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        pathRadius = (measuredWidth / 2 - padding).toFloat()
        layoutChildren()
    }

    private fun layoutChildren() {
        val childCount = childCount
        if (childCount == 0) return
        val averageAngle = if (centerView() == null) 360f / childCount else 360f / (childCount - 1)
        // START_ANGLE°开始画
        var number = 0
        for (index in 0 until childCount) {
            val child = getChildAt(index)
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight

            if (child.tag == "center") {
                child.layout(measuredWidth / 2 - childWidth / 2, measuredHeight / 2 - childHeight / 2,
                        measuredWidth / 2 + childWidth / 2, measuredHeight / 2 + childHeight / 2)
                continue
            } else {
                val angle = (START_ANGLE - averageAngle * number + sweepAngle).toDouble() * PI / 180
                val sin = sin(angle)
                val cos = cos(angle)
                val coordinateX = measuredWidth / 2 - pathRadius * cos
                val coordinateY = measuredHeight / 2 - pathRadius * sin * sin(PI / 9) // sin(PI/9)表示x轴方向倾斜的角度

                child.layout((coordinateX - childWidth / 2).toInt(),
                        (coordinateY - childHeight / 2 + childTranslateY).toInt(),
                        (coordinateX + childWidth / 2).toInt(),
                        (coordinateY + childHeight / 2 + childTranslateY).toInt())

                // 假设view的最小缩放是原来的0.3倍，则缩放比例和角度的关系是
                val scale = (1 - 0.3f) / 2 * (1 - sin(angle)) + 0.3
                child.scaleX = scale.toFloat()
                child.scaleY = scale.toFloat()
                number++
            }
        }
        changeZ()
    }

    private fun centerView(): View? {
        return children.find { it.tag == "center" }
    }

    private fun changeZ() {
        val allChild = children.filter { it.tag != "center" }.sortedBy { it.scaleY }.toMutableList()
        val centerView = centerView()
        if (centerView != null)
            for (index in 0 until allChild.size) {
                if (allChild[index].scaleY > 0.5f) {
                    allChild.add(index, centerView)
                    break
                }
            }

        var order = 0.1f
        allChild.forEach {
            it.z = order
            order += 0.1f
        }
    }

    /**
     * 手势处理
     */
    private var downX = 0f
    /**
     * 手指按下时的角度
     */
    private var downAngle = sweepAngle
    // 速度追踪器
    private val velocity = VelocityTracker.obtain()

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event?.x ?: 0f
        velocity.addMovement(event)
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = x
                downAngle = sweepAngle

                // 取消动画和自动旋转
                velocityAnim.cancel()
                removeCallbacks(autoScrollRunnable)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = (downX - x) * SCALE_PX_ANGLE
                sweepAngle = (dx + downAngle)
                layoutChildren()
            }
            MotionEvent.ACTION_UP -> {
                velocity.computeCurrentVelocity(16)
                Log.d("guolong", "vX:${velocity.xVelocity}")
                // 速度为负值代表顺时针
                scrollByVelocity(velocity.xVelocity)
                postDelayed(autoScrollRunnable, 16)
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        velocity.recycle()
    }

    private fun scrollByVelocity(velocity: Float) {
        val end = if (velocity < 0) -AUTO_SWEEP_ANGLE else 0f
        velocityAnim.setFloatValues(-velocity, end)
        velocityAnim.start()
    }

    fun pause() {
        velocityAnim.cancel()
        removeCallbacks(autoScrollRunnable)
    }

    fun start() {
        postDelayed(autoScrollRunnable, 16)
    }
}
