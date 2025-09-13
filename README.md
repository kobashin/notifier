# Notifier - スマホ通知連動LEDシステム

## プロジェクト概要

### 目的
スマートフォンからの通知情報を取得し、外部のLEDライトを制御することで、視覚的な通知システムを構築する。

### 主な機能
- ✅ **スマートフォンの通知情報の取得**: NotificationListenerService
- ✅ **リアルタイム通知監視**: VS Codeターミナル表示
- ✅ **アプリ別色分け**: WhatsApp緑、Gmail赤等の自動設定
- ✅ **優先度別パターン**: 高速/低速点滅、常時点灯等
- ✅ **JSON データ生成**: 外部LED制御用構造化データ
- 🔄 **LED制御実装**: 次フェーズで予定

## システム要件

### 機能要件

#### 1. 通知取得機能
- [ ] アプリケーション別通知の識別
- [ ] 通知内容の解析（タイトル、メッセージ、重要度）

#### 2. LED制御機能
- [ ] 色の制御（RGB）
- [ ] 明度の制御
- [ ] 点滅パターンの制御

## 技術仕様

### アーキテクチャ

#### システム構成
```
[Pixel 4a (Android 13)]
    ↓ (USB/ADB接続)
[開発PC (VS Code)]
    ↓ (リアルタイムログ監視)
[NotificationListenerService]
    ↓ (JSON通知データ)
[LED制御システム] (未実装)
    ↓ (制御信号)
[LEDハードウェア] (未実装)
```

#### 実装済みコンポーネント
- **NotificationListenerApp**: Kotlin製通知監視アプリ
- **NotificationListenerService**: リアルタイム通知検出エンジン
- **MainActivity**: 権限管理UI
- **ADBログ監視**: VS Codeターミナルでのリアルタイム表示
- **JSON生成**: LED制御用データフォーマット

#### スクリプト概要

##### 🔧 **開発・デバッグツール**
- **`android/WorkingPixelInfo.ps1`**
  - Pixel 4aデバイス接続確認スクリプト
  - ADB接続状態、デバイス情報、バッテリー状態を表示
  - デバッグ環境のセットアップ検証用

##### 📱 **Androidアプリケーション**
- **`android/NotificationListenerApp/`** - メインアプリディレクトリ
  
  **📋 コアファイル:**
  - **`app/src/main/java/com/example/notificationlistener/NotificationListenerService.kt`**
    - **機能**: 通知検出のメインエンジン
    - **役割**: Android通知をリアルタイムで監視し、JSON形式で出力
    - **主要処理**: onNotificationPosted(), アプリ別色分け, 優先度判定
  
  - **`app/src/main/java/com/example/notificationlistener/MainActivity.kt`**
    - **機能**: ユーザーインターフェース
    - **役割**: 通知アクセス権限の管理と状態表示
    - **主要処理**: 権限チェック, 設定画面遷移, ステータス更新

  **🎨 UI・設定ファイル:**
  - **`app/src/main/res/layout/activity_main.xml`**
    - **機能**: メイン画面のレイアウト定義
    - **要素**: 権限状態表示, 設定ボタン, 通知カウンター
  
  - **`app/src/main/res/values/themes.xml`**
    - **機能**: アプリテーマ設定 (AppCompatテーマ)
    - **修正履歴**: Material Theme → AppCompat Themeでクラッシュ解決
  
  - **`app/src/main/AndroidManifest.xml`**
    - **機能**: アプリ権限とサービス登録
    - **重要設定**: BIND_NOTIFICATION_LISTENER_SERVICE権限

  **⚙️ ビルド設定:**
  - **`app/build.gradle.kts`**
    - **機能**: Gradle依存関係とビルド設定
    - **主要設定**: Kotlin DSL, AppCompat依存関係
  
  - **`settings.gradle.kts`** & **`build.gradle.kts`**
    - **機能**: プロジェクト全体の設定とプラグイン管理

##### 📄 **設定・ドキュメント**
- **`.gitignore`**
  - Androidビルド成果物、IDE設定ファイルの除外設定
  
- **`.vscode/settings.json`**
  - VS Code開発環境の設定ファイル
  
- **`README.md`**
  - プロジェクト仕様書（本ファイル）

##### 🔍 **使用方法**
1. **アプリビルド**: `./gradlew assembleDebug` (android/NotificationListenerApp/内)
2. **デバイス接続確認**: `./WorkingPixelInfo.ps1`
3. **APKインストール**: `adb install -r app-debug.apk`
4. **ログ監視**: `adb logcat -s "NotificationListener:*"`

