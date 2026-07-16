package net.mesomods.lootwand.client.gui.loottable.lifoc.parameters;

import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ForgeRegistryParameter<T extends IForgeRegistry<V>, V> extends SimpleParameter<V> {
    protected final T registry;

    public ForgeRegistryParameter(@Nullable V defaultValue, String description, T registry) {
        this(defaultValue, description, registry, (v) -> VanillaRegistryParameter.getTypeDescription(v, value -> Component.literal(registry.getKey(value).toString())));
    }

    public ForgeRegistryParameter(@Nullable V defaultValue, String description, T registry, Function<V, Component> toComponent) {
        super(defaultValue, UnaryOperator.identity(), new Description<>(description, toComponent));
        this.registry = registry;
    }
    protected ForgeRegistryParameter(@Nullable V defaultValue, UnaryOperator<V> validator, Description<V> description, T registry, Supplier<V> supplier, Consumer<V> onValueChange) {
        super(defaultValue, validator, description, supplier, onValueChange);
        this.registry = registry;
    }
    @Override
    public ForgeRegistryParameter<T, V> build(Supplier<V> supplier, Consumer<V> valueSaver) {
        return new ForgeRegistryParameter<>(defaultValue, validator, description, registry, supplier, valueSaver);
    }
}
