package net.mesomods.lootwand.mixin.gui;

import net.mesomods.lootwand.client.tooltip.AdvancedTooltipAbstractWidget;
import net.mesomods.lootwand.client.tooltip.AdvancedTooltipScreen;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(net.minecraft.client.gui.components.AbstractWidget.class)
public abstract class AbstractWidgetMixin implements AdvancedTooltipAbstractWidget {
    @Unique
    boolean lootmod$advancedTooltips = false;

    @Redirect(method = "updateTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;setTooltipForNextRenderPass(Lnet/minecraft/client/gui/components/Tooltip;Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;Z)V"))
    private void redirectToBetterTooltip(Screen screen, Tooltip tooltip, ClientTooltipPositioner positioner, boolean isFocused) {
        if (this.lootmod$advancedTooltips && screen instanceof AdvancedTooltipScreen a) {
            a.lootmod$setNoGapTooltipForNextRenderPass(tooltip, positioner);
        } else {
            screen.setTooltipForNextRenderPass(tooltip, positioner, isFocused);
        }
    }

    @Unique
    @Override
    public void lootmod$enableAdvancedTooltips() {
        this.lootmod$advancedTooltips = true;
    }
}
