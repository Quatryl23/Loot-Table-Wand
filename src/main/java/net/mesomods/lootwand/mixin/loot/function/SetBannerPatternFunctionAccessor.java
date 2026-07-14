package net.mesomods.lootwand.mixin.loot.function;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.functions.SetBannerPatternFunction.class)
public interface SetBannerPatternFunctionAccessor {
    @Accessor("append")
    boolean append();

    @Mutable
    @Accessor
    void setAppend(boolean append);

    @Accessor
    List<Pair<Holder<BannerPattern>, DyeColor>> getPatterns();

    @Mutable
    @Accessor
    void setPatterns(List<Pair<Holder<BannerPattern>, DyeColor>> patterns);
}
