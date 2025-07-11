package soon.capstone.infrastructure.openvidu.controller;

import io.livekit.server.EgressServiceClient;
import jakarta.validation.Valid;
import livekit.LivekitEgress;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import retrofit2.Call;
import retrofit2.Response;
import soon.capstone.global.anootation.AuthMemberId;
import soon.capstone.infrastructure.openvidu.controller.dto.request.OpenViduGenerateTokenRequest;
import soon.capstone.infrastructure.openvidu.controller.dto.request.OpenViduWebhookEventRequest;
import soon.capstone.infrastructure.openvidu.service.OpenViduApiService;
import soon.capstone.infrastructure.openvidu.service.dto.response.OpenViduGenerateTokenResponse;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/v1/open-vidu")
@RestController
public class OpenViduController {

    private final OpenViduApiService openViduApiService;

    @Value("${cloud.aws.s3.bucket}")
    private String s3Bucket;
    @Value("${cloud.aws.credentials.accessKey}")
    private String s3AccessKey;
    @Value("${cloud.aws.credentials.secretKey}")
    private String s3SecretKey;
    @Value("${cloud.aws.region.static}")
    private String s3Region;

    @Value("${livekit.api.url}")
    private String LIVEKIT_URL;
    @Value("${livekit.api.key}")
    private String LIVEKIT_API_KEY;
    @Value("${livekit.api.secret}")
    private String LIVEKIT_API_SECRET;

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

    @PostMapping(value = "/startCompositeEgress")
    public ResponseEntity<String> startCompositeEgress(@RequestBody Map<String, String> params)
        throws IOException {
        String roomName = params.get("roomName");
        String teamId = params.get("teamId");
        String memberId = params.get("memberId");
        if (roomName == null) {
            return ResponseEntity.badRequest().body("roomName is required");
        }

        EgressServiceClient egressClient = EgressServiceClient.createClient(LIVEKIT_URL, LIVEKIT_API_KEY, LIVEKIT_API_SECRET);

        LivekitEgress.EncodedFileOutput fileOutput = LivekitEgress.EncodedFileOutput.newBuilder()
            .setFilepath(roomName + memberId + teamId + ".mp4")
            .setS3(LivekitEgress.S3Upload.newBuilder()
                .setBucket(s3Bucket)
                .setAccessKey(s3AccessKey)
                .setSecret(s3SecretKey)
                .setRegion(s3Region)
                .setForcePathStyle(true)
                .build())
            .build();

        Call<LivekitEgress.EgressInfo> call = egressClient.startRoomCompositeEgress(
            roomName,
            fileOutput,
            "speaker",
            LivekitEgress.EncodingOptionsPreset.H264_720P_30,
            null,
            true,  // audioOnly: true
            false,
            ""
        );

        Response<LivekitEgress.EgressInfo> response = call.execute();
        if (!response.isSuccessful()) {
            return ResponseEntity.status(response.code())
                .body("Egress start failed: " + response.errorBody().string());
        }

        String egressId = response.body().getEgressId();
        return ResponseEntity.ok("Egress started, ID: " + egressId);
    }

}