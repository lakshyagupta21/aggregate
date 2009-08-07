/*
 * Copyright (C) 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.aggregate.constants;

import org.odk.aggregate.table.ResultTable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Static HTML utility functions used to generate proper HTML
 * for ODK Aggregate visual outputs
 *  
 * @author wbrunette@gmail.com
 *
 */
public class HtmlUtil {

  private static final String INPUT_WIDGET_SIZE_LIMIT = "50";

  private static final String HREF = "href";
  private static final String A = "a";
  private static final String INPUT = "input";
  private static final String ATTR_VALUE = "value";
  private static final String ATTR_NAME = "name";
  private static final String ATTR_TYPE = "type";
  private static final String ATTR_METHOD = "method";
  private static final String ATTR_ENCTYPE = "enctype";
  private static final String ATTR_ACTION = "action";
  private static final String ATTR_SIZE = "size";

  static String createEndTag(String tag) {
    return HtmlConsts.BEGIN_CLOSE_TAG + tag + HtmlConsts.END_TAG;
  }

  static String createBeginTag(String tag) {
    return HtmlConsts.BEGIN_OPEN_TAG + tag + HtmlConsts.END_TAG;
  }

  public static String createUrl(String serverName) {
    return HtmlConsts.HTTP + serverName + BasicConsts.FORWARDSLASH;
  }
  
  public static String createAttribute(String name, String value) {
    return name + BasicConsts.EQUALS + BasicConsts.QUOTE + value + BasicConsts.QUOTE;
  }

  public static String wrapWithHtmlTags(String htmlTag, String text) {
    return createBeginTag(htmlTag) + text + createEndTag(htmlTag);
  }

  public static String createHref(String url, String displayText) {
    return HtmlConsts.BEGIN_OPEN_TAG + A + BasicConsts.SPACE + createAttribute(HREF, url)
        + HtmlConsts.END_TAG + displayText + createEndTag(A);
  }

  public static String createHrefWithProperties(String urlBase, Map<String, String> properties,
      String displayText) {
    return createHref(createLinkWithProperties(urlBase, properties), displayText);
  }

  public static String createLinkWithProperties(String url, Map<String, String> properties) {
    StringBuilder urlBuilder = new StringBuilder();
    urlBuilder.append(url);
    if (properties != null) {
      Set<Map.Entry<String, String>> propSet = properties.entrySet();
      if (!propSet.isEmpty()) {
        urlBuilder.append(ServletConsts.BEGIN_PARAM);
        boolean firstParam = true;
        for (Map.Entry<String, String> property : propSet) {
          if(firstParam) {
            firstParam = false;
          } else {
            urlBuilder.append(ServletConsts.PARAM_DELIMITER);
          }
          String valueEncoded;
          try {
            valueEncoded = URLEncoder.encode(property.getValue(), ServletConsts.ENCODE_SCHEME);
          } catch (UnsupportedEncodingException e) {
            valueEncoded = BasicConsts.EMPTY_STRING;
          }
          urlBuilder.append(property.getKey() + BasicConsts.EQUALS + valueEncoded);
        }
      }
    }
    return urlBuilder.toString();
  }

  public static String createInput(String type, String name, String value) {
    StringBuilder html = new StringBuilder();
    html.append(HtmlConsts.BEGIN_OPEN_TAG + INPUT);
    if (type != null) {
      html.append(BasicConsts.SPACE);
      html.append(createAttribute(ATTR_TYPE, type));
    }
    if (name != null) {
      html.append(BasicConsts.SPACE);
      html.append(createAttribute(ATTR_NAME, name));
    }
    if (value != null) {
      html.append(BasicConsts.SPACE);
      html.append(createAttribute(ATTR_VALUE, value));
    }
    html.append(BasicConsts.SPACE);
    html.append(createAttribute(ATTR_SIZE, INPUT_WIDGET_SIZE_LIMIT));
    html.append(HtmlConsts.END_SELF_CLOSING_TAG);
    return html.toString();
  }

  public static String createFormBeginTag(String action, String encodingType, String method) {
    StringBuilder html = new StringBuilder();
    html.append(HtmlConsts.BEGIN_OPEN_TAG + HtmlConsts.FORM);
    if (action != null) {
      html.append(BasicConsts.SPACE);
      html.append(createAttribute(ATTR_ACTION, action));
    }
    if (encodingType != null) {
      html.append(BasicConsts.SPACE);
      html.append(createAttribute(ATTR_ENCTYPE, encodingType));
    }
    if (method != null) {
      html.append(BasicConsts.SPACE);
      html.append(createAttribute(ATTR_METHOD, method));
    }
    html.append(HtmlConsts.END_TAG);
    return html.toString();
  }

  /**
   * Helper function that creates an html button with the following parameters
   * 
   * @param servletAddr
   *    http action
   * @param label
   *    button's label
   * @param properties
   *    key/value pairs to be encoded as hidden input types to be used as parameters
   * @return
   *    html to generate specified button
   *    
   * @throws UnsupportedEncodingException
   */
  public static String createHtmlButtonToGetServlet(String servletAddr, String label, Map<String,String> properties)
      throws UnsupportedEncodingException {
    StringBuilder html = new StringBuilder();
    html.append(createFormBeginTag(servletAddr, null, ServletConsts.GET));
    
    if(properties != null) {
      Set<Map.Entry<String, String>> propSet = properties.entrySet();
      for(Map.Entry<String, String> property: propSet) {
        String valueEncoded = URLEncoder.encode(property.getValue(), ServletConsts.ENCODE_SCHEME);
        html.append(createInput(HtmlConsts.INPUT_TYPE_HIDDEN, property.getKey(), valueEncoded));
      }
    }
    html.append(createInput(HtmlConsts.INPUT_TYPE_SUBMIT, null, label));
    html.append(HtmlConsts.FORM_CLOSE);
    return html.toString();
  }

  public static String wrapResultTableWithHtmlTags(ResultTable resultTable) {
    StringBuilder html = new StringBuilder();
    html.append(HtmlConsts.TABLE_OPEN);
  
    for (String header : resultTable.getHeader()) {
      html.append(wrapWithHtmlTags(HtmlConsts.TABLE_HEADER, header));
    }
  
    for (List<String> row : resultTable.getRows()) {
      html.append(HtmlConsts.TABLE_ROW_OPEN);
      for (String item : row) {
        html.append(wrapWithHtmlTags(HtmlConsts.TABLE_DATA, item));
      }
      html.append(HtmlConsts.TABLE_ROW_CLOSE);
    }
    html.append(HtmlConsts.TABLE_CLOSE);
    
    return html.toString();
  }

}