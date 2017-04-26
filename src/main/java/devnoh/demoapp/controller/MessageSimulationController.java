package devnoh.demoapp.controller;

import devnoh.demoapp.dto.ErrorDTO;
import devnoh.demoapp.dto.RequestHeader;
import devnoh.demoapp.dto.RequestMessage;
import devnoh.demoapp.dto.ResponseHeader;
import devnoh.demoapp.dto.ResponseMessage;
import devnoh.demoapp.dto.Security;
import devnoh.demoapp.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

@RestController
@Slf4j
public class MessageSimulationController {

    /**
     * 1. Partner - Generate a signed credit application request message
     *
     * @param jsonPayload
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/simulate/partner/generatecreditapplication",
            consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RequestMessage generateCreditApplicationMessage(@RequestBody String jsonPayload) throws Exception {
        try {
            // Partner's private key
            InputStream inputStream = getClass().getResourceAsStream("/security/partner_private.key");
            String privateKeyPem = IOUtils.toString(inputStream, "UTF-8");
            PrivateKey privateKey = SecurityUtil.loadPrivateKey(privateKeyPem);

            String payloadBase64 = Base64.getEncoder().encodeToString(jsonPayload.getBytes("UTF-8"));
            String signature = SecurityUtil.sign(payloadBase64, privateKey);

            RequestMessage requestMessage = new RequestMessage();
            requestMessage.setHeader(new RequestHeader("1"));
            requestMessage.setPayload(payloadBase64);
            requestMessage.setSecurity(new Security(signature));

            log.debug("requestMessage={}", requestMessage);
            return requestMessage;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 2. Server - Process the credit application request message
     *
     * @param requestMessage
     * @return
     */
    @PostMapping(value = "/simulate/server/creditapplication",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseMessage processCreditApplicationMessage(@RequestBody RequestMessage requestMessage) {
        try {
            // Partner's public key
            InputStream inputStream = getClass().getResourceAsStream("/security/partner_public.key");
            String publicKeyPem = IOUtils.toString(inputStream, "UTF-8");
            PublicKey publicKey = SecurityUtil.loadPublickey(publicKeyPem);

            // Server's private key
            inputStream = getClass().getResourceAsStream("/security/server_private.key");
            String privateKeyPem = IOUtils.toString(inputStream, "UTF-8");
            PrivateKey privateKey = SecurityUtil.loadPrivateKey(privateKeyPem);

            boolean verified = SecurityUtil.verify(requestMessage.getPayload().toString(),
                    requestMessage.getSecurity().getSig(), publicKey);
            if (!verified) {
                throw new RuntimeException("Signature is invalid.");
            }

            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setHeader(new ResponseHeader(ResponseHeader.STATUS_SUCCESS));
            String payloadBase64 = Base64.getEncoder().encodeToString("{\"applicationId\":\"330301\"}".getBytes());
            String signature = SecurityUtil.sign(payloadBase64, privateKey);
            responseMessage.setPayload(payloadBase64);
            responseMessage.setSecurity(new Security(signature));
            return responseMessage;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ResponseHeader responseHeader = new ResponseHeader(ResponseHeader.STATUS_FAILURE);
            responseHeader.getErrors().add(new ErrorDTO("E400", e.getMessage()));
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setHeader(responseHeader);
            return responseMessage;
        }
    }

    /**
     * 3. Partner - Process the acknowledgement response message for the credit application
     *
     * @param responseMessage
     * @return
     */
    @PostMapping(value = "/simulate/partner/processacknowledgement",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String processAcknowledgement(@RequestBody ResponseMessage responseMessage) {
        try {
            // AutoGravity's public key
            InputStream inputStream = getClass().getResourceAsStream("/security/server_public.key");
            String publicKeyPem = IOUtils.toString(inputStream, "UTF-8");
            PublicKey publicKey = SecurityUtil.loadPublickey(publicKeyPem);

            boolean verified = SecurityUtil.verify(responseMessage.getPayload().toString(),
                    responseMessage.getSecurity().getSig(), publicKey);
            if (!verified) {
                throw new RuntimeException("Signature is invalid.");
            }

            return new String(Base64.getDecoder().decode(responseMessage.getPayload().toString()));

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return e.toString();
        }
    }

    /**
     * 4. Server - Generate a decision request message
     *
     * @param jsonPayload
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/simulate/server/generatedecision",
            consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RequestMessage generateDecisionMessage(@RequestBody String jsonPayload) throws Exception {
        try {
            // Server's private key
            InputStream inputStream = getClass().getResourceAsStream("/security/server_private.key");
            String privateKeyPem = IOUtils.toString(inputStream, "UTF-8");
            PrivateKey privateKey = SecurityUtil.loadPrivateKey(privateKeyPem);

            String payloadBase64 = Base64.getEncoder().encodeToString(jsonPayload.getBytes("UTF-8"));
            String signature = SecurityUtil.sign(payloadBase64, privateKey);

            RequestMessage requestMessage = new RequestMessage();
            requestMessage.setHeader(new RequestHeader("1"));
            requestMessage.setPayload(payloadBase64);
            requestMessage.setSecurity(new Security(signature));

            log.debug("requestMessage={}", requestMessage);
            return requestMessage;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 5. Partner - Process the decision request message
     *
     * @param requestMessage
     * @return
     */
    @PostMapping(value = "/simulate/partner/decision",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseMessage processDecisionMessage(@RequestBody RequestMessage requestMessage) {
        try {
            // AutoGravity's public key
            InputStream inputStream = getClass().getResourceAsStream("/security/server_public.key");
            String publicKeyPem = IOUtils.toString(inputStream, "UTF-8");
            PublicKey publicKey = SecurityUtil.loadPublickey(publicKeyPem);

            boolean verified = SecurityUtil.verify(requestMessage.getPayload().toString(),
                    requestMessage.getSecurity().getSig(), publicKey);
            if (!verified) {
                throw new RuntimeException("Signature is invalid.");
            }

            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setHeader(new ResponseHeader(ResponseHeader.STATUS_SUCCESS));
            return responseMessage;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ResponseHeader responseHeader = new ResponseHeader(ResponseHeader.STATUS_FAILURE);
            responseHeader.getErrors().add(new ErrorDTO("E400", e.getMessage()));
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setHeader(responseHeader);
            return responseMessage;
        }
    }

}
