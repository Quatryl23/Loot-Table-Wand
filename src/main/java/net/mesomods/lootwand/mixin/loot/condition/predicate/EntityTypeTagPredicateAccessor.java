package net.mesomods.lootwand.mixin.loot.condition.predicate;

import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(EntityTypePredicate.TagPredicate.class)
public interface EntityTypeTagPredicateAccessor {
    @Accessor
    TagKey<EntityType<?>> getTag();

    @Mutable
    @Accessor
    void setTag(TagKey<EntityType<?>> tag);
}
