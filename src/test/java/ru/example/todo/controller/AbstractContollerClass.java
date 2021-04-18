package ru.example.todo.controller;
/*
 * Date: 3/14/21
 * Time: 7:05 AM
 * */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
abstract class AbstractContollerClass {

    protected final static String SECTIONS = "/api/sections/";
    protected final static String TASKS = "/api/tasks/";
    protected final static String USERS = "/api/users/";

    protected static final String ADMIN = "admin@mail.com";
    protected static final String USER = "client@mail.com";

    @Autowired
    protected MockMvc mvc;

    protected static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Conversion error");
        }
    }

    protected int getJsonArraySize(String api, String jsonPath) throws Exception {
        MvcResult result = mvc.perform(get(api)).andExpect(status().isOk()).andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), jsonPath + ".length()");
    }

    protected int getJsonArraySize(String api, String jsonPath, String param, String arg) throws Exception {
        MvcResult result = mvc.perform(get(api).param(param, arg)).andExpect(status().isOk()).andReturn();

        return JsonPath.read(result.getResponse().getContentAsString(), jsonPath + ".length()");
    }
}
