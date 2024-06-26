package token

enum class TokenType {
    IDENTIFIER, // variable name

    // keywords
    LET,
    PRINTLN,
    WHILE,
    RETURN,
    FINAL,
    PUBLIC,
    PRIVATE,
    PROTECTED,

    // types
    STRING_TYPE,
    NUMBER_TYPE,
    BOOLEAN_TYPE,

    // declarations (values)
    NUMERIC_LITERAL,
    STRING_LITERAL,
    BOOLEAN_LITERAL,

    // operators
    EQUALS,
    PLUS,
    MULTIPLY,
    MINUS,
    TIMES,
    DIVIDE,
    GREATER_THAN,
    GREATER_THAN_EQUAL,
    LESSER_THAN,
    LESSER_THAN_EQUAL,
    EQUAL_EQUAL,
    UNEQUAL,

    // symbols
    OPEN_PARENTHESIS,
    OPEN_BRACKET,
    CLOSE_BRACKET,
    CLOSE_PARENTHESIS,
    SEMICOLON,
    COLON,
    WHITESPACE,
    ASSIGN, // Added ASSIGN for assignment
    LITERAL, // Added LITERAL for general literals

    INVALID,
}
