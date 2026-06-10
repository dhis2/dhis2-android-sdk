---
name: open-pr
description: Open a GitHub pull request for an ANDROSDK Jira task. Use when the user asks to "open a PR", "abrir la PR", "create the pull request", "subir la PR", or similar phrasing on a branch named like ANDROSDK-XXXX. Pulls Jira context, formats the title and description, and runs `gh pr create` targeting `develop` by default.
---

# Open Pull Request (ANDROSDK)

Open a GitHub PR for the current branch following the dhis2-android-sdk conventions: typed title with the Jira code, a short description grounded in the Jira issue, and `develop` as the default target.

## When to use

- The current branch is named after a Jira ticket (e.g. `ANDROSDK-1995`).
- The user explicitly asks to open / create / push a PR.
- Changes are already committed and pushed (or about to be — verify before invoking `gh pr create`).

This skill **only opens the PR**. It does not commit, rebase, or run any Gradle / `testAll` task — those happen beforehand.

## Inputs

- **Target branch** (optional): defaults to `develop`. If the user passes a different base ("contra master", "target main", `--base xyz`), use that instead.
- **Draft mode**: PR is opened in **normal (non-draft) mode** by default. Only pass `--draft` if the user explicitly asks for draft / borrador.
- **Type override** (optional): the user can pin the type prefix when invoking the skill ("ábrela como feature", "abre la PR tipo chore"). If passed, use it verbatim and skip the inference in step 4.

## Workflow

1. **Confirm prerequisites**
   - `git rev-parse --abbrev-ref HEAD` — extract the `ANDROSDK-XXXX` code from the branch name. If the branch does not match `ANDROSDK-\d+`, ask the user for the ticket code.
   - `git status` — ensure working tree is clean. If not, stop and tell the user to commit first.
   - `git log <base>..HEAD --oneline` — list the commits that will go into the PR (use the chosen target branch as `<base>`, default `origin/develop`).
   - `gh pr view --json url 2>/dev/null` — if a PR already exists for this branch, surface its URL and stop.

2. **Push the branch if needed**
   - `git status -sb` shows the upstream state. If the branch has no upstream or is ahead, run `git push -u origin <branch>` before creating the PR.

3. **Fetch the Jira issue**
   - Use the Atlassian MCP tool `mcp__claude_ai_Atlassian__getJiraIssue` with:
     - `cloudId`: `b40c24fc-6bb4-4f37-bcbb-7d8813be5fc3` (dhis2.atlassian.net)
     - `issueIdOrKey`: the ANDROSDK code from the branch
   - From the response, capture: `summary` (Jira title), `description` (Jira body — Atlassian Document Format, extract the plain text), and `issuetype.name` (Bug / Story / Task / etc.).
   - If the MCP tool is not available in the current session, ask the user to paste the Jira summary and description, or proceed with what can be inferred from the diff/commits.

4. **Determine the type prefix**
   - If the user passed a type override when invoking the skill, use it and skip the rest of this step.
   - `fix:` — **only** when the Jira issuetype is **Bug**. The Jira issuetype is the source of truth for this case.
   - Otherwise the Jira issuetype is typically **Task** for both new functionality and maintenance work — Jira does not distinguish them. Decide between `feature:` and `chore:` by looking at the **actual changes**:
     - `feature:` — introduces new SDK capability or user-visible behavior (often coming from the DHIS2 product roadmap). Examples: new module, new public repository method, support for a new DHIS2 API endpoint, new sync flow.
     - `chore:` — does not change user-visible behavior. Examples: refactors, dependency bumps, internal renames, test additions, build / CI changes, code cleanup, doc-only updates, migration of a model to Kotlin without behavior change.
   - If the call is genuinely ambiguous (e.g. a refactor that also exposes a small new API), stop and ask the user which prefix to use rather than guessing.

5. **Compose the PR title**
   - Format: `<type>: [ANDROSDK-XXXX] <short imperative title in English>`
   - The short title is a tightened version of the Jira summary — drop trailing punctuation, keep it under ~70 characters total, lowercase except for proper nouns / acronyms.
   - Verify against the project's existing PR style with `gh pr list --limit 5 --json title` before finalizing.
   - Examples of the style used in this repo:
     - `chore: [ANDROSDK-2296] add authorization type to databaseAccount`
     - `fix: [ANDROSDK-1995] build SQL for PI disaggregation`
     - `feature: [ANDROSDK-2100] support tracker importer v2 sync`

6. **Compose the PR body**
   - Written in English.
   - One or two paragraphs, no more.
   - Paragraph 1: what the PR does, grounded in the Jira description (rephrase, do not paste raw ADF JSON).
   - Paragraph 2 (optional): notable implementation choices, follow-ups, or caveats — only include if non-obvious from the diff. Skip if not needed.
   - Final line, separated by a blank line:
     ```
     Related task: [ANDROSDK-XXXX](https://dhis2.atlassian.net/browse/ANDROSDK-XXXX)
     ```
   - Do NOT add a `Test plan` section, a `Summary` heading, or a "🤖 Generated with Claude Code" footer — they are not part of this repo's PR style.
   - Do NOT add `Co-Authored-By` lines.

7. **Create the PR**
   - Run via Bash, passing the body with a heredoc so newlines and markdown survive intact:
     ```bash
     gh pr create --base develop --title "<title>" --body "$(cat <<'EOF'
     <paragraph 1>

     Related task: [ANDROSDK-XXXX](https://dhis2.atlassian.net/browse/ANDROSDK-XXXX)
     EOF
     )"
     ```
   - Override `--base` if the user specified a different target.
   - Append `--draft` only if explicitly requested.
   - Do NOT pass `--fill` — always supply title and body.

8. **Report the PR URL** back to the user. That is the deliverable.

## Edge cases

- **Jira fetch fails / issue not found**: tell the user the code couldn't be resolved and ask whether to proceed with a generic description built from the diff.
- **Branch name does not match `ANDROSDK-\d+`**: ask for the ticket code explicitly before generating the title.
- **Multiple Jira codes referenced in commits**: use the branch name's code as the primary; mention the others inline in paragraph 1 if relevant.
- **No commits ahead of base**: stop and tell the user — there is nothing to open a PR for.
- **PR already open for this branch**: don't create a duplicate; output the existing URL.

## What NOT to do

- Do not commit, amend, or rebase as part of this skill. The author controls their git history.
- Do not run `testAll` or any Gradle task — those happen before invoking this skill.
- Do not open the PR as draft unless asked.
- Do not target `master` unless explicitly told to.
