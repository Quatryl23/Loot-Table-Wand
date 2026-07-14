package net.mesomods.lootwand.client.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Optional;

public interface AdvancedTooltipGuiGraphics {

    @Unique
    void lootmod$renderNumberProviderTooltip(Font font, List<Component> components, Optional<TooltipComponent> tooltip, int x, int y, ClientTooltipPositioner positioner);

    @Unique
    void lootmod$setTooltipGap(boolean enabled);
}
