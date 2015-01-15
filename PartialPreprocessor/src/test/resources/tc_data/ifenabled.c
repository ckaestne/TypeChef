//from kconfig.h

#define __ARG_PLACEHOLDER_1 0,
#define config_enabled(cfg) _config_enabled(cfg)
#define _config_enabled(value) __config_enabled(__ARG_PLACEHOLDER_##value)
#define __config_enabled(arg1_or_junk) ___config_enabled(arg1_or_junk 1, 0)
#define ___config_enabled(__ignored, val, ...) val

/*
 * IS_ENABLED(CONFIG_FOO) evaluates to 1 if CONFIG_FOO is set to 'y' or 'm',
 * 0 otherwise.
 *
 */
#define IS_ENABLED(option) \
        (config_enabled(option) || config_enabled(option##_MODULE))

/*
 * IS_BUILTIN(CONFIG_FOO) evaluates to 1 if CONFIG_FOO is set to 'y', 0
 * otherwise. For boolean options, this is equivalent to
 * IS_ENABLED(CONFIG_FOO).
 */
#define IS_BUILTIN(option) config_enabled(option)

/*
 * IS_MODULE(CONFIG_FOO) evaluates to 1 if CONFIG_FOO is set to 'm', 0
 * otherwise.
 */
#define IS_MODULE(option) config_enabled(option##_MODULE)


#ifdef CONFIG_FOO
  #define CONFIG_FOO 1
#endif
#ifdef CONFIG_FOO_MODULE
  #define CONFIG_FOO_MODULE 1
#endif


#if IS_ENABLED(CONFIG_FOO)
  shouldbereachablewithcondition
#endif


#define CONFIG_FOO 1


#if IS_ENABLED(CONFIG_FOO)
  should be on 1
#endif

#undef CONFIG_FOO
#define CONFIG_FOO_MODULE 1


#if IS_ENABLED(CONFIG_FOO)
  should be on 2
#endif

#undef CONFIG_FOO_MODULE

#if IS_ENABLED(CONFIG_FOO)
  should be unreachable
#endif
