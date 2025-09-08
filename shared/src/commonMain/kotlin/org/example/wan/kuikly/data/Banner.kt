package org.example.wan.kuikly.data

import kotlinx.serialization.Serializable

@Serializable
data class BannerItem(
    val desc: String,
    val id: Int,
    val imagePath: String,
    val isVisible: Int,
    val order: Int,
    val title: String,
    val type: Int,
    val url: String,
    var collect: Boolean = false, // 收藏，接口没有返回这个字段的
)