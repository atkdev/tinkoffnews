package com.tinkoff.news.features.newslist.presentation

import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.text.Spanned
import com.github.nitrico.lastadapter.LastAdapter
import com.tinkoff.news.BR
import com.tinkoff.news.R
import com.tinkoff.news.databinding.ActivityNewsListBinding
import com.tinkoff.news.di.inject
import com.tinkoff.news.features.newscontent.presentation.NewsContentActivity
import com.tinkoff.news.features.newslist.domain.model.NewsListItem
import com.tinkoff.news.utils.text.fromHtml

class NewsListActivity : AppCompatActivity() {


    private lateinit var binding: ActivityNewsListBinding
    private val bindingModel = BindingModel()


    private val viewModel: NewsListViewModel by inject()
    private val presenter: NewsListPresenter by inject()
    private val router: NewsListRouter by inject()


    ///////////////////////////////////////////////////////////////////////////
    // Activity lifecycle
    ///////////////////////////////////////////////////////////////////////////
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()

        initActivity()

        subscribeOnViewModel()

        subscribeOnViewModelEvents()

        subscribeOnRouter()
    }

    private fun initActivity() {
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }


    ///////////////////////////////////////////////////////////////////////////
    // Initialization methods
    ///////////////////////////////////////////////////////////////////////////
    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_news_list)
        binding.setLifecycleOwner(this)
        binding.model = bindingModel
        binding.presenter = presenter
    }


    private fun subscribeOnViewModel() {
        viewModel.state.observe(this, Observer {

            val state = viewModel.state.value
            when (state) {
                is NewsListViewState.Loading -> {
                    bindingModel.loading.set(true)
                    bindingModel.showError.set(false)
                }
                is NewsListViewState.LoadingFailed -> {
                    bindingModel.loading.set(true)
                    bindingModel.showError.set(true)
                }
                is NewsListViewState.Data -> {
                    bindingModel.loading.set(false)
                    patchNewsListWith(state.newsList)
                }
            }
        })
    }

    private fun subscribeOnViewModelEvents() {
        viewModel.event.observe(this, Observer {
            when (it) {
                is NewsListViewModelEvent.LoadingFinished ->
                    binding.swiperefresh.isRefreshing = false

            }
        })
    }

    private fun patchNewsListWith(newItems: List<NewsListItem>) {

        val curItems = bindingModel.newsList.map { it.model }.toMutableList()
        val newsList = bindingModel.newsList

        if(curItems.count() == 0) {
            newsList.addAll(
                    newItems.map {
                        NewsListItemBindingModel(it)
                    }
            )
        } else {
            for (i in 0 until newItems.count()) {
                val item = NewsListItemBindingModel(newItems[i])
                when {
                    i >= curItems.count() -> newsList.add(item)
                    else -> {
                        if (curItems[i] != curItems[i]) {
                            newsList.add(item)
                            curItems.add(i, newItems[i])
                        }
                    }
                }
            }

            if(newItems.count() < newsList.count()) {
                newsList.subList(newItems.count(),newsList.count()).clear()
            }
        }
    }

    //TODO: move router implementation out of activity
    private fun subscribeOnRouter() {
        router.routerAction.observe(this, Observer {
            when(it) {
                is NewsListRouterAction.OpenNews ->
                    startActivity(NewsContentActivity.createIntent(this, it.news.id))

            }
        })
    }


    ///////////////////////////////////////////////////////////////////////////
    // Binding models
    ///////////////////////////////////////////////////////////////////////////
    class BindingModel {
        val newsList = ObservableArrayList<NewsListItemBindingModel>()
        val loading = ObservableField<Boolean>()
        val showError = ObservableField<Boolean>()

        var adapter: RecyclerView.Adapter<*>? =
                LastAdapter(newsList, BR.item)
                        .map<NewsListItemBindingModel>(R.layout.item_news_list)
    }

    inner class NewsListItemBindingModel(val model: NewsListItem) {

        val text: Spanned
            get() = model.text.fromHtml()

        fun onClick() {
            presenter.didClickNewsAt(bindingModel.newsList.indexOf(this))
        }
    }

}
