package shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.model.Location;

import java.util.List;
import java.util.UUID;

public interface LocationRepository
        extends JpaRepository<Location, UUID> {

    List<Location> findAllByOrderByIdAsc();
}