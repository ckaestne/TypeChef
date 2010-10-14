package boa;

import org.anarres.cpp.Main;

public class PreprocessBoa {
	public static void main(String[] args) {
		String[] fileList = new String[] { "alias", "boa", "buffer", "cgi",
				"cgi_header", "config", "escape", "get", "hash", "ip", "log",
				"mmap_cache", "pipe", "queue", "read", "request", "response",
				"select", "signals", "util", "sublog" };

		for (String file : fileList)
			try {
				Main.main(new String[] {
						"W:\\work\\TypeChef\\boa\\src\\" + file + ".c", //
						"-o",
						"W:\\work\\TypeChef\\boa\\src\\" + file + ".pi",//
						"--include",
						"W:\\work\\TypeChef\\boa\\src\\cygwin.h",//
						"-p",
						"_", //
						"-I",
						"W:\\work\\TypeChef\\boa\\src", //
						"-I",
						"C:\\cygwin\\usr\\include", //
						"-I",
						"C:\\cygwin\\lib\\gcc\\i686-pc-cygwin\\3.4.4\\include",//
						"-U", "HAVE_LIBDMALLOC" });
			} catch (Exception e) {
				e.printStackTrace();
			}

	}
}
