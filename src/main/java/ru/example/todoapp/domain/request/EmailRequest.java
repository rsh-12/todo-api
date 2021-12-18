package ru.example.todoapp.domain.request;
/*
 * Date: 5/23/21
 * Time: 1:22 PM
 * */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {

    private String email;

}
