package book.store;

import book.store.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RequiredArgsConstructor
@SpringBootApplication
public class BookStoreApplication {
    private final RoleService roleService;

    public static void main(String[] args) {
        SpringApplication.run(BookStoreApplication.class, args);
    }
}
