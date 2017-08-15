package com.charles.audiodemo.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.charles.audiodemo.R;


/**
 * Created by Charles.
 */

public class Practice6DrawBitmap extends View {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Bitmap bitmap;

    public Practice6DrawBitmap(Context context) {
        this(context,null);
    }

    public Practice6DrawBitmap(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public Practice6DrawBitmap(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.board_icon,options);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap,100F,100F,paint);
    }
}
