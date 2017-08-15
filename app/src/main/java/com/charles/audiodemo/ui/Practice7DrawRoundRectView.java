package com.charles.audiodemo.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class Practice7DrawRoundRectView extends View {

    private Paint paint;

    public Practice7DrawRoundRectView(Context context) {
        this(context, null);
    }

    public Practice7DrawRoundRectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Practice7DrawRoundRectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //left, top, right, bottom 是四条边的坐标，rx 和 ry 是圆角的横向半径和纵向半径。
        RectF rectF = new RectF(100F, 100F, 500F, 300F);
        canvas.drawRoundRect(rectF, 40F, 40F, paint);
//        练习内容：使用 canvas.drawRoundRect() 方法画圆角矩形
    }
}
