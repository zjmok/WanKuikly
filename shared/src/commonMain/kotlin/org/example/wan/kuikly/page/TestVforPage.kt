package org.example.wan.kuikly.page

import org.example.wan.kuikly.base.BasePager
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.coroutines.delay
import com.tencent.kuikly.core.coroutines.launch
import com.tencent.kuikly.core.directives.vfor
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import org.example.wan.kuikly.page.common.NavBar
import org.example.wan.kuikly.utils.bridgeModule

@Page("TestVfor")
internal class TestVforPage : BasePager() {

    val observableList by observableList<Int>()

    val list = mutableListOf<Int>()

    override fun body(): ViewBuilder {
        val ctx = this

        for (i in 1 until 10_000) {
            list.add(i)
        }

        observableList.clear()
        observableList.addAll(list)

        return {
            View {
                // NavBar
                NavBar {
                    attr {
                        title = "测试页面"
//                        paddingStatusBar = true // 默认 true
                    }
                    event {
                        backClick {
                            bridgeModule.closePage()
                        }
                    }
                }
                List {
                    attr {
                        allCenter()
                        size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    }
                    vfor({ ctx.observableList }) {
                        Text {
                            attr {
                                text("data = $it")
                            }
                        }
                    }
                }
                View {
                    attr {
                        backgroundColor(Color.RED)
                        absolutePosition(left = 100f, right = 100f, top = 100f)
                        size(200f, 50f)
                        allCenter()
                    }
                    event {
                        click {
                            ctx.observableList.clear()
                        }
                    }
                    Text {
                        attr {
                            text("clear")
                        }
                    }
                }
                View {
                    attr {
                        backgroundColor(Color.GREEN)
                        absolutePosition(left = 100f, right = 100f, top = 200f)
                        size(200f, 50f)
                        allCenter()
                    }
                    event {
                        click {
                            ctx.observableList.clear()
                            ctx.observableList.addAll(ctx.list)
                        }
                    }
                    Text {
                        attr {
                            text("addAll")
                        }
                    }
                }
                View {
                    attr {
                        backgroundColor(Color.YELLOW)
                        absolutePosition(left = 100f, right = 100f, top = 300f)
                        size(200f, 50f)
                        allCenter()
                    }
                    event {
                        click {
                            ctx.observableList.add(0, 101010)
                        }
                    }
                    Text {
                        attr {
                            text("add")
                        }
                    }
                }
            }
        }
    }
}