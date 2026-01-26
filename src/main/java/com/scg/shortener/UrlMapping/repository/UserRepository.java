package com.scg.shortener.UrlMapping.repository;

import com.scg.shortener.User.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
