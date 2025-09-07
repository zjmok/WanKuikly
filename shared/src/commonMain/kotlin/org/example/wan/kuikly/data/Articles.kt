package org.example.wan.kuikly.data

import kotlinx.serialization.Serializable
import org.example.wan.kuikly.utils.htmlDecode

@Serializable
data class Articles(
    val curPage: Int,
    val datas: MutableList<DataX>,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
)

@Serializable
data class DataX(
    val adminAdd: Boolean,
    val apkLink: String,
    val audit: Int,
    val author: String,
    val canEdit: Boolean,
    val chapterId: Int,
    @Deprecated("使用 chapterNameDecoded 获取")
    val chapterName: String,
    val collect: Boolean,
    val courseId: Int,
    val desc: String,
    val descMd: String,
    val envelopePic: String,
    val fresh: Boolean,
    val host: String,
    val id: Int,
    val isAdminAdd: Boolean,
    val link: String,
    val niceDate: String,
    val niceShareDate: String = "",
    val origin: String,
    val originId: Int = -1,
    val prefix: String,
    val projectLink: String,
    val publishTime: Long,
    val realSuperChapterId: Int,
    val selfVisible: Int,
    val shareDate: Long = -1,
    val shareUser: String,
    val superChapterId: Int,
    val superChapterName: String,
    val tags: ArrayList<Tag>?,
    val title: String,
    val type: Int,
    val userId: Int,
    val visible: Int,
    val zan: Int
) {
    @Suppress("DEPRECATION")
    val chapterNameDecoded get() = chapterName.htmlDecode()
}

@Serializable
data class Tag(
    val name: String,
    val url: String
)
