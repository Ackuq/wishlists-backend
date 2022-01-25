package io.github.ackuq.conf

import io.bkbn.kompendium.auth.configuration.JwtAuthConfiguration

object SecurityConfigurations {
    object Names {
        const val DEFAULT = "default"
        const val ADMIN = "admin"
    }

    val default = object : JwtAuthConfiguration {
        override val name = Names.DEFAULT
    }

    val admin = object : JwtAuthConfiguration {
        override val name = Names.ADMIN
    }
}
