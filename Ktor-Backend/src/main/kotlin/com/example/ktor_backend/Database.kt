import java.sql.Connection
import java.sql.DriverManager

object DatabaseFactory {
    private const val JDBC_URL = "jdbc:mysql://mysql.railway.internal:3306/railway"
    private const val USER = "root"
    private const val PASSWORD = "wrCDXIdmCKgSdVCNFZrajmVWxxpeOMoz"

    fun connect(): Connection {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD)
    }
}

