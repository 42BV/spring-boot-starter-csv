package nl._42.boot.csv;

import nl._42.boot.csv.document.CsvDocument;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class CsvControllerTest {

    private static final String BASE_URL = "/csv";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CsvProperties properties;

    @Autowired
    private CsvService csvService;

    private MockMvc webClient;

    @BeforeEach
    public void initWebClient() {
        DefaultMockMvcBuilder builder = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .defaultRequest(get("/").contentType(APPLICATION_JSON));

        this.webClient = builder.build();
    }

    @Test
    public void getParameters_shouldSucceed() throws Exception {
        this.webClient.perform(get(BASE_URL))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.quote", Matchers.is("" + properties.getQuote())))
                .andExpect(jsonPath("$.separator", Matchers.is("" + properties.getSeparator())))
                .andExpect(jsonPath("$.types", Matchers.is(properties.getTypes())));
    }

    @Test
    public void getDocument_shouldSucceed() throws Exception {
        this.webClient.perform(get(BASE_URL + "/document")
                        .param("type", PersonCsvHandler.TYPE))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.type", Matchers.is(PersonCsvHandler.TYPE)))
                .andExpect(jsonPath("$.description", Matchers.is("Describe the persons known in this system")))
                .andExpect(jsonPath("$.columns", Matchers.hasSize(9)))
                .andExpect(jsonPath("$.columns[0].name", Matchers.is("first_name")))
                .andExpect(jsonPath("$.columns[0].pattern", Matchers.is("first_name")))
                .andExpect(jsonPath("$.columns[0].description", Matchers.is("The first name")))
                .andExpect(jsonPath("$.columns[0].example", Matchers.is("Piet")))
                .andExpect(jsonPath("$.columns[8].name", Matchers.is("gender")))
                .andExpect(jsonPath("$.columns[8].pattern", Matchers.is("{property}")))
                .andExpect(jsonPath("$.content").exists());
    }

    @Test
    public void upload_shouldSucceed() throws Exception {
        CsvDocument document = csvService.getDocument(PersonCsvHandler.TYPE);
        byte[] content = document.getContent().getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "persons.csv", "text/plain", content);

        this.webClient.perform(multipart(BASE_URL)
                        .file(file)
                        .param("type", PersonCsvHandler.TYPE)
                        .param("separator", "" + properties.getSeparator())
                        .param("quote", "" + properties.getQuote()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.success", Matchers.is(1)))
                .andExpect(jsonPath("$.rows", Matchers.is(1)))
                .andExpect(jsonPath("$.errors", Matchers.hasSize(0)));
    }

}
