package com.epikron.myglucose.data.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.epikron.myglucose.data.local.GlycemicDatabase
import com.epikron.myglucose.data.local.GlycemicLevelDao
import com.epikron.myglucose.data.repository.DataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Singleton
    @Provides
    fun provideDataRepository(glycemicLevelDao: GlycemicLevelDao): DataRepository =
        DataRepository(glycemicLevelDao)

    @Singleton
    @Provides
    fun provideSharedPrefs(@ApplicationContext appContext: Context): SharedPreferences =
        appContext.getSharedPreferences("app_saved_keys", Context.MODE_PRIVATE)
}

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    fun provideGlycemicLevelDao(glycemicDatabase: GlycemicDatabase): GlycemicLevelDao {
        return glycemicDatabase.glycemicLevelDao()
    }

    @Provides
    @Singleton
    fun provideGlycemicDatabase(@ApplicationContext appContext: Context): GlycemicDatabase {
        return Room.databaseBuilder(
            appContext.applicationContext,
            GlycemicDatabase::class.java,
            "GlycemicDatabase"
        ).build()
    }
}
