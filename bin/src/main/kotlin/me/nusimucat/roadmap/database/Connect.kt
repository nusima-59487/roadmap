package me.nusimucat.roadmap.database

import me.nusimucat.roadmap.Roadmap

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.logging.Level

class Connect {
    @Throws(SQLException::class)
    fun sqlConnect() {
        val URL: String = SylvDBDetails.getDBPath()
        val dbName: String = SylvDBDetails.getDBName()
        val userName: String = SylvDBDetails.getDBUserName()
        val password: String = SylvDBDetails.getDBPassword()

        SQLConnection = DriverManager.getConnection("jdbc:mysql://$URL/$dbName?autoReconnect=true", userName, password)
        pluginInstance.getServer().getLogger().log(
            Level.FINEST, "Database " + dbName + " loaded" +
                    " successfully!"
        )

        // new SylvBankDBTasks().createTables();
    }

    companion object {
        private val SQLConnection: Connection? = null
        private val pluginInstance: Roadmap = Roadmap.getInstance()
        fun getConnection (): Connection? {return SQLConnection}
    }

}
