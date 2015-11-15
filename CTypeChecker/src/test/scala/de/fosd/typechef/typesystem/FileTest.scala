package de.fosd.typechef.typesystem

import org.junit._
import java.io.{InputStream, FileNotFoundException}
import de.fosd.typechef.parser.c.{TestHelper, TranslationUnit}
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureModel}

class FileTest extends TestHelper {

    val folder = "testfiles/"
    private def check(filename: String, featureExpr: FeatureModel = FeatureExprFactory.default.featureModelFactory.empty): Boolean = {
        val start = System.currentTimeMillis
        println("parsing " + filename)
        var inputStream: InputStream = getClass.getResourceAsStream("/" + folder + filename)
        if (inputStream == null) {
            throw new FileNotFoundException("Input file not found: " + filename)
        }
        val ast = parseFile(inputStream, filename, folder)
        val parsed = System.currentTimeMillis
        println("type checking " + filename + " (" + (parsed - start) + ")")
        val r = check(ast, featureExpr)
        println("done. (" + (System.currentTimeMillis - parsed) + ")")
        r
    }
    private def check(ast: TranslationUnit, featureModel: FeatureModel): Boolean = new CTypeSystemFrontend(ast, featureModel).checkAST().filterNot(_.isWarning).isEmpty

    private def d(n: String) = FeatureExprFactory.createDefinedExternal(n)

