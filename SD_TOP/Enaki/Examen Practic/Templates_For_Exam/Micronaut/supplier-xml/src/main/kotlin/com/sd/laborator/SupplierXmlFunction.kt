package com.sd.laborator;

import io.micronaut.function.executor.FunctionInitializer
import io.micronaut.function.FunctionBean;
import java.util.function.Supplier;

@FunctionBean("supplier-xml")
class SupplierXmlFunction : FunctionInitializer(), Supplier<SupplierXml> {

    override fun get(): SupplierXml {
        val sup = SupplierXml()
        val temp = khttp.get("https://xkcd.com/atom.xml")
        sup.setXml(temp.text)
        return sup
    }
}

/**
 * This main method allows running the function as a CLI application using: echo '{}' | java -jar function.jar 
 * where the argument to echo is the JSON to be parsed.
 */
fun main(args : Array<String>) { 
    val function = SupplierXmlFunction()
    function.run(args) { function.get()}
}