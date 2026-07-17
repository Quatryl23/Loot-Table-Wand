package net.mesomods.lootwand.mixin.loot.condition.predicate;

import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(EntityTypePredicate.TypePredicate.class)
public interface TypePredicateAccessor {
    @Accessor
    EntityType<?> getType();

    @Mutable
    @Accessor
    void setType(EntityType<?> type);
}
