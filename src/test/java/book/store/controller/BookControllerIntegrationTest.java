package book.store.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.dto.book.BookRequestDto;
import book.store.dto.book.BookResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
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
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerIntegrationTest {
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
                    new ClassPathResource("database/remove-books-and-categories-from-tables.sql"));
        }
    }
    
    @AfterEach
    void afterEach(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Create a new book")
    @Sql(scripts = "classpath:database/add-3-categories-in-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void createBook_ValidRequestDto_ReturnsBookDto() throws Exception {
        BookRequestDto requestDto = getBookRequestDto();
        BookResponseDto responseDto = getBookResponseDto();
        
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        
        MvcResult result = mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                                   .andExpect(status().isCreated())
                                   .andReturn();
        BookResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookResponseDto.class);
        
        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(responseDto, actual, "id");
    }
    
    @Test
    @WithMockUser
    @DisplayName("Find all books")
    @Sql(scripts = "classpath:database/add-3-books-in-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findAll_ReturnsTwoBookDto() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/books")
                                .contentType(MediaType.APPLICATION_JSON))
                                   .andExpect(status().isOk())
                                   .andReturn();
        
        BookResponseDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookResponseDto[].class);
        
        assertEquals(3, actual.length);
        assertEquals("First Book", actual[0].getTitle());
        assertEquals("Second Book", actual[1].getTitle());
        assertEquals("Third Book", actual[2].getTitle());
    }
    
    @Test
    @WithMockUser
    @DisplayName("Get book by existed id")
    @Sql(scripts = "classpath:database/add-3-books-in-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void getBookById_ValidId_ReturnsBookDto() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/books/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                   .andExpect(status().isOk())
                                   .andReturn();
        
        BookResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookResponseDto.class);
        
        assertNotNull(actual);
        assertEquals(1L, actual.getId());
        assertEquals("First Book", actual.getTitle());
    }
    
    @Test
    @WithMockUser
    @DisplayName("Get books by search parameters")
    @Sql(scripts = "classpath:database/add-3-books-in-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void searchBooks_ValidBookParameters_ReturnsOneBookDto() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/books/search?title=Third Book&author=Third Author")
                                .contentType(MediaType.APPLICATION_JSON))
                                   .andExpect(status().isOk())
                                   .andReturn();
        
        BookResponseDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookResponseDto[].class);
        
        assertEquals(1, actual.length);
        assertEquals(3, actual[0].getId());
        assertEquals("12345678902", actual[0].getIsbn());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Update book by existed id and valid dto")
    @Sql(scripts = "classpath:database/"
                           + "add-books-and-categories-where-poem-category-has-two-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void updateBook_ValidIdAndDto_ReturnsBookDto() throws Exception {
        BookRequestDto requestDto = getBookRequestDto();
        BookResponseDto responseDto = getBookResponseDto().setId(2L);
        
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        
        MvcResult result = mockMvc.perform(
                        put("/books/2")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                                   .andExpect(status().isOk())
                                   .andReturn();
        
        BookResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookResponseDto.class);
        
        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(responseDto, actual);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Delete book by id")
    @Sql(scripts = "classpath:database/add-3-books-in-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void deleteBook_ValidId_BookMustNotBeInDb() throws Exception {
        mockMvc.perform(delete("/books/3")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }
    
    @Test
    @WithMockUser
    @DisplayName("Returns 'Forbidden' status if user tries to go to admin`s endpoints")
    @Sql(scripts = "classpath:database/add-3-books-in-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void userGoesToAdminsEndpoints_ReturnsForbiddenStatus() throws Exception {
        BookRequestDto requestDto = getBookRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        
        mockMvc.perform(post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
        
        mockMvc.perform(put("/books/1")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
        
        mockMvc.perform(delete("/books/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Returns 'Bad request' status when admin passes not validate dto to methods")
    @Sql(scripts = "classpath:database/add-3-books-in-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void passNotValidateDtoToMethods_ReturnsBadRequestStatus() throws Exception {
        BookRequestDto requestDtoWithEmptyFields = new BookRequestDto();
        BookRequestDto requestDtoWithNegativePriceAndWrongIsbn = getBookRequestDto()
                                                                         .setIsbn("123")
                                                                         .setPrice(-15.25);
        
        String jsonRequestWithEmptyFields =
                objectMapper.writeValueAsString(requestDtoWithEmptyFields);
        String jsonRequestWithNegativePriceAndWrongIsbn =
                objectMapper.writeValueAsString(requestDtoWithNegativePriceAndWrongIsbn);
        
        MvcResult updatingBook = mockMvc.perform(
                        put("/books/2")
                                .content(jsonRequestWithEmptyFields)
                                .contentType(MediaType.APPLICATION_JSON))
                                         .andExpect(status().isBadRequest())
                                         .andReturn();
        
        MvcResult creatingBook = mockMvc.perform(
                        post("/books")
                                .content(jsonRequestWithNegativePriceAndWrongIsbn)
                                .contentType(MediaType.APPLICATION_JSON))
                                         .andExpect(status().isBadRequest())
                                         .andReturn();
        
        List<String> errorMessages =
                Stream.of(updatingBook, creatingBook)
                        .map(this::getErrorResponse)
                        .map(messages -> objectMapper.convertValue(messages, String[].class))
                        .flatMap(Arrays::stream)
                        .toList();
        
        assertEquals(7, errorMessages.size());
        assertTrue(errorMessages.contains("isbn size must be between 14 and 20"));
        assertTrue(errorMessages.contains("price must be greater than or equal to 0"));
        assertTrue(errorMessages.contains("price must not be null"));
        assertTrue(errorMessages.contains("title must not be null"));
        assertTrue(errorMessages.contains("isbn must not be null"));
        assertTrue(errorMessages.contains("categoryIds must not be null"));
        assertTrue(errorMessages.contains("author must not be null"));
    }
    
    private JsonNode getErrorResponse(MvcResult mvcResult) {
        try {
            return objectMapper.readTree(mvcResult.getResponse()
                                                 .getContentAsString()).get("errors");
        } catch (JsonProcessingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    private BookRequestDto getBookRequestDto() {
        return new BookRequestDto()
                       .setTitle("Title")
                       .setAuthor("Author")
                       .setPrice(17.95)
                       .setIsbn("111222333444555")
                       .setCategoryIds(Set.of(1L, 2L));
    }
    
    private BookResponseDto getBookResponseDto() {
        return new BookResponseDto()
                       .setTitle("Title")
                       .setAuthor("Author")
                       .setPrice(17.95)
                       .setIsbn("111222333444555")
                       .setCategoryIds(Set.of(1L, 2L));
    }
}
