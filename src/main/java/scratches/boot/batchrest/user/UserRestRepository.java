package scratches.boot.batchrest.user;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;

/**
 * @author Rashidi Zin
 */
@Repository
public class UserRestRepository {

    private final RestTemplate restTemplate;

    public UserRestRepository(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @Nullable
    public User findByUsername(String username) {
        return findAll().stream()
                .filter(user -> username.equalsIgnoreCase(user.getUsername()))
                .findFirst()
                .orElse(null);
    }

    private List<User> findAll() {
        var uri = URI.create("https://jsonplaceholder.typicode.com/users");

        return restTemplate
                .exchange(uri, GET, null, new ParameterizedTypeReference<List<User>>() {})
                .getBody();
    }

}
