package io.appwrite.locale

import io.appwrite.client.Appwrite
import io.appwrite.client.ServiceBase
import io.appwrite.core.models.ContinentList
import io.appwrite.core.models.CountryList
import io.appwrite.core.models.CurrencyList
import io.appwrite.core.models.LanguageList
import io.appwrite.core.models.LocaleCode
import io.appwrite.core.models.LocaleCodeList
import io.appwrite.core.models.LocaleData
import io.appwrite.core.models.PhoneList
import io.appwrite.core.result.AppwriteResult

/**
 * Locale service — geographic and language data.
 *
 * Usage:
 * ```
 * val locale = appwrite.locale
 *
 * // Get user locale
 * locale.get()
 *
 * // List countries
 * locale.listCountries()
 * ```
 */
class Locale(appwrite: Appwrite) : ServiceBase(appwrite.transport) {

    suspend fun get(): AppwriteResult<LocaleData> =
        get(path = "/locale")

    suspend fun listCodes(): AppwriteResult<LocaleCodeList> =
        get(path = "/locale/codes")

    suspend fun listLanguages(): AppwriteResult<LanguageList> =
        get(path = "/locale/languages")

    suspend fun listCountries(): AppwriteResult<CountryList> =
        get(path = "/locale/countries")

    suspend fun listCountriesEU(): AppwriteResult<CountryList> =
        get(path = "/locale/countries/eu")

    suspend fun listCountriesPhones(): AppwriteResult<PhoneList> =
        get(path = "/locale/countries/phones")

    suspend fun listCurrencies(): AppwriteResult<CurrencyList> =
        get(path = "/locale/currencies")

    suspend fun listContinents(): AppwriteResult<ContinentList> =
        get(path = "/locale/continents")
}

/**
 * Extension property: `appwrite.locale`
 */
val Appwrite.locale: Locale get() = Locale(this)
