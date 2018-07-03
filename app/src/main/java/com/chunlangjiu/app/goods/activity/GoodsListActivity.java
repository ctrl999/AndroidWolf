package com.chunlangjiu.app.goods.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chunlangjiu.app.R;
import com.chunlangjiu.app.abase.BaseActivity;
import com.chunlangjiu.app.amain.bean.GoodsListInfoBean;
import com.pkqup.commonlibrary.util.SizeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @CreatedbBy: liucun on 2018/6/23.
 * @Describe:
 */
public class GoodsListActivity extends BaseActivity {

    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.right_view)
    LinearLayout rightView;

    @BindView(R.id.tv_all)
    TextView tv_all;
    @BindView(R.id.tv_new)
    TextView tv_new;
    @BindView(R.id.tv_price)
    TextView tv_price;
    @BindView(R.id.tv_class)
    TextView tv_class;
    @BindView(R.id.tv_filter)
    TextView tv_filter;

    @BindView(R.id.recycle_view)
    RecyclerView recycle_view;

    private boolean listType = true;//是否是列表形式
    private List<GoodsListInfoBean> lists;
    private LinearAdapter linearAdapter;
    private GridAdapter gridAdapter;


    private String secondClassId;
    private String secondClassName;

    public static void startGoodsListActivity(Activity activity, String secondClassId, String secondClassName) {
        Intent intent = new Intent(activity, GoodsListActivity.class);
        intent.putExtra("secondClassId", secondClassId);
        intent.putExtra("secondClassName", secondClassName);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goods_activity_list);
        initDrawerLayout();
        initView();
        initData();
    }

    private void initDrawerLayout() {
        drawerLayout.closeDrawer(Gravity.END);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
        ViewGroup.LayoutParams layoutParams = rightView.getLayoutParams();
        layoutParams.width = SizeUtils.getScreenWidth() - SizeUtils.dp2px(100);
        rightView.setLayoutParams(layoutParams);
    }

    @Override
    public void setTitleView() {
        titleImgRightTwo.setVisibility(View.VISIBLE);
        secondClassId = getIntent().getStringExtra("secondClassId");
        secondClassName = getIntent().getStringExtra("secondClassName");
        titleName.setText(secondClassName);
    }

    private void initView() {
        titleImgLeft.setOnClickListener(onClickListener);
        titleImgRightOne.setOnClickListener(onClickListener);
        titleImgRightTwo.setOnClickListener(onClickListener);
        tv_all.setOnClickListener(onClickListener);
        tv_new.setOnClickListener(onClickListener);
        tv_price.setOnClickListener(onClickListener);
        tv_class.setOnClickListener(onClickListener);
        tv_filter.setOnClickListener(onClickListener);

        lists = new ArrayList<>();
        linearAdapter = new LinearAdapter(R.layout.amain_item_goods_list_linear, lists);
        linearAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GoodsDetailsActivity.startGoodsDetailsActivity(GoodsListActivity.this,"");
            }
        });
        gridAdapter = new GridAdapter(R.layout.amain_item_goods_list_grid, lists);
        gridAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GoodsDetailsActivity.startGoodsDetailsActivity(GoodsListActivity.this,"");
            }
        });
        recycle_view.setLayoutManager(new LinearLayoutManager(this));
        recycle_view.setAdapter(linearAdapter);
    }

    private void initData() {
        lists.clear();
        for (int i = 0; i < 30; i++) {
            GoodsListInfoBean goodsListInfoBean = new GoodsListInfoBean();
            lists.add(goodsListInfoBean);
        }
        if (listType) {
            linearAdapter.setNewData(lists);
        } else {
            gridAdapter.setNewData(lists);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.img_title_left:
                    checkBack();
                    break;
                case R.id.img_title_right_one:
                    break;
                case R.id.img_title_right_two:
                    changeListType();
                    break;
                case R.id.tv_all:
                    break;
                case R.id.tv_new:
                    break;
                case R.id.tv_price:
                    break;
                case R.id.tv_class:
                    break;
                case R.id.tv_filter:
                    showDrawerLayout();
                    break;
            }
        }
    };


    private void changeListType() {
        if (listType) {
            //列表切换到网格
            listType = false;
            recycle_view.setLayoutManager(new GridLayoutManager(this, 2));
            recycle_view.setAdapter(gridAdapter);
        } else {
            //网格切换到列表
            listType = true;
            recycle_view.setLayoutManager(new LinearLayoutManager(this));
            recycle_view.setAdapter(linearAdapter);
        }
    }


    public class LinearAdapter extends BaseQuickAdapter<GoodsListInfoBean, BaseViewHolder> {
        public LinearAdapter(int layoutResId, List<GoodsListInfoBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, GoodsListInfoBean item) {
        }
    }

    public class GridAdapter extends BaseQuickAdapter<GoodsListInfoBean, BaseViewHolder> {
        public GridAdapter(int layoutResId, List<GoodsListInfoBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, GoodsListInfoBean item) {
            ImageView imaPic = helper.getView(R.id.img_pic);
            ViewGroup.LayoutParams layoutParams = imaPic.getLayoutParams();
            int picWidth = (SizeUtils.getScreenWidth() - SizeUtils.dp2px(40)) / 2;
            layoutParams.width = picWidth;
            layoutParams.height = picWidth;
            imaPic.setLayoutParams(layoutParams);
        }
    }


    private void checkBack() {
        if (drawerLayout.isDrawerOpen(Gravity.END)) {
            drawerLayout.closeDrawer(Gravity.END);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END);
        } else {
            finish();
        }
    }

    private void showDrawerLayout() {
        if (drawerLayout.isDrawerOpen(Gravity.END)) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END);
            drawerLayout.closeDrawer(Gravity.END);
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.END);
            drawerLayout.openDrawer(Gravity.END);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            checkBack();
            return true;
        }
        return true;
    }
}
