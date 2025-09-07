package org.example.wan.kuikly.page.main.view

import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ComposeAttr
import com.tencent.kuikly.core.base.ComposeEvent
import com.tencent.kuikly.core.base.ComposeView
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.base.ViewRef
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
import org.example.wan.kuikly.data.ArticlesTreeItem
import org.example.wan.kuikly.data.DataX
import org.example.wan.kuikly.utils.json
import org.example.wan.kuikly.utils.networkModule
import org.example.wan.kuikly.utils.toast

internal class TreeTabItem {
    var moduleName by observable("")
    var tabId by observable("")
    var tabTitle by observable("")
    var isLoad = false
    var articleList by observableList<DataX>()
}

internal class ArticleTreeView : ComposeView<ArticleTreeViewAttr, ArticleTreeViewEvent>() {

    companion object {
        const val BASE_URL = "https://www.wanandroid.com"
        const val HOME_LIST = "/article/list/{page}/json"

        const val PROJECT_TREE = "/project/tree/json"

        // 项目列表数据, page >= 1
        const val PROJECT_LIST = "/project/list/{page}/json"

        const val SQUARE_LIST = "/user_article/list/{page}/json"

        const val WX_TREE = "/wxarticle/chapters/json"

        // 在某个公众号中搜索历史文章, page >= 1
        const val WX_LIST = "/wxarticle/list/{id}/{page}/json"
    }

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
//        tabDataList.clear()
//        tabDataList.add(TabItemData().apply { id = "id_0"; tabTitle = "tab0"; index = 0 })
//        tabDataList.add(TabItemData().apply { id = "id_1"; tabTitle = "tab1"; index = 1 })
//        tabDataList.add(TabItemData().apply { id = "id_2"; tabTitle = "tab2"; index = 2 })
//        tabDataList.add(TabItemData().apply { id = "id_3"; tabTitle = "tab3"; index = 3 })
//        tabDataList.add(TabItemData().apply { id = "id_4"; tabTitle = "tab4"; index = 4 })
//        tabDataList.add(TabItemData().apply { id = "id_5"; tabTitle = "tab5"; index = 5 })
//        tabDataList.add(TabItemData().apply { id = "id_6"; tabTitle = "tab6"; index = 6 })
//        tabDataList.add(TabItemData().apply { id = "id_7"; tabTitle = "tab7"; index = 7 })
//        tabDataList.add(TabItemData().apply { id = "id_8"; tabTitle = "tab8"; index = 8 })

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
        println("url = $url")
        networkModule.requestGet(url, JSONObject().apply {
//            put("", "")
        }) { data, success, errorMsg, response ->
            if (success.not()) {
                toast(errorMsg)
                return@requestGet
            }
            println(data)
            setTabList(data)
        }

    }

    private fun setTabList(data: JSONObject) {
        val tree = data.optJSONArray("data")
        val list = json.decodeFromString<List<ArticlesTreeItem>>(tree.toString())
//        println(list)

        tabList.clear()
        // 遍历文章列表数据
        for (it in list) {
            val tabItem = TreeTabItem().apply {
                tabId = "${it.id}"
                tabTitle = it.nameDecoded
//                articleList.addAll(mutableListOf())
                isLoad = false
            }
            tabItem.moduleName = moduleName
            tabList.add(tabItem)
        }

        // 加载 defaultIndex 的数据
        if (list.isNotEmpty() && defaultIndex < tabList.size && tabList[defaultIndex].isLoad.not()) {
            loadData(defaultIndex)
        }
    }

    private fun loadData(index: Int) {
        if (index >= tabList.size) {
            return
        }
        println("加载数据 index=$index")

        val id = tabList[index].tabId
        var url = ""
        val param = JSONObject()
        var page = 1
        when (moduleName) {
            "项目" -> {
                url = BASE_URL + PROJECT_LIST.run {
                    this.replace("{page}", "$page")
                }
                param.apply {
                    put("cid", id)
                    put("page_size", "10")
                }
            }

            "订阅", "公众号" -> {
                url = BASE_URL + WX_LIST.run {
                    this.replace("{id}", id)
                        .replace("{page}", "$page")
                }
                param.apply {
                    put("page_size", "10")
                }
            }
        }

        println("url = $url")
        networkModule.requestGet(url, param) { data, success, errorMsg, response ->
            if (success.not()) {
                toast(errorMsg)
                return@requestGet
            }
            println(data)
            setArticleList(data, index)
        }
    }

    private fun setArticleList(data: JSONObject, index: Int) {
        val articles = data.optJSONObject("data")
        val dates = articles?.optJSONArray("datas")

        val list = json.decodeFromString<List<DataX>>(dates.toString())
//        println("list.size = ${list.size} list.title = ${list.map { it.title }}")

        tabList[index].articleList.addAll(list)
        tabList[index].isLoad = true
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
                                    backgroundColor(Color.GREEN)
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
                                    println("二级Tab index = $realIndex")
                                    // 加载 index 数据，手动实现懒加载
                                    if (realIndex < ctx.tabList.size && ctx.tabList[realIndex].isLoad.not()) {
                                        ctx.loadData(realIndex)
                                    }
                                }
                            }
                        }
                    }
                    vforIndex({ ctx.tabList }) { item, index, count ->
                        View {
                            attr {
//                                backgroundColor(Color.YELLOW)
                                allCenter()
                            }
//                            Text {
//                                attr {
//                                    text("${ctx.module}: tab = ${item.tabTitle}, id = ${item.id}")
//                                    fontSize(20f)
//                                    color(Color.BLUE)
//                                }
//                            }
                            val tabItem = ctx.tabList[index]
                            ArticleList(tabItem) {
                                attr {

                                }
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
