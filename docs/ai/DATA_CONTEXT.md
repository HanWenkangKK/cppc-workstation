# 数据结构说明

## 核心模型要求

### 1. cppc_tags (评估树)
- 必须支持树形结构（id, parent_id, label, level）。
- 每个标签关联唯一的业务编码。

### 2. report_record (报告数据)
- **content (JSON)**: 核心字段，存储数组格式的结构化内容：
  `[{"id": "uuid", "type": "text/chart/table", "title": "xxx", "value": "xxx"}]`
- **is_modified**: 布尔值，标记是否被人人工微调过。
- **original_content**: 存储 AI 的初始输出，用于对比。

### 3. 数据流约束
- **输入端**：前端传给后端的是 `List<Tag_ID>` + `Patient_ID`。
- **输出端**：后端传给前端的是经过解析的 `Component_List`，以便前端渲染。

## 注意
- 数据库字段命名使用下划线（snake_case）。
- 关键业务操作（如“确认方案”）必须有时间戳。