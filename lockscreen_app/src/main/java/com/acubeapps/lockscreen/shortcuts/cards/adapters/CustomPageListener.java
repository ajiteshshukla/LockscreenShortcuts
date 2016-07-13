package com.acubeapps.lockscreen.shortcuts.cards.adapters;

import android.support.v4.view.ViewPager;

/**
 * Created by netra.shetty on 6/28/16.
 */
public class CustomPageListener implements ViewPager.OnPageChangeListener {

    private final ViewPager viewPager;
    private int pageCount;

    CustomPageListener(ViewPager viewPager, int pageCount) {
        this.viewPager = viewPager;
        this.pageCount = pageCount;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        /*if (position == pageCount-1){
            viewPager.setCurrentItem(0,false);
        }*/
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
