package customer.service;

import customer.domain.Child;
import customer.domain.User;
import customer.dto.ChildDto;
import customer.dto.UserDto;
import customer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class UserService {
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private User makeUser(UserDto userDto) {
        User repoUser = new User();
        repoUser.setName(userDto.getName());
        repoUser.setEmail(userDto.getEmail());
        Stream.ofNullable(userDto.getChildren()).flatMap(Collection::stream).forEach(childDto -> {
            Child repoChild = new Child();
            repoChild.setAge(childDto.getAge());
            repoChild.setGender(childDto.getGender());
            repoChild.setName(childDto.getName());
            repoUser.addChild(repoChild);
        });
        return repoUser;
    }

    public UserDto save(UserDto userDto) {
        userRepository.save(makeUser(userDto));
        return userDto;
    }

    public UserDto update(UserDto userDto, Long id) {
        userRepository.findById(id).ifPresentOrElse(u -> {
                    u.setEmail(userDto.getEmail());
                    u.setName(userDto.getName());
                    userRepository.save(u);
                },
                () -> {
                    throw new UserNotFoundException("[" + id + "] not found");
                });
        return userDto;
    }

    public List<UserDto> getUserDtoList() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(e -> {
                    UserDto dto = new UserDto();
                    dto.setEmail(e.getEmail());
                    dto.setName(e.getName());
                    dto.setChildren(e.getChildren().stream().map(c -> {
                        ChildDto childDto = new ChildDto();
                        childDto.setAge(c.getAge());
                        childDto.setGender(c.getGender());
                        childDto.setName(c.getName());
                        return childDto;
                    }).collect(Collectors.toSet()));
                    return dto;
                }).collect(Collectors.toList());
    }
}
