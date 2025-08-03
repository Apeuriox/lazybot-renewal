# lazybot-renewal
---  
Lazybot is a feature-rich bot designed for osu! players, providing various powerful tools for tracking performance, visualizing scores, filtering and comparing statistics through ‌diverse‌ graphic designs. This project is a completely refactored version of the original Lazybot, with a new structure and improved performance, and it's still continuously developing


### About Image Generation

- We use SVG as template panels
- SVG setup: Apache Batik
- SVG rendering: Resvg (batik can render SVG into PNG too, but way slower)
---  

### Before exploring

This project was started by LazyChildren on March 27th, 2023, and he invited me to develop this project later on. So maybe you will ask why build a new bot from scratch when there are tons of functional ones? Cause we were bored at that time, that's the reason why Lazybot was born.
Since LazyChildren was busy with work, this refactor was mainly by me. codebase is not aimed at quality, but I will do optimization work sometimes.

### Used libraries that cant be retrieved from Maven central

* [Rosu-JNI by HollisMeynell](https://github.com/HollisMeynell)</br>  
  to install it, use the following command in maven:  
  ```mvn install:install-file -Dfile=lib/rosu-jni-0.1.8.jar -DgroupId=org.rosu -DartifactId=rosu-java -Dversion=0.1.8 -Dpackaging=jar```

* [Resvg-JNI by Zh_jk](https://github.com/fantasyzhjk)</br>  
  to install it, use the following command in maven:  
  ```mvn install:install-file -Dfile=lib/resvg-jni-0.1.4.jar -DgroupId=me.aloic -DartifactId=resvg-jni -Dversion=0.1.4 -Dpackaging=jar```
---  
### Development

#### Prerequisites
- Java 21
- A modern **Relational Database**
- Functional osu! client

#### Setup Environment

- Clone the project to your IDEs using `git clone https://github.com/Apeuriox/lazybot-renewal.git`
- Finish the application.yaml
- Run the script.sql to build the database tables
- Install the missing maven libraries

#### Optional Settings

- Environmental variable `LAZYBOT_DIR` was pointed to the folder where Lazybot releases its cache files, if not set, we will try to create a temporary folder via the operating system.
- Environmental variable `RESVG_DIR` was pointed to the folder where Resvg render process library cache, if not set, we will try to create a temporary folder via the operating system.

### Technical Support

- Active Developer: Aloic</br>
- Graphic design: Slimezz, Aloic</br>
- Developer: LazyChildren, Aloic  </br>
- Feasibility: LazyChildren, Marisaya, -Spring Night-, Zh_jk, ATRI1024</br>