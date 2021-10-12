package com.ganesh.springbootjpamysql.request;

import lombok.Data;

@Data
public class AuthorCreationRequest {

    private String firstName;
    private String lastName;
}
