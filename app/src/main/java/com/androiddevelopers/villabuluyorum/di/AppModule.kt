package com.androiddevelopers.villabuluyorum.di


import android.content.Context
import android.content.SharedPreferences
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoImpl
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.service.NotificationAPI
import com.androiddevelopers.villabuluyorum.util.Util.BASE_URL
import com.androiddevelopers.villabuluyorum.util.Util.DATABASE_URL
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
        return FirebaseRepoImpl(auth, firestore ,database, storage,notificationAPI)
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("app_shared_preferences",Context.MODE_PRIVATE)
    }
}