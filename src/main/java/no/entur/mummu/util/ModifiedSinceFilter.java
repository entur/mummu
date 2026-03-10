package no.entur.mummu.util;

import org.rutebanken.netex.model.EntityInVersionStructure;

import java.time.LocalDateTime;
import java.util.function.Predicate;

public class ModifiedSinceFilter implements Predicate<EntityInVersionStructure> {
  private final LocalDateTime modifiedSince;

  public ModifiedSinceFilter(LocalDateTime modifiedSince) {
    this.modifiedSince = modifiedSince;
  }

  @Override
  public boolean test(EntityInVersionStructure entityInVersionStructure) {
    if (modifiedSince == null) {
      return true;
    }
    if (entityInVersionStructure.getChanged() == null) {
      return false;
    }
    return entityInVersionStructure.getChanged().isAfter(modifiedSince);
  }
}
