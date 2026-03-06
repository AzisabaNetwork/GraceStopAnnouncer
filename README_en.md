# README

README language: English | 日本語 (README.md)

---

## English

Overview

GraceStopAnnouncer is a Java-based Minecraft server plugin that provides graceful shutdown features such as holding player connections and emitting notifications during server stop. It supports both Bukkit/Spigot and Velocity platforms.

Primary uses:

- Hold player connections temporarily during server shutdown to delay client disconnects
- Receive stop notifications from external systems (e.g., load balancers or orchestration) and handle them gracefully in-plugin

Key packages in this repository:

- `src/main/java/net/azisaba/gracestopannouncer/bukkit/` — Bukkit adapter and plugin entry
- `src/main/java/net/azisaba/gracestopannouncer/velocity/` — Velocity adapter and plugin entry
- `src/main/java/net/azisaba/gracestopannouncer/core/` — Core logic shared by adapters
- `src/main/resources/` — Default configuration files (e.g., `config.yml`, `velocity-config.yml`)

## Features

- Supports both Bukkit (Spigot/Paper) and Velocity
- Pre-stop hook receiver (`PreStopHookReceiver`)
- Connection hold handler (`HoldConnectionHandler`)
- Message formatting utilities (`MessageFormatter`)

## Requirements

- Java 8 or newer
- Maven (project uses `pom.xml`)

## Build

From the project root run:

```bash
mvn clean package
```

Successful build artifacts are placed in `target/` (e.g., `target/GraceStopAnnouncer.jar`).

## Configuration

Default configuration files live under `src/main/resources/`:

- `config.yml` — for Bukkit
- `velocity-config.yml` — for Velocity

Copy and edit the appropriate file into your server's plugin config folder before starting the server.

## Usage (deployment)

1. Place the built JAR into the plugins/mods folder of the target server (Bukkit or Velocity).
2. Restart or reload the server so the plugin loads.
3. Modify configuration files to tune messages and hold behavior.

## For contributors / developers

- Inspect adapter implementations and core logic under `src/main/java/net/azisaba/gracestopannouncer/`.
- There are no tests included by default; consider adding JUnit tests for key behaviors.

## License

If no LICENSE file is present, check with the repository owner for usage and distribution rights.
