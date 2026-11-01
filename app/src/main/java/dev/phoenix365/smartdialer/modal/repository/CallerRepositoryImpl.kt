package dev.phoenix365.smartdialer.modal.repository

import dev.phoenix365.smartdialer.modal.data.Contact
import dev.phoenix365.smartdialer.modal.`interface`.ICallerRepository
import dev.phoenix365.smartdialer.modal.`interface`.IContactsRepository

class CallerRepositoryImpl(
    private val contactsRepository: IContactsRepository
) : ICallerRepository {
    override suspend fun getContactByNumber(number: String): Contact? {
        return contactsRepository.getContactByNumber(number)
    }
}
