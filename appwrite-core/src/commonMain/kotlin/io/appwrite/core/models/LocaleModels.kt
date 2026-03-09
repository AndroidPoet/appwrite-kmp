package io.appwrite.core.models

import kotlinx.serialization.Serializable

@Serializable
data class LocaleData(
    val ip: String,
    val countryCode: String,
    val country: String,
    val continentCode: String = "",
    val continent: String = "",
    val eu: Boolean = false,
    val currency: String = "",
)

@Serializable
data class Country(val name: String, val code: String)

@Serializable
data class CountryList(val total: Int, val countries: List<Country>)

@Serializable
data class Language(val name: String, val code: String, val nativeName: String)

@Serializable
data class LanguageList(val total: Int, val languages: List<Language>)

@Serializable
data class Currency(
    val symbol: String,
    val name: String,
    val symbolNative: String,
    val decimalDigits: Int,
    val rounding: Double,
    val code: String,
    val namePlural: String,
)

@Serializable
data class CurrencyList(val total: Int, val currencies: List<Currency>)

@Serializable
data class Continent(val name: String, val code: String)

@Serializable
data class ContinentList(val total: Int, val continents: List<Continent>)

@Serializable
data class Phone(val code: String, val countryCode: String, val countryName: String)

@Serializable
data class PhoneList(val total: Int, val phones: List<Phone>)

@Serializable
data class LocaleCode(val code: String, val name: String)

@Serializable
data class LocaleCodeList(val total: Int, val localeCodes: List<LocaleCode>)
