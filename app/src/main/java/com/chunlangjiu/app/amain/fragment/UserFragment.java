package com.chunlangjiu.app.amain.fragment;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chunlangjiu.app.R;
import com.chunlangjiu.app.abase.BaseApplication;
import com.chunlangjiu.app.abase.BaseFragment;
import com.chunlangjiu.app.amain.activity.LoginActivity;
import com.chunlangjiu.app.amain.bean.CartListBean;
import com.chunlangjiu.app.amain.bean.LoginBean;
import com.chunlangjiu.app.net.ApiUtils;
import com.chunlangjiu.app.order.activity.OrderApplyForAfterSaleActivity;
import com.chunlangjiu.app.order.activity.OrderMainActivity;
import com.chunlangjiu.app.order.params.OrderParams;
import com.chunlangjiu.app.user.activity.AddGoodsActivity;
import com.chunlangjiu.app.user.activity.AddressListActivity;
import com.chunlangjiu.app.user.activity.CompanyAuthActivity;
import com.chunlangjiu.app.user.activity.PersonAuthActivity;
import com.chunlangjiu.app.user.bean.MyNumBean;
import com.chunlangjiu.app.user.bean.UploadImageBean;
import com.chunlangjiu.app.user.bean.UserInfoBean;
import com.chunlangjiu.app.util.ConstantMsg;
import com.chunlangjiu.app.util.GlideImageLoader;
import com.chunlangjiu.app.web.WebViewActivity;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.pkqup.commonlibrary.dialog.ChoicePhotoDialog;
import com.pkqup.commonlibrary.eventmsg.EventManager;
import com.pkqup.commonlibrary.glide.GlideUtils;
import com.pkqup.commonlibrary.net.bean.ResultBean;
import com.pkqup.commonlibrary.util.FileUtils;
import com.pkqup.commonlibrary.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import com.pkqup.commonlibrary.net.HttpUtils;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * @CreatedbBy: liucun on 2018/6/16.
 * @Describe: 我的
 */
public class UserFragment extends BaseFragment {

    private TextView tvToLogin;
    private RelativeLayout rlHead;

    private RelativeLayout rlBackground;
    private ImageView imgSetting;

    private CircleImageView imgHead;
    private TextView tvName;
    private TextView tvAccountType;
    private TextView tvChangeType;
    private TextView tvAuthRealName;
    private TextView tvAuthCompany;

    private LinearLayout llCanUseMoney;
    private TextView tvCanUseMoney;
    private LinearLayout llNotUseMoney;
    private TextView tvNotUseMoney;
    private LinearLayout llMessageNum;
    private TextView tvMessageNum;

    /*订单管理*/
    private RelativeLayout rlOrderManager;
    //-----------买家中心------------//
    private LinearLayout llBuyOrder;
    private RelativeLayout rlOrderOne;
    private TextView tvOrderOneNum;
    private RelativeLayout rlOrderTwo;
    private TextView tvOrderTwoNum;
    private RelativeLayout rlOrderThree;
    private TextView tvOrderThreeNum;
    private RelativeLayout rlOrderFour;
    private TextView tvOrderFourNum;
    private RelativeLayout rlOrderFive;
    private TextView tvOrderFiveNum;
    //-----------买家中心------------//

    //-----------卖家中心------------//
    private LinearLayout llSellOrder;
    private RelativeLayout rlSellOrderOne;
    private TextView tvSellOrderOneNum;
    private RelativeLayout rlSellOrderTwo;
    private TextView tvSellOrderTwoNum;
    private RelativeLayout rlSellOrderThree;
    private TextView tvSellOrderThreeNum;
    private RelativeLayout rlSellOrderFour;
    private TextView tvSellOrderFourNum;
    //-----------卖家中心------------//
    /*订单管理*/

    /*竞拍订单管理*/
    private RelativeLayout rlAuctionManager;
    private LinearLayout llBuyAuction;
    private RelativeLayout rlAuctionOne;
    private TextView tvAuctionOneNum;
    private RelativeLayout rlAuctionTwo;
    private TextView tvAuctionTwoNum;
    private RelativeLayout rlAuctionThree;
    private TextView tvAuctionThreeNum;
    private RelativeLayout rlAuctionFour;
    private TextView tvAuctionFourNum;
    private RelativeLayout rlAuctionFive;
    private TextView tvAuctionFiveNum;

