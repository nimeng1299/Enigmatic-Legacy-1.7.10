# Enigmatic Legacy 1.7.10 移植 — 未实现内容清单

> 对照基线：`E:\code\Enigmatic-Legacy-1.20.x`（1.20.1 / Forge）
> 当前状态：`./gradlew build` 通过；服务端冒烟测试与客户端渲染截图验证通过
> 最后更新：2026-11-05

图例：**未实现** = 1.7.10 可做但尚未做；**部分实现** = 有简化版或仅保留数据结构；**平台不适用** = 1.7.10 无对应机制，不建议移植。

---

## 一、玩法系统

### 1. 特殊战利品注入 — 未实现（高优先级）

1.20 通过全局战利品修正器 `objects/SpecialLootModifier` 实现以下一次性奖励：

| 奖励 | 触发条件 | 1.7.10 可行性 |
| --- | --- | --- |
| Architect Eye（`ENIGMATIC_EYE`） | 玩家第一次打开任意原版箱子（持久标记 `LootedArchitectEye`） | 可做 |
| Ichor Bottle | 每人仅能获得一次（持久标记 `LootedIchorBottle`），重复生成时移除 | 可做 |
| Astral Fruit | 诅咒玩家第一次打开末地城宝箱（持久标记 `LootedFirstEndCityChest`） | 有限支持（无结构战利品表，需按维度/坐标近似判定） |
| Heart of the Earth | 考古可疑方块 3%（`CustomLoot.EarthHeartChance`）；同时以权重 7 注入上世界地牢宝箱 | 地牢宝箱途径**已实现**（`ChestGenHooks`）；考古平台不适用（1.7.10 无考古系统） |

现状：`objects/SpecialLootModifier` 只保留了配置项，`registries/EnigmaticLootModifiers` 仅实现了 Earth Heart 一项；**Earth Heart 的地牢宝箱注入已实现**（`EnigmaticLootModifiers.register()`，六类地牢宝箱，权重 7，受 `CustomDungeonLootEnabled` 与 `EarthHeartEnabled` 控制），其余三项（Architect Eye / Ichor Bottle / Astral Fruit）**没有任何战利品注入逻辑**。

补充：1.20 主世界地牢 `epic` 池中的**铁指环（权重 20）已补齐**（同一 `EnigmaticLootModifiers.register()`，受 `CustomDungeonLootEnabled` 与 `IronRingEnabled` 双重控制）；同池的磁铁戒指、急迫药水、抄录笔、铁工具与杂项物品仍未注入。

建议实现：监听 Forge 的 `PlayerOpenContainerEvent`，在服务端识别 `ContainerChest` / 末影箱，按玩家持久 NBT 一次性注入物品；末地城宝箱可用维度 + 距离末地岛中心的范围判定近似。

### 2. 经验掉落修正 — 部分实现

1.20 `EnigmaticEventHandler.onExperienceDrop` 的三段逻辑：

- Monster Charm 击杀怪物时按 `MonsterCharm.bonusXPModifier` 增加经验 — **已实现**（`MixinEntityLivingBase` 重定向 `onDeathUpdate` 中的 `getExperiencePoints` 调用，受 `DoubleXP` 开关控制）。
- 佩戴 Cursed Ring 时按 `CursedRing.experienceBonus` 增加经验 — **已实现**（同上；默认配置为在原版经验之上再 +400%，即 5 倍）。
- 佩戴 Enigmatic Amulet 的玩家死亡时取消经验掉落（非 `keepInventory`）— **未实现**。1.7.10 玩家掉落处理结束时 `POSTMORTAL_POSESSIONS` 已被清空，而经验球在死亡 20 tick 后才生成，需要在死亡时额外记录玩家是否持有护符。

### 3. 诅咒玩家附魔等级加成 — 已实现

1.20 `onEnchantmentLevelSet`：附魔台 16 格半径内只要存在任意诅咒玩家，附魔等级增加 `CursedRing.enchantingBonus`（配置值，只加成一次，不随人数叠加）。

现状：`MixinContainerEnchantment` 重定向 `ContainerEnchantment.onCraftMatrixChanged` 中的 `EnchantmentHelper.calcItemStackEnchantability`，对每个非零附魔等级追加配置值。加成发生在原版等级计算之后，因此满书架时的 30 级上限也能继续提升（默认 +10，即最高约 40 级）。Et Futurum 在 `enableNewEnchantingTable=true` 且 `tileReplacementMode=0` 时会用自己的方块和 `ganymedes01.etfuturum.inventory.ContainerEnchantment` 替换附魔台，另由 `MixinEtFuturumContainerEnchantment`（`@Pseudo` 可选兼容）覆盖同一逻辑。

### 4. 末影珍珠伤害免疫 — 未实现（改动很小）

1.20 `onEnderPearlTeleport`：佩戴 Eye of Nebula 或 The Cube 时末影珍珠传送伤害归零。

现状：`EnigmaticEventHandler.onEnderTeleport`（约 689 行）只处理 `ELTeleportBlock` 取消逻辑，没有珍珠伤害处理。

实现方式：1.7.10 `EnderTeleportEvent` 同样有 `attackDamage` 字段，直接对该字段清零即可；注意只对玩家生效（末影人传送也会触发该事件）。

### 5. 引言（Quote）触发缺失 — 未实现

对照 1.20，4 个触发未移植：

| Quote | 1.20 触发 | 1.7.10 可行性 |
| --- | --- | --- |
| `COUNTLESS_DEAD` | 玩家召唤凋灵，延迟 20 tick | 可做（`EntityJoinWorldEvent` 检测 `EntityWither`） |
| `HORRIBLE_EXISTENCE` | 玩家召唤末影龙，延迟 100 tick | 可做（同上检测 `EntityDragon`） |
| `END_DOORSTEP` | 玩家激活末地传送门，延迟 40 tick | 可做（`EndPortalActivatedEvent` 类已存在但从未被 post，需 Mixin `ItemEnderEye` 或 `PlayerInteractEvent` 检测） |
| `I_WANDERED` | 玩家进入末地折跃门方块 | 平台不适用（1.7.10 无末地折跃门，1.9+ 才有） |

