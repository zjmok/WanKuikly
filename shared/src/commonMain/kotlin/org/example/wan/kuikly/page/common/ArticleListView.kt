package org.example.wan.kuikly.page.common

import com.tencent.kuikly.core.base.BoxShadow
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ComposeAttr
import com.tencent.kuikly.core.base.ComposeEvent
import com.tencent.kuikly.core.base.ComposeView
import com.tencent.kuikly.core.base.Skew
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.base.ViewRef
import com.tencent.kuikly.core.directives.scrollToPosition
import com.tencent.kuikly.core.directives.vforIndex
import com.tencent.kuikly.core.directives.vif
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.FooterRefresh
import com.tencent.kuikly.core.views.FooterRefreshEndState
import com.tencent.kuikly.core.views.FooterRefreshState
import com.tencent.kuikly.core.views.FooterRefreshView
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.ListView
import com.tencent.kuikly.core.views.Refresh
import com.tencent.kuikly.core.views.RefreshView
import com.tencent.kuikly.core.views.RefreshViewState
import com.tencent.kuikly.core.views.ScrollParams
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.SliderPage
import com.tencent.kuikly.core.views.layout.Column
import com.tencent.kuikly.core.views.layout.Row
import org.example.wan.kuikly.data.Articles
import org.example.wan.kuikly.data.remote.WanAPI.BASE_URL
import org.example.wan.kuikly.data.remote.WanAPI.HOME_LIST
import org.example.wan.kuikly.data.remote.WanAPI.PROJECT_LIST
import org.example.wan.kuikly.data.remote.WanAPI.QA_LIST
import org.example.wan.kuikly.data.remote.WanAPI.SQUARE_LIST
import org.example.wan.kuikly.data.remote.WanAPI.WX_LIST
import org.example.wan.kuikly.data.remote.biz
import org.example.wan.kuikly.data.remote.onFailure
import org.example.wan.kuikly.data.remote.onSuccess
import org.example.wan.kuikly.data.remote.runResponseData
import org.example.wan.kuikly.utils.Back
import org.example.wan.kuikly.utils.Background
import org.example.wan.kuikly.utils.Fore
import org.example.wan.kuikly.utils.Foreground
import org.example.wan.kuikly.utils.PrimaryText
import org.example.wan.kuikly.utils.SecondaryText
import org.example.wan.kuikly.utils.ifNotNull
import org.example.wan.kuikly.utils.isNotNullAndNotBlank
import org.example.wan.kuikly.utils.networkModule
import org.example.wan.kuikly.utils.toast

internal class ArticleListView : ComposeView<ArticleListViewAttr, ArticleListViewEvent>() {

    private lateinit var refreshRef: ViewRef<RefreshView>
    private var refreshText by observable("下拉刷新")

    private lateinit var footerRefreshRef: ViewRef<FooterRefreshView>
    private var footerRefreshText by observable("加载更多")

    private lateinit var listRef: ViewRef<ListView<*, *>>
    private var scrollParams: ScrollParams? by observable(null)

    private var currentBannerIndex by observable(0)

    override fun createEvent(): ArticleListViewEvent {
        return ArticleListViewEvent()
    }

    override fun createAttr(): ArticleListViewAttr {
        return ArticleListViewAttr()
    }

