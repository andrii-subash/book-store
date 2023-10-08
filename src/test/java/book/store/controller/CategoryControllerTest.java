package book.store.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.dto.book.BookWithoutCategoryResponseDto;
import book.store.dto.category.CategoryRequestDto;
import book.store.dto.category.CategoryResponseDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
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
class CategoryControllerTest {
    protected static MockMvc mockMvc; 
    @Autowired
    private ObjectMapper objectMapper;
    
    @BeforeAll
    static void beforeAll(@Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) {
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
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Create a new category")
    public void createCategory_ValidRequestDto_ReturnsCategoryDto() throws Exception {
        CategoryRequestDto requestDto = getCategoryRequestDto();
        CategoryResponseDto responseDto = getCategoryResponseDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        CategoryResponseDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), CategoryResponseDto.class);

        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(responseDto, actual, "id");
    }

    @Test
    @WithMockUser
    @DisplayName("Find all categories")
    @Sql(scripts = "classpath:database/add-3-categories-in-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findAll_ReturnsTwoCategoryDto() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/categories")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryResponseDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryResponseDto[].class);

        assertEquals(3, actual.length);
        assertEquals(actual[0].getName(), "poem");
        assertEquals(actual[1].getName(), "fantasy");
        assertEquals(actual[2].getName(), "biography");
    }

    @Test
    @WithMockUser
    @DisplayName("Get category by existed id")
    @Sql(scripts = "classpath:database/add-3-categories-in-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void getCategoryById_ValidId_ReturnsCategoryDto() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/categories/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryResponseDto.class);

        assertNotNull(actual);
        assertEquals(actual.getId(), 1L);
        assertEquals(actual.getName(), "poem");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Update category by existed id and valid dto")
    @Sql(scripts = "classpath:database/add-3-categories-in-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void updateCategory_ValidIdAndDto_ReturnsUpdatedCategoryDto()
            throws Exception {
        CategoryRequestDto requestDto = getCategoryRequestDto();
        CategoryResponseDto responseDto = getCategoryResponseDto().setId(2L);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        put("/categories/2")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryResponseDto.class);

        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(responseDto, actual);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Delete category by id")
    @Sql(scripts = "classpath:database/add-3-categories-in-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void deleteCategory_ValidId_CategoryMustNotBeInDb() throws Exception {
        mockMvc.perform(delete("/categories/3")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @WithMockUser
    @DisplayName("Find all books by categories id")
    @Sql(scripts = "classpath:database/"
            + "add-books-and-categories-where-biography-category-has-no-one-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findAllBooksByCategoryId_ValidId_ReturnsNoOneBookDto() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/categories/1/books")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookWithoutCategoryResponseDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookWithoutCategoryResponseDto[].class);

        assertEquals(2, actual.length);
        assertEquals(actual[0].getTitle(), "Kobzar");
        assertEquals(actual[1].getTitle(), "Poem-Fantasy book");
    }

    @Test
    @WithMockUser
    @DisplayName("Returns 'Forbidden' status if user tries to go to admin`s endpoints")
    @Sql(scripts = "classpath:database/"
            + "add-books-and-categories-where-biography-category-has-no-one-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void userGoesToAdminsEndpoints_ReturnsForbiddenStatus() throws Exception {
        CategoryRequestDto requestDto = getCategoryRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

        mockMvc.perform(put("/categories/1")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

        mockMvc.perform(delete("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Returns 'Bad request' status when admin passes not validate dto to methods")
    @Sql(scripts = "classpath:database/add-3-categories-in-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void passNotValidateDtoToMethods_ReturnsBadRequestStatus() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult updatingCategory = mockMvc.perform(
                        put("/categories/2")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        MvcResult creatingCategory = mockMvc.perform(
                        post("/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        JsonNode errorMessagesAfterUpdating = objectMapper.readTree(
                updatingCategory.getResponse().getContentAsString()).get("errors");
        JsonNode errorMessagesAfterCreating = objectMapper.readTree(
                creatingCategory.getResponse().getContentAsString()).get("errors");

        Stream.of(errorMessagesAfterUpdating, errorMessagesAfterCreating)
                .map(messages -> objectMapper.convertValue(messages, String[].class)[0])
                .forEach(message -> assertEquals("name must not be null", message));
    }

    private CategoryRequestDto getCategoryRequestDto() {
        return new CategoryRequestDto()
                .setName("Category")
                .setDescription("Description");
    }

    private CategoryResponseDto getCategoryResponseDto() {
        return new CategoryResponseDto()
                .setName("Category")
                .setDescription("Description");
    }
}
