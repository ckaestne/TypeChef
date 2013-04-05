package de.fosd.typechef.sampling;

import de.fosd.typechef.FrontendOptions;
import de.fosd.typechef.lexer.options.OptionException;
import de.fosd.typechef.lexer.options.Options;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.util.List;

public class FamilyBasedVsSampleBasedOptions extends FrontendOptions {
    boolean fileconfig = false,
            codecoverage = false,
            codecoverageNH = false,
            pairwise = false;
    private String rootfolder = "";

    private final static char F_FILECONFIG = Options.genOptionId();
    private final static char F_CODECOVERAGE = Options.genOptionId();
    private final static char F_CODECOVERAGENH = Options.genOptionId();
    private final static char F_PAIRWISE = Options.genOptionId();
    private final static char F_ROOTFOLDER = Options.genOptionId();

    @Override
    protected List<Options.OptionGroup> getOptionGroups() {
        List<OptionGroup> r = super.getOptionGroups();

        r.add(new OptionGroup("Sampling options", 1,
                new Option("rootfolder", LongOpt.REQUIRED_ARGUMENT, F_ROOTFOLDER, "rootfolder",
                        "parent folder of case study"),
                new Option("fileconfig", LongOpt.NO_ARGUMENT, F_FILECONFIG, null,
                        "enable fileconfig sampling; default is disabled"),
                new Option("codecoverage", LongOpt.NO_ARGUMENT, F_CODECOVERAGE, null,
                        "enable codecoverage sampling; default is disabled"),
                new Option("codecoveragenh", LongOpt.NO_ARGUMENT, F_CODECOVERAGENH, null,
                        "enable codecoverage (without header files) sampling; default is disabled"),
                new Option("pairwise", LongOpt.NO_ARGUMENT, F_PAIRWISE, null,
                        "enable pairwise sampling; default is disabled")
        ));

        return r;
    }

    @Override
    protected boolean interpretOption(int c, Getopt g) throws OptionException {
        if (c == F_FILECONFIG) fileconfig = true;
        else if (c == F_CODECOVERAGE) codecoverage = true;
        else if (c == F_PAIRWISE) pairwise = true;
        else if (c == F_ROOTFOLDER) {
            checkDirectoryExists(g.getOptarg());
            rootfolder = g.getOptarg();
        } else {
            return super.interpretOption(c, g);
        }

        return true;
    }

    public String getRootFolder() {
        return rootfolder;
    }
}
