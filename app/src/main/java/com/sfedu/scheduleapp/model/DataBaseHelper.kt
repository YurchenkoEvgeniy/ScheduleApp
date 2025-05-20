package com.sfedu.scheduleapp.model.mvp

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.ConnectivityManager
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val databasePath by lazy { context.getDatabasePath(DATABASE_NAME).path }
    private val appContext = context.applicationContext
    private val tempDatabasePath by lazy { File(appContext.filesDir, "temp_$DATABASE_NAME").path}

    init {
        // Проверяем, существует ли БД, и копируем её из assets при необходимости
        if (!checkDatabase()) {
            copyDatabaseFromAssets(context)
        }
    }

    private fun checkDatabase(): Boolean {
        val dbFile = File(databasePath)
        return dbFile.exists() && dbFile.length() > 0
    }

    private fun copyDatabaseFromAssets(context: Context) {
        try {
            val inputStream = context.assets.open(DATABASE_NAME)
            val outputFile = File(databasePath)
            outputFile.parentFile?.mkdirs()
            val outputStream = FileOutputStream(databasePath)
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()

            Log.d("DBCopy", "Database copied successfully. Size: ${outputFile.length()} bytes")
        } catch (e: IOException) {
            Log.e("DBCopy", "Error copying database", e)
        }
    }


    override fun onCreate(db: SQLiteDatabase) {
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        downloadAndUpdateDatabase(db, oldVersion, newVersion)
    }

    private fun downloadAndUpdateDatabase(oldDb: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val githubRawUrl = "https://raw.githubusercontent.com/YurchenkoEvgeniy/RaspisanieApp/main/fullDB.db"
        val tempFile = File(tempDatabasePath)

        DownloadFile(tempFile) { success ->
            if (success) {
                try {
                    // Проверяем новую БД перед использованием
                    val newDb = SQLiteDatabase.openDatabase(tempFile.path, null, SQLiteDatabase.OPEN_READONLY)

                    migrateData(oldDb, tempFile.path)
                    Log.d("DBUpdate", "Database updated successfully")

                } catch (e: Exception) {
                    Log.e("DBUpdate", "Error verifying new database", e)
                    tempFile.delete()
                }
            } else {
                Log.e("DBUpdate", "Failed to download new database")
            }
        }.execute(githubRawUrl)
    }

    private fun migrateData(oldDb: SQLiteDatabase, newDbPath: String) {
        // 1. Создаем резервную копию старой БД
        val backupPath = "$databasePath.backup"
        File(databasePath).copyTo(File(backupPath), overwrite = true)

        try {
            // 2. Закрываем старую БД
            oldDb.close()

            // 3. Заменяем старую БД новой
            File(newDbPath).copyTo(File(databasePath), overwrite = true)

            // 4. Открываем новую БД
            SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READWRITE).use { newDb ->
                // Здесь можно выполнить дополнительные миграции данных если нужно
                Log.d("DBMigrate", "Database migration completed successfully")
            }

            // 5. Удаляем временные файлы
            File(newDbPath).delete()
            File(backupPath).delete()
        } catch (e: Exception) {
            // В случае ошибки восстанавливаем из резервной копии
            Log.e("DBMigrate", "Migration failed, restoring backup", e)
            File(backupPath).copyTo(File(databasePath), overwrite = true)
            File(backupPath).delete()
            throw RuntimeException("Database migration failed", e)
        }
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun checkForUpdates() {
        if (isInternetAvailable(appContext)) {
            // Принудительно вызываем проверку обновлений
            val db = writableDatabase
            try {
                val currentVersion = db.version
                onUpgrade(db, currentVersion, DATABASE_VERSION)
            } finally {
                db.close()
            }
        }
    }

    fun getColumns(): Array<String> {
        val rd = readableDatabase.rawQuery("SELECT * FROM MONDAY", null)
        return rd.columnNames
    }

    fun getLessons(groupNumber: String = "'4.6'", weekDay: String = "MONDAY"): Cursor {
        //Log.d("monday", "SELECT $weekDay.$groupNumber as group_1 FROM $weekDay")
        return readableDatabase.rawQuery("SELECT $weekDay.$groupNumber as group_1 FROM $weekDay", null)
    }

    companion object {
        private const val DATABASE_NAME = "fullDB.db"
        private const val DATABASE_VERSION = 3
        private const val DATABASE_DOWNLOAD_URL = "https://raw.githubusercontent.com//YurchenkoEvgeniy/RaspisanieApp/raw/main/fullDB.db"
    }
}