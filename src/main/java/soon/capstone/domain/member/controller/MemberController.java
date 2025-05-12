package soon.capstone.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import soon.capstone.domain.member.service.MemberService;
import soon.capstone.domain.member.service.dto.response.MemberDetailResponse;
import soon.capstone.global.anootation.AuthMemberId;

@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<MemberDetailResponse> getMemberDetail(@AuthMemberId Long memberId) {
        MemberDetailResponse response = memberService.getMemberDetail(memberId);
        return ResponseEntity.ok(response);
    }

}