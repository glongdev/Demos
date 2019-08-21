package com.glong.demo.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 行星和太阳的父容器
 *
 * @author guolong
 * @since 2019/8/20
 */
public class StarGroupView extends FrameLayout {

    // 从这个角度开始画View ，可以调整
    private static final float START_ANGLE = 270f; // 270°
    // 父容器的边界 单位dp
    private static final int PADDING = 150;
    // 绕x轴旋转的角度 70°对应的弧度
    private static final double ROTATE_X = Math.PI * 7 / 18;
    //自动旋转角度,16ms（一帧）旋转的角度，值越大转的越快
    private static final float AUTO_SWEEP_ANGLE = 0.3f;
    //px转化为angle的比例  ps:一定要给设置一个转换，不然旋转的太欢了
    private static final float SCALE_PX_ANGLE = 0.2f;
    // 以上几个值都可以根据最终效果调整

    /**
     * 角度偏差值
     */
    private float sweepAngle = 0f;

    /**
     * 行星轨迹的半径
     */
    private float mRadius;

    /**
     * 父容器的边界 ，单位px
     */
    private int mPadding;

    private Runnable autoScrollRunnable = new Runnable() {
        @Override
        public void run() {
            sweepAngle += AUTO_SWEEP_ANGLE;
            // 取个模 防止sweepAngle爆表
            sweepAngle %= 360;
            Log.d("guolong", "auto , sweepAngle == " + sweepAngle);
            layoutChildren();
            postDelayed(this, 16);
        }
    };

    public StarGroupView(@NonNull Context context) {
        this(context, null);
    }

    public StarGroupView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StarGroupView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 边距转换为px
        mPadding = (int) (context.getResources().getDisplayMetrics().density * PADDING);
        setChildrenDrawingOrderEnabled(true);
        initAnim();
        postDelayed(autoScrollRunnable, 100);
    }

    private void initAnim() {
        velocityAnim.setDuration(1000);
        velocityAnim.setInterpolator(new DecelerateInterpolator());
        velocityAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                // 乘以SCALE_PX_ANGLE是因为如果不乘 转得太欢了
                sweepAngle += (value * SCALE_PX_ANGLE);
                layoutChildren();
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
        mRadius = (getMeasuredWidth() / 2f - mPadding);
        layoutChildren();
    }

    private void layoutChildren() {
        int childCount = getChildCount();
        if (childCount == 0) return;
        // 行星之间的角度
        View centerView = centerView();
        float averageAngle;
        if (centerView == null) {
            averageAngle = 360f / childCount;
        } else {
            // centerView 不参与计算角度
            averageAngle = 360f / (childCount - 1);
        }

        int number = 0;
        for (int index = 0; index < childCount; index++) {
            View child = getChildAt(index);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            // 如果是centerView 直接居中布局
            if ("center".equals(child.getTag())) {
                child.layout(getMeasuredWidth() / 2 - childWidth / 2, getMeasuredHeight() / 2 - childHeight / 2,
                        getMeasuredWidth() / 2 + childWidth / 2, getMeasuredHeight() / 2 + childHeight / 2);
            } else {
                // 第index 个子View的角度
                double angle = (START_ANGLE - averageAngle * number + sweepAngle) * Math.PI / 180;
                double sin = Math.sin(angle);
                double cos = Math.cos(angle);

                double coordinateX = getMeasuredWidth() / 2f - mRadius * cos;
                // * Math.cos(ROTATE_X) 代表将y坐标转换为旋转之后的y坐标
                double coordinateY = getMeasuredHeight() / 2f - mRadius * sin * Math.cos(ROTATE_X);

                child.layout((int) (coordinateX - childWidth / 2),
                        (int) (coordinateY - childHeight / 2),
                        (int) (coordinateX + childWidth / 2),
                        (int) (coordinateY + childHeight / 2));

                // 假设view的最小缩放是原来的0.3倍，则缩放比例和角度的关系是
                float scale = (float) ((1 - 0.3f) / 2 * (1 - Math.sin(angle)) + 0.3f);
                child.setScaleX(scale);
                child.setScaleY(scale);
                number++;
            }
        }

        changeZ();
    }

    /**
     * 改变子View的z值以改变子View的绘制优先级，z越大优先级越低（最后绘制）
     */
    private void changeZ() {
        View centerView = centerView();
        float centerViewScaleY = 1f;
        if (centerView != null) {
            centerViewScaleY = centerView.getScaleY();
            centerView.setScaleY(0.5f);
        }
        List<View> children = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            children.add(getChildAt(i));
        }
        Collections.sort(children, new Comparator<View>() {
            @Override
            public int compare(View o1, View o2) {
                return (int) ((o1.getScaleY() - o2.getScaleY()) * 1000000);
            }
        });
        float z = 0.1f;
        for (int i = 0; i < children.size(); i++) {
            children.get(i).setZ(z);
            z += 0.1f;
        }
        if (centerView != null) {
            centerView.setScaleY(centerViewScaleY);
        }
    }

    /**
     * 获取centerView
     *
     * @return 太阳
     */
    private View centerView() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if ("center".equals(child.getTag())) {
                return child;
            }
        }
        return null;
    }


    /**
     * 手势处理
     */
    private float downX = 0f;
    /**
     * 手指按下时的角度
     */
    private float downAngle = sweepAngle;
    /**
     * 速度追踪器
     */
    private VelocityTracker velocity = VelocityTracker.obtain();
    /**
     * 滑动结束后的动画
     */
    private ValueAnimator velocityAnim = new ValueAnimator();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        velocity.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = x;
                downAngle = sweepAngle;

                // 取消动画和自动旋转
                velocityAnim.cancel();
                removeCallbacks(autoScrollRunnable);
                return true;
            case MotionEvent.ACTION_MOVE:
                float dx = downX - x;
                sweepAngle = (dx * SCALE_PX_ANGLE + downAngle);
                layoutChildren();
                break;
            case MotionEvent.ACTION_UP:
                velocity.computeCurrentVelocity(16);
                // 速度为负值代表顺时针
                scrollByVelocity(velocity.getXVelocity());
                postDelayed(autoScrollRunnable, 16);
        }
        return super.onTouchEvent(event);
    }

    private void scrollByVelocity(float velocity) {
        float end;
        if (velocity < 0)
            end = -AUTO_SWEEP_ANGLE;
        else
            end = 0f;
        velocityAnim.setFloatValues(-velocity, end);
        velocityAnim.start();
    }

    public void pause() {
        velocityAnim.cancel();
        removeCallbacks(autoScrollRunnable);
    }

    public void start() {
        postDelayed(autoScrollRunnable, 16);
    }
}
