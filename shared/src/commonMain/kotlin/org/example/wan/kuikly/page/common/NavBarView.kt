package org.example.wan.kuikly.page.common

import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ComposeAttr
import com.tencent.kuikly.core.base.ComposeEvent
import com.tencent.kuikly.core.base.ComposeView
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.directives.velse
import com.tencent.kuikly.core.directives.vif
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.layout.Column
import org.example.wan.kuikly.utils.Background
import org.example.wan.kuikly.utils.PrimaryText
import org.example.wan.kuikly.utils.Tab
import org.example.wan.kuikly.utils.isNull

internal class NavBarView : ComposeView<NavBarViewAttr, NavBarViewEvent>() {

    override fun createEvent(): NavBarViewEvent {
        return NavBarViewEvent()
    }

    override fun createAttr(): NavBarViewAttr {
        return NavBarViewAttr()
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    flexDirectionColumn()
                    backgroundColor(Color.WHITE)
                }
                // StatusBar
                vif({ctx.attr.paddingStatusBar}) {
                    View {
                        attr {
                            height(pagerData.safeAreaInsets.top)
                        }
                    }
                }
                // navBar
                View {
                    attr {
                        flexDirectionRow()
                        justifyContentSpaceAround()
                        alignItemsStretch()
                        width(pagerData.pageViewWidth)
                        height(59f)
                    }
                    View {
                        attr {
                            justifyContentCenter()
                            alignItemsCenter()
                            size(60f, 60f)
                        }
                        event {
                            click {
                                ctx.event.backClickHandler?.invoke()
                            }
                        }
                        vif ({ ctx.attr.isShowBack }){
                            // 缺图标
                            Image {
                                attr {
                                    src("")
                                }
                            }
                            // 没有图标，用 `<` 临时顶替
                            Text {
                                attr {
                                    text("<")
                                    fontSize(30f)
                                    color(Color.PrimaryText)
                                }
                            }
                        }
                    }
                    View {
                        attr {
                            flex(1f)
                            flexDirectionRow()
                            justifyContentFlexStart()
                            alignItemsCenter()
                        }
                        vif({ ctx.attr.customViewInit.isNull() }) {
                            Text {
                                attr {
                                    text(ctx.attr.title)
                                    lines(1)
                                    flex(1f)
                                    if (ctx.attr.titleCenter) {
                                        textAlignCenter()
                                    } else {
                                        textAlignLeft()
                                    }
                                    fontSize(20f)
                                    color(Color.PrimaryText)
                                }
                            }
                        }
                        velse {
                            // addChild 添加 View 对象
                            ctx.attr.customViewInit?.invoke(this)
                        }
                    }
                    // 占位
                    Image {
                        attr {
                            size(60f, 60f)
                        }
                    }
                }
                // 分割线
                View {
                    attr {
                        height(1f)
                        width(pagerData.pageViewWidth)
                        backgroundColor(Color.Background)
                    }
                }
            }
        }
    }
}


internal class NavBarViewAttr : ComposeAttr() {

    var paddingStatusBar: Boolean by observable(true)

    var isShowBack: Boolean by observable(true)

    var title: String by observable("")

    //    var color: Color? = null
    var titleCenter: Boolean by observable(false)

    var customViewInit: (ViewContainer<*, *>.() -> Unit)? = null

    /**
     * 使用 CustomView，title 会失效
     */
    fun useCustomView(
        customViewInit: ViewContainer<*, *>.() -> Unit
    ) {
        this.customViewInit = customViewInit
    }

}

internal class NavBarViewEvent : ComposeEvent() {

    var backClickHandler: (() -> Unit)? = null

    fun backClick(backClickHandler: (() -> Unit)) {
        this.backClickHandler = backClickHandler
    }

}

internal fun ViewContainer<*, *>.NavBar(init: NavBarView.() -> Unit) {
    addChild(NavBarView(), init)
}