package com.speaktool.bean

class MusicBean {

    var name: String? = null
    var path: String? = null

    constructor() {}

    constructor(name: String, path: String) {
        this.name = name
        this.path = path
    }

    override fun hashCode(): Int {
        return path!!.hashCode()
    }

    override fun equals(o: Any?): Boolean {
        if (o == null)
            return false
        if (o === this)
            return true
        if (o.javaClass != this.javaClass)
            return false
        val input = o as MusicBean?
        return input!!.path == this.path
    }
}
