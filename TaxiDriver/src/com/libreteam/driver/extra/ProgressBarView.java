package com.libreteam.driver.extra;

import com.libreteam.driver.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


/**
 * This view shows a progress bar, either as a horizontal bar or as a vertical stack of bars.
 *
 */

public class ProgressBarView extends View {


    private static final int DEFAULT_BAR_PIXEL_HEIGHT = 16;
    private static final int DEFAULT_BAR_PIXEL_SPACING = 2;
    private static final int DEFAULT_NUM_DIVISIONS = 4;
    private static final int DEFAULT_INITIAL_VALUE = 0;

    public static final int VERTICAL_ORIENTATION = 1;
    public static final int HORIZONTAL_ORIENTATION = 0;
    private static final int DEFAULT_ORIENTATION = HORIZONTAL_ORIENTATION;

    Paint mPaint = new Paint();

/**
 */
// Constructors

public ProgressBarView (Context context) {
	super (context);
}
public ProgressBarView (Context context, AttributeSet attrs) {
	super (context, attrs);
   readAttrs (context, attrs);
   
}
public ProgressBarView (Context context, AttributeSet attrs, int style) {
	super (context, attrs, style);
   readAttrs (context, attrs);
}


/**
 */
// Properties

/* Property BarColor1 */
/**
 * This variable holds the value of the BarColor1 property.
 */

   private int pBarColor1 = Color.WHITE;

/**
 * Get the value of BarColor1, which is the color used to show progress completed.
 * With horizontal orientation, it is the left color. For vertical, the bottom color.
 * 
 * @return int
 */

public int getBarColor1 ()
{
   //if (pBarColor1 == null) {}
   return pBarColor1;
} // end getBarColor1

/**
 * Set the value of the BarColor1 property.
 * 
 * @param newValue int
 */

public void setBarColor1 (int newValue)
{
   pBarColor1 = newValue;
} // end setBarColor1
/* end Property BarColor1 */

/* Property BarColor2 */
/**
 * This variable holds the value of the BarColor2 property.
 */

   private int pBarColor2 = Color.GRAY;

/**
 * Get the value of BarColor2, which is the color used to show progress completed.
 * 
 * @return int
 */

public int getBarColor2 ()
{
   //if (pBarColor2 == null) {}
   return pBarColor2;
} // end getBarColor2

/**
 * Set the value of the BarColor2 property.
 * 
 * @param newValue int
 */

public void setBarColor2 (int newValue)
{
   pBarColor2 = newValue;
} // end setBarColor2
/* end Property BarColor2 */

/* Property BarHeight */
/**
 * This variable holds the value of the BarHeight property.
 */

   private int pBarHeight;

/**
 * Get the value of the BarHeight property.
 * 
 * @return int
 */

public int getBarHeight ()
{
   //if (pBarHeight == null) {}
   return pBarHeight;
} // end getBarHeight

/**
 * Set the value of the BarHeight property.
 * 
 * @param newValue int
 */

public void setBarHeight (int newValue)
{
   pBarHeight = newValue;
} // end setBarHeight
/* end Property BarHeight */

/* Property BarOrientation */
/**
 * This variable holds the value of the BarOrientation property.
 */

   private int pBarOrientation = HORIZONTAL_ORIENTATION;

/**
 * Get the orientation of the bars. For vertical orientation, the value is 1. Zero means horizontal.
 * 
 * @return int
 */

public int getBarOrientation ()
{
   //if (pBarOrientation == null) {}
   return pBarOrientation;
} // end getBarOrientation

/**
 * Set the value of the BarOrientation property.
 * 
 * @param newValue int
 */

public void setBarOrientation (int newValue)
{
   pBarOrientation = newValue;
} // end setBarOrientation
/* end Property BarOrientation */

/* Property BarSpacing */
/**
 * This variable holds the value of the BarSpacing property.
 */

