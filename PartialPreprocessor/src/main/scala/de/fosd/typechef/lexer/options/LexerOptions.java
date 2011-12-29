package de.fosd.typechef.lexer.options;

import de.fosd.typechef.featureexpr.FeatureModel;
import de.fosd.typechef.lexer.Feature;
import de.fosd.typechef.lexer.Warning;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 28.12.11
 * Time: 22:13
 * To change this template use File | Settings | File Templates.
 */
public class LexerOptions extends FeatureModelOptions implements ILexerOptions {

    @Override
    protected List<Options.OptionGroup> getOptionGroups() {
        List<OptionGroup> r = super.getOptionGroups();

        r.add(new OptionGroup("Preprocessor configuration", 50,
                new Option("define", LongOpt.REQUIRED_ARGUMENT, 'D', "name[=definition]",
                        "Defines the given macro (may currently not be used to define parametric macros)."),
                new Option("undefine", LongOpt.REQUIRED_ARGUMENT, 'U', "name",
                        "Undefines the given macro, previously either builtin or defined using -D."),
                new Option("include", LongOpt.REQUIRED_ARGUMENT, 1, "file",
                        "Process file as if \"#" + "include \"file\"\" appeared as the first line of the primary source file."),
                new Option("incdir", LongOpt.REQUIRED_ARGUMENT, 'I', "dir",
                        "Adds the directory dir to the list of directories to be searched for header files."),
                new Option("iquote", LongOpt.REQUIRED_ARGUMENT, 0, "dir",
                        "Adds the directory dir to the list of directories to be searched for header files included using \"\"."),
                new Option("lexOutput", LongOpt.REQUIRED_ARGUMENT, 18, "file",
                        "Output file (typically .pi).")
        ));

        r.add(new OptionGroup("Preprocessor flag filter", 60,
                new Option("prefixfilter", LongOpt.REQUIRED_ARGUMENT, 'p', "text",
                        "Analysis excludes all flags beginning with this prefix."),
                new Option("postfixfilter", LongOpt.REQUIRED_ARGUMENT, 'P', "text",
                        "Analysis excludes all flags ending with this postfix."),
                new Option("prefixonly", LongOpt.REQUIRED_ARGUMENT, 'x', "text",
                        "Analysis includes only flags beginning with this prefix."),
                new Option("openFeat", LongOpt.REQUIRED_ARGUMENT, 4, "text",
                        "List of flags with an unspecified value; other flags are considered undefined.")
        ));
        r.add(new OptionGroup("Preprocessor warnings and debugging", 70,
                new Option("warning", LongOpt.REQUIRED_ARGUMENT, 'W', "type",
                        "Enables the named warning class (" + getWarningLabels() + ")."),
                new Option("no-warnings", LongOpt.NO_ARGUMENT, 'w', null,
                        "Disables ALL warnings."),
                new Option("verbose", LongOpt.NO_ARGUMENT, 'v', null,
                        "Operates incredibly verbosely."),
                new Option("lexdebug", LongOpt.NO_ARGUMENT, 3, null,
                        "Create debug files for macros and sources (enables debugfile-sources and debugfile-macrotable)."),
                new Option("lexEnable", LongOpt.REQUIRED_ARGUMENT, 5, "type",
                        "Enables a specific lexer feature (" + getFeatureLabels() + ") Features with * are activated by default."),
                new Option("lexDisable", LongOpt.REQUIRED_ARGUMENT, 6, "type",
                        "Disable a specific lexer feature."),
                new Option("lexNoStdout", LongOpt.NO_ARGUMENT, 7, null,
                        "Do not print to stdout.")
        ));
        r.add(new OptionGroup("Misc", 1000,
                new Option("version", LongOpt.NO_ARGUMENT, 2, null,
                        "Prints version number"),
                new Option("help", LongOpt.NO_ARGUMENT, 22, null,
                        "Displays help and usage information.")
        ));
        return r;

    }

    private static CharSequence getWarningLabels() {
        StringBuilder buf = new StringBuilder();
        for (Warning w : Warning.values()) {
            if (buf.length() > 0)
                buf.append(", ");
            String name = w.name().toLowerCase();
            buf.append(name.replace('_', '-'));
        }
        return buf;
    }

    private static CharSequence getFeatureLabels() {
        StringBuilder buf = new StringBuilder();
        for (Feature w : Feature.values()) {
            if (buf.length() > 0)
                buf.append(", ");
            String name = w.name().toLowerCase();
            buf.append(name.replace('_', '-'));
            if (getDefaultFeatures().contains(w))
                buf.append("*");
        }
        return buf;
    }

    private static Set<Feature> getDefaultFeatures() {
        Set<Feature> r = new HashSet<Feature>();
        // No sane code uses TRIGRAPHS or DIGRAPHS - at least, no code
        // written with ASCII available!
        //pp.addFeature(Feature.DIGRAPHS);
        //pp.addFeature(Feature.TRIGRAPHS);
        r.add(Feature.LINEMARKERS);
        r.add(Feature.INCLUDENEXT);
        r.add(Feature.GNUCEXTENSIONS);
        return r;
    }

