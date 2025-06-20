package com.gabeust.literalura.repository;

import com.gabeust.literalura.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookRepository extends JpaRepository<Book, Long> {

}
