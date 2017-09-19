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
    private int lineW, round; //描边宽度、背景角度
    private int[] tP = new int[4];

    private void initView() {
        setGravity(Gravity.CENTER);
//        setLines(1);
//        setEllipsize(TextUtils.TruncateAt.END);
        lineW = 2; //默认描边宽度
        round = 10; //默认背景角度
        tP = new int[]{40, 20, 40, 20};

        mPaintIn = new Paint();
        mPaintIn.setAntiAlias(true); //抗锯齿
        mPaintIn.setDither(true); //防抖动、柔化线条
        mPaintIn.setStyle(Paint.Style.FILL);
        mPaintIn.setColor(Color.parseColor("#999999"));

        mPaintOut = new Paint();
        mPaintOut.setAntiAlias(true); //抗锯齿
        mPaintOut.setDither(true); //防抖动、柔化线条
        mPaintOut.setStyle(Paint.Style.STROKE);
        mPaintOut.setColor(Color.parseColor("#e0e0e0"));

        mRectIn = new RectF();
        mRectOut = new RectF();

        setPadding(tP[0], tP[1], tP[2], tP[3]);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        mRectIn.set(0, 0, w, h);
        mRectOut.set(0, 0, w, h);
        //绘制背景,在绘制文字之前绘制
        canvas.drawRoundRect(mRectOut, round, round, mPaintOut);
        canvas.drawRoundRect(mRectIn, round, round, mPaintIn);
        super.onDraw(canvas);
    }
}
