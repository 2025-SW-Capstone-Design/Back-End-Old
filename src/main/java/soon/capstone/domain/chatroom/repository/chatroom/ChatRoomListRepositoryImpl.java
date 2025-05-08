package soon.capstone.domain.chatroom.repository.chatroom;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import soon.capstone.domain.chatroom.entity.ChatRoom;
import soon.capstone.domain.teammember.service.dto.response.TeamMemberDetailResponse;

import java.util.List;

import static com.querydsl.core.types.Projections.constructor;
import static soon.capstone.domain.chatroom.entity.QChatRoomTeamMember.chatRoomTeamMember;
import static soon.capstone.domain.member.entity.QMember.member;
import static soon.capstone.domain.teammember.entity.QTeamMember.teamMember;

@RequiredArgsConstructor
@Repository
public class ChatRoomListRepositoryImpl implements ChatRoomListRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TeamMemberDetailResponse> getTeamMembersByChatRoom(ChatRoom chatRoom) {
        return queryFactory.select(
                constructor(TeamMemberDetailResponse.class,
                    member.id.as("memberId"),
                    enumToString(teamMember.position),
                    enumToString(teamMember.role),
                    member.nickname,
                    member.profileImageURL
                ))
            .from(chatRoomTeamMember)
            .join(chatRoomTeamMember.teamMember, teamMember)
            .join(teamMember.member, member)
            .where(chatRoomTeamMember.chatRoom.eq(chatRoom))
            .fetch();
    }

    private StringExpression enumToString(Object enumValue) {
        return Expressions.stringTemplate("function('str', {0})", enumValue);
    }

}