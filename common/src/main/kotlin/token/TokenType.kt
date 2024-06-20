package token

enum class TokenType {
    IDENTIFIER, // variable name: x por ej

    // keywords
    LET,
    CONST,
    PRINTLN,
    READINPUT,
    READENV,
    IF,
    ELSE,
    WHILE,
    RETURN,
    FINAL,
    PUBLIC,
    PRIVATE,
    PROTECTED,

    // types
    STRING_TYPE, // String
    NUMBER_TYPE, // Int
    BOOLEAN_TYPE,

    // declarations (values)
    NUMERIC_LITERAL, // 5
    STRING_LITERAL, // "Hello"
    BOOLEAN_LITERAL,

    // operators
    EQUALS,
    EQUALS_EQUALS,
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
