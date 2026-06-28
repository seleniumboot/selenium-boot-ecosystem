package io.github.seleniumboot.idea.run;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/** Creates {@link SeleniumBootRunConfiguration} instances. */
public final class SeleniumBootRunConfigurationFactory extends ConfigurationFactory {

    SeleniumBootRunConfigurationFactory(ConfigurationType type) {
        super(type);
    }

    @Override
    public @NotNull String getId() {
        return SeleniumBootRunConfigurationType.ID;
    }

    @Override
    public @NotNull RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new SeleniumBootRunConfiguration(project, this, "Selenium Boot");
    }
}
