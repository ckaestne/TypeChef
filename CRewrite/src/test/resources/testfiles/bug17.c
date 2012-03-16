/* gcc 4.2.1 inlines it, making code bigger */
static  int
#if (!definedEx(CONFIG_FEATURE_SEAMLESS_GZ) && !definedEx(CONFIG_FEATURE_SEAMLESS_BZ2))
writeTarFile(int tar_fd, int verboseFlag,
 int dereferenceFlag, const int *include,
 const int *exclude)
#endif
#if (definedEx(CONFIG_FEATURE_SEAMLESS_GZ) || definedEx(CONFIG_FEATURE_SEAMLESS_BZ2))
writeTarFile(int tar_fd, int verboseFlag,
	int dereferenceFlag, const int *include,
	const int *exclude, int gzip)
#endif

{
	int errorFlag = ((int) 0);
	struct TarBallInfo tbInfo;

	tbInfo.hlInfoHead = ((void *)0);
	tbInfo.tarFd = tar_fd;
	tbInfo.verboseFlag = verboseFlag;

	/* Store the stat info for the tarball's file, so
	 * can avoid including the tarball into itself....  */
	xfstat(tbInfo.tarFd, &tbInfo.tarFileStatBuf, "can't stat tar file");

#if (definedEx(CONFIG_FEATURE_SEAMLESS_GZ) || definedEx(CONFIG_FEATURE_SEAMLESS_BZ2))
	if (gzip)

#if (definedEx(CONFIG_FEATURE_TAR_CREATE) && (definedEx(CONFIG_FEATURE_SEAMLESS_GZ) || definedEx(CONFIG_FEATURE_SEAMLESS_BZ2)) && (!definedEx(CONFIG_FEATURE_SEAMLESS_GZ) || !definedEx(CONFIG_FEATURE_SEAMLESS_BZ2)))
vfork_compressor(tbInfo.tarFd)
#endif
#if (!definedEx(CONFIG_FEATURE_TAR_CREATE) || (!definedEx(CONFIG_FEATURE_SEAMLESS_GZ) && !definedEx(CONFIG_FEATURE_SEAMLESS_BZ2)) || (definedEx(CONFIG_FEATURE_SEAMLESS_GZ) && definedEx(CONFIG_FEATURE_SEAMLESS_BZ2)))
vfork_compressor(tbInfo.tarFd, gzip)
#endif
;
#endif
	tbInfo.excludeList = exclude;

	/* Read the directory/files and iterate over them one at a time */
	while (include) {
		if (!recursive_action(include->data, ACTION_RECURSE |
				(dereferenceFlag ? ACTION_FOLLOWLINKS : 0),
				writeFileToTarball, writeFileToTarball, &tbInfo, 0)
		) {
			errorFlag = ((int) 1);
		}
		include = include->link;
	}
	/* Write two empty blocks to the end of the archive */
	memset(bb_common_bufsiz1, 0, 2*512);
	xwrite(tbInfo.tarFd, bb_common_bufsiz1, 2*512);

	/* To be pedantically correct, we would check if the tarball
	 * is smaller than 20 tar blocks, and pad it if it was smaller,
	 * but that isn't necessary for GNU tar interoperability, and
	 * so is considered a waste of space */

	/* Close so the child process (if any) will exit */
	close(tbInfo.tarFd);

	/* Hang up the tools, close up shop, head home */
	if (
#if definedEx(CONFIG_FEATURE_CLEAN_UP)
1
#endif
#if !definedEx(CONFIG_FEATURE_CLEAN_UP)
0
#endif
)
		freeHardLinkInfo(&tbInfo.hlInfoHead);

	if (errorFlag)
		bb_error_msg("error exit delayed from previous errors");

#if (definedEx(CONFIG_FEATURE_SEAMLESS_GZ) || definedEx(CONFIG_FEATURE_SEAMLESS_BZ2))
	if (gzip) {
		int status;
		if (safe_waitpid(-1, &status, 0) == -1)
			bb_perror_msg("waitpid");
		else if (!((((__extension__(((union { __typeof(status) __in; int __i; }) { .__in =(status) }).__i))) & 0x7f) == 0) || ((((__extension__ (((union { __typeof(status) __in; int __i; }) { .__in = (status) }).__i))) & 0xff00) >> 8))
			/* gzip was killed or has exited with nonzero! */
			errorFlag = ((int) 1);
	}
#endif
	return errorFlag;
}