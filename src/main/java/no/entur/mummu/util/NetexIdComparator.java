package no.entur.mummu.util;

import org.rutebanken.netex.model.EntityStructure;

import java.util.Comparator;

public class NetexIdComparator implements Comparator<EntityStructure> {
    @Override
    public int compare(EntityStructure o1, EntityStructure o2) {
        String[] firstNetexId = o1.getId().split(":");
        String[] secondNetexId = o2.getId().split(":");

        String firstCodespace = firstNetexId[0];
        String secondCodespace = secondNetexId[0];

        int sortedByCodespace = firstCodespace.compareTo(secondCodespace);

        if (sortedByCodespace != 0) {
            return sortedByCodespace;
        }

        String firstId = firstNetexId[2];
        String secondId = secondNetexId[2];

        int sortedByIdLength = Integer.compare(firstId.length(), secondId.length());

        if (sortedByIdLength != 0) {
            return sortedByIdLength;
        }

        return firstId.compareTo(secondId);
    }
}
