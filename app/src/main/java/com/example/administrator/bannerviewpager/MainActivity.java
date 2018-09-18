package com.example.administrator.bannerviewpager;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Interpolator;
import android.widget.Scroller;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewpager;
    private List<String> list = new ArrayList<>();
    private BannerAdapter bannerAdapter;
    private static final float SCALE = 0.85f;
    private Timer timer;
    private TimerTask timerTask;
    private int temp = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initData() {
        list.add("http://img2.imgtn.bdimg.com/it/u=2097124721,3074829049&fm=27&gp=0.jpg");
        list.add("http://img1.3lian.com/2015/w22/87/d/105.jpg");
        list.add("http://img4.imgtn.bdimg.com/it/u=1534542085,1971219363&fm=27&gp=0.jpg");
    }

    private void initView() {
        viewpager = findViewById(R.id.viewpager);
        bannerAdapter = new BannerAdapter(list , this);
        viewpager.setAdapter(bannerAdapter);
        viewpager.setPageTransformer(false , pageTransformer);
        viewpager.addOnPageChangeListener(onPageChangeListener);
        setViewPagerScroller(viewpager);
        viewpager.setCurrentItem(temp);
        startTimer();
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            temp = i;
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    private ViewPager.PageTransformer pageTransformer = new ViewPager.PageTransformer() {
        @Override
        public void transformPage(@NonNull View view, float position) {
            float v = getPositionConsiderPadding(viewpager, view);
            if (v >= -1 && v <= 1) {
                // [-1,1]，中间以及相邻的页面，一般相邻的才会用于计算动画
                float scale = SCALE + (1 - SCALE) * (1 - Math.abs(v));
                view.setScaleX(scale);
                view.setScaleY(scale);
            } else {
                // [-Infinity,-1)、(1,+Infinity]，超出相邻的范围
                view.setScaleX(SCALE);
                view.setScaleY(SCALE);
            }
        }
    };

    /**
     * padding影响了position，自己生成position
     */
    private float getPositionConsiderPadding(ViewPager viewPager, View page) {
        int clientWidth = viewPager.getMeasuredWidth() - viewPager.getPaddingLeft() - viewPager.getPaddingRight();
        return (float) (page.getLeft() - viewPager.getScrollX() - viewPager.getPaddingLeft()) / clientWidth;
    }

    /**
     * 设置viewpager自动滑动时，切换的时间
     */
    private void setViewPagerScroller(ViewPager viewpager) {
        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            Field interpolator = ViewPager.class.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);
            Scroller scroller = new Scroller(this, (Interpolator) interpolator.get(null)) {
                @Override
                public void startScroll(int startX, int startY, int dx, int dy, int duration) {
                    super.startScroll(startX, startY, dx, dy, duration * 4);    // 这里是关键，将duration变长或变短
                }
            };
            scrollerField.set(viewpager, scroller);
        } catch (Exception e) {
        }
    }

    private void startTimer(){
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                temp++;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewpager.setCurrentItem(temp);
                    }
                });
            }
        };
        timer.schedule(timerTask , 1000 , 3000);
    }
}
