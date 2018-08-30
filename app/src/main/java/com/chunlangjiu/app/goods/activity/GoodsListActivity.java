package com.chunlangjiu.app.goods.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chunlangjiu.app.R;
import com.chunlangjiu.app.abase.BaseActivity;
import com.chunlangjiu.app.amain.bean.FirstClassBean;
import com.chunlangjiu.app.amain.bean.MainClassBean;
import com.chunlangjiu.app.amain.bean.ThirdClassBean;
import com.chunlangjiu.app.goods.adapter.FilterBrandAdapter;
import com.chunlangjiu.app.goods.adapter.FilterStoreAdapter;
import com.chunlangjiu.app.goods.bean.ClassBean;
import com.chunlangjiu.app.goods.bean.FilterBrandBean;
import com.chunlangjiu.app.goods.bean.FilterListBean;
import com.chunlangjiu.app.goods.bean.FilterStoreBean;
import com.chunlangjiu.app.goods.bean.GoodsListBean;
import com.chunlangjiu.app.goods.bean.GoodsListDetailBean;
import com.chunlangjiu.app.goods.dialog.ClassPopWindow;
import com.chunlangjiu.app.net.ApiUtils;
import com.pkqup.commonlibrary.glide.GlideUtils;
import com.pkqup.commonlibrary.net.bean.ResultBean;
import com.pkqup.commonlibrary.net.exception.ApiException;
import com.pkqup.commonlibrary.util.KeyBoardUtils;
import com.pkqup.commonlibrary.util.SizeUtils;
import com.pkqup.commonlibrary.util.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.socks.library.KLog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @CreatedbBy: liucun on 2018/6/23.
 * @Describe:
 */
public class GoodsListActivity extends BaseActivity {

