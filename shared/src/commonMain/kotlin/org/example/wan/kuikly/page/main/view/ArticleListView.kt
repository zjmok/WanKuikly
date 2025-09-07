package org.example.wan.kuikly.page.main.view

import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ComposeAttr
import com.tencent.kuikly.core.base.ComposeEvent
import com.tencent.kuikly.core.base.ComposeView
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.base.ViewRef
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.directives.vforIndex
import com.tencent.kuikly.core.directives.vif
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.ListView
import com.tencent.kuikly.core.views.ScrollParams
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.layout.Column
import org.example.wan.kuikly.RouterPage.Companion.LOGO
import org.example.wan.kuikly.utils.toast

internal class ArticleListView : ComposeView<ArticleListViewAttr, ArticleListViewEvent>() {

    companion object {
        const val BASE_URL = "https://www.wanandroid.com"
        const val HOME_LIST = "/article/list/{page}/json"

        // 项目列表数据, page >= 1
        const val PROJECT_LIST = "/project/list/{page}/json"
        const val SQUARE_LIST = "/user_article/list/{page}/json"

        // 在某个公众号中搜索历史文章, page >= 1
        const val WX_LIST = "/wxarticle/list/{id}/{page}/json"
    }

    private lateinit var tabItem: TreeTabItem

    fun setTabItem(tabItem: TreeTabItem) {
        this.tabItem = tabItem
    }

    private var listRef: ViewRef<ListView<*, *>>? = null
    private var scrollParams: ScrollParams? by observable(null)

    private var defaultIndex = 0

    override fun createEvent(): ArticleListViewEvent {
        return ArticleListViewEvent()
    }

    override fun createAttr(): ArticleListViewAttr {
        return ArticleListViewAttr()
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            // List 必须设置宽高
            List {
                ref {
                    ctx.listRef = it
                }
                attr {
//                    backgroundColor(Color.BLUE)
                    flexDirectionColumn()
                    width(pagerData.pageViewWidth)
                    height(
                        pagerData.pageViewHeight
                                - pagerData.safeAreaInsets.top
                                - pagerData.safeAreaInsets.bottom
                                - 50f // 二级 tab 高
                                - 60f // 一级 tab 高
                                - 0.5f // 分割线高
                                - 0.5f // 分割线高
                    )
                }
                event {
                    scroll {
                        ctx.scrollParams = it
                    }
                }
                // 设置 Scroller 内容边距
                setContentInset(10f, 10f, 10f, 10f)
                vforIndex({ ctx.tabItem.articleList }) { item, index, count ->
                    Column {
                        View {
                            event {
                                click {
                                    toast("点击 id=${item.id}")
                                }
                            }
                            attr {
//                                backgroundColor(Color.YELLOW)
                                flexDirectionRow()
                                padding(10f, 16f, 10f, 16f)
                                minHeight(160f)
                            }
                            Image {
                                attr {
                                    alignSelfCenter()
                                    size(60f, 60f)
                                    src(LOGO)
                                }
                            }
                            Column {
                                attr {
                                    margin(left = 10f, right = 10f)
                                    flex(1f)
                                }
                                Text {
                                    attr {
                                        text("文章： id = ${item.id}")
                                        fontSize(20f)
                                        color(Color.BLUE)
                                    }
                                }
                                vif({
                                    item.author.isNotEmpty()
                                }) {
                                    Text {
                                        attr {
                                            marginTop(8f)
                                            text("author = ${item.author}")
                                            fontSize(20f)
                                            color(Color.BLUE)
                                        }
                                    }
                                }
                                vif({
                                    item.shareUser.isNotEmpty()
                                }) {
                                    Text {
                                        attr {
                                            marginTop(8f)
                                            text("shareUser = ${item.shareUser}")
                                            fontSize(20f)
                                            color(Color.BLUE)
                                        }
                                    }
                                }
                                Text {
                                    attr {
                                        marginTop(8f)
                                        text("title = ${item.title}")
                                        fontSize(20f)
                                        color(Color.BLUE)
                                    }
                                }
                                Text {
                                    attr {
                                        marginTop(8f)
                                        text("index = $index")
                                        fontSize(20f)
                                        color(Color.BLUE)
                                    }
                                }
                            }
                            Image {
                                attr {
                                    alignSelfCenter()
                                    size(10f, 20f)
                                    src(ImageUri.commonAssets("icon_arrow_right.png"))
                                }
                            }
                        }
                        vif({
                            // 最后一个不显示
                            index < ctx.tabItem.articleList.size - 1
                        }){
                            View {
                                attr {
                                    height(0.5f)
                                    width(pagerData.pageViewWidth)
                                    backgroundColor(Color.GRAY)
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

/**
 * 注意传入可观察数据
 */
internal fun ViewContainer<*, *>.ArticleList(tabItem: TreeTabItem, init: ArticleListView.() -> Unit) {
    addChild(ArticleListView().apply {
        setTabItem(tabItem)
    }, init)
}
