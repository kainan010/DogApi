package com.naniak.whatsupdog.di

import androidx.room.Room
import com.naniak.whatsupdog.data.local.AppDatabase
import com.naniak.whatsupdog.data.remote.DogApiService
import com.naniak.whatsupdog.data.remote.RetrofitClient
import com.naniak.whatsupdog.data.repository.DogRepositoryImpl
import com.naniak.whatsupdog.data.repository.FavoriteRepositoryImpl
import com.naniak.whatsupdog.domain.repository.DogRepository
import com.naniak.whatsupdog.domain.repository.FavoriteRepository
import com.naniak.whatsupdog.domain.usecase.GetAllBreedsUseCase
import com.naniak.whatsupdog.domain.usecase.GetBreedImagesUseCase
import com.naniak.whatsupdog.domain.usecase.GetFavoritesUseCase
import com.naniak.whatsupdog.domain.usecase.GetRandomDogUseCase
import com.naniak.whatsupdog.domain.usecase.IsFavoriteUseCase
import com.naniak.whatsupdog.domain.usecase.ToggleFavoriteUseCase
import com.naniak.whatsupdog.presentation.screens.breeds.BreedsViewModel
import com.naniak.whatsupdog.presentation.screens.favorites.FavoritesViewModel
import com.naniak.whatsupdog.presentation.screens.gallery.GalleryViewModel
import com.naniak.whatsupdog.presentation.screens.home.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    single<DogApiService> {
        RetrofitClient.instance.create(DogApiService::class.java)
    }
}

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "whatsupdog_database"
        ).build()
    }

    single { get<AppDatabase>().favoriteDao() }
}

val repositoryModule = module {
    singleOf(::DogRepositoryImpl) bind DogRepository::class
    singleOf(::FavoriteRepositoryImpl) bind FavoriteRepository::class
}

val useCaseModule = module {
    factoryOf(::GetRandomDogUseCase)
    factoryOf(::GetAllBreedsUseCase)
    factoryOf(::GetBreedImagesUseCase)
    factoryOf(::ToggleFavoriteUseCase)
    factoryOf(::GetFavoritesUseCase)
    factoryOf(::IsFavoriteUseCase)
}

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::BreedsViewModel)
    viewModelOf(::GalleryViewModel)
    viewModelOf(::FavoritesViewModel)
}
