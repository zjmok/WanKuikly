package org.example.wan.kuikly.page

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ComposeAttr
import com.tencent.kuikly.core.base.ComposeEvent
import com.tencent.kuikly.core.base.ComposeView
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.coroutines.launch
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.ActionButtonTitleAttr
import com.tencent.kuikly.core.views.AlertDialog
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.ParametersBuilder
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.core.Input
import kotlinx.coroutines.delay
import kotlinx.io.bytestring.ByteString
import org.example.wan.kuikly.base.BasePager
import org.example.wan.kuikly.base.LogModule
import org.example.wan.kuikly.data.ApiResult
import org.example.wan.kuikly.data.BannerItem
import org.example.wan.kuikly.data.remote.WanAPI
import org.example.wan.kuikly.data.remote.biz
import org.example.wan.kuikly.data.remote.ktorClient
import org.example.wan.kuikly.data.remote.onFailure
import org.example.wan.kuikly.data.remote.onSuccess
import org.example.wan.kuikly.data.remote.request
import org.example.wan.kuikly.data.remote.requestPost
import org.example.wan.kuikly.data.remote.runCatchingKtor
import org.example.wan.kuikly.kmp.getEngine
import org.example.wan.kuikly.page.common.NavBar
import org.example.wan.kuikly.utils.Back
import org.example.wan.kuikly.utils.Background
import org.example.wan.kuikly.utils.Fore
import org.example.wan.kuikly.utils.bridgeModule
import org.example.wan.kuikly.utils.toast

@Page("debug")
internal class DebugPage : BasePager() {

