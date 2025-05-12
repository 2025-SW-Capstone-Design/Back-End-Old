package soon.capstone.infrastructure.redis.summary.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import soon.capstone.IntegrationTestSupport;
import soon.capstone.infrastructure.redis.summary.entity.SummaryText;

import static org.assertj.core.api.Assertions.assertThat;

class SummaryTextRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private SummaryTextRepository summaryTextRepository;

    @AfterEach
    void tearDown() {
        summaryTextRepository.deleteAll();
    }

    @DisplayName("summaryText를 저장 할 경우 index는 1부터 시작이다")
    @Test
    void saveSummaryTextStartsWithIndexOne() {
        // given
        Long chatRoomId = 1L;
        String summary = "Test summary";

        // when
        SummaryText savedSummaryText = summaryTextRepository.save(chatRoomId, summary);

        // then
        assertThat(savedSummaryText).isNotNull();
        assertThat(savedSummaryText.getIndex()).isEqualTo(1);
        assertThat(savedSummaryText.getId()).isEqualTo("1:1");
    }

    @DisplayName("같은 chatRoomId로 저장 시 index가 증가한다")
    @Test
    void saveSummaryTextIncrementsIndex() {
        // given
        Long chatRoomId = 1L;
        String summary1 = "First summary";
        String summary2 = "Second summary";

        // when
        SummaryText firstSaved = summaryTextRepository.save(chatRoomId, summary1);
        SummaryText secondSaved = summaryTextRepository.save(chatRoomId, summary2);

        // then
        assertThat(firstSaved.getIndex()).isEqualTo(1);
        assertThat(firstSaved.getId()).isEqualTo("1:1");

        assertThat(secondSaved.getIndex()).isEqualTo(2);
        assertThat(secondSaved.getId()).isEqualTo("1:2");
    }

}