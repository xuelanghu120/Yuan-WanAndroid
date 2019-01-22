package com.example.yuan_wanandroid.view.home;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.example.yuan_wanandroid.R;
import com.example.yuan_wanandroid.adapter.ArticlesAdapter;
import com.example.yuan_wanandroid.base.fragment.BaseMvpFragment;
import com.example.yuan_wanandroid.contract.HomeFragmentContract;
import com.example.yuan_wanandroid.di.module.fragment.HomeFragmentModule;
import com.example.yuan_wanandroid.model.entity.Article;
import com.example.yuan_wanandroid.model.entity.BannerData;
import com.example.yuan_wanandroid.presenter.HomeFragmentPresenter;
import com.example.yuan_wanandroid.utils.BannerImageLoader;
import com.example.yuan_wanandroid.utils.CommonUtils;
import com.example.yuan_wanandroid.view.MainActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/01/21
 *     desc   : 首页
 * </pre>
 */


public class HomeFragment extends BaseMvpFragment<HomeFragmentPresenter> implements HomeFragmentContract.View {

    private static final String TAG = "HomeFragment";

    @Inject
    HomeFragmentPresenter mPresenter;
    @Inject
    @Named("bannerTitles")
    List<String> bannerTitles;
    @Inject
    @Named("bannerImages")
    List<String> bannerImages;
    @Inject
    LinearLayoutManager mLinearLayoutManager;
    @Inject
    List<Article> mArticleList;
    @Inject
    ArticlesAdapter mArticlesAdapter;


    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    private Banner banner;
    private int mPageNum = 0;//首页文章页数
    private boolean isRefresh = false; //是否在上拉刷新

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_nav_home;
    }

    @Override
    protected void initView() {
        super.initView();
        initRecyclerView();
        initRefreshView();
    }

    private void initRecyclerView(){
        //添加轮播图到RecyclerView的Header
        View bannerLayout = LayoutInflater.from(mActivity).inflate(R.layout.banner_layout, null);
        banner = bannerLayout.findViewById(R.id.banner);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mArticlesAdapter.openLoadAnimation();
        mArticlesAdapter.addHeaderView(bannerLayout);
        mRecyclerView.setAdapter(mArticlesAdapter);
    }

    private void initRefreshView(){
        mRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            mPageNum++;
            isRefresh =false;
            mPresenter.loadMoreArticles(mPageNum);

        });
        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mPageNum = 0;
            isRefresh = true;
            mPresenter.loadMoreArticles(mPageNum);

        });
    }

    @Override
    protected void inject() {
        ((MainActivity) getActivity())
                .getComponent()
                .getHomeFragmentComponent(new HomeFragmentModule())
                .inject(this);
    }

    @Override
    protected HomeFragmentPresenter getPresenter() {
        return mPresenter;
    }


    @Override
    protected void loadData() {
        mPresenter.loadBannerData();
        mPresenter.loadArticles(0);
    }

    @Override
    public void showToast(String msg) {
        CommonUtils.toastShow(msg);
    }


    @Override
    public void showBannerData(List<BannerData> bannerDataList) {
        if (!CommonUtils.isEmptyList(bannerTitles)) {
            bannerTitles.clear();
            bannerImages.clear();
        }

        //获得标题,图片
        for (BannerData bannerData : bannerDataList) {
            bannerTitles.add(bannerData.getTitle());
            bannerImages.add(bannerData.getImagePath());
        }
        //设置banner
        banner.setImageLoader(new BannerImageLoader())
                .setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)//显示圆形指示器和标题（水平显示）
                .setImages(bannerImages)//设置图片集合
                .setBannerAnimation(Transformer.Default)//设置轮播动画
                .setBannerTitles(bannerTitles)//设置标题集合
                .setIndicatorGravity(BannerConfig.RIGHT)//设置指示器位置，右边
                .setDelayTime(3000)//设置轮播事件间隔
                .start();
    }

    @Override
    public void showArticles(List<Article> articlesList) {
        if(!CommonUtils.isEmptyList(mArticleList)){
            mArticleList.clear();
        }
        mArticleList.addAll(articlesList);
        mArticlesAdapter.notifyDataSetChanged();
    }

    @Override
    public void showMoreArticles(List<Article> articleList) {
        if(isRefresh){
            if(!CommonUtils.isEmptyList(mArticleList)){
                mArticleList.clear();
            }
            mRefreshLayout.finishRefresh();
        }else{
            mRefreshLayout.finishLoadMore();
        }
        mArticleList.addAll(articleList);
        mArticlesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (banner != null) {
            banner.startAutoPlay();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (banner != null) {
            banner.stopAutoPlay();
        }
    }

}
