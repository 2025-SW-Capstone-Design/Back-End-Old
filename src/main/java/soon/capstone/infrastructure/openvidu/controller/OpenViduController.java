package soon.capstone.infrastructure.openvidu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soon.capstone.global.anootation.AuthMemberId;
import soon.capstone.infrastructure.openvidu.controller.dto.request.OpenViduGenerateTokenRequest;
import soon.capstone.infrastructure.openvidu.controller.dto.request.OpenViduWebhookEventRequest;
import soon.capstone.infrastructure.openvidu.service.OpenViduApiService;
import soon.capstone.infrastructure.openvidu.service.dto.response.OpenViduGenerateTokenResponse;

@RequiredArgsConstructor
@RequestMapping("/api/v1/open-vidu")
@RestController
public class OpenViduController {

    private final OpenViduApiService openViduApiService;

    @PostMapping("/{teamId}/token")
    public ResponseEntity<OpenViduGenerateTokenResponse> createToken(
        @Valid @RequestBody OpenViduGenerateTokenRequest request,
        @AuthMemberId Long memberId,
        @PathVariable Long teamId
    ) {
        OpenViduGenerateTokenResponse response = openViduApiService.generateOpenViduToken(request.toServiceRequest(memberId, teamId));
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/webhook", consumes = "application/webhook+json")
    public ResponseEntity<Void> receiveWebhook(
        @RequestBody String body,
        @RequestHeader(value = "Authorization") String openViduToken
    ) {
        openViduApiService.handleWebhookEvent(OpenViduWebhookEventRequest.toServiceRequest(body, openViduToken));
        return ResponseEntity.ok().build();
    }

}