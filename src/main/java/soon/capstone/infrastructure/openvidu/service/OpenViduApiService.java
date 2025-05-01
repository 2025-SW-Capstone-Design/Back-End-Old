package soon.capstone.infrastructure.openvidu.service;

import io.livekit.server.AccessToken;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import soon.capstone.infrastructure.openvidu.service.dto.OpenViduGenerateTokenServiceRequest;
import soon.capstone.infrastructure.openvidu.service.dto.response.OpenViduGenerateTokenResponse;

@RequiredArgsConstructor
@Service
public class OpenViduApiService {

    @Value("${livekit.api.key}")
    private String apiKey;

    @Value("${livekit.api.secret}")
    private String apiSecret;

    public OpenViduGenerateTokenResponse generateOpenViduToken(OpenViduGenerateTokenServiceRequest request) {
        AccessToken token = new AccessToken(apiKey, apiSecret);
        token.setName(request.roomName());
        token.setIdentity(request.memberName());
        token.addGrants(new RoomJoin(true), new RoomName(request.roomName()));

        String jwt = token.toJwt();
        return OpenViduGenerateTokenResponse.builder()
            .jwt(jwt)
            .roomName(request.roomName())
            .memberName(request.memberName())
            .build();
    }

}