    private static final String ORDER_ALL = "modified_time";//综合
    private static final String ORDER_NEW = "list_time";//新品
    private static final String ORDER_PRICE_ASC = "price_asc";//价格升序
    private static final String ORDER_PRICE_DESC = "price_desc";//价格降序

    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.right_view)
    LinearLayout rightView;

    @BindView(R.id.tv_all)
    TextView tvAll;
    @BindView(R.id.tv_new)
    TextView tvNew;
    @BindView(R.id.sortPrice)
    RelativeLayout sortPrice;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_class)
    TextView tvClass;
    @BindView(R.id.sortFilter)
    RelativeLayout sortFilter;
    @BindView(R.id.tv_filter)
    TextView tvFilter;

    @BindView(R.id.etLowPrice)
    EditText etLowPrice;
    @BindView(R.id.etHighPrice)
    EditText etHighPrice;
    @BindView(R.id.etStartTime)
    EditText etStartTime;
    @BindView(R.id.etEndTime)
    EditText etEndTime;
    @BindView(R.id.tvReset)
    TextView tvReset;
    @BindView(R.id.tvConfirm)
    TextView tvConfirm;
    @BindView(R.id.recyclerViewBrand)
    RecyclerView recyclerViewBrand;//品牌列表
    @BindView(R.id.recyclerViewStore)
    RecyclerView recyclerViewStore;//名庄列表

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycle_view)
    RecyclerView recycleView;

    private View notDataView;

    private CompositeDisposable disposable;
    private List<TextView> sortTextViewLists;
    private boolean listType = true;//是否是列表形式
    private List<GoodsListDetailBean> lists;
    private LinearAdapter linearAdapter;
    private GridAdapter gridAdapter;

    //三级分类列表
    private List<FirstClassBean> categoryLists;
    private ClassPopWindow classPopWindow;

    //品牌列表
    private List<FilterBrandBean> brandLists;
    private FilterBrandAdapter filterBrandAdapter;
    //酒庄列表
    private List<FilterStoreBean> storeLists;
    private FilterStoreAdapter filterStoreAdapter;

    private String searchKey;
    private String classId;
    private String className;

    private int pageNum = 1;
    private String orderBy = ORDER_ALL;
    private int selectIndex = 0;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.img_title_left:
                    checkBack();
                    break;
                case R.id.img_title_right_one:
                    startActivity(new Intent(GoodsListActivity.this, SearchActivity.class));
                    break;
                case R.id.img_title_right_two:
                    changeListType();
                    break;
                case R.id.tv_all:
                    changeSort(0);
                    break;
                case R.id.tv_new:
                    changeSort(1);
                    break;
                case R.id.sortPrice:
                    changeSort(2);
                    break;
                case R.id.tv_class:
                    showClassPopWindow();
                    break;
                case R.id.sortFilter:
                    showDrawerLayout();
                    break;
            }
        }
    };


    public static void startGoodsListActivity(Activity activity, String secondClassId, String secondClassName, String searchKey) {
        Intent intent = new Intent(activity, GoodsListActivity.class);
        intent.putExtra("classId", secondClassId);
        intent.putExtra("className", secondClassName);
        intent.putExtra("searchKey", searchKey);
        activity.startActivity(intent);
    }

    @Override
    public void setTitleView() {
        titleImgLeft.setOnClickListener(onClickListener);
        titleImgRightOne.setVisibility(View.VISIBLE);
        titleImgRightTwo.setVisibility(View.VISIBLE);
        titleImgRightTwo.setImageResource(R.mipmap.icon_grid);
        titleImgRightOne.setOnClickListener(onClickListener);
        titleImgRightTwo.setOnClickListener(onClickListener);
        titleSearchEdit.setOnEditorActionListener(onEditorActionListener);
    }

    private TextView.OnEditorActionListener onEditorActionListener = new
            TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        if (!TextUtils.isEmpty(titleSearchEdit.getText().toString().trim())) {
                            KeyBoardUtils.hideSoftInput(GoodsListActivity.this);
                            refreshLayout.autoRefresh();
                            getGoodsList(pageNum, true);
                        }
                    }
                    return true;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goods_activity_list);
        getIntentData();
        initDrawerLayout();
        initView();
        initData();
    }

    private void getIntentData() {
        classId = getIntent().getStringExtra("classId");
        className = getIntent().getStringExtra("className");
        searchKey = getIntent().getStringExtra("searchKey");
        if (TextUtils.isEmpty(searchKey)) {
            searchKey = "";
            titleName.setText(className);
            titleName.setVisibility(View.VISIBLE);
            titleImgRightOne.setVisibility(View.VISIBLE);
            titleSearchView.setVisibility(View.GONE);
        } else {
            titleName.setVisibility(View.GONE);
            titleImgRightOne.setVisibility(View.GONE);
            titleSearchView.setVisibility(View.VISIBLE);
            titleSearchEdit.setText(searchKey);
        }
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


        brandLists = new ArrayList<>();
        filterBrandAdapter = new FilterBrandAdapter(R.layout.goods_item_pop_class, brandLists);
        filterBrandAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                brandLists.get(position).setSelect(true);
                filterBrandAdapter.notifyDataSetChanged();
            }
        });
        recyclerViewBrand.setHasFixedSize(true);
        recyclerViewBrand.setNestedScrollingEnabled(false);
        recyclerViewBrand.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerViewBrand.setAdapter(filterBrandAdapter);

        storeLists = new ArrayList<>();
        filterStoreAdapter = new FilterStoreAdapter(R.layout.goods_item_pop_class, storeLists);
        filterStoreAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                storeLists.get(position).setSelect(true);
                filterStoreAdapter.notifyDataSetChanged();
            }
        });
        recyclerViewStore.setHasFixedSize(true);
        recyclerViewStore.setNestedScrollingEnabled(false);
        recyclerViewStore.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerViewStore.setAdapter(filterStoreAdapter);
    }


    private void initView() {
        disposable = new CompositeDisposable();

        tvAll.setOnClickListener(onClickListener);
        tvNew.setOnClickListener(onClickListener);
        sortPrice.setOnClickListener(onClickListener);
        tvClass.setOnClickListener(onClickListener);
        sortFilter.setOnClickListener(onClickListener);
        tvAll.setSelected(true);
        sortTextViewLists = new ArrayList<>();
        sortTextViewLists.add(tvAll);
        sortTextViewLists.add(tvNew);
        sortTextViewLists.add(tvPrice);
        sortTextViewLists.add(tvClass);
        sortTextViewLists.add(tvFilter);

        lists = new ArrayList<>();
        linearAdapter = new LinearAdapter(R.layout.amain_item_goods_list_linear, lists);
        linearAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GoodsDetailsActivity.startGoodsDetailsActivity(GoodsListActivity.this, lists.get(position).getItem_id());
            }
        });
        gridAdapter = new GridAdapter(R.layout.amain_item_goods_list_grid, lists);
        gridAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GoodsDetailsActivity.startGoodsDetailsActivity(GoodsListActivity.this, lists.get(position).getItem_id());
            }
        });
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        recycleView.setAdapter(linearAdapter);

        refreshLayout.setEnableAutoLoadMore(false);//关闭自动加载更多
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                getGoodsList(1, true);
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                getGoodsList(pageNum + 1, false);
            }
        });
        notDataView = getLayoutInflater().inflate(R.layout.common_empty_view, (ViewGroup) recycleView.getParent(), false);
    }

    private void initData() {
        getGoodsList(pageNum, true);
        getClassData();
        getFilterData();
    }

    private void getGoodsList(int pageNum, final boolean isRefresh) {
        disposable.add(ApiUtils.getInstance().getGoodsList(classId, pageNum, orderBy, searchKey, "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultBean<GoodsListBean>>() {
                    @Override
                    public void accept(ResultBean<GoodsListBean> goodsListBeanResultBean) throws Exception {
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadMore();
                        getListSuccess(goodsListBeanResultBean.getData(), isRefresh);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadMore();
                    }
                }));
    }

    private void getClassData() {
        disposable.add(ApiUtils.getInstance().getMainClass()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultBean<MainClassBean>>() {
                    @Override
                    public void accept(ResultBean<MainClassBean> mainClassBean) throws Exception {
                        categoryLists = mainClassBean.getData().getCategorys();
                    }
                }, new Consumer<Throwable>() {
                    @Override

                    public void accept(Throwable throwable) throws Exception {
                    }
                }));
    }


    private void getFilterData() {
        disposable.add(ApiUtils.getInstance().getFilterData(classId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultBean<FilterListBean>>() {
                    @Override
                    public void accept(ResultBean<FilterListBean> filterListBeanResultBean) throws Exception {
                        List<FilterBrandBean> brands = filterListBeanResultBean.getData().getBrand();
                        List<FilterStoreBean> cats = filterListBeanResultBean.getData().getCat();
                        if (brands != null) {
                            brandLists = brands;
                            filterBrandAdapter.setNewData(brandLists);
                        }

                        if (cats != null) {
                            storeLists = cats;
                            filterStoreAdapter.setNewData(storeLists);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                }));
    }


    private void getListSuccess(GoodsListBean goodsListBean, boolean isRefresh) {
        if (goodsListBean != null && goodsListBean.getList() != null && goodsListBean.getList().size() > 0) {
            List<GoodsListDetailBean> newLists = goodsListBean.getList();
            if (newLists == null) newLists = new ArrayList<>();
            if (isRefresh) {
                pageNum = 1;
                lists = newLists;
            } else {
                pageNum++;
                lists.addAll(newLists);
            }
            if (goodsListBean.getPagers().getTotal() <= lists.size()) {
                refreshLayout.setFooterHeight(30);
                refreshLayout.finishLoadMoreWithNoMoreData();//显示没有更多数据
            } else {
                refreshLayout.setNoMoreData(false);
            }
            if (listType) {
                if (lists.size() == 0) {
                    linearAdapter.setEmptyView(notDataView);
                } else {
                    linearAdapter.setNewData(lists);
                }
            } else {
                if (lists.size() == 0) {
                    gridAdapter.setEmptyView(notDataView);
                } else {
                    gridAdapter.setNewData(lists);
                }
            }
        } else {
            if (listType) {
                linearAdapter.setEmptyView(notDataView);
            } else {
                gridAdapter.setEmptyView(notDataView);
            }
        }
    }


    private void changeSort(int index) {
        if (selectIndex != index) {
            selectIndex = index;
            if (index == 0) {
                orderBy = ORDER_ALL;
            } else if (index == 1) {
                orderBy = ORDER_NEW;
            } else if (index == 2) {
                orderBy = ORDER_PRICE_ASC;
            }
            for (int i = 0; i < sortTextViewLists.size(); i++) {
                if (i == index) {
                    sortTextViewLists.get(i).setSelected(true);
                } else {
                    sortTextViewLists.get(i).setSelected(false);
                }
            }
        } else {
            if (index == 2) {
                if (orderBy.equals(ORDER_PRICE_ASC)) {
                    orderBy = ORDER_PRICE_DESC;
                } else {
                    orderBy = ORDER_PRICE_ASC;
                }
            }
        }
        getGoodsList(1, true);
    }

    private void changeListType() {
        if (listType) {
            //列表切换到网格
            listType = false;
            titleImgRightTwo.setImageResource(R.mipmap.icon_list);
            recycleView.setLayoutManager(new GridLayoutManager(this, 2));
            recycleView.setAdapter(gridAdapter);
            gridAdapter.setNewData(lists);
        } else {
            //网格切换到列表
            listType = true;
            titleImgRightTwo.setImageResource(R.mipmap.icon_grid);
            recycleView.setLayoutManager(new LinearLayoutManager(this));
            recycleView.setAdapter(linearAdapter);
            linearAdapter.setNewData(lists);
        }
    }


    public class LinearAdapter extends BaseQuickAdapter<GoodsListDetailBean, BaseViewHolder> {
        public LinearAdapter(int layoutResId, List<GoodsListDetailBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, GoodsListDetailBean item) {
            ImageView imgPic = helper.getView(R.id.img_pic);
            TextView tvStartPrice = helper.getView(R.id.tvStartPrice);

            GlideUtils.loadImage(GoodsListActivity.this, item.getImage_default_id(), imgPic);
            helper.setText(R.id.tv_name, item.getTitle());
            helper.setText(R.id.tvStartPriceStr, "原价：");
            tvStartPrice.setText(item.getPrice());
            tvStartPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰
            helper.setText(R.id.tvSellPriceStr, "");
            helper.setText(R.id.tvSellPrice, item.getPrice());
        }
    }

    public class GridAdapter extends BaseQuickAdapter<GoodsListDetailBean, BaseViewHolder> {
        public GridAdapter(int layoutResId, List<GoodsListDetailBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, GoodsListDetailBean item) {
            ImageView imgPic = helper.getView(R.id.img_pic);
            ViewGroup.LayoutParams layoutParams = imgPic.getLayoutParams();
            int picWidth = (SizeUtils.getScreenWidth() - SizeUtils.dp2px(40)) / 2;
            layoutParams.width = picWidth;
            layoutParams.height = picWidth;
            imgPic.setLayoutParams(layoutParams);
            GlideUtils.loadImage(GoodsListActivity.this, item.getImage_default_id(), imgPic);
            helper.setText(R.id.tv_name, item.getTitle());

            TextView tvStartPrice = helper.getView(R.id.tvStartPrice);
            helper.setText(R.id.tvStartPriceStr, "原价：");
            tvStartPrice.setText(item.getPrice());
            tvStartPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰
            helper.setText(R.id.tvSellPriceStr, "");
            helper.setText(R.id.tvSellPrice, item.getPrice());
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

    private void showClassPopWindow() {
        if (categoryLists == null || categoryLists.size() == 0) {
            ToastUtils.showShort("暂无分类");
        } else {
            if (classPopWindow == null) {
                classPopWindow = new ClassPopWindow(this, categoryLists, classId);
                classPopWindow.setCallBack(new ClassPopWindow.CallBack() {
                    @Override
                    public void choiceClass(String name, String id) {
                        classId = id;
                        className = name;
                        titleName.setText(className);
                        pageNum = 1;
                        getGoodsList(pageNum, true);
                        getFilterData();
                    }
                });
            }
            classPopWindow.showAsDropDown(tvClass, 0, 1);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}
