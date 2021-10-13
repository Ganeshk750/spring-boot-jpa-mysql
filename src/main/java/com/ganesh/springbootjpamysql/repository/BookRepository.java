package com.ganesh.springbootjpamysql.repository;


import com.ganesh.springbootjpamysql.model.Book;
import com.ganesh.springbootjpamysql.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);

}
