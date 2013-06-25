package de.fosd.typechef.crefactor.util;

import org.apache.commons.io.IOUtils;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration class to handle and configure CJRefactor and TypeChef.
 */
public final class Configuration {

    /**
     * For convenient use only one instance of this class is allowed. Therefore the singleton design pattern
     * is used.
     */
    private static Configuration instance = new Configuration();

    /**
     * Name of the configuration file.
     */
    private final String configFileName = "config.properties";

    /**
     * Name of the typechef configuration file.
     */
    private final String typeChefConfig = "typechef.properties";

    /**
     * The referenced configuration properties object.
     */
    private Properties configuration;

    /**
     * Load resource bundle.
     */
    private Configuration() {
        this.configuration = new Properties();
        FileReader reader = null;
        try {
            reader = new FileReader(Configuration.class.getResource(configFileName).getFile());
            this.configuration.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    /**
     * Retrieves an instance of this class.
     *
     * @return an instance of this class.
     */
    public static Configuration getInstance() {
        return instance;
    }

    /**
     * Retrieves a configuration property.
     *
     * @param key configuration property to look for.
     * @return the configuration property.
     */
    public String getConfig(final String key) {
        return configuration.getProperty(key);
    }

    /**
     * Retrieves a configuration property as string value.
     *
     * @param key configuration value to look for.
     * @return the configuration property as integer
     */
    public int getConfigAsInt(final String key) {
        return Integer.parseInt(configuration.getProperty(key));
    }

    /**
     * Retrieves the path of the typechef configuration properties file.
     *
     * @return the typechef configuration properties filepath
     */
    public String getTypeChefConfigFilePath() {
        return Configuration.class.getResource(typeChefConfig).getFile();
    }
}
