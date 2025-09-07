package org.example.wan.kuikly.page.main

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.layout.Column
import org.example.wan.kuikly.RouterPage.Companion.LOGO
import org.example.wan.kuikly.base.BasePager
import org.example.wan.kuikly.utils.routerModule
import org.example.wan.kuikly.utils.toast

@Page("settings")
internal class SettingsPage : BasePager() {

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            // View 不设置宽高则大小为包裹内容
            View {
                attr {
                    justifyContentFlexStart() // 主轴 Start
                    alignItemsStretch() // 交叉轴 Stretch 伸展
//                    backgroundColor(Color.BLUE)
//                    size(
//                        pagerData.pageViewWidth,
//                        pagerData.pageViewHeight
//                    )
                }
                // List 必须设置宽高
                List {
                    attr {
                        backgroundColor(Color.GRAY)
                        size(
                            pagerData.pageViewWidth,
                            pagerData.pageViewHeight
//                                    - pagerData.safeAreaInsets.top
//                                    - pagerData.safeAreaInsets.bottom
                        )
                    }
                    View {
                        attr {
                            justifyContentFlexStart()
                            alignItemsStretch()
                            flexDirectionColumn()
                        }
                        // 状态栏 填充
                        View {
                            attr {
                                height(pagerData.safeAreaInsets.top)
                                backgroundColor(Color.WHITE)
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
                                    size(32f, 32f)
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
                                    size(32f, 32f)
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
                                backgroundColor(Color.GRAY)
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
                                    size(32f, 32f)
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
                                height(0.5f)
                                width(pagerData.pageViewWidth)
                                backgroundColor(Color.GRAY)
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
                                    size(32f, 32f)
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
                                height(0.5f)
                                width(pagerData.pageViewWidth)
                                backgroundColor(Color.GRAY)
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
                                    size(32f, 32f)
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
                                backgroundColor(Color.GRAY)
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
                                    toast("about")
                                }
                            }
                            Image {
                                attr {
                                    size(32f, 32f)
                                    src(ImageUri.commonAssets("icon_home_selected.png"))
                                }
                            }
                            Text {
                                attr {
                                    margin(left = 10f, right = 10f)
                                    flex(1f)
                                    text("关于")
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