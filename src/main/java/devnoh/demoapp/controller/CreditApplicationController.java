package devnoh.demoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import devnoh.demoapp.domain.Partner;
import devnoh.demoapp.dto.CreditApplication;
import devnoh.demoapp.dto.ErrorDTO;
import devnoh.demoapp.dto.RequestMessage;
import devnoh.demoapp.dto.ResponseHeader;
import devnoh.demoapp.dto.ResponseMessage;
import devnoh.demoapp.error.PartnerNotFoundException;
import devnoh.demoapp.service.PartnerService;
import devnoh.demoapp.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;

@RestController
@Slf4j
public class CreditApplicationController {

    @Autowired
    PartnerService partnerService;

    @Autowired
    private SecurityService securityService;

    @PostMapping(value = "/creditapplication",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseMessage creditApplication(@RequestBody @Valid RequestMessage message) {
        ResponseMessage responseMessage = new ResponseMessage();
        try {
            log.info("message={}", message);

            Long partnerId = Long.valueOf(message.getHeader().getPid());
            Partner partner = partnerService.getPartner(partnerId);
            if (partner == null) {
                throw new PartnerNotFoundException(partnerId);
            }

            securityService.verifyMessageSignature(message, partnerId);

            String requestPayload = new String(Base64.getDecoder().decode(message.getPayload().toString()), "UTF-8");
            log.debug("requestPayload={}", requestPayload);

            ObjectMapper objectMapper = new ObjectMapper();
            CreditApplication creditApplication = objectMapper.readValue(requestPayload, CreditApplication.class);
            log.debug("creditApplication={}", creditApplication);

            // TODO: use the correct response object
            Map<String, String> responseObject = new HashMap<>();
            responseObject.put("applicationId", "330015");
            log.debug("responseObject={}", responseObject);

            String responsePayload = objectMapper.writeValueAsString(responseObject);
            log.debug("responsePayload={}", responseMessage);

            ResponseHeader responseHeader = new ResponseHeader(ResponseHeader.STATUS_SUCCESS);
            responseMessage.setHeader(responseHeader);
            responseMessage.setPayload(Base64.getEncoder().encode(responsePayload.getBytes("UTF-8")));

            securityService.addMessageSignature(responseMessage);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ResponseHeader responseHeader = new ResponseHeader(ResponseHeader.STATUS_FAILURE);
            responseHeader.setErrors(Arrays.asList(new ErrorDTO("3001", e.getMessage())));
            responseMessage.setHeader(responseHeader);
        }
        return responseMessage;
    }

}
