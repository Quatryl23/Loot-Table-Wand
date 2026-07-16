package net.mesomods.lootwand.client.gui;


import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.mixin.client.keybinding.KeyBindingAccessor;
import net.mesomods.lootwand.LootWandMod;
import net.mesomods.lootwand.ModItems;
import net.mesomods.lootwand.attachments.LootTableWandPlayerDataManager;
import net.mesomods.lootwand.client.LootWandModClient;
import net.mesomods.lootwand.client.ScreenUtils;
import net.mesomods.lootwand.container.RandomizableContainerBlockEntityAccessor;
import net.mesomods.lootwand.item.LootTableWandItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.lwjgl.glfw.GLFW;

public class LootTableWandDetailsOverlay {
    public static final Font FONT = ScreenUtils.FONT;
    public static final int LOOT_TABLE_NAME_COLOR = 0xFFFFFF;
    public static final int WAND_NAME_COLOR = ChatFormatting.LIGHT_PURPLE.getColor();
    public static final int CONTAINER_NAME_COLOR = ChatFormatting.GREEN.getColor();
    public static final int KEYBINDS_DESCRIPTION_COLOR = 0xFFFFFF;
    public static final int KEY_NAME_COLOR = 0x45283C;
    public static final ResourceLocation TEXTURE_LEFT = overlayTexture("loot_table_info_left");
    public static final ResourceLocation TEXTURE_CENTER = overlayTexture("loot_table_info_center");
    public static final ResourceLocation TEXTURE_RIGHT = overlayTexture("loot_table_info_right");
    public static final ResourceLocation TEXTURE_SCALABLE = overlayTexture("loot_table_info_scalable");
    public static final ResourceLocation TEXTURE_KEY_LEFT = overlayTexture("key_left");
    public static final ResourceLocation TEXTURE_KEY_RIGHT = overlayTexture("key_right");
    public static final ResourceLocation TEXTURE_KEY_SCALABLE = overlayTexture("key_scalable");
    public static final ResourceLocation TEXTURE_KEY_SHIFT = overlayTexture("key_shift");
    public static final ResourceLocation TEXTURE_KEY_ARROW_LEFT = overlayTexture("key_arrow_left");
    public static final ResourceLocation TEXTURE_KEY_ARROW_RIGHT = overlayTexture("key_arrow_right");
    public static final ResourceLocation TEXTURE_KEY_MOUSE_LEFT = overlayTexture("mouse_left");
    public static final ResourceLocation TEXTURE_KEY_MOUSE_MIDDLE = overlayTexture("mouse_middle");
    public static final ResourceLocation TEXTURE_KEY_MOUSE_RIGHT = overlayTexture("mouse_right");

    private static ResourceLocation overlayTexture(String string) {
        return new ResourceLocation(LootWandMod.MODID, "textures/gui/overlay/" +  string + ".png");
    }

    public static void render(GuiGraphics graphics, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        Player player = mc.player;
        ItemStack stack = player.getMainHandItem();
        if (!stack.is(ModItems.LOOT_TABLE_WAND)) return;
        HitResult result = mc.hitResult;
        Component containerName = null;
        Component wandName = stack.getHoverName();
        if (result instanceof BlockHitResult blockHitResult) {
            BlockEntity be = mc.level.getBlockEntity(blockHitResult.getBlockPos());
            if (be instanceof RandomizableContainerBlockEntity container) {
                containerName = container.getName();
                ResourceLocation lootTable = ((RandomizableContainerBlockEntityAccessor) container).getLootTable();
                Component lootTableDescr = lootTable == null ? Component.translatable("gui.loot_table_wand.overlay.no_loot_table") : Component.literal(lootTable.toString());
                drawTargetContainerTableDetails(graphics, containerName, lootTableDescr, screenWidth, screenHeight);
            }
        }
        ResourceLocation wandLootTable = LootTableWandItem.getActiveLootTable(player.getMainHandItem());
        Component lootTableDescr = wandLootTable == null ? Component.translatable("gui.loot_table_wand.overlay.no_loot_table") : Component.literal(wandLootTable.toString());
        drawWandTableDetails(graphics, wandName, lootTableDescr, screenWidth, screenHeight);
        if (LootTableWandPlayerDataManager.shouldShowKeybinds(player) && mc.screen == null)
            renderKeybinds(graphics, containerName, wandName, screenHeight);
    }

