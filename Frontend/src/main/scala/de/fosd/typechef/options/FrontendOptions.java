package de.fosd.typechef.options;

import de.fosd.typechef.VALexer;
import de.fosd.typechef.error.Position;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory$;
import de.fosd.typechef.featureexpr.FeatureExprParserJava;
import de.fosd.typechef.parser.c.ParserOptions;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import scala.Function3;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FrontendOptions extends CAnalysisOptions implements ParserOptions {
    public boolean parse = true,
            typecheck = false,
            ifdeftoif = false,
            decluse = false,
            writeInterface = false,
            dumpcfg = false,
            serializeAST = false,
            reuseAST = false,
            writeDebugInterface = false,
            recordTiming = false,
            parserStatistics = false,
            parserResults = true,
            writePI = false,
            printInclude = false,
            printVersion = false;
    protected File errorXMLFile = null;
    private final File _autoErrorXMLFile = new File(".");
    String outputStem = "";
    private String filePresenceConditionFile = "";


    private final static char F_PARSE = Options.genOptionId();
    private final static char F_INTERFACE = Options.genOptionId();
    private final static char F_WRITEPI = Options.genOptionId();
    private final static char F_DEBUGINTERFACE = Options.genOptionId();
    private final static char F_DUMPCFG = Options.genOptionId();
    private final static char F_SERIALIZEAST = Options.genOptionId();
    private final static char F_REUSEAST = Options.genOptionId();
    private final static char F_RECORDTIMING = Options.genOptionId();
    private final static char F_FILEPC = Options.genOptionId();
    private final static char F_PARSERSTATS = Options.genOptionId();
    private final static char F_HIDEPARSERRESULTS = Options.genOptionId();
    private final static char F_BDD = Options.genOptionId();
    private final static char F_ERRORXML = Options.genOptionId();
    private static final char TY_DEBUG_INCLUDES = genOptionId();
    private static final char TY_VERSION = genOptionId();
    private static final char TY_HELP = genOptionId();
    private Function3<FeatureExpr, String, Position, Object> _renderParserError;


    @Override
    public List<Options.OptionGroup> getOptionGroups() {
        List<OptionGroup> r = super.getOptionGroups();

        r.add(new OptionGroup("General processing options (lexing, parsing, type checking, interfaces; select only highest)", 10,
                new Option("lex", LongOpt.NO_ARGUMENT, 'E', null,
                        "Stop after lexing; no parsing."),
                new Option("parse", LongOpt.NO_ARGUMENT, F_PARSE, null,
                        "Lex and parse the file; no type checking (default)."),
                new Option("typecheck", LongOpt.NO_ARGUMENT, 't', null,
                        "Lex, parse, and type check; but do not create interfaces."),
                new Option("interface", LongOpt.NO_ARGUMENT, F_INTERFACE, null,
                        "Lex, parse, type check, and create interfaces."),

                new Option("dumpcfg", LongOpt.NO_ARGUMENT, F_DUMPCFG, null,
                        "Lex, parse, and dump control flow graph"),

                new Option("output", LongOpt.REQUIRED_ARGUMENT, 'o', "file",
                        "Path to output files (no extension, creates .pi, .macrodbg etc files)."),

                new Option("writePI", LongOpt.NO_ARGUMENT, F_WRITEPI, null,
                        "Write lexer output into .pi file"),
                new Option("debugInterface", LongOpt.NO_ARGUMENT, F_DEBUGINTERFACE, null,
                        "Write interface in human readable format (requires --interface)"),

                new Option("serializeAST", LongOpt.NO_ARGUMENT, F_SERIALIZEAST, null,
                        "Write ast to .ast file after parsing."),
                new Option("reuseAST", LongOpt.NO_ARGUMENT, F_REUSEAST, null,
                        "Reuse serialized .ast instead of parsing, if availabe."),
                new Option("recordTiming", LongOpt.NO_ARGUMENT, F_RECORDTIMING, null,
                        "Report times for all phases."),

                new Option("filePC", LongOpt.REQUIRED_ARGUMENT, F_FILEPC, "file",
                        "Presence condition for the file (format like --featureModelFExpr). Default 'file.pc'."),
                new Option("bdd", LongOpt.NO_ARGUMENT, F_BDD, null,
                        "Use BDD engine instead of SAT engine (provide as first parameter)."),

                new Option("errorXML", LongOpt.OPTIONAL_ARGUMENT, F_ERRORXML, "file",
                        "File to store syntax and type errors in XML format.")

        ));
        r.add(new OptionGroup("Parser options", 23,
                new Option("hideparserresults", LongOpt.NO_ARGUMENT, F_HIDEPARSERRESULTS, null,
                        "Do not show parser results."),
                new Option("parserstatistics", LongOpt.NO_ARGUMENT, F_PARSERSTATS, null,
                        "Print parser statistics.")
        ));
        r.add(new OptionGroup("Misc", 1000,
                new Option("printIncludes", LongOpt.NO_ARGUMENT, TY_DEBUG_INCLUDES, null,
                        "Prints gathered include information for debugging"),
                new Option("version", LongOpt.NO_ARGUMENT, TY_VERSION, null,
                        "Prints version number"),
                new Option("help", LongOpt.NO_ARGUMENT, TY_HELP, null,
                        "Displays help and usage information.")
        ));

        return r;

    }

    @Override
    public boolean interpretOption(int c, Getopt g) throws OptionException {
        if (c == 'E') {       //--lex
            parse = typecheck = writeInterface = false;
            lexPrintToStdout = true;
        } else if (c == F_PARSE) {//--parse
            parse = true;
            typecheck = writeInterface = false;
        } else if (c == 't') {//--typecheck
            parse = typecheck = true;
            writeInterface = false;
        } else if (c == F_INTERFACE) {//--interface
            parse = typecheck = writeInterface = true;
        } else if (c == F_DUMPCFG) {
            parse = dumpcfg = true;
        } else if (c == F_SERIALIZEAST) {
            serializeAST = true;
        } else if (c == F_REUSEAST) {
            reuseAST = true;
        } else if (c == F_RECORDTIMING) {
            recordTiming = true;
        } else if (c == F_DEBUGINTERFACE) {
            writeDebugInterface = true;
        } else if (c == 'o') {
            outputStem = g.getOptarg();
        } else if (c == F_FILEPC) {//--filePC
            checkFileExists(g.getOptarg());
            filePresenceConditionFile = g.getOptarg();
        } else if (c == F_HIDEPARSERRESULTS) {
            parserResults = false;
        } else if (c == F_PARSERSTATS) {
            parserStatistics = true;
        } else if (c == F_WRITEPI) {
            writePI = true;
        } else if (c == F_BDD) {
            de.fosd.typechef.featureexpr.FeatureExprFactory$.MODULE$.setDefault(de.fosd.typechef.featureexpr.FeatureExprFactory$.MODULE$.bdd());
        } else if (c == F_ERRORXML) {//--errorXML=file
            if (g.getOptarg() == null)
                errorXMLFile = _autoErrorXMLFile;
            else {
                checkFileWritable(g.getOptarg());
                errorXMLFile = new File(g.getOptarg());
            }
        } else if (c == TY_DEBUG_INCLUDES) { // --printInclude
            printInclude = true;
        } else if (c == TY_VERSION) { // --version
            printVersion = true;
        } else if (c == TY_HELP) {//--help
            printUsage();
            printVersion = true;
        } else
            return super.interpretOption(c, g);

        return true;

    }

    protected void afterParsing() throws OptionException {
        super.afterParsing();
        if (getFiles().size() <= 0)
            throw new OptionException("No file specified.");
        if (getFiles().size() > 1)
            throw new OptionException("Multiple files specified. Only one supported.");

        if (outputStem.length() == 0)
            outputStem = getFile().replace(".c", "");
        if (writePI && (lexOutputFile == null || lexOutputFile.length() == 0))
            lexOutputFile = outputStem + ".pi";
    }

    public String getFile() {
        return getFiles().iterator().next();
    }

    public String getInterfaceFilename() {
        return outputStem + ".interface";
    }

    public String getDebugInterfaceFilename() {
        return outputStem + ".dbginterface";
    }

    String getFilePresenceConditionFilename() {
        if (filePresenceConditionFile.length() > 0)
            return filePresenceConditionFile;
        else
            return outputStem + ".pc";
    }

    private FeatureExpr filePC = null;

    public FeatureExpr getFilePresenceCondition() {
        if (filePC == null) {
            File pcFile = new File(getFilePresenceConditionFilename());
            if (pcFile.exists())
                filePC = new FeatureExprParserJava(FeatureExprFactory$.MODULE$.dflt()).parseFile(pcFile);
            else
                filePC = FeatureExprFactory$.MODULE$.dflt().True();
        }
        return filePC;
    }

    String getLocalFeatureModelFilename() {
        return outputStem + ".fm";
    }

    private FeatureExpr localFM = null;

    public FeatureExpr getLocalFeatureModel() {
        if (localFM == null) {
            File file = new File(getLocalFeatureModelFilename());
            if (file.exists())
                localFM = new FeatureExprParserJava(FeatureExprFactory$.MODULE$.dflt()).parseFile(file);
            else localFM = FeatureExprFactory$.MODULE$.dflt().True();
        }
        return localFM;
    }

    public String getSerializedASTFilename() {
        return outputStem + ".ast";
    }

    public String getCCFGFilename() {
        return outputStem + ".cfg";
    }

    public String getCCFGDotFilename() {
        return outputStem + ".cfg.dot";
    }

    public boolean printParserStatistics() {
        return parserStatistics;
    }

    @Override
    public Function3<FeatureExpr, String, Position, Object> renderParserError() {
        return _renderParserError;
    }

    public void setRenderParserError(Function3<FeatureExpr, String, Position, Object> r) {
        _renderParserError = r;
    }


    public boolean printParserResult() {
        return parserResults;
    }

    public File getErrorXMLFile() {
        if (errorXMLFile == _autoErrorXMLFile)
            return new File(getFile() + ".xml");
        else
            return errorXMLFile;
    }

    public boolean isPrintVersion() {
        return printVersion;
    }

    public boolean isPrintIncludes() { return printInclude; }

    public void printInclude() {
        System.out.println("Included headers:");
        for (String header: this.getIncludedHeaders()) {
            System.out.println("  "+header);
        }
        System.out.println("System Include Paths:");
        for (String dir: this.getIncludePaths()) {
            System.out.println("  "+dir);
        }
        System.out.println("Quote Include Paths:");
        for (String dir: this.getQuoteIncludePath()) {
            System.out.println("  "+dir);
        }
    }


}

