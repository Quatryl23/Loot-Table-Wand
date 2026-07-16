package net.mesomods.lootwand.util;

import net.mesomods.lootwand.client.gui.loottable.poolentry.RenderedItemEntry;
import net.mesomods.lootwand.mixin.loot.function.FunctionReferenceAccessor;
import net.mesomods.lootwand.mixin.loot.function.LootItemConditionalFunctionAccessor;
import net.mesomods.lootwand.network.LootTableNetwork;
import net.mesomods.lootwand.network.packet.client.RequestItemFunctionSimulationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

// creates default LootContexts for Loot Table previews
public class LootContextManager {
    protected static final HashMap<Integer, RenderedItemEntry> pendingItems = new HashMap<>();
    public static LootContext currentContext;

    public static void updateContext(ServerLevel level, LootTable table, ServerPlayer player) {
        currentContext = new LootContext.Builder(buildFakeParams(level, table, player)).create(null);
    }

    public static LootParams buildFakeParams(ServerLevel level, LootTable table, ServerPlayer player) {
        RegistryAccess registry = level.registryAccess();
        Set<LootContextParam<?>> requiredParams = table.getParamSet().getAllowed();
        LootParams.Builder params = new LootParams.Builder(level);
        if (requiredParams.contains(LootContextParams.BLOCK_ENTITY)) {
            params.withParameter(LootContextParams.BLOCK_ENTITY, new ChestBlockEntity(BlockPos.containing(player.getPosition(0)), Blocks.CHEST.defaultBlockState()));
        }
        // just air
        if (requiredParams.contains(LootContextParams.BLOCK_STATE)) {
            params.withParameter(LootContextParams.BLOCK_STATE, Blocks.AIR.defaultBlockState());
        }
        // pretending to be killed by generic damage and no other entity involved
        if (requiredParams.contains(LootContextParams.DAMAGE_SOURCE)) {
            Registry<DamageType> damageTypeRegistry = registry.registryOrThrow(Registries.DAMAGE_TYPE);
            Holder<DamageType> genericDamage = damageTypeRegistry.getHolderOrThrow(DamageTypes.GENERIC);
            params.withParameter(LootContextParams.DAMAGE_SOURCE, new DamageSource(genericDamage, null, null));
        }
        // no tool in hand
        if (requiredParams.contains(LootContextParams.TOOL)) {
            params.withParameter(LootContextParams.TOOL, ItemStack.EMPTY);
        }
        if (requiredParams.contains(LootContextParams.LAST_DAMAGE_PLAYER)) {
            params.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player);
        }
        if (requiredParams.contains(LootContextParams.DIRECT_KILLER_ENTITY)) {
            params.withParameter(LootContextParams.DIRECT_KILLER_ENTITY, player);
        }
        if (requiredParams.contains(LootContextParams.EXPLOSION_RADIUS)) {
            params.withParameter(LootContextParams.EXPLOSION_RADIUS, 0.0F);
        }
        if (requiredParams.contains(LootContextParams.KILLER_ENTITY)) {
            params.withParameter(LootContextParams.KILLER_ENTITY, player);
        }
        // position of the player
        if (requiredParams.contains(LootContextParams.ORIGIN)) {
            params.withParameter(LootContextParams.ORIGIN, player.getPosition(0));
        }
        // the player
        if (requiredParams.contains(LootContextParams.THIS_ENTITY)) {
            params.withParameter(LootContextParams.THIS_ENTITY, player);
        }
        return params.create(table.getParamSet());
    }

    public static void simulateLootItemFunctionsClient(ItemStack stack, List<LootItemFunction> functions, RenderedItemEntry entry) {
        int i = 0;
        while (true) {
            if (!pendingItems.containsKey(i)) {
                pendingItems.put(i, entry);
                LootTableNetwork.sendToServer(new RequestItemFunctionSimulationPacket(i, stack, functions));
                break;
            }
            i++;
        }
    }

    public static ItemStack simulateLootItemFunctionsServer(ItemStack stack, List<LootItemFunction> functions) {
        for (LootItemFunction function : functions) {
            if (isSimulatableFunction(function)) {
                if (function instanceof LootItemConditionalFunctionAccessor c) {
                    if (c.getPredicates().length == 0) stack = function.apply(stack, currentContext);
                } else {
                    stack = function.apply(stack, currentContext);
                }
            }
        }
        return stack;
    }

    public static boolean isSimulatableFunction(LootItemFunction function) {
        LootItemFunctionType type = function.getType();
        while (type == LootItemFunctions.REFERENCE) {
            LootItemFunction f = currentContext.getResolver().getElement(LootDataType.MODIFIER, ((FunctionReferenceAccessor) function).getName());
            if (f == null) return false;
            type = f.getType();
        }
        return type == LootItemFunctions.ENCHANT_RANDOMLY || type == LootItemFunctions.ENCHANT_WITH_LEVELS || type == LootItemFunctions.EXPLORATION_MAP
                || type == LootItemFunctions.FILL_PLAYER_HEAD || type == LootItemFunctions.FURNACE_SMELT || type == LootItemFunctions.SET_ATTRIBUTES
                || type == LootItemFunctions.SET_CONTENTS || type == LootItemFunctions.SET_DAMAGE || type == LootItemFunctions.SET_ENCHANTMENTS
                || type == LootItemFunctions.SET_BANNER_PATTERN || type == LootItemFunctions.SET_LOOT_TABLE
                || type == LootItemFunctions.SET_LORE || type == LootItemFunctions.SET_NAME || type == LootItemFunctions.SET_NBT
                || type == LootItemFunctions.SET_POTION || type == LootItemFunctions.SET_STEW_EFFECT;
    }

    public static void respondModifiedItem(int id, ItemStack stack) {
        pendingItems.get(id).setModifiedStack(stack);
        pendingItems.remove(id);
    }

    public static Component getEntityTargetTranslation(LootContext.EntityTarget target) {
        return Component.translatable(switch (target) {
            case THIS -> "gui.loot_table_wand.loot_context.this";
            case KILLER -> "gui.loot_table_wand.loot_context.attacker";
            case DIRECT_KILLER -> "gui.loot_table_wand.loot_context.direct_attacker";
            case KILLER_PLAYER -> "gui.loot_table_wand.loot_context.attacking_player";
        });
    }
}
