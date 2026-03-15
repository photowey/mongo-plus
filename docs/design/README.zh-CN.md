[English](README.md) | 简体中文

# Mongo Plus 文档站点

这个目录保存了 Mongo Plus 文档站点的源码。

当前站点基于 MkDocs Material 构建，使用 `uv` 管理依赖，并采用双语手册结构：

- 英文是默认语言
- 简体中文通过界面切换提供
- 页面以 `*.md` 和 `*.zh.md` 配对维护

## 站点覆盖范围

当前文档主要覆盖：

- 框架概览与接入说明
- Starter 集成与运行前提
- `MongoEngine`、Wrapper、Mapper 与聚合使用方式
- 拦截器与扩展点说明
- 示例模块与验证流程
- 项目级文档维护说明

## 前置条件

- Python `3.12`
- `uv >= 0.9`

如果本机还没有安装 `uv`，请参考官方安装文档：
<https://docs.astral.sh/uv/getting-started/installation/>

## 快速开始

在当前目录执行：

```bash
cd docs/design

uv sync
uv run mkdocs serve -a 127.0.0.1:9527
```

然后打开：

- `http://127.0.0.1:9527/`
- `http://127.0.0.1:9527/zh/`

## 构建

在提交文档改动前，建议执行严格构建：

```bash
cd docs/design
uv run mkdocs build --strict
```

生成产物输出到：

- `docs/design/site/`

## Canonical URL 配置

本地预览时，`site_url` 默认是：

- `http://127.0.0.1:9527/`

部署时可以通过环境变量覆盖：

```bash
cd docs/design
MONGO_PLUS_DOCS_SITE_URL=https://photowey.github.io/mongo-plus/ uv run mkdocs build --strict
```

## 常用命令

同步或刷新文档环境：

```bash
cd docs/design
uv sync
```

刷新锁文件：

```bash
cd docs/design
uv lock
```

如需导出 requirements 风格文件：

```bash
cd docs/design
uv export --format requirements-txt --no-hashes -o requirements.txt
```

## 仓库级快捷命令

在仓库根目录还可以直接使用 `Makefile`：

```bash
make docs_sync
make docs_build
make docs_serve
```

如需覆盖预览地址：

```bash
make docs_serve DOCS_ADDR=0.0.0.0:9000
```

如果你希望本地预览也模拟分支构建标识：

```bash
make docs_build DOCS_BRANCH=dev DOCS_SHA=abc1234
make docs_serve DOCS_BRANCH=dev DOCS_SHA=abc1234
```

## GitHub Pages 自动化

仓库已经包含 Pages 工作流：

- workflow 文件：`.github/workflows/docs-pages.yml`
- 触发分支：`main`、`dev`
- 部署目标：GitHub Pages

要让它在 GitHub 仓库里真正生效：

1. 打开 `Settings -> Pages`
2. 将 `Source` 设置为 `GitHub Actions`
3. push 到 `main` 或 `dev`，或者手动触发 workflow

CI 构建时会把当前分支和短提交哈希注入 Material 自带的 GitHub/source 区块，通过
`repo_name` 直接显示当前构建来自哪个分支与哪个提交，而不是额外做一套自定义组件。

workflow summary 里还会额外输出：

- 分支名
- 短提交哈希
- Pages 部署地址

## 目录结构

```text
docs/design
├── README.md
├── README.zh-CN.md
├── pyproject.toml
├── uv.lock
├── mkdocs.yml
├── hooks/
│   └── copy_sitemap.py
└── docs/
    ├── assets/
    ├── index.md
    ├── index.zh.md
    ├── getting-started.md
    ├── getting-started.zh.md
    └── ...
```

## 编写约定

- 英文页面作为默认源文件，例如 `index.md`
- 简体中文翻译与英文文件并排放置，例如 `index.zh.md`
- 新增页面时，除非是临时内容，否则应同步补齐双语版本
- 修改导航、资源或跨页链接后，务必执行 `uv run mkdocs build --strict`

## 关键文件

- `pyproject.toml`
  Python 依赖与 `uv` 配置
- `mkdocs.yml`
  MkDocs、Material、导航与 i18n 配置
- `hooks/copy_sitemap.py`
  用于补充 sitemap 输出结构的 post-build hook
- `docs/`
  实际文档页面与共享站点资源
