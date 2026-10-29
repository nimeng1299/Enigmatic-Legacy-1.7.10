# AGENTS.md

本文件约束在本仓库中工作的 AI 代理与开发者。除特别说明外，所有命令均在仓库根目录执行。

## 项目概览

- Minecraft 1.7.10 + Forge 10.13.4.1614 模组，基于 GTNH ExampleMod 模板（RetroFuturaGradle + `com.gtnewhorizons.gtnhconvention`）。
- Gradle 9.7.1（Wrapper），Gradle daemon 与 IDEA 项目 JDK 均为 25；`enableModernJavaSyntax = jabel`：允许 Java 17 语言语法，产物为 Java 8 字节码，标准库 API 请按 Java 8 约束使用。
- 源码在 `src/main/java`，资源在 `src/main/resources`；modid、包名、版本等元数据由 `gradle.properties` 驱动（当前为 `enigmaticlegacy` / `com.aizistral.enigmaticlegacy`，接入正式代码时统一从该文件派生，不要散落硬编码）。
- 客户端/服务端分离使用 `CommonProxy` / `ClientProxy`，仅客户端代码放 `ClientProxy` 或标注 `@SideOnly(Side.CLIENT)`。

## 构建：统一使用 IDEA 的 Gradle 配置

- 构建、运行、测试一律使用 IntelliJ IDEA 已链接的 Gradle 项目（见 `.idea/gradle.xml`）：在 IDEA 的 Gradle 工具窗口执行任务，且 `Settings | Build, Execution, Deployment | Build Tools | Gradle` 中 `Build and run using` 与 `Run tests using` 必须为 `Gradle`（delegated build）。不要切换为 IDEA 原生构建器，也不要手工 `javac`。
- 命令行使用仓库自带的 Gradle Wrapper（与 IDEA 使用同一份 wrapper 和 daemon 工具链）：
  - Windows CMD/PowerShell：`gradlew.bat <task>`
  - Git Bash / POSIX：`./gradlew <task>`
  - 不要使用系统安装的 `gradle`。
- 常用任务：
  - `./gradlew build`：编译 + Spotless/Checkstyle 检查 + 打包，产物在 `build/libs/`
  - `./gradlew compileJava`：快速编译
  - `./gradlew spotlessApply`：按 GTNH 格式自动格式化，改完代码后执行
  - `./gradlew runClient` / `./gradlew runServer`：开发运行，工作目录分别为 `run/client`、`run/server`
  - `./gradlew --stop`：Gradle daemon 卡死时使用
- 添加依赖只改 `dependencies.gradle`，添加仓库只改 `repositories.gradle`；修改后在 IDEA 中 Reload Gradle Project。不要改动 `build.gradle.kts`、`settings.gradle.kts` 或 GTNH convention 插件配置。
- Configuration cache 与并行构建已启用（`org.gradle.configuration-cache=true`、`org.gradle.parallel=true`），不要关闭；不要修改 JDK 25 工具链配置。
- 不提交 `.gradle/`、`build/`、`run/`、`.idea/`。

## JSON 模型：只使用 GTNHLib

- 依赖已在 `dependencies.gradle`：`implementation("com.github.GTNewHorizons:GTNHLib:0.11.52:dev")`。不要引入其他 JSON 模型库或自建模型管线。
- 所有 Minecraft JSON 模型（blockstate、`parent` 继承、`textures`、`elements`、`display`、AO 等）必须由 GTNHLib 的 JSON 模型系统解析，禁止自行编写 Gson 反序列化器或复制原版 `ModelBakery` 逻辑。
- 接入方式：
  - 客户端 preInit（`ClientProxy`）中调用 `ModelRegistry.registerModid(MODID)`（`com.gtnewhorizon.gtnhlib.client.model.loading.ModelRegistry`），让 GTNHLib 扫描本 mod 资源包并自动加载模型引用的纹理。
  - 使用 JSON 模型的方块，其 `getRenderType()` 返回 `ModelISBRH.JSON_ISBRH_ID`（`com.gtnewhorizon.gtnhlib.client.model.ModelISBRH`）；该 ISBRH 已由 GTNHLib 自动注册。
  - JSON 资源放在 `assets/<modid>/blockstates/<block>.json` 与 `assets/<modid>/models/<model>.json`，格式遵循原版 JSON 模型规范。
  - 需要动态/自定义模型选择时，实现 `com.gtnewhorizon.gtnhlib.api.IBlockModelProvider#getModel(BakedModelQuadContext)`；默认路径可调用 `ModelRegistry.getBakedModel(context)`。
  - GTNHLib 的 JSON 模型管线面向方块模型；普通 `Item`（非 Block）图标仍使用 `registerIcons` / `IIcon`。
- 反序列化细节（`ModelDeserializer`、`JSONModel`、`UnbakedModel` 等）由 GTNHLib 负责，业务代码不要重复实现。

## 饰品栏：只使用 Baubles

- 依赖已在 `dependencies.gradle`：`implementation("com.github.GTNewHorizons:Baubles-Expanded:2.2.27-GTNH")`，其 modid 为 `Baubles`。在 `@Mod` 的 `dependencies` 中声明 `required-after:Baubles`。
- 所有饰品/饰品栏逻辑必须通过官方 API（`baubles.api.*`、`baubles.api.expanded.*`）：禁止自建饰品背包或槽位系统，禁止直接访问 `baubles.common.*`（如 `PlayerHandler`）等内部实现。
- 饰品物品实现：
  - 基础槽位：实现 `baubles.api.IBauble`，`getBaubleType(ItemStack)` 返回 `BaubleType.RING` / `AMULET` / `BELT` / `UNIVERSAL`。
  - 扩展槽位：实现 `baubles.api.expanded.IBaubleExpanded`，`getBaubleTypes(ItemStack)` 返回 `String[]`，类型常量取自 `BaubleExpandedSlots`；自定义类型只能在 preInit 阶段通过 `BaubleExpandedSlots.tryRegisterType(...)` 注册。
  - 生命周期回调（`onEquipped`、`onUnequipped`、`onWornTick`、`canEquip`、`canUnequip`、`onSlotContentsChanged`、`onPlayerLoad`）由 Baubles 调度，不要自行调用或另建调度逻辑。
- 读取玩家饰品：`IInventory inv = BaublesApi.getBaubles(player)`；返回值可能为 `null`，必须先判空；每次按需获取，不要长期缓存或跨世界/重登持有。遍历槽位用 `getSizeInventory()` / `getStackInSlot(i)`，槽位合法性交给 `isItemValidForSlot`。
- 客户端辅助：`BaubleItemHelper.addSlotInformation(...)` 仅限客户端；右键装备可直接复用 `BaubleItemHelper.onBaubleRightClick(...)`。
- 饰品的网络同步与持久化由 Baubles 处理，不要额外自建同步逻辑。

## 代码与验证约定

- 格式遵循 `.editorconfig`（4 空格、LF、UTF-8）；提交前执行 `./gradlew spotlessApply`，不要通过 `disableSpotless` / `disableCheckstyle` 绕过检查。
- 新代码放入 `gradle.properties` 中 `modGroup` 对应的包下；不要手改生成文件（`build/` 下的 `Tags` 等）。
- 不要手工向 `run/client/mods` 放 jar 作为编译/运行依赖，依赖统一走 Gradle 声明。
- 每次改动后至少执行 `./gradlew build`；涉及渲染、JSON 模型、饰品交互的改动必须运行 `./gradlew runClient` 实测。
