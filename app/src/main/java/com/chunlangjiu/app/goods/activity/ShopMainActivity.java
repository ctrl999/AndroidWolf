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
import com.chunlangjiu.app.amain.bean.SecondClassBean;
import com.chunlangjiu.app.amain.bean.ThirdClassBean;
import com.chunlangjiu.app.goods.adapter.FilterBrandAdapter;
import com.chunlangjiu.app.goods.adapter.FilterStoreAdapter;
import com.chunlangjiu.app.goods.bean.AlcListBean;
import com.chunlangjiu.app.goods.bean.AreaListBean;
import com.chunlangjiu.app.goods.bean.BrandsListBean;
import com.chunlangjiu.app.goods.bean.FilterBrandBean;
import com.chunlangjiu.app.goods.bean.FilterListBean;
import com.chunlangjiu.app.goods.bean.FilterStoreBean;
import com.chunlangjiu.app.goods.bean.GoodsListBean;
import com.chunlangjiu.app.goods.bean.GoodsListDetailBean;
import com.chunlangjiu.app.goods.bean.OrdoListBean;
import com.chunlangjiu.app.goods.bean.PriceBean;
import com.chunlangjiu.app.goods.bean.ShopInfoBean;
import com.chunlangjiu.app.goods.dialog.ClassPopWindow;
import com.chunlangjiu.app.net.ApiUtils;
import com.chunlangjiu.app.user.dialog.ChoiceAlcPopWindow;
import com.chunlangjiu.app.user.dialog.ChoiceAreaPopWindow;
import com.chunlangjiu.app.user.dialog.ChoiceBrandPopWindow;
import com.chunlangjiu.app.user.dialog.ChoiceOrdoPopWindow;
import com.chunlangjiu.app.user.dialog.ChoicePricePopWindow;
import com.lzy.widget.HeaderViewPager;
import com.pkqup.commonlibrary.glide.GlideUtils;
import com.pkqup.commonlibrary.net.bean.ResultBean;
import com.pkqup.commonlibrary.util.KeyBoardUtils;
import com.pkqup.commonlibrary.util.SizeUtils;
import com.pkqup.commonlibrary.util.ToastUtils;
import com.pkqup.commonlibrary.view.MyHeaderRecycleView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @CreatedbBy: liucun on 2018/7/5
 * @Describe: 卖家主页
 */
public class ShopMainActivity extends BaseActivity {

    private RelativeLayout rlBrand;
    private TextView tvBrand;
    private RelativeLayout rlArea;
    private TextView tvArea;
    private RelativeLayout rlIncense;
    private TextView tvIncense;
    private RelativeLayout rlAlc;
    private TextView tvAlc;
    private RelativeLayout rlPrice;
    private TextView tvPrice;

    //分类列表
    private RecyclerView recyclerViewClass;
    private ClassAdapter classAdapter;
    private List<ThirdClassBean> classLists;
    private String selectClassId = "";

    private String brandId = "";
    private String areaId = "";
    private String ordoId = "";
    private String alcoholId = "";
    private String minPrice = "";
    private String maxPrice = "";


    //品牌列表
    private ChoiceBrandPopWindow choiceBrandPopWindow;
    private List<BrandsListBean.BrandBean> brandLists = new ArrayList<>();

    //产地列表
    private ChoiceAreaPopWindow choiceAreaPopWindow;
    private List<AreaListBean.AreaBean> areaLists = new ArrayList<>();

    //香型列表
    private ChoiceOrdoPopWindow choiceOrdoPopWindow;
    private List<OrdoListBean.OrdoBean> ordoLists = new ArrayList<>();

    //酒精度列表
    private ChoiceAlcPopWindow choiceAlcPopWindow;
    private List<AlcListBean.AlcBean> alcLists = new ArrayList<>();

    //价格列表
    private ChoicePricePopWindow choicePricePopWindow;
    private List<PriceBean> priceLists;
    private String priceId = "";

    @BindView(R.id.imgShow)
    ImageView imgShow;
    @BindView(R.id.imgHead)
    CircleImageView imgHead;
    @BindView(R.id.tvShopName)
    TextView tvShopName;
    @BindView(R.id.tvShopTips)
    TextView tvShopTips;
    @BindView(R.id.rlShopDetail)
    RelativeLayout rlShopDetail;
    @BindView(R.id.tvShopPhone)
    TextView tvShopPhone;
    @BindView(R.id.tvDesc)
    TextView tvDesc;

