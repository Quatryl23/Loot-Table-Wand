package net.mesomods.lootwand.mixin.loot.condition.predicate;

import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(targets = "net.minecraft.advancements.critereon.StatePropertiesPredicate$ExactPropertyMatcher")
public interface ExactPropertyMatcherAccessor {
    @Accessor
    String getValue();

    @Mutable
    @Accessor
    void setValue(String value);
}
