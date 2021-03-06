// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.editorconfig.configmanagement.create;

import com.intellij.lang.Language;
import com.intellij.openapi.ui.DialogWrapper;
import org.editorconfig.configmanagement.extended.EditorConfigPropertyKind;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.EnumSet;
import java.util.List;

public class CreateEditorConfigDialog extends DialogWrapper {

  private CreateEditorConfigForm myForm;

  protected CreateEditorConfigDialog() {
    super(false);
    init();
    setTitle("New EditorConfig File");
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    myForm = new CreateEditorConfigForm();
    return myForm.getTopPanel();
  }

  public EditorConfigPropertyKind[] getPropertyKinds() {
    EnumSet<EditorConfigPropertyKind> kinds = EnumSet.noneOf(EditorConfigPropertyKind.class);
    if (myForm.isStandardProperties()) {
      kinds.add(EditorConfigPropertyKind.EDITOR_CONFIG_STANDARD);
    }
    if (myForm.isIntelliJProperties()) {
      kinds.add(EditorConfigPropertyKind.LANGUAGE);
      kinds.add(EditorConfigPropertyKind.GENERIC);
      kinds.add(EditorConfigPropertyKind.COMMON);
    }
    return kinds.toArray(new EditorConfigPropertyKind[0]);
  }

  public boolean isRoot() {
    return myForm.isRoot();
  }

  public List<Language> getLanguages() {
    return myForm.getSelectedLanguages();
  }
}
