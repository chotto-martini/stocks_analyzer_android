# stocks_analyzer_android（あなすと）

## 概要
stocks_analyzer_android（以降「あなすと」）はとある証券会社の口座情報を取得し、Androidウェジェットとして表示するアプリケーションです。

## 背景
信用取引口座を開設した際、証券会社が提供している管理画面において信用余力がわかりにくかったため当アプリを作成しました。  
信用取引を行う上で、無理のない取引を行う補足情報を得るために利用することを目的としております。

## 動作検証
テスト環境不足のため、下記環境下でのみ動作検証を行っております。  
また、下記検証内容はアプリの動作を保証するものではありません。  
当アプリを利用し負った損害について作成者は一切責任を追いませんのでご了承ください。

- 証券会社の口座状態は「国内現物」「国内信用」口座を開設している状態で検証を行っております。
    - ※「国内現物」のみの場合、期待する動作をしない可能性があります。
- Android端末は「Sony SO-01K Android 8.0.0」で検証を行っております。
- ホームアプリは「Nova Launcher」を利用しており、ウィジェットのりサイズができる状態で確認をしております。
    - ※リサイズができないホームアプリを利用している場合、項目を全て表示出来ない可能性があります。

## スクリーンショット

- 左上「更新」ボタンを押下し、口座情報を取得します。
<img src="https://chotto-martini.github.io/stocks_analyzer_android/doc/img/Screenshot_20180429-185035.png" width="350px">

## ライセンス
Copyright 2018 chotto-martini This software is licensed under the Apache 2 license, quoted below.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this project except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
