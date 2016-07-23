package io.takari.bpm.planner;

import java.util.List;

import io.takari.bpm.actions.Action;
import io.takari.bpm.api.ExecutionException;
import io.takari.bpm.commands.*;
import io.takari.bpm.state.ProcessInstance;

public class DelegatingCommandHandler implements CommandHandler<Command> {

    private final ProcessElementCommandHandler processElementCommandHandler = new ProcessElementCommandHandler();
    private final PerformActionCommandHandler performActionCommandHandler = new PerformActionCommandHandler();
    private final HandleRaisedErrorCommandHandler handleRaisedErrorCommandHandler = new HandleRaisedErrorCommandHandler();
    private final MergeVariablesCommandHandler mergeVariablesCommandHandler = new MergeVariablesCommandHandler();
    private final ClearCommandStartCommandHandler clearCommandStartCommandHandler = new ClearCommandStartCommandHandler();

    @Override
    public List<Action> handle(ProcessInstance state, Command cmd, List<Action> actions) throws ExecutionException {
        if (cmd instanceof ProcessElementCommand) {
            return processElementCommandHandler.handle(state, (ProcessElementCommand) cmd, actions);
        } else if (cmd instanceof PerformActionsCommand) {
            return performActionCommandHandler.handle(state, (PerformActionsCommand) cmd, actions);
        } else if (cmd instanceof HandleRaisedErrorCommand) {
            return handleRaisedErrorCommandHandler.handle(state, (HandleRaisedErrorCommand) cmd, actions);
        } else if (cmd instanceof MergeVariablesCommand) {
            return mergeVariablesCommandHandler.handle(state, (MergeVariablesCommand) cmd, actions);
        } else if (cmd instanceof ClearCommandStackCommand) {
            return clearCommandStartCommandHandler.handle(state, (ClearCommandStackCommand) cmd, actions);
        } else {
            throw new ExecutionException("Unsupported command: %s", cmd);
        }
    }
}
