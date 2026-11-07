// (C) 2026 uchicom
package com.uchicom.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class EnvPropertiesTest {

  private static final File RESOURCE_FILE = new File("src/test/resources/envProperties.properties");

  private static EnvProperties createWithEnv(Map<String, String> env) throws IOException {
    return ResourceUtil.load(
        new EnvProperties() {
          @Override
          String getEnv(String name) {
            return env.get(name);
          }
        },
        RESOURCE_FILE,
        "UTF-8");
  }

  @AfterEach
  public void tearDown() {
    System.clearProperty("db.url");
    System.clearProperty("env.properties.test.key");
  }

  @Test
  public void getProperty_returnsFileValue_whenNoEnvOrSystemProperty() throws IOException {
    var envProperties = ResourceUtil.createEnvProperties(RESOURCE_FILE, "UTF-8");

    assertThat(envProperties.getProperty("db.url")).isEqualTo("jdbc:default");
  }

  @Test
  public void getProperty_returnsSystemProperty_whenSetBeforeLoadAndNoEnvVar() throws IOException {
    // システムプロパティによる上書きはload時に解決されるため、load前に設定する必要がある。
    System.setProperty("db.url", "jdbc:system");
    var envProperties = ResourceUtil.createEnvProperties(RESOURCE_FILE, "UTF-8");

    assertThat(envProperties.getProperty("db.url")).isEqualTo("jdbc:system");
  }

  @Test
  public void getProperty_systemPropertySetAfterLoad_isNotReflected() throws IOException {
    var envProperties = ResourceUtil.createEnvProperties(RESOURCE_FILE, "UTF-8");
    System.setProperty("db.url", "jdbc:system");

    assertThat(envProperties.getProperty("db.url")).isEqualTo("jdbc:default");
  }

  @Test
  public void getProperty_returnsSystemProperty_evenIfEnvVarAndFileValueSet() throws IOException {
    System.setProperty("env.properties.test.key", "sys-value");
    var envProperties = createWithEnv(Map.of("ENV_PROPERTIES_TEST_KEY", "env-value"));

    assertThat(envProperties.getProperty("env.properties.test.key")).isEqualTo("sys-value");
  }

  @Test
  public void getProperty_returnsEnvValue_whenNoSystemPropertySet() throws IOException {
    var envProperties = createWithEnv(Map.of("ENV_PROPERTIES_TEST_KEY", "env-value"));

    assertThat(envProperties.getProperty("env.properties.test.key")).isEqualTo("env-value");
  }

  @Test
  public void getProperty_returnsNull_whenKeyMissingEverywhere() throws IOException {
    var envProperties = ResourceUtil.createEnvProperties(RESOURCE_FILE, "UTF-8");

    assertThat(envProperties.getProperty("unknown.key")).isNull();
  }

  @Test
  public void getPropertyWithDefault_returnsDefault_whenKeyMissingEverywhere() throws IOException {
    var envProperties = ResourceUtil.createEnvProperties(RESOURCE_FILE, "UTF-8");

    assertThat(envProperties.getProperty("unknown.key", "fallback")).isEqualTo("fallback");
  }

  @Test
  public void getPropertyWithDefault_returnsValue_whenPresentInFile() throws IOException {
    var envProperties = ResourceUtil.createEnvProperties(RESOURCE_FILE, "UTF-8");

    assertThat(envProperties.getProperty("db.name", "fallback")).isEqualTo("defaultdb");
  }

  @Test
  public void createEnvProperties_returnsEmpty_whenResourceMissing() throws IOException {
    var envProperties =
        ResourceUtil.createEnvProperties(new File("no/such/resource.properties"), "UTF-8");

    assertThat(envProperties.getProperty("db.url")).isNull();
  }

  @Test
  public void envProperties_isSubclassOfProperties() throws IOException {
    var envProperties = ResourceUtil.createEnvProperties(RESOURCE_FILE, "UTF-8");

    assertThat(envProperties).isInstanceOf(Properties.class);
    assertThat(envProperties.containsKey("db.url")).isTrue();
    assertThat(envProperties.size()).isEqualTo(3);
  }

  @Test
  public void getProperty_returnsEnvValue_whenKeyContainsHyphen() throws IOException {
    var envProperties =
        ResourceUtil.load(
            new EnvProperties() {
              @Override
              String getEnv(String name) {
                return "HYPHEN_KEY".equals(name) ? "env-value" : null;
              }
            },
            new File("src/test/resources/hyphen.properties"),
            "UTF-8");

    assertThat(envProperties.getProperty("hyphen-key")).isEqualTo("env-value");
  }

  @Test
  public void getProperty_returnsRuntimeSetValue_whenNoEnvOrSystemProperty() throws IOException {
    var envProperties = ResourceUtil.createEnvProperties(RESOURCE_FILE, "UTF-8");

    envProperties.setProperty("db.url", "jdbc:runtime");

    assertThat(envProperties.getProperty("db.url")).isEqualTo("jdbc:runtime");
  }
}
