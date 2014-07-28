# このコードについて

Play frameworkのsbt runで実行した際に、  
依存するJarライブラリでScalaリフレクションを使用している場合におこるバグ?(仕様とも言えるレベル)を再現するためのコードです

# 説明

現象として、TypeTag#mirrorからMirrorを取得した際に、  
Play frameworkのプロジェクト中からは、ReloadableClassLoaderをベースにしたMirrorを取得できますが、  
依存ライブラリ内からは、PlayDependencyClassLoaderをベースにしたMirrorが取得されます。

そのため、ライブラリ中でPlay frameworkのプロジェクト中のクラスを参照しようとした場合に

    ScalaReflectionException: class controllers.User in JavaMirror with PlayDependencyClassLoader
    
の例外が発生します。これは、PlayDependencyClassLoaderに、ライブラリから上(今回の場合はserverプロジェクト)のクラスパスが含まれていないためになります。

# 試し方

"sbt run"で実行をし、

[http://localhost:9000/ok_pattern](http://localhost:9000/ok_pattern)  
と  
[http://localhost9000/ng_pattern](http://localhost9000/ng_pattern)  
にアクセスすると、コード的にはほとんど一緒ですが、TypeTagの取得タイミングが異なるために、上に書いてある現象が発生します。


ただし、"sbt start"で実行した場合には、ClassLoaderが一種類しか生成されないため上記の問題は発生しなくなります。  
また、sbt上の.dependsOnで依存を指定した場合も、上記の問題は発生しなくなります。


#解決方法

GoodWayToGetMirrorに定義しているように

* Thread.currentThread.getContextLoaderからMirrorを作る
* 渡されたTypeTagのmirrorを引き回す

などの方法があります。基本的には前者をおすすめします。