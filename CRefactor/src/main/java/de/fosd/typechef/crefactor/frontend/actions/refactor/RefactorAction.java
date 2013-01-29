package de.fosd.typechef.crefactor.frontend.actions.refactor;


import de.fosd.typechef.crefactor.backend.refactor.InlineFunction;
import de.fosd.typechef.crefactor.frontend.Editor;
import de.fosd.typechef.crefactor.frontend.util.InlineDialog;
import de.fosd.typechef.crefactor.util.Configuration;
import de.fosd.typechef.parser.c.AST;
import de.fosd.typechef.parser.c.Id;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class RefactorAction {

    public static Action getInlineFunction(final Editor editor, final Id id) {

        return new AbstractAction() {

            {
                putValue(Action.NAME, Configuration.getInstance().getConfig("refactor.inline") + " " + id.name());
            }

            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                if (InlineFunction.isFunctionCall(editor.getMorpheus(), id)) {
                    System.out.println("InlineOnce");
                }
                final InlineDialog dialog = new InlineDialog(editor,
                        Configuration.getInstance().getConfig("refactor.inline.name") + " " + id.name() + "()",
                        InlineFunction.isFunctionCall(editor.getMorpheus(), id));
                dialog.pack();
                dialog.setVisible(true);

                if (!dialog.isRefactor()) {
                    return;
                }

                try {
                    final long start = System.currentTimeMillis();
                    final AST refactored = InlineFunction.inline(editor.getMorpheus(), id, dialog.isRename());
                    System.out.println("Duration for transforming: " + (System.currentTimeMillis() - start));
                    editor.loadASTinEditor(refactored);
                    System.out.println("Duration for transforming and pretty print: " + (System.currentTimeMillis() - start));
                } catch (final AssertionError e) {
                    JOptionPane.showMessageDialog(editor, Configuration.getInstance().getConfig("refactor.inline.failed") + " "
                            + e.getMessage(), Configuration.getInstance().getConfig("default.error"), JOptionPane.ERROR_MESSAGE);

                } catch (final Exception e) {
                    e.printStackTrace();
                }

            }
        };
    }
}