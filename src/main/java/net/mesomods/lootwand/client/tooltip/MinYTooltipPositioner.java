package net.mesomods.lootwand.client.tooltip;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public class MinYTooltipPositioner implements ClientTooltipPositioner {
    int minY;

    public MinYTooltipPositioner(int minY) {
        this.minY = minY;
    }

    @Override
    public Vector2ic positionTooltip(int i1, int i2, int i3, int i4, int i5, int i6) {
        Vector2ic defaultVector = DefaultTooltipPositioner.INSTANCE.positionTooltip(i1, i2, i3, i4, i5, i6);
        if (defaultVector.y() < this.minY) {
            return new Vector2i(defaultVector.x(), this.minY);
        }
        return defaultVector;
    }
}
