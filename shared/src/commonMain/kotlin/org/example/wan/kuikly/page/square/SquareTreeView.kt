package org.example.wan.kuikly.page.square

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
import org.example.wan.kuikly.data.DataX
import org.example.wan.kuikly.data.remote.WanAPI.BASE_URL
import org.example.wan.kuikly.data.remote.WanAPI.QA_LIST
import org.example.wan.kuikly.data.remote.WanAPI.SQUARE_LIST
import org.example.wan.kuikly.page.common.ArticleList
import org.example.wan.kuikly.page.common.TreeTabItem
import org.example.wan.kuikly.utils.Fore
import org.example.wan.kuikly.utils.fromJson
import org.example.wan.kuikly.utils.networkModule
import org.example.wan.kuikly.utils.toast

internal class SquareTreeView : ComposeView<SquareTreeViewAttr, SquareTreeViewEvent>() {

    private var pageListRef: ViewRef<PageListView<*, *>>? = null
    private var scrollParams: ScrollParams? by observable(null)

    private val tabList by observableList<TreeTabItem>()

    private var defaultIndex = 1

    override fun createEvent(): SquareTreeViewEvent {
        return SquareTreeViewEvent()
    }

    override fun createAttr(): SquareTreeViewAttr {
        return SquareTreeViewAttr()
    }

    override fun created() {
        super.created()

        setSquareTabList()
    }

    private fun setSquareTabList() {
        tabList.clear()
        listOf("搜索", "广场", "问答").forEach {
            val tabItem = TreeTabItem().apply {
                moduleName = "广场"
                tabTitle = it
                isLoad = false
            }
            tabList.add(tabItem)
        }

        // 加载 defaultIndex 的数据
        if (defaultIndex < tabList.size && tabList[defaultIndex].isLoad.not()) {
            loadData(defaultIndex)
        }
    }

    private fun loadData(index: Int) {
        if (index >= tabList.size) {
            return
        }
        println("加载数据 index=$index")

        var url = ""
        val param = JSONObject()

        when (tabList[index].tabTitle) {
            "搜索" -> {
                return
            }

            "广场" -> {
                var page = 0
                url = BASE_URL + SQUARE_LIST.run {
                    this.replace("{page}", "$page")
                }
                param.apply {
                    put("page_size", "10")
                }
            }

            "问答" -> {
                var page = 0
                url = BASE_URL + QA_LIST.run {
                    this.replace("{page}", "$page")
                }
                param.apply {
                    // 接口 bug, 传了 page_size 返回列表没有置顶数据, 不传是正常的
//                    put("page_size", "10")
                }
            }
        }

        println("url = $url")
        networkModule.requestGet(url, param) { data, success, errorMsg, response ->
            if (success.not()) {
                toast(errorMsg)
                return@requestGet
            }

//            println(data)
            setArticleList(data, index)
        }
    }

    private fun setArticleList(data: JSONObject, index: Int) {
        val articles = data.optJSONObject("data")
        val dates = articles?.optJSONArray("datas")

        val list = fromJson<List<DataX>>(dates.toString()) ?: return

        tabList[index].articleList.addAll(list)
        tabList[index].isLoad = true
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    flexDirectionColumn()
                    allCenter()
                }
                Tabs {
                    attr {
//                        backgroundColor(Color.YELLOW)
                        height(50f) // 横向布局，务必指定高度
                        width(240f)
                        defaultInitIndex(ctx.defaultIndex)
                        indicatorAlignCenter()
                        indicatorInTabItem {
                            View {
                                attr {
                                    absolutePosition(left = 15f, right = 15f, bottom = 0f)
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
                                width(80f)
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
//                                    text("广场: tab = ${item.tabTitle}, index = $index")
//                                    fontSize(20f)
//                                    color(Color.BLUE)
//                                }
//                            }
                            val tabItem = ctx.tabList[index]
                            ArticleList(
                                tabItem = tabItem,
                                height = ctx.pagerData.pageViewHeight
                                        - ctx.pagerData.safeAreaInsets.top
                                        - ctx.pagerData.safeAreaInsets.bottom
                                        - 50f // 二级 tab 高
                                        - 60f // 一级 tab 高
                                        - 0.5f // 分割线高
                                        - 0.5f // 分割线高
                            ) {
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


internal class SquareTreeViewAttr : ComposeAttr() {

}

internal class SquareTreeViewEvent : ComposeEvent() {

}

internal fun ViewContainer<*, *>.SquareTree(init: SquareTreeView.() -> Unit) {
    addChild(SquareTreeView(), init)
}