   private int pBarSpacing = DEFAULT_BAR_PIXEL_SPACING;

/**
 * Get the value of the BarSpacing property.
 * 
 * @return int
 */

public int getBarSpacing ()
{
   //if (pBarSpacing == null) {}
   return pBarSpacing;
} // end getBarSpacing

/**
 * Set the value of the BarSpacing property.
 * 
 * @param newValue int
 */

public void setBarSpacing (int newValue)
{
   pBarSpacing = newValue;
} // end setBarSpacing
/* end Property BarSpacing */

/* Property NumDivisions */
/**
 * This variable holds the value of the NumDivisions property.
 */

   private int pNumDivisions;

/**
 * Get the number of divisions in the progress bar. The bar starts at 0 and progresses through the 
 * values 1..numDivisions.
 * 
 * @return int
 */

public int getNumDivisions ()
{
   //if (pNumDivisions == null) {}
   return pNumDivisions;
} // end getNumDivisions

/**
 * Set the value of the NumDivisions property.
 * 
 * @param newValue int
 */

public void setNumDivisions (int newValue)
{
   pNumDivisions = newValue;
} // end setNumDivisions
/* end Property NumDivisions */

/* Property Value */
/**
 * This variable holds the value of the Value property.
 */

   private int pValue = DEFAULT_INITIAL_VALUE;

/**
 * Return the current value of the bar. The value is in the range 0..num_divisions.
 * 
 * @return int
 */

public int getValue ()
{
   //if (pValue == null) {}
   return pValue;
} // end getValue

/**
 * Set the value of the Value property.
 * 
 * @param newValue int
 */

public void setValue (int newValue)
{
   pValue = newValue;
} // end setValue
/* end Property Value */

/**
 */
// Methods

/**
 * Draw progress bar oriented horizontally.
 */

void drawOnCanvasH (Canvas canvas) {
    int maxValue = getNumDivisions ();

    int numValue = getValue ();
    if (numValue > maxValue) numValue = maxValue;
    if (numValue < 0) numValue = 0;

    int bspace = getBarSpacing ();
    int customBarHeight = getBarHeight ();
    int numDivs = getNumDivisions ();
    if (numDivs <= 0) numDivs = 1;
    int color1 = getBarColor1 ();
    int color2 = getBarColor2 ();

    int vw = getWidth ();
    int vh = getHeight ();
    float vwf = (float) vw;
    float vhf = (float) vh;

    // Draw a row of bars. Use up the entire width of the view. 
    // Add spacing between the bars. Number of spaces is 1 less than the numDivs.
    float x = 0.0f + getPaddingLeft ();
    float y = 0.0f + getPaddingTop ();
    float barWidth = (vwf - (float) ((numDivs - 1) * bspace) - getPaddingLeft () - getPaddingRight ()) 
                   / (float) numDivs;
    float deltaX = barWidth + bspace;
    float deltaY = 0.0f;

    float barHeight  = vhf - getPaddingTop () - getPaddingBottom ();
    if (customBarHeight > 0) barHeight = customBarHeight;

    int rcolor = color1;
    // Draw N rectangles. Choose the color based on whether the current value
    // is larger than the index value for the rectangle.
    // The width of the bar is chosen to account for the space that gets added between rectangles.
    for (int j = 0; j < numDivs; j++) {
        if (j < numValue) rcolor = color1;
        else rcolor = color2;
        mPaint.setColor (rcolor);
        canvas.drawRect (x, y, x + barWidth, y + barHeight, mPaint);
        x += deltaX;
        y += deltaY;
    }

}

/**
 * Draw progress bar oriented vertically.
 */

void drawOnCanvasV (Canvas canvas) {
    int maxValue = getNumDivisions ();

    int numValue = getValue ();
    if (numValue > maxValue) numValue = maxValue;
    if (numValue < 0) numValue = 0;

    int bspace = getBarSpacing ();
    int customBarHeight = getBarHeight ();
    int numDivs = getNumDivisions ();
    if (numDivs <= 0) numDivs = 1;
    int color1 = getBarColor1 ();
    int color2 = getBarColor2 ();

    int vw = getWidth ();
    int vh = getHeight ();
    float vwf = (float) vw;
    float vhf = (float) vh;

    // Draw a stack of bars. Use up the entire height of the view. 
    // Add spacing between the bars. Number of spaces is 1 less than the numDivs.
    float x = 0.0f + getPaddingLeft ();
    float y = 0.0f + getPaddingTop ();
    float barHeight = (vhf - (float) ((numDivs - 1) * bspace) - getPaddingTop () - getPaddingBottom ()) 
                   / (float) numDivs;
    float deltaX = 0.0f;
    float deltaY = (barHeight + bspace);
    if (customBarHeight > 0) barHeight = customBarHeight;
    float barWidth  = vwf - getPaddingLeft () - getPaddingRight ();

    int rcolor = color1;
    // Draw N rectangles. Choose the color based on whether the current value
    // is larger than the index value for the rectangle.
    for (int j = 0; j < numDivs; j++) {
        if (j < numValue) rcolor = color2;
        else rcolor = color1;
        mPaint.setColor (rcolor);
        canvas.drawRect (x, y, x + barWidth, y + barHeight, mPaint);
        x += deltaX;
        y += deltaY;
    }

}


/**
 * onDraw
 */

@Override protected void onDraw(Canvas canvas) {
    int orient = getBarOrientation ();

    if (orient == VERTICAL_ORIENTATION) drawOnCanvasV (canvas);
    else drawOnCanvasH (canvas);
}

/**
 * Read the attribute set and set view variables.
 * 
 * @param attrs AttributeSet
 * @return void
 */

private void readAttrs (Context context, AttributeSet attrs) {
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressBarView);
 
