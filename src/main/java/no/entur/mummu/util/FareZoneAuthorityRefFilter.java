package no.entur.mummu.util;

import org.rutebanken.netex.model.FareZone;

import java.util.Collection;
import java.util.function.Predicate;

public class FareZoneAuthorityRefFilter implements Predicate<FareZone> {
    private final Collection<String> authorityRefs;

    public FareZoneAuthorityRefFilter(Collection<String> authorityRefs) {
        this.authorityRefs = authorityRefs;
    }

    @Override
    public boolean test(FareZone fareZone) {
        return authorityRefs == null || authorityRefs.stream().anyMatch(ref -> ref.equals(fareZone.getTransportOrganisationRef().getValue().getRef()));
    }
}
