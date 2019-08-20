package com.glong.demo;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.glong.demo.entry.HomePlanetBean;
import com.glong.demo.kotlinview.StarChildView;
import com.glong.demo.kotlinview.StarGroupViewKotlin;

import java.util.ArrayList;

/**
 * @author guolong
 * @since 2019/8/21
 */
public class LandActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isDemo = getIntent().getBooleanExtra("isDemo", true);
        if (isDemo) {
            setContentView(R.layout.activity_land);
            View sunView = findViewById(R.id.sun_view);
            sunView.startAnimation((AnimationUtils.loadAnimation(this, R.anim.sun_anim)));
        } else {
            setContentView(R.layout.activity_land_star);
            initData();
        }
    }

    private void initData() {
        ArrayList<HomePlanetBean> result = new ArrayList<>();
        HomePlanetBean moon = new HomePlanetBean("月球", R.drawable.planet_yueqiu_mormal, R.drawable.planet_yueqiu_activated,
                500, true, true);
        HomePlanetBean shui = new HomePlanetBean("水星", R.drawable.planet_shuixing_normal, R.drawable.planet_shuixing_activated,
                1000, true, true);
        HomePlanetBean jin = new HomePlanetBean("金星", R.drawable.planet_jinxing_normal, R.drawable.planet_jinxing_activated,
                1500, true, true);
        HomePlanetBean earth = new HomePlanetBean("地球", R.drawable.planet_diqiu_normal, R.drawable.planet_diqiu_activated,
                2000, true, true);
        HomePlanetBean fire = new HomePlanetBean("火星", R.drawable.planet_huoxing_normal, R.drawable.planet_huoxing_activated,
                2500, true, true);
        HomePlanetBean wood = new HomePlanetBean("木星", R.drawable.planet_muxing_normal, R.drawable.planet_muxing_activated,
                3000, false, true);
        HomePlanetBean soil = new HomePlanetBean("土星", R.drawable.planet_tuxing_normal, R.drawable.planet_tuxing_activated,
                3500, true, true);
        HomePlanetBean gold = new HomePlanetBean("天王星", R.drawable.planet_tianwangxing_normal, R.drawable.planet_tianwangxing_activated,
                4000, true, true);
        HomePlanetBean ocean = new HomePlanetBean("海王星", R.drawable.planet_haiwangxing_normal, R.drawable.planet_haiwagnxing_activated,
                4500, false, false);
        result.add(moon);
        result.add(shui);
        result.add(jin);
        result.add(earth);
        result.add(fire);
        result.add(wood);
        result.add(soil);
        result.add(gold);
        result.add(ocean);

        StarGroupViewKotlin starGroupView = findViewById(R.id.starGroupView);
        for (int i = 0; i < result.size(); i++) {
            StarChildView child = new StarChildView(result.get(i), this);
            starGroupView.addView(child, new FrameLayout.LayoutParams(-2, -2));
        }
        starGroupView.requestLayout();
        starGroupView.start();
    }
}
