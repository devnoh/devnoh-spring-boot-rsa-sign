package devnoh.demoapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import devnoh.demoapp.dto.RequestHeader;
import devnoh.demoapp.dto.RequestMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class WebTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Before
    public void setUp() {
    }

    @Ignore
    @Test
    public void testWeb() throws JsonProcessingException {

        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setHeader(new RequestHeader("10001"));

        ObjectMapper mapper = new ObjectMapper();
        String requestBody = mapper.writeValueAsString(requestMessage);
        log.info("requestBody={}", requestBody);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        String response = restTemplate.postForObject("/creditapplication", request, String.class);
        log.info("response={}", response);
    }

}
