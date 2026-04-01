package com.nanqiong.cppcpatient.service;

import com.nanqiong.common.result.BusinessException;
import com.nanqiong.cppcpatient.dto.AssessmentTagRuleViolationData;
import com.nanqiong.cppcpatient.repository.CppcTagRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AssessmentTagRuleServiceTest {

    @Test
    void shouldRejectExclusiveGroupConflict() {
        CppcTagRepository repository = mock(CppcTagRepository.class);
        when(repository.findAll()).thenReturn(List.of(
                new CppcTagRepository.CppcTagEntity(111L, 110L, "MOTOR_TONE_HIGH_RIGHT", "右侧肌张力增高", 3, true, true, "MOTOR_TONE_SIDE", null, "2026-04-01.v1"),
                new CppcTagRepository.CppcTagEntity(112L, 110L, "MOTOR_TONE_HIGH_LEFT", "左侧肌张力增高", 3, true, true, "MOTOR_TONE_SIDE", null, "2026-04-01.v1")
        ));

        AssessmentTagRuleService service = new AssessmentTagRuleService(repository);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> service.validateSelectedTagIds(List.of(111L, 112L))
        );

        assertEquals(4104, ex.getCode());
        AssessmentTagRuleViolationData data = assertInstanceOf(AssessmentTagRuleViolationData.class, ex.getData());
        assertEquals(1, data.violations().size());
        assertEquals("exclusiveGroup", data.violations().get(0).type());
        assertEquals("MOTOR_TONE_SIDE", data.violations().get(0).group());
    }

    @Test
    void shouldRejectNonSelectableGroupNode() {
        CppcTagRepository repository = mock(CppcTagRepository.class);
        when(repository.findAll()).thenReturn(List.of(
                new CppcTagRepository.CppcTagEntity(110L, 100L, "MOTOR_TONE", "肌张力", 2, false, false, null, "分组节点不可直接选择", "2026-04-01.v1")
        ));

        AssessmentTagRuleService service = new AssessmentTagRuleService(repository);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> service.validateSelectedTagIds(List.of(110L))
        );

        assertEquals(4103, ex.getCode());
    }
}
