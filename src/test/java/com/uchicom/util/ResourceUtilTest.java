// (C) 2026 uchicom
package com.uchicom.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.junit.jupiter.api.Test;

public class ResourceUtilTest {

  private static final File RESOURCE_FILE = new File("src/test/resources/envProperties.properties");

  @Test
  public void createProperties_loadsFile() throws IOException {
    var properties = ResourceUtil.createProperties(RESOURCE_FILE, "UTF-8");

    assertThat(properties).isInstanceOf(Properties.class).isNotInstanceOf(EnvProperties.class);
    assertThat(properties.getProperty("db.url")).isEqualTo("jdbc:default");
  }

  @Test
  public void createProperties_returnsEmpty_whenFileMissing() throws IOException {
    var properties =
        ResourceUtil.createProperties(new File("no/such/resource.properties"), "UTF-8");

    assertThat(properties.isEmpty()).isTrue();
  }

  @Test
  public void createProperties_loadsInputStream() throws IOException {
    var is = new ByteArrayInputStream("key=value".getBytes(StandardCharsets.UTF_8));

    var properties = ResourceUtil.createProperties(is, "UTF-8");

    assertThat(properties.getProperty("key")).isEqualTo("value");
  }

  @Test
  public void createEnvProperties_loadsFile() throws IOException {
    var properties = ResourceUtil.createEnvProperties(RESOURCE_FILE, "UTF-8");

    assertThat(properties).isInstanceOf(EnvProperties.class);
    assertThat(properties.getProperty("db.url")).isEqualTo("jdbc:default");
  }

  @Test
  public void createEnvProperties_returnsEmpty_whenFileMissing() throws IOException {
    var properties =
        ResourceUtil.createEnvProperties(new File("no/such/resource.properties"), "UTF-8");

    assertThat(properties.isEmpty()).isTrue();
  }

  @Test
  public void createEnvProperties_loadsInputStream() throws IOException {
    var is = new ByteArrayInputStream("key=value".getBytes(StandardCharsets.UTF_8));

    var properties = ResourceUtil.createEnvProperties(is, "UTF-8");

    assertThat(properties.getProperty("key")).isEqualTo("value");
  }

  @Test
  public void createProperties_throwsUnsupportedEncodingException_whenCharsetInvalid() {
    var is = new ByteArrayInputStream("key=value".getBytes(StandardCharsets.UTF_8));

    assertThatThrownBy(() -> ResourceUtil.createProperties(is, "no-such-charset"))
        .isInstanceOf(UnsupportedEncodingException.class);
  }

  @Test
  public void createEnvProperties_throwsUnsupportedEncodingException_whenCharsetInvalid() {
    var is = new ByteArrayInputStream("key=value".getBytes(StandardCharsets.UTF_8));

    assertThatThrownBy(() -> ResourceUtil.createEnvProperties(is, "no-such-charset"))
        .isInstanceOf(UnsupportedEncodingException.class);
  }

  @Test
  public void load_withFile_loadsIntoGivenProperties() throws IOException {
    var properties = ResourceUtil.load(new Properties(), RESOURCE_FILE, "UTF-8");

    assertThat(properties.getProperty("db.url")).isEqualTo("jdbc:default");
  }

  @Test
  public void load_withFile_returnsSameInstance_whenFileMissing() throws IOException {
    var target = new EnvProperties();

    var properties = ResourceUtil.load(target, new File("no/such/resource.properties"), "UTF-8");

    assertThat(properties).isSameAs(target);
    assertThat(properties.isEmpty()).isTrue();
  }

  @Test
  public void load_withInputStream_loadsIntoGivenProperties() throws IOException {
    var is = new ByteArrayInputStream("key=value".getBytes(StandardCharsets.UTF_8));

    var properties = ResourceUtil.load(new EnvProperties(), is, "UTF-8");

    assertThat(properties.getProperty("key")).isEqualTo("value");
  }

  @Test
  public void load_withInputStream_throwsUnsupportedEncodingException_whenCharsetInvalid() {
    var is = new ByteArrayInputStream("key=value".getBytes(StandardCharsets.UTF_8));

    assertThatThrownBy(() -> ResourceUtil.load(new Properties(), is, "no-such-charset"))
        .isInstanceOf(UnsupportedEncodingException.class);
  }
}
