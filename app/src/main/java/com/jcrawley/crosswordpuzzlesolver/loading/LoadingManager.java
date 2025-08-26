package com.jcrawley.crosswordpuzzlesolver.loading;

import android.view.View;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LoadingManager {

    private LoadingViewsAnimator loadingViewsAnimator;
    private CountDownLatch countDownLatch;
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private SearchRunner searchRunner;
    private View listView;
    private View noResultsView;


    public LoadingManager(LoadingViewsAnimator loadingViewsAnimator,
                          SearchRunner searchRunner,
                          View listView,
                          View noResultsView){
        this.loadingViewsAnimator = loadingViewsAnimator;
        this.searchRunner = searchRunner;
        this.listView = listView;
        this.noResultsView = noResultsView;
    }


    public void startLoading(){
        fadeOutListView();
        loadingViewsAnimator.fadeInViewAndStartAnimation();
        searchRunner.disableSearch();
        countDownLatch = new CountDownLatch(2);
        decrementLatchAfterAWhile();
    }


    public void notifyLoadingComplete(int numberOfResults){
        countDownLatch.countDown();
        try{
            countDownLatch.await();
            Runnable runnable = numberOfResults > 0 ? this::fadeInListView : this::fadeInNoResultsView;
            loadingViewsAnimator.stopAnimationAndFadeOutView(runnable);
            searchRunner.enableSearch();

        }catch (InterruptedException e){
            handleException(e.getMessage());
        }
    }


    private void fadeOutListView(){

    }


    private void fadeInListView(){

    }



    private void fadeInNoResultsView(){

    }




    private void decrementLatchAfterAWhile(){
        executorService.schedule(()->countDownLatch.countDown(), 1200, TimeUnit.MILLISECONDS);
    }


    private void handleException(String msg){
        System.out.println(msg);
    }

}
