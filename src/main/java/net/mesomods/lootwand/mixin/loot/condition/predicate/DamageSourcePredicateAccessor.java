package net.mesomods.lootwand.mixin.loot.condition.predicate;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.world.damagesource.DamageType;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.DamageSourcePredicate.class)
public interface DamageSourcePredicateAccessor {
    @Accessor
    List<TagPredicate<DamageType>> getTags();

    @Mutable
    @Accessor
    void setTags(List<TagPredicate<DamageType>> tags);

    @Accessor
    EntityPredicate getDirectEntity();

    @Mutable
    @Accessor
    void setDirectEntity(EntityPredicate directEntity);

    @Accessor
    EntityPredicate getSourceEntity();

    @Mutable
    @Accessor
    void setSourceEntity(EntityPredicate sourceEntity);
}
