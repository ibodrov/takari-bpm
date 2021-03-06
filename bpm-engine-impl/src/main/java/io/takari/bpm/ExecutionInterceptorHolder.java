package io.takari.bpm;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import io.takari.bpm.api.BpmnError;
import io.takari.bpm.api.ExecutionException;
import io.takari.bpm.api.Variables;
import io.takari.bpm.api.interceptors.*;

public class ExecutionInterceptorHolder {

    private final List<ExecutionInterceptor> interceptors = new CopyOnWriteArrayList<>();

    public void addInterceptor(ExecutionInterceptor i) {
        interceptors.add(i);
    }

    public void fireOnStart(String processBusinessKey, String processDefinitionId, UUID executionId, Map<String, Object> variables)
            throws ExecutionException {

        InterceptorStartEvent ev = new InterceptorStartEvent(processBusinessKey, processDefinitionId, executionId, variables);
        for (ExecutionInterceptor i : interceptors) {
            i.onStart(ev);
        }
    }

    public void fireOnSuspend() throws ExecutionException {
        for (ExecutionInterceptor i : interceptors) {
            i.onSuspend();
        }
    }

    public void fireOnResume() throws ExecutionException {
        for (ExecutionInterceptor i : interceptors) {
            i.onResume();
        }
    }

    public void fireOnFinish(String processBusinessKey) throws ExecutionException {
        for (ExecutionInterceptor i : interceptors) {
            i.onFinish(processBusinessKey);
        }
    }

    public void fireOnFailure(String processBusinessKey, String errorRef) throws ExecutionException {
        for (ExecutionInterceptor i : interceptors) {
            i.onFailure(processBusinessKey, errorRef);
        }
    }

    public void fireOnUnhandledError(String processBusinessKey, BpmnError error) throws ExecutionException {
        for (ExecutionInterceptor i : interceptors) {
            i.onUnhandledError(processBusinessKey, error);
        }
    }

    public void fireOnError(String processBusinessKey, String processDefinitionId, UUID executionId, UUID scopeId, Throwable cause)
            throws ExecutionException {

        InterceptorErrorEvent ev = new InterceptorErrorEvent(processBusinessKey, processDefinitionId, executionId, scopeId, cause);
        for (ExecutionInterceptor i : interceptors) {
            i.onError(processBusinessKey, cause);
            i.onError(ev);
        }
    }

    public void fireOnElement(Variables variables, String processBusinessKey, String processDefinitionId, UUID executionId, UUID scopeId, String elementId)
            throws ExecutionException {

        InterceptorElementEvent ev = new InterceptorElementEvent(processBusinessKey, processDefinitionId, executionId, elementId, scopeId, variables);
        for (ExecutionInterceptor i : interceptors) {
            i.onElement(ev);
        }
    }

    public void fireOnScopeCreated(String processBusinessKey, String processDefinitionId, UUID executionId, UUID scopeId, String elementId)
            throws  ExecutionException{

        InterceptorScopeCreatedEvent ev = new InterceptorScopeCreatedEvent(processBusinessKey, processDefinitionId, executionId, scopeId, elementId);
        for (ExecutionInterceptor i : interceptors) {
            i.onScopeCreated(ev);
        }
    }

    public void fireOnScopeDestroyed(String processBusinessKey, UUID executionId, UUID scopeId)
            throws  ExecutionException{

        InterceptorScopeDestroyedEvent ev = new InterceptorScopeDestroyedEvent(processBusinessKey, executionId, scopeId);
        for (ExecutionInterceptor i : interceptors) {
            i.onScopeDestroyed(ev);
        }
    }
}