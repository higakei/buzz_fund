#!/bin/sh

#変数定義
BINDIR=`dirname $0`
HOME=$BINDIR/..
JAVA_HOME=/usr/java/latest
export LANG=ja_JP.UTF-8

#クラスパス設定
CLASSPATH=$HOME/conf:$HOME/crawler/conf
for i in `ls -al $HOME/lib/*.jar | grep ^\- | sed -e "s/.* \(.*\)/\1/"`
do
CLASSPATH="$CLASSPATH:$i";
done
export CLASSPATH=$CLASSPATH

#実行
$JAVA_HOME/bin/java jp.co.hottolink.buzzfund.crawler.FxRateCrawler $*
