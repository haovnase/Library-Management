package com.library.mvc.librarymanagement.controller;

import com.library.mvc.librarymanagement.entity.*;
import com.library.mvc.librarymanagement.repository.BookRepository;
import com.library.mvc.librarymanagement.repository.BorrowBookRepository;
import com.library.mvc.librarymanagement.repository.CustomerRepository;

import com.library.mvc.librarymanagement.service.PreOrderBookService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
@SessionAttributes({
        "totalMembers",
        "booksBorrowing",
        "overdueBooks",
        "newBooksThisMonth",
        "borrow",
        "preOrderBooks"
})
public class ManagerController {

    private final BookRepository bookRepository;
    private final CustomerRepository customerRepository;
    private final BorrowBookRepository borrowBookRepository;
    private final PreOrderBookService preOrderBookService;

    public ManagerController(BookRepository bookRepository, CustomerRepository customerRepository,
                             BorrowBookRepository borrowBookRepository, PreOrderBookService preOrderBookService) {
        this.bookRepository = bookRepository;
        this.customerRepository = customerRepository;
        this.borrowBookRepository = borrowBookRepository;
        this.preOrderBookService = preOrderBookService;
    }

    @GetMapping("/manager/dashboard")
    public String dashboard(@SessionAttribute(name = "user", required = false) User user,
                            Model model) {
        if (user == null || !"manager".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        if (!model.containsAttribute("activeTab")) {
            model.addAttribute("activeTab", "dashboard");
        }
        model.addAttribute("totalMembers", customerRepository.count());
        model.addAttribute("booksBorrowing", borrowBookRepository.countByStatus("Borrowing"));
        model.addAttribute("overdueBooks", borrowBookRepository.countByStatusAndDeadlineBefore("Borrowing", LocalDate.now()));
        model.addAttribute("newBooksThisMonth", 34);
        model.addAttribute("preOrderBooks", preOrderBookService.findAll());
        List<BorrowBook> borrowBooks =
                borrowBookRepository.findTop10ByOrderByBorrowDateDesc();

        model.addAttribute("borrow", borrowBooks);


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
        redirectAttributes.addFlashAttribute("activeTab", "add-book");
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
        model.addAttribute("activeTab", "borrow-requests");

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
            redirectAttributes.addFlashAttribute("activeTab", "borrow-requests");
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
        borrow.setFine(0);
        borrow.setDelay(0);

        borrowBookRepository.save(borrow);

        bookEntity.setQuantity(bookEntity.getQuantity() - 1);
        bookRepository.save(bookEntity);

        redirectAttributes.addFlashAttribute("successMessage", "Mượn sách thành công.");
        redirectAttributes.addFlashAttribute("activeTab", "borrow-requests");
        return "redirect:/manager/dashboard";
    }

    @PostMapping("/manager/return/check")
    public String checkReturn(
            @SessionAttribute(name = "user", required = false) User user,
            @RequestParam("userId") String customerId,
            Model model) {

        if (user == null || !"manager".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        Customer customer = customerRepository.findById(customerId).orElse(null);

        model.addAttribute("customer", customer);
        model.addAttribute("user", user);
        model.addAttribute("activeTab", "return-requests");

        if (customer == null) {
            model.addAttribute("errorMessage", "Không tìm thấy khách hàng.");
            return "manager/dashboard";
        }

        List<BorrowBook> borrowBooks = borrowBookRepository.findByCustomerAndStatus(customer, "Borrowing");
        borrowBooks.forEach(b -> {
            LocalDate dueDate = b.getDeadline();
            LocalDate today = LocalDate.now();
            long daysLate = ChronoUnit.DAYS.between(dueDate, today);

            double fine = daysLate > 0 ? daysLate * 5000 : 0;

            b.setFine(fine); // chỉ set để hiển thị UI, không cần lưu DB
        });
        model.addAttribute("borrowBooks", borrowBooks);

        return "manager/dashboard";
    }

    @PostMapping("/manager/return/confirm")
    public String confirmReturn(
            @SessionAttribute(name = "user", required = false) User user,
            @RequestParam("borrowId") String borrowId,
            Model model) {

        if (user == null || !"manager".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("activeTab", "return-requests");

        BorrowBook b = borrowBookRepository.findById(borrowId).orElse(null);

        if (b == null) {
            model.addAttribute("errorMessage", "Không tìm thấy phiếu mượn.");
            return "manager/dashboard";
        }

        LocalDate dueDate = b.getDeadline();
        LocalDate today = LocalDate.now();
        long daysLate = ChronoUnit.DAYS.between(dueDate, today);

        double fine = daysLate > 0 ? daysLate * 5000 : 0;

        b.setFine(fine);
        b.setReturnDate(today);
        b.setStatus("Returned");
        borrowBookRepository.save(b);

        Book book = b.getBook();
        book.setQuantity(book.getQuantity() + 1);
        bookRepository.save(book);

        Customer customer = b.getCustomer();
        model.addAttribute("customer", customer);

        if (customer == null) {
            model.addAttribute("errorMessage", "Không tìm thấy khách hàng.");
            return "manager/dashboard";
        }

        List<BorrowBook> borrowBooks = borrowBookRepository.findByCustomerAndStatus(customer, "Borrowing");
        borrowBooks.forEach(a -> {
            LocalDate d = a.getDeadline();
            LocalDate t = LocalDate.now();
            long late = ChronoUnit.DAYS.between(d, t);

            double f = late > 0 ? late * 5000 : 0;

            a.setFine(f); // chỉ set để hiển thị UI, không cần lưu DB
        });
        model.addAttribute("borrowBooks", borrowBooks);

        return "manager/dashboard";
    }

    //xem danh sách đặt trước

    @GetMapping("/manager/dashboard/preorder")
    public String preorderTab(
            @SessionAttribute(name = "user", required = false) User user,
            Model model) {

        if (user == null || !"manager".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("preOrderBooks", preOrderBookService.findAll());
        model.addAttribute("user", user);

        model.addAttribute("activeTab", "preorder");

        return "manager/dashboard";
    }
    // chuyển đơn sang Ready

    @PostMapping("/manager/preorder/ready")
    public String readyPreOrder(
            @SessionAttribute(name = "user", required = false) User user,
            @RequestParam String id) {

        if (user == null || !"manager".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        PreOrderBook preOrderBook =
                preOrderBookService.findById(id);

        if ("Waiting".equals(preOrderBook.getStatus())) {
            preOrderBook.setStatus("Ready");
            preOrderBookService.save(preOrderBook);
        }

        return "redirect:/manager/dashboard/preorder";
    }

    // chuyển đơn sang complete

    @PostMapping("/manager/preorder/complete")
    public String completePreOrder(
            @SessionAttribute(name = "user", required = false) User user,
            @RequestParam String id) {

        if (user == null || !"manager".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        PreOrderBook preOrderBook =
                preOrderBookService.findById(id);

        if ("Ready".equals(preOrderBook.getStatus())) {
            preOrderBook.setStatus("Completed");
            preOrderBookService.save(preOrderBook);
        }

        return "redirect:/manager/dashboard/preorder";
    }

    // hủy đơn(Cancel)

    @PostMapping("/manager/preorder/cancel")
    public String cancelPreOrder(
            @SessionAttribute(name = "user", required = false) User user,
            @RequestParam String id) {

        if (user == null || !"manager".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        PreOrderBook preOrderBook =
                preOrderBookService.findById(id);

        if (!"Completed".equals(preOrderBook.getStatus())
                && !"Cancelled".equals(preOrderBook.getStatus())) {

            preOrderBook.setStatus("Cancelled");
            preOrderBookService.save(preOrderBook);
        }

        return "redirect:/manager/dashboard/preorder";
    }

}
