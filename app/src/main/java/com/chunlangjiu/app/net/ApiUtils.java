package com.chunlangjiu.app.net;

import com.chunlangjiu.app.amain.bean.LoginBean;
import com.chunlangjiu.app.amain.bean.MainClassBean;
import com.chunlangjiu.app.goods.bean.EvaluateListBean;
import com.chunlangjiu.app.goods.bean.GoodsDetailBean;
import com.chunlangjiu.app.goods.bean.GoodsListBean;
import com.chunlangjiu.app.goods.bean.ShopInfoBean;
import com.chunlangjiu.app.user.bean.AddressListBean;
import com.chunlangjiu.app.user.bean.MyNumBean;
import com.pkqup.commonlibrary.net.HttpUtils;
import com.pkqup.commonlibrary.net.bean.ResultBean;

import io.reactivex.Flowable;

/**
 * @CreatedbBy: liucun on 2018/7/6
 * @Describe:
 */
public class ApiUtils {

    private volatile static ApiUtils mInstance;
    private ApiService apiService;

    public static ApiUtils getInstance() {
        if (mInstance == null) {
            synchronized (ApiUtils.class) {
                if (mInstance == null) {
                    mInstance = new ApiUtils();
                }
            }
        }
        return mInstance;
    }

    public void init() {
        apiService = HttpUtils.getInstance().getRetrofit().create(ApiService.class);
    }

    public ApiService getApiService() {
        if (null == apiService) {
            apiService = HttpUtils.getInstance().getRetrofit().create(ApiService.class);
        }
        return apiService;
    }

    public Flowable<ResultBean> getAuthSms(String mobile) {
        return apiService.getAuthSms("user.sendSms", "v1", mobile);
    }

    public Flowable<ResultBean<LoginBean>> login(String mobile, String code) {
        return apiService.login("user.oauthlogin", "v2", mobile, code);
    }

    public Flowable<ResultBean<MainClassBean>> getMainClass() {
        return apiService.getGoodsClass("category.itemCategory", "v1");
    }

    public Flowable<ResultBean<GoodsListBean>> getGoodsList(String cat_id, int page_no, String orderBy) {
        return apiService.getGoodsList("item.search", "v1", cat_id, page_no, 20, orderBy);
    }

    public Flowable<ResultBean<GoodsDetailBean>> getGoodsDetail(String item_id) {
        return apiService.getGoodsDetail("item.detail", "v1", item_id);
    }

    public Flowable<ResultBean<EvaluateListBean>> getEvaluateList(String item_id, int page_no) {
        return apiService.getEvaluateList("item.rate.list", "v1", 0, item_id, page_no, 10);
    }

    public Flowable<ResultBean<ShopInfoBean>> getShopInfo(String shopId) {
        return apiService.getShopInfo("shop.basic", "v1", shopId);
    }


    public Flowable<ResultBean> newAddress(String name, String mobile, String area, String addr, String def_addr) {
        return apiService.newAddress("member.address.create", "v1", name, mobile, area, addr, def_addr);
    }

    public Flowable<ResultBean<AddressListBean>> getAddressList() {
        return apiService.getAddressList("member.address.list", "v1");
    }

    public Flowable<ResultBean> editAddress(String name, String mobile, String area, String addr, String def_addr, String addressId) {
        return apiService.editAddress("member.address.update", "v1", name, mobile, area, addr, def_addr, addressId);
    }

    public Flowable<ResultBean> deleteAddress(String addressId) {
        return apiService.deleteAddress("member.address.delete", "v1", addressId);
    }

    public Flowable<ResultBean> setDefault(String addressId) {
        return apiService.setDefault("member.address.setDefault", "v1", addressId);
    }

    public Flowable<ResultBean<MyNumBean>> getMyNumFlag(String addressId) {
        return apiService.getMyNumFlag("member.index", "v1");
    }

    public Flowable<ResultBean> getStoreClass() {
        return apiService.getStoreClass("category.chateauCat", "v1");
    }

    public Flowable<ResultBean> getStoreList(String id, int pageNum) {
        return apiService.getStoreList("category.chateauList", "v1", id, pageNum, 20);
    }

    public Flowable<ResultBean> getStoreDetail(String id) {
        return apiService.getStoreDetail("category.chateauDetail", "v1", id);
    }


    public Flowable<ResultBean> getCartList() {
        return apiService.getCartList("cart.get", "v1", "cart", "wap");
    }

    public Flowable<ResultBean> addGoodsToCart(int num, String sku_id) {
        return apiService.addGoodsToCart("cart.add", "v1", num, sku_id, "item", "cart");
    }

    public Flowable<ResultBean> deleteCartItem(String cart_id) {
        return apiService.deleteCartItem("cart.del", "v1", cart_id);
    }

    //cart_params: 	[{"cart_id":1,"is_checked":1,"selected_promotion":13,"totalQuantity":2},{"cart_id":7,"is_checked":1,"selected_promotion":0,"totalQuantity":1}]
    public Flowable<ResultBean> updateCartData(String cart_params) {
        return apiService.updateCartData("cart.update", "v1", "item", cart_params);
    }

    public Flowable<ResultBean> getCartCount() {
        return apiService.getCartCount("cart.count", "v1");
    }
}
