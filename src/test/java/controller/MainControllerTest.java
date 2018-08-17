package controller;

import customer.Application;
import customer.controller.MainController;
import customer.dto.ChildDto;
import customer.dto.UserDto;
import customer.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class MainControllerTest {
    private final static String email = "emailname@example.com";
    private final static String name = "firstname lastname";
    private final static String childName = "child name";
    private final static Byte childAge = 4;
    private final static Character childGender = 'f';

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MainController mainController = new MainController(userService);
        mockMvc = standaloneSetup(mainController).build();
    }

    private UserDto createUserWithChildren() {
        ChildDto child = new ChildDto();
        child.setAge(childAge);
        child.setGender(childGender);
        child.setName(childName);
        UserDto user = new UserDto();
        user.setEmail(email);
        user.setName(name);
        user.setChildren(Set.of(child));
        return user;
    }

    @Test
    public void greetingShouldReturnDefaultMessage1() throws Exception {
        when(userService.getUserDtoList()).thenReturn(List.of(createUserWithChildren()));

        String expected = "[{\"name\":\"firstname lastname\",\"email\":\"emailname@example.com\"," +
                "\"children\":[{\"name\":\"child name\",\"gender\":\"f\",\"age\":4}]}]";

        mockMvc.perform(get("/demo/users")).andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

}
