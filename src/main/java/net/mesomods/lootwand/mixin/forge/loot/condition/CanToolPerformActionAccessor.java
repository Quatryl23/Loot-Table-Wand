package net.mesomods.lootwand.mixin.forge.loot.condition;

import net.minecraftforge.common.ToolAction;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(value = net.minecraftforge.common.loot.CanToolPerformAction.class, remap = false)
public interface CanToolPerformActionAccessor {
    @Accessor
    ToolAction getAction();

    @Mutable
    @Accessor
    void setAction(ToolAction action);
}
