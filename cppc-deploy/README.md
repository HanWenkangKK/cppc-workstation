# Test Environment Deployment

本目录提供单台 Linux 云服务器上的最小测试环境部署方案，目标是尽快拉起后端，供前端联调。

## 1. 部署内容

当前 `docker compose` 会启动以下服务：

- `postgres`
- `cppc-patient`
- `cppc-ai-agent`
- `cppc-gateway`

访问入口：

- Docker 网关宿主机映射端口：`http://127.0.0.1:${GATEWAY_HOST_PORT}`
- 对外统一入口：由宿主机现有 Nginx 反向代理到 `cppc-gateway`
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
3. 宿主机已有 Nginx，且可配置反向代理
4. 安全组放通 `80` 端口
5. 使用 `git clone` 拉取本仓库代码到服务器

## 3. 启动步骤

在服务器执行：

```bash
cd /opt/cppc-workstation/cppc-deploy
cp .env.example .env
vim .env
export DOCKER_BUILDKIT=1
export COMPOSE_DOCKER_CLI_BUILD=1
docker compose up -d --build
```

本地开发如果只需要数据库，不要直接拉整套服务，执行：

```bash
docker compose up -d postgres
```

默认会把数据库映射到宿主机 `POSTGRES_HOST_PORT`，因此本机可直接连接：

```text
host=127.0.0.1
port=${POSTGRES_HOST_PORT}
database=${POSTGRES_DB}
username=${POSTGRES_USER}
password=${POSTGRES_PASSWORD}
```

首次启动说明：

- `postgres` 会自动执行 `postgres/init/` 下的 SQL
- `001_init.sql` 负责建表
- `002_seed_cppc_tags.sql` 负责初始化评估标签树
- Java 服务镜像会在服务器上通过 Maven 构建
- Docker 构建阶段会使用 `maven/settings.xml` 中配置的国内镜像
- 建议启用 `DOCKER_BUILDKIT=1`，后续重复构建时可更好利用 Docker 构建缓存
- `cppc-gateway` 会暴露到宿主机 `GATEWAY_HOST_PORT`，供宿主机 Nginx 反向代理

宿主机 Nginx 反向代理示例：

```nginx
server {
    listen 80;
    server_name _;

    location / {
        proxy_pass http://127.0.0.1:8889;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

如果你修改了 `.env` 里的 `GATEWAY_HOST_PORT`，同步调整 `proxy_pass` 端口即可。

## 4. 联调地址

前端建议统一通过宿主机 Nginx 访问：

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
- 当前对外入口依赖宿主机现有 Nginx，而不是 Docker 内部 Nginx

## 8. 后续建议

测试环境稳定后，下一步建议补：

1. HTTPS 与域名
2. 自动化发布脚本
3. Coze 真实接入配置
4. 前端静态资源接入宿主机 Nginx
