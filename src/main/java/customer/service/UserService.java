package customer.service;

import customer.domain.Child;
import customer.domain.User;
import customer.dto.ChildDto;
import customer.dto.UserDto;
import customer.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto save(UserDto userDto) {
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
        userRepository.save(repoUser);
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
