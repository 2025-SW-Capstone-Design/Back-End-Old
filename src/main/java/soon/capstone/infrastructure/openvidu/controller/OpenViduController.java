package soon.capstone.infrastructure.openvidu.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soon.capstone.global.anootation.AuthMemberId;
import soon.capstone.infrastructure.openvidu.controller.dto.request.OpenViduGenerateTokenRequest;
import soon.capstone.infrastructure.openvidu.controller.dto.request.OpenViduWebhookEventRequest;
import soon.capstone.infrastructure.openvidu.service.OpenViduApiService;
import soon.capstone.infrastructure.openvidu.service.dto.response.OpenViduGenerateTokenResponse;

@Slf4j
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
        @RequestBody String body,
//        @Valid @RequestBody OpenViduWebhookEventRequest request,
        @RequestHeader(value = "Authorization") String openViduToken
    ) {
        String requestUrl = servletRequest.getRequestURL().toString();
        String method = servletRequest.getMethod();
        String clientIp = servletRequest.getRemoteAddr();
        log.info("Received Request - URL: {}, Method: {}, Client IP: {}", requestUrl, method, clientIp);
        log.info("Received Body: {}", body);

//        Long chatRoomId = openViduApiService.handleWebhookEvent(request.toServiceRequest(2L, openViduToken));
        return ResponseEntity.ok(1L);
    }

}