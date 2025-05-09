package soon.capstone.domain.milestone.repository;

import soon.capstone.domain.milestone.service.dto.response.MilestoneResponse;

import java.util.List;

public interface MilestoneCustomRepository {

    List<MilestoneResponse> getMilestoneWithIssuesDueTomorrow();

}