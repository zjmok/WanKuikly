package org.example.wan.kuikly.page.person

import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ComposeAttr
import com.tencent.kuikly.core.base.ComposeEvent
import com.tencent.kuikly.core.base.ComposeView
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.module.CallbackRef
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.layout.Column
import com.tencent.kuikly.core.views.layout.Row
import org.example.wan.kuikly.RouterPage.Companion.LOGO
import org.example.wan.kuikly.data.SuperUserInfo
import org.example.wan.kuikly.utils.Background
import org.example.wan.kuikly.utils.Fore
import org.example.wan.kuikly.utils.PrimaryText
import org.example.wan.kuikly.utils.SecondaryText
import org.example.wan.kuikly.utils.addNotify
import org.example.wan.kuikly.utils.fromJson
import org.example.wan.kuikly.utils.removeNotify
import org.example.wan.kuikly.utils.routerModule
import org.example.wan.kuikly.utils.sharedPreferencesModule
import org.example.wan.kuikly.utils.toast

internal class PersonView : ComposeView<PersonViewAttr, PersonViewEvent>() {

    private var superUserInfo by observable<SuperUserInfo?>(null)

    private lateinit var notiRef: CallbackRef

    override fun createEvent(): PersonViewEvent {
        return PersonViewEvent()
    }

    override fun createAttr(): PersonViewAttr {
        return PersonViewAttr()
    }

    override fun created() {
        super.created()
        notiRef = addNotify("superUserInfo") {
            val superUserInfo = fromJson<SuperUserInfo>(it.toString())
            this.superUserInfo = superUserInfo
        }
        val json = sharedPreferencesModule.getString("superUserInfo")
        fromJson<SuperUserInfo>(json)?.let {
            superUserInfo = it
        }
    }

    override fun viewDestroyed() {
        removeNotify("superUserInfo", notiRef)
        super.viewDestroyed()
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
                                    if ((ctx.superUserInfo?.userInfo?.id ?: -1) <= 0) {
                                        routerModule.openPage("login")
                                    }
                                }
                            }
                            Image {
                                attr {
                                    size(80f, 80f)
                                    // 边框 圆角
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
                                        val nickname = ctx.superUserInfo?.userInfo?.nickname
                                        text(nickname?.let { it.takeIf { it.isNotBlank() } } ?: " 未登录 ")
                                        fontSize(20f)
                                        color(Color.PrimaryText)
                                    }
                                }
                                Row {
                                    attr {
                                        marginTop(8f)
                                    }
                                    Text {
                                        attr {
                                            val username = ctx.superUserInfo?.userInfo?.username
                                            text("用户名: ${username?.let { it.takeIf { it.isNotBlank() } } ?: "null"}")
                                            fontSize(16f)
                                            color(Color.SecondaryText)
                                        }
                                    }
                                    Text {
                                        attr {
                                            marginLeft(20f)
                                            val id = ctx.superUserInfo?.userInfo?.id
                                            text("ID: ${id ?: -1}")
                                            fontSize(16f)
                                            color(Color.SecondaryText)
                                        }
                                    }
                                }
                                Text {
                                    attr {
                                        marginTop(8f)
                                        val email = ctx.superUserInfo?.userInfo?.email
                                        text("邮箱: ${email?.let { it.takeIf { it.isNotBlank() } } ?: "null"}")
                                        fontSize(16f)
                                        color(Color.SecondaryText)
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
                                    toast("开发中")
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
                                    routerModule.openPage("login")
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
                                    text("LoginPage")
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
                                    toast("开发中")
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
                        // item debug
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
                                    routerModule.openPage("debug")
//                                    toast("开发中")
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
                                    text("DebugPage")
//                                    text("item")
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
                        // 分割线 RouterPage
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
                                    routerModule.openPage("router")
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
                                    text("RouterPage")
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