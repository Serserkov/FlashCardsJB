package flashcards

import java.io.File
import java.lang.StringBuilder
import java.util.Scanner

class FlashcardCollections (var cards:MutableMap<String, String>, var mistakes: MutableMap<String, Int>, var log: MutableList<String>) {
    val scanner = Scanner(System.`in`)

    fun prinlLog (line: String) {
        println(line)
        log.add(line)
    }

    fun add() {
        prinlLog("The card:")
        val term = scanner.nextLine()
        log.add(term)
        if (cards.containsKey(term)) {
            prinlLog("The card \"$term\" already exists.")
        } else {
            prinlLog("The definition of the card:")
            val definition = scanner.nextLine()
            log.add(definition)
            if (cards.containsValue(definition)) {
                prinlLog("The definition \\\"$definition\\\" already exists.")
            } else {
                cards[term] = definition
                mistakes[term] = 0
                prinlLog("The pair (\"$term\":\"$definition\") has been added.")
            }
        }
    }

    fun remove() {
        prinlLog("The card:")
        val del = scanner.nextLine()
        log.add(del)
        if (cards.containsKey(del)) {
            cards.remove(del)
            mistakes.remove(del)
            prinlLog("The card has been removed.")
        } else {
            prinlLog("Can't remove \"$del\": there is no such card.")
        }
    }

    fun import(fileName: String) {
        if (File("src/$fileName").exists()) {
            val input = File("src/$fileName").readLines()

            for (i in input.indices step 3) {
                cards[input[i]] = input[i + 1]
                mistakes[input[i]] = input[i + 2].toInt()
            }
            prinlLog("${input.size / 3} cards have been loaded.")
        } else prinlLog("File not found")
    }

    fun export(fileName: String) {
        val keys = cards.keys.toList()
        val values = cards.values.toList()
        val mistake = mistakes.values.toList()
        val output =  StringBuilder()
        for (i in keys.indices) output.append("${keys[i]}\n${values[i]}\n${mistake[i]}\n")
        File("src/$fileName").writeText(output.toString())
        prinlLog("${keys.size} cards have been saved.")
    }

    fun asq() {
        prinlLog("How many times to ask?")
        val asqQuantity = scanner.nextLine()
        log.add(asqQuantity)
        for (i in 1..asqQuantity.toInt()) {
            val req = cards.keys.toMutableList().random()
            prinlLog("Print the definition of \"$req\":")
            val answer = scanner.nextLine()
            log.add(answer)
            prinlLog(
                when {
                    answer == cards[req] -> "Correct answer."
                    cards.containsValue(answer) -> {
                        val a = cards.filterValues { it == answer }
                        mistakes[req] = mistakes[req]!!.plus(1)
                        "Wrong answer. The correct one is \"${cards[req]}\", you've just written the definition of \"${a.keys.joinToString()}\"."
                    }
                    else -> {
                        mistakes[req] = mistakes[req]!!.plus(1)
                        "Wrong answer. The correct one is \"${cards[req]}\"."
                    }
                }
            )
        }
    }

    fun reset() {
        for (i in mistakes) i.setValue(0)
    }

    fun hardest() {
        var hardestCard = String()
        var maxMistakes = 0
        for (i in mistakes) if (maxMistakes < i.value) {
            maxMistakes = i.value
            hardestCard = i.key
        }
        if (maxMistakes == 0) {
            prinlLog("There are no cards with errors.")
        } else {
            prinlLog("The hardest card is \"$hardestCard\". You have $maxMistakes errors answering it.")
        }
    }

    fun logging() {
        val output = log.joinToString("\n")
        File("src/todayLog.txt").writeText(output)
    }
}

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    val cardList = FlashcardCollections(mutableMapOf<String, String>(), mutableMapOf<String, Int>(), mutableListOf<String>())
    var exit = false
    for (i in args.indices) if (args[i] == "-import") cardList.import(args[i + 1])
    do {
        cardList.prinlLog("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
        val action = scanner.nextLine()
        cardList.log.add(action)
        when(action) {
            "add" -> cardList.add()
            "remove" -> cardList.remove()
            "import" -> {
                cardList.prinlLog("File name:")
                cardList.import(scanner.nextLine())
            }
            "export" -> {
                cardList.prinlLog("File name:")
                cardList.export(scanner.nextLine())
            }
            "ask" -> cardList.asq()
            "hardest card" -> cardList.hardest()
            "reset stats" -> cardList.reset()
            "log" -> cardList.logging()
            "exit" -> exit = true
            else -> {
                cardList.prinlLog("Unknown command")
            }
        }
    } while (!exit)
    for (i in args.indices) if (args[i] == "-export") cardList.export(args[i + 1])
}
