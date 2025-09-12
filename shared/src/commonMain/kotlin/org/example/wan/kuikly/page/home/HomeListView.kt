package org.example.wan.kuikly.page.home

import com.tencent.kuikly.core.base.ComposeAttr
import com.tencent.kuikly.core.base.ComposeEvent
import com.tencent.kuikly.core.base.ComposeView
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.reactive.handler.observable
import org.example.wan.kuikly.data.BannerItem
import org.example.wan.kuikly.data.DataX
import org.example.wan.kuikly.data.remote.WanAPI.BANNER_LIST
import org.example.wan.kuikly.data.remote.WanAPI.BASE_URL
import org.example.wan.kuikly.data.remote.WanAPI.HOME_LIST
import org.example.wan.kuikly.page.common.ArticleList
import org.example.wan.kuikly.page.common.TreeTabItem
import org.example.wan.kuikly.utils.fromJson
import org.example.wan.kuikly.utils.log
import org.example.wan.kuikly.utils.networkModule
import org.example.wan.kuikly.utils.toast

internal class HomeListView : ComposeView<HomeListViewAttr, HomeListViewEvent>() {

    private val tabItem by observable(TreeTabItem().apply {
        moduleName = "首页"
        tabTitle = "首页"
        isLoad = false
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
        networkModule.requestGet(url, JSONObject()) { data, success, errorMsg, response ->
            if (success.not()) {
                toast(errorMsg)
                return@requestGet
            }

            val banner = data.optJSONArray("data")

            val list = fromJson<List<BannerItem>>(banner.toString()) ?: return@requestGet
//            println(list)

            tabItem.bannerList.clear()
            tabItem.bannerList.addAll(list)
        }
    }

    private fun loadList() {

        var page = 0
        val url = BASE_URL + HOME_LIST.run {
            this.replace("{page}", "$page")
        }
        val param = JSONObject().apply {
            put("page_size", "10")
        }

        log("url = $url")
        networkModule.requestGet(url, param) { data, success, errorMsg, response ->
            if (success.not()) {
                toast(errorMsg)
                return@requestGet
            }

//            println(data)
            setArticleList(data)
        }
    }

    private fun setArticleList(data: JSONObject) {
        val articles = data.optJSONObject("data")
        val dates = articles?.optJSONArray("datas")

        val list = fromJson<List<DataX>>(dates.toString()) ?: return
//        println(list)

        tabItem.articleList.addAll(list)
        tabItem.isLoad = true
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