package com.chunlangjiu.app.goods.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chunlangjiu.app.R;
import com.chunlangjiu.app.abase.BaseActivity;
import com.chunlangjiu.app.abase.BaseApplication;
import com.chunlangjiu.app.abase.BaseFragmentAdapter;
import com.chunlangjiu.app.amain.activity.LoginActivity;
import com.chunlangjiu.app.amain.bean.CartCountBean;
import com.chunlangjiu.app.cart.CartActivity;
import com.chunlangjiu.app.cart.ChoiceNumDialog;
import com.chunlangjiu.app.goods.bean.ConfirmOrderBean;
import com.chunlangjiu.app.goods.bean.GoodsDetailBean;
import com.chunlangjiu.app.goods.fragment.GoodsCommentFragment;
import com.chunlangjiu.app.goods.fragment.GoodsDetailsFragment;
import com.chunlangjiu.app.goods.fragment.GoodsWebFragment;
import com.chunlangjiu.app.net.ApiUtils;
import com.chunlangjiu.app.util.ConstantMsg;
import com.chunlangjiu.app.util.ShareUtils;
import com.flyco.tablayout.SlidingTabLayout;
import com.pkqup.commonlibrary.eventmsg.EventManager;
import com.pkqup.commonlibrary.net.bean.ResultBean;
import com.socks.library.KLog;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.editorpage.ShareActivity;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class GoodsDetailsActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.img_share)
    ImageView imgShare;
    @BindView(R.id.tab)
    SlidingTabLayout tab;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.rlBottom)
    RelativeLayout rlBottom;
    @BindView(R.id.tvBuy)
    TextView tvBuy;
    @BindView(R.id.tvAddCart)
    TextView tvAddCart;

    @BindView(R.id.rlChat)
    RelativeLayout rlChat;
    @BindView(R.id.rlCollect)
    RelativeLayout rlCollect;
    @BindView(R.id.rlCart)
    RelativeLayout rlCart;
    @BindView(R.id.tvCartNum)
    TextView tvCartNum;

    private BaseFragmentAdapter fragmentAdapter;
    private final String[] mTitles = {"商品", "详情", "评价"};
    private List<Fragment> mFragments;

    private CompositeDisposable disposable;
    private GoodsDetailBean goodsDetailBean;
    private String itemId;
    private String skuId;
    private int cartCount;//购物车数量
    private int realStock = 1;//库存

    private ChoiceNumDialog buyNowDialog;
    private ChoiceNumDialog addCartDialog;


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.img_back:
                    finish();
                    break;
                case R.id.img_share://分享
                    showShare();
                    break;
            }
        }
    };

    private View.OnClickListener onClickListenerLogin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (BaseApplication.isLogin()) {
                switch (view.getId()) {
                    case R.id.tvAddCart://加入购物车
                        showAddCartDialog();
                        break;
                    case R.id.tvBuy://立即购买
                        showBuyNowDialog();
                        break;
                    case R.id.rlChat://聊天
                        break;
                    case R.id.rlCollect://收藏
                        break;
                    case R.id.rlCart://跳转到购物车
                        startActivity(new Intent(GoodsDetailsActivity.this, CartActivity.class));
                        break;
                }
            } else {
                startActivity(new Intent(GoodsDetailsActivity.this, LoginActivity.class));
            }
        }
    };


    public static void startGoodsDetailsActivity(Activity activity, String goodsId) {
        Intent intent = new Intent(activity, GoodsDetailsActivity.class);
        intent.putExtra("goodsId", goodsId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goods_activity_details);
        EventManager.getInstance().registerListener(onNotifyListener);
        initView();
        initData();
    }

    @Override
    public void setTitleView() {
        hideTitleView();
    }

    private void initView() {
        tab.setVisibility(View.GONE);
        imgShare.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        rlBottom.setVisibility(View.GONE);

        imgBack.setOnClickListener(onClickListener);
        imgShare.setOnClickListener(onClickListener);
        tvBuy.setOnClickListener(onClickListenerLogin);
        tvAddCart.setOnClickListener(onClickListenerLogin);
        rlChat.setOnClickListener(onClickListenerLogin);
        rlCollect.setOnClickListener(onClickListenerLogin);
        rlCart.setOnClickListener(onClickListenerLogin);

        itemId = getIntent().getStringExtra("goodsId");//itemId=79  测试id
        disposable = new CompositeDisposable();
    }

    private void initData() {
        getCartNum();
        getGoodsDetail();
    }

    private void getCartNum() {
        disposable.add(ApiUtils.getInstance().getCartCount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultBean<CartCountBean>>() {
                    @Override
                    public void accept(ResultBean<CartCountBean> cartCountBeanResultBean) throws Exception {
                        String number = cartCountBeanResultBean.getData().getNumber();
                        if (!TextUtils.isEmpty(number) && Integer.parseInt(number) > 0) {
                            cartCount = Integer.parseInt(number);
                            tvCartNum.setVisibility(View.VISIBLE);
                            tvCartNum.setText(number);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                }));
    }

    private void getGoodsDetail() {
        showLoadingDialog();
        disposable.add(ApiUtils.getInstance().getGoodsDetail(itemId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultBean<GoodsDetailBean>>() {
                    @Override
                    public void accept(ResultBean<GoodsDetailBean> goodsDetailBeanResultBean) throws Exception {
                        hideLoadingDialog();
                        goodsDetailBean = goodsDetailBeanResultBean.getData();
                        skuId = goodsDetailBean.getItem().getDefault_sku_id();
                        String realStore = goodsDetailBean.getItem().getRealStore();
                        if (!TextUtils.isEmpty(realStore)) {
                            realStock = Integer.parseInt(realStore);
                        }
                        updateView();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        hideLoadingDialog();
                    }
                }));
    }

    private void updateView() {
        tab.setVisibility(View.VISIBLE);
        imgShare.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);
        rlBottom.setVisibility(View.VISIBLE);

        mFragments = new ArrayList<>();
        mFragments.add(GoodsDetailsFragment.newInstance(goodsDetailBean));
        mFragments.add(new GoodsWebFragment());
        mFragments.add(GoodsCommentFragment.newInstance(itemId));
        fragmentAdapter = new BaseFragmentAdapter(getSupportFragmentManager());
        fragmentAdapter.setLists(mFragments);
        viewPager.setAdapter(fragmentAdapter);
        tab.setViewPager(viewPager, mTitles);
        viewPager.setCurrentItem(0);
    }


    private void showShare() {
        UMImage thumb = new UMImage(this, goodsDetailBean.getShare().getImage());
        UMWeb web = new UMWeb(goodsDetailBean.getShare().getH5href());
        web.setTitle(goodsDetailBean.getItem().getTitle());//标题
        web.setThumb(thumb);  //缩略图
        web.setDescription(goodsDetailBean.getItem().getSub_title());//描述

        ShareUtils.shareLink(this, web, new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
            }
        });
    }

    private EventManager.OnNotifyListener onNotifyListener = new EventManager.OnNotifyListener() {
        @Override
        public void onNotify(Object object, String eventTag) {
            changeToEvaluate(eventTag);
        }
    };

    /**
     * 跳转到评价tab
     *
     * @param eventTag 标识
     */
    private void changeToEvaluate(String eventTag) {
        if (eventTag.equals(ConstantMsg.CHANGE_TO_EVALUATE)) {
            viewPager.setCurrentItem(2);
        }
    }

    private void showAddCartDialog() {
        if (addCartDialog == null) {
            addCartDialog = new ChoiceNumDialog(this, realStock);
            addCartDialog.setCallBackListener(new ChoiceNumDialog.OnCallBackListener() {
                @Override
                public void choiceNum(int num) {
                    addGoodsToCart(num);
                }
            });
        }
        addCartDialog.show();
    }

    private void addGoodsToCart(int num) {
        showLoadingDialog();
        disposable.add(ApiUtils.getInstance().addGoodsToCart(num, skuId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultBean>() {
                    @Override
                    public void accept(ResultBean resultBean) throws Exception {
                        hideLoadingDialog();
                        cartCount++;
                        tvCartNum.setText(cartCount + "");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        hideLoadingDialog();
                    }
                }));
    }

    private void showBuyNowDialog() {
        if (buyNowDialog == null) {
            buyNowDialog = new ChoiceNumDialog(this, realStock);
            buyNowDialog.setCallBackListener(new ChoiceNumDialog.OnCallBackListener() {
                @Override
                public void choiceNum(int num) {
                    buyNow(num);
                }
            });
        }
        buyNowDialog.show();
    }

    private void buyNow(int num) {
        showLoadingDialog();
        //立即购买流程为先调用添加到购物车接口（模式为fastbuy），然后调用结算接口（模式为fastbuy）
        disposable.add(ApiUtils.getInstance().addGoodsToCartBuyNow(num, skuId)
                .concatMap(new Function<ResultBean, Flowable<ResultBean<ConfirmOrderBean>>>() {
                    @Override
                    public Flowable<ResultBean<ConfirmOrderBean>> apply(ResultBean resultBean) throws Exception {
                        return ApiUtils.getInstance().buyNowConfirmOrder();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultBean<ConfirmOrderBean>>() {
                    @Override
                    public void accept(ResultBean<ConfirmOrderBean> resultBean) throws Exception {
                        hideLoadingDialog();
                        ConfirmOrderActivity.startConfirmOrderActivity(GoodsDetailsActivity.this, resultBean.getData(),"fastbuy");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        hideLoadingDialog();
                    }
                }));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
        EventManager.getInstance().unRegisterListener(onNotifyListener);
    }
}
