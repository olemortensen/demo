package service;

import customer.domain.Child;
import customer.domain.User;
import customer.dto.ChildDto;
import customer.dto.UserDto;
import customer.repository.UserRepository;
import customer.service.UserNotFoundException;
import customer.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    private final static String email = "emailname@example.com";
    private final static String name = "firstname lastname";
    private final static String childName = "child name";
    private final static Integer childAge = 4;
    private final static String childGender = "f";

    @Mock
    private UserRepository repositoryMock;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @InjectMocks
    private UserService userService;

    @Before
    public void setup() {
        userCaptor = ArgumentCaptor.forClass(User.class);
        MockitoAnnotations.initMocks(this);

//        userService = new UserService(repositoryMock);
    }

    private User createUserWithChildren() {
        Child child = new Child();
        child.setAge(childAge);
        child.setGender(childGender);
        child.setName(childName);
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.addChild(child);
        return user;
    }

    private void verifyUser(User user) {
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(1, user.getChildren().size());
        for (Child child : user.getChildren()) {
            assertEquals(childAge, child.getAge());
            assertEquals(childGender, child.getGender());
            assertEquals(childName, child.getName());
        }
    }

    private void verifyUser(UserDto user) {
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(1, user.getChildren().size());
        for (ChildDto child : user.getChildren()) {
            assertEquals(childAge, child.getAge());
            assertEquals(childGender, child.getGender());
            assertEquals(childName, child.getName());
        }
    }

    @Test
    public void userCanBeSavedWithAllFields() {
        // given
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

        // when
        userService.save(userDto);

        // then
        Mockito.verify(repositoryMock).save(userCaptor.capture());
        verifyUser(userCaptor.getValue());
    }

    @Test
    public void userCanBeSavedWithNullChildren() {
        // given
        UserDto userDto = UserDto.builder().name(name).email(email).build();

        // when
        userService.save(userDto);

        // then
        Mockito.verify(repositoryMock).save(userCaptor.capture());
        assertEquals(name, userCaptor.getValue().getName());
        assertEquals(email, userCaptor.getValue().getEmail());
        assertEquals(0, userCaptor.getValue().getChildren().size());
    }

    @Test
    public void userCanBeUpdated() {
        // given
        Long userId = 42L;
        when(repositoryMock.findById(userId)).thenReturn(Optional.of(createUserWithChildren()));
        UserDto userDto = UserDto.builder().name(name).email(email).build();

        // when
        userService.update(userDto, userId);

        // then
        Mockito.verify(repositoryMock).save(userCaptor.capture());
        assertEquals(name, userCaptor.getValue().getName());
        assertEquals(email, userCaptor.getValue().getEmail());
    }

    @Test(expected = UserNotFoundException.class)
    public void userNotFoundTrowsException() {
        // given
        when(repositoryMock.findById(anyLong())).thenReturn(Optional.empty());
        Long userId = 42L;
        UserDto userDto = UserDto.builder().name(name).email(email).build();

        // when
        userService.update(userDto, userId);
    }

    @Test
    public void allUsersCanBeReadWithChildren() {
        // given
        User user = createUserWithChildren();
        when(repositoryMock.findAll()).thenReturn(Set.of(user));

        // when
        List<UserDto> userDtoList = userService.getUserDtoList();

        // then
        assertEquals(1, userDtoList.size());
        verifyUser(userDtoList.get(0));
    }
}
