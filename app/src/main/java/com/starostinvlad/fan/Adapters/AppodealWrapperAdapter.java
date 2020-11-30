package com.starostinvlad.fan.Adapters;

import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.NativeAd;
import com.appodeal.ads.NativeCallbacks;
import com.appodeal.ads.native_ad.views.NativeAdViewNewsFeed;
import com.starostinvlad.fan.R;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * Wrapper adapter to show Native Ad in recycler view with fixed step
 */
public class AppodealWrapperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements NativeCallbacks {

    private static final int DEFAULT_NATIVE_STEP = 5;

    private static final int VIEW_HOLDER_NATIVE_AD_TYPE = 600;
    private final String TAG = getClass().getSimpleName();


    private RecyclerView.Adapter<RecyclerView.ViewHolder> userAdapter;
    private int nativeStep = DEFAULT_NATIVE_STEP;

    private SparseArray<NativeAd> nativeAdList = new SparseArray<>();

    /**
     * @param userAdapter user adapter
     * @param nativeStep  step show {@link com.appodeal.ads.NativeAd}
     */
    public AppodealWrapperAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> userAdapter, int nativeStep) {
        this.userAdapter = userAdapter;
        this.nativeStep = nativeStep + 1;

        userAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onChanged() {
                super.onChanged();

                AppodealWrapperAdapter.this.notifyDataSetChanged();

                fillListWithAd();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                AppodealWrapperAdapter.this.notifyDataSetChanged();

                fillListWithAd();
            }
        });

        Appodeal.setNativeCallbacks(this);

        fillListWithAd();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_HOLDER_NATIVE_AD_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_gridview_ad_item, parent, false);
            return new AdViewHolder(view);
        } else {
            return userAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AdViewHolder) {
            final ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            StaggeredGridLayoutManager.LayoutParams sglp = (StaggeredGridLayoutManager.LayoutParams) lp;
            sglp.setFullSpan(true);
            holder.itemView.setLayoutParams(sglp);
            AdViewHolder viewHolder = (AdViewHolder) holder;
            viewHolder.fill(nativeAdList.get(position));
        } else {
            userAdapter.onBindViewHolder(holder, getPositionInUserAdapter(position));
        }
    }

    @Override
    public int getItemCount() {
        int resultCount = 0;

        resultCount += getNativeAdsCount();
        resultCount += getUserAdapterItemCount();

        return resultCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (isNativeAdPosition(position)) {
            return VIEW_HOLDER_NATIVE_AD_TYPE;
        } else {
            return userAdapter.getItemViewType(getPositionInUserAdapter(position));
        }
    }

    /**
     * Destroy all used native ads
     */
    public void destroyNativeAds() {
        if (nativeAdList != null) {
            for (int i = 0; i < nativeAdList.size(); i++) {
                NativeAd nativeAd = nativeAdList.valueAt(i);
                nativeAd.destroy();
            }

            nativeAdList.clear();
        }
    }

    @Override
    public void onNativeLoaded() {
        fillListWithAd();
    }

    @Override
    public void onNativeFailedToLoad() {

    }

    @Override
    public void onNativeShown(NativeAd nativeAd) {

    }

    @Override
    public void onNativeShowFailed(NativeAd nativeAd) {
        Log.e(TAG, "onNativeShowFailed: show error!");
    }

    @Override
    public void onNativeClicked(NativeAd nativeAd) {

    }

    @Override
    public void onNativeExpired() {

    }


    /**
     * @return count of loaded ads {@link com.appodeal.ads.NativeAd}
     */
    private int getNativeAdsCount() {
        if (nativeAdList != null) {
            return nativeAdList.size();
        }

        return 0;
    }

    /**
     * @return user items count
     */
    private int getUserAdapterItemCount() {
        if (userAdapter != null) {
            return userAdapter.getItemCount();
        }

        return 0;
    }

    /**
     * @param position index in wrapper adapter
     * @return {@code true} if item by position is {@link com.appodeal.ads.NativeAd}
     */
    private boolean isNativeAdPosition(int position) {
        return nativeAdList.get(position) != null;
    }

    /**
     * Method for searching position in user adapter
     *
     * @param position index in wrapper adapter
     * @return index in user adapter
     */
    private int getPositionInUserAdapter(int position) {
        return position - Math.min(nativeAdList.size(), position / nativeStep);
    }

    /**
     * Method for filling list with {@link com.appodeal.ads.NativeAd}
     */
    private void fillListWithAd() {
        int insertPosition = findNextAdPosition();

        NativeAd nativeAd;
        while (canUseThisPosition(insertPosition) && (nativeAd = getNativeAdItem()) != null) {
            nativeAdList.put(insertPosition, nativeAd);
            notifyItemInserted(insertPosition);

            insertPosition = findNextAdPosition();
        }
    }

    /**
     * Get native ad item
     *
     * @return {@link com.appodeal.ads.NativeAd}
     */
    @Nullable
    private NativeAd getNativeAdItem() {
        List<NativeAd> ads = Appodeal.getNativeAds(1);
        return !ads.isEmpty() ? ads.get(0) : null;
    }

    /**
     * Method for finding next position suitable for {@link com.appodeal.ads.NativeAd}
     *
     * @return position for next native ad view
     */
    private int findNextAdPosition() {
        if (nativeAdList.size() > 0) {
            return nativeAdList.keyAt(nativeAdList.size() - 1) + nativeStep;
        }
        return nativeStep - 1;
    }

    /**
     * @param position index in wrapper adapter
     * @return {@code true} if you can add {@link com.appodeal.ads.NativeAd} to this position
     */
    private boolean canUseThisPosition(int position) {
        return nativeAdList.get(position) == null && getItemCount() > position;
    }

    static class AdViewHolder extends RecyclerView.ViewHolder {
        NativeAdViewNewsFeed nav_nf;

        AdViewHolder(View itemView) {
            super(itemView);
            nav_nf = itemView.findViewById(R.id.native_ad_view_news_feed);

        }

        void fill(NativeAd nativeAd) {
            nav_nf.setNativeAd(nativeAd);
        }
    }
}