package net.mesomods.lootwand.mixin.forge.loot.condition;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(value = net.minecraftforge.common.loot.LootTableIdCondition.class, remap = false)
public interface LootTableIdConditionAccessor {
    @Accessor
    ResourceLocation getTargetLootTableId();

    @Mutable
    @Accessor
    void setTargetLootTableId(ResourceLocation targetLootTableId);
}
