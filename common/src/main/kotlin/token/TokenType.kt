package token

enum class TokenType {
    IDENTIFIER, // variable name

    // keywords
    LET,
    CONST,
    PRINTLN,
    READINPUT,
    READENV,
    IF,
    ELSE,
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
    EQUALS_EQUALS,
    UNEQUALS,
    PLUS,
    MULTIPLY,
    MINUS,
    DIVIDE,
    GREATER_THAN,
    GREATER_THAN_EQUAL,
    LESSER_THAN,
    LESSER_THAN_EQUAL,

    // symbols
    OPEN_PARENTHESIS,
    OPEN_BRACKET,
    CLOSE_BRACKET,
    CLOSE_PARENTHESIS,
    SEMICOLON,
    COLON,
    WHITESPACE,
}
