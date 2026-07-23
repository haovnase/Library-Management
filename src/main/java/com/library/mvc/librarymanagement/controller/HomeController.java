package com.library.mvc.librarymanagement.controller;

import com.library.mvc.librarymanagement.entity.Book;
import com.library.mvc.librarymanagement.entity.BorrowBook;
import com.library.mvc.librarymanagement.entity.Customer;
import com.library.mvc.librarymanagement.entity.PreOrderBook;
import com.library.mvc.librarymanagement.entity.User;
import com.library.mvc.librarymanagement.service.BookService;
import com.library.mvc.librarymanagement.service.BorrowBookService;
import com.library.mvc.librarymanagement.service.CustomerService;
import com.library.mvc.librarymanagement.service.PreOrderBookService;
import com.library.mvc.librarymanagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@SessionAttributes("keyword")
public class HomeController {

    private final BookService bookService;
    private final BorrowBookService borrowBookService;
    private final CustomerService customerService;
    private final UserService userService;
    private final PreOrderBookService preOrderBookService;

    public HomeController(
            BookService bookService,
            BorrowBookService borrowBookService,
            CustomerService customerService,
            UserService userService,
            PreOrderBookService preOrderBookService) {
        this.bookService = bookService;
        this.borrowBookService = borrowBookService;
        this.customerService = customerService;
        this.userService = userService;
        this.preOrderBookService = preOrderBookService;
    }

    @GetMapping("/")
    public String home(
            @SessionAttribute(name = "user", required = false) User user,
            Model model) {

        List<Book> books = bookService.findAll();

        model.addAttribute("user", user);
        model.addAttribute("stats", List.of(
                new Stat(String.valueOf(books.size()), "Đầu sách trong hệ thống"),
                new Stat("3", "Nhóm người dùng"),
                new Stat("24/7", "Tra cứu trực tuyến")));
        model.addAttribute("books", books.stream().limit(4).toList());
        model.addAttribute("categories", List.of(
                new Category("Khoa học"),
                new Category("Văn học"),
                new Category("Lịch sử"),
                new Category("Công nghệ"),
                new Category("Nghệ thuật"),
                new Category("Toán học"),
                new Category("Luật"),
                new Category("Xã hội")));

        return "home";
    }

    @GetMapping("/books/search")
    public String searchBooks(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model,
            HttpSession session) {

        model.addAttribute("user", session.getAttribute("user"));
        model.addAttribute("books", bookService.search(keyword));
        model.addAttribute("keyword", keyword);
        session.setAttribute("keyword", keyword);

        return "search";
    }

    @GetMapping("/books/{id}")
    public String viewBookDetail(
            @PathVariable String id,
            Model model,
            HttpSession session) {

        model.addAttribute("book", bookService.findById(id));
        model.addAttribute("keyword", session.getAttribute("keyword"));
        model.addAttribute("user", session.getAttribute("user"));

        return "bookdetail";
    }

    @GetMapping("/borrow")
    public String borrowBook(Model model, HttpSession session) {
        User user = currentCustomer(session);
        if (user == null) {
            return "redirect:/login";
        }

        Customer customer = customerService.findByUserId(user.getId());
        if (customer == null) {
            return "redirect:/";
        }

        List<BorrowBook> borrowList = borrowBookService.findByCustomer(customer);
        borrowList.forEach(borrow -> {
            if (borrow.getReturnDate() != null) {
                borrow.setStatus("Returned");
            } else if (borrow.getDeadline().isBefore(LocalDate.now())) {
                borrow.setStatus("Overdue");
            } else {
                borrow.setStatus("Borrowing");
            }
        });

        model.addAttribute("borrowList", borrowList);
        model.addAttribute("user", user);

        return "myborrow";
    }

