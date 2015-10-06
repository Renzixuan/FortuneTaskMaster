package com.example.lilyren.myapplication.Activities;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by lilyren on 15-09-28.
 */
public class MyItemDecoration extends RecyclerView.ItemDecoration{
    private Paint myPaint;
    private Drawable mDivider;
    private static final int [] ATTRS = new int[]{android.R.attr.listDivider};
    private int mVerticalSpaceHeight;

    public MyItemDecoration(int mVerticalHeight, Context context){
        myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        myPaint.setColor(Color.LTGRAY);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(1);

        this.mVerticalSpaceHeight = mVerticalHeight;

        final TypedArray styledAttr = context.obtainStyledAttributes(ATTRS);
        mDivider = styledAttr.getDrawable(0);
        styledAttr.recycle();
    }

    @Override
    public void getItemOffsets (Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top= mVerticalSpaceHeight;
    }

    @Override
    public void onDraw (Canvas c, RecyclerView parent, RecyclerView.State state){
        super.onDraw(c,parent,state);
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++){
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            //int bottom = top + mDivider.getIntrinsicHeight();
            int bottom = top + 3;
            mDivider.setBounds(left,top,right,bottom);
            mDivider.setColorFilter(Color.LTGRAY, PorterDuff.Mode.ADD);
            mDivider.draw(c);
        }
    }

}
