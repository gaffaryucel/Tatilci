package com.izmirsoftware.tatilci.di


import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.izmirsoftware.tatilci.R
import com.izmirsoftware.tatilci.repo.FirebaseRepoImpl
import com.izmirsoftware.tatilci.repo.FirebaseRepoInterFace
import com.izmirsoftware.tatilci.repo.RoomProvinceRepo
import com.izmirsoftware.tatilci.repo.SharedPreferencesRepo
import com.izmirsoftware.tatilci.room.dao.ProvinceDao
import com.izmirsoftware.tatilci.room.database.AppRoomDatabase
import com.izmirsoftware.tatilci.service.NotificationAPI
import com.izmirsoftware.tatilci.util.Util.BASE_URL
import com.izmirsoftware.tatilci.util.Util.DATABASE_URL
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }


    @Provides
    @Singleton
    fun provideGlide(context: Context): RequestManager {
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        return Glide.with(context)
            .setDefaultRequestOptions(
                RequestOptions().placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.error)
                    .placeholder(circularProgressDrawable)
            )
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth() = Firebase.auth

    @Provides
    @Singleton
    fun provideStorage() = Firebase.storage

    @Singleton
    @Provides
    fun provideRealtimeDatabase() = Firebase.database(DATABASE_URL)

    @Provides
    @Singleton
    fun provideFirebaseFireStore() = Firebase.firestore


    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideNotificationAPI(retrofit: Retrofit): NotificationAPI {
        return retrofit.create(NotificationAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideFirebaseRepo(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        database: FirebaseDatabase,
        storage: FirebaseStorage,
        notificationAPI: NotificationAPI
    ): FirebaseRepoInterFace {
        return FirebaseRepoImpl(auth, firestore, database, storage, notificationAPI)
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("app_shared_preferences", Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideSharedPreferencesRepo(sharedPreferences: SharedPreferences): SharedPreferencesRepo {
        return SharedPreferencesRepo(sharedPreferences)
    }

    @Singleton
    @Provides
    fun provideAppRoomDatabase(context: Context): AppRoomDatabase {
        return Room.databaseBuilder(context, AppRoomDatabase::class.java, "room-database.sqlite")
            .createFromAsset("room-database.sqlite")
            .build()
    }

    @Singleton
    @Provides
    fun provideProvinceDao(database: AppRoomDatabase): ProvinceDao {
        return database.provinceDao()
    }

    @Singleton
    @Provides
    fun provideRoomProvinceRepo(provinceDao: ProvinceDao): RoomProvinceRepo {
        return RoomProvinceRepo(provinceDao)
    }

}