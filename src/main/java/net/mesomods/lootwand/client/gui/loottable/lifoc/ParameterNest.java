package net.mesomods.lootwand.client.gui.loottable.lifoc;

import net.mesomods.lootwand.client.gui.loottable.lifoc.parameters.ContentsParameter;
import net.mesomods.lootwand.client.gui.loottable.lifoc.parameters.NestedConditionParameter;
import net.mesomods.lootwand.client.gui.loottable.lifoc.parameters.NestedFunctionParameter;
import net.mesomods.lootwand.client.gui.loottable.lifoc.parameters.RenderableParameter;

import java.util.List;

public interface ParameterNest {
    List<RenderableParameter> getNestedParameters();

    default List<RenderableParameter> getNestedHideableParameters() {
        return this.getNestedParameters();
    }

    void updateHeight(boolean updateParents);

    default void toggleDefaultParameters(boolean hidden) {
        for (RenderableParameter parameter : getNestedParameters()) {
            if (parameter instanceof ParameterNest p) {
                p.toggleDefaultParameters(hidden);
            } else if (parameter instanceof ContentsParameter c) {
                c.toggleDefaultParameters(hidden);
            } else if (parameter instanceof NestedConditionParameter c) {
                c.toggleDefaultParameters(hidden);
            } else if (parameter instanceof NestedFunctionParameter f) {
                f.toggleDefaultParameters(hidden);
            }
        }
        for (RenderableParameter parameter : this.getNestedHideableParameters()) {
            if (hidden) {
                parameter.setHidden(parameter.isDefault());
            } else {
                parameter.setHidden(false);
            }
        }
        updateHeight(false);
    }

    default void applyFunctionPreviewEffects(boolean enabled) {
        for (RenderableParameter parameter : getNestedParameters()) {
            if (parameter instanceof ParameterNest p) {
                p.applyFunctionPreviewEffects(enabled);
            } else if (parameter instanceof ContentsParameter c) {
                c.applyFunctionPreviewEffects(enabled);
            } else if (parameter instanceof NestedConditionParameter c) {
                c.applyFunctionPreviewEffects(enabled);
            } else if (parameter instanceof NestedFunctionParameter f) {
                f.applyFunctionPreviewEffects(enabled);
            }
        }
    }
}
