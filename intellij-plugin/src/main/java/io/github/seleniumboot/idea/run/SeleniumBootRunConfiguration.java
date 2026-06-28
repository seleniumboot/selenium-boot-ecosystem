package io.github.seleniumboot.idea.run;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A Selenium Boot run configuration. Holds the Maven goals plus the
 * Selenium-Boot-specific knobs and serializes them into the workspace.
 */
@SuppressWarnings("rawtypes") // options bean not used; state is hand-serialized below
public final class SeleniumBootRunConfiguration extends RunConfigurationBase {

    private String goals = "test";
    private String profile = "";       // -Dselenium.boot.profile=<profile>  → selenium-boot-<profile>.yml
    private String configFile = "";    // -Dselenium.boot.config=<path>      → explicit config file
    private String testFilter = "";    // -Dtest=<filter>                    → surefire test selection
    private String mavenArgs = "";     // extra raw Maven arguments

    @SuppressWarnings("unchecked")
    SeleniumBootRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @Override
    public @NotNull SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new SeleniumBootSettingsEditor();
    }

    @Override
    public @Nullable RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
        return new SeleniumBootRunState(environment, this);
    }

    // --- persistence ------------------------------------------------------

    @Override
    public void readExternal(@NotNull Element element) throws com.intellij.openapi.util.InvalidDataException {
        super.readExternal(element);
        goals = orDefault(JDOMExternalizerUtil.readField(element, "goals"), "test");
        profile = orDefault(JDOMExternalizerUtil.readField(element, "profile"), "");
        configFile = orDefault(JDOMExternalizerUtil.readField(element, "configFile"), "");
        testFilter = orDefault(JDOMExternalizerUtil.readField(element, "testFilter"), "");
        mavenArgs = orDefault(JDOMExternalizerUtil.readField(element, "mavenArgs"), "");
    }

    @Override
    public void writeExternal(@NotNull Element element) throws com.intellij.openapi.util.WriteExternalException {
        super.writeExternal(element);
        JDOMExternalizerUtil.writeField(element, "goals", goals);
        JDOMExternalizerUtil.writeField(element, "profile", profile);
        JDOMExternalizerUtil.writeField(element, "configFile", configFile);
        JDOMExternalizerUtil.writeField(element, "testFilter", testFilter);
        JDOMExternalizerUtil.writeField(element, "mavenArgs", mavenArgs);
    }

    private static String orDefault(String value, String fallback) {
        return value == null ? fallback : value;
    }

    // --- getters / setters ------------------------------------------------

    public String getGoals() { return goals; }
    public void setGoals(String goals) { this.goals = goals; }

    public String getProfile() { return profile; }
    public void setProfile(String profile) { this.profile = profile; }

    public String getConfigFile() { return configFile; }
    public void setConfigFile(String configFile) { this.configFile = configFile; }

    public String getTestFilter() { return testFilter; }
    public void setTestFilter(String testFilter) { this.testFilter = testFilter; }

    public String getMavenArgs() { return mavenArgs; }
    public void setMavenArgs(String mavenArgs) { this.mavenArgs = mavenArgs; }
}
