package com.library.mvc.librarymanagement.controller;

import com.library.mvc.librarymanagement.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@SessionAttribute(name = "user", required = false) User user, Model model) {
        if (user != null) {
            model.addAttribute("user", user);
        }

        model.addAttribute("stats", List.of(
                new Stat("1.2M+", "Tổng số sách"),
                new Stat("24k", "Thành viên tích cực"),
                new Stat("850k", "Tài nguyên số")
        ));

        model.addAttribute("books", List.of(
                new BookItem("History of Form", "Prof. Julian S. Miller", "https://lh3.googleusercontent.com/aida-public/AB6AXuCCizBJYK9i8doUeLlc1Yl5aHSXV9IGBVs7g_WFsjh-VbOwvc4PigfNaPrHi8GqdBTOVf3MMwkJRML7ifR44CkkejALyRqHordSCdQKPT5VUil053U6TjDkziyRgSK2LrzEvPWc0t4VSstbiCsSmYnnM_iDYWjUXbPQaKiZEI5XlbrjCs1O887z9UM7xIzKfs2UO7yd6Mtj2S1qgvj_xRm_uj0gA4fb0uDcy7xFfqp3oWQC48HJT6BmRWT_8eLXj1_lo8bQAPens0nr", "Kiểm tra tình trạng", "available"),
                new BookItem("The Quantum Mind", "Dr. Elena Vance", "https://lh3.googleusercontent.com/aida-public/AB6AXuCjwzyYfdkfhRCsBI-MRivRg_ga-KgoX_oaM3nGAKLfs_-d0sHKgLzTeBxWfkOO_M3WFqUOcok3b9-2e4UaDVUOkVQGt7Tdy28nvD0lmp-pRZBtleDx8I1DmlJaKNHYdu_eLR-OqejEIh9gqV7eDY6aFvl6_uVBkQtiOWhTdeU_YBEwgwlCl_4zJqqoAPjRLco8w8M3INK2P-8bJvhSF0oZlRikcx_4vyP79QltJgQEdpaSVDfUsF2FR82JZArY7yuUXALLT9zRePu1", "Đặt trước", "available"),
                new BookItem("Medieval Cartography", "Sir Thomas Greene", "https://lh3.googleusercontent.com/aida-public/AB6AXuDWmPfMt2vUPlGtyUjzLgkDkU2ePL3O6rmL1AJe7e4rHMRZO876jErRJfYF1uQc7_YMgThMXJtFzX3D4JN2byA7sWYpscUeJmneQSdR43PFEtWOBKDDvP-CrISS3wbogntssKT5uXBFWBgdy7w3gJrZHJcV64xx3JteExrbyuZOURRJGGtmk4u892jYFfU1i58KWgoeLEmFmWZLhiqfI5_SBkBE92c9rPfcF5pdm6S4ap1NRZQxTe3YEKXpZX2RRXmn7xLQ7lqUECRQ", "Đã mượn", "borrowed"),
                new BookItem("Sustainable Ecosystems", "Maya K. Reynolds", "https://lh3.googleusercontent.com/aida-public/AB6AXuChSx-XLvJDQPgqsuJZEpXgzp4ODvHzfTNHAz_C1K-7GrN0BTeq-VsebuEo5QdEdwgsBXYToNyv_h9nUGbSIj6Gngr4zsHeMuFbpDeijLOVruB-gS3G4bde9AJ5SH5iIuoOWO6jn1skWD0k72e4bMNLmftO076lUp3HZ_vdPnkXuNRKmK7XBwcUdM6VDIO2QoBGLYQ41leyQ0ifWzit7slQFBg4DsIEzjFgfR1IoDtKLpN_0qgnwyA1irlnJulWAOsocMxBUUmrPPlU", "Kiểm tra tình trạng", "available")
        ));

        model.addAttribute("categories", List.of(
                new Category("science", "Khoa học"),
                new Category("menu_book", "Văn học"),
                new Category("history_edu", "Lịch sử"),
                new Category("devices", "Công nghệ"),
                new Category("palette", "Nghệ thuật"),
                new Category("calculate", "Toán học"),
                new Category("gavel", "Luật"),
                new Category("public", "Xã hội")
        ));

        return "home";
    }

    private record Stat(String value, String label) {
    }

    private record BookItem(String title, String author, String image, String actionLabel, String status) {
    }

    private record Category(String icon, String name) {
    }
}
