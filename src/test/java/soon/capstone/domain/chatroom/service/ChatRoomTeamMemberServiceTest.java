package soon.capstone.domain.chatroom.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.chatroom.entity.ChatRoom;
import soon.capstone.domain.chatroom.entity.ChatRoomTeamMember;
import soon.capstone.domain.chatroom.repository.ChatRoomRepository;
import soon.capstone.domain.chatroom.repository.ChatRoomTeamMemberRepository;
import soon.capstone.domain.chatroom.service.dto.request.ChatRoomAddMemberServiceRequest;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ChatRoomTeamMemberServiceTest extends IntegrationTestSupport {

    @Autowired
    private ChatRoomTeamMemberService chatRoomTeamMemberService;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatRoomTeamMemberRepository chatRoomTeamMemberRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        chatRoomTeamMemberRepository.deleteAllInBatch();
        chatRoomRepository.deleteAllInBatch();
        teamMemberRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("채팅방에 팀원을 추가한다.")
    @Test
    void addMemberToChatRoom() {
        // given
        Member member = createMember("email", "nickname");
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember leader = TeamMember.createLeader(member, team);
        teamMemberRepository.save(leader);

        ChatRoom chatRoom = ChatRoom.create("title", LocalDateTime.now().plusDays(3L), team, "sid");
        chatRoomRepository.save(chatRoom);

        var request = ChatRoomAddMemberServiceRequest.builder()
            .teamId(team.getId())
            .chatRoomId(chatRoom.getId())
            .memberId(member.getId())
            .build();

        // when
        chatRoomTeamMemberService.addMemberToChatRoom(request);

        // then
        ChatRoomTeamMember chatRoomTeamMember = chatRoomTeamMemberRepository.findByChatRoomIdAndTeamMemberId(chatRoom.getId(), member.getId());
        assertThat(chatRoomTeamMember).isNotNull();

    }

    private Member createMember(String email, String nickname) {
        return Member.builder()
            .email(email)
            .nickname(nickname)
            .profileImageURL("profileImageURL")
            .build();
    }

    private Team createTeam() {
        return Team.builder()
            .organizationName("organizationName")
            .name("teamName")
            .description("teamDescription")
            .build();
    }

}