util
=================

[![Maven Central](https://img.shields.io/maven-central/v/com.uchicom/util.svg)](http://search.maven.org/#search|ga|1|com.uchicom.util)
[![Maven Central](https://img.shields.io/github/license/uchicom/util.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)
[![Java CI with Maven](https://github.com/uchicom/util/actions/workflows/package.yml/badge.svg)](https://github.com/uchicom/util/actions/workflows/package.yml)

サーバーでも画面でもどちらでも使用できるユーティリティ

## Maven座標

```xml
　<dependency>
　　<groupId>com.uchicom</groupId>
　　<artifactId>util</artifactId>
　　<version>1.6.6</version>
　</dependency>
```

Java 21以上が必要です。モジュール名は `com.uchicom.util` です。

## クラス概要と使用例

### Parameter

コマンドライン引数を解釈します。

```java
var param = new Parameter(args); // 例: -port 8080 -verbose -from 2025-01-01 dir
int port = param.getInt("port", 80);
boolean verbose = param.is("verbose");
LocalDate from = param.getLocalDate("from");
Path path = param.getPath("path", Path.of("."));
```

- `-key value` でキーに値を登録します。同じキーは後勝ちです。
- 値の位置の引数が `-` で始まる場合、負数(`-5`, `-1.5`)のときだけ値として扱います。それ以外は次のキーとみなし、直前のキーはフラグになります。
- 値のないフラグ(`-verbose`)は、キー自身を値として登録します。
- `-` で始まらない引数 `dir` は、キー・値とも `dir` のフラグとして登録します。

### Numbre

`"1,3,5-7"` 形式の文字列を数値のリストに展開します。

```java
Numbre.expand("1,3,5-7");     // [1, 3, 5, 6, 7]
Numbre.expandLong("1-3");     // [1, 2, 3] (List<Long>)
```

不正な形式、範囲の逆転、展開数が `Numbre.MAX_EXPAND_SIZE`(1,000,000)を超える場合は `IllegalArgumentException` になります。`expand` は int の範囲外でも同様です。

### EnvProperties / ResourceUtil

`ResourceUtil` はpropertiesファイルを読み込み、`EnvProperties` は設定値を環境変数やシステムプロパティで上書きできます。

```java
var props = ResourceUtil.createEnvProperties(new File("app.properties"), "UTF-8");
String url = props.getProperty("db.url");
```

優先順位: **システムプロパティ(`-Ddb.url=...`) > 環境変数(`DB_URL`) > propertiesファイル**

注意点:

- 上書きは `load` 時に解決され、解決後の値が保持されます。load後にシステムプロパティや環境変数を変更しても反映されません。
- 上書き対象は **propertiesファイルに存在するキーのみ** です。ファイルにないキーは環境変数・システムプロパティがあっても取得できません。
- 環境変数名はキーの `.` を `_` に置換して大文字にしたものです(`db.url` → `DB_URL`)。
- 空文字・空白のみの値は無視され、下位の値が使われます。
- `ResourceUtil` はファイルが存在しない場合、空のPropertiesを返します。

### DailyRollingFileHandler

日付ごとにログファイルを切り替える `java.util.logging` のハンドラです。`logging.properties` の例:

```properties
handlers = com.uchicom.util.logging.DailyRollingFileHandler
com.uchicom.util.logging.DailyRollingFileHandler.logDir = logs
com.uchicom.util.logging.DailyRollingFileHandler.fileNameFormat = app-%d.log
com.uchicom.util.logging.DailyRollingFileHandler.level = INFO
```

| プロパティ | 必須 | 既定値 |
|---|---|---|
| `logDir` | 必須 | - |
| `logFormat` | | `%1$tF %1$tT [%4$s] %2$s %5$s%6$s%n` |
| `fileNameFormat` | | `app-%d.log` (`%d` は `yyyy-MM-dd`) |
| `zoneId` | | システムのタイムゾーン |
| `level` | | `INFO` |

ログ切替に失敗した場合は旧ファイルへの出力を続け、次回の出力時に再試行します。

### Throwing系関数インターフェース

`ThrowingFunction`, `ThrowingBiFunction`, `ThrowingConsumer`, `ThrowingSupplier`, `ThrowingRunnable` は検査例外をスローできる関数型インターフェースです。

## ライセンス

[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)
