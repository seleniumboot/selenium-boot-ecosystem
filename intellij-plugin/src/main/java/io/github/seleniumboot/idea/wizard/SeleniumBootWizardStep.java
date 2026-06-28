package io.github.seleniumboot.idea.wizard;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Single options page shown in the New Project wizard for a Selenium Boot
 * project. Collects the handful of values the scaffold needs and writes them
 * back onto the builder's {@link ProjectScaffold.Options} in
 * {@link #updateDataModel()}.
 */
final class SeleniumBootWizardStep extends ModuleWizardStep {

    private final ProjectScaffold.Options options;

    private final JBTextField groupId = new JBTextField();
    private final JBTextField artifactId = new JBTextField();
    private final JBTextField baseUrl = new JBTextField();
    private final ComboBox<String> browser = new ComboBox<>(new String[]{"chrome", "firefox", "edge", "safari"});
    private final JBCheckBox headless = new JBCheckBox("Run headless");
    private final JBTextField version = new JBTextField();

    private final JPanel root;

    SeleniumBootWizardStep(ProjectScaffold.Options options) {
        this.options = options;

        groupId.setText(options.groupId);
        artifactId.setText(options.artifactId);
        baseUrl.setText(options.baseUrl);
        browser.setSelectedItem(options.browser);
        headless.setSelected(options.headless);
        version.setText(options.seleniumBootVersion);

        this.root = FormBuilder.createFormBuilder()
                .addLabeledComponent("Group ID:", groupId)
                .addLabeledComponent("Artifact ID:", artifactId)
                .addLabeledComponent("Base URL:", baseUrl)
                .addLabeledComponent("Browser:", browser)
                .addComponentToRightColumn(headless)
                .addLabeledComponent("Selenium Boot version:", version)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    @Override
    public JComponent getComponent() {
        return root;
    }

    @Override
    public boolean validate() throws ConfigurationException {
        if (groupId.getText().isBlank()) {
            throw new ConfigurationException("Group ID must not be empty.");
        }
        if (artifactId.getText().isBlank()) {
            throw new ConfigurationException("Artifact ID must not be empty.");
        }
        if (version.getText().isBlank()) {
            throw new ConfigurationException("Selenium Boot version must not be empty.");
        }
        return true;
    }

    @Override
    public void updateDataModel() {
        options.groupId = groupId.getText().trim();
        options.artifactId = artifactId.getText().trim();
        options.baseUrl = baseUrl.getText().trim();
        options.browser = String.valueOf(browser.getSelectedItem());
        options.headless = headless.isSelected();
        options.seleniumBootVersion = version.getText().trim();
    }
}
