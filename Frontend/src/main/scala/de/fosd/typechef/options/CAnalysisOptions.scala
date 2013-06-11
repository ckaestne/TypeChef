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

    val Apointersign = SecurityOption("pointer-sign", "Issue type error when pointers have incompatible signedness (undefined behavior)", LinuxDefaultOptions.warning_pointer_sign)
    val Aintegeroverflow = SecurityOption("integer-overflow", "Issue security warning on possible integer overflows in security-relevant locations (unintended effects and undefined behavior)", LinuxDefaultOptions.warning_potential_integer_overflow)
    val Aimplicitcoercion = SecurityOption("implicit-coercion", "Issue security warning on implicit integer coercion (unintended side effects)", LinuxDefaultOptions.warning_implicit_coercion)
    val Alongdesignator = SecurityOption("long-designator", "Issue security warning on lowercase long designators (readability)", LinuxDefaultOptions.warning_long_designator)
    val Aimplicitidentifier = SecurityOption("implicit-identifier", "Issue security warning on implicit identifier definitions (undefined behavior)", LinuxDefaultOptions.warning_implicit_identifier)
    val Aconflictinglinkage = SecurityOption("conflicting-linkage", "Issue security warning on conflicting linkage declarations (undefined behavior)", LinuxDefaultOptions.warning_conflicting_linkage)


    val opts: List[SecurityOption] = List(
        Apointersign, Aintegeroverflow, Aimplicitcoercion, Alongdesignator, Aimplicitidentifier, Aconflictinglinkage
    )


    //-Wno-pointer-sign, -Wpointer-sign
    override def warning_pointer_sign: Boolean = Apointersign.isSelected
    override def warning_potential_integer_overflow: Boolean = Aintegeroverflow.isSelected
    override def warning_implicit_coercion = Aimplicitcoercion.isSelected
    override def warning_long_designator: Boolean = Alongdesignator.isSelected
    override def warning_implicit_identifier: Boolean = Aimplicitidentifier.isSelected
    override def warning_conflicting_linkage: Boolean = Aconflictinglinkage.isSelected

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
            arg = arg.replace('_', '-')

            if (arg == "ALL") opts.map(_.isSelected = true)
            else {
                val opt=opts.filter(_.param.toUpperCase == arg).headOption
                opt.map(_.isSelected = true)
                if (!opt.isDefined)
                    throw new OptionException("Analysis "+arg +" unknown. Known analyses: "+opts.map(_.param).mkString(", "))
            }
        }
        else if (c == 'a') {
            opts.map(_.isSelected = false)
        }
        else return super.interpretOption(c, g)
        return true
    }
}
