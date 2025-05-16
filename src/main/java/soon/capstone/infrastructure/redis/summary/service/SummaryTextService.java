package soon.capstone.infrastructure.redis.summary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import soon.capstone.infrastructure.redis.summary.entity.SummaryText;
import soon.capstone.infrastructure.redis.summary.repository.SummaryTextRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SummaryTextService {

    private final SummaryTextRepository summaryTextRepository;

    public List<SummaryText> findAllByChatRoomId(Long chatRoomId) {
        return summaryTextRepository.findAllByChatRoomId(chatRoomId);
    }

    public void resetIndex(Long chatRoomId) {
        summaryTextRepository.resetIndex(chatRoomId);
    }

}