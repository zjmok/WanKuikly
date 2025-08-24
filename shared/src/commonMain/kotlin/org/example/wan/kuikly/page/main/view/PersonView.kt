package org.example.wan.kuikly.page.main.view

import com.tencent.kuikly.core.base.ComposeAttr
import com.tencent.kuikly.core.base.ComposeEvent
import com.tencent.kuikly.core.base.ComposeView
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.views.Text
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
            Text {
                attr {
                    text("Person")
                }
                event {
                    click {
                        val message = "Person"
                        toast(message)
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