package com.tinkoff.news.features.newscontent.presentation

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Spanned
import android.view.MenuItem
import com.tinkoff.news.R
import com.tinkoff.news.app.inject
import com.tinkoff.news.databinding.ActivityNewsContentBinding
import com.tinkoff.news.features.newscontent.domain.model.NewsContent
import com.tinkoff.news.utils.text.fromHtml


class NewsContentActivity : AppCompatActivity() {

    companion object {

        private const val NEWS_ID_EXTRA = "NEWS_ID_EXTRA"

        fun createIntent(context: Context, newsId: String): Intent {
            val intent = Intent(context, NewsContentActivity::class.java)
            intent.putExtra(NEWS_ID_EXTRA, newsId)
            return intent
        }
    }


    private lateinit var binding: ActivityNewsContentBinding
    private val bindingModel = BindingModel()

    private val viewModel: NewsContentViewModel by inject()
    private val presenter: NewsContentPresenter by inject()
    private val router: NewsContentRouter by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()

        initActivity()

        subscribeOnViewModel()

        subscribeOnRouter()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> presenter.didClickClose()
        }
        return true
    }


    ///////////////////////////////////////////////////////////////////////////
    // Initialization methods
    ///////////////////////////////////////////////////////////////////////////
    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_news_content)
        binding.setLifecycleOwner(this)
        binding.model = bindingModel
        binding.presenter = presenter
    }

    private fun initActivity() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun subscribeOnViewModel() {
        viewModel.state.observe(this, Observer {

            when (it) {
                is NewsContentViewState.Loading -> {
                    bindingModel.loading.set(true)
                    bindingModel.showError.set(false)
                }
                is NewsContentViewState.LoadingFailed -> {
                    bindingModel.loading.set(true)
                    bindingModel.showError.set(true)
                }
                is NewsContentViewState.Data -> {
                    bindingModel.loading.set(false)
                    bindingModel.newsContent.set(
                            NewsContentBindingModel(it.newsContent)
                    )
                }
            }
        })
    }



    //TODO: move router implementation out of activity
    private fun subscribeOnRouter() {
        router.routerAction.observe(this, Observer {
            when (it) {
                is NewsContentRouterAction.Close ->
                    finish()
            }
        })
    }


    ///////////////////////////////////////////////////////////////////////////
    // Getters/setters
    ///////////////////////////////////////////////////////////////////////////
    fun getNewsId(): String =
            intent.extras[NEWS_ID_EXTRA] as String


    ///////////////////////////////////////////////////////////////////////////
    // Binding models
    ///////////////////////////////////////////////////////////////////////////
    class BindingModel {
        val newsContent = ObservableField<NewsContentBindingModel>()
        val loading = ObservableField<Boolean>()
        val showError = ObservableField<Boolean>()

    }

    class NewsContentBindingModel(
            private val model: NewsContent
    ) {
        val text: Spanned
            get() = model.text.fromHtml()
    }

}
