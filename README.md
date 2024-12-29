# Hello

### Before exploring

it's just some random code from my old projects, not so good not so bad. Currently im doing refactoring so...</br>
it will release static files (svg, png, etc) from the jar package to environment variables `LAZYBOT_DIR`


### Used libraries that cant be retrieved from Maven central

* [Rosu-JNI by HollisMeynell](https://github.com/HollisMeynell)</br>
    to install it, use the following command in maven:
  ```mvn install:install-file -Dfile=lib/rosu-jni-all-jdk17.jar -DgroupId=org.rosu -DartifactId=rosu-java -Dversion=0.1.7 -Dpackaging=jar```

* [Resvg-JNI by Zh_jk](https://github.com/fantasyzhjk)</br>
    to install it, use the following command in maven:
```mvn install:install-file -Dfile=lib/resvg-jni-0.1.2.jar -DgroupId=me.aloic -DartifactId=resvg-jni -Dversion=0.1.2 -Dpackaging=jar```



