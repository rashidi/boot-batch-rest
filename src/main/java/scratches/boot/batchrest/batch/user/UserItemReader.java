package scratches.boot.batchrest.batch.user;

import lombok.AllArgsConstructor;
import org.springframework.batch.item.ItemReader;
import scratches.boot.batchrest.user.User;
import scratches.boot.batchrest.user.UserRestRepository;

/**
 * @author Rashidi Zin
 */
@AllArgsConstructor
public class UserItemReader implements ItemReader<User> {

    private final UserRestRepository repository;

    @Override
    public User read() {
        return repository.findByUsername("Samantha");
    }

}
