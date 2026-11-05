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
