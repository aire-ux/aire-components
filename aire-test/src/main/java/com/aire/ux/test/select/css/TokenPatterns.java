package com.aire.ux.test.select.css;

interface TokenPatterns {


  String UNICODE = "\\\\[0-9a-f]{1,6}(\r\n|[ \n\r\t\f])?";

  String ESCAPE = "%s|\\\\[^\n\r\f0-9a-f]".formatted(UNICODE);

  String NON_ASCII = "[^\0-\177]";

  String NAME_CHARACTER = "[_a-z0-9-]|%s|%s".formatted(NON_ASCII, ESCAPE);

  String NAME_START = "[_a-z]|%s|%s".formatted(NON_ASCII, ESCAPE);

  String NEWLINE = "\n|\r\n|\r|\f";

  String NUMBER = "[0-9]+|[0-9]*\\.[0-9]+";

  String STRING_FORM_1 = "\"([^\n\r\f\\\"]|\\\\%s|%s|%s)*\"".formatted(NEWLINE, NON_ASCII, ESCAPE);

  String STRING_FORM_2 = "\'([^\n\r\f\\\"]|\\\\%s|%s|%s)*\'".formatted(NEWLINE, NON_ASCII, ESCAPE);

  String UNCLOSED_STRING_FORM_1 = "\"([^\n\r\f\\\"]|\\\\%s|%s|%s)*".formatted(NEWLINE, NON_ASCII, ESCAPE);

  String UNCLOSED_STRING_FORM_2 = "\'([^\n\r\f\\\"]|\\\\%s|%s|%s)*".formatted(NEWLINE, NON_ASCII, ESCAPE);

  String IDENTIFIER = "[-]?(%s)(%s)*".formatted(NAME_START, NAME_CHARACTER);

}
