package com.tinkoff.news.features.newscontent.presentation

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.OnLifecycleEvent
import com.tinkoff.news.features.newscontent.domain.NewsContentInteractor
import com.tinkoff.news.utils.presentation.BaseViewModel
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable



class NewsContentViewModelImpl(
        private val interactor: NewsContentInteractor
) : BaseViewModel(),
        NewsContentViewModel,
        NewsContentPresenter,
        NewsContentRouter {

    override val state = MutableLiveData<NewsContentViewState>()

    override val routerAction = MutableLiveData<NewsContentRouterAction>()


    private var disposables = CompositeDisposable()


    ///////////////////////////////////////////////////////////////////////////
    // NewsContentPresenter
    ///////////////////////////////////////////////////////////////////////////
    override fun tryReloadData() {

        if(state.value !is NewsContentViewState.Data) {
            state.value = NewsContentViewState.Loading()
        }

        disposables.add(
                interactor.getCachedNewsContent()
                        .doOnSuccess {
                            state.value = NewsContentViewState.Data(it)
                        }
                        .onErrorResumeNext{ _:Throwable -> Maybe.empty() }
                        .isEmpty.flatMap {
                            interactor.getNewsContent()
                        }
                        .subscribe(
                        {
                            state.value = NewsContentViewState.Data(it)
                        },
                        {
                            if(state.value !is NewsContentViewState.Data) {
                                state.value = NewsContentViewState.LoadingFailed()
                            }
                        }
                )
        )
    }

    override fun didClickClose() {
        routerAction.postValue( NewsContentRouterAction.Close() )
    }



    ///////////////////////////////////////////////////////////////////////////
    // LifecycleObserver
    ///////////////////////////////////////////////////////////////////////////
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        tryReloadData()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        disposables.dispose()
    }
}