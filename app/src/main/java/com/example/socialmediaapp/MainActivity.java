package com.example.socialmediaapp;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // KHÔNG padding bottom ở layout chính (chỉ padding top/left/right nếu muốn)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0); // 👈 ĐÂY là điểm quan trọng
            return insets;
        });

        // CHỈ padding bottom cho BottomNavigationView
        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView, (v, insets) -> {
            int bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemGestures()).bottom;
            v.setPadding(0, 0, 0, bottomInset);
            return insets;
        });

        // Thiết lập Adapter cho ViewPager2
        FragmentStateAdapter pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Khi người dùng chọn mục trong BottomNavigationView, ViewPager2 sẽ chuyển tới trang tương ứng
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int position = 0;
            if (item.getItemId() == R.id.navigation_home) {
                position = 0;
            } else if (item.getItemId() == R.id.navigation_search) {
                position = 1;
            } else if (item.getItemId() == R.id.navigation_profile) {
                position = 2;
            }
            viewPager.setCurrentItem(position);
            return true;
        });

        // Cập nhật BottomNavigationView khi người dùng swipe giữa các trang
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.navigation_search);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
                        break;
                }
            }
        });
    }

    private static class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(AppCompatActivity activity) {
            super(activity);
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new fragment_home();
                case 1:
                    return new fragment_friend();
                case 2:
                    return new fragment_profile();
                default:
                    return new fragment_home();
            }
        }

        @Override
        public int getItemCount() {
            return 3; // Số lượng fragment trong ViewPager2
        }
    }
}