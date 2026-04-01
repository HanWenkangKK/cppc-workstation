# 评估项规则扩展协议

> 更新时间：2026-04-01
>
> 文档目的：定义 `GET /api/v1/cppc-tags/tree` 的规则扩展字段，以及 `POST /api/v1/assessments/{assessmentId}/tags` 的规则校验约定，支撑前后端对“哪些评估项可选、哪些互斥、哪些必须由后端兜底校验”的统一实现。

## 1. 适用范围

本协议适用于以下两个接口：

1. `GET /api/v1/cppc-tags/tree`
2. `POST /api/v1/assessments/{assessmentId}/tags`

当前协议仅扩展“评估项选择规则”，不改变患者、评估记录、报告生成等其他接口的基础契约。

## 2. 设计原则

### 2.1 后端是规则真相来源

- 哪些节点可选
- 哪些节点互斥
- 哪些组合非法
- 哪些规则后续会演进

以上都应以**后端返回的规则字段**为准。

### 2.2 前端负责体验，后端负责最终校验

前端应该：

- 根据规则字段决定是否展示勾选框
- 在可感知的场景中做即时拦截或自动取消互斥项
- 在界面上展示清晰的错误提示

后端必须：

- 对提交的 `tagIds` 再次校验
- 防止绕过前端直接提交非法组合
- 在校验失败时返回可识别的错误码和错误信息

### 2.3 保持向后兼容

本协议采用“新增可选字段”的方式扩展 `cppc-tags/tree` 返回结构。

如果前端未识别新字段，应至少保证：

- `isLeaf === true` 的节点仍可作为默认可选项
- 非叶子节点默认不可提交
- 后端最终校验仍然有效

## 3. 节点基础结构

当前基础节点结构保持不变：

```json
{
  "id": 111,
  "parentId": 110,
  "bizCode": "MOTOR_TONE_HIGH_RIGHT",
  "label": "右侧肌张力增高",
  "level": 3,
  "isLeaf": true,
  "children": []
}
```

## 4. 节点规则扩展字段

建议在每个节点上新增以下可选字段：

### 4.1 `selectable`

类型：`boolean`

含义：

- `true`：该节点允许被用户勾选
- `false`：该节点不允许被用户勾选

默认约定：

- 如果后端未返回该字段，前端默认 `isLeaf === true` 的节点可选，其他节点不可选

推荐用途：

- 分组节点不允许勾选
- 某些叶子节点虽然存在，但当前版本不允许选择

示例：

```json
{
  "id": 110,
  "label": "肌张力",
  "isLeaf": false,
  "selectable": false
}
```

### 4.2 `exclusiveGroup`

类型：`string | null`

含义：

- 非空时，表示该节点属于某个“互斥组”
- 同一个 `exclusiveGroup` 下，最多只允许选中一个节点

推荐用途：

- 左 / 右 / 双侧三选一
- 轻 / 中 / 重三选一
- 某一条评估路径下的单选结论

示例：

```json
{
  "id": 111,
  "label": "右侧肌张力增高",
  "isLeaf": true,
  "selectable": true,
  "exclusiveGroup": "MOTOR_TONE_SIDE"
}
```

```json
{
  "id": 112,
  "label": "左侧肌张力增高",
  "isLeaf": true,
  "selectable": true,
  "exclusiveGroup": "MOTOR_TONE_SIDE"
}
```

### 4.3 `disabledReason`

类型：`string | null`

含义：

- 当节点不可选时，用于向前端解释原因

推荐用途：

- 提示“当前版本暂不开放”
- 提示“该项仅资深治疗师可选”
- 提示“该项需依赖前置评估结果”

示例：

```json
{
  "id": 210,
  "label": "高级认知量表异常",
  "isLeaf": true,
  "selectable": false,
  "disabledReason": "当前版本暂不开放该评估项"
}
```

### 4.4 `ruleVersion`

类型：`string | null`

含义：

- 标识当前节点使用的规则版本

推荐用途：

- 前后端排查“规则已更新但页面缓存旧数据”的问题
- 审计某次提交使用的是哪一版规则

示例：

```json
{
  "id": 111,
  "label": "右侧肌张力增高",
  "ruleVersion": "2026-04-01.v1"
}
```

