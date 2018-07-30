package com.chunlangjiu.app.store.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chunlangjiu.app.R;

/**
 * @CreatedbBy: liucun on 2018/7/30
 * @Describe: 酒庄图片
 */
public class PhotoFragment extends HeaderViewPagerFragment {


    private View scrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (scrollView == null) {
            scrollView = inflater.inflate(R.layout.store_fragment_photo, container, false);
            initView();
            initData();
        }
        return scrollView;
    }

    @Override
    public View getScrollableView() {
        return scrollView;
    }


    private void initView() {

    }

    private void initData() {

    }

}