package net.mesomods.lootwand.client.tooltip;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.spongepowered.asm.mixin.Unique;

public interface AdvancedTooltipScreen {
    @Unique
    void lootmod$setNoGapTooltipForNextRenderPass(Tooltip tooltip, ClientTooltipPositioner positioner);
}
