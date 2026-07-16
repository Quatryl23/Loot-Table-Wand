package net.mesomods.lootwand.client.gui.loottable.lifoc.parameters;

import net.minecraft.network.chat.Component;

import java.util.function.BiFunction;

public interface DescriptionMerger<T extends Prefixable<?>> extends BiFunction<Component, T, T> {
    DescriptionMerger<Prefixable<?>> APPEND = (component, prefixable) -> prefixable == null || component == null ? null : prefixable.withPrefix(component.copy(), false);
    DescriptionMerger<Prefixable<?>> APPEND_LOWERCASE = (component, prefixable) -> prefixable == null || component == null ? null : prefixable.withPrefix(component.copy(), true);
    DescriptionMerger<Prefixable<?>> UNDO = (component, prefixable) -> prefixable == null ? null : prefixable.withoutPrefix();

    static DescriptionMerger<Prefixable<?>> replaceDescription(String newDescription) {
        return (component, prefixable) -> prefixable.withNewDescription(newDescription, false);
    }

    static DescriptionMerger<Prefixable<?>> replaceDescriptionLowercase(String newDescription) {
        return (component, prefixable) -> prefixable.withNewDescription(newDescription, true);
    }
}
