package net.mesomods.lootwand.client.tooltip;

import org.spongepowered.asm.mixin.Unique;

public interface AdvancedTooltipAbstractWidget {
    @Unique
    void lootmod$enableAdvancedTooltips();
}
