# Agents.MD

This repository is a **Minecraft 1.21.1 NeoForge** mod. Follow these rules exactly.

---

## 1) Target + constraints (non-negotiable)
- **Minecraft 1.21.1 + NeoForge only.**
- **Do NOT** use Fabric or legacy Forge patterns, APIs, or examples.
- Prefer **data-driven systems + datagen providers** over handwritten JSON.
- Match the repo’s existing architecture, naming, and patterns.

---

## 2) Allowed references (ONLY these)
When you need API details, lifecycle hooks, registries, datagen patterns, etc., you may consult:

1. **NeoForge 1.21.1 official docs**  
   https://docs.neoforged.net/docs/1.21.1/

2. **Kaupenjoe NeoForge 1.21.1 example repo (specific tree)**  
   https://github.com/Tutorials-By-Kaupenjoe/NeoForge-Tutorial-1.21.X/tree/60-jeiCompatibility

3. **This repository’s source code** (treat existing patterns here as the primary reference)

**Do not use any other external references.**  
If information is missing from the two links above and the repo itself, **stop and ask** rather than guessing or pulling patterns from elsewhere.

---

## 3) Change discipline
- Make the **smallest change** that fixes the issue.
- Avoid refactors unless explicitly requested or required to fix the bug.
- If a change touches a **core registry/bootstrap/lifecycle**:
    - Explain the reason briefly.
    - List every file changed.
- **Do not add new dependencies** without asking first.

---

## 4) Commenting rules (minimalistic, high-signal)
- Comments should be **rare**, **short**, and **technical**.
- Add comments only when the logic is non-obvious due to:
    - NeoForge/Minecraft lifecycle quirks
    - syncing/networking edge cases
    - registry/bootstrap ordering constraints
    - datagen constraints or behavior
- Prefer **single-line** comments that explain *why*, not *what*:
    - Good: `// Must run on client because ...`
    - Bad: `// This method sets the block state`
- No tutorial-style narration, no jokes, no fluff.

---

## 5) Repo-first workflow (avoid guessing)
Before writing or changing code:
- **Search the repo first** and align with existing implementations.
- Prefer extending existing helpers/registries/providers rather than creating new parallel systems.
- Keep IDs, paths, and conventions consistent with existing content.

---

## 6) Build + datagen verification
After changes:
- Always run: `./gradlew build`
- If assets/datagen are involved, also run: `./gradlew runData`
- Keep generated outputs consistent (don’t “hand edit” generated files unless the project explicitly does so).

---

## 7) Output expectations (what you must produce)
When you respond with changes:
- Provide **exact file paths** for every file created/edited.
- For edits, include **patch-style diffs** in your summary (what changed and where).
- If a file changes substantially, provide the **full updated file** (ready to paste).
- Otherwise, provide a minimal snippet with enough surrounding context to apply safely.
- Always state **how it integrates** (what registry/event/provider it plugs into, what calls it, etc.).
