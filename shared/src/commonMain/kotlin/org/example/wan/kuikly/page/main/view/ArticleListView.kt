package org.example.wan.kuikly.page.main.view

import com.tencent.kuikly.core.base.BoxShadow
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ComposeAttr
import com.tencent.kuikly.core.base.ComposeEvent
import com.tencent.kuikly.core.base.ComposeView
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.base.ViewRef
import com.tencent.kuikly.core.directives.vforIndex
import com.tencent.kuikly.core.directives.vif
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.ListView
import com.tencent.kuikly.core.views.ScrollParams
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.SliderPage
import com.tencent.kuikly.core.views.layout.Column
import com.tencent.kuikly.core.views.layout.Row
import org.example.wan.kuikly.RouterPage.Companion.LOGO
import org.example.wan.kuikly.utils.Back
import org.example.wan.kuikly.utils.Background
import org.example.wan.kuikly.utils.Fore
import org.example.wan.kuikly.utils.Foreground
import org.example.wan.kuikly.utils.PrimaryText
import org.example.wan.kuikly.utils.SecondaryText
import org.example.wan.kuikly.utils.isNotNullAndNotBlank
import org.example.wan.kuikly.utils.toast

internal class ArticleListView : ComposeView<ArticleListViewAttr, ArticleListViewEvent>() {

    private var height = 0f
    private lateinit var tabItem: TreeTabItem

    fun setListHeight(height: Float) {
        this.height = height
    }

    fun setTabItem(tabItem: TreeTabItem) {
        this.tabItem = tabItem
        println("tabItem.articleList.size() = ${tabItem.articleList.size}")
    }

    private var listRef: ViewRef<ListView<*, *>>? = null
    private var scrollParams: ScrollParams? by observable(null)

    private var defaultIndex = 0

