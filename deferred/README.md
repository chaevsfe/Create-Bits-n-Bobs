# Deferred and dropped sources

This directory is **not compiled and not shipped**. It holds upstream sources that phase P1
(server-side scaffold) set aside, so a later phase can pick each one up with its history and its
reason intact. Nothing here is on the source set; the build never sees it.

Three kinds of entry:

* **DEFERRED — client half.** Correct upstream code that needs the 26.2 render pipeline. It comes
  back in the render phase.
* **DEFERRED — needs a redesign.** The upstream mechanism does not exist on Create Fly or 26.2 and
  a replacement has to be designed.
* **DROPPED — with a consequence.** The feature is not coming back as-is; the consequence is stated.

---

## DEFERRED — client half (render phase)

Block entity renderers and Flywheel visuals, all needing 26.2's
`createRenderState` / `extractRenderState` / `submit` pipeline:

* `content/kinetics/chain_pulley/ChainPulleyVisual`
* `content/kinetics/cogwheel_carriage/contraption/CogwheelChainCarriageRenderer`
* `content/kinetics/cogwheel_chain/behaviour/CogwheelChainBehaviourVisual` — the Flywheel half of the chain,
  **not superseded and not dropped**: see the P3b note below. Its sibling `CogwheelChainBehaviourRenderer` is
  gone (P3b).
* `registry/client/BnbBlockEntityBehaviourRenderers` — behaviour renderers

Deleted from here in P3, with their consequences: the headlamp's Flywheel visual package
(`content/trinkets/light/headlamp/rendering/pipeline/visual/**` and `registry/client/BnbInstanceTypes`) carried
a per-instance UV shift through the port's own GLSL, a rendering-backend feature rather than a gameplay one.
The block entity renderer draws the lamps on both backends, so nothing is missing visually; what is lost is
Flywheel instancing for headlamps, and the two `.vert`/`.glsl` files are unused.

Client-side interaction, HUD and input:

* `foundation/gui/screen/BnbFeatureGroupEntry`, `foundation/BnbConfigBridge`

Client mixins still set aside (the rest are live in `bits_n_bobs.client.mixins.json`):

* `mixin/GuiGameElementMixin`, `mixin/compat/**`

### Superseded in P3 — the dyeable model channel

