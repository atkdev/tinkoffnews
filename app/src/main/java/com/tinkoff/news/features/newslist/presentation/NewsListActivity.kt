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
import com.tinkoff.news.app.inject
import com.tinkoff.news.databinding.ActivityNewsListBinding
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
                    val newItems = state.newsList
                            .sortedByDescending { it.publicationDate }
                            .map { NewsListItemBindingModel(it) }
                    patchNewsListWith(newItems)
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

    private fun patchNewsListWith(newItems: List<NewsListItemBindingModel>) {
        for (i in 0 until newItems.count()) {

            val newsList = bindingModel.newsList

            when {
                newsList.count() == 0 -> newsList.addAll(newItems)
                i >= newsList.count() -> newsList.add( newItems[i] )
                else -> {
                    if (newsList[i] != newsList[i]) {
                        newsList.removeAt(i)
                        newsList.add(i, newItems[i])
                    }
                }
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

    inner class NewsListItemBindingModel(
            private val model: NewsListItem
    ) {
        val text: Spanned
            get() = model.text.fromHtml()

        fun onClick() {
            presenter.didClickNewsAt(bindingModel.newsList.indexOf(this))
        }
    }

}
