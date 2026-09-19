package com.altis.library_backend.books.controllers;

import com.altis.library_backend.books.services.BookService;
import com.altis.library_backend.books.models.dtos.BookRequestDTO;
import com.altis.library_backend.books.models.dtos.BookResponseDTO;
import com.altis.library_backend.books.services.BookService;
import com.altis.library_backend.books.models.dtos.UpdateRequestDTO;
import com.altis.library_backend.books.models.dtos.UpdateResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.net.URI;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService){
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<BookResponseDTO> create(@RequestBody @Valid BookRequestDTO request){
        BookResponseDTO response = bookService.createBook(request);

        URI location = URI.create("/books/" + response.isbn());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BookResponseDTO>> getAll(){
        List<BookResponseDTO> responses = bookService.findAll();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getById(@PathVariable Long id){
        BookResponseDTO response = bookService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateResponseDTO> update(@PathVariable Long id, @RequestBody @Valid UpdateRequestDTO request) {
        UpdateResponseDTO response = bookService.updateBook(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BookResponseDTO> delete(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
