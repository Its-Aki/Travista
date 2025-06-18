package com.example.travista.remote

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeminiRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PlacesRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeminiOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PlacesOkHttp
