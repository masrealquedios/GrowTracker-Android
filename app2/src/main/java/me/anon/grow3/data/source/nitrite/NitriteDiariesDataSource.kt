package me.anon.grow3.data.source.nitrite

import com.fasterxml.jackson.datatype.joda.JodaModule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.anon.grow3.data.model.Diary
import me.anon.grow3.data.model.Log
import me.anon.grow3.data.source.DiariesDataSource
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.getRepository
import org.dizitart.kno2.nitrite
import java.io.File
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class NitriteDiariesDataSource @Inject constructor(
	@Named("garden_source") private val sourcePath: String,
	@Named("io_dispatcher") private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : DiariesDataSource
{
	private val db = nitrite {
		file = File(sourcePath)
		autoCommit = true
		autoCommitBufferSize = 1024
		registerModule(JodaModule())
	}

	override fun close()
	{
		db.close()
	}

	override suspend fun addDiary(diary: Diary): List<Diary>
	{
		withContext(dispatcher) {
			db.getRepository<Diary> {
				insert(diary)
			}
			db.commit()
		}

		return getDiaries()
	}

	override suspend fun deleteDiary(diaryId: String): List<Diary>
	{
		val repo = db.getRepository<Diary>()
		repo.remove(Diary::id eq diaryId)
		return getDiaries()
	}

	override suspend fun getDiaryById(diaryId: String): Diary?
	{
		val repo = db.getRepository<Diary>()
		return repo.find(Diary::id eq diaryId).firstOrNull()
	}

	override suspend fun getDiaries(): List<Diary> = db.getRepository<Diary>().find().toList()

	override suspend fun sync(direction: DiariesDataSource.SyncDirection, vararg diary: Diary): List<Diary>
	{
		when (direction)
		{
			DiariesDataSource.SyncDirection.SAVE -> {
				withContext(dispatcher) {
					db.getRepository<Diary> {
						diary.forEach { update(it) }
					}

					db.commit()
				}
			}
		}

		return getDiaries()
	}

	override suspend fun cache(log: Log): Log
	{
		withContext(dispatcher) {
			db.getRepository<Log>(key = "draft_logs") {
				insert(log)
			}

			db.commit()
		}

		return log
	}

	override suspend fun get(logId: String): Log?
	{
		var log: Log? = null
		withContext(dispatcher) {
			db.getRepository<Log>(key = "draft_logs") {
				log = find(Log::id eq logId).firstOrNull()
			}
		}

		return log
	}
}
