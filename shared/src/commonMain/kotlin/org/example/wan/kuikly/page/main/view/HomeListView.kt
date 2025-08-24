package org.example.wan.kuikly.page.main.view

import com.tencent.kuikly.core.base.ComposeAttr
import com.tencent.kuikly.core.base.ComposeEvent
import com.tencent.kuikly.core.base.ComposeView
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.views.Text

internal class HomeListView : ComposeView<HomeListViewAttr, HomeListViewEvent>() {

    override fun createEvent(): HomeListViewEvent {
        return HomeListViewEvent()
    }

    override fun createAttr(): HomeListViewAttr {
        return HomeListViewAttr()
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            Text {
                attr {
                    text("Home List")
                }
            }
        }
    }
}


internal class HomeListViewAttr : ComposeAttr() {

}

internal class HomeListViewEvent : ComposeEvent() {

}

internal fun ViewContainer<*, *>.HomeList(init: HomeListView.() -> Unit) {
    addChild(HomeListView(), init)
}