    protected Map<String, String> definedMacros = new HashMap<String, String>();
    protected Set<String> undefMacros = new HashSet<String>();
    protected List<String> systemIncludePath = new ArrayList<String>();
    protected List<String> quoteIncludePath = new ArrayList<String>();
    protected List<String> macroFilter = new ArrayList<String>();
    protected List<String> includedHeaders = new ArrayList<String>();
    protected Set<Warning> warnings = new HashSet<Warning>();
    protected Set<Feature> features = getDefaultFeatures();
    protected String lexOutputFile = "";
    protected boolean printVersion = false;
    protected boolean lexPrintToStdout = true;

    @Override
    protected boolean interpretOption(int c, Getopt g) throws OptionException {
        switch (c) {
            case 'D':
                //XXX may not be used to define parametric macros
                String arg = g.getOptarg();
                int idx = arg.indexOf('=');
                String name, value;
                if (idx == -1) {
                    name = arg;
                    value = "1";
                } else {
                    name = arg.substring(0, idx);
                    value = arg.substring(idx + 1);
                }
                definedMacros.put(name, value);
                undefMacros.remove(name);
                return true;
            case 'U':
                definedMacros.remove(g.getOptarg());
                undefMacros.add(g.getOptarg());
                return true;
            case 'I':
                // Paths need to be canonicalized, because include_next
                // processing needs to compare paths!
                checkDirectoryExists(g.getOptarg());
                try {
                    systemIncludePath.add(new File(g.getOptarg()).getCanonicalPath());
                } catch (IOException e) {
                    throw new OptionException("path not found " + g.getOptarg());
                }
                return true;
            case 'p':
                macroFilter.add("p:" + g.getOptarg());
                return true;
            case 'P':
                macroFilter.add("P:" + g.getOptarg());
                return true;
            case 'x':
                macroFilter.add("x:" + g.getOptarg());
                return true;
            case 4:   //--openFeat
                macroFilter.add("4:" + g.getOptarg());
                return true;
            case 0: // --iquote=
                checkDirectoryExists(g.getOptarg());
                try {
                    quoteIncludePath.add(new File(g.getOptarg()).getCanonicalPath());
                } catch (IOException e) {
                    throw new OptionException("path not found " + g.getOptarg());
                }
                return true;
            case 'W':
                arg = g.getOptarg().toUpperCase();
                arg = arg.replace('-', '_');
                if (arg.equals("ALL"))
                    warnings.addAll(EnumSet.allOf(Warning.class));
                else
                    warnings.add(Enum.valueOf(Warning.class, arg));
                return true;
            case 'w':
                warnings.clear();
                return true;
            case 18:   //--lexOutput (previously -o)
                lexOutputFile = g.getOptarg();
                return true;
            case 1: // --include=
                checkFileExists(g.getOptarg());
                try {
                    includedHeaders.add(new File(g.getOptarg()).getCanonicalPath());
                } catch (IOException e) {
                    throw new OptionException("file not found " + g.getOptarg());
                }
                return true;
            case 2: // --version
                printVersion = true;
                return true;
            case 'v':
                features.add(Feature.DEBUG_VERBOSE);
                features.add(Feature.DEBUG_INCLUDEPATH);
                return true;
            case 3:
                features.add(Feature.DEBUGFILE_MACROTABLE);
                features.add(Feature.DEBUGFILE_SOURCES);
                return true;
            case 5://--lexEnable
                arg = g.getOptarg().toUpperCase();
                arg = arg.replace('-', '_');
                features.add(Enum.valueOf(Feature.class, arg));
            case 6://--lexDisable
                arg = g.getOptarg().toUpperCase();
                arg = arg.replace('-', '_');
                features.remove(Enum.valueOf(Feature.class, arg));
            case 7://--lexNoStdout
                lexPrintToStdout = false;
                return true;

            case 22://--help
                printUsage();
                printVersion = true;
                return true;
            default:
                return super.interpretOption(c, g);
        }

    }

    @Override
    public Map<String, String> getDefinedMacros() {
        return definedMacros;
    }

    @Override
    public Set<String> getUndefMacros() {
        return undefMacros;
    }

    @Override
    public List<String> getIncludePaths() {
        return new ArrayList<String>(systemIncludePath);
    }

    @Override
    public List<String> getQuoteIncludePath() {
        return quoteIncludePath;
    }

    @Override
    public List<String> getMacroFilter() {
        return macroFilter;
    }

    @Override
    public List<String> getIncludedHeaders() {
        return includedHeaders;
    }

    @Override
    public String getLexOutputFile() {
        return lexOutputFile;
    }

    @Override
    public boolean isPrintVersion() {
        return printVersion;
    }


    @Override
    public Set<Warning> getWarnings() {
        return warnings;
    }

    @Override
    public Set<Feature> getFeatures() {
        return features;
    }

    @Override
    public boolean isLexPrintToStdout() {
        return lexPrintToStdout;
    }


    public void setPrintToStdOutput(boolean printToStdOutput) {
        this.lexPrintToStdout = printToStdOutput;
    }
}