    private var showAlert by observable(false)  // 定义响应式变量

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            attr {
                flexDirectionColumn()
                justifyContentFlexStart()
                alignItemsCenter()
                backgroundColor(Color.Background)
            }
            AlertDialog {
                attr {
                    showAlert(ctx.showAlert)
                    title("title")
                    message("message")
//                    actionButtons("cancel", "confirm")
//                    actionButtonsCustomAttr({
//                        text("Cancel")
//                        color(Color.Back)
//                    }, {
//                        text("Confirm")
//                        color(Color.Fore)
//                    })
                    val leftBtnAttr: ActionButtonTitleAttr = {
                        text("取消")
                        color(Color.Back)
                    }
                    val rightBtnAttr: ActionButtonTitleAttr = {
                        text("确定")
                        color(Color.Fore)
                    }
                    actionButtonsCustomAttr(leftBtnAttr, rightBtnAttr)
                    inWindow(true) // 似乎没有效果
                }
                event {
                    clickActionButton { index ->
                        ctx.showAlert = false
                        if (index == 0) {
                            toast("点击了 取消")
                        } else if (index == 1) {
                            toast("点击了 确定")
                        }
                    }
                    clickBackgroundMask {
                        // 背景蒙层点击事件回调
                        ctx.showAlert = false
                        toast("背景蒙层点击事件回调")
                    }
                    willDismiss {
                        // 系统返回事件（back按钮或右滑）回调
                        ctx.showAlert = false
                        toast("系统返回事件")
                    }
                }
            }
            // NavBar
            NavBar {
                attr {
                    title = "测试页面"
//                        paddingStatusBar = true // 默认 true
                }
                event {
                    backClick {
                        bridgeModule.closePage()
                    }
                }
            }
            List {
                attr {
                    flex(1f)
                    width(pagerData.pageViewWidth)
                    alignSelfCenter()
                    flexDirectionColumn()
                    justifyContentFlexStart()
                    alignItemsCenter() // 可能不生效，可能需要 item 内部指定
                }
                TestItem {
                    attr {
                        marginTop(10f)
                        text("封装的 request<T>")
                    }
                    event {
                        click {
                            // requestGet
                            // requestPost
                            requestPost<String>(
                                WanAPI.LOGIN,
                                // 请求参数，默认值 null
                                mapOf(
                                    "username" to "123456",
                                    "password" to "123456",
                                ),
                                // 状态码不在区间 [200, 300)，errorCode != 0，其它错误，默认值实现执行 `it.biz(this)`
                                onFailure = {
                                    it.biz(this)
                                },
                                // 响应成功，响应头，默认值 null
                                onSuccessHeaders = {
                                    // 数据类型是 Map<String, List<String>>
                                    println(it)
                                },
                                // 响应成功，200 <= statusCode < 300， errorCode == 0
                                onSuccess = {
                                    // 数据类型是泛型。null 代表 Json 解析错误，后端接口问题
                                    println(it)
                                },
                            )
                        }
                    }
                }
                TestItem {
                    attr {
                        marginTop(10f)
                        text("封装的 request")
                    }
                    event {
                        click {
                            request(
                                url = WanAPI.BASE_URL + WanAPI.LOGIN,
                                // 默认值 false
                                isPost = true,
                                // 请求参数，自动处理 get 或 post 的参数，默认值 null
                                params = mapOf(
                                    "username" to "123456",
                                    "password" to "123456",
                                ),
                                onResult = {
                                    it.onFailure {
                                        // 错误
                                        it.biz(this)
                                    }.onSuccess<String>({
                                        // 响应头
                                        println(it)
                                    }) {
                                        // 响应体
                                        println(it)
                                    }
                                }
                            )
                        }
                    }
                }
                TestItem {
                    attr {
                        marginTop(10f)
                        text("Ktor Client 封装 post")
                    }
                    event {
                        click {
                            getPager().lifecycleScope.launch {
                                runCatchingKtor {
                                    val url = WanAPI.BASE_URL + WanAPI.BANNER_LIST

                                    // FormDataContent
//                                    ktorClient.use {
//                                        // submitForm
//                                        it.submitForm(
//                                            url = url,
//                                            formParameters = parameters {
//                                                append("key1", "value1")
//                                                append("key2", "value2")
//                                            },
//                                        )
//                                    }

                                    // MultiPartFormDataContent
//                                    ktorClient.use {
//                                        // submitFormWithBinaryData
//                                        it.submitFormWithBinaryData(
//                                            url = url,
//                                            formData = formData {
//                                                append("key1", "value1")
//                                                append("key2", "value2")
//                                            },
//                                        )
//                                    }

                                    // post json body 可用 post 或 submitForm
                                    // 表单文件上传用 submitForm submitFormWithBinaryData
                                    ktorClient.use {
                                        it.post(url) {
                                            // setBody 可传 Text、对象、FormDataContent、MultiPartFormDataContent
                                            setBody(
                                                // setBody 可传 FormDataContent
                                                FormDataContent(
                                                    ParametersBuilder()
                                                        .apply {
                                                            set("key1", "value1")
                                                            append("key2", "value2")
                                                        }
                                                        .build()
                                                )
                                                // setBody 可传 MultiPartFormDataContent
//                                                MultiPartFormDataContent(
//                                                    formData {
//                                                        append(FormPart("key1", "value1"))
//                                                        append("key2", "value2")
//                                                    },
//                                                    boundary = "WebAppBoundary"
//                                                )
                                            )
                                            header("Authorization", "Bearer your_token_here")
                                        }
                                    }

                                    // 对象 + application/json，可传 json
                                    // 会自动转换为 TextContent 的 json 字符串
//                                    ktorClient.use {
//                                        it.post(url) {
//                                            setBody(
//                                                BaseData(
//                                                    errorCode = 1,
//                                                    errorMsg = "msg",
//                                                    data = "data",
//                                                )
//                                            )
//                                            contentType(ContentType.parse("application/json"))
//                                        }
//                                    }

                                }.onFailure {
                                    // 异常 + code 不在 [200, 300) 范围内
                                    println("failure: $it")
                                }.onSuccess<List<BannerItem>> {
                                    it?.let {
//                                        println(it)
                                        toast("${it.firstOrNull()?.title}")
                                    } ?: run {
                                        println("解析失败")
                                    }
                                }

                            }
                        }
                    }
                }
                TestItem {
                    attr {
                        marginTop(10f)
                        text("Ktor Client 封装 get")
                    }
                    event {
                        click {
                            getPager().lifecycleScope.launch {
                                runCatchingKtor {
                                    val url = WanAPI.BASE_URL + WanAPI.BANNER_LIST
                                    ktorClient.use {
                                        it.get(url) {
                                            header("Authorization", "Bearer your_token_here") // 认证头示例
                                            header("Accept", "application/json") // 指定期望的响应内容类型
                                            header("Custom-Header", "CustomValue") // 自定义头
                                        }
                                    }
                                }.onFailure {
                                    // 异常 + code 不在 [200, 300) 范围内
                                    println("failure: $it")
                                }.onSuccess<List<BannerItem>> {
                                    it?.let {
                                        toast("${it.firstOrNull()?.title}")
                                    } ?: run {
                                        println("解析失败")
                                    }
                                }

                            }
                        }
                    }
                }
                TestItem {
                    attr {
                        marginTop(10f)
                        text("Ktor Client 直接使用")
                    }
                    event {
                        click {
                            // 直接使用 Ktor Client
                            getPager().lifecycleScope.launch {
                                val engine = getEngine() // KMP 获取
                                val client = HttpClient(engine) {
                                    install(ContentNegotiation) {
                                        json()
                                    }
                                }
                                val url = WanAPI.BASE_URL + WanAPI.BANNER_LIST
                                val httpResponse = client.get(url) {
                                    // 添加请求头
                                    header("Authorization", "Bearer your_token_here") // 认证头示例
                                    header("Accept", "application/json") // 指定期望的响应内容类型
                                    header("Custom-Header", "CustomValue") // 自定义头
                                }
                                val result = httpResponse.body<ApiResult<List<BannerItem>>>()
                                println(result)
                                toast("${result.data?.firstOrNull()?.title}")
                            }
                        }
                    }
                }
                TestItem {
                    attr {
                        marginTop(10f)
                        text("Kuikly 内建协程")
                    }
                    event {
                        click {
                            // 内建协程目前仅支持基础的 suspend 方法执行能力
                            getPager().lifecycleScope.launch {
                                ctx.suspendFun()
                            }
                        }
                    }
                }
                TestItem {
                    attr {
                        marginTop(10f)
                        text("Kuikly alert")
                    }
                    event {
                        click {
                            ctx.showAlert = true
                        }
                    }
                }
                TestItem {
                    attr {
                        marginTop(10f)
                        text("Native alert")
                    }
                    event {
                        click {
                            bridgeModule.showAlert("title", "这是一个 alert 测试", "取消", "确定") { jsonObject ->
                                toast("alert 回调：$jsonObject")
                            }
                        }
                    }
                }
                TestItem {
                    attr {
                        marginTop(10f)
                        text("Native toast")
                    }
                    event {
                        click {
                            toast("点击了")
                        }
                    }
                }
                TestItem {
                    attr {
                        marginTop(10f)
                        text("自定义 Module log")
                    }
                    event {
                        click {
                            val logModule = ctx.acquireModule<LogModule>(LogModule.MODULE_NAME)
                            logModule.log("测试 message", "e", "KuiklyLog")
                        }
                    }
                }
            }
        }
    }

    suspend fun suspendFun() {
        toast("before delay")
        delay(3000)
        toast("after delay")
    }

}

