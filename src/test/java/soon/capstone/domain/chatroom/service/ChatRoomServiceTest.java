package soon.capstone.domain.chatroom.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.domain.chatroom.entity.ChatRoom;
import soon.capstone.domain.chatroom.repository.chatroom.ChatRoomRepository;
import soon.capstone.domain.chatroom.repository.member.ChatRoomTeamMemberRepository;
import soon.capstone.domain.chatroom.service.dto.request.*;
import soon.capstone.domain.chatroom.service.dto.response.ChatRoomDetailsResponse;
import soon.capstone.domain.member.entity.Member;
import soon.capstone.domain.member.repository.MemberRepository;
import soon.capstone.domain.team.entity.Team;
import soon.capstone.domain.team.repository.TeamRepository;
import soon.capstone.domain.teammember.entity.TeamMember;
import soon.capstone.domain.teammember.repository.TeamMemberRepository;
import soon.capstone.infrastructure.openai.service.GptSummaryService;
import soon.capstone.infrastructure.redis.summary.repository.SummaryTextRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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

    @Autowired
    private SummaryTextRepository summaryTextRepository;

    @MockitoBean
    private GptSummaryService gptSummaryService;

    @AfterEach
    void tearDown() {
        chatRoomTeamMemberRepository.deleteAllInBatch();
        chatRoomRepository.deleteAllInBatch();
        teamMemberRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        summaryTextRepository.deleteAll();
    }

    @DisplayName("채팅방 생성 요청 시 동일한 SID로 이미 존재하는 경우 존재하는 채팅방 ID를 반환한다")
    @Test
    void createRoomThrowsExceptionWhenChatRoomAlreadyExists() {
        // given
        Member member = createMember("email", "nickname");
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember leader = TeamMember.createLeader(member, team);
        teamMemberRepository.save(leader);

        ChatRoom existingChatRoom = createChatRoom(team);
        chatRoomRepository.save(existingChatRoom);

        var request = createChatRoomCreateServiceRequest(team, member);

        // when
        Long chatRoomId = chatRoomService.createRoom(request);

        // then
        assertThat(chatRoomId)
            .isEqualTo(existingChatRoom.getId());
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

        ChatRoom chatRoom = createChatRoom(team);
        chatRoomRepository.save(chatRoom);

        var request = ChatRoomFinishServiceRequest.builder()
            .sid("sid")
            .teamId(team.getId())
            .memberId(member.getId())
            .build();

        // when
        Long chatRoomId = chatRoomService.finishRoom(request);

        // then
        ChatRoom updatedChatroom = chatRoomRepository.findById(chatRoomId);
        assertThat(updatedChatroom.isActive())
            .isFalse();
    }

    @DisplayName("채팅방을 다시 활성화 한다")
    @Test
    void resumeChatRoom() {
        // given
        Member member = createMember("email", "nickname");
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember leader = TeamMember.createLeader(member, team);
        teamMemberRepository.save(leader);

        ChatRoom chatRoom = createChatRoom(team);
        chatRoom.finish();
        chatRoomRepository.save(chatRoom);

        var request = ChatRoomResumeServiceRequest.builder()
            .chatRoomId(chatRoom.getId())
            .teamId(team.getId())
            .memberId(member.getId())
            .build();

        // when
        Long chatRoomId = chatRoomService.resumeRoom(request);

        // then
        ChatRoom updatedChatroom = chatRoomRepository.findById(chatRoomId);
        assertThat(updatedChatroom.isActive())
            .isTrue();
    }

    @DisplayName("채팅방 세부 정보를 조회한다")
    @Test
    void getChatRoomDetails() {
        // given
        Member member = createMember("email", "nickname");
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember leader = TeamMember.createLeader(member, team);
        teamMemberRepository.save(leader);

        ChatRoom chatRoom1 = createChatRoom(team);
        ChatRoom chatRoom2 = createChatRoom(team);
        chatRoomRepository.saveAll(List.of(chatRoom1, chatRoom2));

        var request = ChatRoomDetailsServiceRequest.builder()
            .teamId(team.getId())
            .memberId(member.getId())
            .build();

        // when
        List<ChatRoomDetailsResponse> chatRoomDetails = chatRoomService.getChatRoomDetails(request);

        // then
        assertThat(chatRoomDetails).hasSize(2)
            .extracting("id", "title")
            .containsExactlyInAnyOrder(
                tuple(chatRoom1.getId(), chatRoom1.getTitle()),
                tuple(chatRoom2.getId(), chatRoom2.getTitle())
            );
    }

    @DisplayName("채팅방의 텍스트를 중간 요약한다.")
    @Test
    void summarizeChatroom() {
        // given
        Member member = createMember("email", "nickname");
        memberRepository.save(member);

        Team team = createTeam();
        teamRepository.save(team);

        TeamMember leader = TeamMember.createLeader(member, team);
        teamMemberRepository.save(leader);

        ChatRoom chatRoom = createChatRoom(team);
        chatRoomRepository.save(chatRoom);

        String text = "long text";
        boolean isFinal = false;
        var request = ChatRoomSummarizeServiceRequest.builder()
            .chatRoomId(chatRoom.getId())
            .teamId(team.getId())
            .memberId(member.getId())
            .text(text)
            .isFinal(isFinal)
            .build();

        given(gptSummaryService.summaryToText(text, isFinal)).willReturn("Processed Summary");

        // when
        chatRoomService.summarizeChatroom(request);

        // then
        verify(gptSummaryService).summaryToText(text, isFinal);
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

    private ChatRoom createChatRoom(Team team) {
        return ChatRoom.builder()
            .title("title")
            .team(team)
            .sid("sid")
            .build();
    }

    private ChatRoomCreateServiceRequest createChatRoomCreateServiceRequest(Team team, Member member) {
        return ChatRoomCreateServiceRequest.builder()
            .title("title")
            .teamId(team.getId())
            .memberId(member.getId())
            .sid("sid")
            .build();
    }

}