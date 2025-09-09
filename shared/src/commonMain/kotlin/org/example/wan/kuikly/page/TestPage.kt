package org.example.wan.kuikly.page

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import org.example.wan.kuikly.base.BasePager
import org.example.wan.kuikly.base.LogModule
import org.example.wan.kuikly.utils.acquireModule
import org.example.wan.kuikly.utils.toast

@Page("test")
internal class TestPage : BasePager() {

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            attr {
                allCenter()
                flexDirectionColumn()
            }
            Text {
                attr {
                    text("测试页面")
                }
            }
            View {
                attr {
                    marginTop(10f)
                    allCenter()
                    backgroundColor(Color.YELLOW)
                    size(200f, 100f)
                }
                event {
                    click {
                        toast("点击了")
                    }
                }
                Text {
                    attr {
                        text("click")
                    }
                }
            }
            View {
                attr {
                    marginTop(10f)
                    allCenter()
                    backgroundColor(Color.YELLOW)
                    size(200f, 100f)
                }
                event {
                    click {
                        val logModule = acquireModule<LogModule>(LogModule.MODULE_NAME)
                        logModule.log("测试 message", "e", "KuiklyLog")
                    }
                }
                Text {
                    attr {
                        text("click")
                    }
                }
            }
        }
    }

}