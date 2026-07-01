package com.library.mvc.librarymanagement;

import com.library.mvc.librarymanagement.entity.User;
import com.library.mvc.librarymanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class LibraryManagementApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    void customerLoginRedirectsToHome() throws Exception {
        User customer = new User("U001", "LIB-001", "123456", "customer", "Nguyen Van A", "active");
        when(userRepository.findByUsernameAndPassword("LIB-001", "123456")).thenReturn(Optional.of(customer));

        mockMvc.perform(post("/login")
                        .param("member-id", "LIB-001")
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void invalidLoginRedirectsBackToLoginWithError() throws Exception {
        when(userRepository.findByUsernameAndPassword("LIB-999", "wrong")).thenReturn(Optional.empty());

        mockMvc.perform(post("/login")
                        .param("member-id", "LIB-999")
                        .param("password", "wrong"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }
}
