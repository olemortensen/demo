import customer.domain.User;
import customer.dto.ChildDto;
import customer.dto.UserDto;
import customer.repo.UserRepository;
import customer.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserServiceTest {
    @Mock
    UserRepository repositoryMock;

    @Captor
    ArgumentCaptor<User> userCaptor;

    @InjectMocks
    private UserService userService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        userService = new UserService(repositoryMock);
    }

    @Test
    public void userSavedWithAllFields() {
        final String email = "emailname@example.com";
        final String name = "firstname lastname";
        final String childName = "child name";
        final Byte childAge = 4;
        final Character childGender = 'f';
        UserDto userDto = new UserDto(name, email, Set.of(new ChildDto(childName, childGender, childAge)));

        userService.save(userDto);

        Mockito.verify(repositoryMock).save(userCaptor.capture());
        assertEquals(name, userCaptor.getValue().getName());
        assertEquals(email, userCaptor.getValue().getEmail());
        assertEquals(1, userCaptor.getValue().getChildren().size());
        userCaptor.getValue().getChildren().stream().forEach(e -> {
            assertEquals(childAge, e.getAge());
            assertEquals(childGender, e.getGender());
            assertEquals(childName, e.getName());
        });
    }
}
