package net.mesomods.lootwand.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.mesomods.lootwand.attachments.LootTableWandPlayerData;
import net.mesomods.lootwand.item.LootTableWandItem;
import net.mesomods.lootwand.network.LootTableNetwork;
import net.mesomods.lootwand.network.packet.client.UpdateLootTableWandPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class LootTableWandRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    public static final LootTableWandRenderer INSTANCE = new LootTableWandRenderer();

    private LootTableWandRenderer() {
    }

    // -2 : Preview entirely disabled
    // -1 : Preview only a fixed item (no rotation)
    // >0 : Time in ticks a full cycle takes
    public static int PREVIEW_CYCLE_TIME = LootTableWandPlayerData.PREVIEW_TIME_DEFAULT;

    @Override
    public void render(ItemStack itemStack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        if (PREVIEW_CYCLE_TIME == -1) return;
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) return;
        ItemRenderer renderer = mc.getItemRenderer();
        this.renderPreviewItem(renderer, this.getPreviewItem(itemStack, level.getGameTime()), context, poseStack, bufferSource, combinedLight, combinedOverlay, level);
    }

    @Nullable
    public Item getPreviewItem(ItemStack stack, long time) {
        Minecraft mc = Minecraft.getInstance();
        int timeInCycle = (int) time % PREVIEW_CYCLE_TIME;
        int timeInCyclePerMillion = (PREVIEW_CYCLE_TIME == -2) ?  999_999 : Math.round(timeInCycle * 1_000_000F / (float) PREVIEW_CYCLE_TIME);
        CompoundTag nbt = stack.getOrCreateTag();
        if (LootTableWandItem.isInDeletionMode(stack)) {
            return Items.BARRIER;
        }
        if (!nbt.contains("PreviewTimes") || !nbt.contains("PreviewItems")) {
            return null;
        }
        // Check nbt for preview data (rendered item, time until next item)
        if (nbt.contains("PreviewTimeStart") && nbt.contains("PreviewTimeEnd") && nbt.contains("PreviewItem") && timeInCyclePerMillion > nbt.getInt("PreviewTimeStart") && timeInCyclePerMillion < nbt.getInt("PreviewTimeEnd")) {
            return BuiltInRegistries.ITEM.get(new ResourceLocation(nbt.getString("PreviewItem")));
        } else {
            int[] previewTimes = nbt.getIntArray("PreviewTimes");
            for (int i = 0; i < previewTimes.length; i++) {
                if (timeInCyclePerMillion < previewTimes[i]) {
                    int startTime = i == 0 ? 0 : previewTimes[i - 1];
                    int endTime = previewTimes[i];
                    nbt.put("PreviewTimeStart", IntTag.valueOf(startTime));
                    nbt.put("PreviewTimeEnd", IntTag.valueOf(endTime));
                    String previewItemString = nbt.getList("PreviewItems", Tag.TAG_STRING).getString(i);
                    nbt.put("PreviewItem", StringTag.valueOf(previewItemString));
                    if (mc.player != null && mc.player.getInventory().items.contains(stack)) {
                        LootTableNetwork.sendToServer(UpdateLootTableWandPacket.updatePreview(mc.player.getInventory().items.indexOf(stack), startTime, endTime, previewItemString));
                    }
                    return BuiltInRegistries.ITEM.get(new ResourceLocation(previewItemString));
                }
            }
        }
        return null;
    }

    public void renderPreviewItem(ItemRenderer renderer, Item item, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, Level level) {
        if (item == null) return;
        poseStack.pushPose();
        if (context == ItemDisplayContext.GUI) {
            poseStack.translate(0.75, 0.25, 0);
            poseStack.scale(0.45F, 0.45F, 1.2F);
        } else {
            poseStack.translate(10.5F / 16F, 10.5F / 16F, 0.5);
            poseStack.scale(0.4F, 0.4F, 1.2F);
            // Item is scaled if it is a 3d block item and would be hidden inside the wand model on rendering. This fixes some item models,
            // like fences or anvils, but doesn't fix others like buttons or banners, which still look a bit weird when rendered as preview item
            if (item instanceof BlockItem blockItem && renderer.getModel(item.getDefaultInstance(), level, null, 0).isGui3d()) {
                VoxelShape shape = blockItem.getBlock().defaultBlockState().getVisualShape(Minecraft.getInstance().level, BlockPos.ZERO, CollisionContext.empty());
                float xSize = shape.isEmpty() ? 1 : (float) shape.bounds().getXsize();
                poseStack.scale(1, 1, 0.35F / xSize);
            }
        }
        renderer.renderStatic(item.getDefaultInstance(), context == ItemDisplayContext.GUI ? ItemDisplayContext.GUI : ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, level, 0);
        poseStack.popPose();
    }
}
