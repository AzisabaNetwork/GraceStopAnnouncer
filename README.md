
# README

README language: 日本語 (baseline) | [English](README_en.md)

---
---

## 日本語

概要

GraceStopAnnouncer は、Java 製の Minecraft サーバープラグインで、Graceful shutdown（優雅な停止）時に接続保持や通知を行うための機能を提供します。Bukkit/Spigot と Velocity の両方をサポートしています。

主な用途:

- サーバー停止時に接続を保持してクライアントの切断を遅延させる
- 外部サービス（例: ロードバランサやオーケストレーションツール）からの停止通知を受け取り、プラグイン側で適切に処理する

このリポジトリは以下の主要パッケージを含みます:

- `src/main/java/net/azisaba/gracestopannouncer/bukkit/` — Bukkit 用アダプタとプラグイン本体
- `src/main/java/net/azisaba/gracestopannouncer/velocity/` — Velocity 用アダプタとプラグイン本体
- `src/main/java/net/azisaba/gracestopannouncer/core/` — 共通コアロジック
- `src/main/resources/` — デフォルト設定ファイル（`config.yml`, `velocity-config.yml` など）

## 特徴

- Bukkit（Spigot/Paper）と Velocity の両対応
- 停止通知の受信ハンドラ（`PreStopHookReceiver` 等）
- 接続をホールドするハンドラ（`HoldConnectionHandler`）
- カスタムメッセージフォーマッタ（`MessageFormatter`）

## 要件

- Java 8 以上
- Maven（`pom.xml` がリポジトリに含まれています）

## ビルド方法

プロジェクトのルートで以下のコマンドを実行してビルドします（macOS/zsh の場合）:

```bash
mvn clean package
```

ビルドに成功すると、`target/GraceStopAnnouncer.jar` と `target/original-GraceStopAnnouncer.jar` が生成されます。

## 設定

デフォルト設定ファイルは `src/main/resources/` にあります:

- `config.yml` — Bukkit 用の設定
- `velocity-config.yml` — Velocity 用の設定

必要に応じてこれらをサーバーのプラグイン設定フォルダにコピーして編集してください。

## 使い方（デプロイ）

1. 対応するサーバープラットフォーム（Bukkit/Velocity）へ JAR を配置します。
2. サーバーを再起動またはリロードしてプラグインを読み込ませます。
3. 設定ファイルを編集して通知メッセージやホールド挙動を調整します。

## 開発者向けノート

- コアのエントリポイントや各アダプタの実装は `src/main/java/net/azisaba/gracestopannouncer/` 以下を確認してください。
- 単体テストはこのリポジトリに含まれていないようです。必要なら JUnit 等で追加してください。

## ライセンス

プロジェクトにライセンスファイルが含まれていない場合は、利用/配布ポリシーをリポジトリ管理者に確認してください。
