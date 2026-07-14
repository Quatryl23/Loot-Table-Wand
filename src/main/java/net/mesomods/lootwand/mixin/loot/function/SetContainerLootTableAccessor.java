package net.mesomods.lootwand.mixin.loot.function;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.functions.SetContainerLootTable.class)
public interface SetContainerLootTableAccessor {
    @Accessor
    ResourceLocation getName();

    @Mutable
    @Accessor
    void setName(ResourceLocation name);

    @Accessor
    long getSeed();

    @Mutable
    @Accessor
    void setSeed(long seed);

    @Accessor
    BlockEntityType<?> getType();

    @Mutable
    @Accessor
    void setType(BlockEntityType<?> type);
}