### 6. Toast 通知系统 — 部分实现

- `SlotUnlockedToast` 在 `ClientPacketHandlers.java:216` 被加入 `SCHEDULED_TOASTS`，但**没有任何代码消费/渲染该队列**（1.20 在客户端 tick 中调用 `Minecraft.getToasts().addToast`）。
- `RevelationTomeToast` 类存在但从未被实例化；启示录阅读通知改为聊天栏消息（`ClientProxy.pushRevelationToast`）。
- 建议：参照现有 `QuoteOverlay`，在 `RenderGameOverlayEvent` 添加一个 toast 队列绘制；或明确保留聊天栏方案。

### 7. 永久物品拾取动画 — 未实现

1.20 `client/fx/PermanentItemPickupParticle` + `ClientProxy.handleItemPickup`：永久物品被拾取时播放飞向玩家的粒子动画。

现状：`CommonProxy.handleItemPickup` 与 `ClientProxy.handleItemPickup` 均为 NO-OP，`PacketHandleItemPickup` 实际不产生视觉效果。

### 8. 末影龙深渊之心掉落 — 未实现

1.20 `MixinEnderDragon.tickDeath`：

- 击杀龙的“有价值者”获得 Abyssal Heart（`PermanentItemEntity` 形式掉落，最多 5 次，持久计数 `AbyssalHeartsGained`）。
- 龙死亡 200 tick 时，附近 256 格内所有玩家播放 `WITH_DRAGONS` 引言（延迟 140 tick）。

现状：`api/quack/IAbyssalHeartBearer` 接口存在但**从未被调用**；深渊之心只有创造模式/其他途径可获得，`WITH_DRAGONS` 永不触发。

建议实现：在 `LivingDropsEvent`（或 `LivingDeathEvent`）中处理 `EntityDragon`，逻辑参考 1.20 `onLivingDrops` 的龙分支。

---

## 二、渲染 / 外观

### 9. EnigmaticElytraLayer — 未实现

1.20 通过渲染层在玩家模型上叠加鞘翅翅膀（纹理 `textures/models/misc/elytra.png`），同时 `renderCape` 事件取消原版披风显示。

现状：物品功能（飞行/滑翔）已移植，但**没有任何翅膀外观**，且佩戴者仍显示原版披风。

### 10. ShieldAuraLayer — 未实现

以太护盾光环（纹理 `textures/models/misc/ultimate_wither_armor.png`，1.20 用 `RenderType.energySwirl`）：

- 以太盔甲满套、血量低于阈值时环绕玩家的能量漩涡。
- 无敌阶段的 Ultimate Wither Skull 外层光环。

现状：`PermanentItemRenderer` / `UltimateWitherSkullRenderer` 已重写，但凋灵头颅只有 3D 模型，无光环；护盾光环完全缺失。

### 11. 铭牌与披风渲染 — 未实现

- 1.20 `renderNameplate`：佩戴 Insignia 时允许显示玩家自己的铭牌。
- 1.20 `renderCape`：佩戴 Enigmatic Elytra 时隐藏披风（与第 9 条关联）。

1.7.10 可用 `RenderPlayerEvent.Specials` / `RenderLivingEvent` 实现。

### 12. 熔岩雾效 — 未实现

1.20 `onFogRender`：拥有 Molten Heart 效果或佩戴 Blazing Core 时改变岩浆雾的起止距离。

现状：无任何雾效代码。1.7.10 可用 Forge 的 `EntityViewRenderEvent.FogDensity` 实现。

---

## 三、书籍 / UI

### 13. 书籍内容与交互 — 部分实现

自定义 `client/gui/AcknowledgmentBookScreen` 已实现分类 → 条目 → 页面阅读，但相比 Patchouli 仍缺少：

- 合成预览页 / 物品展示页（目前只渲染文字）。
- 已读 / 未读状态记录。
- 书籍所有者名称渲染（1.20 `gui/AcknowlegmentNameRenderer`）。
- 网页链接打开（1.20 `MixinGuiBook` 拦截 Patchouli 链接，1.7.10 无对应页面）。

### 14. 末影箱返回按钮 — 未实现

1.20 `EnderChestInventoryButton` / `PlayerInventoryButton` 在末影箱界面添加“返回玩家背包”按钮。

现状：`client/gui/InventoryButtons` 只在玩家背包界面添加“打开末影箱 / 磁铁开关”按钮，末影箱界面没有返回按钮。

### 15. 物品提示细节 — 部分实现

1.20 `onEarlyTooltip` / `onTooltipRendering` 对模组物品的名称/描述做自定义着色（按稀有度、诅咒状态等）。

现状：只有普通 `onTooltip`（追加描述），没有名称/颜色覆盖。

---

## 四、集成 / 外围

### 16. JEI 插件 — 平台不适用（1.7.10 用 NEI）

1.20 `EnigmaticJEIPlugin`：从 JEI 中隐藏 `IHidden` 物品、注册信息页。

现状：1.7.10 没有 JEI；`IHidden` 接口没有任何消费者，隐藏物品（Quote Player、Relic of Testing 等）可能出现在 NEI/创造模式搜索中。如需要应接入 NEI 的隐藏物品 API。

### 17. 更新检查器 — 未实现

1.20 `EnigmaticUpdateHandler` 从远程拉取版本信息并在聊天栏提示；移植版中 `PacketUpdateNotification` 存在但未使用，也没有配置项。

### 18. 配方书集成 — 平台不适用

