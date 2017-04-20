package devnoh.demoapp.controller;

import devnoh.demoapp.domain.Partner;
import devnoh.demoapp.service.PartnerService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebAppConfiguration
public class CreditApplicationControllerTest {

    MockMvc mockMvc;

    @InjectMocks
    CreditApplicationController creditApplicationController;

    @Mock
    PartnerService partnerService;

    String requestJson;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(creditApplicationController).build();

        requestJson = new String(
                Files.readAllBytes(Paths.get(getClass().getResource("/rest/creditapp_request.json").toURI())));
    }

    @Test
    public void testCreditApplication() throws Exception {
        Partner partner = new Partner();
        partner.setId(1L);
        partner.setName("Partner 1");

        when(partnerService.getPartner(1L)).thenReturn(partner);

        mockMvc.perform(post("/creditapplication").content(requestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.header.status", is("success")))
                .andExpect(jsonPath("$.header.errors", Matchers.hasSize(0)))
                .andExpect(jsonPath("$.payload", Matchers.notNullValue()));

        when(partnerService.getPartner(1L)).thenReturn(null);

        mockMvc.perform(post("/creditapplication").content(requestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.header.status", is("failure")))
                .andExpect(jsonPath("$.header.errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.payload", Matchers.nullValue()));
    }

}
