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

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserServiceTest {
    private final static String email = "emailname@example.com";
    private final static String name = "firstname lastname";
    private final static String childName = "child name";
    private final static Byte childAge = 4;
    private final static Character childGender = 'f';

    @Mock
    UserRepository repositoryMock;

    @Captor
    ArgumentCaptor<User> userCaptor;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        userService = new UserService(repositoryMock);
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
        UserDto userDto = new UserDto(name, email, Set.of(new ChildDto(childName, childGender, childAge)));

        userService.save(userDto);

        Mockito.verify(repositoryMock).save(userCaptor.capture());
        assertEquals(name, userCaptor.getValue().getName());
        assertEquals(email, userCaptor.getValue().getEmail());
        assertNull(userCaptor.getValue().getChildren());
    }
}
