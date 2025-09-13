package org.example.wan.kuikly.page.home

import com.tencent.kuikly.core.base.ComposeAttr
import com.tencent.kuikly.core.base.ComposeEvent
import com.tencent.kuikly.core.base.ComposeView
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.coroutines.launch
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuiklyx.coroutines.Kuikly
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.wan.kuikly.data.Articles
import org.example.wan.kuikly.data.BannerItem
import org.example.wan.kuikly.data.remote.WanAPI.BANNER_LIST
import org.example.wan.kuikly.data.remote.WanAPI.BASE_URL
import org.example.wan.kuikly.data.remote.WanAPI.HOME_LIST
import org.example.wan.kuikly.data.remote.biz
import org.example.wan.kuikly.data.remote.ktorClient
import org.example.wan.kuikly.data.remote.onFailure
import org.example.wan.kuikly.data.remote.onSuccess
import org.example.wan.kuikly.data.remote.runCatchingKtor
import org.example.wan.kuikly.data.remote.runResponseData
import org.example.wan.kuikly.page.common.ArticleList
import org.example.wan.kuikly.page.common.TreeTabItem
import org.example.wan.kuikly.utils.ifNotNull
import org.example.wan.kuikly.utils.lifecycleScope
import org.example.wan.kuikly.utils.log
import org.example.wan.kuikly.utils.networkModule

internal class HomeListView : ComposeView<HomeListViewAttr, HomeListViewEvent>() {

    private val tabItem by observable(TreeTabItem().apply {
        moduleName = "首页"
        tabTitle = "首页"
    })

    override fun createEvent(): HomeListViewEvent {
        return HomeListViewEvent()
    }

    override fun createAttr(): HomeListViewAttr {
        return HomeListViewAttr()
    }

    override fun created() {
        super.created()

        loadBanner()
        loadList()
    }

    private fun loadBanner() {
        val url = BASE_URL + BANNER_LIST

        log("url = $url")
        networkModule.requestGet(url, JSONObject().apply {
//            put("", "")
        }) { data, success, errorMsg, response ->
            runResponseData {
                response to data
            }.onFailure {
                it.biz(this)
            }.onSuccess<List<BannerItem>> {
                it.ifNotNull {
                    tabItem.bannerList.clear()
                    tabItem.bannerList += it
                }
            }
        }

    }

    private fun loadList() {
        log("${tabItem.moduleName} 列表加载")

        var page = 0
        val url = BASE_URL + HOME_LIST.run {
            this.replace("{page}", "$page")
        }
        val param = JSONObject().apply {
            put("page_size", "10")
        }

        fetchList(url, param)
//        fetchListKtor(url, param)

    }

    private fun fetchList(url: String, param: JSONObject) {
        log("url = $url")
        networkModule.requestGet(url, param) { data, success, errorMsg, response ->
            runResponseData {
                response to data
            }.onFailure {
                it.biz(this)
            }.onSuccess<Articles> {
                it.ifNotNull {
                    tabItem.articleList += it.datas
                }
            }
        }
    }

    private fun fetchListKtor(url: String, param: JSONObject) {
        lifecycleScope.launch {
            runCatchingKtor {
                ktorClient.get(url) {
                    param.toMap().entries.forEach {
                        parameter(it.key, it.value)
                    }
                }
            }.onFailure {
                it.biz(this@HomeListView)
            }.onSuccess<Articles> {
                withContext(Dispatchers.Kuikly[this@HomeListView]) {
                    it.ifNotNull {
                        tabItem.articleList += it.datas
                    }
                }
            }
        }
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            ArticleList {
                attr {
                    tabItem = ctx.tabItem
                    listHeight = (
                            ctx.pagerData.pageViewHeight
                                    - ctx.pagerData.safeAreaInsets.top
                                    - ctx.pagerData.safeAreaInsets.bottom
                                    //                        - 50f // 二级 tab 高
                                    - 60f // 一级 tab 高
                                    - 0.5f // 分割线高
                                    - 0.5f // 分割线高
                            )
                }
            }
        }
    }
}


internal class HomeListViewAttr : ComposeAttr() {

}

internal class HomeListViewEvent : ComposeEvent() {

}

internal fun ViewContainer<*, *>.HomeList(init: HomeListView.() -> Unit) {
    addChild(HomeListView(), init)
}