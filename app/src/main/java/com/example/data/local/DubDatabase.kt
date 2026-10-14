package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.TranscriptSegment
import com.example.data.model.VideoDubProject
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "dub_history")
data class DubHistoryEntity(
    @PrimaryKey val videoId: String,
    val originalUrl: String,
    val title: String,
    val channelTitle: String,
    val thumbnailUrl: String,
    val durationSeconds: Int,
    val segmentsJson: String,
    val summaryOriginal: String,
    val summaryTeluguMix: String,
    val summaryTeluguScript: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface DubHistoryDao {
    @Query("SELECT * FROM dub_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<DubHistoryEntity>>

    @Query("SELECT * FROM dub_history WHERE videoId = :videoId LIMIT 1")
    suspend fun getProjectById(videoId: String): DubHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: DubHistoryEntity)

    @Query("DELETE FROM dub_history WHERE videoId = :videoId")
    suspend fun deleteProject(videoId: String)

    @Query("DELETE FROM dub_history")
    suspend fun clearAll()
}

@Database(entities = [DubHistoryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dubHistoryDao(): DubHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "telutube_dub.db"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

object JsonMapper {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val listType = Types.newParameterizedType(List::class.java, TranscriptSegment::class.java)
    private val segmentListAdapter = moshi.adapter<List<TranscriptSegment>>(listType)

    fun segmentsToJson(segments: List<TranscriptSegment>): String {
        return try {
            segmentListAdapter.toJson(segments)
        } catch (e: Exception) {
            "[]"
        }
    }

    fun jsonToSegments(json: String): List<TranscriptSegment> {
        return try {
            segmentListAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun entityToProject(entity: DubHistoryEntity): VideoDubProject {
        return VideoDubProject(
            videoId = entity.videoId,
            originalUrl = entity.originalUrl,
            title = entity.title,
            channelTitle = entity.channelTitle,
            thumbnailUrl = entity.thumbnailUrl,
            durationSeconds = entity.durationSeconds,
            segments = jsonToSegments(entity.segmentsJson),
            summaryOriginal = entity.summaryOriginal,
            summaryTeluguMix = entity.summaryTeluguMix,
            summaryTeluguScript = entity.summaryTeluguScript,
            dateCreated = entity.timestamp
        )
    }

    fun projectToEntity(project: VideoDubProject): DubHistoryEntity {
        return DubHistoryEntity(
            videoId = project.videoId,
            originalUrl = project.originalUrl,
            title = project.title,
            channelTitle = project.channelTitle,
            thumbnailUrl = project.thumbnailUrl,
            durationSeconds = project.durationSeconds,
            segmentsJson = segmentsToJson(project.segments),
            summaryOriginal = project.summaryOriginal,
            summaryTeluguMix = project.summaryTeluguMix,
            summaryTeluguScript = project.summaryTeluguScript,
            timestamp = project.dateCreated
        )
    }
}
