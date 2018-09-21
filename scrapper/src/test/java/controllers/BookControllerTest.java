package controllers;

import com.google.gson.Gson;
import model.Book;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import providers.BooksProvider;
import service.BookService;

import java.util.Locale;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnableSpringDataWebSupport
class BookControllerTest {
    private static final Logger LOG = LoggerFactory.getLogger(BookControllerTest.class);
    private MockMvc mockMvc;
    private Pageable pageRequest;
    private BookService service;
    private BookPageResourceAssembler assembler;
    private Gson gson;

    @BeforeAll
    void setUp() {
        service = mock(BookService.class);
        assembler = new BookPageResourceAssembler();
        mockMvc = standaloneSetup(new BookController(assembler, service))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        gson = new Gson();
    }

    @Test
    void getFirstPageWithTwoBooks_ExpectResponseStatusOkWithProperResponseBody() {
        int pageNumber = 0;
        int size = 2;
        pageRequest = PageRequest.of(pageNumber, size);
        Page<Book> responsePage = new PageImpl<>(
                BooksProvider.provideAllBooksFromEbookpoint(),
                pageRequest,
                4);
        when(service.findPaginated(pageRequest)).thenReturn(responsePage);
        try {
            mockMvc.perform(get("/books")
                    .param("page", String.valueOf(pageNumber))
                    .param("size", String.valueOf(size)))
                    .andExpect(content()
                            .json(gson.toJson(assembler.toResource(responsePage)))
                    )
                    .andExpect(status().isOk());
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }
}
