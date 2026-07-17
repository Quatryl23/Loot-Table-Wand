package net.mesomods.lootwand.mixin.gui;

import net.mesomods.lootwand.ModItems;
import net.mesomods.lootwand.client.gui.LootTableWandDetailsOverlay;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow
    private int toolHighlightTimer;

    @Inject(method = "tick()V", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/gui/Gui;toolHighlightTimer:I", ordinal = 1, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void disableLootTableWandHighlighting(CallbackInfo ci, Entity entity, ItemStack itemStack) {
        if (itemStack.is(ModItems.LOOT_TABLE_WAND))
            toolHighlightTimer = 0;
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lnet/minecraft/client/gui/GuiGraphics;III)V"))
    public void renderOverlayBeforeChat(GuiGraphics graphics, float f, CallbackInfo ci) {
        LootTableWandDetailsOverlay.render(graphics, graphics.guiWidth(), graphics.guiHeight());
    }
}
