package org.example.wan.kuikly.page.common

import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ComposeAttr
import com.tencent.kuikly.core.base.ComposeEvent
import com.tencent.kuikly.core.base.ComposeView
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.base.ViewRef
import com.tencent.kuikly.core.coroutines.launch
import com.tencent.kuikly.core.directives.vfor
import com.tencent.kuikly.core.directives.vforIndex
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.PageList
import com.tencent.kuikly.core.views.PageListView
import com.tencent.kuikly.core.views.ScrollParams
import com.tencent.kuikly.core.views.TabItem
import com.tencent.kuikly.core.views.Tabs
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuiklyx.coroutines.Kuikly
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.wan.kuikly.data.Articles
import org.example.wan.kuikly.data.ArticlesTreeItem
import org.example.wan.kuikly.data.BannerItem
import org.example.wan.kuikly.data.DataX
import org.example.wan.kuikly.data.remote.WanAPI.BASE_URL
import org.example.wan.kuikly.data.remote.WanAPI.PROJECT_LIST
import org.example.wan.kuikly.data.remote.WanAPI.PROJECT_TREE
import org.example.wan.kuikly.data.remote.WanAPI.WX_LIST
import org.example.wan.kuikly.data.remote.WanAPI.WX_TREE
import org.example.wan.kuikly.data.remote.biz
import org.example.wan.kuikly.data.remote.ktorClient
import org.example.wan.kuikly.data.remote.onFailure
import org.example.wan.kuikly.data.remote.onSuccess
import org.example.wan.kuikly.data.remote.runCatchingKtor
import org.example.wan.kuikly.data.remote.runResponseData
import org.example.wan.kuikly.utils.Fore
import org.example.wan.kuikly.utils.bridgeModule
import org.example.wan.kuikly.utils.ifNotNull
import org.example.wan.kuikly.utils.lifecycleScope
import org.example.wan.kuikly.utils.log
import org.example.wan.kuikly.utils.networkModule

internal class TreeTabItem {
    var moduleName by observable("")
    var tabId by observable("")
    var tabTitle by observable("")
    var articlePage by observable(0)
    var articleList by observableList<DataX>()
    var bannerList by observableList<BannerItem>()
}

internal class ArticleTreeView : ComposeView<ArticleTreeViewAttr, ArticleTreeViewEvent>() {

    // 模块名称, 首页 项目 ...
    private var moduleName: String = ""

    fun setModuleName(moduleName: String) {
        this.moduleName = moduleName
    }

    private var pageListRef: ViewRef<PageListView<*, *>>? = null
    private var scrollParams: ScrollParams? by observable(null)

    private val tabList by observableList<TreeTabItem>()

    private var defaultIndex = 0

    override fun createEvent(): ArticleTreeViewEvent {
        return ArticleTreeViewEvent()
    }

    override fun createAttr(): ArticleTreeViewAttr {
        return ArticleTreeViewAttr()
    }

    override fun created() {
        super.created()

//        GlobalScope.launch(Dispatchers.Kuikly[this]) { }
//        KuiklyContextScheduler.runOnKuiklyThread("") { }

        var url = ""
        when (moduleName) {
            "项目" -> {
                url = BASE_URL + PROJECT_TREE
            }

            "订阅", "公众号" -> {
                url = BASE_URL + WX_TREE
            }
        }

        fetchTabs(url)

    }

    private fun fetchTabs(url: String) {
        log("url = $url")
        networkModule.requestGet(url, JSONObject().apply {
//            put("", "")
        }) { data, success, errorMsg, response ->
            runResponseData {
                response to data
            }.onFailure {
                it.biz(this)
            }.onSuccess<List<ArticlesTreeItem>> {
                it.ifNotNull {
                    it.map {
                        TreeTabItem().apply {
                            tabId = "${it.id}"
                            tabTitle = it.nameDecoded
                            moduleName = this@ArticleTreeView.moduleName
                        }
                    }.let {
                        tabList.clear()
                        tabList += it
                    }

                    // 加载 defaultIndex 的数据，进入页面当 defaultIndex != 0 时 PageList 会执行回调 pageIndexDidChanged
                    if (defaultIndex == 0 &&
                        defaultIndex < tabList.size && // 防止越界
                        tabList[defaultIndex].articleList.isEmpty() // 未加载数据
                    ) {
                        loadData(defaultIndex)
                    }
                }
            }
        }
    }

    private fun loadData(tabIndex: Int) {
        if (tabIndex >= tabList.size) {
            return
        }
        log("$moduleName ${tabList[tabIndex].tabTitle} 列表加载 tabIndex = $tabIndex")

        var url = ""
        val param = JSONObject()

        when (moduleName) {
            "项目" -> {
                tabList[tabIndex].articlePage = 1
                url = BASE_URL + PROJECT_LIST.run {
                    this.replace("{page}", "${tabList[tabIndex].articlePage}")
                }
                param.apply {
                    put("cid", tabList[tabIndex].tabId)
                    put("page_size", "10")
                }
            }

            "订阅", "公众号" -> {
                tabList[tabIndex].articlePage = 1
                url = BASE_URL + WX_LIST.run {
                    this.replace("{id}", tabList[tabIndex].tabId)
                        .replace("{page}", "${tabList[tabIndex].articlePage}")
                }
                param.apply {
                    put("page_size", "10")
                }
            }

        }

        bridgeModule.currentThread(::println)

        fetchList(url, param, tabIndex)
//        fetchListKtor(url, param, tabIndex)

    }