    private var currentBannerIndex by observable(0)

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
                    backgroundColor(Color.Background)
                    flexDirectionColumn()
                    width(pagerData.pageViewWidth)
                    height(ctx.height) // 从调用容器处传入
                    padding(top = 6f, bottom = 6f)
                }
                event {
                    scroll {
                        ctx.scrollParams = it
                    }
                }
                // 设置 Scroller 内容边距
                setContentInset(10f, 10f, 10f, 10f)
                // Home Banner
                vif({
                    (ctx.tabItem.moduleName == "首页" || ctx.tabItem.moduleName == "推荐")
                            && ctx.tabItem.bannerList.isNotEmpty()
                }) {
                    println("ctx.tabItem.bannerList.size = ${ctx.tabItem.bannerList.size}")
                    println("w / h = ${ctx.pagerData.pageViewWidth} / ${ctx.pagerData.pageViewWidth * 9 / 16}")
                    SliderPage {
                        attr {
                            // 这两个 补偿 padding, 相当于位移
                            marginTop(-6f) // 补偿上方 padding
                            marginBottom(6f) // 补偿下方 padding
                            val width = pagerData.pageViewWidth
                            // w / h = 16 / 9
                            // h = w * 9 / 16
                            val height = pagerData.pageViewWidth * 9 / 16

                            // 必须设置 item 的宽高
                            pageItemWidth = width
                            pageItemHeight = height
                            isHorizontal = true
                            defaultPageIndex = 0
                            loopPlayIntervalTimeMs = 3000 // 轮播时间间隔（毫秒单位）

                            initSliderItems(ctx.tabItem.bannerList) { banner ->
                                View {
                                    attr {
                                        allCenter()
                                        flexDirectionColumn()
                                    }
                                    Image {
                                        attr {
                                            width(width)
                                            height(height)
                                            src(banner.imagePath)
                                            // 占位
                                            placeholderSrc(LOGO)
                                            // 等比拉伸，X和Y都填充满，刚好填充满整个容器，scaleType = centerCrop
                                            resizeCover()
                                            // 等比拉伸，X或Y有一个填充满，刚好放下整个图片，scaleType = fitCenter
//                                            resizeContain()
                                            // 拉伸XY，X和Y都填充满，刚好放下整个图片，scaleType = fitXY
                                            // resizeStretch()
                                        }
                                        event {
                                            click {
                                                toast(banner.url)
                                            }
                                        }
                                    }
                                    View {
                                        attr {
                                            // absolutePosition 脱离布局，层叠布局
                                            absolutePosition(left = 0f, right = 0f, bottom = 0f)
                                            height(40f)
                                            allCenter()
                                            flexDirectionRow()
                                        }
                                        ctx.tabItem.bannerList.forEachIndexed { index, item ->
                                            View {
                                                attr {
                                                    size(5f, 5f)
                                                    borderRadius(2.5f)
                                                    margin(4f)
                                                    if (index == ctx.currentBannerIndex) {
                                                        backgroundColor(Color.Fore)
                                                    } else {
                                                        backgroundColor(Color.Back)
                                                    }

                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                        startLoopPlayIfNeed()
                        event {
                            pageIndexDidChanged {
                                val index = (it as? JSONObject)?.optInt("index") ?: 0
                                ctx.currentBannerIndex = index
                            }
                        }
                    }
                }
                // StickyHeader 悬停 置顶 粘性头
//                Hover {
//                    attr {
//                        absolutePosition(top = 500f, left = 0f, right = 0f)
//                        height(50f)
//                        backgroundColor(Color.BLUE)
//                        hoverMarginTop(-50f) // 悬停时距离顶部的距离
//                    }
//                }
                vforIndex({ ctx.tabItem.articleList }) { item, index, count ->
                    Column {
                        attr {
                            backgroundColor(Color.Foreground)
                            borderRadius(10f)
                            boxShadow(
                                BoxShadow(
                                    offsetX = 0f,
                                    offsetY = 0f,
                                    shadowRadius = 3f,
                                    shadowColor = Color.Foreground,
                                )
                            )
                            padding(10f, 16f, 10f, 16f)
                            margin(left = 12f, right = 12f, top = 6f, bottom = 6f)
//                            minHeight(160f)
                        }
                        event {
                            click {
                                toast("点击 id=${item.id}")
                                val url = item.link
                            }
                        }
                        Row {
                            // 时间
                            Text {
                                attr {
                                    text(item.niceDate)
                                    fontSize(14f)
                                    color(Color.SecondaryText)
                                }
                            }
                            View {
                                attr {
                                    flex(1f)
                                }
                            }
                            // 作者
                            val author = if (item.superChapterName == "广场Tab") {
                                "分享人: ${item.shareUser}"
                            } else {
                                "作者: ${item.author}"
                            }
                            Text {
                                attr {
                                    text(author)
                                    fontSize(14f)
                                    color(Color.SecondaryText)
                                }
                            }
                        }
                        Row {
                            attr {
                                marginTop(10f)
                            }
                            // 图片
                            vif({ item.envelopePic.isNotNullAndNotBlank() }) {
                                Image {
                                    attr {
//                                        alignSelfCenter()
                                        size(100f, 100f)
                                        margin(right = 10f)
                                        src(item.envelopePic)
                                        resizeCover()
                                        borderRadius(10f)
                                    }
                                }
                            }
                            Column {
                                attr {
                                    alignSelfCenter()
                                    margin(right = 10f)
                                    flex(1f)
                                }
                                // title
                                Text {
                                    attr {
                                        text(item.title)
                                        fontSize(18f)
                                        lines(2)
                                        overflow(true)
                                        textOverFlowTail() // 超出，在末尾显示省略
                                        fontSize(18f)
                                        color(Color.PrimaryText)
                                    }
                                }
                                // desc
                                Text {
                                    attr {
                                        marginTop(8f)
                                        text(item.desc)
                                        fontSize(16f)
                                        lines(2)
                                        overflow(true)
                                        textOverFlowTail() // 超出，在末尾显示省略
                                        color(Color.SecondaryText)
                                    }
                                }
                            }
                        }
                        // module 分类
                        val classification = listOf(
                            item.superChapterName,
                            item.chapterNameDecoded,
                        ).filter {
                            // 解析为 null 了
                            it.isNotNullAndNotBlank()
                        }.joinToString("/")
                        vif({ classification.isNotNullAndNotBlank() }) {
                            Text {
                                attr {
                                    marginTop(10f)
                                    text(classification)
                                    fontSize(14f)
                                    color(Color.SecondaryText)
                                }
                            }
                        }
                        // TAG 分类
                        val tags = item.tags?.joinToString("/") { it.name }
                        vif({
                            tags.isNotNullAndNotBlank()
                        }) {
                            Text {
                                attr {
                                    marginTop(10f)
                                    text(tags!!)
                                    fontSize(14f)
                                    color(Color.SecondaryText)
                                }
                            }
                        }
                        // flex 占位
                        View {
                            attr {
                                flex(1f)
                            }
                        }
                        // Index
                        Text {
                            attr {
                                alignSelfCenter()
                                marginTop(10f)
                                text("[ ${index + 1} ]")
                                fontSize(12f)
                                color(Color.SecondaryText)
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
internal fun ViewContainer<*, *>.ArticleList(
    height: Float,
    tabItem: TreeTabItem,
    init: ArticleListView.() -> Unit
) {
    addChild(ArticleListView().apply {
        setTabItem(tabItem)
        setListHeight(height)
    }, init)
}
