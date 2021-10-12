package com.ganesh.springbootjpamysql.repository;


import com.ganesh.springbootjpamysql.model.Book;
import com.ganesh.springbootjpamysql.model.Lend;
import com.ganesh.springbootjpamysql.model.LendStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LendRepository extends JpaRepository<Lend, Long> {
    Optional<Lend> findByBookAndStatus(Book book, LendStatus burrowed);
}