// 自定义 view
class ItemView : ComposeView<ItemViewAttr, ComposeEvent>() {

    override fun body(): ViewBuilder {
        return {
            attr {
                allCenter()
            }
            View {
                attr {
                    allCenter()
                    backgroundColor(Color.Back)
                    size(200f, 40f)
                }
                Text {
                    attr {
                        text(this@ItemView.attr.text)
                        color(Color.Fore)
                    }
                }
            }
        }
    }

    override fun createAttr(): ItemViewAttr {
        return ItemViewAttr()
    }

    override fun createEvent(): ComposeEvent {
        return ComposeEvent()
    }

}

// 自定义 view 的属性
class ItemViewAttr : ComposeAttr() {
    var text = ""
    fun text(text: String): ItemViewAttr {
        this.text = text
        return this
    }
}

fun ViewContainer<*, *>.TestItem(
    init: ItemView.() -> Unit
) {
    addChild(ItemView(), init)
}

// 文件
suspend fun uploadFileByByteString(client: HttpClient, byteString: ByteString, url: String) {
    uploadFileByByteArray(client, byteString.toByteArray(), url)
}

// 文件
suspend fun uploadFileByByteArray(client: HttpClient, byteArray: ByteArray, url: String) {
    client.use {
        val response: HttpResponse = client.post(url) {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("key1", "value1")
                        append("file", byteArray, Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=\"fileName\"")
                            append(HttpHeaders.ContentType, "image/png") // 设置对应的类型
                        })
                    }
                ))
        }
        if (response.status.isSuccess()) {
            println("Upload successful")
        }
    }
}

// 文件
suspend fun uploadFileByByteArray2(client: HttpClient, byteArray: ByteArray, url: String) {
    client.use {
        val response: HttpResponse = client.submitFormWithBinaryData(
            url = url,
            formData = formData {
                append("key1", "value1")
                append("file", byteArray, Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=\"fileName\"")
                    append(HttpHeaders.ContentType, "image/png") // 设置对应的类型
                })
            },
        )
        if (response.status.isSuccess()) {
            println("Upload successful")
        }
    }
}

// 文件，post + MultiPartFormDataContent，Input
suspend fun uploadFileByInput(client: HttpClient, input: Input, url: String) {
    client.use {
        val response: HttpResponse = client.post(url) {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        appendInput("file", Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=\"fileName\"")
                        }) {
                            input
                        }
                    }
                ))
        }
        if (response.status.isSuccess()) {
            println("Upload successful")
        }
    }
}

// 参数 + 文件
suspend fun uploadByInput2(client: HttpClient, params: Map<String, String>, input: Input, url: String) {
    client.use {
        val response: HttpResponse = client.post(url) {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        params.forEach {
                            append(it.key, it.value)
                        }
                        appendInput("file", Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=\"fileName\"")
                        }) {
                            input
                        }
                    }
                ))
        }
        if (response.status.isSuccess()) {
            println("Upload successful")
        }
    }
}
