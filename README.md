# Spring Batch With REST
Implement batch operation for REST service with [Spring Batch][1].

## Background
Spring Batch allows us to perform large volumes of records from several resources such as [File][3], [Jpa][4], and, [JSON][5].
However, there is no option for REST Services.

In this tutorial we will implement batch operation which will retrieve and persist through REST operation. We will be 
using [User REST service][6] and [Post Rest Service][7] provided by [JSONPlaceHolder][2].

## Dependencies
  - JDK 11
  - spring-boot-starter-batch
  - spring-boot-starter-web
  - H2 Database
  - Lombok
  - [JSONPlaceHolder][2] for REST Service

## REST Repositories
We will implement two repositories which will handle our REST operations.

### [UserRestRepository][8] 
`UserRestRepository` will be responsible to retrieve all `User` and filter based on `username`.

```java
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
```

### [PostRestRepository][9]
`PostRestRepository` will be responsible to persist new `Post`.

```java
@Repository
public class PostRestRepository {

    private final RestTemplate restTemplate;

    public PostRestRepository(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public Post save(Post post) {
        var uri = URI.create("https://jsonplaceholder.typicode.com/posts");

        return restTemplate.postForEntity(uri, post, Post.class).getBody();
    }

}
```

## ItemReader, ItemProcessor, and, ItemWriter

### [UserItemReader][10]
`UserItemReader` will retrieve `User` information through `UserRestRepository`. The `Job` will be completed once `UserItemReader`
returns `null`. In this case, it is when `username` value is `Rashidi`.

It will be `null` for `Rashidi` because the name is not available from [User Rest Service][8].

```java
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
```

### [PostItemProcessor][11]
`PostItemProcessor` will be responsible to convert `User` into `Post` which will be used by `ItemWriter`.

```java
public class PostItemProcessor implements ItemProcessor<User, Post> {

    @Override
    public Post process(User user) {
        return Post.builder()
                .userId(user.getId())
                .title("Spring Boot Batch Rest")
                .body("Implement rest operation with Spring Boot Batch")
                .build();

    }
}
```

### [PostItemWriter][12]
Finally, we will use `PostItemWriter` to persist `Post` objects into [Post REST service][7].

```java
@AllArgsConstructor
public class PostItemWriter implements ItemWriter<Post> {

    private final PostRestRepository repository;

    @Override
    public void write(List<? extends Post> items) {
        items.forEach(repository::save);
    }

}
```

### Configure Batch Operation
We will configure necessary `@Bean` in order for Spring to be aware about our `Job`.

### ItemReader, ItemProcessor, and, ItemWriter
We start by configuring main components

```java
public class BatchConfiguration {

    @Bean
    public UserItemReader userItemReader(UserRestRepository repository) {
        return new UserItemReader(repository);
    }

    @Bean
    public PostItemProcessor postItemProcessor() {
        return new PostItemProcessor();
    }

    @Bean
    public PostItemWriter postItemWriter(PostRestRepository repository) {
        return new PostItemWriter(repository);
    }

}
```

Now, Spring knows about relevant components, we will configure `Step` and `Job`

### Step and Job
```java
public class BatchConfiguration {

    @Bean
    public Step postStep(StepBuilderFactory factory, UserItemReader reader, PostItemWriter writer) {
        return factory.get("postStep")
                .<User, Post>chunk(1)
                .reader(reader)
                .processor(postItemProcessor())
                .writer(writer)
                .build();
    }

    @Bean
    public Job postJob(JobBuilderFactory factory, Step postStep) {
        return factory.get("postJob").incrementer(new RunIdIncrementer()).flow(postStep).end().build();
    }

}
```

### Enable Batch Operation
Finally, we will enable batch operation for the application by annotating [BatchConfiguration][13]:

```java
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

}
```

Final implementation should be as follows:

```java
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Bean
    public UserItemReader userItemReader(UserRestRepository repository) {
        return new UserItemReader(repository);
    }

    @Bean
    public PostItemProcessor postItemProcessor() {
        return new PostItemProcessor();
    }

    @Bean
    public PostItemWriter postItemWriter(PostRestRepository repository) {
        return new PostItemWriter(repository);
    }

    @Bean
    public Step postStep(StepBuilderFactory factory, UserItemReader reader, PostItemWriter writer) {
        return factory.get("postStep")
                .<User, Post>chunk(1)
                .reader(reader)
                .processor(postItemProcessor())
                .writer(writer)
                .build();
    }

    @Bean
    public Job postJob(JobBuilderFactory factory, Step postStep) {
        return factory.get("postJob").incrementer(new RunIdIncrementer()).flow(postStep).end().build();
    }

}
```

## Verification
As usual, we will do our verification via unit test with the help [SpringBatchTest][14].

We will verify that `PostRepository` will only be called once as there is only one valid `User` returned from `UserRepository`,
i.e. `Samantha`. We will also verify that `Post` request will be created with Samantha's `ID` and `Job` execution will be
`COMPLETED`.

```java
@SpringBatchTest
@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = BatchConfiguration.class)
class BatchConfigurationTests {

    @MockBean
    private UserRestRepository userRepository;

    @MockBean
    private PostRestRepository postRepository;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    @DisplayName("PostRepository should be executed based on number existing user")
    public void postJob() throws Exception {
        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);
        var existingUser = User.builder().id(1L).username("Samantha").build();
        var createdPost = Post.builder().id(2L).build();

        doReturn(existingUser).when(userRepository).findByUsername("Samantha");

        doReturn(null).when(userRepository).findByUsername("Rashidi");

        doReturn(createdPost).when(postRepository).save(any(Post.class));

        var execution = jobLauncherTestUtils.launchJob();

        verify(postRepository).save(postArgumentCaptor.capture());

        assertThat(postArgumentCaptor.getValue())
                .extracting(Post::getUserId)
                .isEqualTo(existingUser.getId());

        assertThat(execution)
                .extracting(JobExecution::getExitStatus)
                .isEqualTo(COMPLETED);
    }

}
```

Full implementation can be found in [BatchConfigurationTests][15].

[1]: https://spring.io/projects/spring-batch
[2]: https://jsonplaceholder.typicode.com/
[3]: https://docs.spring.io/spring-batch/docs/current/api/org/springframework/batch/item/file/FlatFileItemReader.html
[4]: https://docs.spring.io/spring-batch/docs/current/api/org/springframework/batch/item/database/JpaPagingItemReader.html
[5]: https://docs.spring.io/spring-batch/docs/current/api/org/springframework/batch/item/json/JsonItemReader.html
[6]: https://jsonplaceholder.typicode.com/users
[7]: https://jsonplaceholder.typicode.com/posts
[8]: src/main/java/scratches/boot/batchrest/user/UserRestRepository.java
[9]: src/main/java/scratches/boot/batchrest/post/PostRestRepository.java
[10]: src/main/java/scratches/boot/batchrest/batch/user/UserItemReader.java
[11]: src/main/java/scratches/boot/batchrest/batch/post/PostItemProcessor.java
[12]: src/main/java/scratches/boot/batchrest/batch/post/PostItemWriter.java
[13]: src/main/java/scratches/boot/batchrest/batch/BatchConfiguration.java
[14]: https://docs.spring.io/spring-batch/docs/current/api/org/springframework/batch/test/context/SpringBatchTest.html
[15]: src/test/java/scratches/boot/batchrest/batch/BatchConfigurationTests.java
