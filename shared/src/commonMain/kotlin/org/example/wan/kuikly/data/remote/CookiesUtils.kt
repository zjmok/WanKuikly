package org.example.wan.kuikly.data.remote

object CookiesUtils {

    fun formatCookies(cookies: List<String>): String {
        /*
[
    "JSESSIONID=865E70BBF7B0148999147F50467656BE; Path=/; Secure; HttpOnly",
    "loginUserName=123453; Expires=Tue, 14-Oct-2025 06:37:13 GMT; Path=/",
    "token_pass=5d9b90bcb70640183e09d1e755ead823; Expires=Tue, 14-Oct-2025 06:37:13 GMT; Path=/",
    "loginUserName_wanandroid_com=123453; Domain=wanandroid.com; Expires=Tue, 14-Oct-2025 06:37:13 GMT; Path=/",
    "token_pass_wanandroid_com=5d9b90bcb70640183e09d1e755ead823; Domain=wanandroid.com; Expires=Tue, 14-Oct-2025 06:37:13 GMT; Path=/"
]
        */
        // 处理成格式 `name1=value1; name2=value2; name3=value3`
        val formatCookies = cookies.joinToString("; ") { cookie ->
            cookie.split("; ").first() // 弱水三千 只取一瓢
        }
        return formatCookies
    }

}

