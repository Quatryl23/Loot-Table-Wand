package net.mesomods.lootwand.client.tooltip;

import org.spongepowered.asm.mixin.Unique;

public interface AdvancedTooltipAbstractWidget {
    @Unique
    void lootmod$setStringColor(int stringColor);

    @Unique
    int lootmod$getStringColor();

    @Unique
    void lootmod$setHeight(int height);

    @Unique
    void lootmod$enableAdvancedTooltips();
}