1.20 的 `AdvancedBrewingRecipeProcessor`、`HiddenRecipeProcessor`、`MixinAutomaticRecipeUnlockModule` 均围绕配方书/进度解锁；1.7.10 无配方书系统。

### 19. BindToPlayerRecipe — 上游本身禁用

1.20 中 `gemOfBinding` 判定被硬编码为 `false`，该配方永远不匹配。无实际功能，可不移植。

### 20. 进度触发器 — 平台不适用

1.20 `triggers/` 下 7 个 Advancement 触发器（`BeheadingTrigger`、`CursedInventoryChangedTrigger`、`CursedRingEquippedTrigger`、`ForbiddenFruitTrigger`、`RevelationGainTrigger`、`RevelationTomeBurntTrigger`、`UseUnholyGrailTrigger`）依赖进度系统；1.7.10 无 Advancement API，未移植。对应行为（如“烧毁启示录”）目前只保留 Item 逻辑，不发放进度。

---

## 五、1.20 专属 Mixin 未移植清单

| Mixin | 1.20 作用 | 1.7.10 结论 |
| --- | --- | --- |
| `MixinEnderDragon` | 龙死亡掉落深渊之心 + 引言 | 未实现，见第 8 条 |
| `MixinEnderEyeItem` | 激活末地门前 post `EndPortalActivatedEvent` | 未实现，见第 5 条 |
| `MixinCrossbowItem` | 弩的特殊弹药处理 | 平台不适用（1.7.10 无弩） |
| `MixinEntityRenderer` | 自定义铭牌显示规则 | 未实现，见第 11 条 |
| `MixinItemStack` | 按玩家自定义物品显示名称 | 未实现 |
| `MixinLivingEntity` / `MixinPlayer` | 盾牌不可被斧禁用、末地锚重生逻辑 | 1.7.10 盾牌机制不同；末地锚已通过 Block 设置重生点，行为近似 |
| `MixinPlayerList` | 自定义重生处理 | 未实现（1.7.10 无对应事件） |
| `MixinMobEffect` | 药水效果 tick 修改 | 未实现 |
| `MixinVillager` / `MixinVillagerEntity` / `MixinHoglinTasks` / `MixinPiglinTasks` / `MixinPhantomSpawner` / `MixinShulker` | 生物 AI、交易价格、生成调整 | 大多为 1.20 生物/机制，1.7.10 无对应实体（猪灵、幻翼、潜影贝） |
| `MixinStarterGear` | 首次登录发放新手装备 | **已实现**（登录事件，日志可见 “Granting starter gear”） |
| `MixinEnchantmentHelper` / `MixinBindingCurseEnchantment` | 绑定诅咒可作用于饰品 | **已实现**（`enchantments/EternalBindingCurse`） |
| `MixinEnchantmentContainer` | 附魔台自定义行为 | 未实现，见第 3 条 |
| `MixinGuiBook` | Patchouli 链接拦截 | 平台不适用（自绘书籍） |
| `MixinForgeHooks` | 战利品表加载后处理 | 平台不适用 |
| `MixinAutomaticRecipeUnlockModule` / `MixinInventoryChangeTrigger` / `MixinEnterBlockTrigger` / `MixinSummonedEntityTrigger` | 进度触发与配方解锁 | 平台不适用 |
| `MixinWorldListEntry` / `MixinMiniInventoryButton` | 世界列表标记 / Curios 按钮 | 平台不适用或已由 `InventoryButtons` 覆盖 |
| `Accessor*` | 1.20 内部字段访问器 | 平台不适用 |

---

## 六、平台差异导致的功能降级

