package io.takari.bpm.reducers;

import java.util.Collections;
import java.util.Set;

import io.takari.bpm.actions.Action;
import io.takari.bpm.actions.CopyEventMapAction;
import io.takari.bpm.actions.MakeSubProcessVariablesAction;
import io.takari.bpm.actions.MergeVariablesAndEventMapAction;
import io.takari.bpm.actions.SetVariableAction;
import io.takari.bpm.actions.SetVariablesAction;
import io.takari.bpm.actions.UnsetVariableAction;
import io.takari.bpm.api.ExecutionException;
import io.takari.bpm.el.ExpressionManager;
import io.takari.bpm.model.VariableMapping;
import io.takari.bpm.state.EventMapHelper;
import io.takari.bpm.state.ProcessInstance;
import io.takari.bpm.state.Variables;
import io.takari.bpm.state.VariablesHelper;

@Impure
public class VariablesReducer implements Reducer {

    private final ExpressionManager expressionManager;

    public VariablesReducer(ExpressionManager expressionManager) {
        this.expressionManager = expressionManager;
    }

    @Override
    public ProcessInstance reduce(ProcessInstance state, Action action) throws ExecutionException {
        if (action instanceof SetVariableAction) {
            SetVariableAction a = (SetVariableAction) action;
            Variables vars = state.getVariables();
            return state.setVariables(vars.setVariable(a.getKey(), a.getValue()));
        } else if (action instanceof UnsetVariableAction) {
            UnsetVariableAction a = (UnsetVariableAction) action;
            Variables vars = state.getVariables();
            return state.setVariables(vars.removeVariable(a.getKey()));
        } else if (action instanceof SetVariablesAction) {
            SetVariablesAction a = (SetVariablesAction) action;
            return state.setVariables(a.getVariables());
        } else if (action instanceof MergeVariablesAndEventMapAction) {
            // TODO move into a separate file
            MergeVariablesAndEventMapAction a = (MergeVariablesAndEventMapAction) action;

            // copy the out variables to the parent's context
            Variables src = a.getSource();
            Variables dst = state.getVariables();
            dst = VariablesHelper.copyVariables(expressionManager, src, dst, a.getOutVariables());

            // copy the child process' event map to the parent's context
            Set<VariableMapping> m = Collections.singleton(EventMapHelper.createOutMapping());
            dst = VariablesHelper.copyVariables(expressionManager, src, dst, m);

            return state.setVariables(dst);
        } else if (action instanceof MakeSubProcessVariablesAction) {
            // TODO move into a separate file
            MakeSubProcessVariablesAction a = (MakeSubProcessVariablesAction) action;

            Variables src = state.getVariables();
            Variables dst = new Variables();

            if (a.isCopyAllVariables()) {
                dst = VariablesHelper.copyVariables(src, dst);
            }

            dst = VariablesHelper.copyVariables(expressionManager, src, dst, a.getInVariables());
            return state.setVariables(dst);
        } else if (action instanceof CopyEventMapAction) {
            CopyEventMapAction a = (CopyEventMapAction) action;
            Variables vars = state.getVariables();
            return state.setVariables(EventMapHelper.set(vars, a.getEventMap()));
        }

        return state;
    }
}