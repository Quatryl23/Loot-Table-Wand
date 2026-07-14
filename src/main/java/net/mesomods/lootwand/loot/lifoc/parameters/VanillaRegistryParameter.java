package net.mesomods.lootwand.loot.lifoc.parameters;

import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.StatType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class VanillaRegistryParameter<T extends Registry<V>, V> extends SimpleParameter<V> {
    protected final T registry;

    public VanillaRegistryParameter(@Nullable V defaultValue, String description, T registry) {
        this(defaultValue, description, registry, (v) -> Component.literal(registry.getKey(v).toString()));
    }

    public VanillaRegistryParameter(@Nullable V defaultValue, String description, T registry, Function<V, Component> toComponent) {
        super(defaultValue, UnaryOperator.identity(), new Description<>(description, toComponent));
        this.registry = registry;
    }
    protected VanillaRegistryParameter(@Nullable V defaultValue, UnaryOperator<V> validator, Description<V> description, T registry, Supplier<V> supplier, Consumer<V> onValueChange) {
        super(defaultValue, validator, description, supplier, onValueChange);
        this.registry = registry;
    }
    @Override
    public VanillaRegistryParameter<T, V> build(Supplier<V> supplier, Consumer<V> valueSaver) {
        return new VanillaRegistryParameter<>(defaultValue, validator, description, registry, supplier, valueSaver);
    }

    public static <V> Component getTypeDescription(V value, Function<V, Component> fallback) {
        if (value instanceof Enchantment enchantment) {
            return Component.translatable(enchantment.getDescriptionId());
        } else if (value instanceof EntityType<?> entityType) {
            return entityType.getDescription();
        } else if (value instanceof Block block) {
            return block.getName();
        } else if (value instanceof Item item) {
            return item.getDescription();
        } else if (value instanceof Fluid fluid) {
            return fluid.getFluidType().getDescription();
        } else if (value instanceof MobEffect effect) {
            return Component.translatable(effect.getDescriptionId());
        } else if (value instanceof Potion potion) {
            return Component.translatable(potion.getName(""));
        } else if (value instanceof Attribute attribute) {
            return Component.translatable(attribute.getDescriptionId());
        } else if (value instanceof StatType<?> statType) {
            return statType.getDisplayName();
        } else {
            return fallback.apply(value);
        }
    }
}
