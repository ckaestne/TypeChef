package de.fosd.typechef.options

import de.fosd.typechef.typesystem.{LinuxDefaultOptions, ICTypeSysOptions}
import gnu.getopt.{Getopt, LongOpt}
import scala.Predef.String
import de.fosd.typechef.crewrite.ICAnalysisOptions

/**
 * options for the type system and additional security analysis
 *
 * (type system, data flow, etc)
 */
class CAnalysisOptions extends FeatureModelOptions with ICTypeSysOptions with ICAnalysisOptions {

    case class SecurityOption(param: String, expl: String, dflt: Boolean) {
        var isSelected = dflt
    }

    private val Apointersign = SecurityOption("pointer-sign", "Issue type error when pointers have incompatible signedness (undefined behavior)", LinuxDefaultOptions.warning_pointer_sign)
    private val Aintegeroverflow = SecurityOption("integer-overflow", "Issue security warning on possible integer overflows in security-relevant locations (unintended effects and undefined behavior)", LinuxDefaultOptions.warning_potential_integer_overflow)
    private val Aimplicitcoercion = SecurityOption("implicit-coercion", "Issue security warning on implicit integer coercion (unintended side effects)", LinuxDefaultOptions.warning_implicit_coercion)
    private val Alongdesignator = SecurityOption("long-designator", "Issue security warning on lowercase long designators (readability)", LinuxDefaultOptions.warning_long_designator)
    private val Aimplicitidentifier = SecurityOption("implicit-identifier", "Issue security warning on implicit identifier definitions (undefined behavior)", LinuxDefaultOptions.warning_implicit_identifier)
    private val Aconflictinglinkage = SecurityOption("conflicting-linkage", "Issue security warning on conflicting linkage declarations (undefined behavior)", LinuxDefaultOptions.warning_conflicting_linkage)
    private val Avolatile = SecurityOption("volatile", "Issue security warning on referencing a volatile object using a nonvolatile value (undefined behavior)", LinuxDefaultOptions.warning_volatile)
    private val Aconst = SecurityOption("const", "Issue security warning on assigning to const value or casting away const qualification (undefined behavior)", LinuxDefaultOptions.warning_const_assignment)
    private val Achar = SecurityOption("char", "Issue warning when converting between 'char' types of different signness (unintended effects)", LinuxDefaultOptions.warning_character_signed)

    private val Sdoublefree = SecurityOption("doublefree", "Issue a warning when dynamically allocated memory is freed multiple times", false)
    private val Sxfree = SecurityOption("xfree", "Issue a warning when trying to free memory that was not allocated dynamically", false)
    private val Sunitializedmemory = SecurityOption("uninitializedmemory", "Issue a warning when the value of a non-initialized variable is used", false)
    private val Scasetermination = SecurityOption("casetermination", "Issue a warning when statements following a case block within a switch aren't terminated using a break statement", false)
    private val Sdanglingswitchcode = SecurityOption("danglingswitchcode", "Issue a warning when code in a switch statement doesn't occur within the control flow of a case or default statement", false)
    private val Scfginnonvoidfunc = SecurityOption("cfginnonvoidfunction", "Issue a warning when control flow in non-void function reaches no return statement", false)
    private val Sstdlibfuncreturn = SecurityOption("checkstdlibfuncreturn", "Issue a warning when the return value of a standard library function is not check for its error values", false)
    private val Sdeadstore = SecurityOption("deadstore", "Issue a warning when values stored to variables are never read afterwards", false)


    val opts: List[SecurityOption] = List(
        Apointersign, Aintegeroverflow, Aimplicitcoercion, Alongdesignator, Aimplicitidentifier, Aconflictinglinkage, Avolatile, Aconst, Achar, Sdoublefree, Sxfree, Sunitializedmemory, Scasetermination, Sdanglingswitchcode, Scfginnonvoidfunc, Sstdlibfuncreturn, Sdeadstore
    )


    //-Wno-pointer-sign, -Wpointer-sign
    override def warning_pointer_sign: Boolean = Apointersign.isSelected
    override def warning_potential_integer_overflow: Boolean = Aintegeroverflow.isSelected
    override def warning_implicit_coercion = Aimplicitcoercion.isSelected
    override def warning_long_designator: Boolean = Alongdesignator.isSelected
    override def warning_implicit_identifier: Boolean = Aimplicitidentifier.isSelected
    override def warning_conflicting_linkage: Boolean = Aconflictinglinkage.isSelected
    override def warning_volatile: Boolean = Avolatile.isSelected
    override def warning_const_assignment: Boolean = Aconst.isSelected
    override def warning_character_signed: Boolean = Achar.isSelected

    override def warning_double_free: Boolean = Sdoublefree.isSelected
    override def warning_xfree: Boolean = Sxfree.isSelected
    override def warning_uninitialized_memory: Boolean = Sunitializedmemory.isSelected
    override def warning_case_termination: Boolean = Scasetermination.isSelected
    override def warning_dangling_switch_code: Boolean = Sdanglingswitchcode.isSelected
    override def warning_cfg_in_non_void_func: Boolean = Scfginnonvoidfunc.isSelected
    override def warning_stdlib_func_return: Boolean = Sstdlibfuncreturn.isSelected
    override def warning_dead_store: Boolean = Sdeadstore.isSelected

    override protected def getOptionGroups: java.util.List[Options.OptionGroup] = {
        val r: java.util.List[Options.OptionGroup] = super.getOptionGroups

        r.add(new Options.OptionGroup("Analysis", 100,
            new Options.Option("analysis", LongOpt.REQUIRED_ARGUMENT, 'A', "type",
                "Enables the analysis class: \n" +
                    opts.map(o => " * " + o.param + (if (o.dflt) "*" else "") + ": " + o.expl).mkString("\n") +
                    "\n(Analyses with * are activated by default)."
            ),
            new Options.Option("no-analysis", LongOpt.NO_ARGUMENT, 'a', null, "Disables ALL analyses.")
        ))

        r
    }


    protected override def interpretOption(c: Int, g: Getopt): Boolean = {

        if (c == 'A') {
            var arg: String = g.getOptarg.toUpperCase
            arg = arg.replace('_', '-')

            if (arg == "ALL") opts.map(_.isSelected = true)
            else {
                val opt = opts.filter(_.param.toUpperCase == arg).headOption
                opt.map(_.isSelected = true)
                if (!opt.isDefined)
                    throw new OptionException("Analysis " + arg + " unknown. Known analyses: " + opts.map(_.param).mkString(", "))
            }
        }
        else if (c == 'a') {
            opts.map(_.isSelected = false)
        }
        else return super.interpretOption(c, g)
        true
    }
}