    //async.i
    @Ignore def test1 {
        assert(check("test1.xi"))
    }
    @Test def busybox_ar {
        assert(check("ar.xi"))
    }
    @Test def boa_boa {
        assert(check("boa.xi"))
    }
    @Test def boa_boa_pi {
        assert(check("boa.pi"))
    }
    @Test def busybox_top_pi {
        assert(check("top.pi"))
    }
    @Test def busybox_umount_pi {
        assert(check("umount.pi"))
    }
    @Test def busybox_udf_pi {
        assert(check("udf.pi"))
    }
    @Ignore("not finished yet")
    @Test def linux_fork_pi {
        assert(check("fork_.pi" /*, FeatureModel.create(linux_fork_fm)*/))
    }
    @Test def toybox_patch_pi {
        assert(check("patch.pi"))
    }
    @Test def toybox_netcat_pi {
        assert(check("netcat.pi"))
    }
    @Test def busybox_modutils_pi {
        assert(check("modutils-24.pi"))
    }
    @Test def busybox_smemcap_pi {
        assert(check("smemcap.pi"))
    }
    //
    //    val linux_fork_fm = ((d("CONFIG_SMP") implies d("CONFIG_X86_LOCAL_APIC")) and
    //            (d("CONFIG_PARAVIRT_SPINLOCKS") implies d("CONFIG_PARAVIRT")) and
    //            (d("CONFIG_PARAVIRT_DEBUG") implies d("CONFIG_PARAVIRT")) and
    //            (d("CONFIG_PARAVIRT_SPINLOCKS") implies d("CONFIG_SMP")) and
    //            (d("CONFIG_SYMBOL_PREFIX").not)
    //            and (d("CONFIG_DISCONTIGMEM") implies d("CONFIG_NEED_MULTIPLE_NODES")) //from FM
    //            and (d("CONFIG_DISCONTIGMEM") implies d("CONFIG_SMP")) //from FM
    //            and (d("CONFIG_DISCONTIGMEM") implies d("CONFIG_X86_PAE")) //from FM
    //            and (d("CONFIG_MEMORY_HOTPLUG") implies d("CONFIG_SPARSEMEM")) //from FM
    //            and (d("CONFIG_HOTPLUG_CPU") implies d("CONFIG_SMP")) //from FM
    //            and (d("CONFIG_PROC_KCORE") implies d("CONFIG_PROC_FS")) //from FM
    //            and (d("CONFIG_NEED_MULTIPLE_NODES") implies d("CONFIG_SMP")) //from FM
    //            and (d("CONFIG_BLK_DEV_DRBD") implies d("CONFIG_BLOCK")) //from FM
    //            and (d("CONFIG_BLK_DEV_INTEGRITY") implies d("CONFIG_BLOCK")) //from FM
    //            and (d("CONFIG_BLK_DEV_LOOP") implies d("CONFIG_BLOCK")) //from FM
    //            and (d("CONFIG_BLK_DEV_RAM") implies d("CONFIG_BLOCK")) //from FM
    //            and (d("CONFIG_BLK_DEV_IO_TRACE") implies d("CONFIG_BLOCK")) //from FM
    //            and (d("CONFIG_EXT3_FS") implies d("CONFIG_BLOCK")) //from FM
    //            and (d("CONFIG_EXT4_FS") implies d("CONFIG_BLOCK")) //from FM
    //            and (d("CONFIG_JBD") implies d("CONFIG_BLOCK")) //from FM
    //            and (d("CONFIG_JBD2") implies d("CONFIG_BLOCK")) //from FM
    //            and (d("CONFIG_DM_LOG_USERSPACE") implies d("CONFIG_BLOCK")) //from FM
    //            and (d("CONFIG_DM_MULTIPATH_ST") implies d("CONFIG_BLOCK")) //from FM
    //            and (d("CONFIG_MD_FAULTY") implies d("CONFIG_BLOCK")) //from FM
    //            and (d("CONFIG_MD_LINEAR") implies d("CONFIG_BLOCK")) //from FM
    //            and (d("CONFIG_BLK_DEV_MD") implies d("CONFIG_BLOCK")) //from FM
    //            and (d("CONFIG_MD_MULTIPATH") implies d("CONFIG_BLOCK")) //from FM
    //            and (d("CONFIG_MD_RAID0") implies d("CONFIG_BLOCK")) //from FM
    //            and (d("CONFIG_MD_RAID10") implies d("CONFIG_BLOCK")) //from FM
    //            and (d("CONFIG_MD_RAID1") implies d("CONFIG_BLOCK")) //from FM
    //            and (d("CONFIG_MD_RAID456") implies d("CONFIG_BLOCK")) //from FM
    //            and (d("CONFIG_NFTL") implies d("CONFIG_BLOCK")) //from FM
    //            and (d("CONFIG_NILFS2_FS") implies d("CONFIG_BLOCK")) //from FM
    //            and (d("CONFIG_OCFS2_FS") implies d("CONFIG_BLOCK")) //from FM
    //            and (d("CONFIG_EXOFS_FS") implies d("CONFIG_BLOCK")) //from FM
    //            and (d("CONFIG_BOUNCE") implies d("CONFIG_BLOCK")) //from FM
    //            and (d("CONFIG_I2C_SCMI") implies d("CONFIG_ACPI")) //from FM
    //            and (d("CONFIG_MD") implies d("CONFIG_BLOCK")) //from FM
    //            and (d("CONFIG_GFS2_FS") implies d("CONFIG_BLOCK")) //from FM
    //            and (d("CONFIG_MTD_UBI_DEBUG_PARANOID") implies d("CONFIG_MTD_UBI_DEBUG")) //from FM
    //            and (d("CONFIG_GENERIC_PENDING_IRQ") implies d("CONFIG_SMP")) //from FM
    //            and (d("CONFIG_PROVE_LOCKING") implies d("CONFIG_DEBUG_SPINLOCK")) //from FM
    //            and (d("CONFIG_NET_EMATCH_U32") implies d("CONFIG_NET_EMATCH")) //from FM
    //            and (d("CONFIG_NET_EMATCH_CMP") implies d("CONFIG_NET_EMATCH")) //from FM
    //            and (d("CONFIG_NET_EMATCH_META") implies d("CONFIG_NET_EMATCH")) //from FM
    //            and (d("CONFIG_NET_EMATCH_NBYTE") implies d("CONFIG_NET_EMATCH")) //from FM
    //            and (d("CONFIG_NET_EMATCH_TEXT") implies d("CONFIG_NET_EMATCH")) //from FM
    //            and (d("CONFIG_X86_USE_3DNOW") mex d("CONFIG_KMEMCHECK")) //from FM
    //            and (d("CONFIG_TOUCHSCREEN_AD7879") implies (d("CONFIG_TOUCHSCREEN_AD7879_I2C") or d("CONFIG_TOUCHSCREEN_AD7879_SPI"))) //from FM
    //            and (d("CONFIG_TOUCHSCREEN_AD7879") equiv (d("CONFIG_TOUCHSCREEN_AD7879_I2C") or d("CONFIG_TOUCHSCREEN_AD7879_SPI"))) //from FM
    //            and (d("CONFIG_NET_EMATCH_CMP") implies d("CONFIG_NET_EMATCH")) //from FM
    //            and (d("CONFIG_NET_EMATCH_NBYTE") implies d("CONFIG_NET_EMATCH")) //from FM
    //            and (d("CONFIG_NET_EMATCH_TEXT") implies d("CONFIG_NET_EMATCH")) //from FM
    //            and (d("CONFIG_NET_EMATCH_U32") implies d("CONFIG_NET_EMATCH")) //from FM
    //            and (d("CONFIG_DEBUG_LOCK_ALLOC") implies d("CONFIG_LOCKDEP")) //from FM
    //            and (d("CONFIG_IRQSOFF_TRACER") implies d("CONFIG_TRACE_IRQFLAGS")) //from FM
    //            and (d("CONFIG_HUGETLBFS") equiv d("CONFIG_HUGETLB_PAGE")) //from FM
    //            and (d("CONFIG_DEBUG_LOCK_ALLOC") implies d("CONFIG_LOCKDEP"))
    //            and (d("CONFIG_PROVE_LOCKING") implies d("CONFIG_DEBUG_LOCK_ALLOC"))
    //            and (d("CONFIG_LOCKDEP") implies d("CONFIG_DEBUG_LOCK_ALLOC"))
    //            and (d("CONFIG_PARAVIRT_SPINLOCKS") implies d("CONFIG_SMP"))
    //            and (d("CONFIG_PARAVIRT_SPINLOCKS") implies d("CONFIG_PARAVIRT"))
    //            )

}