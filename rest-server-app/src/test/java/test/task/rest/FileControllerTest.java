package test.task.rest;

import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import test.task.PageableListWrapper;
import test.task.FileRecord;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static test.task.rest.helpers.SearchParams.searchParams;

@ActiveProfiles({ "test" })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FileControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void listFilesTest() {

        var rest = restTemplate.withBasicAuth("user", "password");

        var parameters = new LinkedMultiValueMap<String, Object>();
        parameters.add("fileName", "image.jpg");
        parameters.add("contents", new org.springframework.core.io.ClassPathResource("test-files/image.jpg"));

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        var entity = new HttpEntity<>(parameters, headers);

        var response1 = rest.exchange("http://localhost:" + port + "/file", HttpMethod.PUT, entity, FileRecord.class);

        assertThat(response1.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response1.getBody().getName(), equalTo("image.jpg"));
        assertThat(response1.getBody().getOwner(), equalTo("user"));

        var response2 = rest.getForEntity("http://localhost:" + port + "/files", RecordsList.class);

        assertThat(response2.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response2.getBody(), not(nullValue()));
        assertThat(response2.getBody().getList(), hasSize(1));
        assertThat(response2.getBody().getList().get(0).getName(), equalTo("image.jpg"));
    }

    public static class RecordsList extends PageableListWrapper<FileRecord> {

        public RecordsList(List<FileRecord> list, int currentPage, int pagesCount) {
            super(list, currentPage, pagesCount);
        }
    }
}
