English | [简体中文](README.zh-CN.md)

# Mongo Plus Documentation Site

This directory contains the source for the Mongo Plus documentation site.

The site is built with MkDocs Material, managed with `uv`, and structured as a bilingual manual:

- English is the default language
- Simplified Chinese is provided as a switchable locale
- pages are maintained as paired `*.md` and `*.zh.md` files

## What This Site Covers

The current documentation set focuses on:

- framework overview and onboarding
- starter integration and runtime requirements
- `MongoEngine`, wrappers, mappers, and aggregation usage
- interceptor and extension-point usage
- example modules and verification flows
- project-level documentation maintenance notes

## Prerequisites

- Python `3.12`
- `uv >= 0.9`

If `uv` is not installed yet, see the official installation guide:
<https://docs.astral.sh/uv/getting-started/installation/>

## Quick Start

From this directory:

```bash
cd docs/design

uv sync
uv run mkdocs serve -a 127.0.0.1:9527
```

Then open:

- `http://127.0.0.1:9527/`
- `http://127.0.0.1:9527/zh/`

## Build

Run a strict build before submitting documentation changes:

```bash
cd docs/design
uv run mkdocs build --strict
```

The generated site is written to:

- `docs/design/site/`

## Canonical URL Configuration

For local preview, `site_url` defaults to:

- `http://127.0.0.1:9527/`

For deployment, override it through an environment variable:

```bash
cd docs/design
MONGO_PLUS_DOCS_SITE_URL=https://photowey.github.io/mongo-plus/ uv run mkdocs build --strict
```

## Common Commands

Sync or refresh the environment:

```bash
cd docs/design
uv sync
```

Refresh the lockfile:

```bash
cd docs/design
uv lock
```

Export a requirements-style file when needed:

```bash
cd docs/design
uv export --format requirements-txt --no-hashes -o requirements.txt
```

## Repository-Level Shortcuts

From the repository root, the `Makefile` also provides:

```bash
make docs_sync
make docs_build
make docs_serve
```

Override the preview address if necessary:

```bash
make docs_serve DOCS_ADDR=0.0.0.0:9000
```

If you want the local preview to simulate a branch-marked deployment:

```bash
make docs_build DOCS_BRANCH=dev DOCS_SHA=abc1234
make docs_serve DOCS_BRANCH=dev DOCS_SHA=abc1234
```

## GitHub Pages Automation

The repository includes a Pages workflow:

- workflow file: `.github/workflows/docs-pages.yml`
- trigger branches: `main`, `dev`
- deployment target: GitHub Pages

To activate it in the GitHub repository settings:

1. Open `Settings -> Pages`
2. Set `Source` to `GitHub Actions`
3. Push to `main` or `dev`, or trigger the workflow manually

During CI builds, the current branch is injected into the built-in Material
repository/source section through `repo_name`, so the site can show both the
branch and the short commit SHA that produced the current deployment without
introducing a custom branch widget.

The workflow summary also records:

- branch name
- short commit SHA
- deployed Pages URL

## Project Layout

```text
docs/design
├── README.md
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

## Writing Notes

- Keep the English page as the default source file, for example `index.md`.
- Keep the Simplified Chinese translation beside it as `index.zh.md`.
- When adding a new page, add both language variants unless the page is intentionally temporary.
- Run `uv run mkdocs build --strict` after changing navigation, assets, or cross-page links.

## Key Files

- `pyproject.toml`
  Python dependencies and `uv` configuration.
- `mkdocs.yml`
  MkDocs, Material, navigation, and i18n configuration.
- `hooks/copy_sitemap.py`
  Post-build hook used for generated sitemap layout adjustments.
- `docs/`
  The actual documentation pages and shared site assets.
