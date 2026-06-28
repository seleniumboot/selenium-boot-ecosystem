package io.github.seleniumboot.idea.wizard;

import com.intellij.icons.AllIcons;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * "Selenium Boot" entry in the New Project wizard. Collects a few options, then
 * scaffolds a runnable Maven + TestNG project on finish.
 *
 * <p>Registered via the {@code com.intellij.moduleBuilder} extension point.
 */
public final class SeleniumBootModuleBuilder extends ModuleBuilder {

    private final ProjectScaffold.Options options = new ProjectScaffold.Options();

    @Override
    public ModuleType<?> getModuleType() {
        return JavaModuleType.getModuleType();
    }

    @Override
    public String getPresentableName() {
        return "Selenium Boot";
    }

    @Override
    public String getDescription() {
        return "Zero-boilerplate Selenium automation project: Maven + TestNG, "
                + "a sample page object and test, and a ready selenium-boot.yml.";
    }

    @Override
    public Icon getNodeIcon() {
        return AllIcons.Nodes.Module;
    }

    @Override
    public String getGroupName() {
        return "Selenium Boot";
    }

    @Override
    public @Nullable ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        return new SeleniumBootWizardStep(options);
    }

    @Override
    public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {
        String contentPath = getContentEntryPath();
        if (contentPath == null) {
            return;
        }
        rootModel.inheritSdk();

        Path root = Paths.get(contentPath);
        try {
            ProjectScaffold.generate(root, options);
        } catch (IOException e) {
            throw new ConfigurationException("Failed to scaffold Selenium Boot project: " + e.getMessage());
        }

        VirtualFile rootDir = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(root);
        if (rootDir == null) {
            return;
        }
        rootDir.refresh(false, true);

        ContentEntry contentEntry = rootModel.addContentEntry(rootDir);
        VirtualFile testRoot = rootDir.findFileByRelativePath("src/test/java");
        if (testRoot != null) {
            contentEntry.addSourceFolder(testRoot, /* isTestSource = */ true);
        }
        VirtualFile mainRoot = rootDir.findFileByRelativePath("src/main/java");
        if (mainRoot != null) {
            contentEntry.addSourceFolder(mainRoot, /* isTestSource = */ false);
        }
    }
}
