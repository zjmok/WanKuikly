
# WanKuikly

当前使用版本

- Kuikly 2.4.0
- Kotlin 2.0.21
- Kotlin OHOS 2.0.21-KBA-004

---

使用到的库

- `kotlinx-serialization`
- `kuiklyx-coroutines` 目前还有问题

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
