# Smart Picture

一个基于 Spring Boot 的图片管理后端，提供用户登录、图片上传与审核、公共图库、个人空间、团队空间、图片检索、空间分析和 AI 扩图等能力。

## 主要功能

- 用户管理：注册、登录、退出、管理员用户管理。
- 图片管理：文件上传、URL 上传、批量抓取、图片编辑、删除和审核。
- 图片存储：支持上传到腾讯云 COS，并处理图片元数据、压缩图和缩略图。
- 图片检索：支持按图片搜索、按主色调检索，以及分页查询图片。
- 空间管理：支持私有空间和团队空间，包含浏览者、编辑者、管理员三级权限。
- 空间分析：统计空间用量、分类、标签、大小、用户行为和空间排行。
- 协同编辑：通过 WebSocket + Disruptor 实现团队空间图片的多人协作编辑。
- AI 能力：接入阿里云百炼（DashScope）图片扩图任务。

## 技术栈

- Java 8、Spring Boot 2.7.6、Maven
- Spring MVC、Spring AOP、Spring WebSocket
- MyBatis-Plus 3.5.9、MySQL 8
- Redis、Spring Session、Caffeine
- Sa-Token 权限认证
- Knife4j / OpenAPI 接口文档
- 腾讯云 COS 对象存储
- Hutool、Jsoup、LMAX Disruptor
- ShardingSphere 5.2（分表相关代码已预留，当前配置中排除了自动装配）

## 项目结构

```text
smart/
├─ sql/                         # 数据库初始化脚本
├─ src/main/java/org/baicai/smart/
│  ├─ controller/               # HTTP 接口
│  ├─ service/                  # 业务服务
│  ├─ manager/                  # 存储、权限、WebSocket、分表等管理器
│  ├─ mapper/                   # MyBatis-Plus Mapper
│  ├─ model/                    # Entity、DTO、VO、枚举
│  ├─ config/                   # 配置类
│  └─ api/                      # 外部服务调用
├─ src/main/resources/          # 配置文件、Mapper XML、静态资源
├─ Dockerfile
├─ docker-compose.yml
└─ pom.xml
```

## 本地运行

### 环境要求

- JDK 8
- Maven 3.8+
- MySQL 8
- Redis

### 启动步骤

1. 使用 [sql/create_table.sql](sql/create_table.sql) 初始化数据库。
2. 修改 [src/main/resources/application.yml](src/main/resources/application.yml) 中的 MySQL 和 Redis 连接信息。
3. 如需使用对象存储或 AI 扩图，请配置腾讯云 COS 和阿里云 DashScope 的密钥。
4. 构建并启动项目：

```bash
mvn clean package -DskipTests
mvn spring-boot:run
```

默认地址：

- 服务地址：`http://localhost:8088/api`
- 健康检查：`http://localhost:8088/api/health`
- 接口文档：`http://localhost:8088/api/doc.html`

## Docker 运行

生产配置通过环境变量读取数据库、Redis、腾讯云 COS 和阿里云配置，变量名可参考 [docker-compose.yml](docker-compose.yml) 与 [src/main/resources/application-prod.yml](src/main/resources/application-prod.yml)。

```bash
mvn clean package -DskipTests
docker compose up --build
```

## WebSocket

团队空间图片协同编辑地址：

```text
ws://localhost:8088/api/ws/picture/edit?pictureId={pictureId}
```

连接前会校验登录状态、图片所属空间和图片编辑权限。
