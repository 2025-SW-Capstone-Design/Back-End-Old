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
import soon.capstone.domain.chatroom.service.dto.request.ChatRoomCreateServiceRequest;
import soon.capstone.domain.chatroom.service.dto.request.ChatRoomFinishServiceRequest;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.global.exception.common.InvalidRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChatRoomServiceTest extends IntegrationTestSupport {

    @Autowired
    private ChatRoomService chatRoomService;

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

    @DisplayName("채팅방을 생성한다")
    @Test
    void createChatRoom() {
        // given
        Member member = createMember("email", "nickname");
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember leader = TeamMember.createLeader(member, team);
        teamMemberRepository.save(leader);

        var request = createChatRoomCreateServiceRequest(team, member, LocalDateTime.now().plusDays(3));

        // when
        Long savedChatRoomId = chatRoomService.createRoom(request);

        // then
        ChatRoom chatRoom = chatRoomRepository.findById(savedChatRoomId);
        assertThat(chatRoom).isNotNull();
    }

    @DisplayName("채팅방 생성을 요청한 멤버는 생성된 채팅방에 추가된다.")
    @Test
    void createChatRoomWithAddedRequestMember() {
        // given
        Member member = createMember("email", "nickname");
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember leader = TeamMember.createLeader(member, team);
        teamMemberRepository.save(leader);

        var request = createChatRoomCreateServiceRequest(team, member, LocalDateTime.now().plusDays(3));

        // when
        Long savedChatRoomId = chatRoomService.createRoom(request);

        // then
        ChatRoomTeamMember chatRoomTeamMember = chatRoomTeamMemberRepository.findByChatRoomIdAndTeamMemberId(savedChatRoomId, leader.getId());
        assertThat(chatRoomTeamMember).isNotNull();
    }

    @DisplayName("채팅방 생성 시 예약시간은 현재보다 미래여야 한다.")
    @Test
    void createChatRoomWithoutReservedAt() {
        // given
        Member member = createMember("email", "nickname");
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember leader = TeamMember.createLeader(member, team);
        teamMemberRepository.save(leader);

        var request = createChatRoomCreateServiceRequest(team, member, LocalDateTime.now().minusDays(3));

        // expected
        assertThatThrownBy(() -> chatRoomService.createRoom(request))
            .isInstanceOf(InvalidRequest.class)
            .hasMessageContaining("잘못된 요청입니다.");
    }

    @DisplayName("채팅방을 종료한다")
    @Test
    void finishChatRoom() {
        // given
        Member member = createMember("email", "nickname");
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember leader = TeamMember.createLeader(member, team);
        teamMemberRepository.save(leader);

        var request = createChatRoomCreateServiceRequest(team, member, LocalDateTime.now().plusDays(3));
        Long savedChatRoomId = chatRoomService.createRoom(request);

        // when
        chatRoomService.finishRoom(ChatRoomFinishServiceRequest.builder()
            .sid("sid")
            .teamId(team.getId())
            .memberId(member.getId())
            .build());

        // then
        ChatRoom chatRoom = chatRoomRepository.findById(savedChatRoomId);
        assertThat(chatRoom.isActive()).isFalse();
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

    private ChatRoomCreateServiceRequest createChatRoomCreateServiceRequest(Team team, Member member, LocalDateTime reservedAt) {
        return ChatRoomCreateServiceRequest.builder()
            .title("title")
            .reservedAt(reservedAt)
            .teamId(team.getId())
            .memberId(member.getId())
            .sid("sid")
            .build();
    }

}