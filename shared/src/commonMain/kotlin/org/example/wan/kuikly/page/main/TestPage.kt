package org.example.wan.kuikly.page.main

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.views.Text
import org.example.wan.kuikly.base.BasePager

@Page("test")
internal class TestPage : BasePager() {

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            attr {
                allCenter()
            }
            Text {
                attr {
                    text("测试页面")
                }
            }
        }
    }

}