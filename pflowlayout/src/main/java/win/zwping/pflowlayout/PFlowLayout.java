package win.zwping.pflowlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>describe：
 * <p>    note：
 * <p>  author：zwp on 2017/9/19 0019 mail：1101558280@qq.com web: http://www.zwping.win </p>
 */
public class PFlowLayout extends ViewGroup {

    public PFlowLayout(Context context) {
        super(context);
        init();
    }

    public PFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PFlowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private int rowW = 20;
    private int columnH = 50; //行之间的宽度、列之间的高度

    /*设置该容器的宽高*/
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 获得父容器的测量模式和大小
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        /*测量该容器的宽高*/
        int width = 0;
        int height = 0;

        /*该容器中每一行的宽高*/
        int lineWidth = 0;
        int lineHeight = 0;

        /*子布局的数量*/
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            /*测量获取子布局的宽高及对应参数*/
            setViewLayoutParams(child, 0, (0 == height ? 0 : columnH), 0, 0); //给非第一行的子控件设置top
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            /*根据迭加lineWidth增加高度*/
            if (lineWidth + childWidth + rowW > sizeWidth) {
                setViewLayoutParams(child, 0, (1 == cCount ? 0 : columnH), 0, 0); //给换行的第一个设置top
                width = Math.max(lineWidth, childWidth);
                lineWidth = childWidth;
                height += childHeight;
                lineHeight = childHeight;
            } else {
                setViewLayoutParams(child, (0 == i ? 0 : rowW), lp.topMargin, 0, 0);
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }

            if (i == cCount - 1) { //最后一个子控件了
                width = Math.max(width, lineWidth);
                if (0 != i) height += lineHeight;
            }
        }
        /*设置该容器的宽高*/
        setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth : width + getPaddingLeft() + getPaddingRight(), //如果父布局为match_parent
                (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight : height + getPaddingTop() + getPaddingBottom());
    }

    private void init() {
        for (int i = 0; i < 100; i++) {
            PTextView textView = new PTextView(getContext());
            textView.setLayoutParams(new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            textView.setText("" + i + "");
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setSelected(!view.isSelected());
                    Toast.makeText(getContext(), "---" + view.isSelected(), Toast.LENGTH_SHORT).show();
                }
            });
            addView(textView);

        }
    }

    /*按行记录所有子控件*/
    private List<List<View>> mAllViews = new ArrayList<List<View>>();
    /*行的高度*/
    private List<Integer> mLineHeight = new ArrayList<Integer>();
    /*行的宽度*/
    private List<Integer> mLineWidth = new ArrayList<>();

    private void setViewLayoutParams(View view, int left, int top, int right, int bottom) {
        MarginLayoutParams lp = (MarginLayoutParams) view.getLayoutParams();
        lp.setMargins(left, top, right, bottom);
        view.setLayoutParams(lp);
    }

    private static final int LEFT = -1;
    private static final int CENTER = 0;
    private static final int RIGHT = 1;
    private int mGravity = LEFT;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeight.clear();
        mLineWidth.clear();

        /*该容器宽度*/
        int width = getWidth();

        /*每一行的宽高*/
        int lineWidth = 0;
        int lineHeight = 0;

        /*每一行的view集合*/
        List<View> lineViews = new ArrayList<View>();

        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            /*子控件的宽高及对应参数*/
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if (childWidth + lp.leftMargin + lp.rightMargin + lineWidth > width) { //换行
                mLineHeight.add(lineHeight);
                mLineWidth.add(lineWidth + lp.leftMargin + lp.rightMargin);
                mAllViews.add(lineViews);
                lineWidth = 0;
                lineViews = new ArrayList<View>();
            }
            lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin);
            lineViews.add(child);
        }
        mLineWidth.add(lineWidth);
        mLineHeight.add(lineHeight);
        mAllViews.add(lineViews);


        int left = 0;
        int top = 0;
        int lineNums = mAllViews.size();
        for (int i = 0; i < lineNums; i++) {
            lineViews = mAllViews.get(i);
            lineHeight = mLineHeight.get(i);

            // set gravity
            int currentLineWidth = this.mLineWidth.get(i);
            switch (this.mGravity) {
                case LEFT:
                    left = getPaddingLeft();
                    break;
                case CENTER:
                    left = (width - currentLineWidth) / 2 + getPaddingLeft();
                    break;
                case RIGHT:
                    left = width - currentLineWidth + getPaddingLeft();
                    break;
            }
            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                if (child.getVisibility() == View.GONE) continue;
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth() + lp.rightMargin;
                int bc = tc + child.getMeasuredHeight() + lp.bottomMargin;

                child.layout(lc, tc, rc, bc);
                left += child.getMeasuredWidth() + lp.rightMargin
                        + lp.leftMargin;
            }
            left = 0;
            top += lineHeight;
        }
    }


    /*设置该容器measureSpec（貌似无用），获取子布局的宽高*/
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }
}
