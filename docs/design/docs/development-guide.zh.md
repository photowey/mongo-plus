# 开发、验证与文档维护

本页覆盖当前仓库的常用构建命令、验证路径，以及 `docs/design` 文档站点自身的维护方式。

## Java 工程常用命令

在仓库根目录执行：

```bash
mvn test
```

这条命令是默认、最稳妥的本地验证路径。

如果更习惯 `make` 包装命令：

```bash
make test
make check
make allure
```

如果你要把示例模块一起纳入验证：

```bash
mvn -Pexamples verify
make allure PROFILE=examples
```

## Toolchains 配置

因为仓库同时覆盖 Java 11 与 Java 17 路径，建议先复制模板：

```bash
cp toolchains.xml.template ~/.m2/toolchains.xml
```

然后把两个 `<jdkHome>` 改成本机实际安装目录。

## APT 诊断

如果你正在排查 `@AutoMapper`、字段常量生成，或者怀疑注解处理器没有生效，可以开启详细日志：

```bash
mvn test -Dmaven.compiler.compilerArgs=-Amongoplus.apt.verbose=true
```

正常开发与 CI 不建议默认开启它，避免噪声过多。

## 文档站点现在如何维护

`docs/design` 目录已经从 `pdm` 迁移到 `uv`。今后文档依赖管理统一采用以下流程：

```bash
cd docs/design
uv sync
uv run mkdocs serve -a 127.0.0.1:9527
uv run mkdocs build --strict
```

说明：

- `uv sync` 负责创建虚拟环境并同步 MkDocs 依赖
- `uv run mkdocs serve` 用于本地预览
- `uv run mkdocs build --strict` 用于提交前校验配置、导航和页面引用

如果你希望本地也模拟带分支标识的构建：

```bash
cd docs/design
MONGO_PLUS_DOCS_REPO_NAME="photowey/mongo-plus · dev · abc1234" uv run mkdocs build --strict
```

## 更新文档依赖

如果需要刷新锁文件或升级依赖：

```bash
cd docs/design
uv lock
```

如需导出 requirements 文本文件：

```bash
cd docs/design
uv export --format requirements-txt --no-hashes -o requirements.txt
```

## 文档目录说明

| 路径 | 作用 |
| --- | --- |
| `docs/design/pyproject.toml` | Python 依赖与 `uv` 配置 |
| `docs/design/mkdocs.yml` | 导航与主题配置 |
| `docs/design/docs/` | Mongo Plus 使用手册正文 |

## 提交前建议

文档或代码改动提交前，至少完成以下检查：

1. `mvn test`
2. 如涉及 Starter / 代理 / 插件链路，补跑 `mvn -Pexamples test`
3. 如修改了 `docs/design`，补跑 `uv run mkdocs build --strict`

## GitHub Pages 工作流

仓库已经补充了面向文档站点的 Pages workflow：

- workflow 文件：`.github/workflows/docs-pages.yml`
- push 触发分支：`main`、`dev`
- 部署目标：GitHub Pages

仓库设置中还需要把 Pages 源切到 GitHub Actions：

1. 打开 `Settings -> Pages`
2. 将 `Source` 设置为 `GitHub Actions`
3. push 到 `main` 或 `dev`，或者手动触发 workflow

线上仍然是单一 Pages 站点。当前部署来自哪个分支、哪个短提交，会通过 Material
自带的 GitHub/source 区块显示，而不是额外自定义一套分支组件。

workflow summary 里还会额外输出：

- 分支名
- 短提交哈希
- Pages 部署地址
