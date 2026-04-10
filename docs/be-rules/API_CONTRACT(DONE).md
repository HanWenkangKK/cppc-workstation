# API DELIVERY REPORT

> 更新时间：2026-03-31
>
> 文档目的：向架构师汇报当前后端 MVP 完成情况，并同步前端、Coze 工作流同学的后续对接内容

## 1. 当前结论

一期 MVP 所需的后端最小闭环已经完成，当前可支撑如下业务链路：

`创建患者 -> 创建评估记录 -> 获取评估树 -> 提交评估标签 -> 生成 AI 报告`

当前状态说明：

- 4 张核心表对应的数据访问代码已完成
- 5 个核心接口已完成
- 统一响应格式 `{code, msg, data}` 已接入
- 全局异常处理已接入
- AI 报告生成链路已打通
- 当前 AI 侧为后端 mock 实现，已预留 Coze 接入层 `RemoteAiService`
- 后端请求 Coze 的内部数据组装结构已落为代码 DTO，可直接对接真实 Coze API

## 2. 设计边界

- 后端作为中转站，负责数据接收、校验、持久化、调用 AI、返回结构化组件
- 前端提交结构化业务数据，不直接请求 AI
- AI 返回的数据先落库，再返回前端
- 报告内容采用 JSON 数组存储，为后续局部修改保留空间
- Coze 侧负责“生成逻辑与内容质量”，后端负责“业务结构、患者上下文、数据落库与接口输出”

## 3. 已完成范围

### 3.1 核心表

以下 4 张核心表所需的后端读写逻辑已完成：

- `patient_info`
- `assessment_record`
- `cppc_tags`
- `report_record`

### 3.2 已完成接口

以下接口均已完成后端实现：

1. `POST /api/v1/patients`
2. `POST /api/v1/assessments`
3. `GET /api/v1/cppc-tags/tree`
4. `POST /api/v1/assessments/{assessmentId}/tags`
5. `POST /api/v1/reports/generate`

### 3.3 已完成公共能力

- 统一响应包装：`{code, msg, data}`
- 全局异常处理
- Swagger 注解基础说明
- `cppc-ai-agent` 中的 Coze 请求载荷 DTO
- `RemoteAiService` 抽象层与 mock 实现

## 4. 当前后端对外接口契约

所有接口当前统一返回：

```json
{
  "code": 0,
  "msg": "success",
  "data": {}
}
```

失败示例：

```json
{
  "code": 4001,
  "msg": "assessment not found",
  "data": null
}
```

### 4.1 创建患者

接口：

`POST /api/v1/patients`

Request：

```json
{
  "name": "张三",
  "gender": "male",
  "age": 58,
  "diseaseType": "脑出血",
  "lesionSide": "右侧",
  "remark": "发病3个月，首次来诊"
}
```

Response：

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "id": 1,
    "name": "张三",
    "gender": "male",
    "age": 58,
    "diseaseType": "脑出血",
    "lesionSide": "右侧",
    "remark": "发病3个月，首次来诊",
    "createdAt": "2026-03-31 14:00:00"
  }
}
```

### 4.2 创建评估记录

接口：

`POST /api/v1/assessments`

Request：

```json
{
  "patientId": 1,
  "chiefComplaint": "右侧肢体活动差",
  "extraText": "伴足内翻，步态不稳，治疗师怀疑存在肌张力异常",
  "imageUrls": [
    "https://mock.example.com/images/1.jpg"
  ]
}
```

Response：

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "id": 1001,
    "patientId": 1,
    "chiefComplaint": "右侧肢体活动差",
    "extraText": "伴足内翻，步态不稳，治疗师怀疑存在肌张力异常",
    "imageUrls": [
      "https://mock.example.com/images/1.jpg"
    ],
    "status": "draft",
    "createdAt": "2026-03-31 14:05:00"
  }
}
```

### 4.3 获取评估树

接口：

`GET /api/v1/cppc-tags/tree`

说明：

