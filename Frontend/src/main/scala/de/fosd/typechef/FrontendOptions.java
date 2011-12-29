package de.fosd.typechef;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprParser;
import de.fosd.typechef.featureexpr.FeatureModel;
import de.fosd.typechef.lexer.options.LexerOptions;
import de.fosd.typechef.lexer.options.OptionException;
import de.fosd.typechef.lexer.options.Options;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.util.List;


public class FrontendOptions extends LexerOptions {
    boolean parse = true,
            typecheck = true,
            writeInterface = true,
            serializeAST = false,
            writeDebugInterface = false,
            recordTiming = false;
    String outputStem = "";
    private String filePresenceConditionFile = "";


    @Override
    protected List<Options.OptionGroup> getOptionGroups() {
        List<OptionGroup> r = super.getOptionGroups();

        r.add(new OptionGroup("General processing options (lexing, parsing, type checking, interfaces; select only highest)", 10,
                new Option("lex", LongOpt.NO_ARGUMENT, 'E', null,
                        "Stop after lexing; no parsing."),
                new Option("parse", LongOpt.NO_ARGUMENT, 21, null,
                        "Lex and parse the file; no type checking."),
                new Option("typecheck", LongOpt.NO_ARGUMENT, 't', null,
                        "Lex, parse, and type check; but do not create interfaces."),
                new Option("interface", LongOpt.NO_ARGUMENT, 14, null,
                        "Lex, parse, type check, and create interfaces (default)."),

                new Option("output", LongOpt.REQUIRED_ARGUMENT, 'o', "file",
                        "Path to output files (no extension, creates .pi, .macrodbg etc files)."),

                new Option("writePI", LongOpt.NO_ARGUMENT, 18, null,
                        "Write lexer output into .pi file"),
                new Option("debugInterface", LongOpt.NO_ARGUMENT, 17, null,
                        "Write interface in human readable format (requires --interface)"),

                new Option("serializeAST", LongOpt.NO_ARGUMENT, 15, null,
                        "Write ast to .ast file after parsing."),
                new Option("recordTiming", LongOpt.NO_ARGUMENT, 16, null,
                        "Report times for all phases."),

                new Option("filePC", LongOpt.REQUIRED_ARGUMENT, 19, "file",
                        "Presence condition for the file (format like --featureModelFExpr). Default 'file.pc'.")
        ));

        return r;

    }

    @Override
    protected boolean interpretOption(int c, Getopt g) throws OptionException {
        switch (c) {
            case 'E':       //--lex
                parse = typecheck = writeInterface = false;
                lexPrintToStdout = true;
                return true;
            case 21:     //--parse
                parse = true;
                typecheck = writeInterface = false;
                return true;
            case 't'://--typecheck
                parse = typecheck = true;
                writeInterface = false;
                return true;
            case 14://--interface
                parse = typecheck = writeInterface = true;
                return true;
            case 15:
                serializeAST = true;
                return true;
            case 16:
                recordTiming = true;
                return true;
            case 17:
                writeDebugInterface = true;
                return true;
            case 'o':
                outputStem = g.getOptarg();
                return true;
            case 19://--filePC
                checkFileExists(g.getOptarg());
                filePresenceConditionFile = g.getOptarg();
                return true;
            default:
                return super.interpretOption(c, g);
        }


    }

    protected void afterParsing() throws OptionException {
        super.afterParsing();
        if (getFiles().size() <= 0)
            throw new OptionException("No file specified.");
        if (getFiles().size() > 1)
            throw new OptionException("Multiple files specified. Only one supported.");

        if (outputStem.length() == 0)
            outputStem = getFile().replace(".c", "");
        if (lexOutputFile.length() == 0)
            lexOutputFile = outputStem + ".pi";
    }

    String getFile() {
        return getFiles().iterator().next();
    }

    String getInterfaceFilename() {
        return outputStem + ".interface";
    }

    String getDebugInterfaceFilename() {
        return outputStem + ".dbginterface";
    }

    String getFilePresenceConditionFilename() {
        if (filePresenceConditionFile.length() > 0)
            return filePresenceConditionFile;
        else
            return outputStem + ".pc";
    }

    private FeatureExpr filePC = null;

    FeatureExpr getFilePresenceCondition() {
        if (filePC == null)
            filePC = new FeatureExprParser().parseFile(getFilePresenceConditionFilename());
        return filePC;
    }

    String getLocalFeatureModelFilename() {
        return outputStem + ".fm";
    }

    private FeatureExpr localFM = null;

    FeatureExpr getLocalFeatureModel() {
        if (localFM == null)
            localFM = new FeatureExprParser().parseFile(getLocalFeatureModelFilename());
        return localFM;
    }

}
