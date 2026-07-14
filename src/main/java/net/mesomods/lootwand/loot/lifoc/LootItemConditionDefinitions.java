package net.mesomods.lootwand.loot.lifoc;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.mesomods.lootwand.client.ScreenUtils;
import net.mesomods.lootwand.loot.lifoc.parameters.*;
import net.mesomods.lootwand.loot.numbers.NumberProvider;
import net.mesomods.lootwand.mixin.StatsScreenAccessor;
import net.mesomods.lootwand.mixin.forge.loot.condition.CanToolPerformActionAccessor;
import net.mesomods.lootwand.mixin.forge.loot.condition.LootTableIdConditionAccessor;
import net.mesomods.lootwand.mixin.loot.condition.*;
import net.mesomods.lootwand.mixin.loot.condition.predicate.*;
import net.mesomods.lootwand.mixin.numbers.IntRangeAccessor;
import net.mesomods.lootwand.mixin.numbers.MinMaxBoundsAccessor;
import net.mesomods.lootwand.mixin.numbers.MinMaxBoundsDoublesAccessor;
import net.mesomods.lootwand.mixin.numbers.MinMaxBoundsIntsAccessor;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class LootItemConditionDefinitions {
    private static final Map<ResourceLocation, ConditionDefinition<?>> definitions = new HashMap<>();
    public static final DescriptionMerger<?> PREDICATE_SIMPLIFIER = DescriptionMerger.APPEND_LOWERCASE;
    private static final Supplier<LIFOCDefinition.MultiParameterDefinition<EntityPredicateAccessor>> entityPredicate;

    public static EnumParameter<LootContext.EntityTarget> getLootContextEntity(String description) {
        return new EnumParameter<>(LootContext.EntityTarget.class, null, description, Map.of(
                LootContext.EntityTarget.THIS, "gui.loot_table_wand.loot_context.this",
                LootContext.EntityTarget.DIRECT_KILLER, "gui.loot_table_wand.loot_context.direct_attacker",
                LootContext.EntityTarget.KILLER, "gui.loot_table_wand.loot_context.attacker",
                LootContext.EntityTarget.KILLER_PLAYER, "gui.loot_table_wand.loot_context.attacking_player"
        ));
    }

    public static <T> LIFOCDefinition.ParameterDefinition<T, String> getNbtDefinition(Function<T, NbtPredicate> nbtGetter, String description) {
        return new LIFOCDefinition.ParameterDefinition<>(new StringParameter(null, description), (predicate) -> {
            CompoundTag tag = ((NbtPredicateAccessor) nbtGetter.apply(predicate)).getTag();
            return tag == null ? null : tag.toString();
        }, (predicate, string) -> {
            try {
                ((NbtPredicateAccessor) nbtGetter.apply(predicate)).setTag(string == null ? null : new TagParser(new StringReader(string)).readStruct());
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static <T> Component getStatDescription(StatAccessor<T> stat, Function<T, Component> fallback) {
        StatType<T> statType = stat.getType();
        T statValue = stat.getValue();
        MutableComponent component;
        if (statType == Stats.CUSTOM) {
            return Component.translatable(StatsScreenAccessor.callGetTranslationKey((Stat<ResourceLocation>) stat));
        } else if (statType == Stats.ENTITY_KILLED) {
            component = Component.translatable("gui.loot_table_wand.condition.predicate.entity.sub.player.stats.stat_type.killed");
        } else if (statType == Stats.ENTITY_KILLED_BY) {
            component = Component.translatable("gui.loot_table_wand.condition.predicate.entity.sub.player.stats.stat_type.killed_by");
        } else {
            component = statType.getDisplayName().copy();
        }
        return component.append(" - ").append(VanillaRegistryParameter.getTypeDescription(statValue, fallback));
    }

    public static <P, T> LIFOCDefinition.InlineMultiModeParameterDefinition<P, StatType<?>> getStatDefinition(Function<P, StatAccessor<T>> supplier, BiConsumer<P, StatAccessor<?>> updater) {
        return new LIFOCDefinition.InlineMultiModeParameterDefinition<P, StatType<?>>(
                new ForgeRegistryParameter<>(null, null, ForgeRegistries.STAT_TYPES), p -> supplier.apply(p).getType(), (p, type) -> updater.accept(p, (StatAccessor<?>) type.iterator().next()), (Function<P, Pair<String, List<LIFOCDefinition.BuildableParameterDefinition<P, ?>>>>) p -> Pair.of(null, List.of(
                    new LIFOCDefinition.ParameterDefinition<>(new ObjectParameter<>(StatAccessor.class, null, null, stat -> getStatDescription(stat, s -> Component.literal(stat.getType().getRegistry().getKey((StatAccessor<?>) s).toString()))), supplier::apply, updater::accept)
            )));
    }

    @Nullable
    public static LIFOCDefinition.MultiParameterDefinition<EntityPredicateAccessor> getEntityPredicate() {
        return entityPredicate.get();
    }

    public static final Supplier<LIFOCDefinition.MultiParameterDefinition<EnchantmentPredicateAccessor>> ENCHANTMENT_PREDICATE = () -> new LIFOCDefinition.MultiParameterDefinition<>(List.of(
            new LIFOCDefinition.ParameterDefinition<>(new ForgeRegistryParameter<>(null, "gui.loot_table_wand.condition.predicate.enchantment.enchantment", ForgeRegistries.ENCHANTMENTS), EnchantmentPredicateAccessor::getEnchantment, EnchantmentPredicateAccessor::setEnchantment),
            new LIFOCDefinition.ParameterDefinition<>(new MinMaxBoundsParameter<>((MinMaxBoundsAccessor<Integer>) MinMaxBoundsIntsAccessor.createInts(1, null), "gui.loot_table_wand.condition.predicate.enchantment.level", true), (predicate) -> (MinMaxBoundsAccessor<Integer>) predicate.getLevel(), (predicate, bounds) -> predicate.setLevel((MinMaxBounds.Ints) bounds))
    ));
    public static final Map<Integer, DescriptionMerger<?>> ENCHANTMENT_PREDICATE_DESCRIPTION_MERGERS = Map.of(0, DescriptionMerger.replaceDescription(null), 1, DescriptionMerger.replaceDescriptionLowercase("gui.loot_table_wand.condition.predicate.enchantment.level.single"));

    public static final Supplier<LIFOCDefinition.MultiParameterDefinition<ItemPredicateAccessor>> ITEM_PREDICATE = () -> new LIFOCDefinition.MultiParameterDefinition<>(List.of(
            new LIFOCDefinition.ParameterDefinition<>(new TagParameter<>(null, "gui.loot_table_wand.condition.predicate.item.tag"), ItemPredicateAccessor::getTag, ItemPredicateAccessor::setTag),
            new LIFOCDefinition.ListParameterDefinition<>(new ListParameter<>(Component.translatable("gui.loot_table_wand.condition.predicate.item.items"), "gui.loot_table_wand.condition.predicate.item.items.single",
                    new LIFOCDefinition.ParameterDefinition<>(new ForgeRegistryParameter<>(null, null, ForgeRegistries.ITEMS), ListParameter.Holder::get, ListParameter.Holder::set)), ListParameter.wrapHolder(ItemPredicateAccessor::getItems), ListParameter.unwrapHolderToSet(ItemPredicateAccessor::setItems), null),
            new LIFOCDefinition.ParameterDefinition<>(new MinMaxBoundsParameter<>(null, "gui.loot_table_wand.condition.predicate.item.count"), (predicate) -> (MinMaxBoundsAccessor<Number>) predicate.getCount(), (predicate, bounds) -> predicate.setCount((MinMaxBounds.Ints) bounds)),
            new LIFOCDefinition.ParameterDefinition<>(new MinMaxBoundsParameter<>(null, "gui.loot_table_wand.condition.predicate.item.durability"), (predicate) -> (MinMaxBoundsAccessor<Number>) predicate.getDurability(), (predicate, bounds) -> predicate.setDurability((MinMaxBounds.Ints) bounds)),
            new LIFOCDefinition.ParameterDefinition<>(new ForgeRegistryParameter<>(null, "gui.loot_table_wand.condition.predicate.item.potion", ForgeRegistries.POTIONS), ItemPredicateAccessor::getPotion, ItemPredicateAccessor::setPotion),
            new LIFOCDefinition.ListParameterDefinition<>(new ListParameter<>(Component.translatable("gui.loot_table_wand.condition.predicate.item.enchantments"), (param) -> param.getSingleEntryIndex() == 0 ? new Parameter.Description<>("gui.loot_table_wand.condition.predicate.item.enchantments.enchantment") : null,
                    new LIFOCDefinition.NullableMultiParameterDefinition<>(null, ENCHANTMENT_PREDICATE_DESCRIPTION_MERGERS, ENCHANTMENT_PREDICATE, ListParameter.Holder::get, (EnchantmentPredicateAccessor) EnchantmentPredicate.ANY, false)), (predicate) -> Arrays.stream(predicate.getEnchantments()).map((enchant) -> new ListParameter.Holder<>((EnchantmentPredicateAccessor) enchant)).toList(), (predicate, list) -> predicate.setEnchantments((EnchantmentPredicate[]) list.stream().map((holder) -> (EnchantmentPredicate) holder.get()).toArray()), null),
            new LIFOCDefinition.ListParameterDefinition<>(new ListParameter<>(Component.translatable("gui.loot_table_wand.condition.predicate.item.stored_enchantments"), (param) -> param.getSingleEntryIndex() == 0 ? new Parameter.Description<>("gui.loot_table_wand.condition.predicate.item.stored_enchantments.enchantment") : null,
                    new LIFOCDefinition.NullableMultiParameterDefinition<>(null, ENCHANTMENT_PREDICATE_DESCRIPTION_MERGERS, ENCHANTMENT_PREDICATE, ListParameter.Holder::get, (EnchantmentPredicateAccessor) EnchantmentPredicate.ANY, false)), (predicate) -> Arrays.stream(predicate.getStoredEnchantments()).map((enchant) -> new ListParameter.Holder<>((EnchantmentPredicateAccessor) enchant)).toList(), (predicate, list) -> predicate.setStoredEnchantments((EnchantmentPredicate[]) list.stream().map((holder) -> (EnchantmentPredicate) holder.get()).toArray()), null),
            getNbtDefinition(ItemPredicateAccessor::getNbt, "gui.loot_table_wand.condition.predicate.item.nbt")
    ));

    public static final Supplier<LIFOCDefinition.MultiParameterDefinition<StatePropertiesPredicateAccessor>> STATE_PROPERTIES_PREDICATE = () -> new LIFOCDefinition.MultiParameterDefinition<>(List.of(
            new LIFOCDefinition.ListParameterDefinition<>(new ListParameter<>(
                    new LIFOCDefinition.InlineMultiModeParameterDefinition<>(
                            PropertyMatcherAccessor.class, ListParameter.Holder::get, ListParameter.Holder::set, (propertyMatcher) -> {
                        if (propertyMatcher instanceof ExactPropertyMatcherAccessor) {
                            return Pair.of("gui.loot_table_wand.condition.predicate.state_properties.property", List.of(
                                    new LIFOCDefinition.ParameterDefinition<>(new StringParameter(null, null), PropertyMatcherAccessor::getName, PropertyMatcherAccessor::setName),
                                    new LIFOCDefinition.ParameterDefinition<>(new StringParameter(null, null), (matcher) -> ((ExactPropertyMatcherAccessor) matcher).getValue(), (matcher, value) -> ((ExactPropertyMatcherAccessor) matcher).setValue(value))
                            ));
                        } else if (propertyMatcher instanceof RangedPropertyMatcherAccessor) {
                            return Pair.of("gui.loot_table_wand.condition.predicate.state_properties.property", List.of(
                                    new LIFOCDefinition.ParameterDefinition<>(new StringParameter(null, null), PropertyMatcherAccessor::getName, PropertyMatcherAccessor::setName),
                                    new LIFOCDefinition.ParameterDefinition<>(new ObjectParameter<>(RangedPropertyMatcherAccessor.class, null, null, MinMaxBoundsParameter.createBoundsDescriptor(RangedPropertyMatcherAccessor::getMinValue, RangedPropertyMatcherAccessor::getMaxValue, Component::literal)), (matcher) -> (RangedPropertyMatcherAccessor) matcher, (matcher, rangedMatcher) -> {
                                    })
                            ));
                        } else {
                            return Pair.of(null, List.of());
                        }
                    }
                    )), (predicate) -> predicate.getProperties().stream().map(matcher -> new ListParameter.Holder<>((PropertyMatcherAccessor) matcher)).toList(), (predicate, list) -> predicate.setProperties(list.stream().map(holder -> (StatePropertiesPredicate.PropertyMatcher) holder.get()).toList()), null)
    ));

    public static final Supplier<LIFOCDefinition.MultiParameterDefinition<BlockPredicateAccessor>> BLOCK_PREDICATE = () -> new LIFOCDefinition.MultiParameterDefinition<>(List.of(
            new LIFOCDefinition.ParameterDefinition<>(new TagParameter<>(null, "gui.loot_table_wand.condition.predicate.block.tag"), BlockPredicateAccessor::getTag, BlockPredicateAccessor::setTag),
            new LIFOCDefinition.ListParameterDefinition<>(new ListParameter<>(Component.translatable("gui.loot_table_wand.condition.predicate.block.blocks"), "gui.loot_table_wand.condition.predicate.block.blocks.single",
                    new LIFOCDefinition.ParameterDefinition<>(new ForgeRegistryParameter<>(null, null, ForgeRegistries.BLOCKS), ListParameter.Holder::get, ListParameter.Holder::set)), ListParameter.wrapHolder(BlockPredicateAccessor::getBlocks), ListParameter.unwrapHolderToSet(BlockPredicateAccessor::setBlocks), null),
            new LIFOCDefinition.NullableMultiParameterDefinition<>(Component.translatable("gui.loot_table_wand.condition.predicate.block.state_properties"), STATE_PROPERTIES_PREDICATE, predicate -> (StatePropertiesPredicateAccessor) predicate.getProperties(), (StatePropertiesPredicateAccessor) StatePropertiesPredicate.ANY, false),
            getNbtDefinition(BlockPredicateAccessor::getNbt, "gui.loot_table_wand.condition.predicate.block.nbt")
    ));

    public static final Supplier<LIFOCDefinition.MultiParameterDefinition<FluidPredicateAccessor>> FLUID_PREDICATE = () -> new LIFOCDefinition.MultiParameterDefinition<>(List.of(
            new LIFOCDefinition.ParameterDefinition<>(new TagParameter<>(null, "gui.loot_table_wand.condition.predicate.fluid.tag"), FluidPredicateAccessor::getTag, FluidPredicateAccessor::setTag),
            new LIFOCDefinition.ParameterDefinition<>(new ForgeRegistryParameter<>(null, "gui.loot_table_wand.condition.predicate.fluid.fluid", ForgeRegistries.FLUIDS), FluidPredicateAccessor::getFluid, FluidPredicateAccessor::setFluid),
            new LIFOCDefinition.NullableMultiParameterDefinition<>(Component.translatable("gui.loot_table_wand.condition.predicate.fluid.state_properties"), STATE_PROPERTIES_PREDICATE, predicate -> (StatePropertiesPredicateAccessor) predicate.getProperties(), (StatePropertiesPredicateAccessor) StatePropertiesPredicate.ANY, false)
    ));

    public static final Supplier<LIFOCDefinition.MultiParameterDefinition<LocationPredicateAccessor>> LOCATION_PREDICATE = () -> new LIFOCDefinition.MultiParameterDefinition<>(List.of(
            new LIFOCDefinition.ParameterDefinition<>(new MinMaxBoundsParameter<>((MinMaxBoundsAccessor<Double>) MinMaxBoundsDoublesAccessor.createDoubles(null, null), "gui.loot_table_wand.condition.predicate.location.x"), (predicate) -> (MinMaxBoundsAccessor<Double>) predicate.getX(), (predicate, bounds) -> predicate.setX((MinMaxBounds.Doubles) bounds)),
            new LIFOCDefinition.ParameterDefinition<>(new MinMaxBoundsParameter<>((MinMaxBoundsAccessor<Double>) MinMaxBoundsDoublesAccessor.createDoubles(null, null), "gui.loot_table_wand.condition.predicate.location.y"), (predicate) -> (MinMaxBoundsAccessor<Double>) predicate.getY(), (predicate, bounds) -> predicate.setY((MinMaxBounds.Doubles) bounds)),
            new LIFOCDefinition.ParameterDefinition<>(new MinMaxBoundsParameter<>((MinMaxBoundsAccessor<Double>) MinMaxBoundsDoublesAccessor.createDoubles(null, null), "gui.loot_table_wand.condition.predicate.location.z"), (predicate) -> (MinMaxBoundsAccessor<Double>) predicate.getZ(), (predicate, bounds) -> predicate.setZ((MinMaxBounds.Doubles) bounds)),
            new LIFOCDefinition.ParameterDefinition<>(new ResourceKeyParameter<>(null, "gui.loot_table_wand.condition.predicate.location.dimension"), LocationPredicateAccessor::getDimension, LocationPredicateAccessor::setDimension),
            new LIFOCDefinition.ParameterDefinition<>(new ResourceKeyParameter<>(null, "gui.loot_table_wand.condition.predicate.location.biome"), LocationPredicateAccessor::getBiome, LocationPredicateAccessor::setBiome),
            new LIFOCDefinition.ParameterDefinition<>(new ResourceKeyParameter<>(null, "gui.loot_table_wand.condition.predicate.location.structure"), LocationPredicateAccessor::getStructure, LocationPredicateAccessor::setStructure),
            new LIFOCDefinition.ParameterDefinition<>(new BooleanParameter(null, "gui.loot_table_wand.condition.predicate.location.smokey"), LocationPredicateAccessor::getSmokey, LocationPredicateAccessor::setSmokey),
            new LIFOCDefinition.ParameterDefinition<>(new MinMaxBoundsParameter<>(null, "gui.loot_table_wand.condition.predicate.location.light"), (predicate) -> (MinMaxBoundsAccessor<Integer>) ((LightPredicateAccessor) predicate.getLight()).getComposite(), (predicate, bounds) -> predicate.setLight(LightPredicateAccessor.createLightPredicate((MinMaxBounds.Ints) bounds))),
            new LIFOCDefinition.NullableMultiParameterDefinition<>(Component.translatable("gui.loot_table_wand.condition.predicate.location.block"), PREDICATE_SIMPLIFIER, BLOCK_PREDICATE, (predicate) -> (BlockPredicateAccessor) predicate.getBlock(), (BlockPredicateAccessor) BlockPredicate.ANY, true),
            new LIFOCDefinition.NullableMultiParameterDefinition<>(Component.translatable("gui.loot_table_wand.condition.predicate.location.fluid"), PREDICATE_SIMPLIFIER, FLUID_PREDICATE, (predicate) -> (FluidPredicateAccessor) predicate.getFluid(), (FluidPredicateAccessor) FluidPredicate.ANY, true)
    ));

    public static final Supplier<LIFOCDefinition.MultiParameterDefinition<EntityPredicateAccessor>> ENTITY_PREDICATE = () -> new LIFOCDefinition.MultiParameterDefinition<EntityPredicateAccessor>(List.of(
            new LIFOCDefinition.InlineMultiModeParameterDefinition<>(EntityTypePredicate.class, (predicate) -> predicate.getEntityType(), (predicate, type) -> predicate.setEntityType(type), (predicate) -> {
                if (predicate instanceof TypePredicateAccessor) {
                    return Pair.of("gui.loot_table_wand.condition.predicate.entity.type.single", List.of(new LIFOCDefinition.ParameterDefinition<>(new ForgeRegistryParameter<>(null, null, ForgeRegistries.ENTITY_TYPES), (p) -> ((TypePredicateAccessor) p).getType(), (p, type) -> ((TypePredicateAccessor) p).setType(type))));
                } else if (predicate instanceof EntityTypeTagPredicateAccessor) {
                    return Pair.of("gui.loot_table_wand.condition.predicate.entity.type.tag", List.of(new LIFOCDefinition.ParameterDefinition<>(new TagParameter<>(null, null), p -> ((EntityTypeTagPredicateAccessor) p).getTag(), (p, tag) -> ((EntityTypeTagPredicateAccessor) p).setTag(tag))));
                } else {
                    return Pair.of(null, List.of());
                }
            }),
            new LIFOCDefinition.ParameterDefinition<>(new MinMaxBoundsParameter<>(null, "gui.loot_table_wand.condition.predicate.entity.distance.x", Component.translatable("gui.loot_table_wand.condition.predicate.entity.distance.any"), "gui.loot_table_wand.condition.predicate.entity.distance.min", "gui.loot_table_wand.condition.predicate.entity.distance.max", MinMaxBoundsParameter.BOUNDS_RANGE, MinMaxBoundsParameter.BOUNDS_EXACT, false), (predicate) -> (MinMaxBoundsAccessor<Double>) ((DistancePredicateAccessor) predicate.getDistanceToPlayer()).getX(), (predicate, bounds) -> ((DistancePredicateAccessor) predicate.getDistanceToPlayer()).setX((MinMaxBounds.Doubles) bounds)),
            new LIFOCDefinition.ParameterDefinition<>(new MinMaxBoundsParameter<>(null, "gui.loot_table_wand.condition.predicate.entity.distance.y", Component.translatable("gui.loot_table_wand.condition.predicate.entity.distance.any"), "gui.loot_table_wand.condition.predicate.entity.distance.min", "gui.loot_table_wand.condition.predicate.entity.distance.max", MinMaxBoundsParameter.BOUNDS_RANGE, MinMaxBoundsParameter.BOUNDS_EXACT, false), (predicate) -> (MinMaxBoundsAccessor<Double>) ((DistancePredicateAccessor) predicate.getDistanceToPlayer()).getY(), (predicate, bounds) -> ((DistancePredicateAccessor) predicate.getDistanceToPlayer()).setY((MinMaxBounds.Doubles) bounds)),
            new LIFOCDefinition.ParameterDefinition<>(new MinMaxBoundsParameter<>(null, "gui.loot_table_wand.condition.predicate.entity.distance.z", Component.translatable("gui.loot_table_wand.condition.predicate.entity.distance.any"), "gui.loot_table_wand.condition.predicate.entity.distance.min", "gui.loot_table_wand.condition.predicate.entity.distance.max", MinMaxBoundsParameter.BOUNDS_RANGE, MinMaxBoundsParameter.BOUNDS_EXACT, false), (predicate) -> (MinMaxBoundsAccessor<Double>) ((DistancePredicateAccessor) predicate.getDistanceToPlayer()).getZ(), (predicate, bounds) -> ((DistancePredicateAccessor) predicate.getDistanceToPlayer()).setZ((MinMaxBounds.Doubles) bounds)),
            new LIFOCDefinition.ParameterDefinition<>(new MinMaxBoundsParameter<>(null, "gui.loot_table_wand.condition.predicate.entity.distance.horizontal", Component.translatable("gui.loot_table_wand.condition.predicate.entity.distance.any"), "gui.loot_table_wand.condition.predicate.entity.distance.min", "gui.loot_table_wand.condition.predicate.entity.distance.max", MinMaxBoundsParameter.BOUNDS_RANGE, MinMaxBoundsParameter.BOUNDS_EXACT, false), (predicate) -> (MinMaxBoundsAccessor<Double>) ((DistancePredicateAccessor) predicate.getDistanceToPlayer()).getHorizontal(), (predicate, bounds) -> ((DistancePredicateAccessor) predicate.getDistanceToPlayer()).setHorizontal((MinMaxBounds.Doubles) bounds)),
            new LIFOCDefinition.ParameterDefinition<>(new MinMaxBoundsParameter<>(null, "gui.loot_table_wand.condition.predicate.entity.distance.absolute", Component.translatable("gui.loot_table_wand.condition.predicate.entity.distance.any"), "gui.loot_table_wand.condition.predicate.entity.distance.min", "gui.loot_table_wand.condition.predicate.entity.distance.max", MinMaxBoundsParameter.BOUNDS_RANGE, MinMaxBoundsParameter.BOUNDS_EXACT, false), (predicate) -> (MinMaxBoundsAccessor<Double>) ((DistancePredicateAccessor) predicate.getDistanceToPlayer()).getAbsolute(), (predicate, bounds) -> ((DistancePredicateAccessor) predicate.getDistanceToPlayer()).setAbsolute((MinMaxBounds.Doubles) bounds)),
            new LIFOCDefinition.NullableMultiParameterDefinition<>(Component.translatable("gui.loot_table_wand.condition.predicate.entity.location"), PREDICATE_SIMPLIFIER, LOCATION_PREDICATE, (predicate) -> (LocationPredicateAccessor) predicate.getLocation(), (LocationPredicateAccessor) LocationPredicate.ANY, true),
            new LIFOCDefinition.NullableMultiParameterDefinition<>(Component.translatable("gui.loot_table_wand.condition.predicate.entity.stepping_on"), PREDICATE_SIMPLIFIER, LOCATION_PREDICATE, (predicate) -> (LocationPredicateAccessor) predicate.getSteppingOnLocation(), (LocationPredicateAccessor) LocationPredicate.ANY, true),
            new LIFOCDefinition.ListParameterDefinition<EntityPredicateAccessor, MultiParameter, ListParameter.Holder<Pair<MobEffect, MobEffectInstancePredicateAccessor>>>(new ListParameter<MultiParameter, ListParameter.Holder<Pair<MobEffect, MobEffectInstancePredicateAccessor>>>(Component.translatable("gui.loot_table_wand.condition.predicate.entity.mob_effects"),
                    new LIFOCDefinition.MultiParameterDefinition<>(List.of(
                            new LIFOCDefinition.ParameterDefinition<>(new ForgeRegistryParameter<>(null, "gui.loot_table_wand.condition.predicate.entity.mob_effects.name", ForgeRegistries.MOB_EFFECTS), (holder) -> holder.get().getFirst(), (holder, effect) -> holder.set(Pair.of(effect, holder.get().getSecond()))),
                            new LIFOCDefinition.ParameterDefinition<>(new MinMaxBoundsParameter<>(null, (amplifier) -> amplifier + 1, (level) -> level - 1, "gui.loot_table_wand.condition.predicate.entity.mob_effects.amplifier", true), holder -> (MinMaxBoundsAccessor<Integer>) holder.get().getSecond().getAmplifier(), (holder, bounds) -> holder.get().getSecond().setAmplifier((MinMaxBounds.Ints) bounds)),
                            new LIFOCDefinition.ParameterDefinition<>(new MinMaxBoundsParameter<>(null, (level) -> ((double) level) / 20.0, (amplifier) -> (int) amplifier.doubleValue() * 20, "gui.loot_table_wand.condition.predicate.entity.mob_effects.duration", false), holder -> (MinMaxBoundsAccessor<Integer>) holder.get().getSecond().getDuration(), (holder, bounds) -> holder.get().getSecond().setDuration((MinMaxBounds.Ints) bounds)),
                            new LIFOCDefinition.ParameterDefinition<>(new BooleanParameter(null, "gui.loot_table_wand.condition.predicate.entity.mob_effects.ambient"), holder -> holder.get().getSecond().getAmbient(), (holder, b) -> holder.get().getSecond().setAmbient(b)),
                            new LIFOCDefinition.ParameterDefinition<>(new BooleanParameter(null, "gui.loot_table_wand.condition.predicate.entity.mob_effects.visible"), holder -> holder.get().getSecond().getVisible(), (holder, b) -> holder.get().getSecond().setVisible(b))
                    ))
            ), predicate -> ((MobEffectsPredicateAccessor) predicate.getEffects()).getEffects().entrySet().stream().map(entry -> new ListParameter.Holder<>(new Pair<>(entry.getKey(), (MobEffectInstancePredicateAccessor) entry.getValue()))).toList(), (predicate, holderList) -> ((MobEffectsPredicateAccessor) predicate.getEffects()).setEffects(holderList.stream().map(ListParameter.Holder::get).collect(Collectors.toMap(Pair::getFirst, (pair) -> (MobEffectsPredicate.MobEffectInstancePredicate) pair.getSecond()))), null),
            getNbtDefinition(EntityPredicateAccessor::getNbt, "gui.loot_table_wand.condition.predicate.entity.nbt"),
            new LIFOCDefinition.ParameterDefinition<>(new BooleanParameter(null, "gui.loot_table_wand.condition.predicate.entity.is_on_fire"), predicate -> ((EntityFlagsPredicateAccessor) predicate.getFlags()).getIsOnFire(), (predicate, b) -> ((EntityFlagsPredicateAccessor) predicate.getFlags()).setIsOnFire(b)),
            new LIFOCDefinition.ParameterDefinition<>(new BooleanParameter(null, "gui.loot_table_wand.condition.predicate.entity.is_sprinting"), predicate -> ((EntityFlagsPredicateAccessor) predicate.getFlags()).getIsSprinting(), (predicate, b) -> ((EntityFlagsPredicateAccessor) predicate.getFlags()).setIsSprinting(b)),
            new LIFOCDefinition.ParameterDefinition<>(new BooleanParameter(null, "gui.loot_table_wand.condition.predicate.entity.is_crouching"), predicate -> ((EntityFlagsPredicateAccessor) predicate.getFlags()).getIsCrouching(), (predicate, b) -> ((EntityFlagsPredicateAccessor) predicate.getFlags()).setIsCrouching(b)),
            new LIFOCDefinition.ParameterDefinition<>(new BooleanParameter(null, "gui.loot_table_wand.condition.predicate.entity.is_swimming"), predicate -> ((EntityFlagsPredicateAccessor) predicate.getFlags()).getIsSwimming(), (predicate, b) -> ((EntityFlagsPredicateAccessor) predicate.getFlags()).setIsSwimming(b)),
            new LIFOCDefinition.ParameterDefinition<>(new BooleanParameter(null, "gui.loot_table_wand.condition.predicate.entity.is_baby"), predicate -> ((EntityFlagsPredicateAccessor) predicate.getFlags()).getIsBaby(), (predicate, b) -> ((EntityFlagsPredicateAccessor) predicate.getFlags()).setIsBaby(b)),
            new LIFOCDefinition.NullableMultiParameterDefinition<>(Component.translatable("gui.loot_table_wand.condition.predicate.entity.equipment.head"), PREDICATE_SIMPLIFIER, ITEM_PREDICATE, (predicate) -> (ItemPredicateAccessor) ((EntityEquipmentPredicateAccessor) predicate.getEquipment()).getHead(), (ItemPredicateAccessor) ItemPredicate.ANY, true),
            new LIFOCDefinition.NullableMultiParameterDefinition<>(Component.translatable("gui.loot_table_wand.condition.predicate.entity.equipment.chest"), PREDICATE_SIMPLIFIER, ITEM_PREDICATE, (predicate) -> (ItemPredicateAccessor) ((EntityEquipmentPredicateAccessor) predicate.getEquipment()).getChest(), (ItemPredicateAccessor) ItemPredicate.ANY, true),
            new LIFOCDefinition.NullableMultiParameterDefinition<>(Component.translatable("gui.loot_table_wand.condition.predicate.entity.equipment.legs"), PREDICATE_SIMPLIFIER, ITEM_PREDICATE, (predicate) -> (ItemPredicateAccessor) ((EntityEquipmentPredicateAccessor) predicate.getEquipment()).getLegs(), (ItemPredicateAccessor) ItemPredicate.ANY, true),
            new LIFOCDefinition.NullableMultiParameterDefinition<>(Component.translatable("gui.loot_table_wand.condition.predicate.entity.equipment.feet"), PREDICATE_SIMPLIFIER, ITEM_PREDICATE, (predicate) -> (ItemPredicateAccessor) ((EntityEquipmentPredicateAccessor) predicate.getEquipment()).getFeet(), (ItemPredicateAccessor) ItemPredicate.ANY, true),
            new LIFOCDefinition.NullableMultiParameterDefinition<>(Component.translatable("gui.loot_table_wand.condition.predicate.entity.equipment.mainhand"), PREDICATE_SIMPLIFIER, ITEM_PREDICATE, (predicate) -> (ItemPredicateAccessor) ((EntityEquipmentPredicateAccessor) predicate.getEquipment()).getMainhand(), (ItemPredicateAccessor) ItemPredicate.ANY, true),
            new LIFOCDefinition.NullableMultiParameterDefinition<>(Component.translatable("gui.loot_table_wand.condition.predicate.entity.equipment.offhand"), PREDICATE_SIMPLIFIER, ITEM_PREDICATE, (predicate) -> (ItemPredicateAccessor) ((EntityEquipmentPredicateAccessor) predicate.getEquipment()).getOffhand(), (ItemPredicateAccessor) ItemPredicate.ANY, true),
            new LIFOCDefinition.InlineMultiModeParameterDefinition<>(EntitySubPredicate.class, EntityPredicateAccessor::getSubPredicate, EntityPredicateAccessor::setSubPredicate, (subPredicate) -> {
                if (subPredicate instanceof LighthingBoltPredicateAccessor) {
                    return Pair.of(null, List.of(new LIFOCDefinition.MultiParameterDefinition<>(List.of(
                            new LIFOCDefinition.ParameterDefinition<>(new MinMaxBoundsParameter<>(null, "gui.loot_table_wand.condition.predicate.entity.sub.lightning.blocks_set_on_fire"), sub -> (MinMaxBoundsAccessor<Integer>) ((LighthingBoltPredicateAccessor) sub).getBlocksSetOnFire(), (sub, bounds) -> ((LighthingBoltPredicateAccessor) sub).setBlocksSetOnFire((MinMaxBounds.Ints) bounds)),
                            new LIFOCDefinition.NullableMultiParameterDefinition<>(Component.translatable("gui.loot_table_wand.condition.predicate.entity.sub.lightning.entity_struck"), PREDICATE_SIMPLIFIER, LootItemConditionDefinitions::getEntityPredicate, sub -> (EntityPredicateAccessor) ((LighthingBoltPredicateAccessor) sub).getEntityStruck(), (EntityPredicateAccessor) EntityPredicate.ANY, true)
                    ), Component.translatable("gui.loot_table_wand.condition.predicate.entity.sub.lightning"), null, true)));
                } else if (subPredicate instanceof FishingHookPredicateAccessor) {
                    return Pair.of(null, List.of(new LIFOCDefinition.ParameterDefinition<>(new BooleanParameter(null, "gui.loot_table_wand.condition.predicate.entity.sub.fishing_hook"), sub -> ((FishingHookPredicateAccessor) sub).isInOpenWater(), (sub, b) -> ((FishingHookPredicateAccessor) sub).setInOpenWater(b))));
                } else if (subPredicate instanceof PlayerPredicateAccessor) {
                    return Pair.of(null, List.of(new LIFOCDefinition.MultiParameterDefinition<>(List.of(
                            new LIFOCDefinition.ParameterDefinition<>(new MinMaxBoundsParameter<>(null, "gui.loot_table_wand.condition.predicate.entity.sub.player.level"), sub -> (MinMaxBoundsAccessor<Integer>) ((PlayerPredicateAccessor)sub).getLevel(), (sub, bounds) -> ((PlayerPredicateAccessor)sub).setLevel((MinMaxBounds.Ints) bounds)),
                            new LIFOCDefinition.ParameterDefinition<>(new EnumParameter<>(GameType.class, null, EnumParameter.createNullDescription("gui.loot_table_wand.condition.predicate.entity.sub.player.game_type"), Map.of(GameType.SURVIVAL, "gui.loot_table_wand.condition.predicate.entity.sub.player.game_type.survival", GameType.CREATIVE, "gui.loot_table_wand.condition.predicate.entity.sub.player.game_type.creative", GameType.ADVENTURE, "gui.loot_table_wand.condition.predicate.entity.sub.player.game_type.adventure", GameType.SPECTATOR, "gui.loot_table_wand.condition.predicate.entity.sub.player.game_type.spectator")), sub -> ((PlayerPredicateAccessor)sub).getGameType(), (sub, mode) -> ((PlayerPredicateAccessor)sub).setGameType(mode)),
                            new LIFOCDefinition.ListParameterDefinition<EntitySubPredicate, InlineMultiParameter, ListParameter.Holder<Pair<StatAccessor<?>, MinMaxBoundsAccessor<Integer>>>>(new ListParameter<>(Component.translatable("gui.loot_table_wand.condition.predicate.entity.sub.player.stats"), new LIFOCDefinition.InlineMultiParameterDefinition<>(List.of(
                                    getStatDefinition(holder -> holder.get().getFirst(), (holder, stat) -> holder.set(Pair.of(stat, holder.get().getSecond()))),
                                    new LIFOCDefinition.ParameterDefinition<ListParameter.Holder<Pair<StatAccessor<?>, MinMaxBoundsAccessor<Integer>>>, MinMaxBoundsAccessor<Integer>>(new MinMaxBoundsParameter<>(null, null), holder -> holder.get().getSecond(), (holder, bounds) -> holder.set(Pair.of(holder.get().getFirst(), bounds)))
                            ), "gui.loot_table_wand.condition.predicate.entity.sub.player.stats.stat")), sub -> ((PlayerPredicateAccessor)sub).getStats().entrySet().stream().map(entry -> new ListParameter.Holder<>((Pair<StatAccessor<?>, MinMaxBoundsAccessor<Integer>>) Pair.of((StatAccessor<?>) entry.getKey(), (MinMaxBoundsAccessor<Integer>) entry.getValue()))).toList(), (sub, list) -> ((PlayerPredicateAccessor)sub).setStats(list.stream().map(holder -> holder.get()).collect(Collectors.toMap(pair -> (Stat<?>) pair.getFirst(), pair -> (MinMaxBounds.Ints) pair.getSecond()))), null),
                            new LIFOCDefinition.ListParameterDefinition<EntitySubPredicate, InlineMultiParameter, ListParameter.Holder<Pair<ResourceLocation, Boolean>>>(new ListParameter<>(Component.translatable("gui.loot_table_wand.condition.predicate.entity.sub.player.recipes"), new LIFOCDefinition.InlineMultiParameterDefinition<>(List.of(
                                    new LIFOCDefinition.ParameterDefinition<>(new StringParameter(null, null), holder -> holder.get().getFirst().toString(), (holder, string) -> holder.set(Pair.of(ResourceLocation.parse(string), holder.get().getSecond()))),
                                    new LIFOCDefinition.ParameterDefinition<>(new BooleanParameter(null, null, Component.translatable("gui.loot_table_wand.condition.predicate.entity.sub.player.recipes.recipe.true"), Component.translatable("gui.loot_table_wand.condition.predicate.entity.sub.player.recipes.recipe.false")), holder -> holder.get().getSecond(), (holder, b) -> holder.set(Pair.of(holder.get().getFirst(), b)))
                            ), "gui.loot_table_wand.condition.predicate.entity.sub.player.recipes.recipe")), sub -> ((PlayerPredicateAccessor)sub).getRecipes().object2BooleanEntrySet().stream().map((entry) -> new ListParameter.Holder<>(Pair.of(entry.getKey(), entry.getBooleanValue()))).toList(), (sub, list) -> ((PlayerPredicateAccessor)sub).setRecipes(list.stream().map(ListParameter.Holder::get).collect(Collector.of(Object2BooleanOpenHashMap::new, (map, pair) -> {if(pair.getSecond() != null) map.put(pair.getFirst(), (boolean) pair.getSecond());}, (map1, map2) -> {map1.putAll(map2); return map2;}))), null),
                            new LIFOCDefinition.ListParameterDefinition<EntitySubPredicate, InlineMultiParameter, ListParameter.Holder<Pair<ResourceLocation, PlayerPredicate.AdvancementPredicate>>>(new ListParameter<>(Component.translatable("gui.loot_table_wand.condition.predicate.entity.sub.player.advancements"), new LIFOCDefinition.InlineMultiParameterDefinition<>(List.of(
                                    new LIFOCDefinition.ParameterDefinition<ListParameter.Holder<Pair<ResourceLocation, PlayerPredicate.AdvancementPredicate>>, String>(new StringParameter(null, null), holder -> holder.get().getFirst().toString(), (holder, string) -> holder.set(Pair.of(ResourceLocation.parse(string), holder.get().getSecond()))),
                                    new LIFOCDefinition.InlineMultiModeParameterDefinition<ListParameter.Holder<Pair<ResourceLocation, PlayerPredicate.AdvancementPredicate>>, PlayerPredicate.AdvancementPredicate>(PlayerPredicate.AdvancementPredicate.class, holder -> holder.get().getSecond(), (holder, advancements) -> holder.set(Pair.of(holder.get().getFirst(), advancements)), (predicate) -> {
                                        if (predicate instanceof PlayerPredicate.AdvancementDonePredicate) {
                                            return Pair.of(null, List.of(new LIFOCDefinition.ParameterDefinition<>(new BooleanParameter(null, null, Component.translatable("gui.loot_table_wand.condition.predicate.entity.sub.player.advancements.advancement.done.true"), Component.translatable("gui.loot_table_wand.condition.predicate.entity.sub.player.advancements.advancement.done.false")), p -> ((AdvancementDonePredicateAccessor)p).isState(), (p, b) -> ((AdvancementDonePredicateAccessor)p).setState(b))));
                                        } else if (predicate instanceof PlayerPredicate.AdvancementCriterionsPredicate) {
                                            return Pair.of(null, List.of(new LIFOCDefinition.ListParameterDefinition<PlayerPredicate.AdvancementPredicate, InlineMultiParameter, ListParameter.Holder<Pair<String, Boolean>>>(new ListParameter<>(
                                                    new LIFOCDefinition.InlineMultiParameterDefinition<>(
                                                            List.of(
                                                                    new LIFOCDefinition.ParameterDefinition<>(new StringParameter(null, null), holder -> holder.get().getFirst(), (holder, string) -> holder.set(Pair.of(string, holder.get().getSecond()))),
                                                                    new LIFOCDefinition.ParameterDefinition<>(new BooleanParameter(null, null, Component.translatable("gui.loot_table_wand.condition.predicate.entity.sub.player.advancements.advancement.criterion.true"), Component.translatable("gui.loot_table_wand.condition.predicate.entity.sub.player.advancements.advancement.criterion.false")), holder -> holder.get().getSecond(), (holder, b) -> holder.set(Pair.of(holder.get().getFirst(), b)))
                                                            ), "gui.loot_table_wand.condition.predicate.entity.sub.player.advancements.advancement.criterion"
                                                    )
                                            ), p -> ((AdvancementCriterionsPredicateAccessor)p).getCriterions().object2BooleanEntrySet().stream().map(entry -> new ListParameter.Holder<>(Pair.of(entry.getKey(), entry.getBooleanValue()))).toList(), (p, list) -> ((AdvancementCriterionsPredicateAccessor)p).setCriterions(list.stream().map(ListParameter.Holder::get).collect(Collector.of(Object2BooleanOpenHashMap::new, (map, pair) -> {if(pair.getSecond() != null) map.put(pair.getFirst(), (boolean) pair.getSecond());}, (map1, map2) -> {map1.putAll(map2); return map2;}))), null)));
                                        } else {
                                            return Pair.of(null, List.of());
                                        }
                                    })
                            ), "gui.loot_table_wand.condition.predicate.entity.sub.player.advancements.advancement")), sub -> ((PlayerPredicateAccessor)sub).getAdvancements().entrySet().stream().map(entry -> new ListParameter.Holder<>(Pair.of(entry.getKey(), entry.getValue()))).toList(), (sub, list) -> ((PlayerPredicateAccessor)sub).setAdvancements(list.stream().map(ListParameter.Holder::get).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))), null),
                            new LIFOCDefinition.NullableMultiParameterDefinition<>(Component.translatable("gui.loot_table_wand.condition.predicate.entity.sub.player.looking_at"), PREDICATE_SIMPLIFIER, LootItemConditionDefinitions::getEntityPredicate, sub -> (EntityPredicateAccessor) ((PlayerPredicateAccessor)sub).getLookingAt(), (EntityPredicateAccessor) EntityPredicate.ANY, true)
                    ), Component.translatable("gui.loot_table_wand.condition.predicate.entity.sub.player"), null, true)));
                } else if (subPredicate instanceof SlimePredicateAccessor) {
                    return Pair.of(null, List.of(new LIFOCDefinition.ParameterDefinition<>(new MinMaxBoundsParameter<>(null, "gui.loot_table_wand.condition.predicate.entity.sub.slime"), sub -> (MinMaxBoundsAccessor<Integer>) ((SlimePredicateAccessor)sub).getSize(), (sub, bounds) -> ((SlimePredicateAccessor)sub).setSize((MinMaxBounds.Ints) bounds))));
                } else if (subPredicate instanceof EntityVariantPredicateAccessor<?> p) {
                    if (p.getValue() instanceof CatVariant) {
                        return Pair.of(null, List.of(new LIFOCDefinition.ParameterDefinition<>(new VanillaRegistryParameter<>(null, "gui.loot_table_wand.condition.predicate.entity.sub.variant.cat", BuiltInRegistries.CAT_VARIANT), sub -> ((EntityVariantPredicateAccessor<CatVariant>)sub).getValue(), (sub, value) -> ((EntityVariantPredicateAccessor<CatVariant>)sub).setValue(value))));
                    } else if (p.getValue() instanceof FrogVariant) {
                        return Pair.of(null, List.of(new LIFOCDefinition.ParameterDefinition<>(new VanillaRegistryParameter<>(null, "gui.loot_table_wand.condition.predicate.entity.sub.variant.frog", BuiltInRegistries.FROG_VARIANT), sub -> ((EntityVariantPredicateAccessor<FrogVariant>)sub).getValue(), (sub, value) -> ((EntityVariantPredicateAccessor<FrogVariant>)sub).setValue(value))));
                    } else if (p.getValue() instanceof Axolotl.Variant) {
                        return Pair.of(null, List.of(new LIFOCDefinition.ParameterDefinition<>(new EnumParameter<>(Axolotl.Variant.class, null, "gui.loot_table_wand.condition.predicate.entity.sub.variant.axolotl", (variant) -> Component.literal(variant.getName())), sub -> ((EntityVariantPredicateAccessor<Axolotl.Variant>)sub).getValue(), (sub, value) -> ((EntityVariantPredicateAccessor<Axolotl.Variant>)sub).setValue(value))));
                    } else if (p.getValue() instanceof Boat.Type) {
                        return Pair.of(null, List.of(new LIFOCDefinition.ParameterDefinition<>(new EnumParameter<>(Boat.Type.class, null, "gui.loot_table_wand.condition.predicate.entity.sub.variant.boat", (type) -> Component.literal(type.getName())), sub -> ((EntityVariantPredicateAccessor<Boat.Type>)sub).getValue(), (sub, value) -> ((EntityVariantPredicateAccessor<Boat.Type>)sub).setValue(value))));
                    } else if (p.getValue() instanceof Fox.Type) {
                        return Pair.of(null, List.of(new LIFOCDefinition.ParameterDefinition<>(new EnumParameter<>(Fox.Type.class, null, "gui.loot_table_wand.condition.predicate.entity.sub.variant.fox", (type) -> Component.literal(type.getSerializedName())), sub -> ((EntityVariantPredicateAccessor<Fox.Type>)sub).getValue(), (sub, value) -> ((EntityVariantPredicateAccessor<Fox.Type>)sub).setValue(value))));
                    } else if (p.getValue() instanceof MushroomCow.MushroomType) {
                        return Pair.of(null, List.of(new LIFOCDefinition.ParameterDefinition<>(new EnumParameter<>(MushroomCow.MushroomType.class, null, "gui.loot_table_wand.condition.predicate.entity.sub.variant.mooshroom", (type) -> Component.literal(type.getSerializedName())), sub -> ((EntityVariantPredicateAccessor<MushroomCow.MushroomType>)sub).getValue(), (sub, value) -> ((EntityVariantPredicateAccessor<MushroomCow.MushroomType>)sub).setValue(value))));
                    } else if (p.getValue() instanceof Holder) {
                        return Pair.of(null, List.of(new LIFOCDefinition.ParameterDefinition<>(new ForgeRegistryParameter<>(null, "gui.loot_table_wand.condition.predicate.entity.sub.variant.painting", ForgeRegistries.PAINTING_VARIANTS), sub -> ((EntityVariantPredicateAccessor<Holder<PaintingVariant>>)sub).getValue().get(), (sub, value) -> ((EntityVariantPredicateAccessor<Holder<PaintingVariant>>)sub).setValue(Holder.direct(value)))));
                    } else if (p.getValue() instanceof Rabbit.Variant) {
                        return Pair.of(null, List.of(new LIFOCDefinition.ParameterDefinition<>(new EnumParameter<>(Rabbit.Variant.class, null, "gui.loot_table_wand.condition.predicate.entity.sub.variant.rabbit", (variant) -> Component.literal(variant.getSerializedName())), sub -> ((EntityVariantPredicateAccessor<Rabbit.Variant>)sub).getValue(), (sub, value) -> ((EntityVariantPredicateAccessor<Rabbit.Variant>)sub).setValue(value))));
                    } else if (p.getValue() instanceof Variant) {
                        return Pair.of(null, List.of(new LIFOCDefinition.ParameterDefinition<>(new EnumParameter<>(Variant.class, null, "gui.loot_table_wand.condition.predicate.entity.sub.variant.horse", (variant) -> Component.literal(variant.getSerializedName())), sub -> ((EntityVariantPredicateAccessor<Variant>)sub).getValue(), (sub, value) -> ((EntityVariantPredicateAccessor<Variant>)sub).setValue(value))));
                    } else if (p.getValue() instanceof Llama.Variant) {
                        return Pair.of(null, List.of(new LIFOCDefinition.ParameterDefinition<>(new EnumParameter<>(Llama.Variant.class, null, "gui.loot_table_wand.condition.predicate.entity.sub.variant.llama", (variant) -> Component.literal(variant.getSerializedName())), sub -> ((EntityVariantPredicateAccessor<Llama.Variant>)sub).getValue(), (sub, value) -> ((EntityVariantPredicateAccessor<Llama.Variant>)sub).setValue(value))));
                    } else if (p.getValue() instanceof VillagerType) {
                        return Pair.of(null, List.of(new LIFOCDefinition.ParameterDefinition<>(new VanillaRegistryParameter<>(null, "gui.loot_table_wand.condition.predicate.entity.sub.variant.villager", BuiltInRegistries.VILLAGER_TYPE), sub -> ((EntityVariantPredicateAccessor<VillagerType>)sub).getValue(), (sub, value) -> ((EntityVariantPredicateAccessor<VillagerType>)sub).setValue(value))));
                    } else if (p.getValue() instanceof Parrot.Variant) {
                        return Pair.of(null, List.of(new LIFOCDefinition.ParameterDefinition<>(new EnumParameter<>(Parrot.Variant.class, null, "gui.loot_table_wand.condition.predicate.entity.sub.variant.parrot", (variant) -> Component.literal(variant.getSerializedName())), sub -> ((EntityVariantPredicateAccessor<Parrot.Variant>)sub).getValue(), (sub, value) -> ((EntityVariantPredicateAccessor<Parrot.Variant>)sub).setValue(value))));
                    } else if (p.getValue() instanceof TropicalFish.Pattern) {
                        return Pair.of(null, List.of(new LIFOCDefinition.ParameterDefinition<>(new EnumParameter<>(TropicalFish.Pattern.class, null, "gui.loot_table_wand.condition.predicate.entity.sub.variant.tropical_fish", (variant) -> Component.literal(variant.getSerializedName())), sub -> ((EntityVariantPredicateAccessor<TropicalFish.Pattern>)sub).getValue(), (sub, value) -> ((EntityVariantPredicateAccessor<TropicalFish.Pattern>)sub).setValue(value))));
                    } else {
                        return Pair.of(null, List.of());
                    }
                } else {
                    return Pair.of(null, List.of());
                }
            }),
            new LIFOCDefinition.NullableMultiParameterDefinition<>(Component.translatable("gui.loot_table_wand.condition.predicate.entity.vehicle"), PREDICATE_SIMPLIFIER, LootItemConditionDefinitions::getEntityPredicate, (predicate) -> (EntityPredicateAccessor) predicate.getVehicle(), (EntityPredicateAccessor) EntityPredicate.ANY, true),
            new LIFOCDefinition.NullableMultiParameterDefinition<>(Component.translatable("gui.loot_table_wand.condition.predicate.entity.passenger"), PREDICATE_SIMPLIFIER, LootItemConditionDefinitions::getEntityPredicate, (predicate) -> (EntityPredicateAccessor) predicate.getPassenger(), (EntityPredicateAccessor) EntityPredicate.ANY, true),
            new LIFOCDefinition.NullableMultiParameterDefinition<>(Component.translatable("gui.loot_table_wand.condition.predicate.entity.targeted_entity"), PREDICATE_SIMPLIFIER, LootItemConditionDefinitions::getEntityPredicate, (predicate) -> (EntityPredicateAccessor) predicate.getTargetedEntity(), (EntityPredicateAccessor) EntityPredicate.ANY, true),
            new LIFOCDefinition.ParameterDefinition<>(new StringParameter(null, "gui.loot_table_wand.condition.predicate.entity.team"), EntityPredicateAccessor::getTeam, EntityPredicateAccessor::setTeam)
    ));

    public static final Supplier<LIFOCDefinition.MultiParameterDefinition<DamageSourcePredicateAccessor>> DAMAGE_SOURCE_PREDICATE = () -> new LIFOCDefinition.MultiParameterDefinition<>(List.of(
            new LIFOCDefinition.ListParameterDefinition<>(new ListParameter<>(Component.translatable("gui.loot_table_wand.condition.predicate.damage_source.tag"),
                    new LIFOCDefinition.InlineMultiParameterDefinition<>(List.of(
                            new LIFOCDefinition.ParameterDefinition<>(new BooleanParameter(null, null, Component.translatable("gui.loot_table_wand.condition.predicate.damage_source.tag.tag.true"), Component.translatable("gui.loot_table_wand.condition.predicate.damage_source.tag.tag.false")), tag -> tag.isExpected(), (tag, b) -> tag.setExpected(b)),
                            new LIFOCDefinition.ParameterDefinition<>(new TagParameter<>(null, null), TagPredicateAccessor::getTag, TagPredicateAccessor::setTag)),
            "gui.loot_table_wand.condition.predicate.damage_source.tag.tag")), predicate -> predicate.getTags().stream().map(tag -> (TagPredicateAccessor<DamageType>) tag).toList(), (predicate, list) -> predicate.setTags(list.stream().map(tag -> (TagPredicate<DamageType>) tag).toList()), null),
            new LIFOCDefinition.NullableMultiParameterDefinition<>(Component.translatable("gui.loot_table_wand.condition.predicate.damage_source.direct_entity"), PREDICATE_SIMPLIFIER, ENTITY_PREDICATE, (predicate) -> (EntityPredicateAccessor) predicate.getDirectEntity(), (EntityPredicateAccessor) EntityPredicate.ANY, true),
            new LIFOCDefinition.NullableMultiParameterDefinition<>(Component.translatable("gui.loot_table_wand.condition.predicate.damage_source.source_entity"), PREDICATE_SIMPLIFIER, ENTITY_PREDICATE, (predicate) -> (EntityPredicateAccessor) predicate.getSourceEntity(), (EntityPredicateAccessor) EntityPredicate.ANY, true)
    ));


    public static final ConditionDefinition<CompositeLootItemConditionAccessor> ALL_OF = new ConditionDefinition<>(CompositeLootItemConditionAccessor.class)
            .listDescriptionParameter("gui.loot_table_wand.condition.all_of", "gui.loot_table_wand.condition.all_of.inverted", null, null, null, null, new LIFOCDefinition.NestedConditionParameterDefinition<>(
                    new NestedConditionParameter(false), ListParameter.Holder::get, ListParameter.Holder::set, false
            ), ListParameter.wrapHolderFromArray(CompositeLootItemConditionAccessor::getTerms), ListParameter.unwrapHolderToArray(CompositeLootItemConditionAccessor::setTerms), null, true);

    public static final ConditionDefinition<CompositeLootItemConditionAccessor> ANY_OF = new ConditionDefinition<>(CompositeLootItemConditionAccessor.class)
            .listDescriptionParameter("gui.loot_table_wand.condition.any_of", "gui.loot_table_wand.condition.any_of.inverted", null, null, null, null, new LIFOCDefinition.NestedConditionParameterDefinition<>(
                    new NestedConditionParameter(false), ListParameter.Holder::get, ListParameter.Holder::set, false
            ), ListParameter.wrapHolderFromArray(CompositeLootItemConditionAccessor::getTerms), ListParameter.unwrapHolderToArray(CompositeLootItemConditionAccessor::setTerms), null, true);

    public static final ConditionDefinition<LootItemBlockStatePropertyConditionAccessor> BLOCK_STATE_PROPERTY = new ConditionDefinition<>(LootItemBlockStatePropertyConditionAccessor.class)
            .registryDescriptionParameter("gui.loot_table_wand.condition.block_state_property", "gui.loot_table_wand.condition.block_state_property.inverted", ForgeRegistries.BLOCKS, LootItemBlockStatePropertyConditionAccessor::getBlock, LootItemBlockStatePropertyConditionAccessor::setBlock)
            .nullableMultiParameter(null, PREDICATE_SIMPLIFIER, STATE_PROPERTIES_PREDICATE, (condition) -> (StatePropertiesPredicateAccessor) condition.getProperties(), (StatePropertiesPredicateAccessor) StatePropertiesPredicate.ANY, false);

    public static final ConditionDefinition<DamageSourceConditionAccessor> DAMAGE_SOURCE_PROPERTIES = new ConditionDefinition<>(DamageSourceConditionAccessor.class)
            .nullableMultiDescriptionParameter(Component.translatable("gui.loot_table_wand.condition.damage_source_properties"), Component.translatable("gui.loot_table_wand.condition.damage_source_properties.inverted"), PREDICATE_SIMPLIFIER, DAMAGE_SOURCE_PREDICATE, (condition) -> (DamageSourcePredicateAccessor) condition.getPredicate(), (DamageSourcePredicateAccessor) DamageSourcePredicate.ANY, false);
    public static final ConditionDefinition<LootItemEntityPropertyConditionAccessor> ENTITY_PROPERTIES = new ConditionDefinition<>(LootItemEntityPropertyConditionAccessor.class)
            .descriptionParameter((b) -> getLootContextEntity(b ? "gui.loot_table_wand.condition.entity_properties.inverted" : "gui.loot_table_wand.condition.entity_properties"), LootItemEntityPropertyConditionAccessor::getEntityTarget, LootItemEntityPropertyConditionAccessor::setEntityTarget)
            .nullableMultiParameter(null, PREDICATE_SIMPLIFIER, ENTITY_PREDICATE, (condition) -> (EntityPredicateAccessor) condition.getPredicate(), (EntityPredicateAccessor) EntityPredicate.ANY, false);

    public static final ConditionDefinition<EntityHasScoreConditionAccessor> ENTITY_SCORES = new ConditionDefinition<>(EntityHasScoreConditionAccessor.class)
            .descriptionParameter((b) -> getLootContextEntity(b ? "gui.loot_table_wand.condition.entity_scores.inverted" : "gui.loot_table_wand.condition.entity_scores"), EntityHasScoreConditionAccessor::getEntityTarget, EntityHasScoreConditionAccessor::setEntityTarget)
            .listParameter(new LIFOCDefinition.InlineMultiParameterDefinition<>(List.of(
                    new LIFOCDefinition.ParameterDefinition<>(new StringParameter(null, null), (holder) -> holder.get().getFirst(), (holder, string) -> holder.set(Pair.of(string, holder.get().getSecond()))),
                    new LIFOCDefinition.ParameterDefinition<>(new IntRangeParameter(null, null, false), (holder) -> (IntRangeAccessor) holder.get().getSecond(), (holder, range) -> holder.set(Pair.of(holder.get().getFirst(), (IntRange) range)))
            ), "gui.loot_table_wand.condition.entity_scores.score"), (condition) -> condition.getScores().entrySet().stream().map((entry) -> new ListParameter.Holder<>(Pair.of(entry.getKey(), entry.getValue()))).toList(), (condition, list) -> condition.setScores(list.stream().map(ListParameter.Holder::get).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))));

    public static final ConditionDefinition<InvertedLootItemConditionAccessor> INVERTED = new ConditionDefinition<>(InvertedLootItemConditionAccessor.class)
            .descriptionParameter((b) -> new LIFOCDefinition.NestedConditionParameterDefinition<>(new NestedConditionParameter(true), InvertedLootItemConditionAccessor::getTerm, InvertedLootItemConditionAccessor::setTerm, true));

    public static final ConditionDefinition<LocationCheckAccessor> LOCATION_CHECK = new ConditionDefinition<>(LocationCheckAccessor.class, Component.translatable("gui.loot_table_wand.condition.location_check"), Component.translatable("gui.loot_table_wand.condition.location_check.inverted"))
            .parameter(new IntParameter(0, "gui.loot_table_wand.condition.location_check.offset_x"), (condition) -> condition.getOffset().getX(), (condition, i) -> condition.setOffset(new BlockPos(i, condition.getOffset().getY(), condition.getOffset().getZ())))
            .parameter(new IntParameter(0, "gui.loot_table_wand.condition.location_check.offset_y"), (condition) -> condition.getOffset().getY(), (condition, i) -> condition.setOffset(condition.getOffset().atY(i)))
            .parameter(new IntParameter(0, "gui.loot_table_wand.condition.location_check.offset_z"), (condition) -> condition.getOffset().getZ(), (condition, i) -> condition.setOffset(new BlockPos(condition.getOffset().getX(), condition.getOffset().getY(), i)))
            .nullableMultiParameter(null, (DescriptionMerger<?>) null, LOCATION_PREDICATE, (condition) -> (LocationPredicateAccessor) condition.getPredicate(), (LocationPredicateAccessor) LocationPredicate.ANY, false);

    public static final ConditionDefinition<LootItemKilledByPlayerCondition> KILLED_BY_PLAYER = new ConditionDefinition<>(LootItemKilledByPlayerCondition.class, Component.translatable("gui.loot_table_wand.condition.killed_by_player"), Component.translatable("gui.loot_table_wand.condition.killed_by_player.inverted"));

    public static final ConditionDefinition<MatchToolAccessor> MATCH_TOOL = new ConditionDefinition<>(MatchToolAccessor.class)
            .nullableMultiDescriptionParameter(Component.translatable("gui.loot_table_wand.condition.match_tool"), Component.translatable("gui.loot_table_wand.condition.match_tool.inverted"), PREDICATE_SIMPLIFIER, ITEM_PREDICATE, (condition) -> (ItemPredicateAccessor) condition.getPredicate(), (ItemPredicateAccessor) ItemPredicate.ANY, false);

    public static final ConditionDefinition<LootItemRandomChanceConditionAccessor> RANDOM_CHANCE = new ConditionDefinition<>(LootItemRandomChanceConditionAccessor.class)
            .descriptionParameter((b) -> new FloatParameter(0.0F, b ? "gui.loot_table_wand.condition.random_chance.inverted" : "gui.loot_table_wand.condition.random_chance"), (condition) -> condition.getProbability() * 100, (condition, percent) -> condition.setProbability(percent / 100));

    public static final ConditionDefinition<LootItemRandomChanceWithLootingConditionAccessor> RANDOM_CHANCE_WITH_LOOTING = new ConditionDefinition<>(LootItemRandomChanceWithLootingConditionAccessor.class)
            .inlineMultiDescriptionParameter("gui.loot_table_wand.condition.random_chance_with_looting", "gui.loot_table_wand.condition.random_chance_with_looting.inverted", List.of(
                    new LIFOCDefinition.ParameterDefinition<>(new FloatParameter(0.0F, null), (condition) -> condition.getPercent() * 100, (condition, percent) -> condition.setPercent(percent / 100)),
                    new LIFOCDefinition.ParameterDefinition<>(new FloatParameter(0.0F, null), (condition) -> condition.getLootingMultiplier() * 100, (condition, percent) -> condition.setLootingMultiplier(percent / 100))));

    public static final ConditionDefinition<ConditionReferenceAccessor> REFERENCE = new ConditionDefinition<>(ConditionReferenceAccessor.class)
            .descriptionParameter((b) -> new StringParameter(null, b ? "gui.loot_table_wand.condition.reference.inverted" : "gui.loot_table_wand.condition.reference"), condition -> condition.getName().toString(), (condition, string) -> condition.setName(ResourceLocation.parse(string)));

    public static final ConditionDefinition<ExplosionCondition> SURVIVES_EXPLOSION = new ConditionDefinition<>(ExplosionCondition.class, Component.translatable("gui.loot_table_wand.condition.survives_explosion"), Component.translatable("gui.loot_table_wand.condition.survives_explosion.inverted"));

    public static final ConditionDefinition<BonusLevelTableConditionAccessor> TABLE_BONUS = new ConditionDefinition<>(BonusLevelTableConditionAccessor.class)
            .registryDescriptionParameter("gui.loot_table_wand.condition.table_bonus", "gui.loot_table_wand.condition.table_bonus.inverted", ForgeRegistries.ENCHANTMENTS, BonusLevelTableConditionAccessor::getEnchantment, BonusLevelTableConditionAccessor::setEnchantment)
            .listParameter(null, new LIFOCDefinition.ParameterDefinition<>(new FloatParameter(null, "gui.loot_table_wand.condition.table_bonus.value"), ListParameter.Holder::get, ListParameter.Holder::set), ListParameter.wrapHolderFromFloatArrayTimes100(BonusLevelTableConditionAccessor::getValues), ListParameter.unwrapHolderToFloatArrayOver100(BonusLevelTableConditionAccessor::setValues), null, (index) -> index == 0 ? Component.translatable("gui.loot_table_wand.condition.table_bonus.value.level_0").getString() : Component.translatable("gui.loot_table_wand.condition.table_bonus.value.level", ScreenUtils.formatInt(index, true)).getString());

    public static final ConditionDefinition<TimeCheckAccessor> TIME_CHECK = new ConditionDefinition<>(TimeCheckAccessor.class)
            .descriptionParameter(b -> new IntRangeParameter(null, b ? "gui.loot_table_wand.condition.time_check.inverted" : "gui.loot_table_wand.condition.time_check", false), (condition) -> (IntRangeAccessor) condition.getValue(), (condition, value) -> condition.setValue((IntRange) value))
            .parameter(new LongParameter(null, "gui.loot_table_wand.condition.time_check.period"), TimeCheckAccessor::getPeriod, TimeCheckAccessor::setPeriod);

    public static final ConditionDefinition<WeatherCheckAccessor> WEATHER_CHECK = new ConditionDefinition<>(WeatherCheckAccessor.class)
            .descriptionParameter(b -> new EnumParameter<>(WeatherState.class, WeatherState.ALWAYS, null, state -> state.getDescription(b)), condition -> WeatherState.fromBooleans(condition.getIsRaining(), condition.getIsThundering()), (condition, weatherState) -> {
                condition.setIsRaining(weatherState.requiresRain());
                condition.setIsThundering(weatherState.requiresThunder());
            });

    public static final ConditionDefinition<ValueCheckConditionAccessor> VALUE_CHECK = new ConditionDefinition<>(ValueCheckConditionAccessor.class)
            .inlineMultiDescriptionParameter("gui.loot_table_wand.condition.value_check", "gui.loot_table_wand.condition.value_check.inverted", List.of(
                    new LIFOCDefinition.ParameterDefinition<>(new NumberProviderParameter(null, null, false), condition -> NumberProvider.fromVanilla(condition.getProvider()), (condition, provider) -> condition.setProvider(provider.toVanilla())),
                    new LIFOCDefinition.ParameterDefinition<>(new IntRangeParameter(null, null, false), condition -> (IntRangeAccessor) condition.getRange(), (condition, range) -> condition.setRange((IntRange) range))));


    // FORGE CONDITIONS
    public static final ConditionDefinition<LootTableIdConditionAccessor> FORGE_LOOT_TABLE_ID = new ConditionDefinition<>(LootTableIdConditionAccessor.class)
            .descriptionParameter(b -> new StringParameter(null, b ? "gui.loot_table_wand.condition.forge.loot_table_id.inverted" : "gui.loot_table_wand.condition.forge.loot_table_id"), (condition) -> condition.getTargetLootTableId().toString(), (condition, string) -> condition.setTargetLootTableId(ResourceLocation.parse(string)));

    public static final ConditionDefinition<CanToolPerformActionAccessor> FORGE_CAN_TOOL_PERFORM_ACTION = new ConditionDefinition<>(CanToolPerformActionAccessor.class)
            .descriptionParameter(b -> new StringParameter(null, b ? "gui.loot_table_wand.condition.forge.can_tool_perform_action.inverted" : "gui.loot_table_wand.condition.forge.can_tool_perform_action"), (condition) -> condition.getAction().name(), (condition, string) -> condition.setAction(ToolAction.get(string)));

    static {
        entityPredicate = ENTITY_PREDICATE;
        define("all_of", ALL_OF);
        define("any_of", ANY_OF);
        define("block_state_property", BLOCK_STATE_PROPERTY);
        define("damage_source_properties", DAMAGE_SOURCE_PROPERTIES);
        define("entity_properties", ENTITY_PROPERTIES);
        define("entity_scores", ENTITY_SCORES);
        define("inverted", INVERTED);
        define("location_check", LOCATION_CHECK);
        define("killed_by_player", KILLED_BY_PLAYER);
        define("match_tool", MATCH_TOOL);
        define("random_chance", RANDOM_CHANCE);
        define("random_chance_with_looting", RANDOM_CHANCE_WITH_LOOTING);
        define("reference", REFERENCE);
        define("survives_explosion", SURVIVES_EXPLOSION);
        define("table_bonus", TABLE_BONUS);
        define("time_check", TIME_CHECK);
        define("weather_check", WEATHER_CHECK);
        define("value_check", VALUE_CHECK);
        // FORGE CONDITIONS
        define("forge", "loot_table_id", FORGE_LOOT_TABLE_ID);
        define("forge", "can_tool_perform_action", FORGE_CAN_TOOL_PERFORM_ACTION);
    }

    public static void define(String path, ConditionDefinition<?> definition) {
        definitions.put(ResourceLocation.fromNamespaceAndPath("minecraft", path), definition);
    }

    public static void define(String namespace, String path, ConditionDefinition<?> definition) {
        definitions.put(ResourceLocation.fromNamespaceAndPath(namespace, path), definition);
    }

    public static ConditionDefinition<?> get(ResourceLocation id) {
        return definitions.get(id);
    }

    public enum WeatherState {
        CLEAR(Component.translatable("gui.loot_table_wand.condition.weather_check.clear"), Component.translatable("gui.loot_table_wand.condition.weather_check.rain_thunder"), false, null),
        RAIN(Component.translatable("gui.loot_table_wand.condition.weather_check.rain"), Component.translatable("gui.loot_table_wand.condition.weather_check.clear_thunder"), true, false),
        THUNDER(Component.translatable("gui.loot_table_wand.condition.weather_check.thunder"), Component.translatable("gui.loot_table_wand.condition.weather_check.clear_rain"), null, true),
        CLEAR_RAIN(Component.translatable("gui.loot_table_wand.condition.weather_check.clear_rain"), Component.translatable("gui.loot_table_wand.condition.weather_check.thunder"), null, false),
        RAIN_THUNDER(Component.translatable("gui.loot_table_wand.condition.weather_check.rain_thunder"), Component.translatable("gui.loot_table_wand.condition.weather_check.clear"), true, null),
        NEVER(Component.translatable("gui.loot_table_wand.condition.false"), Component.translatable("gui.loot_table_wand.condition.true"), false, true),
        ALWAYS(Component.translatable("gui.loot_table_wand.condition.true"), Component.translatable("gui.loot_table_wand.condition.false"), null, null);

        final Component description;
        final Component invertedDescription;
        final Boolean requiresRain;
        final Boolean requiresThunder;

        WeatherState(Component description, Component invertedDescription, Boolean requiresRain, Boolean requiresThunder) {
            this.description = description;
            this.invertedDescription = invertedDescription;
            this.requiresRain = requiresRain;
            this.requiresThunder = requiresThunder;
        }

        Component getDescription(boolean inverted) {
            return inverted ? invertedDescription : description;
        }

        boolean requiresRain() {
            return requiresRain;
        }

        boolean requiresThunder() {
            return requiresThunder;
        }

        static WeatherState fromBooleans(Boolean raining, Boolean thundering) {
            if (raining != null) {
                if (thundering != null) {
                    return raining ? (thundering ? WeatherState.THUNDER : WeatherState.RAIN) : (thundering ? WeatherState.NEVER : WeatherState.CLEAR);
                } else {
                    return raining ? WeatherState.RAIN_THUNDER : WeatherState.CLEAR;
                }
            } else {
                if (thundering != null) {
                    return thundering ? WeatherState.THUNDER : WeatherState.CLEAR_RAIN;
                } else {
                    return WeatherState.ALWAYS;
                }
            }
        }
    }
}