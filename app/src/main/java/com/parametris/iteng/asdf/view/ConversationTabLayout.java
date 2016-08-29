package com.parametris.iteng.asdf.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class ConversationTabLayout extends HorizontalScrollView {

    public interface TabColorizer {
        int getIndicatorColor(int position);
        int getDividerColor(int position);
    }

    public ConversationTabLayout(Context context) {
        super(context, null);
    }

    public ConversationTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public ConversationTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
