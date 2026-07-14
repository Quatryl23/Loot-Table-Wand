package net.mesomods.lootwand.capabilities;

public enum NumberProviderTooltipMode {
    ENABLED(true, true),
    UNIFORM_DISABLED(false, true),
    ALL_DISABLED(false, false);

    final boolean showUniformDistribution;
    final boolean showNonUniformDistribution;

    NumberProviderTooltipMode(boolean showUniformDistribution, boolean showNonUniformDistribution) {
        this.showUniformDistribution = showUniformDistribution;
        this.showNonUniformDistribution = showNonUniformDistribution;
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
}
