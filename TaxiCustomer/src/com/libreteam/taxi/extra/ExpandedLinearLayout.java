package com.libreteam.taxi.extra;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ExpandedLinearLayout extends LinearLayout {
	@SuppressWarnings("unused")
	private static final String TAG = "ExpandedLinearLayout";
	public ExpandedLinearLayout(Context context) {
		super(context);
	}
	public ExpandedLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public ExpandedLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int numberOfChild = getChildCount();
		float totalWidth = 0;
		for(int i = 0;i < numberOfChild;i++){
			totalWidth+=getMeasuredWidth()
					*(1 - 2*((LinearLayout.LayoutParams)getChildAt(i).getLayoutParams()).weight);
		}
		setMeasuredDimension((int) totalWidth, getMeasuredHeight());
	}
}
