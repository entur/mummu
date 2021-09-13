package no.entur.mummu.util;

import org.rutebanken.netex.model.EntityStructure;

import java.util.Comparator;

public class NetexIdComparator implements Comparator<EntityStructure> {
    @Override
    public int compare(EntityStructure o1, EntityStructure o2) {
        String firstId = o1.getId();
        String secondId = o2.getId();

        int sorted = Integer.compare(firstId.length(), secondId.length());

        if (sorted == 0) {
            return firstId.compareTo(secondId);
        } else {
            return sorted;
        }
    }
}
