package de.fosd.typechef.lexer.options;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import de.fosd.typechef.lexer.Version;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

abstract class Options {

    protected static class Option extends LongOpt {
        private String eg;
        private String help;

        public Option(String word, int arg, int ch, String eg, String help) {
            super(word, arg, null, ch);
            this.eg = eg;
            this.help = help;
        }
    }

    protected abstract List<Option> getOptions();

    public void parseOptions(String[] args) throws OptionException {
        Option[] opts = getOptions().toArray(new Option[]{});
        String sopts = getShortOpts(opts);
        Getopt g = new Getopt("TypeChef", args, sopts, opts);
        int c;
        while ((c = g.getopt()) != -1) {
            if (!interpretOption(c, g))
                throw new OptionException("Illegal option " + (char) c);
        }

        for (int i = g.getOptind(); i < args.length; i++) {
            String f = args[i];
            if (!new File(f).exists())
                throw new OptionException("File not found " + f);
            files.add(f);
        }
    }

    protected abstract boolean interpretOption(int c, Getopt g) throws OptionException;


    private String getShortOpts(Option[] opts) throws OptionException {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < opts.length; i++) {
            char c = (char) opts[i].getVal();
            if (!Character.isLetterOrDigit(c))
                continue;
            for (int j = 0; j < buf.length(); j++)
                if (buf.charAt(j) == c)
                    throw new OptionException("Duplicate short option " + c);
            buf.append(c);
            switch (opts[i].getHasArg()) {
                case LongOpt.NO_ARGUMENT:
                    break;
                case LongOpt.OPTIONAL_ARGUMENT:
                    buf.append("::");
                    break;
                case LongOpt.REQUIRED_ARGUMENT:
                    buf.append(":");
                    break;
            }
        }
        return buf.toString();
    }


    protected void printUsage() {
        StringBuilder text = new StringBuilder("Parameters: \n");
        List<Option> options = getOptions();
        for (Option opt : options) {
            StringBuilder line = new StringBuilder();
            line.append("    --").append(opt.getName());
            switch (opt.getHasArg()) {
                case LongOpt.NO_ARGUMENT:
                    break;
                case LongOpt.OPTIONAL_ARGUMENT:
                    line.append("[=").append(opt.eg).append(']');
                    break;
                case LongOpt.REQUIRED_ARGUMENT:
                    line.append('=').append(opt.eg);
                    break;
            }
            if (Character.isLetterOrDigit(opt.getVal()))
                line.append(" (-").append((char) opt.getVal()).append(")");
            if (line.length() < 30) {
                while (line.length() < 30)
                    line.append(' ');
            } else {
                line.append('\n');
                for (int j = 0; j < 30; j++)
                    line.append(' ');
            }
            /* This should use wrap. */
            line.append(opt.help);
            line.append('\n');
            text.append(line);
        }

        System.out.println(text);
    }


    private void version(PrintStream out) {
        out.println("TypeChef "
                + Version.getVersion());
    }


    protected List<String> files = new ArrayList<String>();

    public List<String> getFiles() {
        return files;
    }
}
