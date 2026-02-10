package com.project.mobile.viewComponent;



import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.RecyclerView;

public class MaxHeightRecyclerView extends RecyclerView {

    private int maxHeightPx = 0;

    public MaxHeightRecyclerView(Context context) {
        super(context);
    }

    public MaxHeightRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxHeightRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setMaxHeightDp(int maxHeightDp) {
        maxHeightPx = (int) (maxHeightDp * getResources().getDisplayMetrics().density);
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int cappedHeightSpec = heightSpec;

        if (maxHeightPx > 0) {
            cappedHeightSpec = MeasureSpec.makeMeasureSpec(maxHeightPx, MeasureSpec.AT_MOST);
        }

        super.onMeasure(widthSpec, cappedHeightSpec);
    }
}