    private fun fetchList(url: String, param: JSONObject, tabIndex: Int) {
        log("url = $url")
        networkModule.requestGet(url, param) { data, success, errorMsg, response ->
            runResponseData {
                response to data
            }.apply {
                this.parseWrapped = true
            }.onFailure {
                it.biz(this)
            }.onSuccess<Articles> {
                it.ifNotNull {
                    tabList[tabIndex].articleList += it.datas
                }
            }
        }
    }

    private fun fetchListKtor(url: String, param: JSONObject, tabIndex: Int) {
        lifecycleScope.launch {
            runCatchingKtor {
                ktorClient.get(url) {
                    param.toMap().entries.forEach {
                        parameter(it.key, it.value)
                    }
                }
            }.onFailure {
                it.biz(this@ArticleTreeView)
            }.onSuccess<Articles> {
                withContext(Dispatchers.Kuikly[this@ArticleTreeView]) {
                    it.ifNotNull {
                        tabList[tabIndex].articleList += it.datas
                    }
                }
            }
        }
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    flexDirectionColumn()
                }
                Tabs {
                    attr {
//                        backgroundColor(Color.YELLOW)
                        height(50f) // 横向布局，务必指定高度
                        defaultInitIndex(ctx.defaultIndex)
                        indicatorAlignCenter()
                        indicatorInTabItem {
                            View {
                                attr {
                                    absolutePosition(left = 0f, right = 0f, bottom = 0f)
                                    height(2f)
                                    borderRadius(2f)
                                    backgroundColor(Color.Fore)
                                }
                            }
                        }
                        ctx.scrollParams?.also {
                            scrollParams(it)
                        }
                    }
                    vforIndex({ ctx.tabList }) { tabItem, index, count ->
                        TabItem { state ->
                            attr {
//                                width(80f)
                                marginLeft(10f)
                                marginRight(10f)
                                allCenter()
                            }
                            // 点击 tab 切换 pageView
                            event {
                                click {
                                    ctx.pageListRef?.view?.scrollToPageIndex(index, false)
                                }
                            }
                            View {
                                attr {
                                    flexDirectionColumn()
                                    allCenter()
                                }
                                Text {
                                    attr {
                                        text(tabItem.tabTitle)
                                        fontSize(16f)
                                        color(if (state.selected) Color.BLACK else Color.GRAY)
                                    }
                                }
                            }
                        }
                    }
                }
                View {
                    attr {
                        height(0.5f)
                        width(pagerData.pageViewWidth)
                        backgroundColor(Color.GRAY)
                    }
                }
                // List 必须设置宽高
                PageList {
                    ref {
                        ctx.pageListRef = it
                    }
                    attr {
                        flexDirectionRow()
                        flex(1f) // 高度撑到底部
                        pageDirection(true)
                        pageItemWidth(pagerData.pageViewWidth)
                        pageItemHeight(
                            pagerData.pageViewHeight
                                    - pagerData.safeAreaInsets.top
                                    - pagerData.safeAreaInsets.bottom
                                    - 50f // 二级 tab 高
                                    - 60f // 一级 tab 高
                                    - 0.5f // 分割线高
                                    - 0.5f // 分割线高
                        )
                        defaultPageIndex(ctx.defaultIndex)
                        offscreenPageLimit(1) //
                    }
                    event {
                        scroll {
                            ctx.scrollParams = it
                        }
                        pageIndexDidChanged { index ->
                            // index 的类型是 JSONObject?
                            // 内容是 "{"index": value}"
                            (index as? JSONObject)?.let {
                                val value = it.opt("index")
                                value.toString().toIntOrNull()?.let { realIndex ->
                                    log("二级Tab index = $realIndex")
                                    // 加载 index 数据，手动实现懒加载
                                    if (realIndex < ctx.tabList.size && ctx.tabList[realIndex].articleList.isEmpty()) {
                                        ctx.loadData(realIndex)
                                    }
                                }
                            }
                        }
                    }
                    vfor({ ctx.tabList }) { item ->
                        ArticleList {
                            attr {
                                tabItem = item
                                listHeight = (
                                        ctx.pagerData.pageViewHeight
                                                - ctx.pagerData.safeAreaInsets.top
                                                - ctx.pagerData.safeAreaInsets.bottom
                                                - 50f // 二级 tab 高
                                                - 60f // 一级 tab 高
                                                - 0.5f // 分割线高
                                                - 0.5f // 分割线高
                                        )
                            }
                        }
                    }
                }
            }
        }
    }
}


internal class ArticleTreeViewAttr : ComposeAttr() {

}

internal class ArticleTreeViewEvent : ComposeEvent() {

}

internal fun ViewContainer<*, *>.ArticleTree(moduleName: String, init: ArticleTreeView.() -> Unit) {
    addChild(ArticleTreeView().apply {
        setModuleName(moduleName)
    }, init)
}
