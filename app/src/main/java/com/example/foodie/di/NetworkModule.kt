package com.example.foodie.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.google.firebase.firestore.FirebaseFirestore
import com.example.foodie.repositories.CartRepository
import com.example.foodie.repositories.StallsRepository

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideFirestoreInstance(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideCartRepository(firestore: FirebaseFirestore): CartRepository {
        return CartRepository(firestore)
    }

    @Provides
    @Singleton
    fun provideStallsRepository(firestore: FirebaseFirestore): StallsRepository {
        return StallsRepository(firestore)
    }
}

