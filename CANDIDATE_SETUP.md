# Environment Setup Guide — Coding Interview

Before your interview, please install the runtime and package manager for the language you'll be using. This should take **10–15 minutes** and must be done in advance — the actual challenge will be shared at the start of the session.

You will only need **one** of the four below. Confirm with your recruiter which language you'll be using.

---

## TypeScript

**Requirements:** Node.js ≥ 20, npm

### Install Node.js

**macOS / Linux — via nvm (recommended)**

```bash
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash
# Restart your terminal, then:
nvm install 20
nvm use 20
```

**Windows — via nvm-windows**

Download and run the installer from [github.com/coreybutler/nvm-windows/releases](https://github.com/coreybutler/nvm-windows/releases), then:

```powershell
nvm install 20
nvm use 20
```

**Direct download (any OS)**

Download the LTS installer from [nodejs.org](https://nodejs.org/).

### Verify

```bash
node --version   # v20.x.x or later
npm --version
```

---

## Go

**Requirements:** Go ≥ 1.21

### Install Go

**macOS — Homebrew**

```bash
brew install go
```

**Linux**

```bash
wget https://go.dev/dl/go1.21.0.linux-amd64.tar.gz
sudo tar -C /usr/local -xzf go1.21.0.linux-amd64.tar.gz
```

Add to `~/.bashrc` or `~/.zshrc`:

```bash
export PATH=$PATH:/usr/local/go/bin
```

**Windows / direct download**

Download the installer from [go.dev/dl](https://go.dev/dl/).

### Verify

```bash
go version   # go1.21.x or later
```

---

## Python

**Requirements:** Python ≥ 3.11

### Install Python

**macOS / Linux — via pyenv (recommended)**

```bash
# macOS
brew install pyenv

# Linux
curl https://pyenv.run | bash
```

Restart your terminal, then:

```bash
pyenv install 3.11.0
```

**Windows — via pyenv-win**

```powershell
pip install pyenv-win --target "$HOME\.pyenv"
pyenv install 3.11.0
```

**Direct download (any OS)**

Download from [python.org/downloads](https://www.python.org/downloads/).

### Verify

```bash
python --version   # Python 3.11.x or later
pip --version
```

Also confirm you can create a virtual environment:

```bash
python -m venv test-env
# Should create a `test-env` folder without errors. You can delete it after.
```

---

## Java

**Requirements:** Java ≥ 21, Maven ≥ 3.8

### Install Java and Maven

**macOS / Linux — via SDKMAN (recommended, installs both)**

```bash
curl -s "https://get.sdkman.io" | bash
# Restart your terminal, then:
sdk install java 21.0.3-tem
sdk install maven 3.9.6
```

**macOS — Homebrew**

```bash
brew install openjdk@21 maven
```

Follow the caveats printed by Homebrew to add Java to your `PATH`.

**Windows / direct download**

- Java: [adoptium.net](https://adoptium.net/) — download the Java 21 LTS installer
- Maven: [maven.apache.org/download.cgi](https://maven.apache.org/download.cgi) — download the binary zip, extract it, and add the `bin/` folder to your `PATH`

### Verify

```bash
java --version   # openjdk 21.x.x or later
mvn --version    # Apache Maven 3.8.x or later
```

---

## Other Prerequisites

Regardless of language, please also have:

- [ ] **Git** installed ([git-scm.com/downloads](https://git-scm.com/downloads))
- [ ] An **IDE or editor** of your choice configured for the language (VS Code, IntelliJ, GoLand, PyCharm, etc.)
- [ ] A stable internet connection for the session

---

## Confirming You're Ready

You're good to go if the version-check commands above print the expected versions. If anything fails, please flag it with your recruiter **before** the interview — we want you spending your session on the problem, not on tooling.
