package io.github.seleniumboot.idea.wizard;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Pure file-content generation for a new Selenium Boot project. No IntelliJ
 * Platform types here so it stays trivially unit-testable: given options and a
 * root directory, it writes a runnable starter project.
 */
final class ProjectScaffold {

    /** Selenium Boot release the wizard pins generated projects to. */
    static final String DEFAULT_SELENIUM_BOOT_VERSION = "3.1.1";

    /** User-supplied options collected by the wizard step. */
    static final class Options {
        String groupId = "com.example";
        String artifactId = "selenium-boot-tests";
        String baseUrl = "https://example.com";
        String browser = "chrome";
        boolean headless = false;
        String seleniumBootVersion = DEFAULT_SELENIUM_BOOT_VERSION;

        String basePackage() {
            return sanitizePackage(groupId);
        }
    }

    private ProjectScaffold() {}

    /** Writes the full project tree under {@code root}. */
    static void generate(Path root, Options o) throws IOException {
        String pkg = o.basePackage();
        String pkgPath = pkg.replace('.', '/');

        write(root.resolve("pom.xml"), pom(o));
        write(root.resolve("selenium-boot.yml"), seleniumBootYml(o));
        write(root.resolve("testng.xml"), testngXml(pkg));
        write(root.resolve(".gitignore"), gitignore());
        write(root.resolve("README.md"), readme(o));

        Path testJava = root.resolve("src/test/java/" + pkgPath);
        write(testJava.resolve("pages/LoginPage.java"), loginPage(pkg));
        write(testJava.resolve("tests/LoginTest.java"), loginTest(pkg));

        // Keep the (empty) main source root so the module layout is conventional.
        Files.createDirectories(root.resolve("src/main/java"));
    }

    private static void write(Path file, String content) throws IOException {
        Files.createDirectories(file.getParent());
        Files.writeString(file, content, StandardCharsets.UTF_8);
    }

    // --- templates --------------------------------------------------------

    private static String pom(Options o) {
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                <modelVersion>4.0.0</modelVersion>

                <groupId>%s</groupId>
                <artifactId>%s</artifactId>
                <version>1.0-SNAPSHOT</version>

                <properties>
                    <maven.compiler.release>17</maven.compiler.release>
                    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
                    <selenium-boot.version>%s</selenium-boot.version>
                </properties>

                <dependencies>
                    <!-- The one dependency. TestNG, Selenium and the rest come transitively. -->
                    <dependency>
                        <groupId>io.github.seleniumboot</groupId>
                        <artifactId>selenium-boot</artifactId>
                        <version>${selenium-boot.version}</version>
                    </dependency>
                </dependencies>

                <build>
                    <plugins>
                        <plugin>
                            <groupId>org.apache.maven.plugins</groupId>
                            <artifactId>maven-surefire-plugin</artifactId>
                            <version>3.2.5</version>
                            <configuration>
                                <suiteXmlFiles>
                                    <suiteXmlFile>testng.xml</suiteXmlFile>
                                </suiteXmlFiles>
                            </configuration>
                        </plugin>
                    </plugins>
                </build>
            </project>
            """.formatted(o.groupId, o.artifactId, o.seleniumBootVersion);
    }

    private static String seleniumBootYml(Options o) {
        return """
            # Selenium Boot configuration. Everything here is optional — sensible
            # defaults apply when omitted. Editing this file in IntelliJ gives you
            # completion and validation (Selenium Boot plugin).
            execution:
              mode: local
              baseUrl: %s

            browser:
              name: %s
              headless: %s

            retry:
              enabled: true
              maxAttempts: 2

            timeouts:
              explicit: 10
              pageLoad: 30
            """.formatted(o.baseUrl, o.browser, o.headless);
    }

    private static String testngXml(String pkg) {
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
            <suite name="Selenium Boot Suite">
                <test name="Smoke">
                    <packages>
                        <package name="%s.tests"/>
                    </packages>
                </test>
            </suite>
            """.formatted(pkg);
    }

    private static String loginPage(String pkg) {
        return """
            package %s.pages;

            import com.seleniumboot.test.BasePage;
            import org.openqa.selenium.By;
            import org.openqa.selenium.WebDriver;

            /**
             * Example page object. Extend BasePage and use its helpers
             * (click, type, getText, …) — never touch WebDriver waits yourself.
             */
            public class LoginPage extends BasePage {

                private final By username = By.id("username");
                private final By password = By.id("password");
                private final By submit   = By.cssSelector("button[type='submit']");

                public LoginPage(WebDriver driver) {
                    super(driver);
                }

                public void login(String user, String pass) {
                    type(username, user);
                    type(password, pass);
                    click(submit);
                }
            }
            """.formatted(pkg);
    }

    private static String loginTest(String pkg) {
        return """
            package %s.tests;

            import com.seleniumboot.test.BaseTest;
            import org.testng.annotations.Test;

            /**
             * Example test. Extend BaseTest; the framework manages the driver,
             * waits and retries. open(path) navigates relative to execution.baseUrl.
             */
            public class LoginTest extends BaseTest {

                @Test
                public void openHomePage() {
                    open("/");
                    assertThat($("body")).isVisible();
                }
            }
            """.formatted(pkg);
    }

    private static String gitignore() {
        return """
            target/
            .idea/
            *.iml
            test-output/
            selenium-boot-report/
            """;
    }

    private static String readme(Options o) {
        return """
            # %s

            Selenium Boot test project. Run the suite with:

            ```bash
            mvn test
            ```

            Configuration lives in `selenium-boot.yml`. Tests extend
            `com.seleniumboot.test.BaseTest`; page objects extend `BasePage`.
            Generated against Selenium Boot %s.
            """.formatted(o.artifactId, o.seleniumBootVersion);
    }

    // --- helpers ----------------------------------------------------------

    /** Turns a Maven groupId into a valid Java package, segment by segment. */
    static String sanitizePackage(String groupId) {
        if (groupId == null || groupId.isBlank()) {
            return "com.example";
        }
        String[] parts = groupId.trim().toLowerCase().split("\\.");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            String seg = part.replaceAll("[^a-z0-9_]", "");
            if (seg.isEmpty()) continue;
            if (Character.isDigit(seg.charAt(0))) seg = "_" + seg;
            if (sb.length() > 0) sb.append('.');
            sb.append(seg);
        }
        return sb.length() == 0 ? "com.example" : sb.toString();
    }
}
