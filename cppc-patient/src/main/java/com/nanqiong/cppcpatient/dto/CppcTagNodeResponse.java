package com.nanqiong.cppcpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema(name = "CppcTagNodeResponse", description = "评估树节点")
public class CppcTagNodeResponse {

    @Schema(description = "节点ID", example = "100")
    private Long id;
    @Schema(description = "父节点ID", example = "0")
    private Long parentId;
    @Schema(description = "业务编码", example = "MOTOR")
    private String bizCode;
    @Schema(description = "标签名称", example = "运动功能")
    private String label;
    @Schema(description = "层级", example = "1")
    private Integer level;
    @Schema(description = "是否叶子节点", example = "false")
    private Boolean isLeaf;
    @Schema(description = "是否允许选择", example = "false")
    private Boolean selectable;
    @Schema(description = "互斥组编码", example = "MOTOR_TONE_SIDE")
    private String exclusiveGroup;
    @Schema(description = "不可选原因")
    private String disabledReason;
    @Schema(description = "规则版本", example = "2026-04-01.v1")
    private String ruleVersion;
    @Schema(description = "子节点列表")
    private List<CppcTagNodeResponse> children = new ArrayList<>();

    public CppcTagNodeResponse() {
    }

    public CppcTagNodeResponse(
            Long id,
            Long parentId,
            String bizCode,
            String label,
            Integer level,
            Boolean isLeaf,
            Boolean selectable,
            String exclusiveGroup,
            String disabledReason,
            String ruleVersion
    ) {
        this.id = id;
        this.parentId = parentId;
        this.bizCode = bizCode;
        this.label = label;
        this.level = level;
        this.isLeaf = isLeaf;
        this.selectable = selectable;
        this.exclusiveGroup = exclusiveGroup;
        this.disabledReason = disabledReason;
        this.ruleVersion = ruleVersion;
    }

    public Long getId() {
        return id;
    }

    public Long getParentId() {
        return parentId;
    }

    public String getBizCode() {
        return bizCode;
    }

    public String getLabel() {
        return label;
    }

    public Integer getLevel() {
        return level;
    }

    public Boolean getIsLeaf() {
        return isLeaf;
    }

    public Boolean getSelectable() {
        return selectable;
    }

    public String getExclusiveGroup() {
        return exclusiveGroup;
    }

    public String getDisabledReason() {
        return disabledReason;
    }

    public String getRuleVersion() {
        return ruleVersion;
    }

    public List<CppcTagNodeResponse> getChildren() {
        return children;
    }
}
