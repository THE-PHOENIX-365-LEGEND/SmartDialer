package dev.phoenix365.smartdialer.modal.`interface`

import dev.phoenix365.smartdialer.modal.data.Contact

interface ICallerRepository {
    suspend fun getContactByNumber(number: String): Contact?
}
