package book.store.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.dto.cart.CartItemCreateRequestDto;
import book.store.dto.cart.CartItemResponseDto;
import book.store.dto.cart.CartItemUpdateRequestDto;
import book.store.dto.cart.ShoppingCartResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingCartControllerIntegrationTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    
    @BeforeAll
    static void beforeAll(@Autowired DataSource dataSource,
                          @Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                          .webAppContextSetup(applicationContext)
                          .apply(SecurityMockMvcConfigurers.springSecurity())
                          .build();
        teardown(dataSource);
    }
    
    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(
                            "database/remove-cart-items-"
                                    + "books-shopping-carts-users-from-tables.sql"));
        }
    }
    
    @AfterEach
    void afterEach(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }
    
    @Test
    @WithMockUser(username = "user234@email.com")
    @DisplayName("Add a book to shopping cart")
    @Sql(scripts = "classpath:database/add-users-shopping-carts-books-and-cart-items-to-tables.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void addBookToShoppingCart_ValidEmailAndCartItem_ReturnsShoppingCartDto()
            throws Exception {
        CartItemCreateRequestDto cartItemCreateRequestDto = getCartItemCreateRequestDto();
        CartItemResponseDto responseDto = getCartItemResponseDto().setId(2L);
        String jsonRequest = objectMapper.writeValueAsString(cartItemCreateRequestDto);
        
        MvcResult result = mockMvc.perform(
                        post("/cart")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                                   .andExpect(status().isOk())
                                   .andReturn();
        
        ShoppingCartResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartResponseDto.class);
        
        assertNotNull(actual);
        assertTrue(actual.getCartItems().contains(responseDto));
    }
    
    @Test
    @WithMockUser(username = "user123@email.com")
    @DisplayName("Update a book quantity if books are exist in shopping cart")
    @Sql(scripts = "classpath:database/add-users-shopping-carts-books-and-cart-items-to-tables.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void addBookToShoppingCart_ValidCartItem_ReturnsShoppingCartDtoWithUpdatedBookQuantity()
            throws Exception {
        CartItemCreateRequestDto cartItemCreateRequestDto = getCartItemCreateRequestDto();
        CartItemResponseDto responseDto = getCartItemResponseDto().setQuantity(8);
        String jsonRequest = objectMapper.writeValueAsString(cartItemCreateRequestDto);
        
        MvcResult result = mockMvc.perform(
                        post("/cart")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                                   .andExpect(status().isOk())
                                   .andReturn();
        
        ShoppingCartResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartResponseDto.class);
        
        assertNotNull(actual);
        assertTrue(actual.getCartItems().contains(responseDto));
    }
    
    @Test
    @WithMockUser(username = "user123@email.com")
    @DisplayName("Get a shopping cart")
    @Sql(scripts = "classpath:database/add-users-and-shopping-carts-to-tables.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void getShoppingCartByUserEmail_ValidEmail_ReturnsShoppingCartDto() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/cart")
                                .contentType(MediaType.APPLICATION_JSON))
                                   .andExpect(status().isOk())
                                   .andReturn();
        
        ShoppingCartResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartResponseDto.class);
        
        assertNotNull(actual);
        assertEquals(1L, actual.getId());
        assertEquals(1L, actual.getUserId());
    }
    
    @Test
    @WithMockUser(username = "user123@email.com")
    @DisplayName("Update books quantity in shopping cart")
    @Sql(scripts = "classpath:database/add-users-shopping-carts-books-and-cart-items-to-tables.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void updateCartItem_ValidEmailAndCartItem_ReturnsShoppingCartDto() throws Exception {
        CartItemUpdateRequestDto cartItemCreateRequestDto = getCartItemUpdateRequestDto();
        CartItemResponseDto responseDto = getCartItemResponseDto().setQuantity(
                cartItemCreateRequestDto.getQuantity());
        String jsonRequest = objectMapper.writeValueAsString(cartItemCreateRequestDto);
        
        MvcResult result = mockMvc.perform(
                        put("/cart/cart-items/1")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                                   .andExpect(status().isOk())
                                   .andReturn();
        
        ShoppingCartResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartResponseDto.class);
        
        assertNotNull(actual);
        assertTrue(actual.getCartItems().contains(responseDto));
    }
    
    @Test
    @WithMockUser(username = "user123@email.com")
    @DisplayName("Delete a book from shopping cart")
    @Sql(scripts = "classpath:database/add-users-shopping-carts-books-and-cart-items-to-tables.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void deleteCartItem_ValidCartItemId_ReturnsShoppingCartDtoWithoutCartItem()
            throws Exception {
        MvcResult result = mockMvc.perform(
                        delete("/cart/cart-items/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                   .andExpect(status().isNoContent())
                                   .andReturn();
        
        ShoppingCartResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartResponseDto.class);
        
        assertNotNull(actual);
        assertEquals(0, actual.getCartItems().size());
    }
    
    @Test
    @WithMockUser(username = "admin@email.com", roles = "ADMIN")
    @DisplayName("Returns 'Forbidden' status if admin tries to go to users`s endpoints")
    @Sql(scripts = "classpath:database/add-users-shopping-carts-books-and-cart-items-to-tables.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void adminGoesToUsersEndpoints_ReturnsForbiddenStatus() throws Exception {
        CartItemCreateRequestDto createRequestDto = getCartItemCreateRequestDto();
        CartItemUpdateRequestDto updateRequestDto = getCartItemUpdateRequestDto();
        
        String jsonCreateRequest = objectMapper.writeValueAsString(createRequestDto);
        String jsonUpdateRequest = objectMapper.writeValueAsString(updateRequestDto);
        
        mockMvc.perform(post("/cart")
                                .content(jsonCreateRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
        
        mockMvc.perform(get("/cart")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
        
        mockMvc.perform(put("/cart/cart-items/1")
                                .content(jsonUpdateRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
        
        mockMvc.perform(delete("/cart/cart-items/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
    }
    
    @Test
    @WithMockUser(username = "user123@email.com")
    @DisplayName("Returns 'Bad request' status when user passes not validate dto to methods")
    @Sql(scripts = "classpath:database/add-users-shopping-carts-books-and-cart-items-to-tables.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void passNotValidateDtoToMethods_ReturnsBadRequestStatus() throws Exception {
        CartItemCreateRequestDto createRequestDtoWithEmptyFields =
                new CartItemCreateRequestDto();
        CartItemCreateRequestDto createRequestDtoWithNegativeQuantity =
                getCartItemCreateRequestDto().setQuantity(-5);
        CartItemUpdateRequestDto updateRequestDtoWithEmptyFields =
                new CartItemUpdateRequestDto();
        CartItemUpdateRequestDto updateRequestDtoWithNegativeQuantity =
                getCartItemUpdateRequestDto().setQuantity(-50);
        
        String jsonCreateRequestWithEmptyFields =
                objectMapper.writeValueAsString(createRequestDtoWithEmptyFields);
        String jsonCreateRequestWithNegativeQuantity =
                objectMapper.writeValueAsString(createRequestDtoWithNegativeQuantity);
        String jsonUpdateRequestWithEmptyFields =
                objectMapper.writeValueAsString(updateRequestDtoWithEmptyFields);
        String jsonUpdateRequestWithNegativeQuantity =
                objectMapper.writeValueAsString(updateRequestDtoWithNegativeQuantity);
        
        List<MvcResult> mvcResults = new ArrayList<>();
        
        mvcResults.add(mockMvc.perform(post("/cart")
                                               .content(jsonCreateRequestWithEmptyFields)
                                               .contentType(MediaType.APPLICATION_JSON))
                               .andExpect(status().isBadRequest())
                               .andReturn());
        
        mvcResults.add(mockMvc.perform(post("/cart")
                                               .content(jsonCreateRequestWithNegativeQuantity)
                                               .contentType(MediaType.APPLICATION_JSON))
                               .andExpect(status().isBadRequest())
                               .andReturn());
        
        mvcResults.add(mockMvc.perform(put("/cart/cart-items/1")
                                               .content(jsonUpdateRequestWithEmptyFields)
                                               .contentType(MediaType.APPLICATION_JSON))
                               .andExpect(status().isBadRequest())
                               .andReturn());
        
        mvcResults.add(mockMvc.perform(put("/cart/cart-items/1")
                                               .content(jsonUpdateRequestWithNegativeQuantity)
                                               .contentType(MediaType.APPLICATION_JSON))
                               .andExpect(status().isBadRequest())
                               .andReturn());
        
        List<String> errorMessages =
                mvcResults.stream()
                        .map(this::getErrorResponse)
                        .map(messages -> objectMapper.convertValue(messages, String[].class))
                        .flatMap(Arrays::stream)
                        .toList();
        
        assertEquals(5, errorMessages.size());
        assertTrue(errorMessages.contains("quantity must be greater than or equal to 1"));
        assertTrue(errorMessages.contains("quantity must not be null"));
        assertTrue(errorMessages.contains("bookId must not be null"));
    }
    
    private JsonNode getErrorResponse(MvcResult mvcResult) {
        try {
            return objectMapper.readTree(mvcResult.getResponse()
                                                 .getContentAsString()).get("errors");
        } catch (JsonProcessingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    private CartItemResponseDto getCartItemResponseDto() {
        return new CartItemResponseDto()
                       .setId(1L)
                       .setBookId(1L)
                       .setBookTitle("First Book")
                       .setQuantity(3);
    }
    
    private CartItemCreateRequestDto getCartItemCreateRequestDto() {
        return new CartItemCreateRequestDto()
                       .setBookId(1L)
                       .setQuantity(3);
    }
    
    private CartItemUpdateRequestDto getCartItemUpdateRequestDto() {
        return new CartItemUpdateRequestDto()
                       .setQuantity(2);
    }
}
