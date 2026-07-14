package net.mesomods.lootwand.mixin.loot.condition.predicate;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.FluidPredicate.class)
public interface FluidPredicateAccessor {
    @Accessor
    TagKey<Fluid> getTag();

    @Mutable
    @Accessor
    void setTag(TagKey<Fluid> tag);

    @Accessor
    Fluid getFluid();

    @Mutable
    @Accessor
    void setFluid(Fluid fluid);

    @Accessor
    StatePropertiesPredicate getProperties();

    @Mutable
    @Accessor
    void setProperties(StatePropertiesPredicate properties);
}
