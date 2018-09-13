package com.chunlangjiu.app.amain.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chunlangjiu.app.R;
import com.chunlangjiu.app.amain.bean.AuctionListBean;
import com.pkqup.commonlibrary.glide.GlideUtils;
import com.pkqup.commonlibrary.view.countdownview.CountdownView;

import java.util.List;

/**
 * @CreatedbBy: liucun on 2018/6/26
 * @Describe:
 */
public class AuctionListAdapter extends BaseQuickAdapter<AuctionListBean.AuctionBean, BaseViewHolder> {

    private Context context;


    public AuctionListAdapter(Context context, int layoutResId, List<AuctionListBean.AuctionBean> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, AuctionListBean.AuctionBean item) {
        ImageView imageView = helper.getView(R.id.img_pic);
        GlideUtils.loadImage(context, item.getImage_default_id(), imageView);
        helper.setText(R.id.tv_name, item.getTitle());
        helper.setText(R.id.tvStartPrice, item.getStarting_price());
        helper.setText(R.id.tvSellPrice, item.getStarting_price());

        CountdownView countdownView = helper.getView(R.id.countdownView);
        String end_time = item.getEnd_time();
        try {
            long endTime = 0;
            if (!TextUtils.isEmpty(end_time)) {
                endTime = Long.parseLong(end_time);
            }
            if ((endTime * 1000 - System.currentTimeMillis()) > 0) {
                countdownView.start(endTime * 1000 - System.currentTimeMillis());
                dealWithLifeCycle(helper, helper.getAdapterPosition(), item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 以下两个接口代替 activity.onStart() 和 activity.onStop(), 控制 timer 的开关
     */
    private void dealWithLifeCycle(final BaseViewHolder viewHolder, final int position, final AuctionListBean.AuctionBean item) {
        final CountdownView countdownView = viewHolder.getView(R.id.countdownView);
        countdownView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                String end_time = item.getEnd_time();
                try {
                    long endTime = 0;
                    if (!TextUtils.isEmpty(end_time)) {
                        endTime = Long.parseLong(end_time);
                    }
                    if ((endTime * 1000 - System.currentTimeMillis()) > 0) {
                        countdownView.start(endTime * 1000 - System.currentTimeMillis());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                countdownView.stop();
            }
        });
    }

    public static class MyViewHolder extends BaseViewHolder {
        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }

}

