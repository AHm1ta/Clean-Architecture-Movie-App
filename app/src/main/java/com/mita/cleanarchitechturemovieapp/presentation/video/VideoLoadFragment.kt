package com.mita.cleanarchitechturemovieapp.presentation.video

import android.app.Activity
import android.content.Intent
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.arthenica.ffmpegkit.FFmpegKit
import com.mita.cleanarchitechturemovieapp.R
import com.mita.cleanarchitechturemovieapp.databinding.FragmentVideoBinding
import com.mita.cleanarchitechturemovieapp.databinding.FragmentVideoLoadBinding
import java.io.File
import java.nio.ByteBuffer


class VideoLoadFragment : Fragment() {
    private val videoCaptureRequestCode = 2001
    private val videoPickRequestCode = 2002
    private val audioPickRequestCode = 3001
    private var videoUri: Uri? = null
    private var audioUri: Uri? = null
    private var outputUri: Uri? = null
    private lateinit var binding: FragmentVideoLoadBinding
    var muxerStarted = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_video_load, container, false)
        binding = FragmentVideoLoadBinding.inflate(inflater, container, false)

        binding.recordButton.setOnClickListener { recordVideo() }
        binding.pickButton.setOnClickListener { pickVideoFromGallery() }

        binding.pickAudioButton.setOnClickListener { pickAudioFromStorage() }

        binding.mergeButton.setOnClickListener {
            // Get the output URI where the merged video will be saved
            outputUri = getOutputUri()

            if (outputUri != null) {
                // Proceed to merge video and audio
                mergeAudioAndVideo()
            } else {
                Toast.makeText(context, "Error: Output URI is null", Toast.LENGTH_SHORT).show()
            }
            //mergeAudioAndVideo()
        }


        return binding.root
    }

    private fun recordVideo() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60)
        startActivityForResult(intent, videoCaptureRequestCode)
    }

    private fun pickVideoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        intent.type = "video/*"
        startActivityForResult(intent, videoPickRequestCode)
    }

    private fun pickAudioFromStorage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        intent.type = "audio/*"
        startActivityForResult(intent, audioPickRequestCode)
    }



    private fun playMergedVideo(path: Uri?) {

        val bundle = Bundle()
        bundle.putString("videoPath", path.toString())
      //  findNavController().navigate(R.id.action_videoLoadFragment_to_videoPlayerFragment, bundle)

    }

    private fun getPath(uri: Uri): String? {
        val contentResolver = requireContext().contentResolver
        if (uri.scheme == "content") {
            // Use content resolver to open the input stream
            val fileName = getFileName(uri)
            val file = File(requireContext().cacheDir, fileName ?: "temp_file")
            contentResolver.openInputStream(uri)?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            return file.absolutePath
        } else if (uri.scheme == "file") {
            return uri.path
        }
        return null
    }

    private fun getFileName(uri: Uri): String? {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
              //  val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val displayNameIndex = it.getColumnIndex("_data")
                return if (displayNameIndex >= 0) it.getString(displayNameIndex) else null
            }
        }
        return null
    }

    private fun getOutputFilePath(): String {
        val outputDir =
            File(requireContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES), "MergedVideos")
        if (!outputDir.exists()) outputDir.mkdirs()
        return File(outputDir, "output_${System.currentTimeMillis()}.mp4").absolutePath
    }

    // After selecting video and audio URIs, we will merge them
    private fun mergeAudioAndVideo() {
        try {
            val videoExtractor = MediaExtractor()
            val audioExtractor = MediaExtractor()

            // Set data sources for video and audio files
            videoUri?.let { videoExtractor.setDataSource(requireContext(), it, null) }
            audioUri?.let { audioExtractor.setDataSource(requireContext(), it, null) }

            // Get the track indices for video and audio
            val videoTrackIndex = getTrackIndex(videoExtractor, "video")
            //  val audioTrackIndex = getTrackIndex(audioExtractor, "audio")

            //audio
            var audioTrackIndex = -1
            val trackCount = audioExtractor.trackCount
            for (i in 0 until trackCount) {
                val format = audioExtractor.getTrackFormat(i)
                val mimeType = format.getString(MediaFormat.KEY_MIME)
                if (mimeType != null && mimeType.startsWith("audio/mpeg")) {
                    audioTrackIndex = i
                    break
                }
            }

            if (audioTrackIndex == -1) {
                throw IllegalArgumentException("No audio track found in the input file.")
            }
            // Get the formats of the video and audio tracks
            val videoFormat = videoExtractor.getTrackFormat(videoTrackIndex)
            val audioFormat = audioExtractor.getTrackFormat(audioTrackIndex)

            // Create a MediaMuxer to combine the video and audio
            val mediaMuxer =
                outputUri?.path?.let { MediaMuxer(it, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4) }

            val outputAudioFormat = MediaFormat.createAudioFormat(
                "audio/mp4a-latm",
                audioFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE),
                audioFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
            )
            outputAudioFormat.setInteger(MediaFormat.KEY_BIT_RATE, 128000) // Set bit rate

            // Add video and audio tracks to the muxer
            val videoTrack = mediaMuxer?.addTrack(videoFormat)
            val audioTrack = mediaMuxer?.addTrack(outputAudioFormat)

            // Start muxer to write data
            mediaMuxer?.start()
            muxerStarted=true

            val buffer = ByteBuffer.allocate(1024 * 1024)  // Buffer to read and write data
            val bufferInfo =
                MediaCodec.BufferInfo()  // BufferInfo to store size, timestamps, and flags

            // Extract and write video samples to muxer
            videoExtractor.selectTrack(videoTrackIndex)
            while (true) {
                val sampleSize = videoExtractor.readSampleData(buffer, 0)
                if (sampleSize < 0) break
                bufferInfo.set(0, sampleSize.toLong().toInt(), videoExtractor.sampleTime, 0)
                if (videoTrack != null) {
                    mediaMuxer.writeSampleData(videoTrack, buffer, bufferInfo)
                }
                videoExtractor.advance()
            }

            // Extract and write audio samples to muxer
            audioExtractor.selectTrack(audioTrackIndex)
            while (true) {
                val sampleSize = audioExtractor.readSampleData(buffer, 0)
                if (sampleSize < 0) break
                bufferInfo.set(0, sampleSize.toLong().toInt(), audioExtractor.sampleTime, 0)
                if (audioTrack != null) {
                    mediaMuxer.writeSampleData(audioTrack, buffer, bufferInfo)
                }
                audioExtractor.advance()
            }

            // Stop and release resources
            try {
                if (muxerStarted) {
                    mediaMuxer?.stop()
                    mediaMuxer?.release()
                    videoExtractor.release()
                    audioExtractor.release()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Show success message
            Toast.makeText(context, "Merging Complete! $outputUri", Toast.LENGTH_SHORT).show()

            // Optionally, you can use ExoPlayer to play the merged file
            playMergedVideo(outputUri)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error merging audio and video", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getTrackIndex(extractor: MediaExtractor, type: String): Int {
        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            val mimeType = format.getString(MediaFormat.KEY_MIME)
            if (mimeType?.startsWith(type) == true) {
                return i
            }
        }
        throw IllegalArgumentException("No $type track found")
    }

    // Define the function to generate the output URI for merged file
    private fun getOutputUri(): Uri {
        // Define the location where the merged file will be saved
        val outputDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        val outputFile = File(outputDir, "merged_video.mp4")
        return Uri.fromFile(outputFile)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                videoCaptureRequestCode, videoPickRequestCode -> {
                    videoUri = data?.data
                    if (videoUri != null) {
                        Toast.makeText(requireContext(), "Video save $videoUri", Toast.LENGTH_SHORT)
                            .show()
                        binding.pickAudioButton.visibility = View.VISIBLE
                        //goToAudioFragment()
                    } else {
                        binding.pickAudioButton.visibility = View.GONE
                    }
                }

                audioPickRequestCode -> {
                    audioUri = data?.data
                    if (audioUri != null) {
                        Toast.makeText(requireContext(), "Audio save $audioUri", Toast.LENGTH_SHORT)
                            .show()
                        binding.mergeButton.visibility = View.VISIBLE
                    } else {
                        binding.mergeButton.visibility = View.GONE
                    }
                }
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VideoLoadFragment().apply {

            }
    }
}