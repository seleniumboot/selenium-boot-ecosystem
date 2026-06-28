package io.github.seleniumboot.idea.run;

import com.intellij.openapi.options.SettingsEditor;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import javax.swing.JPanel;

/** Settings UI for {@link SeleniumBootRunConfiguration}. */
public final class SeleniumBootSettingsEditor extends SettingsEditor<SeleniumBootRunConfiguration> {

    private final JBTextField goals = new JBTextField();
    private final JBTextField profile = new JBTextField();
    private final JBTextField configFile = new JBTextField();
    private final JBTextField testFilter = new JBTextField();
    private final JBTextField mavenArgs = new JBTextField();

    private final JPanel panel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Maven goals:", goals)
            .addLabeledComponent("Config profile:", profile)
            .addTooltip("Sets -Dselenium.boot.profile — loads selenium-boot-<profile>.yml")
            .addLabeledComponent("Config file:", configFile)
            .addTooltip("Sets -Dselenium.boot.config — explicit selenium-boot.yml path (overrides profile)")
            .addLabeledComponent("Test filter:", testFilter)
            .addTooltip("Sets -Dtest — e.g. LoginTest or LoginTest#opensHomePage")
            .addLabeledComponent("Extra Maven args:", mavenArgs)
            .addComponentFillVertically(new JPanel(), 0)
            .getPanel();

    @Override
    protected void resetEditorFrom(@NotNull SeleniumBootRunConfiguration s) {
        goals.setText(s.getGoals());
        profile.setText(s.getProfile());
        configFile.setText(s.getConfigFile());
        testFilter.setText(s.getTestFilter());
        mavenArgs.setText(s.getMavenArgs());
    }

    @Override
    protected void applyEditorTo(@NotNull SeleniumBootRunConfiguration s) {
        s.setGoals(goals.getText().trim());
        s.setProfile(profile.getText().trim());
        s.setConfigFile(configFile.getText().trim());
        s.setTestFilter(testFilter.getText().trim());
        s.setMavenArgs(mavenArgs.getText().trim());
    }

    @Override
    protected @NotNull JComponent createEditor() {
        return panel;
    }
}
