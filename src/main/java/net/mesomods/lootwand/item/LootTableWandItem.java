package net.mesomods.lootwand.item;

import net.mesomods.lootwand.LootWandMod;
import net.mesomods.lootwand.ModItems;
import net.mesomods.lootwand.attachments.LootTableWandPlayerDataManager;
import net.mesomods.lootwand.client.gui.screen.LootTableWandScreen;
import net.mesomods.lootwand.container.RandomizableContainerBlockEntityAccessor;
import net.mesomods.lootwand.network.LootTableNetwork;
import net.mesomods.lootwand.network.packet.client.UpdateLootTableWandPacket;
import net.mesomods.lootwand.network.packet.server.WandItemPreviewPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LootTableWandItem extends Item {
    public LootTableWandItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);
        ResourceLocation lootTable = getActiveLootTable(stack);
            if (lootTable != null) {
                String lootTableString = lootTable.toString();
                list.add(Component.literal(lootTableString));
                return;
            }
        list.add(Component.translatable("item.loot_table_wand.loot_table_wand.description"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) return InteractionResultHolder.success(stack);
        Minecraft.getInstance().setScreen(new LootTableWandScreen(player, stack));
        return InteractionResultHolder.success(stack);
    }

    public static InteractionResult leftClickOn(Player player, Level level, BlockPos targetPos, InteractionHand hand) {
        if (player == null) return InteractionResult.FAIL;
        BlockEntity targetBE = level.getBlockEntity(targetPos);
        if (targetBE instanceof RandomizableContainerBlockEntity container) {
            if (player.isSecondaryUseActive()) return InteractionResult.PASS;
            if (level.isClientSide) return InteractionResult.SUCCESS;
            ResourceLocation containerLootTable = ((RandomizableContainerBlockEntityAccessor)container).getLootTable();
            if (LootTableWandPlayerDataManager.shouldEmptyTargetContainer(player)) {
                container.clearContent();
            }
            if (containerLootTable == null) {
                return InteractionResult.SUCCESS;
            }
            container.setLootTable(null, 0);
            container.setChanged();
            level.playSound(null, targetPos, SoundEvents.NOTE_BLOCK_BANJO.value(), SoundSource.PLAYERS);
            ((ServerLevel) level).sendParticles(LootWandMod.REMOVE_LOOT_TABLE_PARTICLE, targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5, 12, 0.5, 0.5, 0.5, 0.5);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult middleClickOn(Player player, RandomizableContainerBlockEntityAccessor entity, ItemStack wand) {
        if (player.isSecondaryUseActive()) return InteractionResult.PASS;
        ResourceLocation lootTable = entity.getLootTable();
        if (lootTable == null) return InteractionResult.SUCCESS;
        setLootTable(player, wand, getActiveLootTableIndex(wand), lootTable.toString(), true);
        return InteractionResult.SUCCESS;
    }

    public static InteractionResult rightClickOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.FAIL;
        BlockPos targetPos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        BlockEntity targetBE = level.getBlockEntity(targetPos);
        ResourceLocation wandLootTable = getActiveLootTable(stack);
        if (targetBE instanceof RandomizableContainerBlockEntity container) {
            if (context.isSecondaryUseActive()) return container.getBlockState().use(level, player, context.getHand(), (BlockHitResult) Minecraft.getInstance().hitResult);
            if (level.isClientSide) return InteractionResult.SUCCESS;
            ResourceLocation containerLootTable = ((RandomizableContainerBlockEntityAccessor)container).getLootTable();
            if (LootTableWandPlayerDataManager.shouldEmptyTargetContainer(player)) {
                container.clearContent();
            }
            if (wandLootTable == null || wandLootTable.equals(containerLootTable)) {
                return InteractionResult.SUCCESS;
            }
            container.setLootTable(wandLootTable, 0);
            container.setChanged();
            level.playSound(null, targetPos, SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.PLAYERS);
            ((ServerLevel) level).sendParticles(LootWandMod.ADD_LOOT_TABLE_PARTICLE, targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5, 12, 0.5, 0.5, 0.5, 0.5);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public static String getShortenedLocation(String string) {
        if (string.contains("/")) {
            return string.substring(string.lastIndexOf("/") + 1);
        }
        return string.substring(string.lastIndexOf(":") + 1);
    }

    public static void setLootTable(Player player, ItemStack stack, int lootTableSlot, String lootTableString, boolean isClient) {
        if (lootTableSlot > 9 || lootTableSlot < 0) {
            return;
        }
        CompoundTag itemStackData = stack.getOrCreateTag();
        String lootTableKey = "LootTable" + lootTableSlot;
        itemStackData.putString(lootTableKey, lootTableString);
        if (isClient) {
            LootTableNetwork.sendToServer(UpdateLootTableWandPacket.setTable(player.getInventory().items.indexOf(stack), lootTableSlot, lootTableString));
        }
    }

    public static boolean toggleDeletionMode(Player player, ItemStack stack, boolean isClient) {
        CompoundTag itemStackNbt = stack.getOrCreateTag();
        boolean deletionMode = itemStackNbt.contains("DeletionMode") && itemStackNbt.getBoolean("DeletionMode");
        itemStackNbt.putBoolean("DeletionMode", !deletionMode);
        if (isClient)
            LootTableNetwork.sendToServer(UpdateLootTableWandPacket.toggleDeletionMode(player.getInventory().items.indexOf(stack)));
        return !deletionMode;
    }

    public static boolean isInDeletionMode(ItemStack stack) {
        CompoundTag itemStackNbt = stack.getOrCreateTag();
        return itemStackNbt.contains("DeletionMode") && itemStackNbt.getBoolean("DeletionMode");
    }

    public static int getActiveLootTableIndex(ItemStack stack) {
        CompoundTag itemStackData = stack.getOrCreateTag();
        if (itemStackData.contains("ActiveLootTable")) {
            return itemStackData.getInt("ActiveLootTable");
        } else {
            return -1;
        }
    }

    private static void setActiveLootTableIndex(ItemStack stack, int index) {
        CompoundTag itemStackData = stack.getOrCreateTag();
        itemStackData.putInt("ActiveLootTable", index);
    }

    public static void decreaseLootTableIndex(ItemStack stack, Player player) {
        int index = getActiveLootTableIndex(stack);
        if (index == -1) return;
        index--;
        if (index == -1) index = 9;
        setActiveLootTableIndexClient(stack, index, player);
    }

    public static void increaseLootTableIndex(ItemStack stack, Player player) {
        int index = getActiveLootTableIndex(stack);
        if (index == -1) return;
        index++;
        if (index == 10) index = 0;
        setActiveLootTableIndexClient(stack, index, player);
    }

    public static void setActiveLootTableIndexClient(ItemStack stack, int index, Player player) {
        if (LootTableWandPlayerDataManager.shouldSynchronizeWands(player)) {
            for (ItemStack itemstack : player.getInventory().items) {
                if (itemstack.is(ModItems.LOOT_TABLE_WAND)) {
                    setActiveLootTableIndex(itemstack, index);
                }
            }
        } else {
            setActiveLootTableIndex(stack, index);
        }
        LootTableNetwork.sendToServer(UpdateLootTableWandPacket.activateTable(player.getInventory().items.indexOf(stack), index));
    }

    public static void setActiveLootTableWandIndexServer(ItemStack stack, int index, ServerPlayer player) {
        if (LootTableWandPlayerDataManager.shouldSynchronizeWands(player)) {
            for (ItemStack itemstack : player.getInventory().items) {
                if (itemstack.is(ModItems.LOOT_TABLE_WAND)) {
                    setActiveLootTableIndex(itemstack, index);
                    WandItemPreviewPacket.sendPreviewListUpdate(player.getInventory().items.indexOf(itemstack), LootTableWandItem.getLootTable(itemstack, index), player);
                }
            }
        } else {
            setActiveLootTableIndex(stack, index);
            WandItemPreviewPacket.sendPreviewListUpdate(player.getInventory().items.indexOf(stack), LootTableWandItem.getLootTable(stack, index), player);
        }
    }

    public static void removeLootTable(Player player, ItemStack stack, int index, boolean isClient) {
        if (index == -1) {
            return;
        }
        CompoundTag itemStackData = stack.getOrCreateTag();
        String lootTableKey = "LootTable" + index;
        itemStackData.remove(lootTableKey);
        if (isClient) {
            LootTableNetwork.sendToServer(UpdateLootTableWandPacket.removeTable(player.getInventory().items.indexOf(stack), index));
        }
    }

    public static void setPreviewList(ItemStack stack, int[] times, ListTag items) {
        CompoundTag nbt = stack.getOrCreateTag();
        if (times.length != items.size()) return;
        if (items.isEmpty()) {
            nbt.remove("PreviewTimes");
            nbt.remove("PreviewItems");
            nbt.remove("PreviewItem");
        } else {
            nbt.putIntArray("PreviewTimes", times);
            nbt.put("PreviewItems", items);
            nbt.remove("PreviewItem");

        }
        nbt.remove("PreviewTimeStart");
        nbt.remove("PreviewTimeEnd");
    }

    public static void updatePreview(ItemStack stack, UpdateLootTableWandPacket.UpdatePreviewData previewData) {
        CompoundTag nbt = stack.getOrCreateTag();
        if (previewData.isEmpty()) {
            nbt.remove("PreviewItem");
            nbt.remove("PreviewTimeStart");
            nbt.remove("PreviewTimeEnd");
        } else {
            nbt.putInt("PreviewTimeStart", previewData.previewStart());
            nbt.putInt("PreviewTimeEnd", previewData.previewEnd());
            nbt.putString("PreviewItem", previewData.previewItem());
        }
    }

    @Nullable
    public static ResourceLocation getActiveLootTable(ItemStack stack) {
        return getLootTable(stack, -1);
    }

    public static boolean hasActiveLootTable(ItemStack stack) {
        return stack.getOrCreateTag().contains("ActiveLootTable");
    }

    @Nullable
    public static ResourceLocation getLootTable(ItemStack stack, int lootTableSlot) {
        if (lootTableSlot > 9 || lootTableSlot < -1) {
            return null;
        }
        CompoundTag itemStackData = stack.getOrCreateTag();
        if (lootTableSlot == -1) {
            lootTableSlot = itemStackData.contains("ActiveLootTable") ? itemStackData.getInt("ActiveLootTable") : 0;
        }
        String lootTableKey = "LootTable" + lootTableSlot;
        if (!itemStackData.contains(lootTableKey)) {
            return null;
        }
        String lootTableString = itemStackData.getString(lootTableKey);
        return new ResourceLocation(lootTableString);
    }
}
