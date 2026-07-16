package net.mesomods.lootwand.mixin.gui;

import net.mesomods.lootwand.client.tooltip.AdvancedTooltipGuiGraphics;
import net.mesomods.lootwand.client.tooltip.AdvancedTooltipScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
public abstract class ScreenMixin implements AdvancedTooltipScreen {
    @Shadow @Nullable protected Minecraft minecraft;
    @Unique boolean lootmod$skipNextTooltipGap = false;

    @Shadow
    public abstract void setTooltipForNextRenderPass(List<FormattedCharSequence> p_262939_, ClientTooltipPositioner p_263078_, boolean p_263107_);

    @Inject(method = "setTooltipForNextRenderPass(Lnet/minecraft/client/gui/components/Tooltip;Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;Z)V", at = @At("TAIL"))
    public void setTooltipForNextRenderPass(CallbackInfo ci) {
        this.lootmod$skipNextTooltipGap = false;
    }

    @Unique
    @Override
    public void lootmod$setNoGapTooltipForNextRenderPass(Tooltip tooltip, ClientTooltipPositioner positioner) {
        this.setTooltipForNextRenderPass(tooltip.toCharSequence(this.minecraft), positioner, true);
        this.lootmod$skipNextTooltipGap = true;
    }

    @Inject(method = "renderWithTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", shift = At.Shift.AFTER))
    public void disableTooltipGap(GuiGraphics graphics, int i1, int i2, float f, CallbackInfo ci) {
        if (lootmod$skipNextTooltipGap && graphics instanceof AdvancedTooltipGuiGraphics guiGraphics) {
            guiGraphics.lootmod$setTooltipGap(false);
        }
    }

    @Inject(method = "renderWithTooltip", at = @At(value = "TAIL"))
    public void enableTooltipGap(GuiGraphics graphics, int i1, int i2, float f, CallbackInfo ci) {
        if (graphics instanceof AdvancedTooltipGuiGraphics guiGraphics) {
            guiGraphics.lootmod$setTooltipGap(true);
        }
    }
}
