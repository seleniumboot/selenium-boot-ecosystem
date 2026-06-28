package io.github.seleniumboot.idea.run;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.icons.AllIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

/**
 * Run configuration type: "Selenium Boot". Runs the project's tests through
 * Maven with Selenium-Boot-specific knobs (config profile, test filter).
 *
 * <p>This intentionally does <em>not</em> add gutter icons — IntelliJ already
 * provides run gutters for TestNG/JUnit methods. This type is for running a
 * whole suite with a chosen profile/headless setting.
 */
public final class SeleniumBootRunConfigurationType implements ConfigurationType {

    static final String ID = "SeleniumBootRunConfiguration";

    private final ConfigurationFactory factory = new SeleniumBootRunConfigurationFactory(this);

    @Override
    public @NotNull String getDisplayName() {
        return "Selenium Boot";
    }

    @Override
    public String getConfigurationTypeDescription() {
        return "Run Selenium Boot tests via Maven with a config profile and test filter.";
    }

    @Override
    public Icon getIcon() {
        return AllIcons.RunConfigurations.Junit;
    }

    @Override
    public @NotNull String getId() {
        return ID;
    }

    @Override
    public ConfigurationFactory @NotNull [] getConfigurationFactories() {
        return new ConfigurationFactory[]{factory};
    }
}
