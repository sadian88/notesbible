package com.notesbible.app

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.notesbible.app.data.BibleRepository
import com.notesbible.app.data.OfflineFirstBibleRepository
import com.notesbible.app.data.local.BibleDatabase
import com.notesbible.app.data.models.DefaultBibleVersions
import com.notesbible.app.data.remote.BibleApi
import com.notesbible.app.data.remote.BibleJsonParser
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class NotesBibleApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

class AppContainer(context: Context) {
    private val database: BibleDatabase = Room.databaseBuilder(
        context,
        BibleDatabase::class.java,
        "notesbible.db"
    ).fallbackToDestructiveMigration().build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BASIC)
            }
        )
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://raw.githubusercontent.com/")
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    private val bibleApi: BibleApi = retrofit.create(BibleApi::class.java)
    private val parser = BibleJsonParser()

    val repository: BibleRepository = OfflineFirstBibleRepository(
        bibleApi = bibleApi,
        parser = parser,
        versionDao = database.bibleVersionDao(),
        verseDao = database.verseDao(),
        availableVersions = DefaultBibleVersions,
        database = database
    )
}
