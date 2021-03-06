package com.chunlangjiu.app.amain.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.chunlangjiu.app.R;
import com.chunlangjiu.app.abase.BaseActivity;
import com.chunlangjiu.app.abase.BaseFragmentAdapter;
import com.chunlangjiu.app.amain.fragment.AuctionFragment;
import com.chunlangjiu.app.amain.fragment.CartFragment;
import com.chunlangjiu.app.amain.fragment.ClassFragment;
import com.chunlangjiu.app.amain.fragment.GoodsFragment;
import com.chunlangjiu.app.amain.fragment.HomeFragment;
import com.chunlangjiu.app.amain.fragment.UserFragment;
import com.chunlangjiu.app.util.ConstantMsg;
import com.chunlangjiu.app.util.GeTuiIntentService;
import com.chunlangjiu.app.util.GeTuiPushService;
import com.chunlangjiu.app.util.LocationUtils;
import com.github.promeg.pinyinhelper.Pinyin;
import com.igexin.sdk.PushManager;
import com.pkqup.commonlibrary.dialog.CommonConfirmDialog;
import com.pkqup.commonlibrary.eventmsg.EventManager;
import com.pkqup.commonlibrary.util.PermissionUtils;
import com.pkqup.commonlibrary.view.MyViewPager;
import com.socks.library.KLog;
import com.yanzhenjie.permission.PermissionListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class MainActivity extends BaseActivity {

    @BindView(R.id.view_pager)
    MyViewPager viewPager;

    @BindView(R.id.tab_one)
    LinearLayout tabOne;
    @BindView(R.id.tab_one_image)
    ImageView tabOneImage;
    @BindView(R.id.tab_one_text)
    TextView tabOneText;

    @BindView(R.id.tab_two)
    LinearLayout tabTwo;
    @BindView(R.id.tab_two_image)
    ImageView tabTwoImage;
    @BindView(R.id.tab_two_text)
    TextView tabTwoText;

    @BindView(R.id.tab_three)
    LinearLayout tabThree;
    @BindView(R.id.tab_three_image)
    ImageView tabThreeImage;
    @BindView(R.id.tab_three_text)
    TextView tabThreeText;

    @BindView(R.id.tab_four)
    LinearLayout tabFour;
    @BindView(R.id.tab_four_image)
    ImageView tabFourImage;
    @BindView(R.id.tab_four_text)
    TextView tabFourText;
    @BindView(R.id.tv_cart_num)
    TextView tvCartNum;

    @BindView(R.id.tab_five)
    LinearLayout tabFive;
    @BindView(R.id.tab_five_image)
    ImageView tabFiveImage;
    @BindView(R.id.tab_five_text)
    TextView tabFiveText;

    private BaseFragmentAdapter myFragmentAdapter;
    private List<Fragment> fragments;
    private List<ImageView> imageViews;
    private List<TextView> textViews;

    private long exitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amain_activity_main);
        EventManager.getInstance().registerListener(onNotifyListener);
        requestPermission();
        initGeTuiPush();
        initView();
        initData();
    }

    private void initGeTuiPush() {
        PushManager.getInstance().initialize(getApplicationContext(), GeTuiPushService.class);
        PushManager.getInstance().registerPushIntentService(getApplicationContext(), GeTuiIntentService.class);
    }

    private void requestPermission() {
        //请求必要的权限
        PermissionUtils.PermissionForStart(this, new PermissionListener() {
            @Override
            public void onSucceed(int requestCode, List<String> grantPermissions) {
            }

            @Override
            public void onFailed(int requestCode, List<String> deniedPermissions) {
                showPermissionDialog();
            }
        });
    }

    private void showPermissionDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final CommonConfirmDialog commonConfirmDialog = new CommonConfirmDialog(MainActivity.this, "信任是美好的开始，请授权相关权限，让我们更好的为你服务");
                commonConfirmDialog.setDialogStr("取消", "去授权");
                commonConfirmDialog.setCallBack(new CommonConfirmDialog.CallBack() {
                    @Override
                    public void onConfirm() {
                        commonConfirmDialog.dismiss();
                        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MainActivity.this.startActivity(intent);
                    }

                    @Override
                    public void onCancel() {
                        commonConfirmDialog.dismiss();
                    }
                });
                commonConfirmDialog.show();
            }
        });
    }

    @Override
    public void setTitleView() {
        titleView.setVisibility(View.GONE);
    }

    private void initView() {
        tabOne.setOnClickListener(onClickListener);
        tabTwo.setOnClickListener(onClickListener);
        tabThree.setOnClickListener(onClickListener);
        tabFour.setOnClickListener(onClickListener);
        tabFive.setOnClickListener(onClickListener);

        imageViews = new ArrayList<>();
        imageViews.add(tabOneImage);
        imageViews.add(tabTwoImage);
        imageViews.add(tabThreeImage);
        imageViews.add(tabFourImage);
        imageViews.add(tabFiveImage);

        textViews = new ArrayList<>();
        textViews.add(tabOneText);
        textViews.add(tabTwoText);
        textViews.add(tabThreeText);
        textViews.add(tabFourText);
        textViews.add(tabFiveText);
    }

    private void initData() {
        fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(GoodsFragment.newInstance("", false, "", ""));
        fragments.add(new AuctionFragment());
        fragments.add(new CartFragment());
        fragments.add(new UserFragment());
        myFragmentAdapter = new BaseFragmentAdapter(getSupportFragmentManager());
        myFragmentAdapter.setLists(fragments);
        viewPager.setAdapter(myFragmentAdapter);
        setPageFragment(0);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tab_one:
                    setPageFragment(0);
                    break;
                case R.id.tab_two:
                    setPageFragment(1);
                    break;
                case R.id.tab_three:
                    setPageFragment(2);
                    break;
                case R.id.tab_four:
                    setPageFragment(3);
                    break;
                case R.id.tab_five:
                    setPageFragment(4);
                    break;
            }
        }
    };

    private void setPageFragment(int position) {
        viewPager.setCurrentItem(position, false);
        for (int i = 0; i < imageViews.size(); i++) {
            if (position == i) {
                imageViews.get(i).setSelected(true);
            } else {
                imageViews.get(i).setSelected(false);
            }
        }
        for (int i = 0; i < textViews.size(); i++) {
            if (position == i) {
                textViews.get(i).setSelected(true);
            } else {
                textViews.get(i).setSelected(false);
            }
        }

    }

    private EventManager.OnNotifyListener onNotifyListener = new EventManager.OnNotifyListener() {
        @Override
        public void onNotify(Object object, String eventTag) {
            msgToPageClass(eventTag);//我要买酒
            msgToPageAuction(eventTag);//竞拍专区
            msgToPageCart(eventTag);//购物车
            msgToPageMy(eventTag);//我的
        }
    };

    private void msgToPageClass(String eventTag) {
        if (eventTag.equals(ConstantMsg.MSG_PAGE_CLASS)) {
            setPageFragment(1);
        }
    }

    private void msgToPageAuction(String eventTag) {
        if (eventTag.equals(ConstantMsg.MSG_PAGE_AUCTION)) {
            setPageFragment(2);
        }
    }

    private void msgToPageCart(String eventTag) {
        if (eventTag.equals(ConstantMsg.MSG_PAGE_CART)) {
            setPageFragment(3);
        }
    }

    private void msgToPageMy(String eventTag) {
        if (eventTag.equals(ConstantMsg.MSG_PAGE_MY)) {
            setPageFragment(4);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventManager.getInstance().unRegisterListener(onNotifyListener);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
        }
        return true;
    }

}
