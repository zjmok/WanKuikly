package org.example.wan.kuikly.page.main

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.ViewRef
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.directives.vfor
import com.tencent.kuikly.core.directives.vforIndex
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.PageList
import com.tencent.kuikly.core.views.PageListView
import com.tencent.kuikly.core.views.ScrollParams
import com.tencent.kuikly.core.views.TabItem
import com.tencent.kuikly.core.views.Tabs
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import org.example.wan.kuikly.base.BasePager
import org.example.wan.kuikly.page.main.view.ArticleTree
import org.example.wan.kuikly.page.main.view.HomeList
import org.example.wan.kuikly.page.main.view.Person
import org.example.wan.kuikly.page.main.view.SquareTree

internal class MainTabItem {
    var id by observable("")
    var tabTitle by observable("")
    var tabImg by observable("")
    var pageBgColor by observable(Color.WHITE)
}

@Page("main")
internal class MainPage : BasePager() {

    private var pageListRef: ViewRef<PageListView<*, *>>? = null
    private var scrollParams: ScrollParams? by observable(null)

    private val tabDataList by observableList<MainTabItem>()

    private var defaultIndex = 1

    override fun created() {
        super.created()
        val list =
            listOf(
                mapOf("title" to "推荐", "img" to "icon_home"),
                mapOf("title" to "项目", "img" to "icon_project"),
                mapOf("title" to "广场", "img" to "icon_square"),
                mapOf("title" to "订阅", "img" to "icon_subscribe"),
                mapOf("title" to "我的", "img" to "icon_person"),
//                mapOf("title" to "体系", "img" to "tree"),
//                mapOf("title" to "导航", "img" to "navi"),
//                mapOf("title" to "收藏", "img" to "collect"),
            )
        tabDataList.clear()
        tabDataList.addAll(list.map {
            MainTabItem().apply {
                tabTitle = it["title"] ?: ""
                tabImg = it["img"] ?: ""
            }
        })

        // 宽高
        println("pagerData.statusBarHeight = ${pagerData.statusBarHeight}")
        println("pagerData.deviceWidth = ${pagerData.deviceWidth}")
        println("pagerData.activityWidth = ${pagerData.activityWidth}")
        println("pagerData.pageViewWidth = ${pagerData.pageViewWidth}")
        println("pagerData.safeAreaInsets = ${pagerData.safeAreaInsets}")

        println("MainPage index = $defaultIndex")
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
//                    flexDirectionColumn()
                    marginTop(pagerData.statusBarHeight)
                    flexDirection(FlexDirection.COLUMN_REVERSE)
                }
                Tabs {
                    attr {
//                        backgroundColor(Color.GREEN)
                        height(60f) // 横向布局，务必指定高度
                        defaultInitIndex(ctx.defaultIndex)
                        indicatorAlignCenter()
                        indicatorInTabItem {
                            View {
                                attr {
                                    absolutePosition(left = 15f, right = 15f, bottom = 5f)
                                    height(6f)
                                    borderRadius(2f)
//                                    backgroundColor(Color.YELLOW)
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
                                width(pagerData.deviceWidth / ctx.tabDataList.size)
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
                                Image {
                                    attr {
                                        size(20f, 20f)
                                        // 使用 assets 资源
                                        // assets-resource.md
                                        // https://kuikly.tds.qq.com/DevGuide/assets-resource.html
                                        val imageUri = ImageUri.commonAssets("${tabItem.tabImg}.png")
                                        val imageSelectedUri = ImageUri.commonAssets("${tabItem.tabImg}_selected.png")
                                        src(if (state.selected) imageSelectedUri else imageUri)
                                    }
                                }
                                Text {
                                    attr {
                                        text(tabItem.tabTitle)
                                        fontSize(16f)
                                        color(if (state.selected) Color.RED else Color.GRAY)
                                    }
                                }
                            }
                        }
                    }
                }
                // 分割线
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
//                            pagerData.pageViewHeight
//                                    - pagerData.statusBarHeight
//                                    - pagerData.navigationBarHeight // 总会有值
//                                    - 60f
                            pagerData.pageViewHeight
                                    - pagerData.safeAreaInsets.top
                                    - pagerData.safeAreaInsets.bottom
                                    - 60f // 一级 tab 高
                                    - 0.5f
                        )
                        defaultPageIndex(ctx.defaultIndex)
                        offscreenPageLimit(4) //
//                        pageItemHeight()
                        scrollEnable(false) // 禁止滑动
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
                                    println("MainPage index = $realIndex")
                                    // index = 0
                                }
                            }
                        }
                    }
                    vfor({ ctx.tabDataList }) { item ->
                        View {
                            attr {
                                allCenter()
                            }
                            when (item.tabTitle) {
                                "首页", "推荐" -> {
                                    HomeList { }
                                }

                                "广场" -> {
                                    SquareTree {
                                        attr {
                                            width(pagerData.pageViewWidth)
                                            height(
                                                pagerData.pageViewHeight
                                                        - pagerData.safeAreaInsets.top
                                                        - pagerData.safeAreaInsets.bottom
                                                        - 60f // 一级 tab 高
                                            )
                                        }
                                    }
                                }

                                "项目", "订阅", "公众号" -> {
                                    ArticleTree(item.tabTitle) {
                                        attr {
                                            width(pagerData.pageViewWidth)
                                            height(
                                                pagerData.pageViewHeight
                                                        - pagerData.safeAreaInsets.top
                                                        - pagerData.safeAreaInsets.bottom
                                                        - 60f // 一级 tab 高
                                            )
                                        }
                                    }
                                }

                                "我的", "个人" -> {
                                    Person { }
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}
