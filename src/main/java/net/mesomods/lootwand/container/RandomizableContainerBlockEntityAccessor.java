package net.mesomods.lootwand.container;

import net.minecraft.resources.ResourceLocation;

public interface RandomizableContainerBlockEntityAccessor {
    ResourceLocation getLootTable();
    long getLootTableSeed();
}