    private LinearLayout llSellAuction;
    private RelativeLayout rlSellAuctionOne;
    private TextView tvSellAuctionOneNum;
    private RelativeLayout rlSellAuctionTwo;
    private TextView tvSellAuctionTwoNum;
    private RelativeLayout rlSellAuctionThree;
    private TextView tvSellAuctionThreeNum;
    private RelativeLayout rlSellAuctionFour;
    private TextView tvSellAuctionFourNum;
    /*竞拍订单管理*/

    /*商品管理*/
    private RelativeLayout rlGoodsManager;
    private LinearLayout llGoodsContent;
    private RelativeLayout rlAddGoods;
    private RelativeLayout rlSellGoods;
    private RelativeLayout rlAuctionGoods;
    private RelativeLayout rlWareHouseGoods;
    private RelativeLayout rlCheckGoods;
    /*商品管理*/

    /*我的管理*/
    private RelativeLayout rlMoneyManager;
    private RelativeLayout rlCollect;
    private RelativeLayout rlShare;
    private RelativeLayout rlVip;
    private RelativeLayout rlAddress;
    private RelativeLayout rlBankCard;
    private LinearLayout llMyManagerSecond;
    private RelativeLayout rlBankCardSecond;
    /*我的管理*/

    private static final int TYPE_BUYER = 0;//买家中心
    private static final int TYPE_SELLER = 1;//卖家中心
    private int userType = TYPE_BUYER;

