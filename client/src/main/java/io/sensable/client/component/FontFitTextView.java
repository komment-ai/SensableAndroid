package io.sensable.client.component;

/**
 * Extremely useful class found at:
 * http://stackoverflow.com/questions/2617266/how-to-adjust-text-font-size-to-fit-textview
 */
import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * is an extension of the TextView class that adjusts the font size to fit the text
 * within a specified width. The class has several methods for resizing the font,
 * including initialising the paint object, measuring the text size, and refitting
 * the text when the width changes. These methods allow the text to be resized to fit
 * within the available width while maintaining a consistent font size.
 */
public class FontFitTextView extends TextView {

    public FontFitTextView(Context context) {
        super(context);
        initialise();
    }

    public FontFitTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise();
    }

    /**
     * initializes a new `Paint` object and sets its properties to match those of the
     * `Paint` object associated with the current component. It also sets the maximum
     * size of the `Paint` object based on the initially specified text size, unless it
     * is too small.
     */
    private void initialise() {
        mTestPaint = new Paint();
        mTestPaint.set(this.getPaint());
        //max size defaults to the initially specified text size unless it is too small
    }

    /* Re size the font so the specified text fits in the text box
     * assuming the text box is the specified width.
     */
    /**
     * adjusts the text size based on its width and padding to fit within a specified
     * width while maintaining a consistent aspect ratio.
     * 
     * @param text text to be repositioned within the specified width.
     * 
     * @param textWidth width of the text that the function is repositioning, and it is
     * used to determine the appropriate size for the text so that it fits within the
     * available space.
     */
    private void refitText(String text, int textWidth)
    {
        if (textWidth <= 0)
            return;
        int targetWidth = textWidth - this.getPaddingLeft() - this.getPaddingRight();
        float hi = 100;
        float lo = 2;
        final float threshold = 0.5f; // How close we have to be

        mTestPaint.set(this.getPaint());

        while((hi - lo) > threshold) {
            float size = (hi+lo)/2;
            mTestPaint.setTextSize(size);
            if(mTestPaint.measureText(text) >= targetWidth)
                hi = size; // too big
            else
                lo = size; // too small
        }
        // Use lo so that we undershoot rather than overshoot
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX, lo);
    }

    /**
     * recalculates the text dimensions when its width or height changes and updates the
     * measured dimensions of the view.
     * 
     * @param widthMeasureSpec width size of the component that the `onMeasure()` method
     * is being called for, and is used to determine the width of the component's layout
     * space.
     * 
     * @param heightMeasureSpec specifier for the desired height of the view.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int height = getMeasuredHeight();
        refitText(this.getText().toString(), parentWidth);
        this.setMeasuredDimension(parentWidth, height);
    }

    /**
     * recalculates the width of a view when its text changes, and then updates the text
     * to fit within that width.
     * 
     * @param text modified text value that triggered the onTextChanged() event.
     * 
     * 	- `text`: The changed text value, which is a `CharSequence`.
     * 	- `start`: The position where the change occurred in the original text, represented
     * as an integer.
     * 	- `before`: The previous value of the text at the specified position, also
     * represented as an integer.
     * 	- `after`: The new value of the text at the specified position, again represented
     * as an integer.
     * 
     * @param start 0-based index of the first character in the text that has changed,
     * indicating where the `onTextChanged()` method should start recalculating the width
     * of the text component.
     * 
     * @param before number of characters that were present in the text before the specified
     * range.
     * 
     * @param after number of characters that have been added or removed from the text
     * after the last call to `refitText()`.
     */
    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        refitText(text.toString(), this.getWidth());
    }

    /**
     * updates the text size based on the view's new width and height, if they have changed
     * since the last update.
     * 
     * @param w width of the component, which is used to determine whether the text needs
     * to be refitted.
     * 
     * @param h height of the viewport, which is used to determine if the text needs to
     * be refitted.
     * 
     * @param oldw previous width of the view component.
     * 
     * @param oldh previous height of the view component.
     */
    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh) {
        if (w != oldw) {
            refitText(this.getText().toString(), w);
        }
    }

    //Attributes
    private Paint mTestPaint;
}