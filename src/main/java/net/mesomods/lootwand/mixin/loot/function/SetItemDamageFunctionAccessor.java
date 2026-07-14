package net.mesomods.lootwand.mixin.loot.function;

import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction.class)
public interface SetItemDamageFunctionAccessor {
    @Accessor
    boolean isAdd();

    @Accessor
    void setAdd(boolean add);

    @Accessor
    NumberProvider getDamage();

    @Accessor
    void setDamage(NumberProvider damage);
}
