package no.entur.mummu.util;

import org.rutebanken.netex.model.EntityStructure;

import java.util.Comparator;

public class NetexEntityComparator implements Comparator<EntityStructure> {
    @Override
    public int compare(EntityStructure o1, EntityStructure o2) {
        return Integer.compare(
                Integer.parseInt(o1.getId().split(":")[2]),
                Integer.parseInt(o2.getId().split(":")[2])
        );
    }
}
