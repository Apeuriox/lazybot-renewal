# Lazybot

Lazybot is a multi-platform osu! bot for OneBot and Discord. Providing various powerful tools for tracking performance, visualizing scores, filtering and comparing statistics through diverse graphic designs.

This repository is a full rewrite version of the original Lazybot, with a new architecture and improved performance, and it's still continuously developing...


## About image generation

Score panels and other graphics start as SVG templates. Apache Batik is used to
manipulate the SVG DOM, while Resvg-JNI performs the final PNG/JPEG rendering.
We are slowly migrating Apache Batik to Template Engine.

Static resources are extracted into Lazybot's working directory when the
application starts. We're migrating rasterization pipeline into new side project but it is not finished at this moment.


## Partial result showcase
including different variations. Remember, some designs are not original.

### Score Panel

#### Dark Mode, the default score panel, supports all modes. Designed by Slayemus and improved by Aloic
<img width="1280" height="720" alt="lazybot-score-panel-01" src="https://github.com/user-attachments/assets/072ef1c7-d1c6-4c78-98dd-d061a75d28e4" />

#### White Mode, our first score panel design. Designed by Slayemus and improved by Aloic
<img width="1280" height="720" alt="lazybot-score-panel-02" src="https://github.com/user-attachments/assets/d28161c0-a798-4438-b3f9-04d8c7ee83f1" />

#### Material 3 style; it is not finished. Designed by Slayemus.
<img width="1525" height="858" alt="lazybot-score-panel-03" src="https://github.com/user-attachments/assets/28c8b87e-305f-4bd2-a7e5-684c440eb557" />

#### Quadra Grid, the score panel that is specifically designed to display PP+ statistics. Designed by Slayemus and improved by Aloic.
<img width="1280" height="720" alt="lazybot-score-panel-04" src="https://github.com/user-attachments/assets/919d4bec-f01f-454e-8e80-613520338f16" />

#### Marathon Style, for 2026 April Fools. Designed by Aloic, originally from Marathon.
<img width="1280" height="777" alt="lazybot-score-panel-05" src="https://github.com/user-attachments/assets/f6eb03a7-f067-4657-9351-96d7635080a9" />

#### Small Plus Score, a small panel to display only PP+ statistics. Designed by Aloic.
<img width="640" height="824" alt="3QYWTD}TC0UP@2)K%53~7BF_tmb" src="https://github.com/user-attachments/assets/842bfe1a-f092-4dde-80bd-084b0f94b0f6" />

### User Profiles

#### Profile, the main Profile panel where users can customize their background with responsive colors. Designed by Slayemus and improved by Aloic.
<img width="1280" height="674" alt="lazybot-profile" src="https://github.com/user-attachments/assets/cae7a965-06a4-4a7d-a300-785c26f055c8" />

#### Card, the card form of profile, including PP+ statistics; its colors adapt to the user's avatar. Designed by Aloic.
<img width="640" height="602" alt="lazybot-moelleux" src="https://github.com/user-attachments/assets/1894ae81-5774-4428-8c7a-89901af01462" />

#### Plus Card, the card to display detailed PP+ stats, including PP+ statistics; its colors adapt to the user's avatar. Design originally from Corsace Open 2024.
<img width="510" height="990" alt="lazybot-pluscard" src="https://github.com/user-attachments/assets/ce0d40e9-2dcc-46f3-b505-5b9730305dd5" />

#### Plus Card Old, same as Plus Card, but inspired by Marathon; its colors use the user's hue settings from Bancho. Designed by Aloic.
<img width="600" height="800" alt="BQ 58@1Y@CRCNPNVBD F9 L_tmb" src="https://github.com/user-attachments/assets/3cd6a4a0-8fa9-4c56-a550-06844caece99" />

#### Info Card, small card cut from Marathon Score, with some small tweaks; its colors adapt to the user's avatar. Designed by Aloic.
<img width="633" height="1080" alt="$%Q %PZ$36O$K6SP_W7G~RM_tmb" src="https://github.com/user-attachments/assets/b4ca1c19-6c47-4fd0-94e7-76d6864bf9c2" />

#### Trimmed Card, banner size card for user to display in their osu! website profile; its colors adapt to the user's avatar. Designed by Aloic.
<img width="1760" height="800" alt="image" src="https://github.com/user-attachments/assets/717a184a-fa0e-4a81-b574-3ba143e6d939" />


### Score List

#### Score List Card, Designed by Aloic.
<img width="850" height="920" alt="lazybot-bpcard" src="https://github.com/user-attachments/assets/8a863ef9-0ebc-44d8-bce8-785cf20b1e40" />

#### Score List Card, Designed by Aloic.
<img width="1000" height="1330" alt="score-list-card" src="https://github.com/user-attachments/assets/3f12738a-5fcb-4cda-b883-ec95a794d5dc" />

