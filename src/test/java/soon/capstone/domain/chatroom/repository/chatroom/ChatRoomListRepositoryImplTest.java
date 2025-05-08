package soon.capstone.domain.chatroom.repository.chatroom;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.chatroom.entity.ChatRoom;
import soon.capstone.domain.chatroom.entity.ChatRoomTeamMember;
import soon.capstone.domain.chatroom.repository.member.ChatRoomTeamMemberRepository;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.domain.teammember.service.dto.response.TeamMemberDetailResponse;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

class ChatRoomListRepositoryImplTest extends IntegrationTestSupport {

    @Autowired
    private ChatRoomListRepositoryImpl chatRoomListRepositoryImpl;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatRoomTeamMemberRepository chatRoomTeamMemberRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @AfterEach
    void tearDown() {
        chatRoomTeamMemberRepository.deleteAllInBatch();
        chatRoomRepository.deleteAllInBatch();
        teamMemberRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("채팅방에 속한 팀 멤버 목록을 조회한다.")
    @Test
    void getTeamMembersByChatRoom() {
        // given
        Member member1 = createMember("email1", "nickname1");
        Member member2 = createMember("email2", "nickname2");
        Member member3 = createMember("email3", "nickname3");
        memberRepository.saveAll(List.of(member1, member2, member3));

        Team team = createTeam();
        teamRepository.save(team);

        ChatRoom chatRoom = createChatRoom(team);
        chatRoomRepository.save(chatRoom);

        TeamMember leader = TeamMember.createLeader(member1, team);
        TeamMember teamMember = TeamMember.createMember(member2, team);
        TeamMember teamMember1 = TeamMember.createMember(member3, team);
        teamMemberRepository.saveAll(List.of(leader, teamMember, teamMember1));

        ChatRoomTeamMember chatRoomTeamMember1 = createChatRoomTeamMember(chatRoom, leader);
        ChatRoomTeamMember chatRoomTeamMember2 = createChatRoomTeamMember(chatRoom, teamMember);
        ChatRoomTeamMember chatRoomTeamMember3 = createChatRoomTeamMember(chatRoom, teamMember1);
        chatRoomTeamMemberRepository.saveAll(List.of(chatRoomTeamMember1, chatRoomTeamMember2, chatRoomTeamMember3));

        // when
        List<TeamMemberDetailResponse> teamMembers = chatRoomListRepositoryImpl.getTeamMembersByChatRoom(chatRoom);

        // then
        assertThat(teamMembers).hasSize(3)
            .extracting("memberId", "position", "role", "nickname", "profileImageURL")
            .containsExactlyInAnyOrder(
                tuple(member1.getId(), "NONE", "ROLE_LEADER", "nickname1", "profileImageURL"),
                tuple(member2.getId(), "NONE", "ROLE_MEMBER", "nickname2", "profileImageURL"),
                tuple(member3.getId(), "NONE", "ROLE_MEMBER", "nickname3", "profileImageURL")
            );
    }

    @DisplayName("채팅방에 속한 팀 멤버가 없을 경우 빈 목록을 반환한다.")
    @Test
    void getTeamMembersByChatRoomReturnsEmptyList() {
        // given
        Team team = createTeam();
        teamRepository.save(team);

        ChatRoom chatRoom = createChatRoom(team);
        chatRoomRepository.save(chatRoom);

        // when
        List<TeamMemberDetailResponse> teamMembers = chatRoomListRepositoryImpl.getTeamMembersByChatRoom(chatRoom);

        // then
        assertThat(teamMembers).isEmpty();
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
            .name("teamName")
            .description("teamDescription")
            .organizationName("organizationName")
            .build();
    }

    private ChatRoom createChatRoom(Team team) {
        return ChatRoom.builder()
            .title("chatRoomName")
            .reservedAt(LocalDateTime.now().plusDays(3L))
            .team(team)
            .sid("sid")
            .build();
    }

    private ChatRoomTeamMember createChatRoomTeamMember(ChatRoom chatRoom, TeamMember teamMember) {
        return ChatRoomTeamMember.builder()
            .teamMember(teamMember)
            .chatRoom(chatRoom)
            .build();
    }

}