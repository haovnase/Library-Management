package com.library.mvc.librarymanagement;

import com.library.mvc.librarymanagement.controller.AdminController;
import com.library.mvc.librarymanagement.controller.HomeController;
import com.library.mvc.librarymanagement.controller.LoginController;
import com.library.mvc.librarymanagement.controller.ManagerController;
import com.library.mvc.librarymanagement.entity.Book;
import com.library.mvc.librarymanagement.entity.Customer;
import com.library.mvc.librarymanagement.entity.User;
import com.library.mvc.librarymanagement.repository.BookRepository;
import com.library.mvc.librarymanagement.repository.BorrowBookRepository;
import com.library.mvc.librarymanagement.repository.CustomerRepository;
import com.library.mvc.librarymanagement.service.BookService;
import com.library.mvc.librarymanagement.service.BorrowBookService;
import com.library.mvc.librarymanagement.service.CustomerService;
import com.library.mvc.librarymanagement.service.PreOrderBookService;
import com.library.mvc.librarymanagement.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest({
        LoginController.class,
        HomeController.class,
        ManagerController.class,
        AdminController.class
})
class LibraryManagementApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private BookService bookService;

    @MockBean
    private BorrowBookService borrowBookService;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private PreOrderBookService preOrderBookService;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private BorrowBookRepository borrowBookRepository;

    @MockBean
    private CustomerRepository customerRepository;

    @Test
    void customerLoginRedirectsToHome() throws Exception {
        User customer = new User("U001", "LIB-001", "123456", "customer", "Nguyen Van A", "active");
        when(userService.login("LIB-001", "123456")).thenReturn(Optional.of(customer));

        mockMvc.perform(post("/login")
                        .param("member-id", "LIB-001")
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void invalidLoginRedirectsBackToLoginWithError() throws Exception {
        when(userService.login("LIB-999", "wrong")).thenReturn(Optional.empty());

        mockMvc.perform(post("/login")
                        .param("member-id", "LIB-999")
                        .param("password", "wrong"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    void homeTemplateRendersWithEmptyCatalog() throws Exception {
        when(bookService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    void searchTemplateRendersWithNoResults() throws Exception {
        when(bookService.search("quantum")).thenReturn(List.of());

        mockMvc.perform(get("/books/search").param("keyword", "quantum"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"));
    }

    @Test
    void bookDetailTemplateRenders() throws Exception {
        Book book = new Book();
        book.setId("B001");
        book.setBookName("Clean Code");
        book.setAuthor("Robert C. Martin");
        book.setQuantity(2);
        when(bookService.findById("B001")).thenReturn(book);

        mockMvc.perform(get("/books/B001"))
                .andExpect(status().isOk())
                .andExpect(view().name("bookdetail"));
    }

    @Test
    void customerBorrowAndPreorderTemplatesRender() throws Exception {
        User customerUser = new User(
                "U001",
                "LIB-001",
                "123456",
                "customer",
                "Nguyen Van A",
                "active");
        Customer customer = new Customer("C001", customerUser, "0900000000");

        when(customerService.findByUserId("U001")).thenReturn(customer);
        when(borrowBookService.findByCustomer(customer)).thenReturn(List.of());
        when(preOrderBookService.findByCustomer(customer)).thenReturn(List.of());

        mockMvc.perform(get("/borrow").sessionAttr("user", customerUser))
                .andExpect(status().isOk())
                .andExpect(view().name("myborrow"));

        mockMvc.perform(get("/preorder").sessionAttr("user", customerUser))
                .andExpect(status().isOk())
                .andExpect(view().name("preorder"));
    }

    @Test
    void managerPreorderTemplateRenders() throws Exception {
        User manager = new User(
                "U002",
                "MANAGER-01",
                "123456",
                "manager",
                "Tran Thi B",
                "active");
        when(preOrderBookService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/manager/preorder").sessionAttr("user", manager))
                .andExpect(status().isOk())
                .andExpect(view().name("manager/preorder"));
    }

    @Test
    void lockedUserCannotLogin() throws Exception {
        User lockedUser = new User(
                "U004",
                "locked-user",
                "123456",
                "customer",
                "Locked User",
                "locked");
        when(userService.login("locked-user", "123456")).thenReturn(Optional.of(lockedUser));

        mockMvc.perform(post("/login")
                        .param("member-id", "locked-user")
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?locked=true"));
    }

    @Test
    void adminTemplatesRender() throws Exception {
        User customer = new User(
                "U001",
                "customer",
                "123456",
                "customer",
                "Nguyen Van A",
                "active");
        User admin = new User(
                "U003",
                "admin",
                "123456",
                "admin",
                "Quan tri Lexicon",
                "active");

        when(userService.findAll()).thenReturn(List.of(customer, admin));
        when(userService.findById("U001")).thenReturn(customer);

        mockMvc.perform(get("/admin/dashboard").sessionAttr("user", admin))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"));

        mockMvc.perform(get("/admin/users/add").sessionAttr("user", admin))
                .andExpect(status().isOk())
                .andExpect(view().name("add-user"));

        mockMvc.perform(get("/admin/users/edit/U001").sessionAttr("user", admin))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-user"));
    }
}
