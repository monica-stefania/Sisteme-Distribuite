package com.sd.laborator.model

class Person(private var id: Int, private var firstName: String, private var lastName: String, private var Age: Int, private var timeStamp: String) {

    var personID: Int
        get() {
            return id
        }
        set(value) {
            id = value
        }

    var firstname: String
        get() {
            return firstName
        }
        set(value) {
            firstName = value
        }

    var lastname: String
        get() {
            return lastName
        }
        set(value) {
            lastName = value
        }

    var age: Int
        get() {
            return Age
        }
        set(value) {
            Age = value
        }

    var timestamp: String
        get() {
            return timeStamp
        }
        set(value) {
            timeStamp = value
        }

    fun hasAge(value: Int): Boolean {
        return value == Age
    }

    override fun toString(): String {
        return "Person [id=$personID, firstName=$firstName, lastName=$lastName, age=$Age, timestamp=$timestamp]"
    }

}