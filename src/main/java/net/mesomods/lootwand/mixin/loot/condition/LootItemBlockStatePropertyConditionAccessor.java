package net.mesomods.lootwand.mixin.loot.condition;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition.class)
public interface LootItemBlockStatePropertyConditionAccessor {
    @Accessor
    Block getBlock();

    @Mutable
    @Accessor
    void setBlock(Block block);

    @Accessor
    StatePropertiesPredicate getProperties();

    @Mutable
    @Accessor
    void setProperties(StatePropertiesPredicate properties);
}