    private static void drawTargetContainerTableDetails(GuiGraphics graphics, Component containerName, Component lootTable, int screenWidth, int screenHeight) {
        drawLootTableInfoBox(graphics, lootTable, screenWidth / 2, screenHeight - 83, containerName, CONTAINER_NAME_COLOR);
    }

    private static void drawWandTableDetails(GuiGraphics graphics, Component lootTableWandName, Component lootTable, int screenWidth, int screenHeight) {
        drawLootTableInfoBox(graphics, lootTable, screenWidth / 2, screenHeight - 53, lootTableWandName, WAND_NAME_COLOR);
    }

    private static void renderKeybinds(GuiGraphics graphics, Component containerName, Component wandName, int screenHeight) {
        int lineHeight = (FONT.lineHeight / 2) + 4;
        int startHeight = screenHeight - 14;
        Options options = Minecraft.getInstance().options;
        drawKeybindInfo(graphics, LootWandModClient.HIDE_KEYBINDS_KEY, "gui.loot_table_wand.overlay.hide_keybinds", null, startHeight);
        drawKeybindInfo(graphics, LootWandModClient.NEXT_LOOT_TABLE_KEY, "gui.loot_table_wand.overlay.next_loot_table", null, startHeight - lineHeight);
        drawKeybindInfo(graphics, LootWandModClient.PREVIOUS_LOOT_TABLE_KEY, "gui.loot_table_wand.overlay.previous_loot_table", null, startHeight - 2 * lineHeight);
        if (containerName == null) {
            drawKeybindInfo(graphics, LootWandModClient.INSPECT_KEY, "gui.loot_table_wand.overlay.inspect_wand_loot_table", wandName, startHeight - 3 * lineHeight);
            drawKeybindInfo(graphics, options.keyUse, "gui.loot_table_wand.overlay.open_gui", null, startHeight - 4 * lineHeight);
        } else {
            drawKeybindInfo(graphics, options.keyShift, LootWandModClient.INSPECT_KEY, "gui.loot_table_wand.overlay.inspect_loot_table", wandName, startHeight - 3 * lineHeight);
            drawKeybindInfo(graphics, options.keyShift, options.keyAttack, "gui.loot_table_wand.overlay.destroy_container", containerName, startHeight - 4 * lineHeight);
            drawKeybindInfo(graphics, options.keyShift, options.keyPickItem, "gui.loot_table_wand.overlay.pick_container", containerName, startHeight - 5 * lineHeight);
            drawKeybindInfo(graphics, options.keyShift, options.keyUse, "gui.loot_table_wand.overlay.open_container", containerName, startHeight - 6 * lineHeight);
            drawKeybindInfo(graphics, LootWandModClient.INSPECT_KEY, "gui.loot_table_wand.overlay.inspect_loot_table", containerName, startHeight - 7 * lineHeight);
            drawKeybindInfo(graphics, options.keyAttack, "gui.loot_table_wand.overlay.remove_loot_table", containerName, startHeight - 8 * lineHeight);
            drawKeybindInfo(graphics, options.keyPickItem, "gui.loot_table_wand.overlay.pick_loot_table", containerName, startHeight - 9 * lineHeight);
            drawKeybindInfo(graphics, options.keyUse, "gui.loot_table_wand.overlay.set_loot_table", containerName, startHeight - 10 * lineHeight);
        }

    }

    private static void drawKeybindInfo(GuiGraphics graphics, KeyMapping keyMapping, String translationKey, Component containerName, int y) {
        drawKeybindInfo(graphics, keyMapping, null, translationKey, containerName, y);
    }

    private static void drawKeybindInfo(GuiGraphics graphics, KeyMapping keyMapping, KeyMapping secondKeyMapping, String translationKey, Component actionTargetName, int y) {
        int x = drawNamedKey(graphics, keyMapping, 10, y);
        if (secondKeyMapping != null) {
            ScreenUtils.drawScaledString(graphics, "+", x + 3, y + 1.5f, KEYBINDS_DESCRIPTION_COLOR, 0.5f, false, true);
            x = drawNamedKey(graphics, secondKeyMapping, x + 7, y);
        }
        Component keybindDescription = actionTargetName == null ? Component.translatable(translationKey) : Component.translatable(translationKey, actionTargetName);
        ScreenUtils.drawScaledString(graphics, keybindDescription, Math.max(20, x + 3), y + 1.5f, KEYBINDS_DESCRIPTION_COLOR, 0.5f, false, true);
    }

