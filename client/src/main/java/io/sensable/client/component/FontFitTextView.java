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
 * Is an extension of the TextView class that adjusts the font size to fit the text
 * within a specified width while maintaining a consistent aspect ratio. It uses a
 * `Paint` object to measure and resize the text, recalculating the dimensions when
 * its width or height changes. The class also updates the text size based on the
 * view's new width and height.
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
     * Initializes a private variable `mTestPaint` with a new instance of `Paint`. It
     * then sets the properties of the new paint to match those of an existing `Paint`
     * object retrieved from the current context using `getPaint()`.
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
     * Adjusts the text size to fit within a given width while taking into account left
     * and right padding. It iteratively narrows down the range of possible sizes by
     * measuring the text's width at each midpoint until it converges on the correct size.
     *
     * @param text string whose font size is to be adjusted to fit within the specified
     * width.
     *
     * @param textWidth width of the available space for rendering the text, from which
     * the optimal font size is calculated to fit the given text within that width.
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
     * Adjusts the size of a view based on its content and available space. It first calls
     * the superclass's `onMeasure` method to set up the measurement process, then retrieves
     * the parent's width and updates the view's height based on the text content.
     *
     * @param widthMeasureSpec measure specification for the width of the view, which
     * specifies how much space is available for the view to occupy.
     *
     * @param heightMeasureSpec measure specification for the view's height, which is
     * used to determine the actual height of the view.
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
     * Overrides a method to respond to changes in the text of an input field. It takes
     * four parameters: the changed text, and the start and end positions of the change,
     * and updates the layout based on the new text.
     *
     * @param text character sequence that is being changed within the text field.
     *
     * @param start 0-based index of the first character changed during the text modification
     * process.
     *
     * @param before number of characters being removed from the text at the specified
     * start position before making changes to it.
     *
     * @param after number of characters added to the text at the specified position.
     */
    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        refitText(text.toString(), this.getWidth());
    }

    /**
     * Is overridden to handle changes in width. It checks if the new width differs from
     * the previous one, and if so, calls the `refitText` method with the current text
     * and new width as arguments to adjust the text according to the changed width.
     *
     * @param w new width of the view, which is compared with its previous width (`oldw`)
     * to determine if a refitting of text is necessary.
     *
     * @param h height of the view, which is not being used in this method.
     *
     * @param oldw width of the view before it was resized, allowing the method to detect
     * whether the width has changed since the last time the onSizeChanged method was called.
     *
     * @param oldh previous height of the view, which is used for comparison with the
     * current height (`h`) to determine if there has been a change in the view's size.
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