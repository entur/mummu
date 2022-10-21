package no.entur.mummu.util;

import org.rutebanken.netex.model.TariffZone;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class TariffZoneAuthorityRefFilter implements Predicate<TariffZone> {
    private final Collection<String> authorityRefs;

    public TariffZoneAuthorityRefFilter(Collection<String> authorityRefs) {
        this.authorityRefs = authorityRefs;
    }

    @Override
    public boolean test(TariffZone tariffZone) {
        return authorityRefs == null || authorityRefs.stream().anyMatch(
                authorityRef -> tariffZone.getId().split(":")[0].equals(authorityRef)
        );
    }
}
