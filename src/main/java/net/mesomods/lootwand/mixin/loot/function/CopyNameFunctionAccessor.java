package net.mesomods.lootwand.mixin.loot.function;

import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CopyNameFunction.class)
public interface CopyNameFunctionAccessor {
    @Accessor
    CopyNameFunction.NameSource getSource();
    @Accessor
    void setSource(CopyNameFunction.NameSource source);
}
