package de.fosd.typechef.crefactor.frontend.actions.refactor;

import de.fosd.typechef.crefactor.backend.Connector;
import de.fosd.typechef.crefactor.backend.refactor.Helper;
import de.fosd.typechef.crefactor.backend.refactor.Renaming;
import de.fosd.typechef.crefactor.frontend.Editor;
import de.fosd.typechef.crefactor.frontend.util.InputBox;
import de.fosd.typechef.crefactor.util.Configuration;
import de.fosd.typechef.parser.c.AST;
import de.fosd.typechef.parser.c.Id;
import de.fosd.typechef.parser.c.PrettyPrinter;
import de.fosd.typechef.parser.c.TranslationUnit;
import de.fosd.typechef.typesystem.CEnv;
import scala.Tuple3;
import scala.collection.immutable.List;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.IdentityHashMap;

/**
 * All actions required for renaming are found here.
 */
public class Rename {

    /**
     * Retrieves the action for renaming a variable or function
     *
     * @param editor the current editor instance
     * @param id     the id to rename
     * @return the action
     */
    public static Action getRenameAction(final Editor editor, final Id id) {
        return new AbstractAction() {

            {
                putValue(Action.NAME, id.name() + " " + Configuration.getInstance().getConfig("refactor.rename"));
            }

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                final InputBox box = new InputBox();
                box.createAndShowInputBox(Configuration.getInstance().getConfig("refactor.rename.name"),
                        Configuration.getInstance().getConfig("refactor.rename.newName"), id.name());
                if (box.getInput() != null) {
                    Renaming.renamingIsPossible(
                            Connector.getAST(), Connector.getDefUseMap(), box.getInput(), id);

                    // Verify Input
                    if (!Renaming.validName(box.getInput())) {
                        JOptionPane.showMessageDialog(
                                null, "Der Name entspricht nicht dem ISO C Standard.", "Fehler", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    if (Renaming.isDeclaredVarInScope((TranslationUnit) Connector.getAST(), Connector.getDefUseMap(), box.getInput(), id)) {
                        JOptionPane.showMessageDialog(
                                null, "Der Name verdeckt eine weitere Variable", "Fehler", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    if (Helper.isStructOrUnion(id)
                            && Renaming.isDeclaredStructOrUnionDef((TranslationUnit) Connector.getAST(), Connector.getDefUseMap(), box.getInput(), id)) {
                        JOptionPane.showMessageDialog(
                                null, "Der Name verdeckt eine weitere Variable", "Fehler", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    if (Helper.isTypedef(id)
                            && Renaming.isDeclaredTypeDef((TranslationUnit) Connector.getAST(), Connector.getDefUseMap(), box.getInput(), id)) {
                        JOptionPane.showMessageDialog(
                                null, "Der Name verdeckt eine weitere Variable", "Fehler", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    System.out.println("oldDefuse " + Connector.getDefUseMap());
                    AST refactored = Renaming.renameId(Connector.getAST(), Connector.getDefUseMap(), box.getInput(), id);
                    Tuple3<AST, IdentityHashMap<Id, List<Id>>, CEnv.Env> updatedParsedCode = Connector.doTypeCheck(refactored);
                    // Pretty Print :)
                    editor.getRTextArea().setText(PrettyPrinter.print(refactored));
                    // TODO Verification!
                    System.out.println("Refactored DefUse " + updatedParsedCode._2());
                }
            }
        };
    }
}
