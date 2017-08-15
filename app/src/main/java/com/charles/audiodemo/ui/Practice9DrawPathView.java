package com.charles.audiodemo.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class Practice9DrawPathView extends View {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path path = new Path(); //初始化path对象，用来画爱心

    public Practice9DrawPathView(Context context) {
        this(context, null);
    }

    public Practice9DrawPathView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Practice9DrawPathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF rectF = new RectF(200, 200, 400, 400);
        RectF rectTo = new RectF(400, 200, 600, 400);
        path.addArc(rectF, -225, 225);  //画爱心的弧
        path.arcTo(rectTo, -180, 225, false);
        path.lineTo(400, 542);
        canvas.drawPath(path, paint);
//        练习内容：使用 canvas.drawPath() 方法画心形
    }
}
