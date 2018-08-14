package customer.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(exclude = "children")
public class UserDto {
    private String name;
    private String email;
    Set<ChildDto> children;
}
