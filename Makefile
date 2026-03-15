SHELL := /bin/bash

# ----------------------------------------------------------------

MVN ?= $(if $(MVND_HOME),mvnd,mvn)
PROFILE ?=
ALLURE_MODULE := mongo-plus-report
ALLURE_PROFILE_ARGS := $(if $(PROFILE),-P$(PROFILE),)
DOCS_DIR := $(CURDIR)/docs/design
DOCS_ADDR ?= 127.0.0.1:9527
DOCS_SITE_URL ?= http://$(DOCS_ADDR)/
DOCS_BRANCH ?= local
DOCS_SHA ?= local
DOCS_REPO_NAME ?= photowey/mongo-plus · $(DOCS_BRANCH) · $(DOCS_SHA)
UV ?= uv
UV_CACHE_DIR ?= /tmp/uv-cache

.PHONY: clean compile install test deploy package tree prepare perform check git_config allure allure_serve docs_sync docs_build docs_serve

.DEFAULT_GOAL := help

# ----------------------------------------------------------------

dir:
	@echo "Current directory: $(CURDIR)"

# ----------------------------------------------------------------

clean: dir
	@echo "Cleaning the project..."
	$(MVN) clean -T 1C

# ----------------------------------------------------------------

compile: clean
	@echo "Using $(MVN) to compile the project..."
	$(MVN) compile -T 1C

# ----------------------------------------------------------------

install: clean
	@echo "Using $(MVN) to install the project..."
	$(MVN) install -T 1C

# ----------------------------------------------------------------

test: clean
	@echo "Using $(MVN) to test the project..."
	$(MVN) test -T 1C

deploy: clean
	@echo "Using $(MVN) to deploy the project..."
	$(MVN) -DskipTests=true source:jar deploy -T 1C

package: clean
	@echo "Using $(MVN) to package the project..."
	$(MVN) -DskipTests=true package -T 1C

tree:
	@echo "Using $(MVN) to show dependency tree..."
	$(MVN) dependency:tree -Dincludes=$(filter-out $@,$(MAKECMDGOALS))

# ----------------------------------------------------------------

prepare: clean
	@echo "Using $(MVN) to release:prepare the project..."
	$(MVN) release:prepare

perform:
	@echo "Using $(MVN) to release:perform the project..."
	$(MVN) release:perform

# ----------------------------------------------------------------

check:
	@echo "Using $(MVN) to checkstyle:check the project..."
	$(MVN) checkstyle:check -T 1C

# ----------------------------------------------------------------

allure: clean
	@echo "Using $(MVN) to generate the Allure report..."
	$(MVN) $(ALLURE_PROFILE_ARGS) verify
	@echo "Allure report: $(CURDIR)/target/allure-report/index.html"

allure_serve:
	@echo "Using $(MVN) to serve the Allure report..."
	$(MVN) -pl $(ALLURE_MODULE) allure:serve

# ----------------------------------------------------------------

docs_sync:
	@echo "Using $(UV) to sync the documentation environment..."
	cd $(DOCS_DIR) && UV_CACHE_DIR=$(UV_CACHE_DIR) $(UV) sync

docs_build:
	@echo "Using $(UV) to build the documentation site..."
	cd $(DOCS_DIR) && MONGO_PLUS_DOCS_REPO_NAME="$(DOCS_REPO_NAME)" MONGO_PLUS_DOCS_SITE_URL=$(DOCS_SITE_URL) UV_CACHE_DIR=$(UV_CACHE_DIR) $(UV) run mkdocs build --strict

docs_serve:
	@echo "Using $(UV) to serve the documentation site..."
	cd $(DOCS_DIR) && MONGO_PLUS_DOCS_REPO_NAME="$(DOCS_REPO_NAME)" MONGO_PLUS_DOCS_SITE_URL=$(DOCS_SITE_URL) UV_CACHE_DIR=$(UV_CACHE_DIR) $(UV) run mkdocs serve -a $(DOCS_ADDR)

# ----------------------------------------------------------------

name?=Your Name
email?=you@example.com

git_config:
	@echo "Git user.name and user.email have been set globally."
	git config user.name "$(name)"
	git config user.email "$(email)"

# ----------------------------------------------------------------

help:
	@echo "Available targets:"
	@echo "  clean        - Clean the project"
	@echo "  compile      - Compile the project"
	@echo "  test         - Run tests"
	@echo "  deploy       - Deploy the project"
	@echo "  package      - Package the project"
	@echo "  tree         - Show dependency tree (e.g., make tree group:artifact | :artifact)"
	@echo "  docs_sync    - Sync the uv-managed documentation environment"
	@echo "  docs_build   - Build the documentation site with strict checks (override with DOCS_BRANCH=name DOCS_SHA=sha)"
	@echo "  docs_serve   - Serve the documentation site (override with DOCS_ADDR=host:port DOCS_BRANCH=name DOCS_SHA=sha)"
	@echo "  prepare      - Release:prepare the project"
	@echo "  perform      - Release:perform the project"
	@echo "  check        - Checkstyle:check the project"
	@echo "  allure       - Generate the root Allure report (set PROFILE=examples when needed)"
	@echo "  allure_serve - Serve the root Allure report from the mongo-plus-report module"
	@echo "  git_config   - Git:config git user.name and user.email of the project"
	@echo "  help         - Show this help message"

# ----------------------------------------------------------------

%:
	@echo "Unknown target: $@"
	@echo "Use 'make help' to see available targets."
