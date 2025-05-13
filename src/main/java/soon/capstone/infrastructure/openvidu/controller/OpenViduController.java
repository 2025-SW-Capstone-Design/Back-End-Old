package soon.capstone.infrastructure.openvidu.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.ContentCachingRequestWrapper;
import soon.capstone.global.anootation.AuthMemberId;
import soon.capstone.infrastructure.openvidu.controller.dto.request.OpenViduGenerateTokenRequest;
import soon.capstone.infrastructure.openvidu.controller.dto.request.OpenViduWebhookEventRequest;
import soon.capstone.infrastructure.openvidu.service.OpenViduApiService;
import soon.capstone.infrastructure.openvidu.service.dto.response.OpenViduGenerateTokenResponse;

import java.io.IOException;

@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/api/v1/open-vidu")
@RestController
public class OpenViduController {

    private final OpenViduApiService openViduApiService;

    @PostMapping("/token")
    public ResponseEntity<OpenViduGenerateTokenResponse> createToken(
        @Valid @RequestBody OpenViduGenerateTokenRequest request,
        @AuthMemberId Long memberId
    ) {
        OpenViduGenerateTokenResponse response = openViduApiService.generateOpenViduToken(request.toServiceRequest(memberId));
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/webhook", consumes = "application/webhook+json")
    public ResponseEntity<Long> receiveWebhook(
        HttpServletRequest servletRequest,
        @Valid @RequestBody OpenViduWebhookEventRequest request,
        @RequestHeader(value = "Authorization") String openViduToken
    ) throws IOException {
        ContentCachingRequestWrapper wrapper = new ContentCachingRequestWrapper(servletRequest);
        wrapper.getInputStream().readAllBytes();
        String requestBody = new String(wrapper.getContentAsByteArray());
        log.info("requestBody: {}", requestBody);

        Long chatRoomId = openViduApiService.handleWebhookEvent(request.toServiceRequest(openViduToken));
        return ResponseEntity.ok(chatRoomId);
    }

}