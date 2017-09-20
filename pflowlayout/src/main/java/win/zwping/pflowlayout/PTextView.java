package win.zwping.pflowlayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;

/**
 * <p>describe：
 * <p>    note：
 * <p>  author：zwp on 2017/9/19 0019 mail：1101558280@qq.com web: http://www.zwping.win </p>
 */
public class PTextView extends AppCompatTextView {
    public PTextView(Context context) {
        super(context);
        initView();
    }

    public PTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private Paint mPaintIn, mPaintOut; //画笔
    private RectF mRectIn, mRectOut; //矩形
    private int lineW, round, pL, pT, pR, pB; //描边宽度、背景角度、padding left...

    private void initView() {
        lineW = 8; //默认描边宽度
        round = 10; //默认背景角度

        mPaintIn = new Paint();
        mPaintIn.setAntiAlias(true); //抗锯齿
        mPaintIn.setDither(true); //防抖动、柔化线条
        mPaintIn.setStyle(Paint.Style.FILL);
        mPaintIn.setColor(Color.parseColor("#999999"));

        mPaintOut = new Paint();
        mPaintOut.setAntiAlias(true); //抗锯齿
        mPaintOut.setDither(true); //防抖动、柔化线条
        mPaintOut.setStyle(Paint.Style.FILL);
        mPaintOut.setColor(Color.parseColor("#ffffff"));

        mRectIn = new RectF();
        mRectOut = new RectF();

        setPadding(pL, pT, pR, pB);
        setHeight(100);
        setGravity();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        mRectOut.set(0, 0, w, h);
        mRectIn.set(lineW, lineW, w - lineW, h - lineW);
        //绘制背景,在绘制文字之前绘制
        canvas.drawRoundRect(mRectOut, round, round, mPaintOut);
        canvas.drawRoundRect(mRectIn, round, round, mPaintIn);
        super.onDraw(canvas);
    }
}
