package com.library.mvc.librarymanagement.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private String id;

    @NotBlank
    @Size(min=4,max=20)
    private String username;

    @NotBlank
    @Size(min=6)
    private String password;

    @NotBlank
    @Size(min=5,max=50)
    private String fullName;

    @NotBlank
    private String role;

    @NotBlank
    private String status;

}