package net.mesomods.lootwand.mixin.loot.function;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.functions.SetLoreFunction.class)
public interface SetLoreFunctionAccessor {
    @Accessor
    boolean isReplace();

    @Mutable
    @Accessor
    void setReplace(boolean replace);

    @Accessor
    List<Component> getLore();

    @Mutable
    @Accessor
    void setLore(List<Component> lore);

    @Accessor
    LootContext.EntityTarget getResolutionContext();

    @Mutable
    @Accessor
    void setResolutionContext(LootContext.EntityTarget resolutionContext);
}
