package com.chunlangjiu.app.amain.fragment;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chunlangjiu.app.R;
import com.chunlangjiu.app.abase.BaseFragment;
import com.chunlangjiu.app.amain.activity.CityListActvity;
import com.chunlangjiu.app.amain.bean.RealmBean;
import com.chunlangjiu.app.user.activity.AddAddressActivity;
import com.chunlangjiu.app.user.activity.AddressListActivity;
import com.pkqup.commonlibrary.glide.BannerGlideLoader;
import com.pkqup.commonlibrary.net.bean.ResultBean;
import com.pkqup.commonlibrary.realm.RealmUtils;
import com.pkqup.commonlibrary.util.ToastUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.transformer.AccordionTransformer;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;
import com.zaaach.citypicker.model.LocateState;
import com.zaaach.citypicker.model.LocatedCity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @CreatedbBy: liucun on 2018/6/16.
 * @Describe: 首页
 */
public class HomeFragment extends BaseFragment {

    String[] images =
            new String[]{"http://img.my.csdn.net/uploads/201407/26/1406383299_1976.jpg",
                    "http://img.my.csdn.net/uploads/201407/26/1406383291_6518.jpg",
                    "http://img.my.csdn.net/uploads/201407/26/1406383291_8239.jpg",
                    "http://img.my.csdn.net/uploads/201407/26/1406383290_9329.jpg",
                    "http://img.my.csdn.net/uploads/201407/26/1406383290_1042.jpg",
                    "http://img.my.csdn.net/uploads/201407/26/1406383275_3977.jpg",
                    "http://img.my.csdn.net/uploads/201407/26/1406383265_8550.jpg",
                    "http://img.my.csdn.net/uploads/201407/26/1406383264_3954.jpg",
                    "http://img.my.csdn.net/uploads/201407/26/1406383264_4787.jpg",
                    "http://img.my.csdn.net/uploads/201407/26/1406383264_8243.jpg",
                    "http://img.my.csdn.net/uploads/201407/26/1406383248_3693.jpg",};

    private static final int CHOICE_CITY = 10001;

    private TextView tvCity;
    private RelativeLayout rlTitleSearch;

    private View headerView;
    private Banner banner;
    private LinearLayout indicator;
    private List<ImageView> imageViews;
    private List<String> bannerUrls;

    private RefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private List lists;
    private HomeAdapter homeAdapter;