- 当前基础契约仍以本文为准
- 当前已支持“仅末级评估项可选”“评估项互斥”等规则扩展
- 规则字段与校验约定详见同目录下的 [ASSESSMENT_RULE_EXTENSION_PROTOCOL.md](./ASSESSMENT_RULE_EXTENSION_PROTOCOL.md)

Response：

```json
{
  "code": 0,
  "msg": "success",
  "data": [
    {
      "id": 100,
      "parentId": 0,
      "bizCode": "MOTOR",
      "label": "运动功能",
      "level": 1,
      "isLeaf": false,
      "children": [
        {
          "id": 110,
          "parentId": 100,
          "bizCode": "MOTOR_TONE",
          "label": "肌张力",
          "level": 2,
          "isLeaf": false,
          "children": [
            {
              "id": 111,
              "parentId": 110,
              "bizCode": "MOTOR_TONE_HIGH_RIGHT",
              "label": "右侧肌张力增高",
              "level": 3,
              "isLeaf": true,
              "children": []
            }
          ]
        }
      ]
    },
    {
      "id": 200,
      "parentId": 0,
      "bizCode": "COGNITION",
      "label": "认知功能",
      "level": 1,
      "isLeaf": false,
      "children": []
    }
  ]
}
```

### 4.4 提交评估勾选结果

接口：

`POST /api/v1/assessments/{assessmentId}/tags`

Request：

```json
{
  "tagIds": [111, 231, 356]
}
```

Response：

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "assessmentId": 1001,
    "tagIds": [111, 231, 356],
    "tagCount": 3,
    "status": "draft"
  }
}
```

### 4.5 生成 AI 报告

接口：

`POST /api/v1/reports/generate`

Request：

```json
{
  "assessmentId": 1001,
  "reportType": "assessment_report"
}
```

Response：

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "reportId": 5001,
    "assessmentId": 1001,
    "patientId": 1,
    "reportType": "assessment_report",
    "status": "success",
    "isModified": false,
    "content": [
      {
        "id": "title_1",
        "type": "title",
        "title": "CPPC评估报告",
        "value": "CPPC评估报告"
      },
      {
        "id": "text_1",
        "type": "text",
        "title": "基本结论",
        "value": "患者存在右侧运动功能受限，结合评估项提示肌张力异常及步态稳定性不足。"
      },
      {
        "id": "table_1",
        "type": "table",
        "title": "评估摘要",
        "value": {
          "columns": ["模块", "发现"],
          "rows": [
            ["运动功能", "右侧肌张力增高"],
            ["步态", "步态不稳"]
          ]
        }
      }
    ],
    "createdAt": "2026-03-31 14:20:00"
  }
}
```

## 5. 当前 AI 接入状态

### 5.1 已完成部分

- `cppc-ai-agent` 已实现评估信息查询、标签解析、报告落库
- 已定义后端内部请求 Coze 的载荷结构
- 已抽象 `RemoteAiService`
- 当前默认实现为 mock，便于前后端先联调闭环

### 5.2 后端请求 Coze 的内部结构

这部分不是前端请求体，而是后端在 `POST /api/v1/reports/generate` 中查库后，发送给 Coze 的内部载荷。

```json
{
  "patient": {
    "id": 1,
    "name": "张三",
    "age": 58,
    "gender": "male",
    "diseaseType": "脑出血",
    "lesionSide": "右侧"
  },
  "assessment": {
    "id": 1001,
    "chiefComplaint": "右侧肢体活动差",
    "extraText": "伴足内翻，步态不稳，治疗师怀疑存在肌张力异常",
    "tagIds": [111, 231, 356]
  },
  "reportType": "assessment_report"
}
```

### 5.3 当前实现边界

- 后端已负责组装以上结构
- Coze 真实 HTTP 调用尚未接入
- 当前返回的 `content` 为 mock 结构化结果
- 后续替换为真实 Coze 调用时，不需要修改前端接口契约，只需替换 `RemoteAiService` 的实现

