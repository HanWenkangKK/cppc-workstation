# API CONTRACT

> 更新时间：2026-03-31
>
> 目标：支持一期 MVP 的最小闭环“患者评估 -> AI 生成报告”
>
> 范围：仅覆盖 4 张核心表和 5 个核心接口，不包含用户管理、权限系统、PDF 导出、语音服务、图片上传服务

## 1. 设计边界

- 后端作为中转站，负责数据接收、校验、持久化、调用 AI、返回结构化组件
- 前端提交的是结构化业务数据，不直接请求 AI
- AI 返回的数据必须先落库，再返回前端
- 报告内容采用 JSON 数组存储，为后续局部修改保留空间

## 2. 4 张核心表 SQL 初稿

以下为 postgres 初稿。

### 2.1 `patient_info`

已完成

### 2.2 `assessment_record`

已完成

### 2.3 `cppc_tags`

已完成
### 2.4 `report_record`

已完成

## 3. 表关系说明

- `patient_info` 1 -> N `assessment_record`
- `assessment_record` 1 -> N `report_record`
- `assessment_record.tag_ids_json` 存储该次评估提交给 AI 的标签快照
- `cppc_tags` 由后端维护，提供前端评估树展示，不依赖 AI 动态生成

## 4. 统一响应格式

所有接口统一返回：

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

## 5. 5 个接口完整 JSON 契约

## 5.1 创建患者

### 接口

`POST /api/v1/patients`

### Request

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

### Response

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

## 5.2 创建评估记录

### 接口

`POST /api/v1/assessments`

### Request

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

### Response

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

## 5.3 获取评估树

### 接口

`GET /api/v1/cppc-tags/tree`

### Response

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

## 5.4 提交评估勾选结果

### 接口

`POST /api/v1/assessments/{assessmentId}/tags`

### Request

```json
{
  "tagIds": [111, 231, 356]
}
```

### Response

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

## 5.5 生成 AI 报告

### 接口

`POST /api/v1/reports/generate`

### Request

```json
{
  "assessmentId": 1001,
  "reportType": "assessment_report"
}
```

### 后端内部调用 AI 的建议输入

这部分不是给前端的接口，而是你后端请求 Coze 时建议组装的结构。

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

### Response

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

## 6. 服务端处理建议

为了尽快跑通最小闭环，建议今天下午按这个顺序实现：

1. `POST /api/v1/patients`
2. `POST /api/v1/assessments`
3. `GET /api/v1/cppc-tags/tree`
4. `POST /api/v1/assessments/{assessmentId}/tags`
5. `POST /api/v1/reports/generate`

其中第 5 步第一版可以先不真实接 Coze，先返回 mock 的结构化 `content`，确保前后端闭环先跑通。

## 7. 当前刻意不做的内容

- 不做用户/角色/权限相关表
- 不做历史版本表
- 不做图片文件存储表
- 不做报告局部修改 `PATCH` 接口
- 不做 PDF 文件落库存储

这些内容放在“评估 -> AI 报告生成”闭环跑通之后再补。
