package net.mesomods.lootwand.mixin.loot.condition.predicate;

import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.NbtPredicate.class)
public interface NbtPredicateAccessor {
    @Accessor
    CompoundTag getTag();

    @Mutable
    @Accessor
    void setTag(CompoundTag tag);
}