    private static int drawNamedKey(GuiGraphics graphics, KeyMapping keyMapping, int x, int y) {
        InputConstants.Key key = ((KeyBindingAccessor)keyMapping).fabric_getBoundKey();
        if (key.getValue() == GLFW.GLFW_KEY_LEFT_SHIFT) {
            graphics.blit(TEXTURE_KEY_SHIFT, x, y, 0, 0, 0, 24, 8, 24, 8);
            return x + 15;
        } else if (key.getValue() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            graphics.blit(TEXTURE_KEY_MOUSE_LEFT, x, y, 0, 0, 0,  8, 8, 8, 8);
            return x + 8;
        } else if (key.getValue() == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
            graphics.blit(TEXTURE_KEY_MOUSE_MIDDLE, x, y, 0, 0, 0, 8, 8, 8, 8);
            return x + 8;
        } else if (key.getValue() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            graphics.blit(TEXTURE_KEY_MOUSE_RIGHT, x, y, 0, 0, 0, 8, 8, 8, 8);
            return x + 8;
        } else if (key.getValue() == GLFW.GLFW_KEY_LEFT) {
            graphics.blit(TEXTURE_KEY_ARROW_LEFT, x, y, 0, 0, 0, 8, 8, 8, 8);
            return x + 8;
        } else if (key.getValue() == GLFW.GLFW_KEY_RIGHT) {
            graphics.blit(TEXTURE_KEY_ARROW_RIGHT, x, y, 0, 0, 0, 8, 8, 8, 8);
            return x + 8;
        }
        Component keyName = keyMapping.getTranslatedKeyMessage();
        int textWidth = FONT.width(keyName);
        int textureWidth = Mth.clamp((int) Math.round(((textWidth / 2.0) + 2.0)), 6, 80);
        graphics.blit(TEXTURE_KEY_LEFT, x + 1, y, 0, 0, 0, 1, 8, 1, 8);
        int i;
        for (i = x + 2; i < x + textureWidth; i++) {
            graphics.blit(TEXTURE_KEY_SCALABLE, i, y, 0, 0, 0, 1, 8, 1, 8);
        }
        graphics.blit(TEXTURE_KEY_RIGHT, i, y, 0, 0, 0, 1, 8, 1, 8);
        ScreenUtils.drawScaledString(graphics, keyName, x + 1.5f + (textureWidth / 2.0f), y + 1.5f, KEY_NAME_COLOR, 0.5f, true, false);
        return i + 2;
    }

    private static void drawLootTableInfoBox(GuiGraphics graphics, Component lootTable, int centerX, int y, Component tableSource, int tableSourceColor) {
        int x = centerX - 4;
        int textWidth = FONT.width(lootTable);
        int halfTextureWidth = Mth.clamp(4 * (int) Math.round((Math.max(textWidth / 2.0, FONT.width(tableSource) / 4.0) + 6.0) / 4.0), 12, 180);
        graphics.blit(TEXTURE_LEFT, x - halfTextureWidth, y, 0, 0, 8, 24, 8, 24);
        graphics.blit(TEXTURE_CENTER, x, y, 0, 0, 8, 24, 8, 24);
        graphics.blit(TEXTURE_RIGHT, x + halfTextureWidth, y, 0, 0, 8, 24, 8, 24);
        for (int i = halfTextureWidth - 12; i >= 0; i -= 4) {
            graphics.blit(TEXTURE_SCALABLE, x - i - 4, y, 0, 0, 4, 24, 4, 24);
            graphics.blit(TEXTURE_SCALABLE, x + i + 8, y, 0, 0, 4, 24, 4, 24);
        }
        graphics.drawCenteredString(FONT, lootTable, centerX, y + 12, LOOT_TABLE_NAME_COLOR);
        ScreenUtils.drawScaledString(graphics, tableSource, centerX - 0.5f, y + 2, tableSourceColor, 0.5f, true, false);
    }
}
