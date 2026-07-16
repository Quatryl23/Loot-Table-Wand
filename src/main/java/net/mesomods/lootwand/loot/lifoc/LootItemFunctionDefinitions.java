package net.mesomods.lootwand.loot.lifoc;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import net.mesomods.lootwand.loot.RenderedLootPool;
import net.mesomods.lootwand.loot.lifoc.parameters.*;
import net.mesomods.lootwand.loot.numbers.NumberProvider;
import net.mesomods.lootwand.mixin.loot.function.*;
import net.mesomods.lootwand.mixin.numbers.IntRangeAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProvider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LootItemFunctionDefinitions {
    private static final Map<ResourceLocation, FunctionDefinition<?>> definitions = new HashMap<>();

    public static EnumParameter<LootContext.EntityTarget> getLootContextEntity(String description) {
        return new EnumParameter<>(LootContext.EntityTarget.class, null, description, Map.of(
                LootContext.EntityTarget.THIS, "gui.loot_table_wand.loot_context.this",
                LootContext.EntityTarget.DIRECT_KILLER, "gui.loot_table_wand.loot_context.direct_attacker",
                LootContext.EntityTarget.KILLER, "gui.loot_table_wand.loot_context.attacker",
                LootContext.EntityTarget.KILLER_PLAYER, "gui.loot_table_wand.loot_context.attacking_player"
        ));
    }

    public static final FunctionDefinition<ApplyBonusCountAccessor> APPLY_BONUS = new FunctionDefinition<>(ApplyBonusCountAccessor.class)
            .registryDescriptionParameter("gui.loot_table_wand.function.apply_bonus", BuiltInRegistries.ENCHANTMENT, ApplyBonusCountAccessor::getEnchantment, ApplyBonusCountAccessor::setEnchantment)
            .inlineMultiModeParameter(ApplyBonusCount.Formula.class, ApplyBonusCountAccessor::getFormula, ApplyBonusCountAccessor::setFormula, (formula) -> {
                if (formula instanceof UniformBonusCountAccessor uniform) {
                    return Pair.of("gui.loot_table_wand.function.apply_bonus.uniform", List.of(new LIFOCDefinition.ParameterDefinition<>(new IntParameter(null, (String) null), (function) -> uniform.getBonusMultiplier(), (function, value) -> uniform.setBonusMultiplier(value))));
                } else if (formula instanceof BinomialWithBonusCountAccessor binomial) {
                    return Pair.of("gui.loot_table_wand.function.apply_bonus.binomial", List.of(
                            new LIFOCDefinition.ParameterDefinition<>(new FloatParameter(null, null), (function) -> binomial.getProbability() * 100, (function, value) -> binomial.setProbability(value / 100)),
                            new LIFOCDefinition.ParameterDefinition<>(new IntParameter(null, (String) null), (function) -> binomial.getExtraRounds(), (function, value) -> binomial.setExtraRounds(value))));
                } else {
                    return Pair.of("gui.loot_table_wand.function.apply_bonus.ore_drops", List.of());
                }
            });

    public static final FunctionDefinition<CopyNameFunctionAccessor> COPY_NAME = new FunctionDefinition<>(CopyNameFunctionAccessor.class).descriptionParameter(
            new EnumParameter<>(CopyNameFunction.NameSource.class, null, "gui.loot_table_wand.function.copy_name", Map.of(
                    CopyNameFunction.NameSource.THIS, "gui.loot_table_wand.loot_context.this",
                    CopyNameFunction.NameSource.BLOCK_ENTITY, "gui.loot_table_wand.loot_context.block_entity",
                    CopyNameFunction.NameSource.KILLER, "gui.loot_table_wand.loot_context.attacker",
                    CopyNameFunction.NameSource.KILLER_PLAYER, "gui.loot_table_wand.loot_context.attacking_player")),
            CopyNameFunctionAccessor::getSource,
            CopyNameFunctionAccessor::setSource);

    public static final FunctionDefinition<CopyNbtFunctionAccessor> COPY_NBT = new FunctionDefinition<>(CopyNbtFunctionAccessor.class)
            .inlineMultiModeDescriptionParameter(NbtProvider.class,
                    CopyNbtFunctionAccessor::getSource, CopyNbtFunctionAccessor::setSource, source -> {
                        if (source instanceof ContextNbtProviderAccessor) {
                            return Pair.of("gui.loot_table_wand.function.copy_nbt.context", List.of(new LIFOCDefinition.ParameterDefinition<>(new EnumParameter<>(ContextNbtProviderTarget.class, null, null, ContextNbtProviderTarget::getDescription), (provider) -> ContextNbtProviderTarget.getById(((ContextNbtProviderAccessor)provider).getGetter().getId()), (provider, value) -> ((ContextNbtProviderAccessor)provider).setGetter(((ContextNbtProviderAccessor)ContextNbtProviderAccessor.callCreateFromContext(value.id)).getGetter()))));
                        } else if (source instanceof StorageNbtProviderAccessor) {
                            return Pair.of("gui.loot_table_wand.function.copy_nbt.storage", List.of(new LIFOCDefinition.ParameterDefinition<>(new StringParameter(null, null), (provider) -> ((StorageNbtProviderAccessor)provider).getId().toString(), (provider, string) -> ((StorageNbtProviderAccessor)provider).setId(new ResourceLocation(string)))));
                        } else {
                            return Pair.of(null, List.of());
                        }
                    })
            .listParameter(new LIFOCDefinition.MultiParameterDefinition<>(List.of(
                    new LIFOCDefinition.ParameterDefinition<>(new StringParameter(null, "gui.loot_table_wand.function.copy_nbt.storage.ops.source"), CopyOperationAccessor::getSourcePathText, (operation, string) -> {operation.setSourcePathText(string); operation.setSourcePath(CopyNbtFunctionAccessor.callCompileNbtPath(string));}),
                    new LIFOCDefinition.ParameterDefinition<>(new StringParameter(null, "gui.loot_table_wand.function.copy_nbt.storage.ops.target"), CopyOperationAccessor::getTargetPathText, (operation, string) -> {operation.setTargetPathText(string); operation.setTargetPath(CopyNbtFunctionAccessor.callCompileNbtPath(string));}),
                    new LIFOCDefinition.ParameterDefinition<>(new EnumParameter<>(CopyNbtFunction.MergeStrategy.class, null, EnumParameter.createNullDescription("gui.loot_table_wand.function.copy_nbt.storage.ops.op"), Map.of(
                            CopyNbtFunction.MergeStrategy.REPLACE, "gui.loot_table_wand.function.copy_nbt.storage.ops.op.replace",
                            CopyNbtFunction.MergeStrategy.APPEND, "gui.loot_table_wand.function.copy_nbt.storage.ops.op.append",
                            CopyNbtFunction.MergeStrategy.MERGE, "gui.loot_table_wand.function.copy_nbt.storage.ops.op.merge"
                    )), CopyOperationAccessor::getOp, CopyOperationAccessor::setOp)
            )), function -> function.getOperations().stream().map(op -> (CopyOperationAccessor) op).toList(), (function, list) -> function.setOperations(list.stream().map(op -> (CopyNbtFunction.CopyOperation) op).toList()));

    public static final FunctionDefinition<CopyBlockStateAccessor> COPY_STATE = new FunctionDefinition<>(CopyBlockStateAccessor.class).registryDescriptionParameter(
                    "gui.loot_table_wand.function.copy_state", BuiltInRegistries.BLOCK, CopyBlockStateAccessor::getBlock, CopyBlockStateAccessor::setBlock, Block::getName)
            .listParameter(new LIFOCDefinition.ParameterDefinition<>(new PropertyParameter(null, null), ListParameter.Holder::get, ListParameter.Holder::set),
                    (function) -> function.getProperties().stream().map((Function<Property<?>, ListParameter.Holder<Property<?>>>) ListParameter.Holder::new).toList(), (function, holderList) -> function.setProperties(holderList.stream().map(ListParameter.Holder::get).collect(Collectors.toSet())));

    public static final FunctionDefinition<EnchantRandomlyFunctionAccessor> ENCHANT_RANDOMLY = new FunctionDefinition<>(EnchantRandomlyFunctionAccessor.class)
            .listDescriptionParameter("gui.loot_table_wand.function.enchant_randomly.multiple", "gui.loot_table_wand.function.enchant_randomly.single", "gui.loot_table_wand.function.enchant_randomly.empty", new LIFOCDefinition.ParameterDefinition<>(new VanillaRegistryParameter<>(null, null, BuiltInRegistries.ENCHANTMENT), ListParameter.Holder::get, ListParameter.Holder::set), function -> function.getEnchantments().stream().map(ListParameter.Holder::new).toList(), (function, holderList) -> function.setEnchantments(holderList.stream().map(ListParameter.Holder::get).toList()));

    public static final FunctionDefinition<EnchantWithLevelsFunctionAccessor> ENCHANT_WITH_LEVELS = new FunctionDefinition<>(EnchantWithLevelsFunctionAccessor.class).descriptionParameter(new NumberProviderParameter(null, "gui.loot_table_wand.function.enchant_with_levels", false), (function) -> NumberProvider.fromVanilla(function.getLevels()), (function, value) -> function.setLevels(value.toVanilla()));

    public static final FunctionDefinition<ExplorationMapFunctionAccessor> EXPLORATION_MAP = new FunctionDefinition<>(ExplorationMapFunctionAccessor.class, Component.translatable("gui.loot_table_wand.function.exploration_map"))
            .parameter(new TagParameter<>(ExplorationMapFunction.DEFAULT_DESTINATION, "gui.loot_table_wand.function.exploration_map.destination"), ExplorationMapFunctionAccessor::getDestination, ExplorationMapFunctionAccessor::setDestination)
            .parameter(new EnumParameter<>(MapDecoration.Type.class, MapDecoration.Type.MANSION, "gui.loot_table_wand.function.exploration_map.icon", (type) -> Component.literal(type.name().toLowerCase())), ExplorationMapFunctionAccessor::getMapDecoration, ExplorationMapFunctionAccessor::setMapDecoration)
            .parameter(new ByteParameter((byte) 2, "gui.loot_table_wand.function.exploration_map.zoom"), ExplorationMapFunctionAccessor::getZoom, ExplorationMapFunctionAccessor::setZoom)
            .parameter(new IntParameter(50, "gui.loot_table_wand.function.exploration_map.search_radius"), ExplorationMapFunctionAccessor::getSearchRadius, ExplorationMapFunctionAccessor::setSearchRadius)
            .parameter(new BooleanParameter(true, "gui.loot_table_wand.function.exploration_map.skip_existing_chunks"), ExplorationMapFunctionAccessor::skipKnownStructures, ExplorationMapFunctionAccessor::setSkipKnownStructures);

    public static final FunctionDefinition<ApplyExplosionDecay> EXPLOSION_DECAY = new FunctionDefinition<>(ApplyExplosionDecay.class, Component.translatable("gui.loot_table_wand.function.explosion_decay"));

    public static final FunctionDefinition<FillPlayerHeadAccessor> FILL_PLAYER_HEAD = new FunctionDefinition<>(FillPlayerHeadAccessor.class).descriptionParameter(getLootContextEntity("gui.loot_table_wand.function.fill_player_head"), FillPlayerHeadAccessor::getEntityTarget, FillPlayerHeadAccessor::setEntityTarget);

    public static final FunctionDefinition<SmeltItemFunction> FURNACE_SMELT = new FunctionDefinition<>(SmeltItemFunction.class, Component.translatable("gui.loot_table_wand.function.furnace_smelt")).hiddenByDefault();

    public static final FunctionDefinition<LimitCountAccessor> LIMIT_COUNT = new FunctionDefinition<>(LimitCountAccessor.class)
            .descriptionParameter(new IntRangeParameter(null, null, "gui.loot_table_wand.function.limit_count.any", "gui.loot_table_wand.function.limit_count.min", "gui.loot_table_wand.function.limit_count.max", "gui.loot_table_wand.function.limit_count.range", "gui.loot_table_wand.function.limit_count.exact", false), (function) -> (IntRangeAccessor) function.getLimiter(), (function, range) -> function.setLimiter((IntRange) range));

    public static final FunctionDefinition<LootingEnchantFunctionAccessor> LOOTING_ENCHANT = new FunctionDefinition<>(LootingEnchantFunctionAccessor.class)
            .descriptionParameter(new NumberProviderParameter(null, "gui.loot_table_wand.function.looting_enchant", true), (function) -> NumberProvider.fromVanilla(function.getValue()), (function, value) -> function.setValue(value.toVanilla()))
            .parameter(new IntParameter(0, new Parameter.Description<>(null, (limit) -> limit == 0 ? Component.translatable("gui.loot_table_wand.function.looting_enchant.limit.0") : Component.literal(Component.translatable("gui.loot_table_wand.function.looting_enchant.limit").getString().replace("##", limit.toString())))), LootingEnchantFunctionAccessor::getLimit, LootingEnchantFunctionAccessor::setLimit);

    public static final FunctionDefinition<FunctionReferenceAccessor> REFERENCE = new FunctionDefinition<>(FunctionReferenceAccessor.class)
            .descriptionParameter(new StringParameter(null, "gui.loot_table_wand.function.reference"), function -> function.getName().toString(), (function, string) -> function.setName(new ResourceLocation(string)));

    public static final FunctionDefinition<SetAttributesFunctionAccessor> SET_ATTRIBUTES = new FunctionDefinition<>(SetAttributesFunctionAccessor.class, Component.translatable("gui.loot_table_wand.function.set_attributes"))
            .listParameter(new LIFOCDefinition.MultiParameterDefinition<>(List.of(
                            new LIFOCDefinition.ParameterDefinition<>(new VanillaRegistryParameter<>(null, "gui.loot_table_wand.function.set_attributes.attribute", BuiltInRegistries.ATTRIBUTE), holder -> holder.get().getAttribute(), (holder, attribute) -> holder.get().setAttribute(attribute)),
                            new LIFOCDefinition.ParameterDefinition<>(new StringParameter(null, "gui.loot_table_wand.function.set_attributes.name"), holder -> holder.get().getName(), (holder, name) -> holder.get().setName(name)),
                            new LIFOCDefinition.ParameterDefinition<>(new EnumParameter<>(AttributeModifier.Operation.class, null, "gui.loot_table_wand.function.set_attributes.operation", Map.of(
                                    AttributeModifier.Operation.ADDITION, "gui.loot_table_wand.function.set_attributes.operation.addition",
                                    AttributeModifier.Operation.MULTIPLY_BASE, "gui.loot_table_wand.function.set_attributes.operation.multiply_base",
                                    AttributeModifier.Operation.MULTIPLY_TOTAL, "gui.loot_table_wand.function.set_attributes.operation.multiply_total"
                            )), holder -> holder.get().getOperation(), (holder, operation) -> holder.get().setOperation(operation)),
                            new LIFOCDefinition.ParameterDefinition<>(new NumberProviderParameter(null, "gui.loot_table_wand.function.set_attributes.amount", true), holder -> NumberProvider.fromVanilla(holder.get().getAmount()), (holder, amount) -> holder.get().setAmount(amount.toVanilla())),
                            new LIFOCDefinition.ParameterDefinition<>(new UUIDParameter("gui.loot_table_wand.function.set_attributes.id"), holder -> holder.get().getId(), (holder, id) -> holder.get().setId(id)),
                            new LIFOCDefinition.ListParameterDefinition<>(new ListParameter<>(Component.translatable("gui.loot_table_wand.function.set_attributes.slots"), new LIFOCDefinition.ParameterDefinition<>(new EnumParameter<>(EquipmentSlot.class, null, null, (slot) -> Component.literal(slot.getName())), ListParameter.Holder::get, (holder, slot) -> new ListParameter.Holder<>(slot))),
                                    holder -> Arrays.stream(holder.get().getSlots()).map(ListParameter.Holder::new).toList(), (holder, list) -> holder.get().setSlots(((EquipmentSlot[]) list.stream().map(slotHolder -> (slotHolder.get())).toArray())), null))),
                    function -> function.getModifiers().stream().map((modifier) -> (ListParameter.Holder<SetAttributesFunctionModifierAccessor>) new ListParameter.Holder(modifier)).toList(), (function, holderList) -> function.setModifiers(holderList.stream().map((holder) -> (SetAttributesFunction.Modifier) holder.get()).toList()));

    public static final FunctionDefinition<SetBannerPatternFunctionAccessor> SET_BANNER_PATTERN = new FunctionDefinition<>(SetBannerPatternFunctionAccessor.class, Component.translatable("gui.loot_table_wand.function.set_banner_pattern")).listParameter(
            new LIFOCDefinition.MultiParameterDefinition<>(List.of(
                    new LIFOCDefinition.ParameterDefinition<>(new VanillaRegistryParameter<>(null, "gui.loot_table_wand.function.set_banner_pattern.pattern", BuiltInRegistries.BANNER_PATTERN), holder -> holder.get().getFirst().value(), (holder, value) -> holder.set(new Pair<>(Holder.direct(value), holder.get().getSecond()))),
                    new LIFOCDefinition.ParameterDefinition<>(new EnumParameter<>(DyeColor.class, null, "gui.loot_table_wand.function.set_banner_pattern.color", (color) -> Component.literal(color.name().toLowerCase())), holder -> holder.get().getSecond(), (holder, value) -> holder.set(new Pair<>(holder.get().getFirst(), value))))),
            function -> function.getPatterns().stream().map(ListParameter.Holder::new).toList(), (function, value) -> function.setPatterns(value.stream().map(ListParameter.Holder::get).toList()))
            .hiddenByDefault();

    public static final FunctionDefinition<SetContainerContentsAccessor> SET_CONTENTS = new FunctionDefinition<>(SetContainerContentsAccessor.class)
            .registryDescriptionParameter("gui.loot_table_wand.function.set_contents", BuiltInRegistries.BLOCK_ENTITY_TYPE, SetContainerContentsAccessor::getType, SetContainerContentsAccessor::setType)
            .parameter(new ContentsParameter(), (function) -> function.getEntries().stream().map(entry -> RenderedLootPool.Entry.fromVanilla(entry, true)).toList(), (function, value) -> function.setEntries(value.stream().map(RenderedLootPool.Entry::toVanilla).toList()));

    public static final FunctionDefinition<SetItemCountFunctionAccessor> SET_COUNT = new FunctionDefinition<>(SetItemCountFunctionAccessor.class)
            .descriptionParameter(new NumberProviderParameter(null, "gui.loot_table_wand.function.set_count", false), function -> NumberProvider.fromVanilla(function.getValue()), (function, numberProvider) -> function.setValue(numberProvider.toVanilla())).
            parameter(new BooleanParameter(false, "gui.loot_table_wand.function.set_count.add"), SetItemCountFunctionAccessor::isAdd, SetItemCountFunctionAccessor::setAdd)
            .previewEffect(new LIFOCDefinition.SetCountPreviewEffectDefinition());

    public static final FunctionDefinition<SetItemDamageFunctionAccessor> SET_DAMAGE = new FunctionDefinition<>(SetItemDamageFunctionAccessor.class)
            .descriptionParameter(new NumberProviderParameter(null, "gui.loot_table_wand.function.set_damage", true), (function) -> NumberProvider.fromVanillaTimes100(function.getDamage()), (function, value) -> function.setDamage(value.toVanilla()))
            .parameter(new BooleanParameter(false, "gui.loot_table_wand.function.set_damage.add"), SetItemDamageFunctionAccessor::isAdd, SetItemDamageFunctionAccessor::setAdd);

    public static final FunctionDefinition<SetContainerLootTableAccessor> SET_LOOT_TABLE = new FunctionDefinition<>(SetContainerLootTableAccessor.class)
            .descriptionParameter(new StringParameter(null, "gui.loot_table_wand.function.set_loot_table"), function -> function.getName().toString(), (function, rl) -> function.setName(new ResourceLocation(rl)))
            .parameter(new VanillaRegistryParameter<>(null, "gui.loot_table_wand.function.set_loot_table.type", BuiltInRegistries.BLOCK_ENTITY_TYPE), SetContainerLootTableAccessor::getType, SetContainerLootTableAccessor::setType)
            .parameter(new LongParameter((long) 0, "gui.loot_table_wand.function.set_loot_table.seed"), SetContainerLootTableAccessor::getSeed, SetContainerLootTableAccessor::setSeed);

    public static final FunctionDefinition<SetEnchantmentsFunctionAccessor> SET_ENCHANTMENTS = new FunctionDefinition<>(SetEnchantmentsFunctionAccessor.class, Component.translatable("gui.loot_table_wand.function.set_enchantments"))
            .listParameter(new LIFOCDefinition.InlineMultiParameterDefinition<>(List.of(
                    new LIFOCDefinition.ParameterDefinition<>(new VanillaRegistryParameter<>(null, null, BuiltInRegistries.ENCHANTMENT), holder -> holder.get().getFirst(), (holder, enchantment) -> holder.set(new Pair<>(enchantment, holder.get().getSecond()))),
                    new LIFOCDefinition.ParameterDefinition<>(new NumberProviderParameter(null, null, false, true), holder -> NumberProvider.fromVanilla(holder.get().getSecond()), (holder, numberProvider) -> holder.set(new Pair<>(holder.get().getFirst(), numberProvider.toVanilla()))
                    )), "gui.loot_table_wand.function.set_enchantments.enchantment"), function -> function.getEnchantments().entrySet().stream().map(entry -> new ListParameter.Holder<>(new Pair<>(entry.getKey(), entry.getValue()))).toList(), (function, holderList) -> function.setEnchantments(holderList.stream().map(ListParameter.Holder::get).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))))
            .parameter(new BooleanParameter(false, "gui.loot_table_wand.function.set_enchantments.add"), SetEnchantmentsFunctionAccessor::isAdd, SetEnchantmentsFunctionAccessor::setAdd);

    public static final FunctionDefinition<SetInstrumentFunctionAccessor> SET_INSTRUMENT = new FunctionDefinition<>(SetInstrumentFunctionAccessor.class)
            .descriptionParameter(new StringParameter(null, "gui.loot_table_wand.function.set_instrument"), (function) -> function.getOptions().location().toString(), (function, options) -> function.setOptions(TagKey.create(ResourceKey.createRegistryKey(new ResourceLocation("minecraft:instruments")), new ResourceLocation(options))));

    public static final FunctionDefinition<SetLoreFunctionAccessor> SET_LORE = new FunctionDefinition<>(SetLoreFunctionAccessor.class, Component.translatable("gui.loot_table_wand.function.set_lore"))
            .listParameter(new LIFOCDefinition.ParameterDefinition<>(new StringParameter(null, null), holder -> holder.get().getString(), (holder, string) -> holder.set(Component.literal(string))),
                    function -> function.getLore().stream().map(ListParameter.Holder::new).toList(), (function, lore) -> function.setLore(lore.stream().map(ListParameter.Holder::get).toList()))
            .parameter(getLootContextEntity("gui.loot_table_wand.function.set_lore.resolution_context"), SetLoreFunctionAccessor::getResolutionContext, SetLoreFunctionAccessor::setResolutionContext)
            .parameter(new BooleanParameter(false, "gui.loot_table_wand.function.set_lore.replace"), SetLoreFunctionAccessor::isReplace, SetLoreFunctionAccessor::setReplace);

    public static final FunctionDefinition<SetNameFunctionAccessor> SET_NAME = new FunctionDefinition<>(SetNameFunctionAccessor.class)
            .descriptionParameter(new StringParameter(null, "gui.loot_table_wand.function.set_name"), function -> function.getName().getString(), (function, string) -> function.setName(Component.literal(string)))
            .parameter(getLootContextEntity("gui.loot_table_wand.function.set_name.resolution_context"), SetNameFunctionAccessor::getResolutionContext, SetNameFunctionAccessor::setResolutionContext);

    public static final FunctionDefinition<SetNbtFunctionAccessor> SET_NBT = new FunctionDefinition<>(SetNbtFunctionAccessor.class)
            .descriptionParameter(new StringParameter(null, "gui.loot_table_wand.function.set_nbt"), function -> function.getTag().toString(), (function, string) -> {
                try {
                    function.setTag(new TagParser(new StringReader(string)).readStruct());
                } catch (CommandSyntaxException e) {
                    throw new RuntimeException(e);
                }
            });

    public static final FunctionDefinition<SetPotionFunctionAccessor> SET_POTION = new FunctionDefinition<>(SetPotionFunctionAccessor.class)
            .registryDescriptionParameter("gui.loot_table_wand.function.set_potion", BuiltInRegistries.POTION, SetPotionFunctionAccessor::getPotion, SetPotionFunctionAccessor::setPotion)
            .hiddenByDefault();

    public static final FunctionDefinition<SetStewEffectFunctionAccessor> SET_STEW_EFFECT = new FunctionDefinition<>(SetStewEffectFunctionAccessor.class, Component.translatable("gui.loot_table_wand.function.set_stew_effect"))
            .listParameter(new LIFOCDefinition.InlineMultiParameterDefinition<>(List.of(
                    new LIFOCDefinition.ParameterDefinition<>(new VanillaRegistryParameter<>(null, null, BuiltInRegistries.MOB_EFFECT), holder -> holder.get().getFirst(), (holder, effect) -> holder.set(new Pair<>(effect, holder.get().getSecond()))),
                    new LIFOCDefinition.ParameterDefinition<>(new NumberProviderParameter(null, null, false), holder -> NumberProvider.fromVanilla(holder.get().getSecond()), (holder, numberProvider) -> holder.set(new Pair<>(holder.get().getFirst(), numberProvider.toVanilla()))
                    )), "gui.loot_table_wand.function.set_stew_effect.effect"), function -> function.getEffectDurationMap().entrySet().stream().map(entry -> new ListParameter.Holder<>(new Pair<>(entry.getKey(), entry.getValue()))).toList(), (function, holderList) -> function.setEffectDurationMap(holderList.stream().map(ListParameter.Holder::get).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))));


    static {
        define("apply_bonus", APPLY_BONUS);
        define("copy_name", COPY_NAME);
        define("copy_nbt", COPY_NBT);
        define("copy_state", COPY_STATE);
        define("enchant_randomly", ENCHANT_RANDOMLY);
        define("enchant_with_levels", ENCHANT_WITH_LEVELS);
        define("exploration_map", EXPLORATION_MAP);
        define("explosion_decay", EXPLOSION_DECAY);
        define("fill_player_head", FILL_PLAYER_HEAD);
        define("furnace_smelt", FURNACE_SMELT);
        define("limit_count", LIMIT_COUNT);
        define("looting_enchant", LOOTING_ENCHANT);
        define("reference", REFERENCE);
        define("set_attributes", SET_ATTRIBUTES);
        define("set_banner_pattern", SET_BANNER_PATTERN);
        define("set_contents", SET_CONTENTS);
        define("set_count", SET_COUNT);
        define("set_damage", SET_DAMAGE);
        define("set_enchantments", SET_ENCHANTMENTS);
        define("set_instrument", SET_INSTRUMENT);
        define("set_loot_table", SET_LOOT_TABLE);
        define("set_lore", SET_LORE);
        define("set_name", SET_NAME);
        define("set_nbt", SET_NBT);
        define("set_potion", SET_POTION);
        define("set_stew_effect", SET_STEW_EFFECT);
    }

    public static void define(String path, FunctionDefinition<?> definition) {
        definitions.put(new ResourceLocation("minecraft", path), definition);
    }

    public static void define(String namespace, String path, FunctionDefinition<?> definition) {
        definitions.put(new ResourceLocation(namespace, path), definition);
    }

    public static FunctionDefinition<?> get(ResourceLocation id) {
        return definitions.get(id);
    }

    public enum ContextNbtProviderTarget {
        THIS("gui.loot_table_wand.loot_context.this", getName(LootContext.EntityTarget.THIS)),
        KILLER("gui.loot_table_wand.loot_context.attacker", getName(LootContext.EntityTarget.KILLER)),
        KILLER_PLAYER("gui.loot_table_wand.loot_context.attacker_player", getName(LootContext.EntityTarget.KILLER_PLAYER)),
        BLOCK_ENTITY("gui.loot_table_wand.loot_context.block_entity", ContextNbtProviderAccessor.getBLOCK_ENTITY_ID()),
        DIRECT_KILLER("gui.loot_table_wand.loot_context.direct_attacker", getName(LootContext.EntityTarget.DIRECT_KILLER));

        final String description;
        final String id;

        ContextNbtProviderTarget(String description, String id) {
            this.description = description;
            this.id = id;
        }

        public Component getDescription() {
            return Component.translatable(description);
        }

        public static ContextNbtProviderTarget getById(String id) {
            for(ContextNbtProviderTarget value : values()) {
                if (value.id.equals(id)) {
                    return value;
                }
            }

            throw new IllegalArgumentException("Invalid context NBT provider target " + id);
        }

        private static String getName(LootContext.EntityTarget target) {
            return target.name();
        }
    }
}