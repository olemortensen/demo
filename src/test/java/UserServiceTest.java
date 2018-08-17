import customer.domain.Child;
import customer.domain.User;
import customer.dto.ChildDto;
import customer.dto.UserDto;
import customer.repo.UserRepository;
import customer.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    private final static String email = "emailname@example.com";
    private final static String name = "firstname lastname";
    private final static String childName = "child name";
    private final static Byte childAge = 4;
    private final static Character childGender = 'f';

    @Mock
    private UserRepository repositoryMock;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        userCaptor = ArgumentCaptor.forClass(User.class);
        MockitoAnnotations.initMocks(this);

        userService = new UserService(repositoryMock);
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
    public void userSavedWithAllFields() {

        UserDto userDto = new UserDto(name, email, Set.of(new ChildDto(childName, childGender, childAge)));

        userService.save(userDto);

        Mockito.verify(repositoryMock).save(userCaptor.capture());
        verifyUser(userCaptor.getValue());
    }

    @Test
    public void userSavedWithNullChildren() {
        UserDto userDto = new UserDto(name, email, null);

        userService.save(userDto);

        Mockito.verify(repositoryMock).save(userCaptor.capture());
        assertEquals(name, userCaptor.getValue().getName());
        assertEquals(email, userCaptor.getValue().getEmail());
        assertEquals(0, userCaptor.getValue().getChildren().size());
    }

    @Test
    public void userReadWithChildren() {
        User user = createUserWithChildren();
        when(repositoryMock.findAll()).thenReturn(Set.of(user));

        List<UserDto> userDtoList = userService.getUserDtoList();

        assertEquals(1, userDtoList.size());
        verifyUser(userDtoList.get(0));
    }
}