#### Map Score List, Designed by Aloic.
<img width="800" height="1525" alt="lazybot-mapscore" src="https://github.com/user-attachments/assets/3ae0f50d-56be-455e-9552-dccb7cc492f3" />

### Analysis
#### Beatmap Statistics, to see some PP statistics of a beatmap. Designed by Aloic.
<img width="960" height="540" alt="bs" src="https://github.com/user-attachments/assets/3c5d807c-eadc-49ed-aa5c-d9b38955ef9a" />

#### Beatmap Statistics Plus, even more detailed statistics, including historical data. Designed by Aloic.
<img width="640" height="695" alt="bsp" src="https://github.com/user-attachments/assets/6afd70ec-f58c-4db9-8990-d90dd6bb8083" />

### Others
### Command Usage, to see some basic internal command invoke data. Designed by Aloic
<img width="1280" height="639" alt="lazybot-command-usage" src="https://github.com/user-attachments/assets/11b5a29a-7e24-41ce-8442-d14c71841d30" />

### Single Command Help Page, detailed help page for a single command. Designed by Aloic
<img width="640" height="896" alt="help-single" src="https://github.com/user-attachments/assets/1fc6ad33-7453-4f76-b2d6-a170c07c74e9" />

### Total Help Page, list all commands. Designed by Aloic
<img width="1000" height="2301" alt="HELP" src="https://github.com/user-attachments/assets/d542c0e2-838e-4d4c-b33b-3bdbde008695" />

### Mod Wiki, Designed by Aloic
<img width="500" height="308" alt="mod" src="https://github.com/user-attachments/assets/b8ec9ee6-4a0c-4010-b57a-b17bc8fc1f51" />


##
## Prerequisites

- JRE 25
- MySQL
- An osu! OAuth application and API credentials

## Bundled Maven dependencies

Some native or project-specific libraries are not published to Maven Central:

| Maven coordinate                                                          | Purpose |
|---------------------------------------------------------------------------| --- |
| [`me.aloic:rosu-pp-java:0.0.3`](https://github.com/Apeuriox/rosu-pp-java) | FFM binding for versioned osu! difficulty and performance calculation |
| [`me.aloic:resvg-jni:0.1.4`](https://github.com/Apeuriox/resvg-jni)       | Native SVG rendering |
| `me.zhjk:rosu-ppplus:0.2.2`                                               | PP+ integration |

These artifacts are already included in [`local-maven-repo`](local-maven-repo)
using the standard Maven repository layout. The repository is declared in
[`pom.xml`](pom.xml), so Maven resolves them automatically. **Do not run
`mvn install:install-file`; no manual dependency installation is required.**

## Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/Apeuriox/lazybot-jda.git
   cd lazybot-jda
   ```

2. Create the local configuration:

   ```bash
   cp src/main/resources/application.yaml.template \
      src/main/resources/application.yaml
   ```

   On Windows PowerShell:

   ```powershell
   Copy-Item src/main/resources/application.yaml.template `
       src/main/resources/application.yaml
   ```

   Fill in the MySQL connection, osu! OAuth credentials, callback URL, and the
   platform credentials you intend to use. The generated
   `application.yaml` is ignored by Git and must not be committed.

3. Create the database schema using [`script.sql`](script.sql).

4. Start the application:

   ```bash
   ./mvnw spring-boot:run
   ```

   On Windows:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

## Build and run

Build without tests:

```bash
./mvnw clean package -DskipTests
```

Run tests:

```bash
./mvnw test
```

The Spring Boot Maven configuration already enables Java preview features and
native access. When starting the packaged JAR directly, supply the same flags:

```bash
java --enable-preview --enable-native-access=ALL-UNNAMED \
     -jar target/lazybot-1.2.0.jar
```

`--enable-native-access=ALL-UNNAMED` is required by the FFM-based PP calculator
and prevents restricted native-access warnings from `SymbolLookup`.

## Runtime directories

- `LAZYBOT_DIR` controls where extracted templates, fonts, images, beatmaps, and
  other runtime resources are stored. If unset, Lazybot creates a temporary
  working directory.
- `RESVG_DIR` controls the Resvg native-library cache directory. If unset, the
  operating system's temporary directory is used.

## Project background

Lazybot was started by LazyChildren on March 27, 2023, who later invited Aloic
to join the project. This rewrite is primarily maintained by Aloic and continues
to evolve alongside osu! API and performance-algorithm changes.

## Contributors and special thanks

- Active developer: Aloic
- Developers: LazyChildren, Aloic
- Graphic design: Slimezz, Aloic
- Feasibility and testing: LazyChildren, Marisaya, -Spring Night-, [Zh_jk](https://github.com/fantasyzhjk),
  ATRI1024

## License

This project is licensed under the [GNU General Public License v3.0](LICENSE).
