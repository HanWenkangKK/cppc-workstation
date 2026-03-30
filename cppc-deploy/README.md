# Test Environment Deployment

本目录提供单台 Linux 云服务器上的最小测试环境部署方案，目标是尽快拉起后端，供前端联调。

## 1. 部署内容

当前 `docker compose` 会启动以下服务：

- `postgres`
- `cppc-patient`
- `cppc-ai-agent`
- `cppc-gateway`
- `nginx`

访问入口：

- 网关统一入口：`http://<server-ip>/`
- 患者服务代理前缀：`/patient`
- AI 服务代理前缀：`/ai`
- Swagger 聚合地址：`http://<server-ip>/swagger-ui.html`

## 2. 服务器准备

建议服务器最低配置：

- 2 vCPU
- 4 GB RAM
- 40 GB 磁盘

需要提前准备：

1. 安装 Docker Engine
2. 安装 Docker Compose Plugin
3. 安全组放通 `80` 端口
4. 使用 `git clone` 拉取本仓库代码到服务器

## 3. 启动步骤

在服务器执行：

```bash
cd /opt/cppc-workstation/cppc-deploy
cp .env.example .env
vim .env
docker compose up -d --build
```

首次启动说明：

- `postgres` 会自动执行 `postgres/init/` 下的 SQL
- `001_init.sql` 负责建表
- `002_seed_cppc_tags.sql` 负责初始化评估标签树
- Java 服务镜像会在服务器上通过 Maven 构建

## 4. 联调地址

前端建议统一通过 Nginx 访问：

- 患者接口：`http://<server-ip>/patient/api/v1/...`
- AI 接口：`http://<server-ip>/ai/api/v1/...`

示例：

```text
POST http://<server-ip>/patient/api/v1/patients
POST http://<server-ip>/patient/api/v1/assessments
GET  http://<server-ip>/patient/api/v1/cppc-tags/tree
POST http://<server-ip>/patient/api/v1/assessments/{assessmentId}/tags
POST http://<server-ip>/ai/api/v1/reports/generate
```

## 5. 常用命令

启动：

```bash
docker compose up -d --build
```

停止：

```bash
docker compose down
```

查看日志：

```bash
docker compose logs -f postgres
docker compose logs -f cppc-patient
docker compose logs -f cppc-ai-agent
docker compose logs -f cppc-gateway
docker compose logs -f nginx
```

重新构建某个服务：

```bash
docker compose build cppc-patient
docker compose up -d cppc-patient
```

## 6. 验收建议

启动后先人工验证：

1. `GET http://<server-ip>/patient/api/v1/cppc-tags/tree`
2. `POST http://<server-ip>/patient/api/v1/patients`
3. `POST http://<server-ip>/patient/api/v1/assessments`
4. `POST http://<server-ip>/patient/api/v1/assessments/{assessmentId}/tags`
5. `POST http://<server-ip>/ai/api/v1/reports/generate`

预期所有接口统一返回：

```json
{
  "code": 0,
  "msg": "success",
  "data": {}
}
```

## 7. 当前边界

- 当前 `cppc-ai-agent` 仍使用 mock `RemoteAiService`
- Coze 真实 HTTP 接口尚未接入
- 因此 `reports/generate` 当前返回的是结构化 mock 报告

## 8. 后续建议

测试环境稳定后，下一步建议补：

1. HTTPS 与域名
2. 自动化发布脚本
3. Coze 真实接入配置
4. 前端静态资源接入 Nginx
