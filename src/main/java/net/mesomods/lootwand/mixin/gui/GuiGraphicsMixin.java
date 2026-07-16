package net.mesomods.lootwand.mixin.gui;

import net.mesomods.lootwand.client.tooltip.AdvancedTooltipGuiGraphics;
import net.mesomods.lootwand.client.tooltip.LoadingProbabilityChartTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin implements AdvancedTooltipGuiGraphics {
    @Unique
    boolean lootmod$isFirstTooltipComponent;
    @Unique
    boolean lootmod$tooltipGap = true;

    @Shadow
    protected abstract void renderTooltipInternal(Font font, List<ClientTooltipComponent> list, int i, int j, ClientTooltipPositioner clientTooltipPositioner);

    @Unique
    @Override
    public void lootmod$renderNumberProviderTooltip(Font font, List<Component> components, Optional<TooltipComponent> tooltip, int x, int y, ClientTooltipPositioner positioner) {
        if (tooltip.isEmpty()) {
            tooltip = Optional.of(new LoadingProbabilityChartTooltip());
        }
        List<ClientTooltipComponent> list = components.stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).collect(Collectors.toList());
        tooltip.ifPresent((c) -> list.add(1, ClientTooltipComponent.create(c)));
        this.renderTooltipInternal(font, list, x, y, positioner);
    }

    @Unique
    @Override
    public void lootmod$setTooltipGap(boolean enabled) {
        this.lootmod$tooltipGap = enabled;
    }

    @ModifyVariable(method = "renderTooltipInternal", at = @At(value = "STORE", ordinal = 0), ordinal = 3, name = "j")
    public int correctHeight(int i) {
        return this.lootmod$tooltipGap ? i : -2;
    }

    @ModifyVariable(method = "renderTooltipInternal", at = @At(value = "STORE", ordinal = 0), ordinal = 9, name = "k1")
    public int prepareSkipTooltipGap(int i) {
        this.lootmod$isFirstTooltipComponent = true;
        return i;
    }

    @ModifyVariable(method = "renderTooltipInternal", at = @At(value = "STORE", ordinal = 2), ordinal = 9, name = "k1")
    public int prepareSkipTooltipGap2(int i) {
        this.lootmod$isFirstTooltipComponent = true;
        return i;
    }

    @ModifyVariable(method = "renderTooltipInternal", at = @At(value = "STORE", ordinal = 1), ordinal = 9, name = "k1")
    public int skipTooltipGap(int i) {
        if (lootmod$isFirstTooltipComponent) {
            if (!this.lootmod$tooltipGap) {
                i -= 2;
            }
            this.lootmod$isFirstTooltipComponent = false;
        }
        return i;
    }

    @ModifyVariable(method = "renderTooltipInternal", at = @At(value = "STORE", ordinal = 3), ordinal = 9)
    public int skipTooltipGap2(int i) {
        if (lootmod$isFirstTooltipComponent) {
            if (!this.lootmod$tooltipGap) {
                i -= 2;
            }
            this.lootmod$isFirstTooltipComponent = false;
        }
        return i;
    }
}
