package com.library.mvc.librarymanagement.controller;

import com.library.mvc.librarymanagement.entity.Book;
import com.library.mvc.librarymanagement.entity.BorrowBook;
import com.library.mvc.librarymanagement.entity.Customer;
import com.library.mvc.librarymanagement.entity.User;
import com.library.mvc.librarymanagement.repository.BookRepository;
import com.library.mvc.librarymanagement.repository.BorrowBookRepository;
import com.library.mvc.librarymanagement.repository.CustomerRepository;
import com.library.mvc.librarymanagement.repository.UserRepository;
import com.library.mvc.librarymanagement.service.BookService;
import com.library.mvc.librarymanagement.service.BorrowBookService;
import com.library.mvc.librarymanagement.service.CustomerService;
import com.library.mvc.librarymanagement.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SessionAttributes("keyword")
@Controller
public class HomeController {

    private final BookService bookService;
    private final BorrowBookService borrowBookService;
    private final CustomerService customerService;
    private final UserService userService;

    public HomeController(BookService bookService,
            BorrowBookService borrowBookService,
            CustomerService customerService,
            UserService userService) {
        this.bookService = bookService;
        this.borrowBookService = borrowBookService;
        this.customerService = customerService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(@SessionAttribute(name = "user", required = false) User user,
            Model model) {

        if (user != null) {
            model.addAttribute("user", user);
        }

        model.addAttribute("stats", List.of(
                new Stat("1.2M+", "Tổng số sách"),
                new Stat("24k", "Thành viên tích cực"),
                new Stat("850k", "Tài nguyên số")));

        model.addAttribute("books", List.of(
                new BookItem("History of Form", "Prof. Julian S. Miller",
                        "https://lh3.googleusercontent.com/aida-public/AB6AXuCCizBJYK9i8doUeLlc1Yl5aHSXV9IGBVs7g_WFsjh-VbOwvc4PigfNaPrHi8GqdBTOVf3MMwkJRML7ifR44CkkejALyRqHordSCdQKPT5VUil053U6TjDkziyRgSK2LrzEvPWc0t4VSstbiCsSmYnnM_iDYWjUXbPQaKiZEI5XlbrjCs1O887z9UM7xIzKfs2UO7yd6Mtj2S1qgvj_xRm_uj0gA4fb0uDcy7xFfqp3oWQC48HJT6BmRWT_8eLXj1_lo8bQAPens0nr",
                        "Kiểm tra tình trạng", "available"),
                new BookItem("The Quantum Mind", "Dr. Elena Vance",
                        "https://lh3.googleusercontent.com/aida-public/AB6AXuCjwzyYfdkfhRCsBI-MRivRg_ga-KgoX_oaM3nGAKLfs_-d0sHKgLzTeBxWfkOO_M3WFqUOcok3b9-2e4UaDVUOkVQGt7Tdy28nvD0lmp-pRZBtleDx8I1DmlJaKNHYdu_eLR-OqejEIh9gqV7eDY6aFvl6_uVBkQtiOWhTdeU_YBEwgwlCl_4zJqqoAPjRLco8w8M3INK2P-8bJvhSF0oZlRikcx_4vyP79QltJgQEdpaSVDfUsF2FR82JZArY7yuUXALLT9zRePu1",
                        "Đặt trước", "available"),
                new BookItem("Medieval Cartography", "Sir Thomas Greene",
                        "https://lh3.googleusercontent.com/aida-public/AB6AXuDWmPfMt2vUPlGtyUjzLgkDkU2ePL3O6rmL1AJe7e4rHMRZO876jErRJfYF1uQc7_YMgThMXJtFzX3D4JN2byA7sWYpscUeJmneQSdR43PFEtWOBKDDvP-CrISS3wbogntssKT5uXBFWBgdy7w3gJrZHJcV64xx3JteExrbyuZOURRJGGtmk4u892jYFfU1i58KWgoeLEmFmWZLhiqfI5_SBkBE92c9rPfcF5pdm6S4ap1NRZQxTe3YEKXpZX2RRXmn7xLQ7lqUECRQ",
                        "Đã mượn", "borrowed"),
                new BookItem("Sustainable Ecosystems", "Maya K. Reynolds",
                        "https://lh3.googleusercontent.com/aida-public/AB6AXuChSx-XLvJDQPgqsuJZEpXgzp4ODvHzfTNHAz_C1K-7GrN0BTeq-VsebuEo5QdEdwgsBXYToNyv_h9nUGbSIj6Gngr4zsHeMuFbpDeijLOVruB-gS3G4bde9AJ5SH5iIuoOWO6jn1skWD0k72e4bMNLmftO076lUp3HZ_vdPnkXuNRKmK7XBwcUdM6VDIO2QoBGLYQ41leyQ0ifWzit7slQFBg4DsIEzjFgfR1IoDtKLpN_0qgnwyA1irlnJulWAOsocMxBUUmrPPlU",
                        "Kiểm tra tình trạng", "available")));

        model.addAttribute("categories", List.of(
                new Category("science", "Khoa học"),
                new Category("menu_book", "Văn học"),
                new Category("history_edu", "Lịch sử"),
                new Category("devices", "Công nghệ"),
                new Category("palette", "Nghệ thuật"),
                new Category("calculate", "Toán học"),
                new Category("gavel", "Luật"),
                new Category("public", "Xã hội")));

        return "home";
    }

    private record Stat(String value, String label) {
    }

    private record BookItem(String title,
            String author,
            String image,
            String actionLabel,
            String status) {
    }

    private record Category(String icon,
            String name) {
    }

    @GetMapping("/books/search")
    public String searchBooks(@RequestParam("keyword") String keyword,
            Model model,
            HttpSession session) {

        List<Book> books = bookService.search(keyword);

        model.addAttribute("user", session.getAttribute("user"));
        model.addAttribute("books", books);

        session.setAttribute("keyword", keyword);
        model.addAttribute("keyword", keyword);

        return "search";
    }

    @GetMapping("/books/{id}")
    public String viewBookDetail(@PathVariable String id,
            Model model,
            HttpSession session) {

        Book book = bookService.findById(id);

        model.addAttribute("book", book);
        model.addAttribute("keyword", session.getAttribute("keyword"));
        model.addAttribute("user", session.getAttribute("user"));

        return "bookdetail";
    }

    @GetMapping("/borrow")
    public String borrowBook(Model model, HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        Customer customer = customerService.findByUserId(user.getId());

        List<BorrowBook> borrowList = borrowBookService.findByCustomer(customer);

        borrowList.forEach(b -> {
            if (b.getReturnDate() != null) {
                b.setStatus("Returned");
            } else if (b.getDeadline().isBefore(LocalDate.now())) {
                b.setStatus("Overdue");
            } else {
                b.setStatus("Borrowing");
            }
        });

        model.addAttribute("borrowList", borrowList);
        model.addAttribute("user", user);

        return "myborrow";
    }

    @GetMapping("/borrow/renew/{id}")
    public String renewBorrow(@PathVariable String id) {

        borrowBookService.renewBorrow(id);

        return "redirect:/borrow";
    }

    @GetMapping("/manager/members/add")
    public String showAddMemberForm(Model model,
            HttpSession session) {

        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("activeTab", "add-member");

        return "manager/dashboard";
    }

    @PostMapping("/manager/members/add")
    public String addMember(
            @RequestParam String fullName,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String phone,
            Model model,
            HttpSession session) {

        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("activeTab", "add-member");

        try {

            if (userService.existsByUsername(username)) {
                model.addAttribute("errorMessage",
                        "Tên đăng nhập đã tồn tại.");
                return "manager/dashboard";
            }

            User newUser = new User();
            newUser.setId(userService.generateNextUserId());
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setRole("customer");
            newUser.setFullName(fullName);
            newUser.setStatus("active");

            newUser = userService.save(newUser);

            Customer customer = new Customer();
            customer.setId(customerService.generateNextCustomerId());
            customer.setUser(newUser);
            customer.setPhone(phone);

            customerService.save(customer);

            model.addAttribute("successMessage",
                    "Thêm thành viên thành công.");

        } catch (Exception e) {

            model.addAttribute("errorMessage",
                    "Có lỗi xảy ra: " + e.getMessage());
        }

        return "manager/dashboard";
    }
}
