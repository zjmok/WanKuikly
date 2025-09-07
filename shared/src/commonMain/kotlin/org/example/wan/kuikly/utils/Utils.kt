package org.example.wan.kuikly.utils

import com.tencent.kuikly.core.base.BaseObject
import com.tencent.kuikly.core.manager.BridgeManager
import com.tencent.kuikly.core.manager.PagerManager
import com.tencent.kuikly.core.pager.IPager
import org.example.wan.kuikly.base.BridgeModule

internal object Utils : BaseObject() {

    /**
     * currentPageId的值仅在Pager上下文中有效，错误使用可能导致observable不更新、PagerNotFoundException、
     * ReactiveObserverNotFoundException等问题，建议使用Pager上下文的pagerId代替。
     */
    @Deprecated("Deprecated", replaceWith = ReplaceWith("Utils.bridgeModule(IPager, String)"))
    fun bridgeModule(): BridgeModule {
        return PagerManager.getPager(BridgeManager.currentPageId)
            .acquireModule(BridgeModule.MODULE_NAME)
    }

    fun bridgeModule(pager: IPager): BridgeModule {
        return pager.acquireModule(BridgeModule.MODULE_NAME)
    }

    fun logToNative(pager: IPager, content: String) {
        // logToNaive
        bridgeModule(pager).log(content)
    }

    fun currentBridgeModule(): BridgeModule {
        return PagerManager.getPager(BridgeManager.currentPageId).acquireModule(
            BridgeModule.MODULE_NAME
        )
    }

    fun logToNative(content: String) {
        bridgeModule().log(content)
    }

    fun convertToPriceStr(price: Long): String {
        return (price / 100f).toString()
    }

}