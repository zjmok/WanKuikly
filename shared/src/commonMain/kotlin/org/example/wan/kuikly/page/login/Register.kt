package org.example.wan.kuikly.page.login

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Input
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.views.layout.Column
import org.example.wan.kuikly.base.BasePager
import org.example.wan.kuikly.page.common.NavBar
import org.example.wan.kuikly.utils.Background
import org.example.wan.kuikly.utils.Fore
import org.example.wan.kuikly.utils.bridgeModule
import org.example.wan.kuikly.utils.routerModule
import org.example.wan.kuikly.utils.toast

@Page("register")
internal class Register : BasePager() {

    var userName: String by observable("")
    var password: String by observable("")
    var passwordAgain: String by observable("")

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            attr {
                flexDirectionColumn()
            }
            NavBar {
                attr {
                    title = "注册"
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
                        marginTop(10f)
                        padding(left = 15f, right = 15f)
                        backgroundColor(Color.GRAY)
                        // 边框 圆角
                        borderRadius(999f)
                    }
                    Input {
                        attr {
                            size(200f, 40f)
                            placeholder("确认密码")
                            fontSize(16f)
                            keyboardTypePassword()
                        }
                        event {
                            textDidChange {
                                ctx.passwordAgain = it.text
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
                                text("注册")
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
                                if (ctx.password.isEmpty() || ctx.passwordAgain.isEmpty()) {
                                    toast("密码不能为空")
                                    return@click
                                }
                                if (ctx.password != ctx.passwordAgain) {
                                    toast("两次密码输入不一致，请确认密码")
                                    return@click
                                }

                                toast("注册\nuserName = ${ctx.userName}\npassword = ${ctx.password}")
                            }
                        }
                    }
                }
            }
        }
    }

}