package org.example.wan.kuikly.page.main.view

import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ComposeAttr
import com.tencent.kuikly.core.base.ComposeEvent
import com.tencent.kuikly.core.base.ComposeView
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.base.ViewRef
import com.tencent.kuikly.core.directives.vforIndex
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.PageList
import com.tencent.kuikly.core.views.PageListView
import com.tencent.kuikly.core.views.ScrollParams
import com.tencent.kuikly.core.views.TabItem
import com.tencent.kuikly.core.views.Tabs
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import org.example.wan.kuikly.page.main.TabItemData

internal class ArticleListView : ComposeView<ArticleListViewAttr, ArticleListViewEvent>() {

    companion object {
        const val BASE_URL = "https://www.wanandroid.com/"
        const val HOME_LIST = "/article/list/{page}/json"
        const val PROJECT_TABS = "/project/tree/json"
        const val PROJECT_LIST = "/project/list/{page}/json"
        const val SQUARE_LIST = "/user_article/list/{page}/json"
        const val WX_TABS = "/wxarticle/chapters/json"
        const val WX_LIST = "/wxarticle/list/{id}/{page}/json"
    }

    private var pageListRef: ViewRef<PageListView<*, *>>? = null
    private var scrollParams: ScrollParams? by observable(null)

    private val tabDataList by observableList<TabItemData>()

    private var defaultIndex = 0

    override fun createEvent(): ArticleListViewEvent {
        return ArticleListViewEvent()
    }

    override fun createAttr(): ArticleListViewAttr {
        return ArticleListViewAttr()
    }

    override fun created() {
        super.created()
        tabDataList.clear()
        tabDataList.add(TabItemData().apply { id = "id_0"; tabTitle = "tab0" })
        tabDataList.add(TabItemData().apply { id = "id_1"; tabTitle = "tab1" })
        tabDataList.add(TabItemData().apply { id = "id_2"; tabTitle = "tab2" })
        tabDataList.add(TabItemData().apply { id = "id_3"; tabTitle = "tab3" })
        tabDataList.add(TabItemData().apply { id = "id_4"; tabTitle = "tab4" })
        tabDataList.add(TabItemData().apply { id = "id_5"; tabTitle = "tab5" })
        tabDataList.add(TabItemData().apply { id = "id_6"; tabTitle = "tab6" })
        tabDataList.add(TabItemData().apply { id = "id_7"; tabTitle = "tab7" })
        tabDataList.add(TabItemData().apply { id = "id_8"; tabTitle = "tab8" })
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
                                    absolutePosition(left = 15f, right = 15f, bottom = 0f)
                                    height(3f)
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
                                    text("文章列表： tab = ${item.tabTitle}, index = $index")
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


internal class ArticleListViewAttr : ComposeAttr() {

}

internal class ArticleListViewEvent : ComposeEvent() {

}

internal fun ViewContainer<*, *>.ArticleList(init: ArticleListView.() -> Unit) {
    addChild(ArticleListView(), init)
}
