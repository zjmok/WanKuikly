package org.example.wan.kuikly.page.main.view

import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ComposeAttr
import com.tencent.kuikly.core.base.ComposeEvent
import com.tencent.kuikly.core.base.ComposeView
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.base.ViewRef
import com.tencent.kuikly.core.directives.vforIndex
import com.tencent.kuikly.core.module.NetworkModule
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
import kotlinx.serialization.json.Json
import org.example.wan.kuikly.data.ArticlesTreeItem
import org.example.wan.kuikly.page.main.TabItemData

internal class ArticleTreeView : ComposeView<ArticleTreeViewAttr, ArticleTreeViewEvent>() {

    companion object {
        const val BASE_URL = "https://www.wanandroid.com/"
        const val HOME_LIST = "/article/list/{page}/json"
        const val PROJECT_TREE = "/project/tree/json"
        const val PROJECT_LIST = "/project/list/{page}/json"
        const val SQUARE_LIST = "/user_article/list/{page}/json"
        const val WX_TREE = "/wxarticle/chapters/json"
        const val WX_LIST = "/wxarticle/list/{id}/{page}/json"
    }

    data class ArticleTree(
        val `data`: List<Data>,
        val errorCode: Int,
        val errorMsg: String
    )

    data class Data(
        val articleList: List<Any>,
        val author: String,
        val children: List<Any>,
        val courseId: Int,
        val cover: String,
        val desc: String,
        val id: Int,
        val lisense: String,
        val lisenseLink: String,
        val name: String,
        val order: Int,
        val parentChapterId: Int,
        val type: Int,
        val userControlSetTop: Boolean,
        val visible: Int
    )

    private var module: String = ""

    private var pageListRef: ViewRef<PageListView<*, *>>? = null
    private var scrollParams: ScrollParams? by observable(null)

    private val tabDataList by observableList<TabItemData>()

    private var defaultIndex = 0

    override fun createEvent(): ArticleTreeViewEvent {
        return ArticleTreeViewEvent()
    }

    override fun createAttr(): ArticleTreeViewAttr {
        return ArticleTreeViewAttr()
    }

    fun setModule(module: String) {
        this.module = module
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

        val networkModule = acquireModule<NetworkModule>(NetworkModule.MODULE_NAME)
        when (module) {
            "项目" -> {
                networkModule.requestGet(BASE_URL + PROJECT_TREE, JSONObject().apply {
//                    put("", "")
                }) { data, success, errorMsg, response ->
//                    println(data)
                    val tree = data.optJSONArray("data")
                    val list = Json.decodeFromString<List<ArticlesTreeItem>>(tree.toString())
                    tabDataList.clear()
                    tabDataList.addAll(list.mapIndexed { i, it ->
                        TabItemData().apply { id = "${it.id}"; tabTitle = it.nameDecoded }
                    })
//                    println(list)
                }
            }

            "订阅", "公众号" -> {
                networkModule.requestGet(BASE_URL + WX_TREE, JSONObject().apply {
//                    put("", "")
                }) { data, success, errorMsg, response ->
//                    println(data)
                    val tree = data.optJSONArray("data")
                    val list = Json.decodeFromString<List<ArticlesTreeItem>>(tree.toString())
                    tabDataList.clear()
                    tabDataList.addAll(list.mapIndexed { i, it ->
                        TabItemData().apply { id = "${it.id}"; tabTitle = it.nameDecoded }
                    })
//                    println(list)
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
                                    backgroundColor(Color.GREEN)
                                }
                            }
                        }
                        ctx.scrollParams?.also {
                            scrollParams(it)
                        }
                    }
                    vforIndex({ ctx.tabDataList }) { tabItem, index, count ->
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
                                    - 50
                                    - 60
                                    - 0.5f
                                    - 0.5f
                        )
                        defaultPageIndex(ctx.defaultIndex)
                        offscreenPageLimit(1) //
                    }
                    event {
                        scroll {
                            ctx.scrollParams = it
                        }
                    }
                    vforIndex({ ctx.tabDataList }) { item, index, count ->
                        View {
                            attr {
//                                backgroundColor(Color.YELLOW)
                                allCenter()
                            }
                            Text {
                                attr {
                                    text("${ctx.module}: tab = ${item.tabTitle}, id = ${item.id}")
                                    fontSize(20f)
                                    color(Color.BLUE)
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

internal fun ViewContainer<*, *>.ArticleTree(init: ArticleTreeView.() -> Unit) {
    addChild(ArticleTreeView(), init)
}
