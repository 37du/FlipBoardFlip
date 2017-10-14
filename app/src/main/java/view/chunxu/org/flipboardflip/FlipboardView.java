package view.chunxu.org.flipboardflip;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class FlipboardView extends View {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private ObjectAnimator startAnimator = ObjectAnimator.ofInt(this, "startAngle", 0, 30);
    private ObjectAnimator rotateAnimator = ObjectAnimator.ofInt(this, "rotate", 0, 270);
    private ObjectAnimator endAnimator = ObjectAnimator.ofInt(this, "endAngle", 0, 30);
    private Camera camera = new Camera();
    private RectF circleRF = new RectF();
    private Path path = new Path();
    private Path halfCirclePath = new Path();
    private Bitmap bitmap;
    private int mStartAngle;
    private int mEndAngle;
    private int mRotate;


    public FlipboardView(Context context) {
        super(context);
    }

    public FlipboardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FlipboardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float newZ = - displayMetrics.density * 6;
        camera.setLocation(0, 0, newZ);

        startAnimator.setDuration(1000);
        startAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                rotateAnimator.setDuration(1000);
                rotateAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        endAnimator.setDuration(1000);
                        endAnimator.start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });

                rotateAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimator.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        float diagonal = (float) (Math.sqrt(Math.pow(bitmapHeight, 2) + Math.pow(bitmapWidth, 2)));
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int x = centerX - bitmapWidth / 2;
        int y = centerY - bitmapHeight / 2;

        path.reset();
        halfCirclePath.reset();
        path.addRect(x, y, x + bitmapWidth, y + bitmapHeight, Path.Direction.CCW);
        halfCirclePath.moveTo(centerX, centerY);
        circleRF.set(centerX - diagonal / 2, centerY - diagonal / 2, centerX + diagonal / 2, centerY + diagonal / 2);
        halfCirclePath.addArc(circleRF, -90f - mRotate, 180.0f);
        halfCirclePath.close();
        path.op(halfCirclePath, Path.Op.DIFFERENCE);

        if (mEndAngle == 0) {
            canvas.save();
            if (mStartAngle > 0) {
                canvas.clipPath(path);
            }


            canvas.drawBitmap(bitmap, x, y, paint);
            canvas.restore();
        } else if (mEndAngle > 0) {
            canvas.save();
            canvas.translate(centerX, centerY);
            camera.save();
            camera.rotateX(-mEndAngle);
            camera.applyToCanvas(canvas);
            camera.restore();
            canvas.translate(-centerX, -centerY);
            canvas.clipPath(path);
            canvas.drawBitmap(bitmap, x, y, paint);
            canvas.restore();
        }

        path.reset();
        path.addRect(x, y, x + bitmapWidth, y + bitmapHeight, Path.Direction.CCW);
        halfCirclePath.reset();
        halfCirclePath.moveTo(centerX, centerY);
        circleRF.set(centerX - diagonal / 2, centerY - diagonal / 2, centerX + diagonal / 2, centerY + diagonal / 2);
        halfCirclePath.addArc(circleRF, -90f - mRotate, 180.0f);
        halfCirclePath.close();
        path.op(halfCirclePath, Path.Op.INTERSECT);

        if (mStartAngle > 0 && mStartAngle <= 30 && mRotate == 0) {
            canvas.save();
            canvas.translate(centerX, centerY);
            camera.save();
            camera.rotateY(-mStartAngle);
            camera.applyToCanvas(canvas);
            camera.restore();
            canvas.translate(-centerX, -centerY);
            canvas.clipPath(path);
            canvas.drawBitmap(bitmap, x, y, paint);
            canvas.restore();
        }

        if (mRotate > 0 && mRotate <= 270) {
            canvas.save();
            canvas.translate(centerX, centerY);
            camera.save();
            camera.rotateZ(mRotate);
            camera.rotateY(-mStartAngle);
            camera.applyToCanvas(canvas);
            camera.restore();
            canvas.rotate(mRotate);
            canvas.translate(-centerX, -centerY);
            canvas.clipPath(path);
            canvas.drawBitmap(bitmap, x, y, paint);
            canvas.restore();
        }

    }

    public int getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(int startAngle) {
        mStartAngle = startAngle;
        invalidate();
    }

    public int getEndAngle() {
        return mEndAngle;
    }

    public void setEndAngle(int endAngle) {
        mEndAngle = endAngle;
        invalidate();
    }

    public int getRotate() {
        return mRotate;
    }

    public void setRotate(int rotate) {
        mRotate = rotate;
        invalidate();
    }
}
