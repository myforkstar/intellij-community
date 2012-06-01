package com.jetbrains.python.configuration;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.options.UnnamedConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Splitter;
import com.intellij.platform.ModuleAttachProcessor;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.jetbrains.python.run.PyModuleRenderer;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yole
 */
public abstract class ModuleAwareProjectConfigurable<T extends UnnamedConfigurable> implements SearchableConfigurable {
  private final Project myProject;
  private final String myDisplayName;
  private final String myHelpTopic;
  private final Map<Module, T> myModuleConfigurables = new HashMap<Module, T>();

  public ModuleAwareProjectConfigurable(Project project, String displayName, String helpTopic) {
    myProject = project;
    myDisplayName = displayName;
    myHelpTopic = helpTopic;
  }

  @Nls
  @Override
  public String getDisplayName() {
    return myDisplayName;
  }

  @Override
  public String getHelpTopic() {
    return myHelpTopic;
  }

  @Override
  public JComponent createComponent() {
    if (myProject.isDefault()) {
      T configurable = createDefaultProjectConfigurable();
      if (configurable != null) {
        myModuleConfigurables.put(null, configurable);
        return configurable.createComponent();
      }
    }
    final List<Module> modules = ModuleAttachProcessor.getSortedModules(myProject);
    if (modules.size() == 1) {
      Module module = modules.get(0);
      final T configurable = createModuleConfigurable(module);
      myModuleConfigurables.put(module, configurable);
      return configurable.createComponent();
    }
    final Splitter splitter = new Splitter(false, 0.25f);
    final JBList moduleList = new JBList(new CollectionListModel<Module>(modules));
    moduleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    moduleList.setCellRenderer(new PyModuleRenderer(moduleList.getCellRenderer()));
    splitter.setFirstComponent(new JBScrollPane(moduleList));
    final CardLayout layout = new CardLayout();
    final JPanel cardPanel = new JPanel(layout);
    splitter.setSecondComponent(cardPanel);
    for (Module module : modules) {
      final T configurable = createModuleConfigurable(module);
      myModuleConfigurables.put(module, configurable);
      final JComponent component = configurable.createComponent();
      cardPanel.add(component, module.getName());
    }
    moduleList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        final Module value = (Module)moduleList.getSelectedValue();
        layout.show(cardPanel, value.getName());
      }
    });
    if (modules.size() > 0) {
      moduleList.setSelectedIndex(0);
      layout.show(cardPanel, modules.get(0).getName());
    }
    return splitter;
  }

  @Nullable
  protected T createDefaultProjectConfigurable() {
    return null;
  }

  protected abstract T createModuleConfigurable(Module module);

  @Override
  public boolean isModified() {
    for (T configurable : myModuleConfigurables.values()) {
      if (configurable.isModified()) return true;
    }
    return false;
  }

  @Override
  public void apply() throws ConfigurationException {
    for (T configurable : myModuleConfigurables.values()) {
      configurable.apply();
    }
  }

  @Override
  public void reset() {
    for (T configurable : myModuleConfigurables.values()) {
      configurable.reset();
    }
  }

  @Override
  public void disposeUIResources() {
    for (T configurable : myModuleConfigurables.values()) {
      configurable.disposeUIResources();
    }
    myModuleConfigurables.clear();
  }

  @NotNull
  @Override
  public String getId() {
    return getClass().getName();
  }

  @Override
  public Runnable enableSearch(String option) {
    return null;
  }
}
