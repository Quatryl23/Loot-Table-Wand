package net.mesomods.lootwand.mixin.loot.condition.predicate;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.PlayerPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.PlayerPredicate.class)
public interface PlayerPredicateAccessor {
    @Accessor
    MinMaxBounds.Ints getLevel();

    @Mutable
    @Accessor
    void setLevel(MinMaxBounds.Ints level);

    @Accessor
    GameType getGameType();

    @Mutable
    @Accessor
    void setGameType(GameType gameType);

    @Accessor
    Map<Stat<?>, MinMaxBounds.Ints> getStats();

    @Mutable
    @Accessor
    void setStats(Map<Stat<?>, MinMaxBounds.Ints> stats);

    @Accessor
    Object2BooleanMap<ResourceLocation> getRecipes();

    @Mutable
    @Accessor
    void setRecipes(Object2BooleanMap<ResourceLocation> recipes);

    @Accessor
    Map<ResourceLocation, PlayerPredicate.AdvancementPredicate> getAdvancements();

    @Mutable
    @Accessor
    void setAdvancements(Map<ResourceLocation, PlayerPredicate.AdvancementPredicate> advancements);

    @Accessor
    EntityPredicate getLookingAt();

    @Mutable
    @Accessor
    void setLookingAt(EntityPredicate lookingAt);
}