## 6. 交付给架构师的信息

当前后端已交付的能力如下：

- 多模块工程结构已跑通
- 患者模块、评估模块、AI 报告模块均已可编译、可测试
- 统一响应与异常处理已生效
- 多模块单仓库结构已整理完成
- 核心接口可供前端开始联调
- Coze 接口层已预留抽象，可进入真实 AI 联调阶段

希望架构师下一步明确的事项：

1. 是否确认当前 5 个接口作为一期联调冻结版本
2. 是否确认 `report_record.content` 结构作为后续报告编辑功能的数据底座
3. 是否要求二期增加报告局部修改 `PATCH` 接口
4. 是否要求提前定义统一错误码表，而不是仅使用当前 `4001/5000`
5. Coze 真实接入方式是否固定为 HTTP API，由 `cppc-ai-agent` 直接请求

## 7. 交付给前端同学的内容

前端当前可以直接对接以下接口：

1. `POST /api/v1/patients`
2. `POST /api/v1/assessments`
3. `GET /api/v1/cppc-tags/tree`
4. `POST /api/v1/assessments/{assessmentId}/tags`
5. `POST /api/v1/reports/generate`

前端需要注意：

- 所有接口统一解析 `{code, msg, data}`
- 成功判断条件为 `code === 0`
- 失败时优先展示 `msg`
- `reports/generate` 当前已经可联调，但 `content` 为 mock 结果，不代表最终 Coze 文案质量
- 报告展示区域需要支持 `content` 数组的组件化渲染

前端按当前数据结构至少需要支持以下组件类型：

- `title`
- `text`
- `table`

建议前端当前阶段的联调顺序：

1. 先完成患者创建与评估创建
2. 再接评估树展示与标签提交
3. 最后接报告生成与结构化内容展示

前端待确认项：

1. 是否需要补“获取患者详情/评估详情”接口用于页面回显
2. 是否需要报告生成中的 loading / polling 机制
3. 是否需要前端预留报告局部编辑能力

## 8. 交付给 Coze 同学的内容

Coze 同学当前需要承接的是“真实 AI 生成逻辑”，而不是重新定义业务接口。

后端已经固定：

- 前端请求入口：`POST /api/v1/reports/generate`
- 后端内部传给 Coze 的输入结构：`patient + assessment + reportType`
- 后端需要 Coze 返回结构化内容，可映射到 `content` 数组

建议 Coze 侧输出目标：

```json
[
  {
    "id": "title_1",
    "type": "title",
    "title": "CPPC评估报告",
    "value": "CPPC评估报告"
  },
  {
    "id": "text_1",
    "type": "text",
    "title": "基本结论",
    "value": "患者存在右侧运动功能受限。"
  },
  {
    "id": "table_1",
    "type": "table",
    "title": "评估摘要",
    "value": {
      "columns": ["模块", "发现"],
      "rows": [
        ["运动功能", "右侧肌张力增高"]
      ]
    }
  }
]
```

Coze 同学需要重点确认：

1. 是否能稳定返回严格 JSON，而不是自然语言大段文本
2. 是否能保证 `type` 仅使用当前约定的 `title/text/table`
3. 是否需要后端额外传入标签名称，而不仅是 `tagIds`
4. 是否需要在 Coze 工作流中增加 few-shot、知识库或固定输出模板
5. Coze 最终提供给后端的是哪个 HTTP 接口、认证方式是什么、超时时间建议是多少

建议 Coze 联调顺序：

1. 先用固定 mock 输入调通结构化 JSON 输出
2. 再接入真实 `patient + assessment + tagIds`
3. 最后再优化文案质量与知识库召回效果

## 9. 当前刻意不做的内容

- 不做用户/角色/权限相关表
- 不做历史版本表
- 不做图片文件存储表
- 不做报告局部修改 `PATCH` 接口
- 不做 PDF 文件落库存储

以上内容建议放到“评估 -> AI 报告生成”闭环稳定后再进入下一阶段。
