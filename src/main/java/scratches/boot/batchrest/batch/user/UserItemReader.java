package scratches.boot.batchrest.batch.user;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import scratches.boot.batchrest.user.User;
import scratches.boot.batchrest.user.UserRestRepository;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Rashidi Zin
 */
@RequiredArgsConstructor
public class UserItemReader implements ItemReader<User> {

    @NonNull
    private final UserRestRepository repository;

    private static final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public User read() {
        var username = getUsername();
        return repository.findByUsername(username);
    }

    private String getUsername() {
        return counter.getAndIncrement() == 0 ? "Samantha" : "Rashidi";
    }

}
