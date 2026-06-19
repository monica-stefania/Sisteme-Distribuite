package com.sd.laborator;

import io.micronaut.function.executor.FunctionInitializer
import io.micronaut.function.FunctionBean;
import java.io.File
import java.util.function.Consumer

@FunctionBean("consumer-xml")
class ConsumerXmlFunction : FunctionInitializer(), Consumer<ConsumerXml> {

    override fun accept(p0: ConsumerXml) {

        val regex = """<title>(.*?)</title><link href="(.*?)" rel="alternate"></link>""".toRegex()
        val matchResults = regex.findAll(p0.xml)
        val names = matchResults.map { Pair(it.groupValues[1], it.groupValues[2]) }.toHashSet()

        val builder = StringBuilder()
        names.forEach {
            builder.append("Title: ${it.first}\nLink: ${it.second}\n\n")
        }
        File("consumer_out.txt").writeText(builder.toString())
    }
}

/**
 * This main method allows running the function as a CLI application using: echo '{}' | java -jar function.jar 
 * where the argument to echo is the JSON to be parsed.
 */
fun main(args : Array<String>) { 
    val function = ConsumerXmlFunction()
    function.run(args) { context -> function.accept(context.get(ConsumerXml::class.java))}
}