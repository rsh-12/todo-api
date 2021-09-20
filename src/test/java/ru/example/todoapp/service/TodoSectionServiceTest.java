package ru.example.todoapp.service;
/*
 * Date: 19.09.2021
 * Time: 7:16 PM
 * */

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.example.todoapp.service.impl.TodoSectionServiceImpl;

@ExtendWith(SpringExtension.class)
public class TodoSectionServiceTest {

    @InjectMocks
    private TodoSectionServiceImpl sectionService;

}