1. **药水效果缺失**：**已解决**。1.7.10 原本缺少跳跃提升、抗性提升、缓慢下降的药水物品；`MixinPotionHelper` 为三者注册了空闲的元数据位模式，`PotionHelper.registerLegacyBasePotions()` 提供可酿造的基础药水（史莱姆球/铁锭/羽毛），终极药水配方随之可用。缓慢下降改为自定义 `SlowFallingEffect`（下坠限速并免疫摔落伤害），不再近似为缓慢；滞留药水使用 `EnigmaticAreaEffectCloud` 持续施加区域效果。
2. **无考古系统**：Earth Heart 无法通过可疑方块获得，`CustomLoot.EarthHeartChance` 配置保留但无效；地牢宝箱途径已通过 `EnigmaticLootModifiers.register()` 用 `ChestGenHooks` 实现（`DUNGEON_CHEST`、`MINESHAFT_CORRIDOR`、`STRONGHOLD_CORRIDOR`/`CROSSING`、沙漠/丛林神庙宝箱，权重 7）。
3. **无进度 / 配方书**：对应触发器与配方解锁逻辑均不适用。
4. **依赖替代**：Patchouli → 自绘 `AcknowledgmentBookScreen`；Curios → Baubles-Expanded；JEI → 未接入 NEI。
5. **龙首 / 部分生物掉落**：1.7.10 无龙首、潜影贝、猪灵等，相关掉落分支已跳过。
6. **饰品属性自动应用**：1.20 由 Curios 在装备/卸下时自动调用 `ICurioItem#getAttributeModifiers` 并同步到玩家属性表；Baubles 无此机制，移植版中这些 `getAttributeModifiers` 实现原本全部是死代码。**已解决（护甲 + 幸运）**：`MixinEntityPlayerArmor` 通过 `compat/curios/CuriosApi.getEquippedArmorBonus(...)` 实时读取饰品栏中的 `Attributes.ARMOR` 加法修饰器并折算进 `getTotalArmorValue()`，铁指环的 +1 护甲因此生效（双端一致，无需登录/重生时重新同步）；满钻甲时也能生效，总值上限 24 是 1.7.10 `25 - armor` 伤害公式的安全上限。`Attributes.LUCK` 同理由 `CuriosApi.getEquippedLuckBonus(...)` 读取并接入钓鱼（见第 13 条）。**仍缺失**：其它饰品的 `ATTACK_DAMAGE` / `ARMOR_TOUGHNESS` 等修饰器没有消费者（1.7.10 也无对应属性系统），如需要应另建通用的双端属性同步桥，注意不要与自行管理属性的物品（Golem Heart、Enigmatic Amulet 等）重复计数。
7. **属性 tooltip**：1.20 由 Curios 自动显示「+1 Armor」；1.7.10 已在 `IronRing.appendHoverText` 中显式补上本地化行（`tooltip.enigmaticlegacy.ironRingArmor`，五种语言）。其它饰品的属性提示仍未补。
8. **怪物猎人勋章的作用范围**：已静态核对，四项效果链全部闭合（伤害加成 `EnigmaticEventHandler.onEntityHurt` → EVENT_BUS；双倍经验 `MixinEntityLivingBase` → `SuperpositionHandler.applyExperienceBonuses`；+1 抢夺 `MixinEnchantmentHelper` → `getCurioLootingBonus` → `MonsterCharm.getLootingLevel`，且 `LivingDropsEvent.lootingLevel` 与原版同源，斩首概率随之受益）。但 1.7.10 用 `IMob` 判定受害者/死者，1.20 用 `Monster`（伤害分支另加 EnderDragon、并有 `isAggressive()` / `canChangeDimensions()` 门槛），因此 1.7.10 对恶魂、史莱姆、岩浆怪、末影龙也提供 +10% 伤害与双倍经验（恶魂在伤害分支被显式排除，经验分支没有）。**未改动**：收紧会削弱现有行为并同时影响七咒之戒的怪物伤害减益分支。若要严格对齐，把 `EnigmaticEventHandler.java:1288` 与 `SuperpositionHandler.java:253` 的判定收紧为 `EntityMob`。
9. **勋章的两个布尔开关**：1.20 里 `DoubleXP` / `BonusLooting` 都只是 tooltip 死配置，关掉仍生效；1.7.10 的 `DoubleXP` 本来就真的生效，本次起 `BonusLooting` 也在 `MonsterCharm.getLootingLevel` 里生效，避免 tooltip 说"没有 +1 抢夺"而实际仍有。默认 true 时两版行为一致，设为 false 时 1.7.10 会真的关闭效果（比上游更符合配置语义）。
10. **charm 槽位与槽位解锁**：1.20 的 charm 槽默认 1 个、可由 Ichor Bottle 永久 +1；1.7.10 用 Baubles 的 AMULET 槽（同样 1 个，数量等价），Baubles-Expanded 虽有内置 `charmType` 但未单独分配槽位。`SuperpositionHandler.unlockSpecialSlot` 为空实现（Baubles 槽位是全局静态分配，无每玩家解锁机制）、`CHARM_SLOT_UUID` 为零引用死字段、`isSlotLocked` 恒 false。属于平台差异，不是勋章专属缺失；若改动需同时迁移 11 个 charm 类物品并考虑旧存档槽位兼容。

11. **猎宝者护符的额外触及距离**：1.20 用 `ForgeMod.BLOCK_REACH` 属性修饰器实现（`MiningCharm.getAttributeModifiers`，配置 `MiningCharm:ReachDistance`，默认 +2.15 格），1.7.10 既没有触及属性、`getAttributeModifiers` 也没有消费者，移植版此前只留了一句注释和空 map。**已解决（双端注入）**：`SuperpositionHandler.getBlockReachBonus` 统一给出加成，`MixinPlayerControllerMP` 注入 `PlayerControllerMP#getBlockReachDistance`（客户端准星射线，`EntityRenderer#getMouseOver` 靠它选取方块），`MixinItemInWorldManager` 注入 `ItemInWorldManager#getBlockReachDistance`（服务端校验：`NetHandlerPlayServer` 用 `reach + 1` 判定挖掘与放置距离，`Item#getMovingObjectPositionFromPlayer` 用它做物品射线）；`compat/ClipContext` 的射线同样计入该加成。注意两点实现约束：`ItemInWorldManager#getBlockReachDistance` 是 Forge 新增方法（无 SRG 映射），注入必须标 `remap = false`，否则注解处理器报 “Unable to locate obfuscation mapping”；读取 `thisPlayerMP` 必须走 `@Accessor`（`MixinItemInWorldManagerAccessor`），本工程的注解处理器不会为 `@Shadow` 公开字段写 refmap，正式环境会找不到字段。

12. **血战沙场之证的攻击速度**：1.20 的 `BerserkEmblem` 把「每损失 1% 生命 +1% 攻击速度」写成 `ATTACK_SPEED` 属性修饰器（`curioTick` 里以 transient modifier 施加），而 1.7.10 没有攻击冷却，也没有任何代码读取该属性——移植版此前照搬了属性却没接消费者，等于这一条效果不存在（配置与 tooltip 都在，实际无效）。**已解决**：改为作用在受击后的无敌帧上，`BerserkEmblem.shortenHurtResistance` 在玩家近战命中（`DamageTypes.PLAYER_ATTACK`，与原版冷却只对近战生效一致）时按同一百分比缩短目标的 `hurtResistantTime`：新值 = `max/2 + (max/2)/(1+加成)`，即把「下一次满额命中前的等待」从 `max/2` 缩短为 `(max/2)/(1+加成)`，每秒命中数与 1.20 的等价。实现依赖一个容易踩错的事实：1.7.10 的 `LivingHurtEvent` 是在 `EntityLivingBase#damageEntity` 里触发的，即 `hurtResistantTime` 已经被重新武装**之后**，所以在事件里写该字段不会被覆盖；同时用 `min(当前值, 新值)` 确保只缩短、不延长（处于冷却中的追加伤害不会重新武装无敌帧，其事件同样会走到这里）。`ATTACK_SPEED` 属性修饰器仍按 1.20 保留，但注释已标明它是占位、不产生效果。

