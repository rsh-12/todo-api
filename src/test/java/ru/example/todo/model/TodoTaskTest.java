package ru.example.todo.model;
/*
 * Date: 04.07.2021
 * Time: 9:03 PM
 * */

import org.junit.Before;
import org.modelmapper.ModelMapper;

public class TodoTaskTest {

    private final ModelMapper modelMapper = new ModelMapper();

    @Before
    public void setUp() {
        modelMapper.getConfiguration().setSkipNullEnabled(true);
    }

}
