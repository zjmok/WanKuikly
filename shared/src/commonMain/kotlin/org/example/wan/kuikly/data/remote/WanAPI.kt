package org.example.wan.kuikly.data.remote

object WanAPI {

        const val BASE_URL = "https://www.wanandroid.com"
        // Banner
        const val BANNER_LIST = "/banner/json"
        // 置顶文章
        const val HOME_TOP_LIST = "/article/top/json"
        // 首页文章列表, page >= 0
        const val HOME_LIST = "/article/list/{page}/json"

        const val PROJECT_TREE = "/project/tree/json"

        // 项目列表数据, page >= 1
        const val PROJECT_LIST = "/project/list/{page}/json"

        // 广场列表数据, page >= 0
        const val SQUARE_LIST = "/user_article/list/{page}/json"
        // 问答, page >= 1
        // 接口 bug, 传了 page_size 返回列表没有置顶数据, 不传是正常的
        const val QA_LIST = "/wenda/list/{page}/json"

        const val WX_TREE = "/wxarticle/chapters/json"

        // 在某个公众号中搜索历史文章, page >= 1
        const val WX_LIST = "/wxarticle/list/{id}/{page}/json"

}