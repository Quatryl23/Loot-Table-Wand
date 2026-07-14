package net.mesomods.lootwand.mixin.loot.condition.predicate;

import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.TagPredicate.class)
public interface TagPredicateAccessor<T> {
    @Accessor
    TagKey<T> getTag();

    @Mutable
    @Accessor
    void setTag(TagKey<T> tag);

    @Accessor
    boolean isExpected();

    @Mutable
    @Accessor
    void setExpected(boolean expected);
}
