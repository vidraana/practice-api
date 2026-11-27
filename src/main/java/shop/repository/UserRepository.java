package shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.model.User;

import java.util.UUID;

public interface UserRepository
        extends JpaRepository<User, UUID> {
}