## 5. 推荐返回结构

建议后端在 `GET /api/v1/cppc-tags/tree` 中返回如下结构：

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
      "selectable": false,
      "children": [
        {
          "id": 110,
          "parentId": 100,
          "bizCode": "MOTOR_TONE",
          "label": "肌张力",
          "level": 2,
          "isLeaf": false,
          "selectable": false,
          "children": [
            {
              "id": 111,
              "parentId": 110,
              "bizCode": "MOTOR_TONE_HIGH_RIGHT",
              "label": "右侧肌张力增高",
              "level": 3,
              "isLeaf": true,
              "selectable": true,
              "exclusiveGroup": "MOTOR_TONE_SIDE",
              "ruleVersion": "2026-04-01.v1",
              "children": []
            },
            {
              "id": 112,
              "parentId": 110,
              "bizCode": "MOTOR_TONE_HIGH_LEFT",
              "label": "左侧肌张力增高",
              "level": 3,
              "isLeaf": true,
              "selectable": true,
              "exclusiveGroup": "MOTOR_TONE_SIDE",
              "ruleVersion": "2026-04-01.v1",
              "children": []
            }
          ]
        }
      ]
    }
  ]
}
```

## 6. 前端执行约定

前端收到规则后，建议按以下方式处理：

### 6.1 勾选框展示

- `selectable === true`：展示勾选框
- `selectable === false`：隐藏或禁用勾选框
- 字段缺失：按“叶子节点可选、非叶子节点不可选”降级

### 6.2 互斥组处理

- 当用户勾选某个带 `exclusiveGroup` 的节点时
- 前端应自动取消同组其他已勾选节点
- 若前端未实现自动取消，后端仍必须拦截非法提交

### 6.3 提交数据结构

`POST /api/v1/assessments/{assessmentId}/tags` 的请求体暂不变化：

```json
{
  "tagIds": [111, 231, 356]
}
```

前端不需要提交规则本身，只提交最终选中的 `tagIds`。

## 7. 后端校验约定

后端在接收 `tagIds` 时，必须至少校验以下规则：

1. `tagIds` 中的节点必须真实存在
2. `tagIds` 中的节点必须允许选择
3. `tagIds` 中不得包含非叶子节点，除非后端明确允许该节点 `selectable=true`
4. 同一 `exclusiveGroup` 内不得同时出现两个及以上节点
5. 当树规则更新时，后端应以最新规则重新校验，而不是信任前端缓存

## 8. 推荐错误码

当前项目尚未冻结完整错误码表，以下为“评估项规则扩展”建议保留的错误码：

| 错误码 | 含义 |
| --- | --- |
| `4101` | 评估项不存在 |
| `4102` | 评估项不可选 |
| `4103` | 提交了不允许选择的分组节点 |
| `4104` | 互斥组冲突 |
| `4105` | 评估项规则定义异常 |

## 9. 推荐失败返回

建议后端在规则校验失败时返回如下结构：

```json
{
  "code": 4104,
  "msg": "exclusive rule violated",
  "data": {
    "violations": [
      {
        "type": "exclusiveGroup",
        "group": "MOTOR_TONE_SIDE",
        "tagIds": [111, 112],
        "message": "“左侧肌张力增高”和“右侧肌张力增高”不能同时选择"
      }
    ]
  }
}
```

其中：

- `msg` 用于通用提示
- `data.violations` 用于前端精细提示或调试排查

## 10. 版本建议

建议分两期推进：

### 10.1 一期最小可用版

后端至少补齐：

- `selectable`
- `exclusiveGroup`

前端至少支持：

- 仅末级评估项可选
- 同组互斥自动取消
- 提交失败展示后端返回的 `msg`

### 10.2 二期增强版

视业务复杂度增加：

- `disabledReason`
- `ruleVersion`
- 更细粒度的 `violations`

## 11. 最终结论

本项目中，“评估项可选性”和“互斥关系”的**规则定义权**应在后端；
“勾选框展示、即时交互反馈、错误提示”的**体验实现权**应在前端；
“非法组合兜底拦截”的**最终裁决权**必须仍在后端。

换句话说：

- 前端可以优化体验
- 后端必须掌握规则真相
- 前后端都要参与，但以后端规则为准
