package com.example.animalapp.Adapters;

import androidx.fragment.app.Fragment;

public interface ScreenSlidePagerAdapter {

    Fragment getItem(int position);

    int getCount();
}
