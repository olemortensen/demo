package customer.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(exclude = "children")
public class UserDto {
    private String name;
    private String email;
    List<ChildDto> children;
}
