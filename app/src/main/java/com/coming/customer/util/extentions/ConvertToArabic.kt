package com.coming.merchant.util.extentions


fun getEnglishNumberToArabicNumber(data: String, isCounretCode: Boolean = false): String {
    val words: Array<String> = data.split("").toTypedArray()
    var convertedToAabic = ""

    for (i in 0 until words.size) {
        convertedToAabic += getArabiNumber(words.get(i), isCounretCode)
    }

    return if (isCounretCode) {
        convertedToAabic.trim()
    } else {
        convertedToAabic.trim()
    }

}

fun getArabiNumber(data: String, isCounretCode: Boolean = false): String {
    when (data) {
        "1" -> {
            return "١"
        }
        "2" -> {
            return "٢"
        }
        "3" -> {
            return "٣"
        }
        "4" -> {
            return "٤"
        }
        "5" -> {
            return "٥"
        }
        "6" -> {
            return "٦"
        }
        "7" -> {
            return "٧"
        }
        "8" -> {
            return "٨"
        }
        "9" -> {
            return "٩"
        }
        "0" -> {
            return "٠"
        }
        "+" -> {
            return if (isCounretCode) {
                ""
            } else {
                "+"
            }

        }
        "." -> {
            return "."
        }
        " " -> {
            return " "
        }

    }
    return ""
}