13. **精美戒指的幸运与猪灵效果**：1.20 的 `GoldenRing` 有三条实现——`Attributes.LUCK +1`（由 Curios 自动显示为「+1 Luck」属性行）、`makesPiglinsNeutral`、`isPiglinCurrency`。1.7.10 侧：**幸运已实现**，`CuriosApi.getEquippedLuckBonus(player)` 汇总饰品栏里 `Attributes.LUCK` 的加法修饰器，`MixinEnchantmentHelper` 注入 `EnchantmentHelper#func_151386_g`（钓鱼的「海之眷顾」等级，全代码库只有一个调用方 `EntityFishHook`）把幸运折进钓鱼幸运等级：垃圾概率 `0.1 - 0.025×luck`、宝藏概率 `0.05 + 0.01×luck`，方向与 1.20 的幸运（压低垃圾、推高宝藏）一致；tooltip 另补 `tooltip.enigmaticlegacy.goldenRingLuck`（五语言），因为 1.20 那行是 Curios 自动生成的。同一条链路顺带让 `CosmicScroll`、`TheCube` 的 LUCK 修饰器一并生效。**平台不适用**：1.7.10 无猪灵、无反交易，`makesPiglinsNeutral` / `isPiglinCurrency` 无处可施；僵尸猪人本就是中立生物（只有被攻击才激怒），「戴上后保持中立」在原版行为下已经成立，故未额外改动（改成「攻击猪人也不激怒」会超出 1.20 的语义）。**仍不可做**：1.20 的幸运还作用于战利品表的 `quality` 权重，而 1.7.10 的宝箱内容在区块生成时就已掷定、与玩家无关。

14. **附魔师的珍珠**：1.20 的实现由三块组成——`clickMenuButton` 整体接管（双次附魔并合并、把 Binding Curse 以 50% 转成 Eternal Binding）、`getGoldCount` 返回 64（按钮不再因青金石不足变灰）、charm 槽 +1 属性修饰器。1.7.10 侧此前只有一个从未被调用的 `isPresent()`，三块都没接。**已实现（双次附魔）**：`EnchanterPearl.applyEnchanting` 按 1.20 的规则掷两次——第一次 `buildEnchantmentList(level)`、第二次 `min(level + 7, 40)`（对应 1.20 允许宝藏附魔的第二掷），书输入先转成附魔书并按原版规则随机扣掉一个附魔，最后 `SuperpositionHandler.mergeEnchantments(..., false, false)` 合并。两个附魔台各接一处：原版走 `MixinContainerEnchantment` 注入 `enchantItem` HEAD，**消耗沿用 1.7.10 的展示等级**（1.20 的 1~3 级是它自己的原版消耗模型，照搬到 1.7.10 等于白送 29 级）；Et Futurum 走 `MixinEtFuturumContainerEnchantment` 注入 `enchantItem`，跳过青金石需求与消耗、消耗 `slot + 1` 级（EF 本身就是 1.8 模型，与 1.20 一致），并让 `getLapisAmount()` 返回 64 保持按钮可点（对应 1.20 的 `getGoldCount`）。**跳过**：Binding Curse → Eternal Binding 转换，1.7.10 没有 Binding Curse（`Enchantments.BINDING_CURSE` 为 null），而本模组的诅咒在这里是普通附魔、`mapEnchantmentData` 不过滤 treasure，第二掷本就能掷出，「有几率出诅咒」自然成立。**平台不适用**：charm 槽 +1（Baubles 槽位静态分配，见第 10 条）。**顺带修掉的隐患**：Et Futurum 的 `enchantItem` / `onCraftMatrixChanged` 是原版方法的覆盖，正式包里是 SRG 名（`func_75140_a` / `func_75130_a`），而 `@Pseudo` mixin 拿不到方法选择器的 refmap 映射（注解处理器只写 `@At` 目标），原先只写了 MCP 名，正式环境打开 EF 附魔台会因找不到方法而崩溃；现在两套名字各写一份并 `require = 0`，开发与正式环境各命中一个。**修复（点击无反应）**：Et Futurum 的 `GuiEnchantment.mouseClicked` 会先在**客户端**调用 `container.enchantItem(...)`，只有返回 `true` 才 `sendEnchantPacket` 发给服务端；原先的注入在 `world.isRemote` 时直接放行，客户端落到 EF 原版逻辑（青金石槽为空）返回 `false`，包根本没发出去，表现为「选项亮但点击没反应」。现在客户端分支通过同一组校验后直接 `cir.setReturnValue(true)` 作为 GUI 预测，只有服务端真正掷附魔并扣级（与 1.20 在 `container.access.execute` 里只于服务端执行的结构一致）。

