package io.takari.bpm.el;

import io.takari.bpm.api.ExecutionContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ExpressionManager {

    <T> T eval(ExecutionContext ctx, String expr, Class<T> type);

    @SuppressWarnings("unchecked")
    default Object interpolate(ExecutionContext ctx, Object v) {
        if (v instanceof String) {
            String s = (String) v;
            if (!s.contains("${")) {
                return s;
            }
            return eval(ctx, s, Object.class);
        } else if (v instanceof Map) {
            Map<Object, Object> m = (Map<Object, Object>) v;
            if (m.isEmpty()) {
                return v;
            }

            Map<Object, Object> mm = new HashMap<>(m.size());
            for (Map.Entry<Object, Object> e : m.entrySet()) {
                mm.put(e.getKey(), interpolate(ctx, e.getValue()));
            }

            return mm;
        } else if (v instanceof List) {
            List src = (List) v;
            if (src.isEmpty()) {
                return v;
            }

            List dst = new ArrayList(src.size());
            for (Object vv : src) {
                dst.add(interpolate(ctx, vv));
            }

            return dst;
        } else if (v instanceof Object[]) {
            Object[] src = (Object[]) v;
            if (src.length == 0) {
                return v;
            }

            for (int i = 0; i < src.length; i++) {
                src[i] = interpolate(ctx, src[i]);
            }
        }

        return v;
    }
}
