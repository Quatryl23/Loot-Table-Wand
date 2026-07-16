package net.mesomods.lootwand.container;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Unique;

public interface RandomizableContainerBlockEntityAccessor {
    ResourceLocation getLootTable();

    @Unique
    void lootmod$onLoad();

    long getLootTableSeed();
}
