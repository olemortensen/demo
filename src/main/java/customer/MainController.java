package customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path="/demo")
public class MainController {
	@Autowired
	private UserRepository userRepository;

	@Autowired
    private ChildRepository childRepository;

	@PostMapping(path="/user")
	public @ResponseBody User createUser (@RequestBody UserDto userDto) {
	    User repoUser = new User();
	    repoUser.setName(userDto.getName());
	    repoUser.setEmail(userDto.getEmail());
	    for (ChildDto childDto : userDto.getChildren()) {
	        Child repoChild = new Child();
	        repoChild.setAge(childDto.getAge());
	        repoChild.setGender(childDto.getGender());
	        repoChild.setName(childDto.getName());
	        repoUser.addChild(repoChild);
        }
	    return userRepository.save(repoUser);

	}

	
	@GetMapping(path="/all")
	public @ResponseBody Iterable<User> getAllUsers() {
		return userRepository.findAll();
	}
}
