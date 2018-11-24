package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import customer.Application;
import customer.controller.MainController;
import customer.controller.UserExceptionHandler;
import customer.dto.ChildDto;
import customer.dto.UserDto;
import customer.service.UserNotFoundException;
import customer.service.UserService;
import org.junit.Before;
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@ContextConfiguration
public class MainControllerTest {
    private final static String email = "emailname@example.com";
    private final static String name = "firstname lastname";
    private final static String childName = "child name";
    private final static Integer childAge = 4;
    private final static String childGender = "f";

    @Captor
    private ArgumentCaptor<UserDto> userCaptor;

    @Mock
    private UserService userService;

    private ObjectMapper mapper;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MainController mainController = new MainController(userService);
        mockMvc = standaloneSetup(mainController).setControllerAdvice(UserExceptionHandler.class).build();
        mapper = new ObjectMapper();
    }


    @Test
    public void getUsersShouldReturnAllUsers() throws Exception {
        UserDto userDto = UserDto.builder()
            .name(name)
            .email(email)
            .build();

        userDto.setChildren(Set.of(ChildDto.builder()
            .name(childName)
            .age(childAge)
            .gender(childGender)
            .build()
        ));

        when(userService.getUserDtoList()).thenReturn(List.of(userDto));


        mockMvc.perform(get("/demo/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is(name)))
            .andExpect(jsonPath("$[0].email", is(email)))
            .andExpect(jsonPath("$[0].children", hasSize(1)))
            .andExpect(jsonPath("$[0].children[0].name", is(childName)))
            .andExpect(jsonPath("$[0].children[0].gender", is(childGender)))
            .andExpect(jsonPath("$[0].children[0].age", is(childAge)));
    }

    @Test
    public void postUserShouldSaveUser() throws Exception {


        UserDto userDto = UserDto.builder()
            .name(name)
            .email(email)
            .build();

        userDto.setChildren(Set.of(ChildDto.builder()
            .name(childName)
            .age(childAge)
            .gender(childGender)
            .build()
        ));
        when(userService.save(any(UserDto.class))).thenReturn(userDto);

        String body = mapper.writeValueAsString(userDto);
        mockMvc.perform(post("/demo/users")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name", is(name)))
            .andExpect(jsonPath("$.email", is(email)))
            .andExpect(jsonPath("$.children", hasSize(1)))
            .andExpect(jsonPath("$.children[0].name", is(childName)))
            .andExpect(jsonPath("$.children[0].gender", is(childGender)))
            .andExpect(jsonPath("$.children[0].age", is((childAge))));

        verify(userService).save(userCaptor.capture());
        JSONAssert.assertEquals(mapper.writeValueAsString(userDto), body, JSONCompareMode.STRICT);
    }

    @Test
    public void putNonExistingUserShouldReturnError404() throws Exception {
        long userId = 42L;

        when(userService.update(any(UserDto.class), anyLong())).thenThrow(new UserNotFoundException("not found"));

        UserDto userDto = UserDto.builder()
            .name(name)
            .email(email)
            .build();

        userDto.setChildren(Set.of(ChildDto.builder()
            .name(childName)
            .age(childAge)
            .gender(childGender)
            .build()
        ));

        String body = mapper.writeValueAsString(userDto);
        mockMvc.perform(put("/demo/users/" + userId).contentType(MediaType.APPLICATION_JSON_VALUE).content(body))
            .andExpect(status().isNotFound());
    }

    @Test
    public void putUserShouldUpdateUser() throws Exception {
        Long userId = 42L;

        UserDto userDto = UserDto.builder()
            .name(name)
            .email(email)
            .build();

        userDto.setChildren(Set.of(ChildDto.builder()
            .name(childName)
            .age(childAge)
            .gender(childGender)
            .build()
        ));

        when(userService.update(any(UserDto.class), eq(userId))).thenReturn(userDto);

        String body = mapper.writeValueAsString(userDto);

        mockMvc.perform(put("/demo/users/" + userId).contentType(MediaType.APPLICATION_JSON_VALUE).content(body))
            .andExpect(status().isOk());
        verify(userService).update(userCaptor.capture(), eq(userId));
        JSONAssert.assertEquals(mapper.writeValueAsString(userDto), body, JSONCompareMode.STRICT);
    }
}
