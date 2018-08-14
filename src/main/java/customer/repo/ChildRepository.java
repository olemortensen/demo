package customer.repo;

import customer.domain.Child;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildRepository  extends CrudRepository<Child, Long> {
}
