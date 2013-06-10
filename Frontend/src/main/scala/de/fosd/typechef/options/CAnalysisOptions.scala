package de.fosd.typechef.options

import de.fosd.typechef.typesystem.{LinuxDefaultOptions, ICTypeSysOptions}
import gnu.getopt.{Getopt, LongOpt}
import scala.Predef.String

/**
 * options for the type system and additional security analysis
 *
 * (type system, data flow, etc)
 */
class CAnalysisOptions extends FeatureModelOptions with ICTypeSysOptions {

    case class SecurityOption(param: String, expl: String, dflt: Boolean) {
        var isSelected = dflt
    }

    val Apointersign = SecurityOption("pointer-sign", "Issue type error when pointers have incompatible signess", LinuxDefaultOptions.warning_pointer_sign)
    val Aintegeroverflow = SecurityOption("integer-overflow", "Issue security warning on possible integer overflows in security-relevant locations", LinuxDefaultOptions.warning_potential_integer_overflow)
    val Aimplicitcoercion = SecurityOption("implicit-coercion", "Issue security warning on implicit integer coercion", LinuxDefaultOptions.warning_implicit_coercion)

    val opts: List[SecurityOption] = List(
        Apointersign, Aintegeroverflow, Aimplicitcoercion
    )


    //-Wno-pointer-sign, -Wpointer-sign
    def warning_pointer_sign: Boolean = Apointersign.isSelected
    def warning_potential_integer_overflow: Boolean = Aintegeroverflow.isSelected
    def warning_implicit_coercion = Aimplicitcoercion.isSelected

    override protected def getOptionGroups: java.util.List[Options.OptionGroup] = {
        val r: java.util.List[Options.OptionGroup] = super.getOptionGroups

        r.add(new Options.OptionGroup("Analysis", 100,
            new Options.Option("analysis", LongOpt.REQUIRED_ARGUMENT, 'A', "type",
                "Enables the analysis class: \n" +
                    opts.map(o => " * " + o.param + (if (o.dflt) "*" else "") + ": " + o.expl).mkString("\n") +
                    "\n(Analyses with * are activated by default)."
            ),
            new Options.Option("no-anaysis", LongOpt.NO_ARGUMENT, 'a', null, "Disables ALL analyses.")
        ))


        return r
    }


    protected override def interpretOption(c: Int, g: Getopt): Boolean = {

        if (c == 'A') {
            var arg: String = g.getOptarg.toUpperCase
            arg = arg.replace('-', '_')

            if (arg == "ALL") opts.map(_.isSelected = true)
            else opts.filter(_.param.toUpperCase == arg).map(_.isSelected = true)
        }
        else if (c == 'a') {
            opts.map(_.isSelected = false)
        }
        else return super.interpretOption(c, g)
        return true
    }
}
