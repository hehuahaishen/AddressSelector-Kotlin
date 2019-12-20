package com.shen.library.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.recyclerview.widget.RecyclerView;

/**
 *
 */
public class UninterceptableRecyclerView extends RecyclerView {
    public UninterceptableRecyclerView(Context context) {
        super(context);
    }

    public UninterceptableRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UninterceptableRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onTouchEvent(ev);
    }
}
