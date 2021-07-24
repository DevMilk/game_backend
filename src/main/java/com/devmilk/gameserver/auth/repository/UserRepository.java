package com.devmilk.gameserver.auth.repository;

import java.util.Optional;

import com.devmilk.gameserver.auth.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUserId(Long userId);
}