    override fun body(): ViewBuilder {

        val listHeight = attr.listHeight
        val tabItem = attr.tabItem

        val ctx = this
        return {
            View {
                List {
                    // List 必须设置宽高
                    ref {
                        ctx.listRef = it
                    }
                    attr {
                        backgroundColor(Color.Background)
                        flexDirectionColumn()
                        width(pagerData.pageViewWidth)
                        height(listHeight) // 从调用容器处传入
                        padding(top = 6f, bottom = 6f)
                    }
                    event {
                        scroll {
                            ctx.scrollParams = it
                        }
                    }

                    Refresh {
                        ref {
                            ctx.refreshRef = it
                        }
                        attr {
                            height(50f)
                            allCenter()
                        }
                        Text {
                            attr {
                                color(Color.BLACK)
                                fontSize(20f)
                                text(ctx.refreshText)
                                transform(Skew(-10f, 0f))
                            }
                        }
                        event {
                            refreshStateDidChange {
                                when (it) {
                                    RefreshViewState.REFRESHING -> {
                                        ctx.refreshText = "正在刷新"
                                        ctx.fetchList(isLoadMore = false)
                                    }

                                    RefreshViewState.IDLE -> ctx.refreshText = "下拉刷新"
                                    RefreshViewState.PULLING -> ctx.refreshText = "松手即可刷新"
                                }
                            }
                        }
                    }

                    // 设置 Scroller 内容边距
                    setContentInset(10f, 10f, 10f, 10f)
                    // Home Banner
                    vif({
                        (tabItem.moduleName == "首页" || tabItem.moduleName == "推荐")
                                && tabItem.bannerList.isNotEmpty()
                    }) {
//                        println("ctx.tabItem.bannerList.size = ${tabItem.bannerList.size}")
//                        println("w / h = ${ctx.pagerData.pageViewWidth} / ${ctx.pagerData.pageViewWidth * 9 / 16}")
                        View {
                            attr {
                                val width = pagerData.pageViewWidth
                                // w / h = 16 / 9
                                // h = w * 9 / 16
                                val height = pagerData.pageViewWidth * 9 / 16
                                size(width, height)
                            }
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

                                    initSliderItems(tabItem.bannerList) { banner ->
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
//                                                    placeholderSrc(LOGO)
                                                    // 等比拉伸，X和Y都填充满，刚好填充满整个容器，scaleType = centerCrop
                                                    resizeCover()
                                                    // 等比拉伸，X或Y有一个填充满，刚好放下整个图片，scaleType = fitCenter
//                                                    resizeContain()
                                                    // 拉伸XY，X和Y都填充满，刚好放下整个图片，scaleType = fitXY
                                                    // resizeStretch()
                                                }
                                                event {
                                                    click {
                                                        toast(banner.url)
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
                            View {
                                attr {
                                    // absolutePosition 脱离布局，层叠布局
                                    absolutePosition(left = 0f, right = 0f, bottom = 0f)
                                    height(40f)
                                    allCenter()
                                    flexDirectionRow()
                                }
                                tabItem.bannerList.forEachIndexed { index, item ->
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
                    // StickyHeader 悬停 置顶 粘性头
//                    Hover {
//                        attr {
//                            absolutePosition(top = 500f, left = 0f, right = 0f)
//                            height(50f)
//                            backgroundColor(Color.BLUE)
//                            hoverMarginTop(-50f) // 悬停时距离顶部的距离
//                        }
//                    }
                    vforIndex({ tabItem.articleList }) { item, index, count ->
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
//                                minHeight(160f)
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
//                                            alignSelfCenter()
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

                    vif({ tabItem.articleList.isNotEmpty() }) {
                        FooterRefresh {
                            ref {
                                ctx.footerRefreshRef = it
                            }
                            attr {
                                preloadDistance(600f)
                                allCenter()
                                height(60f)
                            }
                            event {
                                refreshStateDidChange {
                                    when (it) {
                                        FooterRefreshState.REFRESHING -> {
                                            ctx.footerRefreshText = "正在加载更多..."
                                            ctx.fetchList(isLoadMore = true)
                                        }

                                        FooterRefreshState.IDLE -> ctx.footerRefreshText = "加载更多"
                                        FooterRefreshState.NONE_MORE_DATA -> ctx.footerRefreshText = "我是有底线的"
                                        FooterRefreshState.FAILURE -> ctx.footerRefreshText = "点击重试加载更多"
                                        else -> {}
                                    }
                                }
                            }
                        }
                    }

                }

                vif({ ctx.attr.tabItem.articleList.isNotEmpty() }) {
                    View {
                        attr {
                            absolutePosition(right = 20f, bottom = 50f)
                            allCenter()
                        }
                        event {
                            click {
                                ctx.listRef.view?.scrollToPosition(0)
                            }
                        }
                        Text {
                            attr {
                                text("👆")
                                fontSize(36f)
                            }
                        }
                    }
                }

            }
        }
    }

    private fun fetchList(isLoadMore: Boolean = false) {
        var url = ""
        val param = JSONObject()

        when (attr.tabItem.moduleName) {
            "首页" -> {
                attr.tabItem.articlePage = if (isLoadMore) attr.tabItem.articlePage + 1 else 0
                url = BASE_URL + HOME_LIST.run {
                    this.replace("{page}", "${attr.tabItem.articlePage}")
                }
                param.apply {
                    put("page_size", "10")
                }
            }

            "项目" -> {
                attr.tabItem.articlePage = if (isLoadMore) attr.tabItem.articlePage + 1 else 1
                url = BASE_URL + PROJECT_LIST.run {
                    this.replace("{page}", "${attr.tabItem.articlePage}")
                }
                param.apply {
                    put("cid", attr.tabItem.tabId)
                    put("page_size", "10")
                }
            }

            "广场" -> {
                when (attr.tabItem.tabTitle) {
                    "搜索" -> {
                        toast("开发中")
                        // 未实现
                        refreshRef.view?.endRefresh()
                        return
                    }

                    "广场" -> {
                        attr.tabItem.articlePage = if (isLoadMore) attr.tabItem.articlePage + 1 else 0
                        url = BASE_URL + SQUARE_LIST.run {
                            this.replace("{page}", "${attr.tabItem.articlePage}")
                        }
                        param.apply {
                            put("page_size", "10")
                        }
                    }

                    "问答" -> {
                        attr.tabItem.articlePage = if (isLoadMore) attr.tabItem.articlePage + 1 else 1
                        url = BASE_URL + QA_LIST.run {
                            this.replace("{page}", "${attr.tabItem.articlePage}")
                        }
                        param.apply {
                            // 接口 bug, 传了 page_size 返回列表没有置顶数据, 不传是正常的
//                            put("page_size", "10")
                        }
                    }
                }
            }

            "订阅" -> {
                attr.tabItem.articlePage = if (isLoadMore) attr.tabItem.articlePage + 1 else 1
                url = BASE_URL + WX_LIST.run {
                    this.replace("{id}", attr.tabItem.tabId)
                        .replace("{page}", "${attr.tabItem.articlePage}")
                }
                param.apply {
                    put("page_size", "10")
                }
            }
        }

        networkModule.requestGet(url, param) { data, success, errorMsg, response ->
            runResponseData {
                response to data
            }.onFailure {
                it.biz(this@ArticleListView)
                refreshRef.view?.endRefresh()
                footerRefreshRef.view?.resetRefreshState()
            }.onSuccess<Articles> {
                it.ifNotNull {
                    if (isLoadMore.not()) {
                        // refresh
                        attr.tabItem.articleList.clear()
                        attr.tabItem.articleList += it.datas

                        refreshRef.view?.endRefresh()
                        footerRefreshRef.view?.resetRefreshState() // 刷新成功后，需要重置尾部刷新状态
                    } else {
                        // loadMore
                        attr.tabItem.articleList += it.datas

                        if (it.over) {
                            footerRefreshRef.view?.endRefresh(FooterRefreshEndState.NONE_MORE_DATA)
                        } else {
                            footerRefreshRef.view?.endRefresh(FooterRefreshEndState.SUCCESS)
                        }
                    }
                }
            }
        }
    }

}


internal class ArticleListViewAttr : ComposeAttr() {

    var listHeight: Float = 0f

    var tabItem: TreeTabItem = TreeTabItem()

}

internal class ArticleListViewEvent : ComposeEvent() {

}

internal fun ViewContainer<*, *>.ArticleList(init: ArticleListView.() -> Unit) {
    addChild(ArticleListView(), init)
}
