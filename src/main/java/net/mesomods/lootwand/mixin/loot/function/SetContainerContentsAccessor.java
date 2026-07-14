package net.mesomods.lootwand.mixin.loot.function;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.functions.SetContainerContents.class)
public interface SetContainerContentsAccessor {
    @Accessor
    List<LootPoolEntryContainer> getEntries();

    @Mutable
    @Accessor
    void setEntries(List<LootPoolEntryContainer> entries);

    @Accessor
    BlockEntityType<?> getType();

    @Mutable
    @Accessor
    void setType(BlockEntityType<?> type);
}
