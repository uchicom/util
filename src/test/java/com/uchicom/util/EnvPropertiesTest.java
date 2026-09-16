// (C) 2026 uchicom
package com.uchicom.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class EnvPropertiesTest {

  @AfterEach
  public void tearDown() {
    System.clearProperty("db.url");
    System.clearProperty("env.properties.test.key");
  }

  @Test
  public void getProperty_returnsFileValue_whenNoEnvOrSystemProperty() {
    var envProperties = new EnvProperties("envProperties.properties");

    assertThat(envProperties.getProperty("db.url")).isEqualTo("jdbc:default");
  }

  @Test
  public void getProperty_returnsSystemProperty_whenSetAndNoEnvVar() {
    var envProperties = new EnvProperties("envProperties.properties");
    System.setProperty("db.url", "jdbc:system");

    assertThat(envProperties.getProperty("db.url")).isEqualTo("jdbc:system");
  }

  @Test
  public void getProperty_returnsEnvValue_evenIfSystemPropertyAndFileValueSet() {
    // ENV_PROPERTIES_TEST_KEY=env-value is configured for the test JVM via the
    // maven-surefire-plugin <environmentVariables> in pom.xml.
    var envProperties = new EnvProperties("envProperties.properties");
    System.setProperty("env.properties.test.key", "sys-value");

    assertThat(envProperties.getProperty("env.properties.test.key")).isEqualTo("env-value");
  }

  @Test
  public void getProperty_returnsNull_whenKeyMissingEverywhere() {
    var envProperties = new EnvProperties("envProperties.properties");

    assertThat(envProperties.getProperty("unknown.key")).isNull();
  }

  @Test
  public void getPropertyWithDefault_returnsDefault_whenKeyMissingEverywhere() {
    var envProperties = new EnvProperties("envProperties.properties");

    assertThat(envProperties.getProperty("unknown.key", "fallback")).isEqualTo("fallback");
  }

  @Test
  public void getPropertyWithDefault_returnsValue_whenPresentInFile() {
    var envProperties = new EnvProperties("envProperties.properties");

    assertThat(envProperties.getProperty("db.name", "fallback")).isEqualTo("defaultdb");
  }

  @Test
  public void constructor_doesNotThrow_whenResourceMissing() {
    var envProperties = new EnvProperties("no/such/resource.properties");

    assertThat(envProperties.getProperty("db.url")).isNull();
  }

  @Test
  public void envProperties_isSubclassOfProperties() {
    var envProperties = new EnvProperties("envProperties.properties");

    assertThat(envProperties).isInstanceOf(Properties.class);
    assertThat(envProperties.containsKey("db.url")).isTrue();
    assertThat(envProperties.size()).isEqualTo(3);
  }

  @Test
  public void getProperty_returnsRuntimeSetValue_whenNoEnvOrSystemProperty() {
    var envProperties = new EnvProperties("envProperties.properties");

    envProperties.setProperty("db.url", "jdbc:runtime");

    assertThat(envProperties.getProperty("db.url")).isEqualTo("jdbc:runtime");
  }
}
