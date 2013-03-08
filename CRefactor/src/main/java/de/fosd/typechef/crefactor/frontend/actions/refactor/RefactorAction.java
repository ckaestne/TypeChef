package de.fosd.typechef.crefactor.frontend.actions.refactor;


import de.fosd.typechef.crefactor.Morpheus;
import de.fosd.typechef.crefactor.backend.refactor.InlineFunction;
import de.fosd.typechef.crefactor.backend.refactor.RenameIdentifier;
import de.fosd.typechef.crefactor.frontend.util.InlineDialog;
import de.fosd.typechef.crefactor.frontend.util.InputBox;
import de.fosd.typechef.crefactor.util.Configuration;
import de.fosd.typechef.parser.c.AST;
import de.fosd.typechef.parser.c.Id;
import scala.collection.immutable.List;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class RefactorAction {

    public static Action getExtractFunction(final Morpheus morpheus, final List<AST> selection) {

        return new AbstractAction() {

            {
                putValue(Action.NAME, Configuration.getInstance().getConfig("refactor.extractFunction"));
            }

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
            }
        };
    }

    public static Action getInlineFunctionAction(final Morpheus morpheus, final Id id) {

        return new AbstractAction() {

            {
                putValue(Action.NAME, Configuration.getInstance().getConfig("refactor.inline") + " " + id.name());
            }

            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                if (InlineFunction.isFunctionCall(morpheus, id)) {
                    System.out.println("InlineOnce");
                }
                final InlineDialog dialog = new InlineDialog(null,
                        Configuration.getInstance().getConfig("refactor.inline.name") + " " + id.name() + "()",
                        InlineFunction.isFunctionCall(morpheus, id));
                dialog.pack();
                dialog.setVisible(true);

                if (!dialog.isRefactor()) {
                    return;
                }

                try {
                    final long start = System.currentTimeMillis();
                    final AST refactored = InlineFunction.inline(morpheus, id, dialog.isRename(), dialog.isOnce());
                    System.out.println("Duration for transforming: " + (System.currentTimeMillis() - start));
                    morpheus.update(refactored);
                } catch (final AssertionError e) {
                    JOptionPane.showMessageDialog(null, Configuration.getInstance().getConfig("refactor.inline.failed") + " "
                            + e.getMessage(), Configuration.getInstance().getConfig("default.error"), JOptionPane.ERROR_MESSAGE);

                } catch (final Exception e) {
                    e.printStackTrace();
                }

            }
        };
    }

    public static Action getRenameAction(final Morpheus morpheus, final Id id) {
        return new AbstractAction() {

            {
                putValue(Action.NAME, Configuration.getInstance().getConfig("refactor.rename") + " \"" + id.name() + "\"");
            }

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                final InputBox box = new InputBox();
                box.createAndShowInputBox(Configuration.getInstance().getConfig("refactor.rename.name"),
                        Configuration.getInstance().getConfig("refactor.rename.newName"), id.name());
                if (box.getInput() == null) {
                    return;
                }

                try {
                    final ThreadMXBean tb = ManagementFactory.getThreadMXBean();
                    final long time = tb.getCurrentThreadCpuTime();
                    final AST refactored = RenameIdentifier.rename(id, box.getInput(), morpheus);
                    System.out.println("Duration for transforming: " + (tb.getCurrentThreadCpuTime() - time) / 1000000);
                    morpheus.update(refactored);
                } catch (final AssertionError e) {
                    JOptionPane.showMessageDialog(null, Configuration.getInstance().getConfig("refactor.rename.failed") + " "
                            + e.getMessage(), Configuration.getInstance().getConfig("default.error"), JOptionPane.ERROR_MESSAGE);

                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}