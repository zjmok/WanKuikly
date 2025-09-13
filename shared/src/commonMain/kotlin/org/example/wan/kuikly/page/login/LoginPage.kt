package org.example.wan.kuikly.page.login

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.ViewRef
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Input
import com.tencent.kuikly.core.views.InputView
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.views.layout.Column
import org.example.wan.kuikly.base.BasePager
import org.example.wan.kuikly.data.SuperUserInfo
import org.example.wan.kuikly.data.UserInfo
import org.example.wan.kuikly.data.remote.CookiesUtils
import org.example.wan.kuikly.data.remote.WanAPI
import org.example.wan.kuikly.data.remote.biz
import org.example.wan.kuikly.data.remote.onFailure
import org.example.wan.kuikly.data.remote.onSuccess
import org.example.wan.kuikly.data.remote.runResponseData
import org.example.wan.kuikly.page.common.NavBar
import org.example.wan.kuikly.utils.Background
import org.example.wan.kuikly.utils.Fore
import org.example.wan.kuikly.utils.bridgeModule
import org.example.wan.kuikly.utils.ifNotNull
import org.example.wan.kuikly.utils.networkModule
import org.example.wan.kuikly.utils.postNotify
import org.example.wan.kuikly.utils.routerModule
import org.example.wan.kuikly.utils.sharedPreferencesModule
import org.example.wan.kuikly.utils.toJson
import org.example.wan.kuikly.utils.toast

@Page("login")
internal class LoginPage : BasePager() {

    lateinit var userNameRef: ViewRef<InputView>
    lateinit var passwordRef: ViewRef<InputView>

    var userName: String by observable("")
    var password: String by observable("")

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            attr {
                flexDirectionColumn()
            }
            NavBar {
                attr {
                    title = "登录"
//                    isShowBack = false
                }
                event {
                    backClick {
                        routerModule.closePage()
                    }
                }
            }
            Column {
                attr {
                    flex(1f)
                    backgroundColor(Color.Background)
                    justifyContentFlexStart()
                    paddingTop(50f)
                    alignItemsCenter()
                }
                View {
                    attr {
                        padding(left = 15f, right = 15f)
                        backgroundColor(Color.GRAY)
                        // 边框 圆角
                        borderRadius(999f)
                    }
                    Input {
                        ref {
                            ctx.userNameRef = it
                        }
                        attr {
                            size(200f, 40f)
                            placeholder("用户名")
                            fontSize(16f)
                        }
                        event {
                            textDidChange {
                                ctx.userName = it.text
                            }
                        }
                    }
                }
                View {
                    attr {
                        marginTop(10f)
                        padding(left = 15f, right = 15f)
                        backgroundColor(Color.GRAY)
                        // 边框 圆角
                        borderRadius(999f)
                    }
                    Input {
                        ref {
                            ctx.passwordRef = it
                        }
                        attr {
                            size(200f, 40f)
                            placeholder("密码")
                            fontSize(16f)
                            keyboardTypePassword()
                        }
                        event {
                            textDidChange {
                                ctx.password = it.text
                            }
                        }
                    }
                }
                View {
                    attr {
                        marginTop(20f)
                        backgroundColor(Color.Fore)
                        // 边框 圆角
                        borderRadius(999f)
                    }
                    Button {
                        attr {
                            size(100f, 40f)
                            titleAttr {
                                text("登录")
                                fontSize(16f)
                                color(Color.WHITE)
                            }
                        }
                        event {
                            click {
                                // clearFocus
                                // closeKeyboard
                                bridgeModule.closeKeyboard() // TODO Native 未实现

                                if (ctx.userName.isEmpty()) {
                                    toast("用户名不能为空")
                                    return@click
                                }
                                if (ctx.password.isEmpty()) {
                                    toast("密码不能为空")
                                }

                                ctx.login(ctx.userName, ctx.password)
                            }
                        }
                    }
                }
                Text {
                    attr {
                        marginTop(20f)
                        alignSelfFlexEnd()
                        marginRight(100f)
                        text("去注册")
                    }
                    event {
                        click {
                            routerModule.openPage("register")
                        }
                    }
                }
            }
        }
    }

    private fun login(userName: String, password: String) {
        val url = WanAPI.BASE_URL + WanAPI.LOGIN
        val param = JSONObject().apply {
            put("username", userName)
            put("password", password)
        }
        networkModule.requestPost(url, param) { data, success, errorMsg, response ->
            runResponseData {
                response to data
            }.onFailure {
                it.biz(this)
            }.onSuccess<UserInfo>({
                // 保存 Set-Cookie，以后在每次请求加上 Cookie
                val cookiesList = it["Set-Cookie"] ?: run {
                    toast("登录信息获取失败")
                    return@onSuccess
                }
                val cookiesString = CookiesUtils.formatCookies(cookiesList)
                // save
                sharedPreferencesModule.setString("Cookie", cookiesString)
            }) {
                // 登录接口返回的数据不完整，请求另一个接口获取完整数据
                getUserInfo()
            }
        }
    }

    private fun getUserInfo() {
        // get
        val cookiesString = sharedPreferencesModule.getString("Cookie")
        println(cookiesString)

        val url = WanAPI.BASE_URL + WanAPI.USERINFO
        val param = JSONObject()
        val headers = JSONObject().apply {
            put("Cookie", cookiesString)
        }
        networkModule.httpRequest(url, false, param, headers) { data, success, errorMsg, response ->
            runResponseData {
                response to data
            }.onFailure {
                it.biz(this)
            }.onSuccess<SuperUserInfo> {
                it.ifNotNull {
                    val json = it.toJson()
                    // save
                    sharedPreferencesModule.setString("superUserInfo", json)
                    // notify
                    postNotify("superUserInfo", JSONObject(json))
                    // close
                    bridgeModule.closePage()
                }
            }
        }
    }

}