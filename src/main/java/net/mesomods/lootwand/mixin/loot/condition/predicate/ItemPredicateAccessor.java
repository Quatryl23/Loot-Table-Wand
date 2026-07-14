package net.mesomods.lootwand.mixin.loot.condition.predicate;

import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.ItemPredicate.class)
public interface ItemPredicateAccessor {
    @Accessor
    TagKey<Item> getTag();

    @Mutable
    @Accessor
    void setTag(TagKey<Item> tag);

    @Accessor
    Set<Item> getItems();

    @Mutable
    @Accessor
    void setItems(Set<Item> items);

    @Accessor
    MinMaxBounds.Ints getCount();

    @Mutable
    @Accessor
    void setCount(MinMaxBounds.Ints count);

    @Accessor
    MinMaxBounds.Ints getDurability();

    @Mutable
    @Accessor
    void setDurability(MinMaxBounds.Ints durability);

    @Accessor
    EnchantmentPredicate[] getEnchantments();

    @Mutable
    @Accessor
    void setEnchantments(EnchantmentPredicate[] enchantments);

    @Accessor
    EnchantmentPredicate[] getStoredEnchantments();

    @Mutable
    @Accessor
    void setStoredEnchantments(EnchantmentPredicate[] storedEnchantments);

    @Accessor
    Potion getPotion();

    @Mutable
    @Accessor
    void setPotion(Potion potion);

    @Accessor
    NbtPredicate getNbt();

    @Mutable
    @Accessor
    void setNbt(NbtPredicate nbt);
}
