package net.mesomods.lootwand.mixin.loot.condition.predicate;

import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(targets = "net.minecraft.advancements.critereon.EntityTypePredicate$TypePredicate")
public interface TypePredicateAccessor {
    @Accessor
    EntityType<?> getType();

    @Mutable
    @Accessor
    void setType(EntityType<?> type);
}
