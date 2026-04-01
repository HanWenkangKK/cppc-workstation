package com.nanqiong.cppcpatient.service;

import com.nanqiong.common.result.BusinessException;
import com.nanqiong.cppcpatient.dto.AssessmentTagRuleViolation;
import com.nanqiong.cppcpatient.dto.AssessmentTagRuleViolationData;
import com.nanqiong.cppcpatient.repository.CppcTagRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AssessmentTagRuleService {

    private final CppcTagRepository cppcTagRepository;

    public AssessmentTagRuleService(CppcTagRepository cppcTagRepository) {
        this.cppcTagRepository = cppcTagRepository;
    }

    public List<CppcTagRepository.CppcTagEntity> getAllTags() {
        return cppcTagRepository.findAll();
    }

    public void validateSelectedTagIds(List<Long> rawTagIds) {
        List<Long> tagIds = normalize(rawTagIds);
        Map<Long, TagRule> ruleMap = loadRuleMap();

        for (Long tagId : tagIds) {
            TagRule rule = ruleMap.get(tagId);
            if (rule == null) {
                throw new BusinessException(4101, "assessment tag not found");
            }
            if (!rule.selectable()) {
                if (Boolean.TRUE.equals(rule.isLeaf())) {
                    throw new BusinessException(4102, "assessment tag is not selectable");
                }
                throw new BusinessException(4103, "group node is not selectable");
            }
        }

        Map<String, List<TagRule>> byExclusiveGroup = tagIds.stream()
                .map(ruleMap::get)
                .filter(rule -> rule != null && hasText(rule.exclusiveGroup()))
                .collect(Collectors.groupingBy(
                        TagRule::exclusiveGroup,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<AssessmentTagRuleViolation> violations = new ArrayList<>();
        for (Map.Entry<String, List<TagRule>> entry : byExclusiveGroup.entrySet()) {
            List<TagRule> rules = entry.getValue();
            if (rules.size() <= 1) {
                continue;
            }
            violations.add(new AssessmentTagRuleViolation(
                    "exclusiveGroup",
                    entry.getKey(),
                    rules.stream().map(TagRule::id).toList(),
                    joinLabels(rules) + "不能同时选择"
            ));
        }

        if (!violations.isEmpty()) {
            throw new BusinessException(
                    4104,
                    "exclusive rule violated",
                    new AssessmentTagRuleViolationData(violations)
            );
        }
    }

    public TagRule resolveRule(CppcTagRepository.CppcTagEntity entity) {
        return new TagRule(
                entity.id(),
                entity.parentId(),
                entity.bizCode(),
                entity.label(),
                entity.level(),
                entity.isLeaf(),
                Boolean.TRUE.equals(entity.selectable()),
                entity.exclusiveGroup(),
                entity.disabledReason(),
                entity.ruleVersion()
        );
    }

    private Map<Long, TagRule> loadRuleMap() {
        Map<Long, TagRule> map = new LinkedHashMap<>();
        for (CppcTagRepository.CppcTagEntity entity : cppcTagRepository.findAll()) {
            TagRule rule = resolveRule(entity);
            map.put(rule.id(), rule);
        }
        return map;
    }

    private List<Long> normalize(List<Long> rawTagIds) {
        if (rawTagIds == null) {
            return Collections.emptyList();
        }
        Set<Long> distinct = new LinkedHashSet<>();
        for (Long tagId : rawTagIds) {
            if (tagId != null) {
                distinct.add(tagId);
            }
        }
        return new ArrayList<>(distinct);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String joinLabels(List<TagRule> rules) {
        return rules.stream()
                .map(rule -> "“" + rule.label() + "”")
                .collect(Collectors.joining("和"));
    }

    public record TagRule(
            Long id,
            Long parentId,
            String bizCode,
            String label,
            Integer level,
            Boolean isLeaf,
            boolean selectable,
            String exclusiveGroup,
            String disabledReason,
            String ruleVersion
    ) {
    }
}
