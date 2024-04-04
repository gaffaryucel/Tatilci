package com.androiddevelopers.villabuluyorum.data

import android.content.Context
import android.widget.Toast
import java.sql.Connection
import java.sql.DriverManager

object MySqlDbHelper {
    fun connect(context: Context): Connection? {
        var connection: Connection? = null

        try {
//            val url = "jdbc:mysql://127.0.0.1:3306/banka"
//            val user = "root"
//            val password = "23745827"
            val url = "jdbc:mysql://db2575.public.databaseasp.net:3306/db2575"
            val user = "db2575"
            val password = "nT?97_yPiH%8"
            connection = DriverManager.getConnection(url,user, password)

        } catch (e: Exception) {
            Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }

        return connection
    }
}