    final int N = a.getIndexCount();
    for (int i = 0; i < N; ++i) {
        int attr = a.getIndex(i);
        switch (attr) {
        case R.styleable.ProgressBarView_bar_color1:
            int c = a.getColor (attr, Color.WHITE);
            setBarColor1 (c);
            break;
        case R.styleable.ProgressBarView_bar_color2:
            int c2 = a.getColor (attr, Color.GRAY);
            setBarColor2 (c2);
            break;
        case R.styleable.ProgressBarView_bar_initial_value:
            int val = a.getInt (attr, 0);
            setValue (val);
            break;
        case R.styleable.ProgressBarView_bar_num_divisions:
            int numdiv = a.getInt (attr, DEFAULT_NUM_DIVISIONS);
            setNumDivisions (numdiv);
            break;

        case R.styleable.ProgressBarView_bar_height:
            int bh = a.getDimensionPixelSize (attr, DEFAULT_BAR_PIXEL_HEIGHT);
            setBarHeight (bh);
            break;

        case R.styleable.ProgressBarView_bar_spacing:
            int bspace = a.getDimensionPixelSize (attr, DEFAULT_BAR_PIXEL_SPACING);
            setBarSpacing (bspace);
            break;

        case R.styleable.ProgressBarView_bar_orientation:
            int orient = a.getInt (attr, DEFAULT_ORIENTATION);
            setBarOrientation (orient);
            break;
        }
    }
    a.recycle();
}

/**
 * Set the value for the progress bar, given values for the min, max, and current values.
 * This results in the value being set to an integer value in the range 0..numDivisions.
 *
 * <p> This method is provided for convenience for the situations where a float number is being
 * displayed.
 * 
 * @param val float
 * @param minVal float
 * @param maxVal float
 * @return void
 */

public void setValue (float val, float minVal, float maxVal) {
    float frange = maxVal - minVal;
    int ival = 0;
    int numDivs = getNumDivisions ();
    if (frange > 0.0f) {
       ival = (int) Math.round ((val - minVal) / frange * (float) numDivs);
       if (ival < 0) ival = 0;
       else if (ival > numDivs) ival = numDivs;
    }
    setValue (ival);
}

} // end class ProgressBarView