15. **行刑者之斧**：1.20 的实现由三部分组成——`LivingDropsEvent` 里的斩首（基础 `BeheadingBase`=10% + 每级抢夺 `BeheadingBonus`=5%、上限 100%，覆盖骷髅/凋灵骷髅/僵尸/苦力怕/末影龙）、`isCorrectToolForDrops` 恒 `false`（不能当采集工具）、tooltip 显示当前斩首概率（含饰品栏抢夺）。1.7.10 侧**斩首本身已实现**（`EnigmaticEventHandler#onLivingDrops`，用 `Items.skull` 的 meta 0/1/2/4 区分骷髅/凋灵骷髅/僵尸/苦力怕；末影龙无龙首，跳过。凋灵骷髅原版本就掉头，`ifDroplistContainsItem` 命中时不重复追加，与 1.20 一致）。**`isCorrectToolForDrops` 已等价**：1.7.10 走 `func_150897_b`/`canHarvestBlock`，本物品的 `effectiveBlocks` 与 `effectiveTags` 都是空集，因此同样采不到任何方块、挖掘速度退化为 1.0，与 1.20 硬编码 `false` 的结果相同（`ForbiddenAxe` 里的一参 `isCorrectToolForDrops(BlockState)` 只是占位，1.7.10 无调用点，已加注释说明）。**补齐的两处差异**：① tooltip 之前只算附魔抢夺等级，而 1.20 还会叠加 Curios 提供的抢夺，且实际掉落用的 `event.lootingLevel` 已经包含这份加成（`MixinEnchantmentHelper` 注入 `getLootingModifier`），导致显示概率低于真实概率——现在 tooltip 同步加上 `SuperpositionHandler.getCurioLootingBonus`；② 1.20 用 `Zombie.class` 精确匹配，僵尸村民（独立类）不掉头，而 1.7.10 的僵尸村民只是 `EntityZombie` 的标志位，现补 `!isVillager()` 排除。**平台不适用**：斩首成就 `enigmaticlegacy:forbidden_axe_beheading`（`BeheadingTrigger` 未移植，`SuperpositionHandler.hasAdvancement/grantAdvancement` 在 1.7.10 均为空实现）。

16. **以太套装**：1.20 的效果是「穿齐四件以太护甲 + 血量 ≤ 阈值（默认 40%，拾遗者卷轴可改）→ 以太护盾」：箭/火球类投射物完全免疫、近战伤害减免（默认 50%）、击退攻击者并播放音效、身上有能量光晕。1.7.10 侧**功能部分全部已实现**——`EtheriumEventHandler` 的 `LivingAttackEvent` 取消 `EntityArrow`/`EntityFireball`（覆盖 1.7.10 全部对应投射物，含 `EntityWitherSkull`、大小火球），`LivingHurtEvent` 做击退与减伤；`EtheriumArmor.hasShield` 的全套判定、`EtheriumConfigHandler` 的 40%/50% 默认值、拾遗者卷轴覆盖阈值（`getShieldThreshold`）均与 1.20 一致。1.20 里「护盾清除负面效果」那段代码在 2.5.0 已被注释掉，无需移植。**补齐**：1.20 的 `ShieldAuraLayer`（护盾激活时用 `models/misc/ultimate_wither_armor.png` 把玩家放大 1.05 倍再渲染一遍，`RenderType.energySwirl` 加法混合 + UV 滚动）在 1.7.10 完全缺失；现新增 `com/aizistral/etherium/client/ShieldAuraLayer`，挂在 `RenderPlayerEvent.Specials.Post` 上（该事件仍在玩家变换内、模型保持本帧姿势），逐个部件渲染而非调用 `ModelBiped#render`（后者会重跑 `setRotationAngles` 把姿势打回默认），用 `GL_TEXTURE` 矩阵平移复刻 UV 滚动，并在 `ClientProxy` 注册。**未做**：游戏内视觉验证。

17. **破烂书籍的战利品掉落**：1.20 用两个**独立** LootPool 注入主世界箱子——`overworldLiterature`（rolls 取 `-7..2`，加进 `SIMPLE_DUNGEON`/`ABANDONED_MINESHAFT`/`STRONGHOLD_CORRIDOR`/`STRONGHOLD_CROSSING`/`DESERT_PYRAMID`/`JUNGLE_TEMPLE` 等，`SHIPWRECK_SUPPLY` 除外）与 `literature`（rolls 取 `-4..3`，加进要塞图书馆），每次 roll 产出一本、NBT 为 `createTome(1..3, 50..500)`。因为取值范围大部分是负数，实际概率是 **20%**（1 或 2 本）与 **37.5%**（1/2/3 本）。1.7.10 的 `ChestGenHooks` 只能让物品与原版条目竞争同一组权重槽，`addItem` 会把概率稀释到个位数，无法表达"独立附加池"，所以改用 mixin：`MixinChestGenHooks` 在 `getItems` 返回时记下类别与数组引用，`MixinWeightedRandomChestContent` 在 `generateChestContents` 返回后按 1.20 的 rolls 公式往随机空槽追加书籍（`EnigmaticLootModifiers.onChestContentsGenerated`）。**数组引用比较**是关键——奖励箱、下界要塞这些直接传私有数组的箱子因此不会被误判成地牢。类别映射：`DUNGEON_CHEST`/`MINESHAFT_CORRIDOR`/`STRONGHOLD_CORRIDOR`/`STRONGHOLD_CROSSING`/`PYRAMID_DESERT_CHEST`/`PYRAMID_JUNGLE_CHEST` → 20%；`STRONGHOLD_LIBRARY` → 37.5%；1.7.10 不存在的结构（冰屋、林地府邸、海底废墟、沉船、掠夺者前哨、末地城）自然跳过。**实测**（临时服务端探针，各 5000 箱）：`dungeonChest` 20.4%、`strongholdLibrary` 38.44%，平均 0.31 / 0.77 本每箱，与理论 20% / 37.5%、0.3 / 0.75 吻合。**凋零书籍 / 腐烂书籍**：同一套机制扩展到下界与末地池——`netherLiterature`（rolls `-7..2`、NBT `createTome(1..3, 100..700)`）加入 `getNetherDungeons()`（下界要塞、堡垒遗迹、废弃传送门），`endLiterature`（rolls `-7..2`、`createTome(1..4, 200..1000)`）加入 `END_CITY_TREASURE`。1.7.10 的下界要塞箱子用的是 `StructureNetherBridgePieces` 的私有静态数组（不经过 ChestGenHooks），所以 `register()` 在注入炽焰核心之后用反射缓存该数组引用，`onChestContentsGenerated` 通过引用识别；末地类别 `endCityTreasure` 仍按名字匹配，等结构 backport mod 填充后自动生效（1.7.10 原版没有末地箱子，也没有下界要塞之外的堡垒遗迹/废弃传送门）。**实测**（各 5000 箱）：下界要塞 / 凋零 20.12%、末地类别 / 腐烂 20.88%，均 0.31 本每箱。

