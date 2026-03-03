#!/usr/bin/env bash
set -euo pipefail

if ! command -v gitleaks >/dev/null 2>&1; then
  echo "gitleaks is not installed. Install it first: https://github.com/gitleaks/gitleaks"
  exit 127
fi

gitleaks detect \
  --source . \
  --no-git \
  --redact \
  --config .gitleaks.toml \
  --report-format sarif \
  --report-path gitleaks.sarif

echo "Secret scan complete: gitleaks.sarif"
