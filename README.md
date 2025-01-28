# coding-test


https://quo-digital.hatenablog.com/entry/2024/03/22/143542

### 開発環境

* windows 11 24H2 + WSL2 (Ubuntu 24.04.1 LTS)
* docker 27.4.1, build b9d17ea


### 実行手順

以下のコマンドでclone＆実行してください。
```
$ git clone https://github.com/s-f-934/coding-test.git
$ cd coding-test
$ docker compose up
```

#### 起動したらブラウザで以下のURLを開いてください。

` http://localhost:8080/`

#### swagger-uiが開くので、各種APIの動作を確認してください。

初期データとして、以下が存在します。
* book id=1
* author id=1

bookを追加する場合、まずauthorを追加して、authorのidを取得してから、book追加時にそのidを指定してください。

存在しないauthor idは指定不可です。

#### DBのデータを残して止める場合

`$ docker compose down`


#### DBのデータごと消す場合

`$ docker compose down --volumes`


以上です。
