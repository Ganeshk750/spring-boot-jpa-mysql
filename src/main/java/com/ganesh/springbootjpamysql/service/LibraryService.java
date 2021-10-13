package com.ganesh.springbootjpamysql.service;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.ganesh.springbootjpamysql.model.*;
import com.ganesh.springbootjpamysql.repository.AuthorRepository;
import com.ganesh.springbootjpamysql.repository.BookRepository;
import com.ganesh.springbootjpamysql.repository.LendRepository;
import com.ganesh.springbootjpamysql.repository.MemberRepository;
import com.ganesh.springbootjpamysql.request.AuthorCreationRequest;
import com.ganesh.springbootjpamysql.request.BookCreationRequest;
import com.ganesh.springbootjpamysql.request.BookLendRequest;
import com.ganesh.springbootjpamysql.request.MemberCreationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final AuthorRepository authorRepository;
    private final MemberRepository memberRepository;
    private final LendRepository lendRepository;
    private final BookRepository bookRepository;

    public Book readBook(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            return book.get();
        }
        throw new EntityNotFoundException("Cant find any book under given ID");
    }

    public List<Book> readBooks() {
        return bookRepository.findAll();
    }

    public Book readBook(String isbn) {
        Optional<Book> book = bookRepository.findByIsbn(isbn);
        if (book.isPresent()) {
            return book.get();
        }
        throw new EntityNotFoundException("Cant find any book under given ISBN");
    }

    public Book createBook(BookCreationRequest book) {
        Optional<Author> author = authorRepository.findById(book.getAuthorId());
        if (!author.isPresent()) {
            throw new EntityNotFoundException("Author Not Found");
        }
        Book bookToCreate = new Book();
        BeanUtils.copyProperties(book, bookToCreate);
        bookToCreate.setAuthor(author.get());
        return bookRepository.save(bookToCreate);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public Member createMember(MemberCreationRequest request) {
        Member member = new Member();
        BeanUtils.copyProperties(request, member);
        member.setStatus(MemberStatus.ACTIVE);
        return memberRepository.save(member);
    }

    public Member updateMember (Long id, MemberCreationRequest request) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        if (!optionalMember.isPresent()) {
            throw new EntityNotFoundException("Member not present in the database");
        }
        Member member = optionalMember.get();
        member.setLastName(request.getLastName());
        member.setFirstName(request.getFirstName());
        return memberRepository.save(member);
    }

    public Author createAuthor (AuthorCreationRequest request) {
        Author author = new Author();
        BeanUtils.copyProperties(request, author);
        return authorRepository.save(author);
    }

    public List<String> lendABook (List<BookLendRequest> list){
        List<String> booksApprovedToBurrow = new ArrayList<>();
        list.forEach(bookLendRequest -> {
            Optional<Book> bookForId = bookRepository.findById(bookLendRequest.getBookId());
            if(!bookForId.isPresent()){
                throw new EntityNotFoundException("Cant find any book under given Id");
            }

            Optional<Member> memberForId = memberRepository.findById(bookLendRequest.getMemberId());
            if(!memberForId.isPresent()){
                throw new EntityNotFoundException("Member not present in the database");
            }

            Member member = memberForId.get();
            if (member.getStatus() != MemberStatus.ACTIVE) {
                throw new RuntimeException("User is not active to proceed a lending.");
            }

            Optional<Lend> burrowedBook = lendRepository.findByBookAndStatus(bookForId.get(), LendStatus.BURROWED);
            if(!burrowedBook.isPresent()){
                booksApprovedToBurrow.add(bookForId.get().getName());
                Lend lend = new Lend();
                lend.setMember(memberForId.get());
                lend.setBook(bookForId.get());
                lend.setStatuss(LendStatus.BURROWED);
                lend.setStartOn(Instant.now());
                lend.setDueOn(Instant.now().plus(30, ChronoUnit.DAYS));
                lendRepository.save(lend);
            }
        });
        return booksApprovedToBurrow;
    }


    public List<Author> readAuthors() {
        return authorRepository.findAll();
    }

    public Book updateBook(Long bookId, BookCreationRequest request) {
        Optional<Author> author = authorRepository.findById(request.getAuthorId());
        if (!author.isPresent()) {
            throw new EntityNotFoundException("Author Not Found");
        }
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (!optionalBook.isPresent()) {
            throw new EntityNotFoundException("Book Not Found");
        }
        Book book = optionalBook.get();
        book.setIsbn(request.getIsbn());
        book.setName(request.getName());
        book.setAuthor(author.get());
        return bookRepository.save(book);
    }

    public List<Member> readMembers() {
        return memberRepository.findAll();
    }

}
