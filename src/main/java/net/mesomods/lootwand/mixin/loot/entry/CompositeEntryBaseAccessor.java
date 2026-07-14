package net.mesomods.lootwand.mixin.loot.entry;

import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.entries.CompositeEntryBase.class)
public interface CompositeEntryBaseAccessor extends LootPoolEntryContainerAccessor {
    @Accessor
    LootPoolEntryContainer[] getChildren();

    @Mutable
    @Accessor
    void setChildren(LootPoolEntryContainer[] children);
}
