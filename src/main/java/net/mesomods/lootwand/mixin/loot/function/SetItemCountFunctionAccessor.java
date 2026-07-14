package net.mesomods.lootwand.mixin.loot.function;

import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.functions.SetItemCountFunction.class)
public interface SetItemCountFunctionAccessor {
    @Accessor
    NumberProvider getValue();

    @Mutable
    @Accessor
    void setValue(NumberProvider value);

    @Accessor
    boolean isAdd();

    @Mutable
    @Accessor
    void setAdd(boolean add);
}
