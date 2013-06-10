package de.fosd.typechef.options

import de.fosd.typechef.typesystem.{LinuxDefaultOptions, ICTypeSysOptions}
import gnu.getopt.{Getopt, LongOpt}
import scala.collection.JavaConversions._

/**
 * options for the type system and additional security analysis
 *
 * (type system, data flow, etc)
 */
class CAnalysisOptions extends FeatureModelOptions with ICTypeSysOptions {

    case class SecurityOption(param: String, expl: String, dflt: Boolean) {
        val id = Options.genOptionId
    }

    val Wpointersign = SecurityOption("Wpointersign", "Issue type error when pointers have incompatible signess", LinuxDefaultOptions.warning_pointer_sign)
    val Wintegeroverflow = SecurityOption("Wintegeroverflow", "Issue security warning on possible integer overflows in security-relevant locations", LinuxDefaultOptions.warning_potential_integer_overflow)
    val Wimplicitcoercion = SecurityOption("Wimplicitcoercion", "Issue security warning on implicit integer coercion", LinuxDefaultOptions.warning_implicit_coercion)

    val opts: List[SecurityOption] = List(
        Wpointersign, Wintegeroverflow
    )

    var selectedOpts: List[String] = opts.filter(_.dflt).map(_.param)


    //-Wno-pointer-sign, -Wpointer-sign
    def warning_pointer_sign: Boolean = selectedOpts.contains(Wpointersign.param)
    def warning_potential_integer_overflow: Boolean = selectedOpts.contains(Wintegeroverflow.param)
    def warning_implicit_coercion = selectedOpts.contains(Wimplicitcoercion.param)

    override protected def getOptionGroups: java.util.List[Options.OptionGroup] = {
        val r: java.util.List[Options.OptionGroup] = super.getOptionGroups

        val entires = opts.map(o => new Options.Option(o.param, LongOpt.NO_ARGUMENT, o.id, null, o.expl))

        r.add(new Options.OptionGroup("Analysis", 100, entires))

        return r
    }


    protected override def interpretOption(c: Int, g: Getopt): Boolean = {
        val key = opts.filter(_.id == c).map(_.param).headOption

        if (key.isDefined)
            selectedOpts = key.get :: selectedOpts
        else return super.interpretOption(c, g)
        return true
    }
}
