package no.entur.mummu.web;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for the maximum {@code count} allowed on list endpoints.
 * <ul>
 *   <li>{@code no.entur.mummu.list.max-count} — the default ceiling (1000).</li>
 *   <li>{@code no.entur.mummu.list.max-count-overrides.<endpoint>} — per-endpoint overrides,
 *       keyed by the first path segment, e.g. {@code fare-zones}.</li>
 *   <li>{@code no.entur.mummu.list.enforce-max-count} — when false (default) over-limit
 *       requests are logged but served; when true they are rejected with 400.</li>
 * </ul>
 */
@Component
@ConfigurationProperties("no.entur.mummu.list")
public class MaxCountProperties {

    private long maxCount = 1000;
    private Map<String, Long> maxCountOverrides = new HashMap<>();
    private boolean enforceMaxCount = false;

    public long getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(long maxCount) {
        this.maxCount = maxCount;
    }

    public Map<String, Long> getMaxCountOverrides() {
        return maxCountOverrides;
    }

    public void setMaxCountOverrides(Map<String, Long> maxCountOverrides) {
        this.maxCountOverrides = maxCountOverrides;
    }

    public boolean isEnforceMaxCount() {
        return enforceMaxCount;
    }

    public void setEnforceMaxCount(boolean enforceMaxCount) {
        this.enforceMaxCount = enforceMaxCount;
    }

    /** The maximum count allowed for the given endpoint (first path segment), falling back to the default. */
    public long maxFor(String endpoint) {
        return maxCountOverrides.getOrDefault(endpoint, maxCount);
    }
}
