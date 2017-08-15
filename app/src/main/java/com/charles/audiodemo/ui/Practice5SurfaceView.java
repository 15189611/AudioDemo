package com.charles.audiodemo.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.charles.audiodemo.R;

/**
 * Created by Charles.
 */

public class Practice5SurfaceView extends SurfaceView {

    private Bitmap bitmap;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public Practice5SurfaceView(Context context) {
        this(context,null);
    }

    public Practice5SurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public Practice5SurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.board_icon);
        SurfaceHolder holder = getHolder();
        holder.addCallback(new DoThings());
    }

    private class DoThings implements SurfaceHolder.Callback{
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            //在surface的大小发生改变时激发
            Log.e("Charles2","surfaceChanged");
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder){
            //绘制在整个Surface上
            Log.e("Charles2","surfaceCreated");
            Canvas canvas = holder.lockCanvas();
            canvas.drawColor(Color.BLACK);
            holder.unlockCanvasAndPost(canvas);
            //绘制在Surface上的图片
            canvas = holder.lockCanvas();
            canvas.drawBitmap(bitmap,100F,100F,paint);
            holder.unlockCanvasAndPost(canvas);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            //销毁时激发，一般在这里将画图的线程停止、释放。
            Log.e("Charles2","surfaceDestroyed");
        }
    }
}
