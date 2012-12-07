package de.fosd.typechef.crefactor.frontend.actions.refactor;

import de.fosd.typechef.conditional.Opt;
import de.fosd.typechef.crefactor.backend.Cache;
import de.fosd.typechef.crefactor.backend.refactor.ExtractFunction;
import de.fosd.typechef.crefactor.frontend.Editor;
import de.fosd.typechef.crefactor.util.Configuration;
import de.fosd.typechef.parser.c.AST;
import de.fosd.typechef.parser.c.PrettyPrinter;
import scala.collection.immutable.List;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * All actions required for extracting a function are found here.
 */
public class ExtractFunctionActions {

    public static Action getExtractFunctionAction(final Editor editor, final List<Opt<?>> selectedAST) {
        final Action action = new AbstractAction() {

            {
                putValue(Action.NAME, Configuration.getInstance().getConfig("refactor.extractFunction"));
            }

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // TODO Show window & Name Verification
                final AST ast = ExtractFunction.extract(selectedAST, "newFunc", Cache.getAST(), Cache.getASTEnv(), Cache.getDeclUseMap());
                Cache.update(ast);
                editor.getRTextArea().setText(PrettyPrinter.print(ast));
            }
        };

        return action;
    }
}
