package com.mita.cleanarchitechturemovieapp.presentation.video

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mita.cleanarchitechturemovieapp.R
import com.mita.cleanarchitechturemovieapp.databinding.FragmentVideoLoadBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer

class VideoAudioLoadFragment : Fragment() {
    private val videoCaptureRequestCode = 2001
    private val videoPickRequestCode = 2002
    private val audioPickRequestCode = 3001
    private var videoUri: Uri? = null
    private var audioUri: Uri? = null
    private lateinit var outputMergedFile: File
    private lateinit var binding: FragmentVideoLoadBinding
    private var muxerStarted = false


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

        outputMergedFile = File(requireContext().getExternalFilesDir(null), "merged_output.mp4")

        binding.recordButton.setOnClickListener { recordVideo() }
        binding.pickButton.setOnClickListener { pickVideoFromGallery() }

        binding.pickAudioButton.setOnClickListener { pickAudioFromStorage() }

        binding.mergeButton.setOnClickListener {
            mergeVideoAndAudio()
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

    private fun playMergedVideo(outputFile: File) {

        val bundle = Bundle()
        bundle.putString("outputFile", outputFile.absolutePath)
        findNavController().navigate(R.id.action_videoAudioLoadFragment_to_videoPlayerFragment, bundle)

    }

    private fun mergeVideoAndAudio() {
        if (videoUri == null || audioUri == null) {
            Toast.makeText(requireContext(), "Please record a video and select audio first", Toast.LENGTH_SHORT).show()
            return
        }

        val videoFile = File(getPath(requireContext(),videoUri!!)!!)
        val audioFile = getPath(requireContext(),audioUri!!)?.let { File(it) }
        val aacFile = File(requireContext().getExternalFilesDir(null), "converted_audio.aac")

       // convertMp3ToAac(audioFile!!, aacFile)
        MediaMuxerHelper.merge(videoFile, audioFile!!, outputMergedFile)

        playMergedVideo(outputMergedFile)

        Toast.makeText(requireContext(), "Files merged successfully: ${outputMergedFile.absolutePath}", Toast.LENGTH_SHORT).show()
    }

    private fun convertMp3ToAac(mp3File: File, aacFile: File) {
        try {
            val inputStream = FileInputStream(mp3File)
            val outputStream = FileOutputStream(aacFile)

            // Set up MP3 decoder
            val mp3Decoder = MediaCodec.createDecoderByType("audio/mpeg")
            val mp3Format = MediaFormat.createAudioFormat("audio/mpeg", 44100, 2)
            mp3Decoder.configure(mp3Format, null, null, 0)
            mp3Decoder.start()

            // Set up AAC encoder
            val aacEncoder = MediaCodec.createEncoderByType("audio/mp4a-latm")
            val aacFormat = MediaFormat.createAudioFormat("audio/mp4a-latm", 44100, 2)
            aacFormat.setInteger(MediaFormat.KEY_BIT_RATE, 128000)
            aacFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)
            aacEncoder.configure(aacFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            aacEncoder.start()

            val bufferInfo = MediaCodec.BufferInfo()
            var endOfStream = false

            while (!endOfStream) {
                // Feed MP3 data into decoder
                val inputIndex = mp3Decoder.dequeueInputBuffer(10000)
                if (inputIndex >= 0) {
                    val inputBuffer = mp3Decoder.getInputBuffer(inputIndex)!!
                    inputBuffer.clear()

                    val byteArray = ByteArray(inputBuffer.remaining())
                    val bytesRead = inputStream.read(byteArray)
                    if (bytesRead == -1) {
                        // End of stream
                        mp3Decoder.queueInputBuffer(inputIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                        endOfStream = true
                    } else {
                        inputBuffer.put(byteArray, 0, bytesRead)
                        mp3Decoder.queueInputBuffer(inputIndex, 0, bytesRead, 0, 0)
                    }
                }

                // Process decoder output and feed into encoder
                var decoderOutputIndex = mp3Decoder.dequeueOutputBuffer(bufferInfo, 10000)
                if (decoderOutputIndex >= 0) {
                    val decodedBuffer = mp3Decoder.getOutputBuffer(decoderOutputIndex)!!
                    val encoderInputIndex = aacEncoder.dequeueInputBuffer(10000)
                    if (encoderInputIndex >= 0) {
                        val encoderInputBuffer = aacEncoder.getInputBuffer(encoderInputIndex)!!
                        encoderInputBuffer.clear()

                        // Transfer data from decoder to encoder
                        while (decodedBuffer.hasRemaining() && encoderInputBuffer.hasRemaining()) {
                            encoderInputBuffer.put(decodedBuffer)
                        }
                        aacEncoder.queueInputBuffer(
                            encoderInputIndex,
                            0,
                            encoderInputBuffer.position(),
                            bufferInfo.presentationTimeUs,
                            bufferInfo.flags
                        )
                    }
                    mp3Decoder.releaseOutputBuffer(decoderOutputIndex, false)
                    decoderOutputIndex = mp3Decoder.dequeueOutputBuffer(bufferInfo, 10000)
                }else if (decoderOutputIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    // Format changed
                    val newFormat = mp3Decoder.outputFormat
                    Toast.makeText(requireContext(), "Decoder output format changed: $newFormat",Toast.LENGTH_SHORT).show()
                } else if (decoderOutputIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    // No output available, try again
                    Toast.makeText(requireContext(), "No decoder output available yet",Toast.LENGTH_SHORT).show()
                    println("No decoder output available yet")
                }

                // Retrieve AAC-encoded output
                var encoderOutputIndex = aacEncoder.dequeueOutputBuffer(bufferInfo, 10000)
                while (encoderOutputIndex >= 0) {
                    val encodedData = aacEncoder.getOutputBuffer(encoderOutputIndex)!!
                    val outputBytes = ByteArray(bufferInfo.size)
                    encodedData.get(outputBytes)
                    outputStream.write(outputBytes)
                    aacEncoder.releaseOutputBuffer(encoderOutputIndex, false)
                    encoderOutputIndex = aacEncoder.dequeueOutputBuffer(bufferInfo, 10000)
                }
            }

            // Cleanup
            inputStream.close()
            outputStream.close()
            mp3Decoder.stop()
            mp3Decoder.release()
            aacEncoder.stop()
            aacEncoder.release()

            println("MP3 to AAC conversion completed: ${aacFile.absolutePath}")
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error converting MP3 to AAC: ${e.message}",Toast.LENGTH_SHORT).show()
        }
    }

    /*private fun convertMp3ToAac(mp3File: File, aacFile: File) {
        val mp3InputStream = FileInputStream(mp3File)
        val aacOutputStream = FileOutputStream(aacFile)

        // Prepare the MP3 decoder
        val mp3Decoder = MediaCodec.createDecoderByType("audio/mpeg")
        val mp3Format = MediaFormat.createAudioFormat("audio/mpeg", 44100, 2) // MP3 has 44.1kHz sample rate and stereo (2 channels)
        mp3Decoder.configure(mp3Format, null, null, 0)
        mp3Decoder.start()

        // Prepare the AAC encoder
        val aacEncoder = MediaCodec.createEncoderByType("audio/mp4a-latm")
        val aacFormat = MediaFormat.createAudioFormat("audio/mp4a-latm", 44100, 2) // AAC also typically has 44.1kHz sample rate and stereo channels
        aacFormat.setInteger(MediaFormat.KEY_BIT_RATE, 128000) // 128 kbps bitrate for the AAC encoding
        aacFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)
        aacEncoder.configure(aacFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        aacEncoder.start()

        val inputBuffer = ByteBuffer.allocate(1024 * 1024) // Buffer for MP3 data
        val outputBuffer = ByteBuffer.allocate(1024 * 1024) // Buffer for AAC data

        val mp3InputBuffer = ByteBuffer.allocate(1024 * 1024) // Input buffer for MP3
        val aacOutputBuffer = ByteBuffer.allocate(1024 * 1024) // Output buffer for AAC

        val mp3BufferInfo = MediaCodec.BufferInfo()
        val aacBufferInfo = MediaCodec.BufferInfo()

        var endOfStream = false

        while (!endOfStream) {
            // Step 1: Feed MP3 data into MP3 decoder
            val inputIndex = mp3Decoder.dequeueInputBuffer(10000) // Timeout of 10 seconds
            if (inputIndex >= 0) {
                val inputBuffer = mp3Decoder.getInputBuffer(inputIndex)!!
                inputBuffer.clear() // Clear buffer before use
                val bytesRead = mp3InputStream.read(inputBuffer.array(), 0, inputBuffer.remaining())
                if (bytesRead == -1) {
                    mp3Decoder.queueInputBuffer(inputIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                } else {
                    mp3Decoder.queueInputBuffer(inputIndex, 0, bytesRead, 0, 0)
                }
            }

            // Step 2: Process the MP3 decoder output (to get raw PCM data)
            val outputIndex = mp3Decoder.dequeueOutputBuffer(mp3BufferInfo, 10000)
            if (outputIndex >= 0) {
                val decodedBuffer = mp3Decoder.getOutputBuffer(outputIndex)!!
                val encoderInputIndex = aacEncoder.dequeueInputBuffer(10000)
                if (encoderInputIndex >= 0) {
                    val encoderInputBuffer = aacEncoder.getInputBuffer(encoderInputIndex)!!
                    encoderInputBuffer.clear()
                    while (decodedBuffer.hasRemaining() && encoderInputBuffer.hasRemaining()) {
                        encoderInputBuffer.put(decodedBuffer)
                    }
                    aacEncoder.queueInputBuffer(encoderInputIndex, 0, encoderInputBuffer.position(), 0, 0)
                }
                mp3Decoder.releaseOutputBuffer(outputIndex, false)
            }

            // Step 3: Retrieve the AAC-encoded output from the encoder
            val aacIndex = aacEncoder.dequeueOutputBuffer(aacBufferInfo, 10000)
            if (aacIndex >= 0) {
                val encodedData = aacEncoder.getOutputBuffer(aacIndex)!!
                val outputBytes = ByteArray(aacBufferInfo.size)
                encodedData.get(outputBytes) // Copy buffer data to byte array
                aacOutputStream.write(outputBytes)
                aacEncoder.releaseOutputBuffer(aacIndex, false)
            }
        }

        // Final cleanup
        mp3Decoder.stop()
        mp3Decoder.release()

        aacEncoder.stop()
        aacEncoder.release()

        mp3InputStream.close()
        aacOutputStream.close()

        println("MP3 to AAC conversion completed: ${aacFile.absolutePath}")
    }*/

    fun getPath(context: Context, uri: Uri): String? {
        // Check if URI is from a document provider (e.g., Google Drive, etc.)
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // Handle document URIs
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":")
            val type = split[0]

            // Check if it's a file on primary storage
            if ("primary" == type) {
                return "${android.os.Environment.getExternalStorageDirectory()}/${split[1]}"
            }
        }

        // Handle content URIs (e.g., MediaStore)
        if ("content" == uri.scheme) {
            if (uri.toString().startsWith("content://")) {
                return getDataColumn(context, uri, null, null)
            }
        }

        // Handle File URIs (e.g., files directly accessed from internal storage or file provider)
        if ("file" == uri.scheme) {
            return uri.path
        }

        return null
    }

    private fun getDataColumn(context: Context, uri: Uri, selection: String?, selectionArgs: Array<String>?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = context.contentResolver.query(uri, projection, selection, selectionArgs, null)

        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }

}