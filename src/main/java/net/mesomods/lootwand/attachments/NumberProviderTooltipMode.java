package net.mesomods.lootwand.attachments;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum NumberProviderTooltipMode implements StringRepresentable {
    ENABLED(true, true, "enabled"),
    UNIFORM_DISABLED(false, true, "uniform_disabled"),
    ALL_DISABLED(false, false, "all_disabled");

    final boolean showUniformDistribution;
    final boolean showNonUniformDistribution;
    final String name;

    public static final Codec<NumberProviderTooltipMode> CODEC = StringRepresentable.fromEnum(NumberProviderTooltipMode::values);

    NumberProviderTooltipMode(boolean showUniformDistribution, boolean showNonUniformDistribution, String name) {
        this.showUniformDistribution = showUniformDistribution;
        this.showNonUniformDistribution = showNonUniformDistribution;
        this.name = name;
    }

    public boolean showScoreboardDependentDistribution() {
        return showNonUniformDistribution;
    }

    public boolean showUniformDistribution() {
        return showUniformDistribution;
    }

    public boolean showNonUniformDistribution() {
        return showNonUniformDistribution;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
