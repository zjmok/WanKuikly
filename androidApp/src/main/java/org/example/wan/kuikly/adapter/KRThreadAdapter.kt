package org.example.wan.kuikly.adapter

import com.tencent.kuikly.core.render.android.adapter.IKRThreadAdapter

class KRThreadAdapter : IKRThreadAdapter {
    override fun executeOnSubThread(task: () -> Unit) {
        execOnSubThread(task)
    }
}