18. **倒转之启（The Twist）**：对照 1.20 `items/TheTwist.java` 逐条核对，除「对 Boss/玩家的 +300% 伤害」「非诅咒者攻击无效」「绕过七咒之戒的怪物伤害减益」「命中点燃 4 秒」「右键开书」原本已实现外，此前**三处效果是死代码**，本次全部补齐：

- **击退 +300%**：`EnigmaticEventHandler:1020` 原先只有一句「1.7.10: no LivingKnockBackEvent」注释。1.7.10 的近战位移分两段——`EntityLivingBase#attackEntityFrom` 里经 `knockBack` 施加的固定 0.4（该值是 `knockBack` 内的硬编码常量，参数 `strength` 根本没用），以及 `EntityPlayer#attackTargetEntityWithCurrentItem` 里为附魔/冲刺追加的 `Entity#addVelocity`。`MixinEntityPlayerKnockback` 现在两段都覆盖：`@Redirect` 缩放 `addVelocity`，`@Inject(TAIL)` 追加 `0.4 × (倍率 - 1)` 的水平速度（方向取「受害者 - 攻击者」，退化时用攻击者朝向），因此默认为 0.4 → 1.6，与 1.20 的 `strength(0.4) × 4` 等价。同一条链路顺带接通了**无限之启 +200%**、**末影杀手 +600%**（仅末地生物）与**建筑师之恩 +100%**（仅玩家目标），后两者此前同样只有注释。命中判定用 `hurtTime > 0`，被取消/拒绝的攻击不会推动目标；击退抗性沿用原版的随机判定。
- **攻击伤害 9 / 攻击速度 -1.8**：`TheAcknowledgment` 只实现了现代的双参 `getAttributeModifiers(EquipmentSlot, ItemStack)`，而 1.7.10 从 `Item#getAttributeModifiers(ItemStack)` 读取装备属性，二者之间没有任何桥接，导致三本书的伤害修饰器从未生效（实际只有空手 1 点伤害）。桥接现统一实现在 `ItemBase.getAttributeModifiers(ItemStack)`（见下条），把主手修饰器并入原版 `Multimap`。**攻击速度在 1.7.10 无消费者**（无攻击冷却，`RegisteredMeleeAttack` 恒返回 1.0），-1.8 仍只是占位数据。
- **附魔能力 24**：1.7.10 只有无参的 `Item#getItemEnchantability()`，`getEnchantmentValue(ItemStack)` 永远不会被调用，已改为无参 `getEnchantmentValue()`（`ItemBase` 已有桥接）。
- **`use()` 的副手判定**：原代码在 `hand == MAIN_HAND` 分支里再次取 `getHeldItem()`（还是主手），本意是「副手举盾时不翻书」。1.7.10 没有副手槽，直接删除该分支并注明原因。

**同类隐患已统一修复**：`ItemBase` 现在提供 `getAttributeModifiers(ItemStack)` 桥接，等价于「1.7.10 版的默认主手槽装饰器汇入」。`TheJudgement`（∞ +28）、`EldritchPan`、`The Infinitum`、三本书等所有走 `ItemBase` 的武器，其双参属性方法终于生效；此前它们的伤害/攻速全是死数据，只有 `ItemBaseTool` 子类因为另写了 1.7.10 原生 `getItemAttributeModifiers()` 而正常。注意 `EldritchPan` 自己的 "Tool modifier" 与 "Weapon modifier" 共用攻击伤害 UUID，按 UUID 去重后只剩后者（与其 1.20 行为一致）；未重写 `getAttributeModifiers(EquipmentSlot, …)` 的物品（护甲类）返回空 map，桥接直接回落到原版，行为不变。

**平台不适用**：幻翼击退 ×1.5（1.7.10 无幻翼）；书籍标题仍固定显示 "The Acknowledgment"，不随手持的倒转之启/无限之启变化（1.20 由 `gui/AcknowlegmentNameRenderer` 实现，属第 13 条）。

**tooltip 文本修复**：`zh_CN.lang` 的 `tooltip.enigmaticlegacy.theTwist2` 末尾带着两枚私用区字符 `U+E908 U+E909`（1.20 里由 unifont 兜底，1.7.10 没有 `unicode_page_e9.png`，客户端会刷 `Failed to load texture: minecraft:textures/font/unicode_page_e9.png` 并渲染成空白/豆腐块）。该字符对在中文里是「……」的替代字形，现已连同其它 12 处一起替换为真正的省略号。检查结论：`theTwist1`~`theTwist7` 五个语言文件全部存在，占位符 `%1$s` 在 1.7.10 的 `ChatComponentTranslation`（`String.format`）下也能正常代入，不存在「显示原始键」的情况（1.7.10 缺键会回落 en_US，而不是打印键名）。

**验证**：服务端探针确认 `enigmaticlegacy$knockbackMultiplier` 已被并入 `EntityPlayer`（mixin 应用成功）、倒转之启返回 `generic.attackDamage=+8` / `generic.attackSpeed=-1.8`、审判之剑返回 `+∞` / `+28`、邪术锅返回 `+31` / `-3.2` / `+4 护甲`、启示之证返回 `+3.5` / `-2.1`，`getItemEnchantability()=24`；`./gradlew build`（含 Spotless/Checkstyle）通过，服务端启动无异常。**未做**：游戏内实际挥击的击退手感、伤害数值与 tooltip 最终观感实测。

