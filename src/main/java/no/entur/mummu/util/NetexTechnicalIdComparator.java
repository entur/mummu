package no.entur.mummu.util;

import org.rutebanken.netex.model.EntityStructure;

import java.util.Comparator;

public class NetexTechnicalIdComparator implements Comparator<EntityStructure>  {
    @Override
    public int compare(EntityStructure o1, EntityStructure o2) {
        return o1.getId().compareTo(o2.getId());
    }
}
