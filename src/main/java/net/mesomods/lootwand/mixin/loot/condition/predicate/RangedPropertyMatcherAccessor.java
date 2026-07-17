package net.mesomods.lootwand.mixin.loot.condition.predicate;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(StatePropertiesPredicate.RangedPropertyMatcher.class)
public interface RangedPropertyMatcherAccessor {
    @Accessor
    String getMinValue();

    @Mutable
    @Accessor
    void setMinValue(String minValue);

    @Accessor
    String getMaxValue();

    @Mutable
    @Accessor
    void setMaxValue(String maxValue);
}
