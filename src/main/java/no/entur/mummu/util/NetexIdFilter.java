package no.entur.mummu.util;

import org.rutebanken.netex.model.EntityStructure;

import java.util.List;
import java.util.function.Predicate;

public class NetexIdFilter implements Predicate<EntityStructure> {
    private final List<String> ids;

    public NetexIdFilter(List<String> ids) {
        this.ids = ids;
    }

    @Override
    public boolean test(EntityStructure entityStructure) {
        return ids == null || ids.contains(entityStructure.getId());
    }
}
