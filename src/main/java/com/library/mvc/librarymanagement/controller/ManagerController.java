package com.library.mvc.librarymanagement.controller;

import com.library.mvc.librarymanagement.entity.Book;
import com.library.mvc.librarymanagement.entity.BorrowBook;
import com.library.mvc.librarymanagement.entity.Customer;
import com.library.mvc.librarymanagement.entity.User;
import com.library.mvc.librarymanagement.repository.BookRepository;
import com.library.mvc.librarymanagement.repository.BorrowBookRepository;
import com.library.mvc.librarymanagement.repository.CustomerRepository;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

@Controller
public class ManagerController {

    private final BookRepository bookRepository;
    private final CustomerRepository customerRepository;
    private final BorrowBookRepository borrowBookRepository;

    public ManagerController(BookRepository bookRepository, CustomerRepository customerRepository,
            BorrowBookRepository borrowBookRepository) {
        this.bookRepository = bookRepository;
        this.customerRepository = customerRepository;
        this.borrowBookRepository = borrowBookRepository;
    }

    @GetMapping("/manager/dashboard")
    public String dashboard(@SessionAttribute(name = "user", required = false) User user,
            Model model) {
        if (user == null || !"manager".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        if(!model.containsAttribute("activeTab")) {
            model.addAttribute("activeTab", "dashboard");
        }
        return "manager/dashboard";
    }

    @PostMapping("/manager/books/add")
    public String addBook(@SessionAttribute(name = "user", required = false) User user,
            @RequestParam("bookName") String bookName,
            @RequestParam("author") String author,
            @RequestParam("isbn") String isbn,
            @RequestParam("type") String type,
            @RequestParam("language") String language,
            @RequestParam("year") Integer year,
            @RequestParam("publisher") String publisher,
            @RequestParam("quantity") Integer quantity,
            @RequestParam("location") String location,
            @RequestParam(value = "pages", required = false) Integer pages,
            @RequestParam("description") String description,
            @RequestParam(value = "bookJacket", required = false) MultipartFile bookJacket,
            RedirectAttributes redirectAttributes) {
        if (user == null || !"manager".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        StringBuilder errorMessage = new StringBuilder();

        if (bookName == null || bookName.trim().isEmpty()) {
            errorMessage.append("Tên sách không được để trống. ");
        }
        if (author == null || author.trim().isEmpty()) {
            errorMessage.append("Tác giả không được để trống. ");
        }
        if (isbn == null || isbn.trim().isEmpty()) {
            errorMessage.append("ISBN không được để trống. ");
        } else if (bookRepository.findByIsbn(isbn.trim()).isPresent()) {
            errorMessage.append("ISBN đã tồn tại trong hệ thống. ");
        }
        if (type == null || type.trim().isEmpty()) {
            errorMessage.append("Danh mục không được để trống. ");
        }
        if (language == null || language.trim().isEmpty()) {
            errorMessage.append("Ngôn ngữ không được để trống. ");
        }
        if (publisher == null || publisher.trim().isEmpty()) {
            errorMessage.append("Nhà xuất bản không được để trống. ");
        }
        if (quantity == null || quantity <= 0) {
            errorMessage.append("Số lượng phải lớn hơn 0. ");
        }
        if (location == null || location.trim().isEmpty()) {
            errorMessage.append("Vị trí không được để trống. ");
        }
        if (year == null || year < 1800 || year > LocalDate.now().getYear() + 1) {
            errorMessage.append("Năm xuất bản phải từ 1800 đến năm hiện tại + 1. ");
        }
        if (pages == null || pages <= 0 || pages > 5000) {
            errorMessage.append("Số trang phải nằm trong khoảng 1 đến 5000. ");
        }
        if (description == null || description.trim().isEmpty()) {
            errorMessage.append("Tóm tắt nội dung không được để trống. ");
        }

        if (errorMessage.length() > 0) {
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage.toString());
            return "redirect:/manager/dashboard";
        }

        long nextBookNumber = bookRepository.count() + 1;
        String bookId = "B" + String.format("%03d", nextBookNumber);

        String bookJacketPath = "";
        if (bookJacket != null && !bookJacket.isEmpty()) {
            try {
                Path uploadDir = Paths.get("src/main/resources/static/uploads");
                Files.createDirectories(uploadDir);
                String fileName = bookId + ".jpg";
                Path target = uploadDir.resolve(fileName);
                Files.copy(bookJacket.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
                bookJacketPath = "/uploads/" + fileName;
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể tải ảnh bìa sách lên. ");
                return "redirect:/manager/dashboard";
            }
        }

        Book book = new Book();
        book.setId(bookId);
        book.setBookName(bookName.trim());
        book.setAuthor(author.trim());
        book.setIsbn(isbn.trim());
        book.setType(type.trim());
        book.setLanguage(language.trim());
        book.setYear(year);
        book.setPublisher(publisher.trim());
        book.setQuantity(quantity);
        book.setLocation(location.trim());
        book.setDescription(description.trim());
        book.setBookJacket(bookJacketPath);

        bookRepository.save(book);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm sách thành công!");
        return "redirect:/manager/dashboard";
    }

    @PostMapping("/manager/borrow/check")
    public String checkBorrow(
            @SessionAttribute(name = "user", required = false) User user,
            @RequestParam String member,
            @RequestParam String book,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate,
            Model model) {

        if (user == null || !"manager".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        Customer customer = customerRepository.findById(member).orElse(null);
        Book bookEntity = bookRepository.findById(book).orElse(null);

        boolean canBorrow = true;
        String message = "";

        if (customer == null) {
            canBorrow = false;
            message = "Không tìm thấy khách hàng.";
        } else if (bookEntity == null) {
            canBorrow = false;
            message = "Không tìm thấy sách.";
        } else if (bookEntity.getQuantity() <= 0) {
            canBorrow = false;
            message = "Sách đã hết.";
        }

        model.addAttribute("user", user);

        model.addAttribute("member", customer);
        model.addAttribute("book", bookEntity);
        model.addAttribute("returnDate", returnDate);
        model.addAttribute("canBorrow", canBorrow);
        model.addAttribute("message", message);
        model.addAttribute("activetTab", "borrow-requests");
        System.out.println("Member: " + member);
        System.out.println("canBorrow = " + canBorrow);
        System.out.println("Book: " + book);
        System.out.println("Customer: " + customer);
        System.out.println("BookEntity: " + bookEntity);
        return "manager/dashboard";
    }

    @PostMapping("/manager/borrow/confirm")
    public String confirmBorrow(
            @SessionAttribute(name = "user", required = false) User user,
            @RequestParam String member,
            @RequestParam String book,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate,
            RedirectAttributes redirectAttributes) {

        if (user == null || !"manager".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        Customer customer = customerRepository.findById(member).orElse(null);
        Book bookEntity = bookRepository.findById(book).orElse(null);

        if (customer == null || bookEntity == null || bookEntity.getQuantity() <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể mượn sách.");
            redirectAttributes.addFlashAttribute("activetTab", "borrow-requests");
            return "redirect:/manager/dashboard";
        }

        long total = borrowBookRepository.count() + 1;

        BorrowBook borrow = new BorrowBook();
        borrow.setId(String.format("BR%03d", total));
        borrow.setCustomer(customer);
        borrow.setBook(bookEntity);
        borrow.setBorrowDate(LocalDate.now());
        borrow.setDeadline(returnDate);
        borrow.setReturnDate(null);
        borrow.setQuantity(1);
        borrow.setStatus("Borrowing");
        borrow.setDescription("");
        borrow.setFine(BigDecimal.ZERO);

        borrowBookRepository.save(borrow);

        bookEntity.setQuantity(bookEntity.getQuantity() - 1);
        bookRepository.save(bookEntity);

        redirectAttributes.addFlashAttribute("successMessage", "Mượn sách thành công.");
        redirectAttributes.addFlashAttribute("activeTab","borrow-requests");
        return "redirect:/manager/dashboard";
    }

}
