package soon.capstone.infrastructure.openvidu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soon.capstone.global.anootation.AuthMemberId;
import soon.capstone.infrastructure.openvidu.controller.dto.OpenViduGenerateTokenRequest;
import soon.capstone.infrastructure.openvidu.controller.dto.request.OpenViduWebhookEventRequest;
import soon.capstone.infrastructure.openvidu.service.OpenViduApiService;
import soon.capstone.infrastructure.openvidu.service.dto.response.OpenViduGenerateTokenResponse;


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

    @PostMapping(value = "/webhook/{teamId}", consumes = "application/webhook+json")
    public ResponseEntity<Void> receiveWebhook(
        @Valid @RequestBody OpenViduWebhookEventRequest request,
        @RequestHeader(value = "X-OpenVidu-Token") String openViduToken,
        @AuthMemberId Long memberId,
        @PathVariable Long teamId
    ) {
        openViduApiService.handleWebhookEvent(request.toServiceRequest(memberId, teamId, openViduToken));
        return ResponseEntity.ok().build();
    }

}