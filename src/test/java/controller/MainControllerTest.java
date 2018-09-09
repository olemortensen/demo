package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import customer.Application;
import customer.controller.MainController;
import customer.dto.ChildDto;
import customer.dto.UserDto;
import customer.service.UserNotFoundException;
import customer.service.UserService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@ContextConfiguration
public class MainControllerTest {
    private final static String email = "emailname@example.com";
    private final static String name = "firstname lastname";
    private final static String childName = "child name";
    private final static Byte childAge = 4;
    private final static Character childGender = 'f';
    private final static String childJSON = "{\"name\":\"firstname lastname\",\"email\":\"emailname@example.com\"," +
        "\"children\":[{\"name\":\"child name\",\"gender\":\"f\",\"age\":4}]}";

    @Captor
    private ArgumentCaptor<UserDto> userCaptor;

    @Mock
    private UserService userServiceMock;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MainController mainController = new MainController(userServiceMock);
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
    public void getUsersShouldReturnAllUsers() throws Exception {
        when(userServiceMock.getUserDtoList()).thenReturn(List.of(createUserWithChildren()));

        String expected = "[" + childJSON + "]";

        mockMvc.perform(get("/demo/users")).andExpect(status().isOk())
            .andExpect(content().json(expected));
    }

    @Test
    public void postUserShouldSaveUser() throws Exception {
        String body = childJSON;

        when(userServiceMock.save(any(UserDto.class))).thenReturn(new UserDto());

        mockMvc.perform(post("/demo/users").contentType(MediaType.APPLICATION_JSON_VALUE).content(body)).andExpect(status().isCreated());
        verify(userServiceMock).save(userCaptor.capture());
        JSONAssert.assertEquals(new ObjectMapper().writeValueAsString(createUserWithChildren()), body, JSONCompareMode.STRICT);
    }

    // Ignored because MockMvc does not use exception handler. See https://github.com/spring-projects/spring-boot/issues/7321
    @Ignore
    @Test
    public void putNonExistingUserShouldReturnError404() throws Exception {
        String body = childJSON;
        Long userId = 42L;

        when(userServiceMock.update(any(UserDto.class), anyLong())).thenThrow(new UserNotFoundException("not found"));

        try {
            mockMvc.perform(put("/demo/users/" + userId).contentType(MediaType.APPLICATION_JSON_VALUE).content(body)).andExpect(status().isNotFound());
            fail("exeption expected");
        } catch (UserNotFoundException e) {
            verify(userServiceMock, never()).update(userCaptor.capture(), eq(userId));
        }
    }

    @Test
    public void putUserShouldUpdateUser() throws Exception {
        String body = childJSON;
        Long userId = 42L;

        when(userServiceMock.update(any(UserDto.class), eq(userId))).thenReturn(new UserDto());

        mockMvc.perform(put("/demo/users/" + userId).contentType(MediaType.APPLICATION_JSON_VALUE).content(body)).andExpect(status().isOk());
        verify(userServiceMock).update(userCaptor.capture(), eq(userId));
        JSONAssert.assertEquals(new ObjectMapper().writeValueAsString(createUserWithChildren()), body, JSONCompareMode.STRICT);
    }
}
