#if definedEx(CONFIG_GUNZIP)
int gzip_main(int argc, char **argv)
{
	unsigned opt;

#if definedEx(CONFIG_FEATURE_GZIP_LONG_OPTIONS)
	applet_long_options = gzip_longopts;
#endif
	return bbunpack(argv, pack_gzip, append_ext, "gz");
}
#endif
