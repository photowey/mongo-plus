#
# Copyright (c) 2026-present The MongoPlus Authors. All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

from pathlib import Path
import shutil


def on_post_build(config, **kwargs):
    site_dir = Path(config["site_dir"])
    root_sitemap = site_dir / "sitemap.xml"
    root_sitemap_gz = site_dir / "sitemap.xml.gz"
    if not root_sitemap.exists():
        return

    targets = []

    for index_file in site_dir.rglob("index.html"):
        targets.append(index_file.parent)

    for directory in targets:
        sitemap_target = directory / "sitemap.xml"
        if sitemap_target != root_sitemap:
            shutil.copy2(root_sitemap, sitemap_target)

        if root_sitemap_gz.exists():
            gzip_target = directory / "sitemap.xml.gz"
            if gzip_target != root_sitemap_gz:
                shutil.copy2(root_sitemap_gz, gzip_target)

    devtools_dir = site_dir / ".well-known" / "appspecific"
    devtools_dir.mkdir(parents=True, exist_ok=True)
    (devtools_dir / "com.chrome.devtools.json").write_text("{}\n", encoding="utf-8")