19. **野猎指南（Hunter Guidebook）**：1.20 上该物品只有一条效果——主人背包里有它时，`effectiveDistance`（默认 24）格内**已驯服**宠物受到的伤害会被取消并原样转移给主人；若主人同时持有兽友指南，转移量再乘 `SynergyDamageReduction` 的反向修正（默认 50%）。移植版**原本已实现**（`EnigmaticEventHandler.onEntityHurt` 的 `victim instanceof EntityTameable` 分支），本次核对只发现一处保真度差异并修复：原实现把来源替换成 `DamageSource.causeMobDamage(pet)`，而 1.20 是把 `event.getSource()` 直接交给 `owner.hurt(...)`——在 1.7.10 这意味着被箭/火球/爆炸打中的转移伤害统统变成「生物近战」类型，弹射物保护、爆炸保护、火焰保护全部失效，死亡提示与击杀归属也失真。现改为 `owner.attackEntityFrom(event.source, redirected)`，来源类型、保护附魔与归属链恢复与 1.20 一致。

**实测**（临时服务端探针：FakePlayer 作主人 + 每例新生成的已驯服狼，避免受击无敌帧吞掉后续伤害）：无指南时狼 20→14；持野猎指南时狼保持 20、转移 `amount=6 / redirected=6`；再加兽友指南时 `redirected=3`（50% 减伤），且 `event.source` 的 `damageType` 原样传递。（FakePlayer 的 `attackEntityFrom` 恒返回 false 是 Forge 的保护，所以探针里主人血量不变；数值验证走的是转移量日志，真实玩家会受到对应伤害。**未做**：真实玩家的游戏内实测。）

**平台差异（行为一致，无需处理）**：1.7.10 的 `EntityTameable` 只覆盖狼/豹猫，1.20 的 `TamableAnimal` 覆盖狼/猫/鹦鹉——1.7.10 没有鹦鹉；马在两边都不属于该体系（1.7.10 `EntityHorse` / 1.20 `AbstractHorse` 走 `OwnableEntity`），所以两边都不会转移马匹伤害。物品本身除 tooltip、`stacksTo(1)`、`RARE` 稀有度外没有其它逻辑（`Vanishable` 是 1.20 的标记接口，1.7.10 无对应机制）。

---

## 七、已实现但未完整实测

以下系统已通过编译和启动，但未做完整游戏内流程验证：

- 诅咒之环完整进度（诅咒阶段、梦魇、特殊掉落联动）。
- 死亡确认 / 永久死亡 / 复活 / Soul Archive 回收流程。
- 引言播放时序（音乐延迟、字幕淡入淡出）与全部 28 段引言触发点。
- 酿造边界情况（多槽同时酿造、普通/喷溅/滞留转换）。
- 磁铁戒指、末影戒指、法术石等饰品的实际交互。
- 怪物猎人勋章四项效果链的端到端游戏内验证（伤害倍率、双倍经验、+1 抢夺与斩首概率）；目前只做了调用链静态核对与启动冒烟。
- 猎宝者护符的触及距离：已验证双端 mixin 均能正常应用（服务端探针调用 `ItemInWorldManager#getBlockReachDistance` 返回 5.0，客户端 `PlayerControllerMP` 加载无异常），但未做「佩戴护符后实际挖/放到 7 格」的游戏内实测。
- 血战沙场之证的四项效果：攻击伤害与伤害抗性走 `EnigmaticEventHandler#onEntityHurt`（静态核对已闭合），移动速度走 `AttributeMap`（1.7.10 属性系统本身可用），攻击速度改为缩短受击无敌帧（公式 `max/2 + (max/2)/(1+加成)`）；四者均未做游戏内数值实测。
- 精美戒指的 +1 幸运：已验证注入能正常应用（服务端强制加载 `EnchantmentHelper`/`EntityFishHook` 无异常），但未做「戴戒指钓鱼，统计垃圾/宝藏比例」的游戏内实测。
- 附魔师的珍珠：双次附魔、原版与 Et Futurum 两条附魔台路径均只做了注入应用验证（强制加载两个 `ContainerEnchantment`，`require = 1` 下无注入错误），未在游戏内实际附魔比对结果数量/等级。
- 行刑者之斧：斩首概率公式与骷髅/凋灵骷髅/僵尸/苦力怕四条分支只做了静态核对；tooltip 新增的饰品抢夺加成、僵尸村民排除均未做游戏内验证。
- 以太套装：护盾的投射物免疫、近战减伤、击退与音效只做了静态核对；新增的 `ShieldAuraLayer` 光晕渲染未做游戏内视觉验证（1.7.10 用 `RenderPlayerEvent.Specials.Post` + GL 复刻，无法在无头环境确认实际观感）。

---

## 八、建议实施顺序

1. 末影珍珠伤害免疫（改动最小，立即可做）。
2. 深渊之心龙掉落 + `WITH_DRAGONS` 引言（影响核心内容获取）。
3. 特殊战利品注入（Architect Eye / Ichor Bottle / Astral Fruit）。
4. 经验掉落修正（Mixin）。
5. 引言触发（凋灵/末影龙召唤、末地门）。
6. Toast 队列渲染。
7. 鞘翅翅膀、护盾光环、铭牌/披风、熔岩雾效。
8. 附魔台诅咒加成。
9. 书籍配方预览与已读状态。
10. NEI 隐藏过滤与更新检查（可选）。

---

## 九、验证基线

- `./gradlew spotlessApply build` 通过，产物：`build/libs/enigmaticlegacy-*.jar`（约 7.6 MB，447 个模组类 + Mixin 配置）。
- 服务端冒烟测试：注册表空字段 0/0，5 个法术石，392 条配方（58 条产出本模组物品），Baubles API/饰品判定 true，自定义酿造（粗制 + 石英 → 迅捷）与原版酿造（粗制 + 金胡萝卜 → 夜视）均 PASS。
- 客户端：书籍 GUI、引言字幕、永久掉落物（钻石）与 3D 凋灵头颅渲染截图验证通过，无崩溃。
- 临时验证探针（`PortSmokeTest`、`RenderProbe`）已删除。
