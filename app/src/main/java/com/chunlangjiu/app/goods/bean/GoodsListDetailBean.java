package com.chunlangjiu.app.goods.bean;

import java.util.List;

/**
 * @CreatedbBy: liucun on 2018/8/6
 * @Describe:
 */
public class GoodsListDetailBean {

    private String item_id;
    private String title;
    private String image_default_id;
    private String price;
    private String mkt_price;
    private String label;
    private String view_count ;//关注人数
    private String rate_count  ;//评价条数
    private String sold_quantity;
    private List promotion;//促销信息
    private GiftDetail gift;


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getView_count() {
        return view_count;
    }

    public void setView_count(String view_count) {
        this.view_count = view_count;
    }

    public String getRate_count() {
        return rate_count;
    }

    public void setRate_count(String rate_count) {
        this.rate_count = rate_count;
    }

    public String getMkt_price() {
        return mkt_price;
    }

    public void setMkt_price(String mkt_price) {
        this.mkt_price = mkt_price;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage_default_id() {
        return image_default_id;
    }

    public void setImage_default_id(String image_default_id) {
        this.image_default_id = image_default_id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSold_quantity() {
        return sold_quantity;
    }

    public void setSold_quantity(String sold_quantity) {
        this.sold_quantity = sold_quantity;
    }

    public List getPromotion() {
        return promotion;
    }

    public void setPromotion(List promotion) {
        this.promotion = promotion;
    }

    public GiftDetail getGift() {
        return gift;
    }

    public void setGift(GiftDetail gift) {
        this.gift = gift;
    }

    public class PromotionDetail{
        private String promotion_id;
        private String tag;

        public String getPromotion_id() {
            return promotion_id;
        }

        public void setPromotion_id(String promotion_id) {
            this.promotion_id = promotion_id;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
    }

    public class GiftDetail{
        private String gift_id;
        private String promotion_tag;

        public String getGift_id() {
            return gift_id;
        }

        public void setGift_id(String gift_id) {
            this.gift_id = gift_id;
        }

        public String getPromotion_tag() {
            return promotion_tag;
        }

        public void setPromotion_tag(String promotion_tag) {
            this.promotion_tag = promotion_tag;
        }
    }


}
