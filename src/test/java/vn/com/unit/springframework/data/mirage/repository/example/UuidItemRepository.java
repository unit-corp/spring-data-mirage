package vn.com.unit.springframework.data.mirage.repository.example;

import java.util.UUID;

import vn.com.unit.sparwings.spring.data.repository.ScannableRepository;
import vn.com.unit.sparwings.spring.data.repository.UpsertableRepository;

/**
 * Repository interface for {@link UuidItem}.
 */
public interface UuidItemRepository extends ScannableRepository<UuidItem, UUID>, UpsertableRepository<UuidItem, UUID> {
}
