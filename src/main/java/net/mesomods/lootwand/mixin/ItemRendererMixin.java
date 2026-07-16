package net.mesomods.lootwand.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mesomods.lootwand.ModItems;
import net.mesomods.lootwand.client.LootTableWandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"))
    public void afterItemRender(ItemStack stack, ItemDisplayContext context, boolean p_115146_, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel p_115151_, CallbackInfo ci) {
        if (stack.is(ModItems.LOOT_TABLE_WAND)) {
            LootTableWandRenderer.INSTANCE.render(stack, context, poseStack, bufferSource, combinedLight, combinedOverlay);
        }
    }
}
