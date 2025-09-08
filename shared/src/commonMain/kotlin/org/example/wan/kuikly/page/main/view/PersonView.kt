package org.example.wan.kuikly.page.main.view

import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ComposeAttr
import com.tencent.kuikly.core.base.ComposeEvent
import com.tencent.kuikly.core.base.ComposeView
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.layout.Column
import org.example.wan.kuikly.RouterPage.Companion.LOGO
import org.example.wan.kuikly.utils.Background
import org.example.wan.kuikly.utils.Fore
import org.example.wan.kuikly.utils.routerModule
import org.example.wan.kuikly.utils.toast

internal class PersonView : ComposeView<PersonViewAttr, PersonViewEvent>() {

    override fun createEvent(): PersonViewEvent {
        return PersonViewEvent()
    }

    override fun createAttr(): PersonViewAttr {
        return PersonViewAttr()
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    justifyContentFlexStart() // 主轴 Start
                    alignItemsStretch() // 交叉轴 Stretch 伸展
//                    backgroundColor(Color.YELLOW)
                }
                // List 必须设置宽高
                List {
                    attr {
                        backgroundColor(Color.Background)
                        size(
                            pagerData.pageViewWidth,
                            pagerData.pageViewHeight
                                    - pagerData.safeAreaInsets.top
                                    - pagerData.safeAreaInsets.bottom
//                                - 50 // 二级 tab 高
                                    - 60f // 一级 tab 高
                                    - 0.5f // 分割线高
                                    - 0.5f // 分割线高
                        )
                    }
                    View {
                        attr {
                            justifyContentFlexStart()
                            alignItemsStretch()
                            flexDirectionColumn()
                        }
                        // item header
                        View {
                            attr {
                                alignItemsCenter()
                                flexDirectionRow()
                                backgroundColor(Color.WHITE)
                                minHeight(200f)
                                padding(left = 10f, right = 10f)
                            }
                            event {
                                click {
                                    toast("click")
                                }
                            }
                            Image {
                                attr {
                                    size(80f, 80f)
                                    borderRadius(40f)
                                    border(Border(1f, BorderStyle.DASHED, Color.Fore))
                                    resizeCover()
                                    src(LOGO)
                                }
                            }
                            Column {
                                attr {
                                    flex(1f)
                                    margin(left = 10f, right = 10f)
                                }
                                Text {
                                    attr {
                                        text("昵称昵称昵称")
                                        fontSize(20f)
                                    }
                                }
                                Text {
                                    attr {
                                        marginTop(8f)
                                        text("ididid")
                                        fontSize(18f)
                                    }
                                }
                            }
                            Image {
                                attr {
                                    size(10f, 20f)
                                    src(ImageUri.commonAssets("icon_arrow_right.png"))
                                }
                            }
                        }
                        // 分割块
                        View {
                            attr {
                                height(10f)
                                width(pagerData.pageViewWidth)
//                                backgroundColor(Color.GRAY)
                            }
                        }
                        // item
                        View {
                            attr {
                                alignItemsCenter()
                                flexDirectionRow()
                                backgroundColor(Color.WHITE)
                                minHeight(60f)
                                padding(left = 10f, right = 10f)
                            }
                            event {
                                click {
                                    toast("click")
                                }
                            }
                            Image {
                                attr {
                                    size(20f, 20f)
                                    src(ImageUri.commonAssets("icon_home_selected.png"))
                                }
                            }
                            Text {
                                attr {
                                    margin(left = 10f, right = 10f)
                                    flex(1f)
                                    text("item")
                                    fontSize(18f)
                                }
                            }
                            Image {
                                attr {
                                    size(10f, 20f)
                                    src(ImageUri.commonAssets("icon_arrow_right.png"))
                                }
                            }
                        }
                        // 分割块
                        View {
                            attr {
                                height(10f)
                                width(pagerData.pageViewWidth)
//                                backgroundColor(Color.GRAY)
                            }
                        }
                        // item
                        View {
                            attr {
                                alignItemsCenter()
                                flexDirectionRow()
                                backgroundColor(Color.WHITE)
                                minHeight(60f)
                                padding(left = 10f, right = 10f)
                            }
                            event {
                                click {
                                    toast("click")
                                }
                            }
                            Image {
                                attr {
                                    size(20f, 20f)
                                    src(ImageUri.commonAssets("icon_home_selected.png"))
                                }
                            }
                            Text {
                                attr {
                                    margin(left = 10f, right = 10f)
                                    flex(1f)
                                    text("item")
                                    fontSize(18f)
                                }
                            }
                            Image {
                                attr {
                                    size(10f, 20f)
                                    src(ImageUri.commonAssets("icon_arrow_right.png"))
                                }
                            }
                        }
                        // 分割线
                        View {
                            attr {
                                height(1f)
                                width(pagerData.pageViewWidth)
//                                backgroundColor(Color.GRAY)
                            }
                        }
                        // item
                        View {
                            attr {
                                alignItemsCenter()
                                flexDirectionRow()
                                backgroundColor(Color.WHITE)
                                minHeight(60f)
                                padding(left = 10f, right = 10f)
                            }
                            event {
                                click {
                                    toast("click")
                                }
                            }
                            Image {
                                attr {
                                    size(20f, 20f)
                                    src(ImageUri.commonAssets("icon_home_selected.png"))
                                }
                            }
                            Text {
                                attr {
                                    margin(left = 10f, right = 10f)
                                    flex(1f)
                                    text("item")
                                    fontSize(18f)
                                }
                            }
                            Image {
                                attr {
                                    size(10f, 20f)
                                    src(ImageUri.commonAssets("icon_arrow_right.png"))
                                }
                            }
                        }
                        // 分割线
                        View {
                            attr {
                                height(1f)
                                width(pagerData.pageViewWidth)
//                                backgroundColor(Color.GRAY)
                            }
                        }
                        // item test
                        View {
                            attr {
                                alignItemsCenter()
                                flexDirectionRow()
                                backgroundColor(Color.WHITE)
                                minHeight(60f)
                                padding(left = 10f, right = 10f)
                            }
                            event {
                                click {
                                    routerModule.openPage("test")
                                }
                            }
                            Image {
                                attr {
                                    size(20f, 20f)
                                    src(ImageUri.commonAssets("icon_home_selected.png"))
                                }
                            }
                            Text {
                                attr {
                                    margin(left = 10f, right = 10f)
                                    flex(1f)
                                    text("test")
                                    fontSize(18f)
                                }
                            }
                            Image {
                                attr {
                                    size(10f, 20f)
                                    src(ImageUri.commonAssets("icon_arrow_right.png"))
                                }
                            }
                        }
                        // 分割块
                        View {
                            attr {
                                height(10f)
                                width(pagerData.pageViewWidth)
//                                backgroundColor(Color.GRAY)
                            }
                        }
                        // item 设置
                        View {
                            attr {
                                alignItemsCenter()
                                flexDirectionRow()
                                backgroundColor(Color.WHITE)
                                minHeight(60f)
                                padding(left = 10f, right = 10f)
                            }
                            event {
                                click {
                                    routerModule.openPage("settings")
                                }
                            }
                            Image {
                                attr {
                                    size(20f, 20f)
                                    src(ImageUri.commonAssets("icon_home_selected.png"))
                                }
                            }
                            Text {
                                attr {
                                    margin(left = 10f, right = 10f)
                                    flex(1f)
                                    text("设置")
                                    fontSize(18f)
                                }
                            }
                            Image {
                                attr {
                                    size(10f, 20f)
                                    src(ImageUri.commonAssets("icon_arrow_right.png"))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

internal class PersonViewAttr : ComposeAttr() {

}

internal class PersonViewEvent : ComposeEvent() {

}

internal fun ViewContainer<*, *>.Person(init: PersonView.() -> Unit) {
    addChild(PersonView(), init)
}