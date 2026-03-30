package com.nanqiong.cppcpatient.controller;

import com.nanqiong.cppcpatient.dto.CppcTagNodeResponse;
import com.nanqiong.cppcpatient.service.CppcTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "评估标签")
@RestController
@RequestMapping("/api/v1/cppc-tags")
public class CppcTagController {

    private final CppcTagService cppcTagService;

    public CppcTagController(CppcTagService cppcTagService) {
        this.cppcTagService = cppcTagService;
    }

    @Operation(summary = "获取评估树", description = "获取 CPPC 评估标签树")
    @GetMapping("/tree")
    public List<CppcTagNodeResponse> tree() {
        return cppcTagService.getTree();
    }
}
