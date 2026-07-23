package com.library.mvc.librarymanagement.config;

import com.library.mvc.librarymanagement.entity.Book;
import com.library.mvc.librarymanagement.entity.BorrowBook;
import com.library.mvc.librarymanagement.entity.Customer;
import com.library.mvc.librarymanagement.entity.User;
import com.library.mvc.librarymanagement.repository.BookRepository;
import com.library.mvc.librarymanagement.repository.BorrowBookRepository;
import com.library.mvc.librarymanagement.repository.CustomerRepository;
import com.library.mvc.librarymanagement.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
@Profile("local")
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class LocalDataInitializer {

    @Bean
    CommandLineRunner seedLocalData(
            UserRepository userRepository,
            CustomerRepository customerRepository,
            BookRepository bookRepository,
            BorrowBookRepository borrowBookRepository) {

        return args -> {
            User customerUser = ensureUser(
                    userRepository,
                    "U001",
                    "customer",
                    "123456",
                    "customer",
                    "Nguyễn Văn Minh");

            ensureUser(
                    userRepository,
                    "U002",
                    "manager",
                    "123456",
                    "manager",
                    "Trần Thu Hà");

            ensureUser(
                    userRepository,
                    "U003",
                    "admin",
                    "123456",
                    "admin",
                    "Quản trị Lexicon");

            Customer customer = customerRepository.findById("C001")
                    .orElseGet(() -> {
                        Customer created = new Customer();
                        created.setId("C001");
                        created.setUser(customerUser);
                        created.setPhone("0901234567");
                        return customerRepository.save(created);
                    });

            Book cleanCode = ensureBook(
                    bookRepository,
                    "B001",
                    "Clean Code",
                    "Robert C. Martin",
                    "Công nghệ",
                    4,
                    "9780132350884",
                    2008,
                    "Prentice Hall",
                    "Tiếng Anh",
                    "A-01",
                    "/uploads/B001.jpg",
                    "Một cẩm nang thực tiễn về cách viết mã nguồn rõ ràng, dễ đọc và dễ bảo trì.");

            ensureBook(
                    bookRepository,
                    "B002",
                    "The Pragmatic Programmer",
                    "Andrew Hunt, David Thomas",
                    "Công nghệ",
                    3,
                    "9780135957059",
                    2019,
                    "Addison-Wesley",
                    "Tiếng Anh",
                    "A-02",
                    "/uploads/B002.jpg",
                    "Những nguyên tắc và thói quen giúp lập trình viên phát triển sản phẩm bền vững.");

            ensureBook(
                    bookRepository,
                    "B003",
                    "Dế Mèn Phiêu Lưu Ký",
                    "Tô Hoài",
                    "Văn học",
                    6,
                    "9786042089950",
                    2020,
                    "Kim Đồng",
                    "Tiếng Việt",
                    "B-01",
                    "/uploads/B003.jpg",
                    "Tác phẩm văn học thiếu nhi kinh điển về hành trình trưởng thành của Dế Mèn.");

            ensureBook(
                    bookRepository,
                    "B004",
                    "Lược Sử Thời Gian",
                    "Stephen Hawking",
                    "Khoa học",
                    0,
                    "9780553380163",
                    1998,
                    "Bantam",
                    "Tiếng Việt",
                    "C-03",
                    "/uploads/B004.jpg",
                    "Khám phá nguồn gốc vũ trụ, không gian, thời gian và những câu hỏi lớn của vật lý.");

            ensureBook(
                    bookRepository,
                    "B005",
                    "Sapiens",
                    "Yuval Noah Harari",
                    "Lịch sử",
                    5,
                    "9780062316097",
                    2015,
                    "Harper",
                    "Tiếng Việt",
                    "D-02",
                    "/uploads/B005.jpg",
                    "Một cách nhìn khái quát về lịch sử hình thành và phát triển của loài người.");

            ensureBook(
                    bookRepository,
                    "B006",
                    "The Design of Everyday Things",
                    "Don Norman",
                    "Nghệ thuật",
                    2,
                    "9780465050659",
                    2013,
                    "Basic Books",
                    "Tiếng Anh",
                    "E-01",
                    "/uploads/B006.jpg",
                    "Giải thích cách thiết kế sản phẩm trực quan dựa trên hành vi và nhu cầu con người.");

            if (!borrowBookRepository.existsById("BR001")) {
                BorrowBook borrow = new BorrowBook();
                borrow.setId("BR001");
                borrow.setBook(cleanCode);
                borrow.setCustomer(customer);
                borrow.setBorrowDate(LocalDate.now().minusDays(5));
                borrow.setDeadline(LocalDate.now().plusDays(9));
                borrow.setReturnDate(null);
                borrow.setQuantity(1);
                borrow.setStatus("Borrowing");
                borrow.setDescription("");
                borrow.setFine(0);
                borrow.setDelay(0);
                borrowBookRepository.save(borrow);
            }
        };
    }

    private User ensureUser(
            UserRepository repository,
            String id,
            String username,
            String password,
            String role,
            String fullName) {

        return repository.findById(id).orElseGet(() -> {
            User user = new User();
            user.setId(id);
            user.setUsername(username);
            user.setPassword(password);
            user.setRole(role);
            user.setFullName(fullName);
            user.setStatus("active");
            return repository.save(user);
        });
    }

    private Book ensureBook(
            BookRepository repository,
            String id,
            String name,
            String author,
            String type,
            int quantity,
            String isbn,
            int year,
            String publisher,
            String language,
            String location,
            String jacket,
            String description) {

        return repository.findById(id).orElseGet(() -> {
            Book book = new Book();
            book.setId(id);
            book.setBookName(name);
            book.setAuthor(author);
            book.setType(type);
            book.setQuantity(quantity);
            book.setIsbn(isbn);
            book.setYear(year);
            book.setPublisher(publisher);
            book.setLanguage(language);
            book.setLocation(location);
            book.setBookJacket(jacket);
            book.setDescription(description);
            book.setPrice(BigDecimal.ZERO);
            return repository.save(book);
        });
    }
}