Deleted rather than deferred, because the port now does the same job a different way. NeoForge's
`ModelData` carried the dye colour from the block entity to the model; Create Fly's
`WrapperBlockStateModel.addPartsWithInfo` is handed the level and the position, so the colour is read
where the parts are collected and no channel is needed. `DyeableBlockStateModel` plus
`BnbDyeableModels` (composed onto Create's own wrappers through `AllModels.ALL`) replace all of:

* `content/decoration/dyeable/simple/SimpleDyeableModelWrapper`, `SimpleDyeableModelHelper`
* `content/decoration/dyeable/pipes/DyeablePipeModelHelper`
* `mixin/dyeable/fluid_tank/FluidTankModelMixin`, `mixin/dyeable/pipes/PipeAttachmentModelMixin`,
  `mixin/dyeable/simple/PipeAttachmentModelMixin`
* `mixin/cogwheel_material/SafeBlockEntityRendererMixin` — its target class is gone; the material
  context is now pushed around render-state extraction instead (see
  `mixin/client/cogwheel_material/`).

`mixin/dyeable/fluid_tank/FluidTankRendererMixin` and `mixin/dyeable/simple/FluidValveRendererMixin`
were ported to the extract pipeline and live under `mixin/client/dyeable/`.

### Superseded in P3b — the cogwheel chain's behaviour renderer

`content/kinetics/cogwheel_chain/behaviour/CogwheelChainBehaviourRenderer` is **deleted**. It was a per-behaviour
renderer hosted by Azimuth's `BlockEntityBehaviourRenderer` and invoked from `SafeBlockEntityRenderer.render`;
Create Fly has neither class, and the block entities that carry the chain behaviour are Create's own cogwheels,
plus this port's flanged cogs, plus anything an addon tags — an open-ended host set that no per-type registration
can cover. Both of 26.2's registries hold one entry per block entity type
(`VisualizerRegistry.setVisualizer` is a setter; `AllBlockEntityRenders` ends in vanilla's renderer map), so a
renderer of the chain's own would have had to replace Create's.

The chain is drawn instead from the level's own two phases —
`LevelExtractionEvents.END_EXTRACTION` builds the geometry and lights it, `LevelRenderEvents.COLLECT_SUBMITS`
submits it and reads nothing else — in `content/kinetics/cogwheel_chain/render/CogwheelChainMesh`,
`CogwheelChainRenderState`, `CogwheelChainRenderStates` and `CogwheelChainLevelRenderer`. Flywheel takes no part
in those events, so **one path serves both backends** and no mixin on any Create render class was needed. The
design and the rejected routes are written up in `scratchpad/bnb2/p3b-design.md`.

Two consequences, stated:

* **Chains are CPU-submitted on both backends.** `CogwheelChainBehaviourVisual` stays here rather than being
  deleted, because the Flywheel path is reachable without owning a block entity type: Flywheel's `Effect` /
  `EffectVisual` API (`VisualizationHelper.queueAdd/queueRemove/queueUpdate`) is not keyed by type, and
  everything the visual's mesh needs — `QuadMesh`, `SingleMeshModel`, `AllInstanceTypes.SCROLLING_TRANSFORMED`,
  `ScrollTransformedInstance` — is present in Fly 6.0.9. It buys GPU instancing, not a feature.
* **Upstream's distant "fast but with gaps" chain LOD is not ported.** Distant chains keep their full geometry
  rather than degrading to a mip-strip beam, which costs vertices and loses upstream's anti-shimmer strip.
  Chains beyond 256 blocks — the view distance Create Fly gives its own chain conveyor — are not drawn at all.

**Chains on a moving contraption are not drawn.** Upstream reached them because behaviour renderers ran inside
`SafeBlockEntityRenderer`, which contraption block entities also went through; a level-space event does not.
Covering them needs a hook on Create's contraption block-entity submit, in contraption space, and belongs with
`CogwheelChainCarriageRenderer`, which is deferred with it.

### Restored in P3c — the cogwheel chain's interaction subsystem

All of it is live: `ChainDriveDisplayRenderer`, `CogwheelChainPlacementInteraction`,
`CogwheelChainPlacementEffect`, `CogwheelChainInteractionHandler`, both partial-edit handlers,
`CogwheelChainRidingHelper`, `PlayerSkyhookRendererBridge` and the four mixins the last two need, now under
`mixin/client/chain/`. Upstream's `foundation/client/ClientEvents` is deleted; the port's own
`foundation/client/BnbClientEvents` registers the lot from the client entrypoint. The event mapping:
`ClientTickEvent.Post` becomes `ClientTickEvents.END_CLIENT_TICK`;
`InputEvent.InteractionKeyMappingTriggered` becomes `UseBlockCallback` plus `UseItemCallback`, which between
them cover a right click with and without a targeted block and cancel with `InteractionResult.FAIL`;
`RenderHighlightEvent.Block` and `RenderLevelStageEvent` collapse into one
`LevelRenderEvents.BEFORE_BLOCK_OUTLINE` listener, which fires every frame whether or not a block is targeted
and both submits the chain's outline and suppresses the vanilla highlight.

Three upstream pieces did not come with it. `CogwheelChainRidingHelper.shouldPreventMovement` has no caller
upstream either and is dropped. `SableCompanion.distanceSquaredWithSubLevels` has no stand-in, so the
selection raycast compares against a plain `distanceToSqr` — the same answer whenever Sable is absent, which
on this platform is always. `ClientEvents.registerPresetEditors` belongs to the world preset and stays
deferred with it.

### Superseded in P3 — the nixie text pipeline

* `mixin/FontAccessMixin` and `mixin_accessor/FontAccess` are replaced by
  `mixin/client/nixie/FontMixin`, an `@Invoker` on 26.2's `Font.getGlyphSource`; `FontSet` is gone.
* `mixin/BakedGlyphMixin` and `mixin_accessor/ReverseRenderableBakedGlyph` are **dropped**: on 26.2 a
  `BakedGlyph` makes a `TextRenderable` for a placed glyph rather than drawing itself, so the reverse
  face is a second submission under a mirrored pose and needs no mixin at all.

---

## DEFERRED — needs a redesign

* `content/kinetics/cogwheel_chain/migration/ChainMigrationEvents` — **dropped in P2, deliberately.**
  Upstream registered block and block-entity id aliases through NeoForge's `RegisterEvent`; 26.2's
  registries have no `addAlias` (verified by `javap` on `Registry`, `MappedRegistry` and
  `WritableRegistry`), so the only replacement is a DataFixerUpper rename. The ids it migrates
  (`bits_n_bobs:small_cogwheel_chain` and friends) were only ever written by Bits 'n' Bobs 2.0-2.2 on
  NeoForge 1.21.1. A world written there cannot be opened on this platform at all, so the migration
  path is unreachable, while a datafixer is the single riskiest thing this workspace has shipped
  (see PROJECT.md § Lessons on fix-name aliasing and `DSL.optionalFields`). `ChainIdMigrations` is
  kept as the record of the rename table; nothing calls it. Revisit only if the owner wants
  cross-loader world migration, in which case the shape to copy is Steam 'n' Rails'.
* `mixin/ContraptionColliderMixin` — wrapped `BlockEntry.has(BlockState)` inside
  `ContraptionCollider.isCollidingWithWorld` so the chain pulley magnet took part in the check.
  Create Fly has no Registrate and no such call; the equivalent Fly-side check has not been
  identified. Until then the chain pulley magnet is treated as an ordinary block by contraption
  collision.
* `foundation/generation/**`, `mixin/presets/**`, `registry/worldgen/BnbWorldPresets`,
  `foundation/client/ShipyardHelper` and the `ponderous_planes` world-preset data — the flat scene-authoring
  world preset, together with the preset-editor registration that `ClientEvents` used to carry. Its four preset
  mixins are index-brittle (`WorldDimensionsMixin` targeted `lambda$specialWorldProperty$2`; 26.2 has
  `$0`) and it exists to author ponder scenes, which are themselves deferred.

---

## DROPPED — with a consequence

* `compat/computercraft/**`, `mixin/ComputerBehaviourMixin`,
  `network/packets/to_client/ApplyHeadlampQueuedOperationsPacket` — CC:Tweaked has no Fabric 26.2
  build here, and Create Fly's `ComputerBehaviour` has no `getPeripheralFor`. **The headlamp has no
  computer peripheral**, and the queued-mask-change packet it needed is out of the packet set.
* `CreateBitsnBobsData`, `content/trinkets/chair/ChairBlockStateGen`,
  `content/decoration/weathered_girder/WeatheredGirderBlockStateGenerator`,
  `foundation/client/block_state_gen/BnbBlockStateGen`, `registry/datagen/BnbLangEntries`,
  `registry/datagen/BnbDataConditions` — NeoForge datagen. Ports commit generated resources instead
  of running datagen, and `src/generated/resources` is committed, so nothing is lost; the generators
  are kept only so a future regeneration has a starting point.
* `foundation/SuppressedCogwheelTooltips`, `foundation/config/ReleaseLockTooltipHandler` — both hang
  off NeoForge's `ItemTooltipEvent`, which is client-side. The suppressed-cogwheel and release-lock
  tooltips do not appear.
* `mixin/CreateClientMixin`, `mixin/SubMenuConfigScreenMixin`, `mixin/ConfigChangeAccessor` — their
  target classes do not exist in Create Fly (`CreateClient` is absent entirely; catnip ships
  `ConfigBase` but no config UI). There is no in-game config screen entry for Bits 'n' Bobs.

## Resources set aside

`scratchpad/bnb2/deferred-resources/` holds the two world-preset JSONs
(`data/bits_n_bobs/worldgen/world_preset/ponderous_planes.json` and the
`data/minecraft/tags/worldgen/world_preset/normal.json` that pointed at it). They are out of the jar
because the preset's generator type is no longer registered, and an unbound world-preset value fails
registry loading outright.

`data/sable/tags/block/**` (four tags) were deleted: Sable Companion has no Fabric build, the stand-ins
under `com/kipti/bnb/compat/sable` always answer "not in a sub-level", and the tags were inert.

---

## Resolved in P4

### `registry/azimuth/BnbCreateBlockEdits` — deleted, both halves covered

Azimuth's `RegisterCreateBlockEditsEvent` reached into Create's Registrate builders before Create
registered its blocks, and Create Fly has neither Registrate nor a pre-construct window. The event did
two things and both now have a Fly-side route, so the file and the two `BlockItem` subclasses it
installed (`content/decoration/dyeable/simple/SimpleDyeableBlockItem`,
`content/decoration/dyeable/pipes/DyeablePipeBlockItem`) are deleted rather than deferred.

* **The belt's emissive rendering** is `mixin/client/glowing_belts/BlockStateBaseMixin`, at the HEAD of
  `BlockBehaviour$BlockStateBase.emissiveRendering()`. That method is a `Predicate<BlockState>` baked in
  at block construction, so it cannot be set afterwards — but it is read in exactly one place in the
  whole client (`BlockModelResolver`, verified by a constant-pool sweep), so overriding the answer is
  the same thing. The `GLOWING` property itself was never part of this: `mixin/glowing_belts/BeltBlockMixin`
  adds it at the TAIL of `BeltBlock.createBlockStateDefinition`.
* **Placing a dyed pipe, pump, smart pipe, valve or steam engine** is
  `mixin/dyeable/create_blocks/BlockItemMixin` on vanilla `BlockItem`, guarded by
  `DyeableCreateBlockItems.behaviourFor(getBlock())` — an identity lookup over exactly the five blocks
  upstream listed, so every other placement in the game pays one map get. It reproduces the two deleted
  item classes: save the pending colour at the HEAD of `place`, apply it client-side and consume it at
  the RETURN, and set it on the block entity at the RETURN of `updateCustomBlockEntityTag`. The colour
  comes from the player's **offhand**, which is what upstream's item classes did too — neither upstream
  nor this port stores a colour on the item stack, so a dyed block broken and re-placed comes back
  undyed on both.

### `mixin/chair/ContraptionMixin` — replaced by a datapack tag

It widened `Contraption.moveBlock`'s seat check to this mod's chairs. `AllBlockTags.SEATS` is read in
exactly one class in Create Fly (`Contraption`, verified by a constant-pool sweep) and it is a plain
`TagKey`, so `data/create/tags/block/seats.json` pointing at `#bits_n_bobs:chairs` does the same job
with no mixin.

### `mixin/DynamicComponentMixin`, `mixin_accessor/DynamicComponentMigrator` — superseded

The port writes its own `foundation/display/BnbDynamicComponent` rather than mixing into Create's
`DynamicComponent`. Its `displayCustomText` always deserializes (which subsumes the mixin's fallback
parse) and it has its own `setValueToLiteral` and `toJson`.

---

## DROPPED in P4 — with a consequence

* `GiganticCogwheelSatelliteBlock.RenderProperties` (`addDestroyEffects`, `addHitEffects`,
  `getExtraPositions`) and `addLandingEffects` — NeoForge `IClientBlockExtensions` and
  `MultiPosDestructionHandler`, which Fabric has no counterpart for. **Breaking or landing on a gigantic
  cogwheel cracks particles at the block you hit rather than at the wheel's centre**, and the multi-block
  break particles are the vanilla per-block ones. Cosmetic only; the block's own
  `getPistonPushReaction` is restored as `.pushReaction(PushReaction.BLOCK)` on its properties, and
  `isFlammable -> false` is already the vanilla default for a block outside the fire registry.
* `LightBlock.canConnectRedstone -> true` — NeoForge `IBlockExtension`. Vanilla decides a dust
  connection from `BlockState.isSignalSource()`, so **redstone dust no longer grows a nub pointing at a
  light**. The light is still powered by adjacent dust; only the dust's own shape differs.
* Create Fly ships **no mod-config screen at all** — `com/zurrtum/create/catnip/config/ui/` holds only
  `ConfigAnnotations`, there is no `ConfigScreen`, `ConfigScreenList`, `ConfigHelper` or
  `SubMenuConfigScreen` anywhere in the jar (the one match for "ConfigScreen" is the goggles' own
  `GoggleConfigScreen`), and Fly declares no Mod Menu entrypoint. So there is nowhere for a feature-group
  entry to go, not merely no Bits 'n' Bobs entry. `foundation/gui/screen/BnbFeatureGroupEntry` and
  `foundation/BnbConfigBridge` stay here against a future Fly config UI; the config is edited in
  `config/bits_n_bobs-common.toml` and `-server.toml`.

## Restored in P4

* The ponder plugin, its scene index, three instructions and five scene files are live again, plus
  `mixin/client/ponder/AllCreatePonderScenesMixin` (the chain pulley reuses Create's own
  `rope_pulley/*` storyboards, so it has to register inside Create's namespace). Azimuth's
  `ExpandingOutlineInstruction`/`ExpandingLineOutline` became the port's own
  `foundation/client/outline/ExpandingLineOutline` over Fly's `LineOutline.submitInner`. **Azimuth's
  `NewPonderTooltipManager` is dropped**: the gold "NEW" badge on an item whose ponder scenes have not
  been watched does not appear, and no `ponders_watched_*.json` is written.
  `scenes/CogwheelChainScenes.cogwheelChainPathingBehaviour` and
  `assets/bits_n_bobs/ponder/chain_cog/pathing_behaviour.nbt` are carried unregistered, exactly as
  upstream leaves them — there are no lang keys for that scene, so registering it would show raw keys.
* `foundation/behaviour/drag/DragInteractionScreen` and `DragInteractionClientHandler`. The screen is on
  26.2's `extractRenderState`/`extractBackground` and re-centres the cursor through
  `InputConstants.grabOrReleaseMouse` (`Window.handle()` is not public) rather than raw GLFW; the handler
  is a `UseBlockCallback` registered from the client entrypoint. Without it the throttle lever's
  `DragInteractionBehaviour` and its C2S packet were live but unreachable.
* `HeadlampBlock.registerBreakHandler` on `PlayerBlockBreakEvents.BEFORE`, replacing NeoForge's
  `onDestroyedByPlayer`. Without it, breaking a headlamp block removed every lamp on it at once instead
  of the nearest one.

## Recorded in the P5 audit pass

* **Cogwheel material on Create Connected crank wheels and Create Hypertube entrances is dropped.**
  Upstream's `CogwheelMaterialRenderer.getMiscVariant` mapped four ids onto three extra `Variant`
  constants backed by `CCPartialModels.CRANK_WHEEL_BASE`/`LARGE_CRANK_WHEEL_BASE` and
  `ModPartialModels.COGWHEEL_HOLE`, which would mean compiling against Create Connected. The four
  `"required": false` ids are removed from `cogwheel_material_candidates.json` so those blocks no longer
  accept and swallow a material they cannot display.
* **`CreateRegistrate.casingConnectivity` has no counterpart.** Create Fly ships no CasingConnectivity
  API at all, so Create's own casing blocks do not connect their texture to the encased flanged
  cogwheels and encased piston poles. The cogwheel-side half is restored: the two encased flanged
  cogwheel lists now use Create's own `AllCTBehaviours.COG_SIDE_*`/`COG_*` behaviours instead of the
  plain `EncasedCTBehaviour`.
* **`foundation/caching/LevelSafeStorage` and `LevelSimpleCache` have no instances.** Upstream's only
  user was the ComputerCraft `HeadlampQueuedOperationHandler`, dropped with CC:Tweaked. They are staged
  here for a future revival; if revived, note that they key by `ResourceKey<Level>` (so a single-player
  client and server level share one entry) and that the port clears them only on
  `ServerLevelEvents.UNLOAD`, where upstream's `LevelEvent.Unload` fired on both sides.
* **`mixin/compat/**` is still set aside, so `BnbMixinPlugin` currently gates nothing** — it is kept as
  the placeholder for that tree and is why `bits_n_bobs.mixins.json` still declares a plugin. When the
  compat mixins return, four of the five are client visual mixins, so
  `bits_n_bobs.client.mixins.json` will need the plugin declaration too.
* **`RenderedBehaviourExtension` is deleted.** Azimuth's `CachedRenderBBBlockEntityMixin` was not
  ported, and the chain draws from `LevelExtractionEvents`/`LevelRenderEvents`, which never consult a
  block entity's render bounding box. If `CogwheelChainBehaviourVisual` is ever restored from here, the
  interface and that mixin have to come back together.
* **The gigantic cogwheel and the wooden strut are release-locked upstream** and therefore unobtainable
  in a shipped jar: `BnbCommonConfig.getFeatureFlagState` returns `BnbFeatureFlag.isDevEnvironment()`
  for a release-locked flag and creates no config entry for it, so they are hidden from the creative
  tabs and `/give` is refused. `/setblock` still places the gigantic cogwheel. This is upstream's gate,
  not a port decision.