    private CompositeDisposable disposable;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!BaseApplication.isLogin()) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
                return;
            }
            switch (view.getId()) {
                case R.id.tvToLogin:
                    break;
                case R.id.imgSetting:
                    break;
                case R.id.imgHead:
                    setHeadIcon();
                    break;
                case R.id.tvChangeType:// 切换买/卖家中心
                    changeUserType();
                    break;
                case R.id.tvAuthRealName:// 个人认证
                    toAuthActivity();
                    break;
                case R.id.tvAuthCompany:// 升级为企业(进入企业认证)
                    toAuthCompanyActivity();
                    break;
                case R.id.rlOrderManager:// 订单管理
                    if (llSellAuction.isShown()) {
                        toOrderMainActivity(3, 0);
                    } else {
                        toOrderMainActivity(0, 0);
                    }
                    break;
                case R.id.rlOrderOne:// 买家待付款
                    toOrderMainActivity(0, 1);
                    break;
                case R.id.rlOrderTwo:// 买家待收货
                    toOrderMainActivity(0, 3);
                    break;
                case R.id.rlOrderThree:// 买家待发货
                    toOrderMainActivity(0, 2);
                    break;
                case R.id.rlOrderFour:// 买家售后订单
                    toOrderMainActivity(2, 0);
                    break;
                case R.id.rlOrderFive:// 买家全部订单
                    toOrderMainActivity(0, 0);
                    break;
                case R.id.rlSellOrderOne:// 卖家待付款
                    toOrderMainActivity(3, 1);
                    break;
                case R.id.rlSellOrderTwo:// 卖家待发货
                    toOrderMainActivity(3, 2);
                    break;
                case R.id.rlSellOrderThree:// 卖家售后订单
                    toOrderMainActivity(4, 0);
                    break;
                case R.id.rlSellOrderFour:// 卖家全部订单
                    toOrderMainActivity(3, 0);
                    break;
                case R.id.rlAuctionManager:// 竞拍订单管理
                    break;
                case R.id.rlAuctionOne:// 买家竞拍订单-待付定金
                    break;
                case R.id.rlAuctionTwo:// 买家竞拍订单-竞拍中
                    break;
                case R.id.rlAuctionThree:// 买家竞拍订单-已中标
                    break;
                case R.id.rlAuctionFour:// 买家竞拍订单-落标
                    break;
                case R.id.rlAuctionFive:// 买家竞拍订单-全部订单
                    break;
                case R.id.rlSellAuctionOne:// 卖家竞拍订单-待付款
                    break;
                case R.id.rlSellAuctionTwo:// 卖家竞拍订单-待发货
                    break;
                case R.id.rlSellAuctionThree:// 卖家竞拍订单-待收货
                    break;
                case R.id.rlSellAuctionFour:// 卖家竞拍订单-全部订单
                    break;
                case R.id.rlAddGoods:// 添加商品
                    startActivity(new Intent(getActivity(), AddGoodsActivity.class));
                    break;
                case R.id.rlSellGoods:// 在售商品
                    break;
                case R.id.rlAuctionGoods:// 竞拍商品
                    break;
                case R.id.rlWareHouseGoods:// 仓库商品
                    break;
                case R.id.rlCheckGoods:// 审核商品
                    break;
                case R.id.rlMoneyManager:// 资金管理
                    break;
                case R.id.rlCollect:// 我的收藏
                    break;
                case R.id.rlVip:// 会员资料
                    break;
                case R.id.rlAddress:// 地址管理
                    startActivity(new Intent(getActivity(), AddressListActivity.class));
                    break;
                case R.id.rlBankCard:// 银行卡管理
                    break;
            }
        }
    };


    @Override
    public void getContentView(LayoutInflater inflater, ViewGroup container) {
        inflater.inflate(R.layout.amain_fragment_user, container, true);
    }

    @Override
    public void initView() {
        tvToLogin = rootView.findViewById(R.id.tvToLogin);
        tvToLogin.setOnClickListener(onClickListener);
        rlHead = rootView.findViewById(R.id.rlHead);

        rlBackground = rootView.findViewById(R.id.rlBackground);
        imgSetting = rootView.findViewById(R.id.imgSetting);
        imgSetting.setOnClickListener(onClickListener);

        imgHead = rootView.findViewById(R.id.imgHead);
        imgHead.setOnClickListener(onClickListener);
        tvName = rootView.findViewById(R.id.tvName);
        tvAccountType = rootView.findViewById(R.id.tvAccountType);

        tvChangeType = rootView.findViewById(R.id.tvChangeType);
        tvChangeType.setOnClickListener(onClickListener);
        tvAuthRealName = rootView.findViewById(R.id.tvAuthRealName);
        tvAuthRealName.setOnClickListener(onClickListener);
        tvAuthCompany = rootView.findViewById(R.id.tvAuthCompany);
        tvAuthCompany.setOnClickListener(onClickListener);

        llCanUseMoney = rootView.findViewById(R.id.llCanUseMoney);
        llCanUseMoney.setOnClickListener(onClickListener);
        tvCanUseMoney = rootView.findViewById(R.id.tvCanUseMoney);
        llNotUseMoney = rootView.findViewById(R.id.llNotUseMoney);
        llNotUseMoney.setOnClickListener(onClickListener);
        tvNotUseMoney = rootView.findViewById(R.id.tvNotUseMoney);
        llMessageNum = rootView.findViewById(R.id.llMessageNum);
        llMessageNum.setOnClickListener(onClickListener);
        tvMessageNum = rootView.findViewById(R.id.tvMessageNum);

        rlOrderManager = rootView.findViewById(R.id.rlOrderManager);
        rlOrderManager.setOnClickListener(onClickListener);
        llBuyOrder = rootView.findViewById(R.id.llBuyOrder);
        rlOrderOne = rootView.findViewById(R.id.rlOrderOne);
        rlOrderOne.setOnClickListener(onClickListener);
        tvOrderOneNum = rootView.findViewById(R.id.tvOrderOneNum);
        rlOrderTwo = rootView.findViewById(R.id.rlOrderTwo);
        rlOrderTwo.setOnClickListener(onClickListener);
        tvOrderTwoNum = rootView.findViewById(R.id.tvOrderTwoNum);
        rlOrderThree = rootView.findViewById(R.id.rlOrderThree);
        rlOrderThree.setOnClickListener(onClickListener);
        tvOrderThreeNum = rootView.findViewById(R.id.tvOrderThreeNum);
        rlOrderFour = rootView.findViewById(R.id.rlOrderFour);
        rlOrderFour.setOnClickListener(onClickListener);
        tvOrderFourNum = rootView.findViewById(R.id.tvOrderFourNum);
        rlOrderFive = rootView.findViewById(R.id.rlOrderFive);
        rlOrderFive.setOnClickListener(onClickListener);
        tvOrderFiveNum = rootView.findViewById(R.id.tvOrderFiveNum);
        llSellOrder = rootView.findViewById(R.id.llSellOrder);
        rlSellOrderOne = rootView.findViewById(R.id.rlSellOrderOne);
        rlSellOrderOne.setOnClickListener(onClickListener);
        tvSellOrderOneNum = rootView.findViewById(R.id.tvSellOrderOneNum);
        rlSellOrderTwo = rootView.findViewById(R.id.rlSellOrderTwo);
        rlSellOrderTwo.setOnClickListener(onClickListener);
        tvSellOrderTwoNum = rootView.findViewById(R.id.tvSellOrderTwoNum);
        rlSellOrderThree = rootView.findViewById(R.id.rlSellOrderThree);
        rlSellOrderThree.setOnClickListener(onClickListener);
        tvSellOrderThreeNum = rootView.findViewById(R.id.tvSellOrderThreeNum);
        rlSellOrderFour = rootView.findViewById(R.id.rlSellOrderFour);
        rlSellOrderFour.setOnClickListener(onClickListener);
        tvSellOrderFourNum = rootView.findViewById(R.id.tvSellOrderFourNum);

        rlAuctionManager = rootView.findViewById(R.id.rlAuctionManager);
        llBuyAuction = rootView.findViewById(R.id.llBuyAuction);
        rlAuctionOne = rootView.findViewById(R.id.rlAuctionOne);
        tvAuctionOneNum = rootView.findViewById(R.id.tvAuctionOneNum);
        rlAuctionTwo = rootView.findViewById(R.id.rlAuctionTwo);
        tvAuctionTwoNum = rootView.findViewById(R.id.tvAuctionTwoNum);
        rlAuctionThree = rootView.findViewById(R.id.rlAuctionThree);
        tvAuctionThreeNum = rootView.findViewById(R.id.tvAuctionThreeNum);
        rlAuctionFour = rootView.findViewById(R.id.rlAuctionFour);
        tvAuctionFourNum = rootView.findViewById(R.id.tvAuctionFourNum);
        rlAuctionFive = rootView.findViewById(R.id.rlAuctionFive);
        tvAuctionFiveNum = rootView.findViewById(R.id.tvAuctionFiveNum);
        llSellAuction = rootView.findViewById(R.id.llSellAuction);
        rlSellAuctionOne = rootView.findViewById(R.id.rlSellAuctionOne);
        tvSellAuctionOneNum = rootView.findViewById(R.id.tvSellAuctionOneNum);
        rlSellAuctionTwo = rootView.findViewById(R.id.rlSellAuctionTwo);
        tvSellAuctionTwoNum = rootView.findViewById(R.id.tvSellAuctionTwoNum);
        rlSellAuctionThree = rootView.findViewById(R.id.rlSellAuctionThree);
        tvSellAuctionThreeNum = rootView.findViewById(R.id.tvSellAuctionThreeNum);
        rlSellAuctionFour = rootView.findViewById(R.id.rlSellAuctionFour);
        tvSellAuctionFourNum = rootView.findViewById(R.id.tvSellAuctionFourNum);
        rlAuctionManager.setOnClickListener(onClickListener);
        rlAuctionOne.setOnClickListener(onClickListener);
        rlAuctionTwo.setOnClickListener(onClickListener);
        rlAuctionThree.setOnClickListener(onClickListener);
        rlAuctionFour.setOnClickListener(onClickListener);
        rlAuctionFive.setOnClickListener(onClickListener);
        rlSellAuctionOne.setOnClickListener(onClickListener);
        rlSellAuctionTwo.setOnClickListener(onClickListener);
        rlSellAuctionThree.setOnClickListener(onClickListener);
        rlSellAuctionFour.setOnClickListener(onClickListener);

        rlGoodsManager = rootView.findViewById(R.id.rlGoodsManager);
        llGoodsContent = rootView.findViewById(R.id.llGoodsContent);
        rlAddGoods = rootView.findViewById(R.id.rlAddGoods);
        rlSellGoods = rootView.findViewById(R.id.rlSellGoods);
        rlAuctionGoods = rootView.findViewById(R.id.rlAuctionGoods);
        rlWareHouseGoods = rootView.findViewById(R.id.rlWareHouseGoods);
        rlCheckGoods = rootView.findViewById(R.id.rlCheckGoods);
        rlAddGoods.setOnClickListener(onClickListener);
        rlSellGoods.setOnClickListener(onClickListener);
        rlAuctionGoods.setOnClickListener(onClickListener);
        rlWareHouseGoods.setOnClickListener(onClickListener);
        rlCheckGoods.setOnClickListener(onClickListener);

        rlMoneyManager = rootView.findViewById(R.id.rlMoneyManager);
        rlCollect = rootView.findViewById(R.id.rlCollect);
        rlShare = rootView.findViewById(R.id.rlShare);
        rlVip = rootView.findViewById(R.id.rlVip);
        rlAddress = rootView.findViewById(R.id.rlAddress);
        rlBankCard = rootView.findViewById(R.id.rlBankCard);
        rlMoneyManager.setOnClickListener(onClickListener);
        rlCollect.setOnClickListener(onClickListener);
        rlShare.setOnClickListener(onClickListener);
        rlVip.setOnClickListener(onClickListener);
        rlAddress.setOnClickListener(onClickListener);
        rlBankCard.setOnClickListener(onClickListener);

        llMyManagerSecond = rootView.findViewById(R.id.llMyManagerSecond);
        rlBankCardSecond = rootView.findViewById(R.id.rlBankCardSecond);
        rlBankCardSecond.setOnClickListener(onClickListener);

        checkLogin();
        showUserTypeView();
    }

    private void checkLogin() {
        if (BaseApplication.isLogin()) {
            tvToLogin.setVisibility(View.GONE);
            rlHead.setVisibility(View.VISIBLE);
        } else {
            tvToLogin.setVisibility(View.VISIBLE);
            rlHead.setVisibility(View.GONE);
        }
    }

    private void showUserTypeView() {
        if (userType == TYPE_BUYER) {
            HttpUtils.USER_TOKEN = true;
            //买家中心
            rlBackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.bg_low_black));
            tvChangeType.setText("卖家中心");
            tvAuthCompany.setVisibility(View.VISIBLE);
            llNotUseMoney.setVisibility(View.GONE);

            llBuyOrder.setVisibility(View.VISIBLE);
            llSellOrder.setVisibility(View.GONE);
            llBuyAuction.setVisibility(View.VISIBLE);
            llSellAuction.setVisibility(View.GONE);

            rlGoodsManager.setVisibility(View.GONE);
            llGoodsContent.setVisibility(View.GONE);

            rlCollect.setVisibility(View.VISIBLE);
            rlBankCard.setVisibility(View.GONE);
            llMyManagerSecond.setVisibility(View.VISIBLE);
        } else {
            HttpUtils.USER_TOKEN = false;
            //卖家中心
            rlBackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.bg_red));
            tvChangeType.setText("买家中心");
            tvAuthCompany.setVisibility(View.GONE);
            llNotUseMoney.setVisibility(View.VISIBLE);

            llBuyOrder.setVisibility(View.GONE);
            llSellOrder.setVisibility(View.VISIBLE);
            llBuyAuction.setVisibility(View.GONE);
            llSellAuction.setVisibility(View.VISIBLE);

            rlGoodsManager.setVisibility(View.VISIBLE);
            llGoodsContent.setVisibility(View.VISIBLE);

            rlCollect.setVisibility(View.GONE);
            rlBankCard.setVisibility(View.VISIBLE);
            llMyManagerSecond.setVisibility(View.GONE);
        }
    }

    @Override
    public void initData() {
        disposable = new CompositeDisposable();
        EventManager.getInstance().registerListener(onNotifyListener);
        initImagePicker();
        getUserInfo();
        getOrderNumIndex();
    }


    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setMultiMode(false);
        imagePicker.setCrop(true);                            //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(1);                        //选中数量限制
        imagePicker.setStyle(CropImageView.Style.CIRCLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(500);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(500);                         //保存文件的高度。单位像素
    }

    private void getUserInfo() {
        disposable.add(ApiUtils.getInstance().getUserInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultBean<UserInfoBean>>() {
                    @Override
                    public void accept(ResultBean<UserInfoBean> userInfoBeanResultBean) throws Exception {
                        // TODO: 2018/9/17 显示头像和用户名
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                }));
    }

    private void getOrderNumIndex() {
        disposable.add(ApiUtils.getInstance().getMyNumFlag()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultBean<MyNumBean>>() {
                    @Override
                    public void accept(ResultBean<MyNumBean> myNumBeanResultBean) throws Exception {
                        MyNumBean data = myNumBeanResultBean.getData();
                        if (data != null) {
                            tvOrderOneNum.setText(data.getWait_pay_num());
                            tvOrderOneNum.setVisibility(TextUtils.isEmpty(data.getWait_pay_num()) ? View.GONE : View.VISIBLE);
                            tvOrderTwoNum.setText(data.getWait_send_goods_num());
                            tvOrderTwoNum.setVisibility(TextUtils.isEmpty(data.getWait_send_goods_num()) ? View.GONE : View.VISIBLE);
                            tvOrderThreeNum.setText(data.getWait_confirm_goods_num());
                            tvOrderThreeNum.setVisibility(TextUtils.isEmpty(data.getWait_confirm_goods_num()) ? View.GONE : View.VISIBLE);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                }));
    }


    private ChoicePhotoDialog photoDialog;
    public static final int REQUEST_CODE_CHOICE_HEAD = 1001;

    /**
     * 设置头像
     */
    private void setHeadIcon() {
        if (photoDialog == null) {
            photoDialog = new ChoicePhotoDialog(getActivity());
            photoDialog.setCallBackListener(new ChoicePhotoDialog.OnCallBackListener() {
                @Override
                public void takePhoto() {
                    openCamera(REQUEST_CODE_CHOICE_HEAD);
                }

                @Override
                public void toPhotoAlbum() {
                    openAlbum(REQUEST_CODE_CHOICE_HEAD);
                }
            });
        }
        photoDialog.show();
    }

    private void openCamera(int requestCode) {
        Intent intent = new Intent(getActivity(), ImageGridActivity.class);
        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
        startActivityForResult(intent, requestCode);
    }

    private void openAlbum(int requestCode) {
        Intent intent = new Intent(getActivity(), ImageGridActivity.class);
        startActivityForResult(intent, requestCode);
    }

    private void changeUserType() {
        userType = userType == TYPE_BUYER ? TYPE_SELLER : TYPE_BUYER;
        showUserTypeView();
    }

    private void toAuthActivity() {
        startActivity(new Intent(getActivity(), PersonAuthActivity.class));
    }

    private void toAuthCompanyActivity() {
        startActivity(new Intent(getActivity(), CompanyAuthActivity.class));
    }


    private EventManager.OnNotifyListener onNotifyListener = new EventManager.OnNotifyListener() {
        @Override
        public void onNotify(Object object, String eventTag) {
            loginSuccess(eventTag);
        }
    };

    private void loginSuccess(String eventTag) {
        if (eventTag.equals(ConstantMsg.LOGIN_SUCCESS)) {
            checkLogin();
        }
    }

    private void toOrderMainActivity(int type, int target) {
        Intent intent = new Intent(getActivity(), OrderMainActivity.class);
        intent.putExtra(OrderParams.TYPE, type);
        intent.putExtra(OrderParams.TARGET, target);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                //添加图片返回
                if (data != null) {
                    if (requestCode == REQUEST_CODE_CHOICE_HEAD) {
                        ArrayList<ImageItem> mainPicLists = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                        ImageItem imageItem = mainPicLists.get(0);
                        int index = imageItem.path.lastIndexOf("/");
                        imageItem.name = imageItem.path.substring(index + 1, imageItem.path.length());
                        String base64Head = FileUtils.imgToBase64(mainPicLists.get(0).path);
                        uploadHeadIcon(mainPicLists, base64Head);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadHeadIcon(ArrayList<ImageItem> mainPicLists, String base64Head) {
        showLoadingDialog();
        disposable.add(ApiUtils.getInstance().userUploadImage(base64Head, mainPicLists.get(0).name, "rate")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultBean<UploadImageBean>>() {
                    @Override
                    public void accept(ResultBean<UploadImageBean> uploadImageBeanResultBean) throws Exception {
                        setHeadUrl(uploadImageBeanResultBean.getData().getUrl());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        hideLoadingDialog();
                        ToastUtils.showShort("上传头像失败");
                    }
                }));
    }

    private void setHeadUrl(final String url) {
        disposable.add(ApiUtils.getInstance().setHeadImg(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultBean>() {
                    @Override
                    public void accept(ResultBean loginBeanResultBean) throws Exception {
                        hideLoadingDialog();
                        GlideUtils.loadImage(getActivity(), url, imgHead);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        hideLoadingDialog();
                        ToastUtils.showShort("设置头像失败");
                    }
                }));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventManager.getInstance().unRegisterListener(onNotifyListener);
    }

}
