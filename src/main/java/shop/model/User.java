package shop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "ShopUser")
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String passwordHash;

    @Column(name = "email_address", nullable = false, unique = true)
    private String emailAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    /** For trusted application code only. The password must already be encoded. */
    public User(String firstName, String lastName, String username,
                String passwordHash, String emailAddress, UserRole role) {
        this.firstName = ModelValidation.requiredText(firstName, 100, "First name");
        this.lastName = ModelValidation.requiredText(lastName, 100, "Last name");
        this.username = ModelValidation.requiredText(username, 100, "Username");
        this.emailAddress = ModelValidation.requiredText(emailAddress, 255, "Email address");
        this.role = Objects.requireNonNull(role, "Role is required");
        changePasswordHash(passwordHash);
    }

    /** Accepts only an encoded password supplied by the future authentication service. */
    public void changePasswordHash(String passwordHash) {
        this.passwordHash = ModelValidation.requiredText(passwordHash, 255, "Password hash");
    }
}
