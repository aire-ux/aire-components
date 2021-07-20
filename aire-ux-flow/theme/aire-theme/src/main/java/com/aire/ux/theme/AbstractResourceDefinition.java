package com.aire.ux.theme;

public class AbstractResourceDefinition implements ResourceDefinition {

  private final int order;
  private final String url;
  private final String content;
  private final String integrity;

  protected AbstractResourceDefinition(String url) {
    this(0, url, "", "");
  }

  protected AbstractResourceDefinition(String url, String content, String integrity) {
    this(0, url, content, integrity);
  }

  protected AbstractResourceDefinition(int order, String url, String content, String integrity) {
    this.url = url;
    this.order = order;
    this.content = content;
    this.integrity = integrity;
  }

  @Override
  public int getOrder() {
    return order;
  }

  @Override
  public String getUrl() {
    return url;
  }

  @Override
  public String getContent() {
    return content;
  }

  @Override
  public String getIntegrity() {
    return integrity;
  }
}
