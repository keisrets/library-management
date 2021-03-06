package kn18012.librarymanagement.repository;

import kn18012.librarymanagement.domain.Role;
import kn18012.librarymanagement.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findUserByRolesContaining(Role role);

    Page<User> findByEmailIgnoreCaseContainingOrFirstNameIgnoreCaseContainingOrLastNameIgnoreCaseContaining(String email, String firstName, String lastName, Pageable pageable);
}