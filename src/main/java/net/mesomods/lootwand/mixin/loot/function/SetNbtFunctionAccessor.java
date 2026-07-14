package net.mesomods.lootwand.mixin.loot.function;

import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.functions.SetNbtFunction.class)
public interface SetNbtFunctionAccessor {
    @Accessor
    CompoundTag getTag();

    @Mutable
    @Accessor
    void setTag(CompoundTag tag);
}
