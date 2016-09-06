package com.parametris.iteng.asdf.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

public class ConversationTabLayout extends HorizontalScrollView {
    public interface TabColorizer {
        int getIndicatorColor(int position);
        int getDividerColor(int position);
    }

    private static final int TITLE_OFFSET_DIPS = 24;
    private static final int TAB_VIEW_PADDING_DIPS = 16;
    private static final int TAB_VIEW_TEXT_SIZE_SP = 18;

    private int titleOffset;
    private int tabViewLayoutId;
    private int tabViewTextViewId;

    private ViewPager viewPager;
    private ViewPager.OnPageChangeListener viewPagerChangeListener;

    private SlidingTabStrip slidingTabStrip;

    public ConversationTabLayout(Context context) {
        super(context, null);
    }

    public ConversationTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public ConversationTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setHorizontalScrollBarEnabled(false);
        setFillViewport(true);
        titleOffset = (int) (TITLE_OFFSET_DIPS * getResources().getDisplayMetrics().density);
        slidingTabStrip = new SlidingTabStrip(context);
        addView(slidingTabStrip, ViewGroup.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
    }
    
    public void update() {
    }

    public void setViewPagerChangeListener(ViewPager.OnPageChangeListener viewPagerChangeListener) {
        this.viewPagerChangeListener = viewPagerChangeListener;
    }

    public void setCustomTabView(int tabViewLayoutId, int tabViewTextViewId) {
        this.tabViewLayoutId = tabViewLayoutId;
        this.tabViewTextViewId = tabViewTextViewId;
    }

    public void setViewPager(ViewPager viewPager) {
        slidingTabStrip.removeAllViews();
        this.viewPager = viewPager;
        if (null == viewPager) {
            viewPager.setOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();
        }
    }

    protected TextView createDefaultTabView(Context context) {
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(0xFFFFFF);
        int padding = (int) (TAB_VIEW_PADDING_DIPS * getResources().getDisplayMetrics().density);
        textView.setPadding(padding, padding, padding, padding);
        return textView;
    }

    private void populateTabStrip() {
        final PagerAdapter pagerAdapter = viewPager.getAdapter();
        final View.OnClickListener onClickListener = new TabClickListener();

        for (int i = 0; i <= pagerAdapter.getCount(); i++) {
            View tabView = null;
            TextView textView = null;

            if (0 != tabViewLayoutId) {
                tabView = LayoutInflater.from(getContext()).inflate(tabViewLayoutId, slidingTabStrip, false);
                textView = (TextView) tabView.findViewById(tabViewTextViewId);
            }

            if (null == tabView) {
                tabView = createDefaultTabView(getContext());
            }

            if (null == textView && TextView.class.isInstance(tabView)) {
                textView = (TextView) tabView;
            }

            textView.setText(pagerAdapter.getPageTitle(i));
            tabView.setOnClickListener(onClickListener);

            slidingTabStrip.addView(tabView);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (null != viewPager) {
            scrollToTab(viewPager.getCurrentItem(), 0);
        }
    }

    private void scrollToTab(int tabIndex, int positionOffset) {
        final int tabStripChildCount = slidingTabStrip.getChildCount();
        if (0 == tabStripChildCount || 0 > tabIndex || tabIndex >= tabStripChildCount) {
            return;
        }

        View selectedChild = slidingTabStrip.getChildAt(tabIndex);
        if (null != selectedChild) {
            int targetScrollX = selectedChild.getLeft() + positionOffset;
            if (0 < tabIndex || 0 < positionOffset) {
                targetScrollX -= titleOffset;
            }

            scrollTo(targetScrollX, 0);
        }
    }

    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        private int scrollState;
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int tabStripCount = slidingTabStrip.getChildCount();
            if (0 == tabStripCount || 0 > position || position >= tabStripCount) {
                return;
            }

            View selectedTitle = slidingTabStrip.getChildAt(position);
            int extraOffset = 0;
            if (null != selectedTitle) {
                extraOffset = (int) (positionOffset * selectedTitle.getWidth());
            }
            scrollTo(position, extraOffset);
            if (null != viewPagerChangeListener) {
                viewPagerChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (ViewPager.SCROLL_STATE_IDLE == scrollState) {
                slidingTabStrip.onViewPagerPageChanged(position, 0);
            }
            if (null != viewPagerChangeListener) {
                viewPagerChangeListener.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            scrollState = state;
            if (null != viewPagerChangeListener) {
                viewPagerChangeListener.onPageScrollStateChanged(state);
            }
        }
    }

    private class TabClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            for (int i = 0; i < slidingTabStrip.getChildCount(); i++) {
                if (slidingTabStrip.getChildAt(i) == v) {
                    viewPager.setCurrentItem(i);
                    return;
                }
            }
        }
    }
}