    @GetMapping("/borrow/renew/{id}")
    public String renewBorrow(@PathVariable String id, HttpSession session) {
        User user = currentCustomer(session);
        if (user == null) {
            return "redirect:/login";
        }

        Customer customer = customerService.findByUserId(user.getId());
        BorrowBook borrowBook = borrowBookService.findById(id);

        if (customer != null
                && borrowBook.getCustomer().getId().equals(customer.getId())
                && borrowBook.getReturnDate() == null
                && Integer.valueOf(0).equals(borrowBook.getDelay())) {
            borrowBookService.renewBorrow(id);
        }

        return "redirect:/borrow";
    }

    @GetMapping("/manager/members/add")
    public String showAddMemberForm(Model model, HttpSession session) {
        User currentUser = currentManager(session);
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

        User currentUser = currentManager(session);
        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("activeTab", "add-member");

        try {
            if (userService.existsByUsername(username)) {
                model.addAttribute("errorMessage", "Tên đăng nhập đã tồn tại.");
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

            model.addAttribute("successMessage", "Thêm thành viên thành công.");
        } catch (Exception exception) {
            model.addAttribute("errorMessage", "Không thể thêm thành viên: " + exception.getMessage());
        }

        return "manager/dashboard";
    }

    @PostMapping("/book/preorder")
    public String preOrderBook(
            @RequestParam String bookId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User user = currentCustomer(session);
        if (user == null) {
            return "redirect:/login";
        }

        Customer customer = customerService.findByUserId(user.getId());
        Book book = bookService.findById(bookId);

        if (customer == null) {
            return "redirect:/";
        }

        boolean existed = preOrderBookService.findByCustomer(customer)
                .stream()
                .anyMatch(preOrder -> preOrder.getBook().getId().equals(bookId)
                        && ("Waiting".equals(preOrder.getStatus())
                        || "Ready".equals(preOrder.getStatus())));

        if (existed) {
            redirectAttributes.addFlashAttribute("error", "Bạn đã đặt trước sách này.");
            return "redirect:/books/" + bookId;
        }

        String preOrderId = "P" + String.format(
                "%04d",
                preOrderBookService.findAll().size() + 1);

        PreOrderBook preOrderBook = new PreOrderBook();
        preOrderBook.setId(preOrderId);
        preOrderBook.setBook(book);
        preOrderBook.setCustomer(customer);
        preOrderBook.setPreOrderDate(LocalDate.now());
        preOrderBook.setStatus("Waiting");
        preOrderBookService.save(preOrderBook);

        redirectAttributes.addFlashAttribute("success", "Đặt trước sách thành công.");
        return "redirect:/books/" + bookId;
    }

    @GetMapping("/preorder")
    public String getMyPreOrders(HttpSession session, Model model) {
        User user = currentCustomer(session);
        if (user == null) {
            return "redirect:/login";
        }

        Customer customer = customerService.findByUserId(user.getId());
        if (customer == null) {
            return "redirect:/";
        }

        model.addAttribute("preOrderBooks", preOrderBookService.findByCustomer(customer));
        model.addAttribute("user", user);
        return "preorder";
    }

    @PostMapping("/preorder/cancel")
    public String cancelMyPreOrder(@RequestParam String id, HttpSession session) {
        User user = currentCustomer(session);
        if (user == null) {
            return "redirect:/login";
        }

        Customer customer = customerService.findByUserId(user.getId());
        PreOrderBook preOrderBook = preOrderBookService.findById(id);

        if (customer == null
                || !preOrderBook.getCustomer().getId().equals(customer.getId())
                || "Completed".equals(preOrderBook.getStatus())
                || "Cancelled".equals(preOrderBook.getStatus())) {
            return "redirect:/preorder";
        }

        preOrderBook.setStatus("Cancelled");
        preOrderBookService.save(preOrderBook);
        return "redirect:/preorder";
    }

    private User currentCustomer(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && "customer".equalsIgnoreCase(user.getRole()) ? user : null;
    }

    private User currentManager(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && "manager".equalsIgnoreCase(user.getRole()) ? user : null;
    }

    private record Stat(String value, String label) {
    }

    private record Category(String name) {
    }
}
