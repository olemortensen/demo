package customer.controller;

import customer.dto.UserDto;
import customer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/demo")
public class MainController {
    private final UserService userService;

    @Autowired
    public MainController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/user")
    public @ResponseBody
    UserDto createUser(@RequestBody UserDto userDto) {
        return userService.save(userDto);
    }


    @GetMapping("/users")
    public @ResponseBody
    List<UserDto> getAllUsers() {
        return userService.getUserDtoList();
    }
}
