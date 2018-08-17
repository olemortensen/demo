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

    @Test
    public void userSavedWithAllFields() {

        UserDto userDto = new UserDto(name, email, Set.of(new ChildDto(childName, childGender, childAge)));

        userService.save(userDto);

        Mockito.verify(repositoryMock).save(userCaptor.capture());
        assertEquals(name, userCaptor.getValue().getName());
        assertEquals(email, userCaptor.getValue().getEmail());
        assertEquals(1, userCaptor.getValue().getChildren().size());
        userCaptor.getValue().getChildren().forEach(e -> {
            assertEquals(childAge, e.getAge());
            assertEquals(childGender, e.getGender());
            assertEquals(childName, e.getName());
        });
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
        assertEquals(name, userDtoList.get(0).getName());
        assertEquals(email, userDtoList.get(0).getEmail());
        assertEquals(1, userDtoList.get(0).getChildren().size());
        userDtoList.get(0).getChildren().forEach(e -> {
            assertEquals(childAge, e.getAge());
            assertEquals(childGender, e.getGender());
            assertEquals(childName, e.getName());
        });

    }
}