    @BindView(R.id.scrollableLayout)
    HeaderViewPager scrollableLayout;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycle_view)
    MyHeaderRecycleView recycleView;

    private View notDataView;

    private CompositeDisposable disposable;
    private boolean listType = true;//是否是列表形式
    private List<GoodsListDetailBean> lists;
    private LinearAdapter linearAdapter;
    private GridAdapter gridAdapter;
    private String classId;
    private String className;


    private String shopId;
    private boolean showDesc = false;
    private int pageNum = 1;


    public static void startShopMainActivity(Activity activity, String shopId) {
        Intent intent = new Intent(activity, ShopMainActivity.class);
        intent.putExtra("shopId", shopId);
        activity.startActivity(intent);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.img_title_left:
                    finish();
                    break;
                case R.id.img_title_right_one:
                    changeListType();
                    break;
                case R.id.imgShow:
                    changeShopDesc();
                    break;
                case R.id.rlBrand:
                    showBrandPopWindow();
                    break;
                case R.id.rlArea:
                    showAreaPopWindow();
                    break;
                case R.id.rlIncense:
                    showIncensePopWindow();
                    break;
                case R.id.rlAlc:
                    showDegreePopWindow();
                    break;
                case R.id.rlPrice:
                    showPricePopWindow();
                    break;

            }
        }
    };


    @Override
    public void setTitleView() {
        titleImgLeft.setOnClickListener(onClickListener);
        titleImgRightOne.setVisibility(View.VISIBLE);
        titleImgRightOne.setImageResource(R.mipmap.icon_grid);
        titleImgRightOne.setOnClickListener(onClickListener);
        titleName.setVisibility(View.GONE);
        titleSearchView.setVisibility(View.VISIBLE);
        titleSearchEdit.setOnEditorActionListener(onEditorActionListener);
    }

    private TextView.OnEditorActionListener onEditorActionListener = new
            TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        if (!TextUtils.isEmpty(titleSearchEdit.getText().toString().trim())) {
                            KeyBoardUtils.hideSoftInput(ShopMainActivity.this);
                            refreshLayout.autoRefresh();
                            getGoodsList(1, true);
                        }
                    }
                    return true;
                }
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goods_activity_shop_main);
        initFilterView();
        initView();
        initData();
    }

    private void initFilterView() {
        rlBrand = findViewById(R.id.rlBrand);
        tvBrand = findViewById(R.id.tvBrand);
        rlArea = findViewById(R.id.rlArea);
        tvArea = findViewById(R.id.tvArea);
        rlIncense = findViewById(R.id.rlIncense);
        tvIncense = findViewById(R.id.tvIncense);
        rlAlc = findViewById(R.id.rlAlc);
        tvAlc = findViewById(R.id.tvAlc);
        rlPrice = findViewById(R.id.rlPrice);
        tvPrice = findViewById(R.id.tvPrice);
        rlBrand.setOnClickListener(onClickListener);
        rlArea.setOnClickListener(onClickListener);
        rlIncense.setOnClickListener(onClickListener);
        rlAlc.setOnClickListener(onClickListener);
        rlPrice.setOnClickListener(onClickListener);

        recyclerViewClass = findViewById(R.id.recyclerViewClass);
        classLists = new ArrayList<>();
        ThirdClassBean thirdClassBean = new ThirdClassBean();
        thirdClassBean.setCat_id("");
        thirdClassBean.setCat_name("全部");
        classLists.add(thirdClassBean);
        classAdapter = new ClassAdapter(R.layout.amain_item_class, classLists);
        recyclerViewClass.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewClass.setAdapter(classAdapter);
        classAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                selectClassId = classLists.get(position).getCat_id();
                classAdapter.notifyDataSetChanged();
                getGoodsList(1, true);
            }
        });
    }


    private void initView() {
        imgShow.setOnClickListener(onClickListener);
        scrollableLayout.setCurrentScrollableContainer(recycleView);

        lists = new ArrayList<>();
        linearAdapter = new LinearAdapter(R.layout.amain_item_goods_list_linear, lists);
        linearAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GoodsDetailsActivity.startGoodsDetailsActivity(ShopMainActivity.this, lists.get(position).getItem_id());
            }
        });
        gridAdapter = new GridAdapter(R.layout.amain_item_goods_list_grid, lists);
        gridAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GoodsDetailsActivity.startGoodsDetailsActivity(ShopMainActivity.this, lists.get(position).getItem_id());
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
        disposable = new CompositeDisposable();
        shopId = getIntent().getStringExtra("shopId");
        initPriceLists();
        getClassData();
        getBrandLists();
        getAreaLists();
        getInsenceLists();
        getAlcLists();
        getShopInfo();
        getGoodsList(1, true);
    }

    private void initPriceLists() {
        priceLists = new ArrayList<>();
        priceLists.add(new PriceBean("", "", ""));
        priceLists.add(new PriceBean("1", "", "999"));
        priceLists.add(new PriceBean("2", "1000", "2999"));
        priceLists.add(new PriceBean("3", "3000", "4999"));
        priceLists.add(new PriceBean("4", "5000", "99999"));
        priceLists.add(new PriceBean("5", "10000", ""));
    }

    private void getBrandLists() {
        disposable.add(ApiUtils.getInstance().getUserBrandList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultBean<BrandsListBean>>() {
                    @Override
                    public void accept(ResultBean<BrandsListBean> brandsListBeanResultBean) throws Exception {
                        brandLists = brandsListBeanResultBean.getData().getList();
                        BrandsListBean.BrandBean brandBean = new BrandsListBean().new BrandBean();
                        brandBean.setBrand_id("");
                        brandBean.setBrand_name("全部");
                        brandLists.add(0, brandBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                }));
    }

    private void getAreaLists() {
        disposable.add(ApiUtils.getInstance().getUserAreaList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultBean<AreaListBean>>() {
                    @Override
                    public void accept(ResultBean<AreaListBean> areaListBeanResultBean) throws Exception {
                        areaLists = areaListBeanResultBean.getData().getList();
                        AreaListBean.AreaBean areaBean = new AreaListBean().new AreaBean();
                        areaBean.setArea_id("");
                        areaBean.setArea_name("全部");
                        areaLists.add(0, areaBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                }));
    }

    private void getInsenceLists() {
        disposable.add(ApiUtils.getInstance().getUserOrdoList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultBean<OrdoListBean>>() {
                    @Override
                    public void accept(ResultBean<OrdoListBean> ordoListBeanResultBean) throws Exception {
                        ordoLists = ordoListBeanResultBean.getData().getList();
                        OrdoListBean.OrdoBean ordoBean = new OrdoListBean().new OrdoBean();
                        ordoBean.setOdor_id("");
                        ordoBean.setOdor_name("全部");
                        ordoLists.add(0, ordoBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                }));
    }

    private void getAlcLists() {
        disposable.add(ApiUtils.getInstance().getUserAlcList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultBean<AlcListBean>>() {
                    @Override
                    public void accept(ResultBean<AlcListBean> alcListBeanResultBean) throws Exception {
                        alcLists = alcListBeanResultBean.getData().getList();
                        AlcListBean.AlcBean alcBean = new AlcListBean().new AlcBean();
                        alcBean.setAlcohol_id("");
                        alcBean.setAlcohol_name("全部");
                        alcLists.add(0, alcBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
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
                        classLists.clear();
                        ThirdClassBean thirdClassBean = new ThirdClassBean();
                        thirdClassBean.setCat_id("");
                        thirdClassBean.setCat_name("全部");
                        classLists.add(thirdClassBean);
                        List<FirstClassBean> categorys = mainClassBean.getData().getCategorys();
                        for (int i = 0; i < categorys.size(); i++) {
                            List<SecondClassBean> lv2 = categorys.get(i).getLv2();
                            for (int j = 0; j < lv2.size(); j++) {
                                List<ThirdClassBean> lv3 = lv2.get(j).getLv3();
                                classLists.addAll(lv3);
                            }
                        }
                        classAdapter.setNewData(classLists);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }));
    }

    private void getShopInfo() {
        disposable.add(ApiUtils.getInstance().getShopInfo(shopId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultBean<ShopInfoBean>>() {
                    @Override
                    public void accept(ResultBean<ShopInfoBean> shopInfoBeanResultBean) throws Exception {
                        getShopInfoSuccess(shopInfoBeanResultBean.getData());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                }));
    }

    private void getShopInfoSuccess(ShopInfoBean data) {
        GlideUtils.loadImage(this, data.getShopInfo().getShop_logo(), imgHead);
        tvShopName.setText(data.getShopInfo().getShop_name());
        tvShopTips.setText(data.getShopInfo().getShop_descript());
        tvShopPhone.setText(data.getShopInfo().getMobile());
        tvDesc.setText(data.getShopInfo().getShop_descript());
    }

    private void getGoodsList(int pageNum, final boolean isRefresh) {
        if (isRefresh) {
            refreshLayout.autoRefresh();
        }
        disposable.add(ApiUtils.getInstance().getGoodsList(selectClassId, pageNum, "", shopId, brandId, areaId, ordoId, alcoholId, minPrice, maxPrice)
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
                    linearAdapter.setNewData(lists);
                    linearAdapter.setEmptyView(getLayoutInflater().inflate(R.layout.common_empty_view, (ViewGroup) recycleView.getParent(), false));
                } else {
                    linearAdapter.setNewData(lists);
                }

            } else {
                if (lists.size() == 0) {
                    gridAdapter.setNewData(lists);
                    gridAdapter.setEmptyView(getLayoutInflater().inflate(R.layout.common_empty_view, (ViewGroup) recycleView.getParent(), false));
                } else {
                    gridAdapter.setNewData(lists);
                }
            }
        } else {
            lists.clear();
            if (listType) {
                linearAdapter.setNewData(lists);
                linearAdapter.setEmptyView(getLayoutInflater().inflate(R.layout.common_empty_view, (ViewGroup) recycleView.getParent(), false));
            } else {
                gridAdapter.setNewData(lists);
                gridAdapter.setEmptyView(getLayoutInflater().inflate(R.layout.common_empty_view, (ViewGroup) recycleView.getParent(), false));
            }
        }
    }

    private void changeListType() {
        if (listType) {
            //列表切换到网格
            listType = false;
            titleImgRightOne.setImageResource(R.mipmap.icon_list);
            recycleView.setLayoutManager(new GridLayoutManager(this, 2));
            recycleView.setAdapter(gridAdapter);
            if (lists.size() == 0) {
                gridAdapter.setNewData(lists);
                gridAdapter.setEmptyView(getLayoutInflater().inflate(R.layout.common_empty_view, (ViewGroup) recycleView.getParent(), false));
            } else {
                gridAdapter.setNewData(lists);
            }
        } else {
            //网格切换到列表
            listType = true;
            titleImgRightOne.setImageResource(R.mipmap.icon_grid);
            recycleView.setLayoutManager(new LinearLayoutManager(this));
            recycleView.setAdapter(linearAdapter);
            if (lists.size() == 0) {
                linearAdapter.setNewData(lists);
                linearAdapter.setEmptyView(getLayoutInflater().inflate(R.layout.common_empty_view, (ViewGroup) recycleView.getParent(), false));
            } else {
                linearAdapter.setNewData(lists);
            }
        }
    }

    private void changeShopDesc() {
        if (showDesc) {
            showDesc = false;
            imgShow.setImageResource(R.mipmap.gray_down);
            rlShopDetail.setVisibility(View.GONE);
        } else {
            showDesc = true;
            imgShow.setImageResource(R.mipmap.gray_up);
            rlShopDetail.setVisibility(View.VISIBLE);
        }
    }

    private void showBrandPopWindow() {
        if (brandLists == null || brandLists.size() == 0) {
            ToastUtils.showShort("暂无品牌");
        } else {
            if (choiceBrandPopWindow == null) {
                choiceBrandPopWindow = new ChoiceBrandPopWindow(this, brandLists, brandId);
                choiceBrandPopWindow.setCallBack(new ChoiceBrandPopWindow.CallBack() {
                    @Override
                    public void choiceBrand(String brandName, String brandIdC) {
                        brandId = brandIdC;
                        tvBrand.setText(TextUtils.isEmpty(brandId) ? "品牌" : brandName);
                        getGoodsList(1, true);
                    }
                });
            }
            choiceBrandPopWindow.showAsDropDown(rlBrand, 0, 1);
        }
    }


    private void showAreaPopWindow() {
        if (areaLists == null || areaLists.size() == 0) {
            ToastUtils.showShort("暂无产地");
        } else {
            if (choiceAreaPopWindow == null) {
                choiceAreaPopWindow = new ChoiceAreaPopWindow(this, areaLists, areaId);
                choiceAreaPopWindow.setCallBack(new ChoiceAreaPopWindow.CallBack() {
                    @Override
                    public void choiceBrand(String brandName, String brandId) {
                        areaId = brandId;
                        tvArea.setText(TextUtils.isEmpty(areaId) ? "产地" : brandName);
                        getGoodsList(1, true);
                    }
                });
            }
            choiceAreaPopWindow.showAsDropDown(rlBrand, 0, 1);
        }
    }

    private void showIncensePopWindow() {
        if (ordoLists == null || ordoLists.size() == 0) {
            ToastUtils.showShort("暂无香型");
        } else {
            if (choiceOrdoPopWindow == null) {
                choiceOrdoPopWindow = new ChoiceOrdoPopWindow(this, ordoLists, ordoId);
                choiceOrdoPopWindow.setCallBack(new ChoiceOrdoPopWindow.CallBack() {
                    @Override
                    public void choiceBrand(String brandName, String brandId) {
                        ordoId = brandId;
                        tvIncense.setText(TextUtils.isEmpty(ordoId) ? "香型" : brandName);
                        getGoodsList(1, true);
                    }
                });
            }
            choiceOrdoPopWindow.showAsDropDown(rlBrand, 0, 1);
        }
    }

    private void showDegreePopWindow() {
        if (alcLists == null || alcLists.size() == 0) {
            ToastUtils.showShort("暂无酒精度");
        } else {
            if (choiceAlcPopWindow == null) {
                choiceAlcPopWindow = new ChoiceAlcPopWindow(this, alcLists, alcoholId);
                choiceAlcPopWindow.setCallBack(new ChoiceAlcPopWindow.CallBack() {
                    @Override
                    public void choiceBrand(String brandName, String brandId) {
                        alcoholId = brandId;
                        tvAlc.setText(TextUtils.isEmpty(alcoholId) ? "酒精度" : brandName);
                        getGoodsList(1, true);
                    }
                });
            }
            choiceAlcPopWindow.showAsDropDown(rlBrand, 0, 1);
        }
    }

    private void showPricePopWindow() {
        if (priceLists == null || priceLists.size() == 0) {
            ToastUtils.showShort("暂无价格区间");
        } else {
            if (choicePricePopWindow == null) {
                choicePricePopWindow = new ChoicePricePopWindow(this, priceLists, priceId);
                choicePricePopWindow.setCallBack(new ChoicePricePopWindow.CallBack() {
                    @Override
                    public void choicePrice(String minPriceC, String maxPriceC, String id, String content) {
                        minPrice = minPriceC;
                        maxPrice = maxPriceC;
                        priceId = id;
                        tvPrice.setText(content);
                        tvPrice.setText(TextUtils.isEmpty(priceId) ? "价格区间" : content);
                        getGoodsList(1, true);
                    }
                });
            }
            choicePricePopWindow.showAsDropDown(rlBrand, 0, 1);
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

            GlideUtils.loadImage(ShopMainActivity.this, item.getImage_default_id(), imgPic);
            helper.setText(R.id.tv_name, item.getTitle());
            helper.setText(R.id.tvStartPriceStr, "原价：");
            tvStartPrice.setText(item.getMkt_price());
            tvStartPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰
            helper.setText(R.id.tvSellPriceStr, "");
            helper.setText(R.id.tvSellPrice, item.getPrice());

            LinearLayout llTime = helper.getView(R.id.llTime);
            llTime.setVisibility(View.GONE);
            helper.setText(R.id.tvLabel, item.getLabel());
            TextView tvLabel = helper.getView(R.id.tvLabel);
            if (TextUtils.isEmpty(item.getLabel())) {
                tvLabel.setVisibility(View.GONE);
            } else {
                tvLabel.setVisibility(View.VISIBLE);
            }
            helper.setText(R.id.tv_attention, item.getView_count() + "人关注");
            helper.setText(R.id.tv_evaluate, item.getRate_count() + "条评价");
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
            GlideUtils.loadImage(ShopMainActivity.this, item.getImage_default_id(), imgPic);
            helper.setText(R.id.tv_name, item.getTitle());

            TextView tvStartPrice = helper.getView(R.id.tvStartPrice);
            helper.setText(R.id.tvStartPriceStr, "原价：");
            tvStartPrice.setText(item.getMkt_price());
            tvStartPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰
            helper.setText(R.id.tvSellPriceStr, "");
            helper.setText(R.id.tvSellPrice, item.getPrice());

            helper.setText(R.id.tvLabel, item.getLabel());
            TextView tvLabel = helper.getView(R.id.tvLabel);
            if (TextUtils.isEmpty(item.getLabel())) {
                tvLabel.setVisibility(View.GONE);
            } else {
                tvLabel.setVisibility(View.VISIBLE);
            }
            helper.setText(R.id.tvAttention, item.getView_count() + "人关注");
            helper.setText(R.id.tvEvaluate, item.getRate_count() + "条评价");
        }
    }

    public class ClassAdapter extends BaseQuickAdapter<ThirdClassBean, BaseViewHolder> {
        public ClassAdapter(int layoutResId, List<ThirdClassBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, ThirdClassBean item) {
            TextView tvClass = helper.getView(R.id.tvClass);
            tvClass.setText(item.getCat_name());
            tvClass.setSelected(item.getCat_id().equals(selectClassId));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}
