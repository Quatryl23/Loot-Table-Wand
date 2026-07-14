package net.mesomods.lootwand.loot;

import net.mesomods.lootwand.loot.lifoc.RenderedCondition;
import net.mesomods.lootwand.loot.lifoc.RenderedFunction;

import java.util.List;

public interface LifocParent {
    List<RenderedFunction> getFunctions();
    List<RenderedCondition> getConditions();
    void updateHeight(boolean updateParent);
    default void setFunctionHeightUpdater() {
        for (RenderedFunction function : getFunctions()) {
            function.setHeightUpdater(() -> updateHeight(true));
        }
        for (RenderedCondition condition : getConditions()) {
            condition.setHeightUpdater(() -> updateHeight(true));
        }
    }
    default void toggleDefaultParameters(boolean hidden) {
        for (RenderedFunction function : getFunctions()) {
            function.toggleDefaultParameters(hidden);
        }
        for (RenderedCondition condition : getConditions()) {
            condition.toggleDefaultParameters(hidden);
        }
    }
}
