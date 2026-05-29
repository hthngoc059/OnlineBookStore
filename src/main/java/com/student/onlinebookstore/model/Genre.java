package com.student.onlinebookstore.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "genres")
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer genreId;
    
    @Column(unique = true, nullable = false, length = 100)
    private String genreName;
    
    @ManyToMany(mappedBy = "genres")
    private Set<Book> books = new HashSet<>();
    
    public Genre() {}
    
    public Genre(String genreName) {
        this.genreName = genreName;
    }
    
    // Getters and Setters
    public Integer getGenreId() { return genreId; }
    public void setGenreId(Integer genreId) { this.genreId = genreId; }
    
    public String getGenreName() { return genreName; }
    public void setGenreName(String genreName) { this.genreName = genreName; }
    
    public Set<Book> getBooks() { return books; }
    public void setBooks(Set<Book> books) { this.books = books; }
}