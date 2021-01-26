import java.io.File
import kotlin.math.log2

val UPPERCASE: Array<Char> = arrayOf(
    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
    'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
)

val LOWERCASE: Array<Char> = arrayOf(
    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
    'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
)

var NUMBERS: Array<Char> = arrayOf(
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
)

var SYMBOLS: Array<Char> = arrayOf(
    '!', '@', '#', '$', '^', '*', '.', '-', '_', '+'
)

abstract class Rule() {
    abstract fun checkRule(password: String)

    open val name: String
        get() = "Simple"

}

class RuleException(message: String) : Exception(message)

class ValidatorException(message: String) : Exception(message)

class LengthRule(len: Int) : Rule() {
    private val len: Int = len

    override fun checkRule(password: String) {
        if (password.length < len) {
            throw RuleException("The password should be no less than $len symbols")
        }
    }
}

class CaseRule() : Rule() {
    override val name: String
        get() = "Register"

    override fun checkRule(password: String) {
        var flag = false

        for (symbol in password) {
            if (LOWERCASE.contains(symbol)) {
                flag = true
                break
            }
        }
        if (!flag) {
            throw RuleException("The password isn't contains the symbols of lowercase")
        }

        flag = false

        for (symbol in password) {
            if (UPPERCASE.contains(symbol)) {
                flag = true
                break
            }
        }
        if (!flag) {
            throw RuleException("The password isn't contains the symbols of uppercase")
        }
    }
}

class NumberRule() : Rule() {
    override val name: String
        get() = "Number"

    override fun checkRule(password: String) {
        var flag = false
        for (symbol in password) {
            if (NUMBERS.contains(symbol)) {
                flag = true
                break
            }
        }
        if (!flag) {
            throw RuleException("There are no numbers in password")
        }
    }
}

class SymbolRule() : Rule() {
    override val name: String
        get() = "Symbol"

    override fun checkRule(password: String) {
        var flag = false

        for (symbol in password) {
            if (SYMBOLS.contains(symbol)) {
                flag = true
                break
            }
        }
        if (!flag) {
            throw RuleException("There are no special symbols in password")
        }
    }
}

class DictionaryRule(fileName: String) : Rule() {
    override val name: String
        get() = "Dictionary"

    private val dictName: String = fileName
    val file = File(dictName)
    val words = file.readText().split("\n")

    override fun checkRule(password: String) {
        for (word in words) {
            if ((password.contains(word)) && (word != "")){
                throw RuleException("There's a dictionary word in password")
            }
        }
    }
}

class EntropyRule() : Rule() {
    override val name: String
        get() = "Entropy"

    var N = 0

    override fun checkRule(password: String) {
        for (symbol in password) {
            if (NUMBERS.contains(symbol)) {
                N += NUMBERS.size
                break
            }
        }
        for (symbol in password) {
            if (SYMBOLS.contains(symbol)) {
                N += NUMBERS.size
                break
            }
        }
        for (symbol in password) {
            if (UPPERCASE.contains(symbol)) {
                N += NUMBERS.size
                break
            }
        }
        for (symbol in password) {
            if (LOWERCASE.contains(symbol)) {
                N += NUMBERS.size
                break
            }
        }

        var H = 0.0

        for (i in 1..N) {
            H += (1.toDouble() / N) * log2((1.toDouble() / N))
        }

        H = -H

        if (H < 5) {
            throw RuleException("The amount of entropy is low")
        }
    }
}

class Validator() {
    private var rules: ArrayList<Rule> = ArrayList()

    fun addRule(rule: Rule) {
        rules.add(rule)
    }

    fun checkPassword(password: String) {
        checkRules()
        for (rule in rules) {
            rule.checkRule(password)
        }
    }

    private fun checkRules() {
        if (rules.size == 0) {
            throw ValidatorException("There are no rules in validator")
        }
        for (rule in rules) {
            var count = 0
            for (rule2 in rules) {
                if (rule2.name == rule.name) {
                    count += 1
                }
            }
            if (count >= 2) {
                throw ValidatorException("The rules are repeating")
            }
        }
    }
}

fun main(args: Array<String>) {
    val password = "eh1fUIDn!ejrj"
    val validator = Validator()
    validator.addRule(LengthRule(6))
    validator.addRule(CaseRule())
    validator.addRule(NumberRule())
    validator.addRule(SymbolRule())
    validator.addRule(DictionaryRule("src/pswd-dict.txt"))
    validator.addRule(EntropyRule())
    validator.checkPassword(password)
}

fun LengthTest() {
    val validator = Validator()
    validator.addRule(LengthRule(6))
    validator.checkPassword("1234567")
    validator.checkPassword("123")
}

fun CaseTest() {
    val validator = Validator()
    validator.addRule(CaseRule())
    validator.checkPassword("UwU")
    validator.checkPassword("uwu")
}

fun NumberTest() {
    val validator = Validator()
    validator.addRule(NumberRule())
    validator.checkPassword("1UwU1")
    validator.checkPassword("uwu")
}

fun SymbolTest() {
    val validator = Validator()
    validator.addRule(SymbolRule())
    validator.checkPassword("-UwU-")
    validator.checkPassword("uwu")
}

fun DictionaryTest() {
    val validator = Validator()
    validator.addRule(DictionaryRule("src/pswd-dict.txt"))
    validator.checkPassword("udfghapofu")
    validator.checkPassword("ashley")
}

fun EntropyTest() {
    val validator = Validator()
    validator.addRule(EntropyRule())
    validator.checkPassword("s*sejhEY-13aH")
    validator.checkPassword("111")
}