    private List<HotCity> hotCities;
    private List<City> cityList;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tvCity:
                    choiceCity();
                    break;
                case R.id.rlTitleSearch:
                    break;
            }
        }
    };


    @Override
    public void getContentView(LayoutInflater inflater, ViewGroup container) {
        inflater.inflate(R.layout.amain_fragment_home, container, true);
    }

    @Override
    public void initView() {
        headerView = View.inflate(getActivity(), R.layout.amain_item_home_header, null);
        banner = headerView.findViewById(R.id.banner);
        indicator = headerView.findViewById(R.id.indicator);
        tvCity = rootView.findViewById(R.id.tvCity);
        tvCity.setOnClickListener(onClickListener);
        rlTitleSearch = rootView.findViewById(R.id.rlTitleSearch);
        rlTitleSearch.setOnClickListener(onClickListener);
        refreshLayout = rootView.findViewById(R.id.refreshLayout);
        recyclerView = rootView.findViewById(R.id.listView);
        imageViews = new ArrayList<>();
        initBannerView();
        initBannerIndicator();
        initRecyclerView();
    }


    private void initBannerView() {
        bannerUrls = new ArrayList<>();
        bannerUrls.add(images[0]);
        bannerUrls.add(images[1]);
        bannerUrls.add(images[2]);

        banner.setImages(bannerUrls)
                .setImageLoader(new BannerGlideLoader())
                .setBannerStyle(BannerConfig.NOT_INDICATOR)//去掉自带的indicator
                .setBannerAnimation(AccordionTransformer.class)
                .isAutoPlay(true)
                .setDelayTime(4000)
                .start();
    }


    private void initBannerIndicator() {
        imageViews.clear();
        indicator.removeAllViews();
        for (int i = 0; i < bannerUrls.size(); i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 8;
            params.rightMargin = 8;
            if (i == 0) {
                imageView.setImageResource(R.drawable.banner_select);
            } else {
                imageView.setImageResource(R.drawable.banner_unselect);
            }
            imageViews.add(imageView);
            indicator.addView(imageView, params);
        }
        banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < imageViews.size(); i++) {
                    if (position == i) {
                        imageViews.get(i).setImageResource(R.drawable.banner_select);
                    } else {
                        imageViews.get(i).setImageResource(R.drawable.banner_unselect);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //设置banner点击事件
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                ToastUtils.showShort("点击了item" + position);
            }
        });
    }


    private void initRecyclerView() {
        lists = new ArrayList();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        homeAdapter = new HomeAdapter(R.layout.amain_item_home_list, lists);
        homeAdapter.addHeaderView(headerView);
        recyclerView.setAdapter(homeAdapter);

        refreshLayout.setEnableAutoLoadMore(true);//开启滑到底部自动加载
//        refreshLayout.autoRefresh();       //触发自动刷新
        refreshLayout.setFooterHeight(0);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshLaut) {
                refreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lists.clear();
                        lists.addAll(getData());
                        homeAdapter.setNewData(lists);
                        refreshLayout.finishRefresh();
                        refreshLayout.setNoMoreData(false);
                    }
                }, 1000);
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final RefreshLayout refreshLaut) {
                refreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishLoadMore();
                        lists.addAll(getData());
                        homeAdapter.setNewData(lists);
                        if (lists.size() > 50) {
                            recyclerView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    refreshLayout.setFooterHeight(30);
                                    refreshLayout.finishLoadMoreWithNoMoreData();//显示没有更多数据
                                }
                            }, 1000);

                        }
                    }
                }, 1000);
            }
        });


    }

    @Override
    public void initData() {
        RealmBean realmBean = new RealmBean();
        realmBean.setAge("10");
        realmBean.setName("名字");
        RealmUtils.add(realmBean);
    }


    private void choiceCity() {
        hotCities = new ArrayList<>();
        hotCities.add(new HotCity("北京", "北京", "101010100"));
        hotCities.add(new HotCity("上海", "上海", "101020100"));
        hotCities.add(new HotCity("广州", "广东", "101280101"));
        hotCities.add(new HotCity("深圳", "广东", "101280601"));
        hotCities.add(new HotCity("杭州", "浙江", "101210101"));

        cityList = new ArrayList<>();
        cityList.add(new City("广州", "", "guangzhou", "101010100"));
        cityList.add(new City("北京", "北京", "beijing", "101010100"));
        cityList.add(new City("安定", "", "anding", "101010100"));
        cityList.add(new City("重庆", "", "chongqing", "101010100"));
        cityList.add(new City("上海", "上海", "shanghai", "101010100"));
        cityList.add(new City("武汉", "", "wuhan", "101010100"));
        Collections.sort(cityList, new CityComparator());

        CityPicker.getInstance()
                .setFragmentManager(getActivity().getSupportFragmentManager())
                .setLocatedCity(null)
                .setHotCities(hotCities)
                .setCityLists(cityList)
                .setOnPickListener(new OnPickListener() {
                    @Override
                    public void onPick(int position, City data) {
                        if (data != null) {
                            Toast.makeText(
                                    getActivity(),
                                    String.format("点击的数据：%s，%s", data.getName(), data.getCode()),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }

                    @Override
                    public void onLocate() {
                        //开始定位，这里模拟一下定位
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                CityPicker.getInstance().locateComplete(new LocatedCity("深圳", "广东", "101280601"), LocateState.SUCCESS);
                            }
                        }, 3000);
                    }
                })
                .show();
    }

    /**
     * sort by a-z
     */
    private class CityComparator implements Comparator<City> {
        @Override
        public int compare(City lhs, City rhs) {
            String a = lhs.getPinyin().substring(0, 1);
            String b = rhs.getPinyin().substring(0, 1);
            return a.compareTo(b);
        }
    }

    public class HomeAdapter extends BaseQuickAdapter<ResultBean, BaseViewHolder> {

        public HomeAdapter(int layoutResId, List<ResultBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, ResultBean item) {
            helper.setText(R.id.tv_name, helper.getAdapterPosition() + "");
        }
    }

    private List getData() {
        return Arrays.asList(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        banner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        banner.stopAutoPlay();
    }


}
