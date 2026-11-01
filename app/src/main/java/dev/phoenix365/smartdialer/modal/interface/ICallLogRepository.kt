package dev.phoenix365.smartdialer.modal.`interface`

import dev.phoenix365.smartdialer.modal.data.CallLogEntry

interface ICallLogRepository {
    suspend fun getCallLogs(): List<CallLogEntry>
    suspend fun saveCallLog(entry: CallLogEntry)
    suspend fun deleteCallLog(number: String)
    suspend fun deleteCallLogsByIds(ids: List<Long>)
    suspend fun clearCallLogs()
}