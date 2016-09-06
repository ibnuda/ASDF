package com.parametris.iteng.asdf.view;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;

public class SlidingTabStrip extends LinearLayout {
    private static final int SELECTED_INDICATOR_THICKNESS_DIPS = 4;
    private static final int DEFAULT_SELECTED_INDICATOR_COLOR = 0xFF33B5E5;

    private static final int DEFAULT_DIVIDER_THICKNESS_DIPS = 1;
    private static final byte DEFAULT_DIVIDER_COLOR_ALPHA = 0x20;
    private static final float DEFAULT_DIVIDER_HEIGHT = 0.5f;


    public SlidingTabStrip(Context context) {
        this(context, null);
    }

    public SlidingTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorForeground, typedValue, false);
        final int themeForeground = typedValue.data;
    }

    void onViewPagerPageChanged(int position, float positionOffset) {

    }
}
