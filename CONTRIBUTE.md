# Contributing Guide

## How to Create a Pull Request

### 1. Fork the repository

Go to the repo on GitHub and click the **Fork** button in the top right. This creates your own copy of the repo under your GitHub account.

### 2. Clone your fork

```bash
git clone git@github.com:OmarBouchoucha0/pi.git
cd pi
```

### 3. Add the original repo as upstream

This lets you pull in future changes from the main repo:

```bash
git remote add upstream git@github.com:OmarBouchoucha0/pi.git
```

Verify your remotes:
```bash
git remote -v
```

### 4. Create a new branch

Never work directly on `main`. Create a branch named after what you're working on:

```bash
git checkout -b feature/your-feature-name
# or for bug fixes:
git checkout -b fix/your-bug-name
```

### 5. Make your changes

Write your code, then verify the build passes before committing:

### 6. Commit your changes

```bash
git add .
git commit -m "feat: short description of what you did"
```

Use clear commit message prefixes:

| Prefix | When to use |
|---|---|
| `feat:` | Adding a new feature |
| `fix:` | Fixing a bug |
| `refactor:` | Code cleanup, no behavior change |
| `docs:` | Documentation changes only |
| `test:` | Adding or updating tests |

### 7. Sync with upstream before pushing

Make sure your branch is up to date with the latest changes from the main repo:

```bash
git fetch upstream
git rebase upstream/main
```

Resolve any conflicts if they come up, then continue:

```bash
git rebase --continue
```

### 8. Push your branch

```bash
git push origin feature/your-feature-name
```

### 9. Open the Pull Request

1. Go to your fork on GitHub
2. Click the **Compare & pull request** button that appears
3. Set the base branch to `main` on the original repo
4. Write a clear title and description explaining what your PR does and why
5. Click **Create pull request**

---

## Branch Naming Convention

| Type | Format | Example |
|---|---|---|
| New feature | `feature/name` | `feature/jwt-auth` |
| Bug fix | `fix/name` | `fix/login-error` |
| Refactor | `refactor/name` | `refactor/user-service` |
| Documentation | `docs/name` | `docs/update-readme` |
