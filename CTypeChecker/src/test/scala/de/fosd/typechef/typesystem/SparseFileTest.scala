package de.fosd.typechef.typesystem

import java.{util => ju, lang => jl}
import org.junit._
import java.io.{InputStream, FileNotFoundException}
import de.fosd.typechef.parser.c.{ExternalDef, TestHelper, TranslationUnit}
import de.fosd.typechef.lexer.{LexerException, InternalException}
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureModel}

class SparseFileTest extends TestHelper {

    val folder = "sparse/"
    private def checkSparse(filename: String) {
//        println("==========")
        def stream = getClass.getResourceAsStream("/" + folder + filename)
        var inputStream: InputStream = stream
        if (inputStream == null) {
            throw new FileNotFoundException("Input file not found: " + filename)
        }

        val start = System.currentTimeMillis

        //information
        val (name, isError, isKnownToFail, expectsExitValue, specialCommand) = readSparseInfo(stream, filename)
        println("testing " + name + " (" + filename + ")")

        var ast: TranslationUnit = null
        try {
            ast = parseFile(inputStream, filename, folder)
        } catch {
            case e: InternalException => ast = null
            case e: LexerException => ast = null
        }
        val parsingError = ast == null
        val parsed = System.currentTimeMillis
        var typeError = false
        if (!parsingError) {
            typeError = !check(ast, FeatureExprFactory.default.featureModelFactory.empty)
        }

        Assert.assertEquals(
            (if (isError) "expected error"
            else "should succeed") + ", but " +
                (if (parsingError) "parsing error"
                else if (typeError) "type error"
                else "succeeded") + ".",
            isError, parsingError || typeError)

    }
    private def check(ast: TranslationUnit, featureModel: FeatureModel): Boolean =
        new CTypeSystemFrontend(ast, featureModel) {
            override def checkingExternal(externalDef: ExternalDef) {}
        }.makeSilent().checkAST().isEmpty

    private def readSparseInfo(file: InputStream, filename: String) = {
        val lines = scala.io.Source.fromInputStream(file).getLines().toList
        val nameLines: List[String] = lines.filter(_ startsWith " * check-name: ").map(_ substring 15).toList
        val name = if (nameLines.size == 1) nameLines(0) else "checking " + filename

        val isError = lines.exists(_ contains "check-error-start")
        val isKnownToFail = lines.exists(_ contains "check-known-to-fail")
        val expectsExitValue = lines.exists(_ contains "check-exit-value")
        val specialCommand = lines.exists(_ contains "check-command:")

        (name, isError, isKnownToFail, expectsExitValue, specialCommand)

    }

