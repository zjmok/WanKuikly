package org.example.wan.kuikly.page.settings

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.directives.vif
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import org.example.wan.kuikly.base.BasePager
import org.example.wan.kuikly.data.SuperUserInfo
import org.example.wan.kuikly.page.common.NavBar
import org.example.wan.kuikly.utils.Background
import org.example.wan.kuikly.utils.PrimaryText
import org.example.wan.kuikly.utils.SecondaryText
import org.example.wan.kuikly.utils.bridgeModule
import org.example.wan.kuikly.utils.fromJson
import org.example.wan.kuikly.utils.postNotify
import org.example.wan.kuikly.utils.sharedPreferencesModule
import org.example.wan.kuikly.utils.showAlert
import org.example.wan.kuikly.utils.toast

@Page("settings")
internal class SettingsPage : BasePager() {

    private var isLogin by observable(false)

    override fun created() {
        super.created()
        val json = sharedPreferencesModule.getString("superUserInfo")
        val superUserInfo = fromJson<SuperUserInfo>(json)
        isLogin = (superUserInfo?.userInfo?.id ?: -1) > 0
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            // View 不设置宽高则大小为包裹内容
            View {
                attr {
                    flexDirectionColumn()
                    justifyContentFlexStart() // 主轴 Start
                    alignItemsStretch() // 交叉轴 Stretch 伸展
//                    backgroundColor(Color.BLUE)
//                    size(
//                        pagerData.pageViewWidth,
//                        pagerData.pageViewHeight
//                    )
                }
                // NavBar
                NavBar {
                    attr {
                        title = "设置"
//                        paddingStatusBar = true // 默认 true
                    }
                    event {
                        backClick {
                            bridgeModule.closePage()
                        }
                    }
                }
                // List 必须设置宽高
                List {
                    attr {
                        backgroundColor(Color.Background)
                        size(
                            pagerData.pageViewWidth,
                            pagerData.pageViewHeight
                                    - 60f // NavBar
                                    - pagerData.safeAreaInsets.top
//                                    - pagerData.safeAreaInsets.bottom
                        )
                    }
                    View {
                        attr {
                            justifyContentFlexStart()
                            alignItemsStretch()
                            flexDirectionColumn()
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
                        // item 源码
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
                                    toast("源码")
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
                                    text("源码")
                                    fontSize(18f)
                                }
                            }
                            Text {
                                attr {
                                    text("WanKuikly")
                                    color(Color.SecondaryText)
                                    fontSize(16f)
                                    margin(right = 10f)
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
                        // item 关于
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
                                    toast("关于")
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
                                    text("关于")
                                    color(Color.PrimaryText)
                                    fontSize(18f)
                                }
                            }
                            Text {
                                attr {
                                    text("v1.0.0")
                                    color(Color.SecondaryText)
                                    fontSize(16f)
                                    margin(right = 10f)
                                }
                            }
                            Image {
                                attr {
                                    size(10f, 20f)
                                    src(ImageUri.commonAssets("icon_arrow_right.png"))
                                }
                            }
                        }
                        vif({ ctx.isLogin }) {
                            // 分割块
                            View {
                                attr {
                                    height(10f)
                                    width(pagerData.pageViewWidth)
//                                    backgroundColor(Color.GRAY)
                                }
                            }
                            // item 关于
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
                                        showAlert("提示", "要注销登录吗", "取消", "确定") { jsonObject ->
                                            if (jsonObject?.optBoolean("confirm") == true) {
                                                sharedPreferencesModule.setString("superUserInfo", "")
                                                postNotify("superUserInfo")
                                                toast("已退出登录")
                                                ctx.isLogin = false
                                            }
                                        }
                                    }
                                }
                                Text {
                                    attr {
                                        margin(left = 10f, right = 10f)
                                        flex(1f)
                                        textAlignCenter()
                                        text("退出")
                                        color(Color.PrimaryText)
                                        fontSize(18f)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}