package com.nanqiong.cppcpatient.service;

import com.nanqiong.cppcpatient.dto.CppcTagNodeResponse;
import com.nanqiong.cppcpatient.repository.CppcTagRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CppcTagService {

    private final AssessmentTagRuleService assessmentTagRuleService;

    public CppcTagService(AssessmentTagRuleService assessmentTagRuleService) {
        this.assessmentTagRuleService = assessmentTagRuleService;
    }

    public List<CppcTagNodeResponse> getTree() {
        List<CppcTagRepository.CppcTagEntity> tags = assessmentTagRuleService.getAllTags();
        Map<Long, CppcTagNodeResponse> nodeMap = new LinkedHashMap<>();
        List<CppcTagNodeResponse> roots = new ArrayList<>();

        for (CppcTagRepository.CppcTagEntity tag : tags) {
            AssessmentTagRuleService.TagRule rule = assessmentTagRuleService.resolveRule(tag);
            nodeMap.put(tag.id(), new CppcTagNodeResponse(
                    rule.id(),
                    rule.parentId(),
                    rule.bizCode(),
                    rule.label(),
                    rule.level(),
                    rule.isLeaf(),
                    rule.selectable(),
                    rule.exclusiveGroup(),
                    rule.disabledReason(),
                    rule.ruleVersion()
            ));
        }

        for (CppcTagRepository.CppcTagEntity tag : tags) {
            CppcTagNodeResponse current = nodeMap.get(tag.id());
            if (tag.parentId() == 0) {
                roots.add(current);
                continue;
            }
            CppcTagNodeResponse parent = nodeMap.get(tag.parentId());
            if (parent != null) {
                parent.getChildren().add(current);
            }
        }

        return roots;
    }
}