    @Ignore("__attribute_ address_space not enforced_")
    @Test def test_address_space {
        checkSparse("address_space.c")
    }
    @Ignore("expected false, but was true")
    @Test def test_asm_empty_clobber {
        checkSparse("asm-empty-clobber.c")
    }
    @Ignore("expected false, but was true")
    @Test def test_asm_goto_lables {
        checkSparse("asm-goto-lables.c")
    }
    @Test def test_attr_warning {
        checkSparse("attr-warning.c")
    }
    @Test def test_attr_in_parameter {
        checkSparse("attr_in_parameter.c")
    }
    @Test def test_attr_vector_size {
        checkSparse("attr_vector_size.c")
    }
    @Test def test_bad_array_designated_initializer {
        checkSparse("bad-array-designated-initializer.c")
    }
    //parser error
    @Test def test_bad_assignment {
        checkSparse("bad-assignment.c")
    }
    @Test def test_bad_cast {
        checkSparse("bad-cast.c")
    }
    @Test def test_bad_ternary_cond {
        checkSparse("bad-ternary-cond.c")
    }
    @Test def test_bad_typeof {
        checkSparse("bad-typeof.c")
    }
    @Ignore("TODO check whether enum in scope")
    @Test def test_badtype1 {
        checkSparse("badtype1.c")
    }
    @Test def test_badtype2 {
        checkSparse("badtype2.c")
    }
    @Test def test_badtype3 {
        checkSparse("badtype3.c")
    }
    @Test def test_badtype4 {
        checkSparse("badtype4.c")
    }
    @Test def test_bitfields {
        checkSparse("bitfields.c")
    }
    @Test def test_bug_inline_switch {
        checkSparse("bug_inline_switch.c")
    }
    //    @Test def test_builtin_safe1 {checkSparse("builtin_safe1.c")} //dirty macro usage. not supposed to be checked by TypeChef
    @Test def test_builtin_unreachable {
        checkSparse("builtin_unreachable.c")
    }
    @Test def test_calling_convention_attributes {
        checkSparse("calling-convention-attributes.c")
    }
    @Test def test_check_byte_count_ice {
        checkSparse("check_byte_count-ice.c")
    }
    @Ignore("__builtin_choose_expr not checked further")
    @Test def test_choose_expr {
        checkSparse("choose_expr.c")
    }
    @Test def test_comma {
        checkSparse("comma.c")
    }
    @Test def test_compare_null_to_int {
        checkSparse("compare-null-to-int.c")
    }
    @Test def test_cond_expr {
        checkSparse("cond_expr.c")
    }
    @Ignore("corresponding warnings not yet supported")
    @Test def test_cond_expr2 {
        checkSparse("cond_expr2.c")
    }
    @Test def test_context {
        checkSparse("context.c")
    }
    //    @Test def test_declaration_after_statement_ansi {checkSparse("declaration-after-statement-ansi.c")} //not relevant for other specifications
    //    @Test def test_declaration_after_statement_c89 {checkSparse("declaration-after-statement-c89.c")}
    //    @Test def test_declaration_after_statement_c99 {checkSparse("declaration-after-statement-c99.c")}
    @Ignore("TODO not yet checked")
    @Test def test_declaration_after_statement_default {
        checkSparse("declaration-after-statement-default.c")
    }
    @Test def test_definitions {
        checkSparse("definitions.c")
    }
    @Ignore("warning not checked (initializers)")
    @Test def test_designated_init {
        checkSparse("designated-init.c")
    }
    @Test def test_double_semicolon {
        checkSparse("double-semicolon.c")
    }
    @Ignore("warning not checked (bitwise operations)")
    @Test def test_dubious_bitwise_with_not {
        checkSparse("dubious-bitwise-with-not.c")
    }
    @Test def test_enum_scope {
        checkSparse("enum_scope.c")
    }
    @Test def test_escapes {
        checkSparse("escapes.c")
    }
    @Test def test_extern_inline {
        checkSparse("extern-inline.c")
    }
    @Test def test_field_overlap {
        checkSparse("field-overlap.c")
    }
    @Ignore("__attribute_ bitwise not enforced_")
    @Test def test_foul_bitwise {
        checkSparse("foul-bitwise.c")
    }
    @Test def test_function_pointer_modifier_inheritance {
        checkSparse("function-pointer-modifier-inheritance.c")
    }
    @Test def test_identifier_list {
        checkSparse("identifier_list.c")
    }
    @Test def test_init_char_array {
        checkSparse("init-char-array.c")
    }
    @Ignore("TODO warning not checked (initializer defined twice)")
    @Test def test_initializer_entry_defined_twice {
        checkSparse("initializer-entry-defined-twice.c")
    }
    @Test def test_inline_compound_literals {
        checkSparse("inline_compound_literals.c")
    }
    @Test def test_integer_promotions {
        checkSparse("integer-promotions.c")
    }
    @Test def test_label_asm {
        checkSparse("label-asm.c")
    }
    @Test def test_label_attr {
        checkSparse("label-attr.c")
    }
    @Test def test_label_scope {
        checkSparse("label-scope.c")
    }
    @Test def test_local_label {
        checkSparse("local-label.c")
    }
    @Test def test_logical {
        checkSparse("logical.c")
    }
    @Test def test_member_of_typeof {
        checkSparse("member_of_typeof.c")
    }
    @Test def test_multi_typedef {
        checkSparse("multi_typedef.c")
    }
    @Test def test_nested_declarator {
        checkSparse("nested-declarator.c")
    }
    @Test def test_nested_declarator2 {
        checkSparse("nested-declarator2.c")
    }
    @Ignore("warning not checked (void *p = 0)")
    @Test def test_non_pointer_null {
        checkSparse("non-pointer-null.c")
    }
    @Test def test_old_initializer_nowarn {
        checkSparse("old-initializer-nowarn.c")
    }
    @Ignore("warning not checked (initializer conventions")
    @Test def test_old_initializer {
        checkSparse("old-initializer.c")
    }
    //    @Test def test_outer_scope {checkSparse("outer-scope.c")}//does not apply
    @Test def test_reserved {
        checkSparse("reserved.c")
    }
    @Test def test_restrict_array {
        checkSparse("restrict-array.c")
    }
    @Test def test_restricted_typeof {
        checkSparse("restricted-typeof.c")
    }
    @Ignore("warning not checked (sizeof(_Bool))")
    @Test def test_sizeof_bool {
        checkSparse("sizeof-bool.c")
    }
    @Ignore("TODO not supported sizeof usage on structs")
    @Test def test_sizeof_compound_postfix {
        checkSparse("sizeof-compound-postfix.c")
    }
    @Test def test_specifiers1 {
        checkSparse("specifiers1.c")
    }
    @Test def test_specifiers2 {
        checkSparse("specifiers2.c")
    }
    @Test def test_static_forward_decl {
        checkSparse("static-forward-decl.c")
    }
    @Test def test_struct_as {
        checkSparse("struct-as.c")
    }
    @Test def test_struct_attribute_placement {
        checkSparse("struct-attribute-placement.c")
    }
    @Test def test_struct_ns1 {
        checkSparse("struct-ns1.c")
    }
    //    @Test def test_struct_ns2 {checkSparse("struct-ns2.c")}//broken test case
    @Test def test_struct_size1 {
        checkSparse("struct-size1.c")
    }
    @Test def test_test_be {
        checkSparse("test-be.c")
    }
    @Test def test_type1 {
        checkSparse("type1.c")
    }
    @Test def test_typedef_shadow {
        checkSparse("typedef_shadow.c")
    }
    @Test def test_typeof_attribute {
        checkSparse("typeof-attribute.c")
    }
    @Ignore("ignore signed vs unsigned ints for now TODO")
    @Test def test_typesign {
        checkSparse("typesign.c")
    }
    @Test def test_varargs1 {
        checkSparse("varargs1.c")
    }


}
