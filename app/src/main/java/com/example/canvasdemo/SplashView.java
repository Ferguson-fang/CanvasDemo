package com.example.canvasdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;

public class SplashView extends View {

    //旋转圆的画笔
    private Paint mPaint;
    //扩散圆的画笔
    private Paint mHolePaint;
    //属性动画
    private ValueAnimator mValueAnimator;

    //背景色
    private int mBackgroundColor = Color.WHITE;
    private int[] mCircleColors;

    //表示旋转圆的中心坐标
    private float mCenterX;
    private float mCenterY;
    //表示斜对角线长度的一半，扩散圆最大半径
    private float mDistance;

    //六个小球的半径
    private float mCircleRadius = 18;
    //旋转大圆的半径
    private float mRotateRadius = 90;

    //当前大圆的旋转角度
    private float mCurrentRotateAngle = 0F;
    //当前大圆的半径
    private float mCurrentRotateRadius = mRotateRadius;
    //扩散圆的半径
    private float mCurrentHoleRadius = 0F;
    //表示旋转动画的时长
    private int mRotateDuration = 1200;


    public SplashView(Context context) {
        super(context);
        init(context);
    }

    public SplashView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SplashView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mHolePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHolePaint.setStyle(Paint.Style.STROKE);
        mHolePaint.setColor(mBackgroundColor);

        mCircleColors = context.getResources().getIntArray(R.array.splash_circle_colors);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w * 1f / 2;
        mCenterY = h * 1f / 2;
        mDistance = (float) (Math.hypot(w,h) / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mState == null){
            mState = new RotateState();
        }
        mState.drawState(canvas);
    }

    private SplashState mState;

    private abstract class SplashState{
        abstract void drawState(Canvas canvas);
    }
    //1.旋转
    private class RotateState extends SplashState{

        private RotateState(){
            mValueAnimator = ValueAnimator.ofFloat(0,(float)(Math.PI *2));
            mValueAnimator.setRepeatCount(2);
            //时长
            mValueAnimator.setDuration(mRotateDuration);
            mValueAnimator.setInterpolator(new LinearInterpolator());
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentRotateAngle = (float)animation.getAnimatedValue();
                    invalidate();
                }
            });
            mValueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    //当第一种动画执行完以后切换成第二种
                    mState = new MerginState();
                }
            });
            mValueAnimator.start();
        }

        @Override
        void drawState(Canvas canvas) {
            //绘制背景
            drawBackground(canvas);
            //绘制六个小球
            drawCircles(canvas);
        }
    }

    private void drawCircles(Canvas canvas) {
        /**
         * 判断空数据一定要先判断是否为null，再判断数据的长度。
         * if(array != null && array.leng != 0)
         * {
         * 数据不为空数据的判断。
         * */
        if (mCircleColors != null && mCircleColors.length != 0) {
            //得到两小球之间的角度，Math.PI * 2表示一周，
            float rotateAngle = (float) (Math.PI * 2 / mCircleColors.length);
            for (int i = 0; i < mCircleColors.length; i++) {
                // x = r * cos(a) + centX;
                // y = r * sin(a) + centY;
                float angle = i * rotateAngle + mCurrentRotateAngle;
                float cx = (float) (Math.cos(angle) * mCurrentRotateRadius + mCenterX);
                float cy = (float) (Math.sin(angle) * mCurrentRotateRadius + mCenterY);
                mPaint.setColor(mCircleColors[i]);
                canvas.drawCircle(cx, cy, mCircleRadius, mPaint);
            }
        }
    }

    private void drawBackground(Canvas canvas){

        if(mCurrentHoleRadius > 0){
            //绘制一个空心圆
            float strokeWidth = mDistance - mCurrentHoleRadius;
            //真实半径
            float radius = strokeWidth / 2+ mCurrentHoleRadius;
            mHolePaint.setStrokeWidth(strokeWidth);
            canvas.drawCircle(mCenterX,mCenterY,radius,mHolePaint);
        }else {
            //绘制白色背景
            canvas.drawColor(mBackgroundColor);
        }
    }
    //2.扩散聚合
    private class MerginState extends SplashState{

        private MerginState(){
            mValueAnimator = ValueAnimator.ofFloat(mCircleRadius,mRotateRadius);
            //mValueAnimator.setRepeatCount(2);
            //时长
            mValueAnimator.setDuration(mRotateDuration);
            //查OvershootInterpolator类的作用!!!!!!!!!!!!!
            /**
             * setInterpolator()为插值器
             *
             * 因为在补间动画中，我们一般只定义关键帧（首帧或尾帧），
             * 然后由系统自动生成中间帧，生成中间帧的这个过程可以成为“插值”
             *
             * OvershootInterpolator：在上一个基础上超出终点一小步再回到终点
             * */
            mValueAnimator.setInterpolator(new OvershootInterpolator(10f));
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentRotateRadius = (float)animation.getAnimatedValue();
                    invalidate();
                }
            });
            mValueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    //当第二种动画执行完以后切换成第三种
                    mState = new ExpandState();
                }
            });
            mValueAnimator.start();
        }
        @Override
        void drawState(Canvas canvas) {
            drawBackground(canvas);
            //绘制扩散聚合的效果
            drawCircles(canvas);
        }
    }
    //3.水波纹效果
    private class ExpandState extends SplashState{

        private ExpandState() {
            mValueAnimator = ValueAnimator.ofFloat(mCircleRadius,mDistance);
            //mValueAnimator.setRepeatCount(2);
            //时长
            mValueAnimator.setDuration(mRotateDuration);
            mValueAnimator.setInterpolator(new LinearInterpolator());
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentHoleRadius = (float)animation.getAnimatedValue();
                    invalidate();
                }
            });
            mValueAnimator.start();
        }

        @Override
        void drawState(Canvas canvas) {
            drawBackground(canvas);

        }
    }
}





