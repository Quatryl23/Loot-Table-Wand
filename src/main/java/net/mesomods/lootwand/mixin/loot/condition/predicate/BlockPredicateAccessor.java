package net.mesomods.lootwand.mixin.loot.condition.predicate;

import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.BlockPredicate.class)
public interface BlockPredicateAccessor {
    @Accessor
    TagKey<Block> getTag();

    @Mutable
    @Accessor
    void setTag(TagKey<Block> tag);

    @Accessor
    Set<Block> getBlocks();

    @Mutable
    @Accessor
    void setBlocks(Set<Block> blocks);

    @Accessor
    StatePropertiesPredicate getProperties();

    @Mutable
    @Accessor
    void setProperties(StatePropertiesPredicate properties);

    @Accessor
    NbtPredicate getNbt();

    @Mutable
    @Accessor
    void setNbt(NbtPredicate nbt);
}
