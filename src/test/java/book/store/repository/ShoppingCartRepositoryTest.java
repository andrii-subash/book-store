package book.store.repository;

import book.store.model.ShoppingCart;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    
    @Test
    @DisplayName("Find shopping cart by valid user email")
    @Sql(scripts = "classpath:database/add-users-and-shopping-carts-to-tables.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/remove-users-and-shopping-carts-from-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findShoppingCartByUserEmail_ValidEmail_ReturnsOneShoppingCart() {
        ShoppingCart shoppingCart =
                shoppingCartRepository.findShoppingCartByUserEmail("user123@email.com");
        Assertions.assertNotNull(shoppingCart);
        Assertions.assertEquals(1L, shoppingCart.getId());
        Assertions.assertEquals(1L, shoppingCart.getUser().getId());
    }
    
    @Test
    @DisplayName("Find shopping cart by not valid user email")
    @Sql(scripts = "classpath:database/add-users-and-shopping-carts-to-tables.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/remove-users-and-shopping-carts-from-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findShoppingCartByUserEmail_NotValidEmail_ReturnsNull() {
        ShoppingCart shoppingCart =
                shoppingCartRepository.findShoppingCartByUserEmail("user123@email");
        Assertions.assertNull(shoppingCart);
    }
}
