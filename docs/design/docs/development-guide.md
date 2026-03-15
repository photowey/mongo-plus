# Development, Verification, And Docs Maintenance

This page covers the common build commands for the current repository, the recommended validation
paths, and how the documentation site under `docs/design` is maintained.

## Common Java Commands

Run from the repository root:

```bash
mvn test
```

This is the default and most stable local verification path.

If you prefer the `make` wrappers:

```bash
make test
make check
make allure
```

If you want to include the example modules:

```bash
mvn -Pexamples verify
make allure PROFILE=examples
```

## Toolchains Setup

Because the repository spans both Java 11 and Java 17 paths, start by copying the template:

```bash
cp toolchains.xml.template ~/.m2/toolchains.xml
```

Then replace both `<jdkHome>` entries with the real local installation directories.

## APT Diagnostics

If you are debugging `@AutoMapper`, generated field constants, or suspect that annotation
processing did not run, enable the verbose APT log:

```bash
mvn test -Dmaven.compiler.compilerArgs=-Amongoplus.apt.verbose=true
```

This should be used for diagnostics only, not as the default local or CI path.

## How The Documentation Site Is Maintained

`docs/design` has already been migrated from `pdm` to `uv`. The standard workflow is:

```bash
cd docs/design
uv sync
uv run mkdocs serve -a 127.0.0.1:9527
uv run mkdocs build --strict
```

Notes:

- `uv sync` creates the virtual environment and syncs MkDocs dependencies.
- `uv run mkdocs serve` is the local preview path.
- `uv run mkdocs build --strict` validates configuration, navigation, and page references before a
  commit.

To simulate branch-aware builds locally:

```bash
cd docs/design
MONGO_PLUS_DOCS_REPO_NAME="photowey/mongo-plus · dev · abc1234" uv run mkdocs build --strict
```

For deployed canonical URLs, override `site_url` when building:

```bash
cd docs/design
MONGO_PLUS_DOCS_SITE_URL=https://photowey.github.io/mongo-plus/ uv run mkdocs build --strict
```

## Updating Documentation Dependencies

To refresh the lockfile or upgrade dependencies:

```bash
cd docs/design
uv lock
```

To export a `requirements.txt`-style file:

```bash
cd docs/design
uv export --format requirements-txt --no-hashes -o requirements.txt
```

## Docs Directory Layout

| Path | Purpose |
| --- | --- |
| `docs/design/pyproject.toml` | Python dependencies and `uv` configuration |
| `docs/design/mkdocs.yml` | Navigation, theme, and language-switcher configuration |
| `docs/design/docs/*.md` | Default English manual pages |
| `docs/design/docs/*.zh.md` | Simplified Chinese manual pages |

## Recommended Pre-Commit Checks

Before submitting documentation or code changes, complete at least:

1. `mvn test`
2. `mvn -Pexamples test` if the change touches starters, proxies, or plugin chains
3. `uv run mkdocs build --strict` if the change touches `docs/design`

## GitHub Pages Workflow

The repository also includes a dedicated Pages workflow for this documentation site:

- workflow file: `.github/workflows/docs-pages.yml`
- push triggers: `main`, `dev`
- deployment: GitHub Pages

Repository settings must still point Pages to GitHub Actions:

1. Open `Settings -> Pages`
2. Set `Source` to `GitHub Actions`
3. Push to `main` or `dev`, or trigger the workflow manually

The deployed site remains a single Pages site. The currently deployed branch and short commit SHA
are exposed through the built-in Material repository/source block by injecting them into
`repo_name`.

The workflow summary also records:

- branch name
- short commit SHA
- final Pages deployment URL
