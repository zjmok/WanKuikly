
效果图

<div style="display: flex; justify-content: space-between;">
    <img src="picture/Screenshot_android_home.jpg" width="32%">
    <img src="picture/Screenshot_android_project.jpg" width="32%">
    <img src="picture/Screenshot_android_person.jpg" width="32%">
</div>

---

# WanKuikly

当前使用版本

- Kuikly 2.4.0
- Kotlin 2.0.21
- Kotlin OHOS 2.0.21-KBA-004

当前只在 Android 平台正常运行，未调试其它平台

---

在插件模板创建项目之后，
使用到的库

common
- `kotlinx-serialization`
- `kuiklyx-coroutines` 目前还有问题

android
- `Toaster`

---

todo，待完成

- WebPager
- 搜索页面
- 下拉上拉处理
- 登录，存储
- 懒加载优化
- shared模块的协程问题
- 解决硬编码
- 

---

存在的问题

- common 协程未成功接入
- html 标签的显示
- 多个容器高度设置 0.5f，显示不一致，多个分割线大小不一致，在模拟器上 1f 也大小不一致
- android 模块，kuikly 源码在 ide 加载不出来，爆红，但可以正常使用运行
- Android API 未适配到最新版，最高支持 34
- 

---

写 Bug 心得

- ComposeView，插件创建的类是通过 addChild 添加的，在构建对象时传递参数，view创建时会调用一次。可以先传一个引用，之后更新这个引用的数据

---

## Android

KuiklyTemplate 1.2.0

- KSP 版本与 Kotlin 版本不对应，需要手动修改
- AGP 7.4.2 最高支持 api 33，实际测试 api 34 可以运行
- 打包提示 dex 或 merge 相关错误，设置 `isMinifyEnabled = true` 可正常编译

## iOS

无

## OHOS

截至 0823, OHOS 仅支持 MacOS 编译

编译或运行前，需要手动复制 assets 资源

```
cp shared/src/commonMain/assets ohos/entry/src/main/resource/resfile
```

- 使用独立编译链，即使用带 ohos 的构建脚本

```
./gradlew -c settings.ohos.gradle.kts :shared:linkOhosArm64
```

```
cp build/bin/ohosArm64/releaseShared/libshared.so ohosApp/entry/libs/arm64-v8a/libshared.so
cp build/bin/ohosArm64/releaseShared/libshared_api.h ohosApp/entry/src/main/cpp/libshared_api.h
```

- 或在 DevEco 运行，会使用 hvigor 脚本执行编译命令，并复制 so 和头文件到 ohosApp 中

ohosApp 的 local.properties 配置相关变量

```
# kuiklyCompilePlugin
kuikly.projectPath=../
kuikly.moduleName=shared
kuikly.ohosGradleSettings=settings.ohos.gradle.kts

# OPTIONAL Parameters
#kuikly.soPath=Your so product path(Relative path to the Ohos project root directory, the default is entry/libs/arm64-v8a)
#kuikly.headerPath=Your header product path(Relative path to the Ohos project root directory, the default is entry/src/main/cpp)
```

## MiniApp

无

## H5App

无
