package com.gabeust.literalura.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;
@Data
@Entity
@Table(name = "books")
public class Book {

    @Id
    private Long id;

    private String title;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "book_summaries", joinColumns = @JoinColumn(name = "book_id"))
    @Column(columnDefinition = "TEXT")
    private List<String> summaries;


    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    @ToString.Exclude
    private List<Author> authors;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "book_languages", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "language")
    private List<String> languages;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "book_subjects", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "subject")
    private List<String> subjects;

    private Integer downloadCount;
}
