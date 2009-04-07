package com.intellij.historyIntegrTests.ui;

import com.intellij.history.core.revisions.RecentChange;
import com.intellij.history.integration.revertion.Reverter;
import com.intellij.history.integration.ui.models.RecentChangeDialogModel;
import com.intellij.history.integration.ui.views.RecentChangeDialog;
import com.intellij.historyIntegrTests.IntegrationTestCase;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;

public class RecentChangeDialogTest extends IntegrationTestCase {
  public void testDialogWork() throws IOException {
    getVcs().beginChangeSet();
    root.createChildData(null, "f.txt");
    getVcs().endChangeSet("change");

    RecentChange c = getVcs().getRecentChanges().get(0);
    RecentChangeDialog d = null;

    try {
      d = new RecentChangeDialog(gateway, c);
    }
    finally {
      if (d != null) d.close(0);
    }
  }

  public void testRevertChange() throws IOException {
    getVcs().beginChangeSet();
    VirtualFile f = root.createChildData(null, "f1.txt");
    getVcs().endChangeSet("change");

    getVcs().beginChangeSet();
    f.rename(null, "f2.txt");
    getVcs().endChangeSet("another change");

    RecentChange c = getVcs().getRecentChanges().get(1);
    RecentChangeDialogModel m = new RecentChangeDialogModel(gateway, getVcs(), c);

    Reverter r = m.createReverter();
    r.revert();

    assertNull(root.findChild("f1.txt"));
    assertNull(root.findChild("f2.txt"));
  }
}