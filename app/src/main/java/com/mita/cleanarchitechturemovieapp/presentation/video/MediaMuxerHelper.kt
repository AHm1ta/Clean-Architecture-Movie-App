package com.mita.cleanarchitechturemovieapp.presentation.video
import android.media.*
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer

object MediaMuxerHelper {

    fun merge(videoFile: File, audioFile: File, outputFile: File) {
        val videoExtractor = MediaExtractor()
        val audioExtractor = MediaExtractor()

        try {
            // Set data sources for video and audio
            videoExtractor.setDataSource(videoFile.absolutePath)
            audioExtractor.setDataSource(audioFile.absolutePath)

            // Get the video duration
            val videoDurationUs = getVideoDuration(videoExtractor)

            val muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

            // Select and add the video track
            var videoTrackIndex = -1
            for (i in 0 until videoExtractor.trackCount) {
                val format = videoExtractor.getTrackFormat(i)
                if (format.getString(MediaFormat.KEY_MIME)!!.startsWith("video")) {
                    videoTrackIndex = muxer.addTrack(format)
                    videoExtractor.selectTrack(i)
                    break
                }
            }

            // Select and add the audio track
            var audioTrackIndex = -1
            for (i in 0 until audioExtractor.trackCount) {
                val format = audioExtractor.getTrackFormat(i)
                if (format.getString(MediaFormat.KEY_MIME)!!.startsWith("audio")) {
                    audioTrackIndex = muxer.addTrack(format)
                    audioExtractor.selectTrack(i)
                    break
                }
            }

            if (videoTrackIndex == -1 || audioTrackIndex == -1) {
                throw IllegalArgumentException("Failed to find valid video or audio track.")
            }

            muxer.start()

            val buffer = ByteBuffer.allocate(1024 * 1024) // 1MB buffer
            val bufferInfo = MediaCodec.BufferInfo()

            // Process the video and audio
            var videoSampleTimeUs: Long
            var audioSampleTimeUs: Long = 0

            // Process video
            while (true) {
                val videoSampleSize = videoExtractor.readSampleData(buffer, 0)
                if (videoSampleSize < 0) break

                videoSampleTimeUs = videoExtractor.sampleTime
                if (videoSampleTimeUs > videoDurationUs) break // Stop when video ends

                bufferInfo.apply {
                    offset = 0
                    size = videoSampleSize
                    presentationTimeUs = videoSampleTimeUs
                    flags = videoExtractor.sampleFlags
                }

                muxer.writeSampleData(videoTrackIndex, buffer, bufferInfo)
                videoExtractor.advance()
            }

            // Process audio
            while (true) {
                val audioSampleSize = audioExtractor.readSampleData(buffer, 0)
                if (audioSampleSize < 0) break

                audioSampleTimeUs = audioExtractor.sampleTime
                if (audioSampleTimeUs > videoDurationUs) break // Stop when audio exceeds video length

                bufferInfo.apply {
                    offset = 0
                    size = audioSampleSize
                    presentationTimeUs = audioSampleTimeUs
                    flags = audioExtractor.sampleFlags
                }

                muxer.writeSampleData(audioTrackIndex, buffer, bufferInfo)
                audioExtractor.advance()
            }

            muxer.stop()
            muxer.release()
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle exception
        } finally {
            videoExtractor.release()
            audioExtractor.release()
        }
    }

    // Function to get video duration in microseconds
    private fun getVideoDuration(extractor: MediaExtractor): Long {
        var durationUs: Long = 0
        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            if (format.getString(MediaFormat.KEY_MIME)!!.startsWith("video")) {
                extractor.selectTrack(i)
                durationUs = format.getLong(MediaFormat.KEY_DURATION)
                break
            }
        }
        return durationUs
    }
}