#### データフロー
1. **通知検出**: Android通知がNotificationListenerServiceにより捕捉
2. **データ抽出**: アプリ名、タイトル、内容、優先度を解析
3. **LED情報生成**: アプリ別カラー(#25D366等)とパターン(blink等)を自動生成
4. **JSON出力**: 構造化データとしてログ出力
5. **リアルタイム監視**: ADB経由でVS Codeに表示

#### 通信プロトコル
#### 通信プロトコル
- ✅ **Pixel 4a - 開発PC間**: USB + ADB (Android Debug Bridge)
- ✅ **ログ転送**: Android Logcat → VS Code Terminal
- ✅ **データ形式**: JSON (アプリ名、タイトル、内容、LEDカラー、パターン)
- 🔄 **LED制御**: 未実装 (将来的にWi-Fi/Bluetooth予定)

### ハードウェア要件

#### スマートフォン側
- [ ] OS: Android 13

#### 制御システム側
- [ ] マイコン: Raspberry Pi Pico WH or ESP32
- [ ] 通信: Wi-Fi対応
- [ ] GPIO: LED制御用ピン

### ソフトウェア要件

#### スマートフォンアプリ
- [ ] 開発言語: 
  - Android: Kotlin

#### 制御システム
- [ ] 開発言語: C/C++
- [ ] ライブラリ: 
  - LED制御: 未定
  - 通信: 未定
  - ハードウェア制御: 未定

## データ設計

### 通知データ構造
```json
{
  "id": "2",
  "packageName": "com.google.android.apps.messaging",
  "appName": "メッセージ",
  "title": "新着メッセージ",
  "content": "メッセージ内容",
  "subText": "",
  "timestamp": 1757726318033,
  "priority": 0,
  "category": "msg",
  "ledColor": "#FFFFFF",
  "ledPattern": "slow_blink"
}
```

### LED制御マッピング
#### アプリ別カラー設定
- **WhatsApp**: `#25D366` (緑)
- **Gmail**: `#EA4335` (赤)
- **電話**: `#FF0000` (赤)
- **Spotify**: `#1DB954` (緑)
- **Discord**: `#5865F2` (紫)
- **Twitter**: `#1DA1F2` (青)
- **Messenger**: `#0084FF` (青)
- **デフォルト**: `#FFFFFF` (白)

#### 優先度別パターン
- **HIGH**: `fast_blink` (高速点滅)
- **DEFAULT**: `slow_blink` (低速点滅)
- **LOW**: `solid` (常時点灯)
- **その他**: `fade` (フェード)

### 設定データ構造

## UI/UX設計

### スマートフォンアプリ画面構成

### LED表示パターン

## 開発計画

### ✅ フェーズ1: プロトタイプ開発 (完了)
- ✅ **基本的な通知取得機能**: NotificationListenerService実装
- ✅ **JSON データ生成**: LED制御用構造化データ
- ✅ **リアルタイム監視**: VS Codeでのログ表示
- ✅ **権限管理機能**: Android設定連携UI
- ✅ **Pixel 4a動作確認**: 実機テスト完了

### 🔄 フェーズ2: LED制御実装 (次期)
- [ ] **LEDハードウェア選定**: ESP32/Raspberry Pi等
- [ ] **Wi-Fi通信実装**: JSON送信機能
- [ ] **LED制御回路**: フルカラーLED対応
- [ ] **制御プロトコル**: RESTful API設計

### 🔄 フェーズ3: 機能拡張
- [ ] **アプリ別設定**: ユーザーカスタマイズ
- [ ] **パターン制御**: 複雑な点滅シーケンス
- [ ] **音声通知**: スピーカー連携

### フェーズ3: 最適化・安定化
- [ ] パフォーマンス最適化
- [ ] エラーハンドリング強化
- [ ] セキュリティ対応

## 検討事項・課題

### 技術的課題
- [ ] Androidスマホの通知情報の取得方法
- [ ] AndroidスマホからLED制御デバイスへの発信方法

### 運用上の課題

## 参考資料・リンク

### 技術文書
- [ ] Android Notification Listener Service

### 類似プロジェクト

## 更新履歴

| 日付 | バージョン | 更新内容 | 担当者 |
|------|-----------|---------|-------|
| 2025-09-12 | 0.1.0 | 初版作成 | - |
| 2025-09-13 | 0.2.0 | Android NotificationListener実装完了<br/>- NotificationListenerService<br/>- MainActivity (権限管理UI)<br/>- Pixel 4a動作確認<br/>- VS Codeリアルタイム監視<br/>- JSON通知データ生成 | GitHub Copilot |

---

## 次のステップ

1. **要件の詳細化**: 上記の各項目について具体的な要件を決定
2. **技術選定**: 開発言語・フレームワーク・ハードウェアの確定
3. **プロトタイプ開発**: 最小限の機能での動作確認
4. **詳細設計**: API設計・データベース設計等の詳細化

> **Note**: このテンプレートを元に、プロジェクトの進行に合わせて内容を更新・詳細化してください。