package com.example.tiktok_analog.ui.menuscreens.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.hardware.Camera
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.example.tiktok_analog.R
import com.example.tiktok_analog.databinding.BroadcastBinding
import com.example.tiktok_analog.databinding.FragmentProfileBinding
import java.io.IOException

class BroadcastFragment() : Fragment(R.layout.broadcast) {
    private var surfaceHolder: SurfaceHolder? = null
    private var camera: Camera? = null
    private var cameraId = 0
    private var rotation = 0

    private var isBroadcastStarted = false

    private var _binding: BroadcastBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BroadcastBinding.inflate(inflater, container, false)

        binding.backArrowButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        cameraId = Camera.CameraInfo.CAMERA_FACING_BACK
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        surfaceHolder = binding.surfaceView.holder
        surfaceHolder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder?) {
                openCamera(Camera.CameraInfo.CAMERA_FACING_BACK)
            }

            override fun surfaceChanged(
                holder: SurfaceHolder?,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {}
        })

        binding.flipCameraButton.setOnClickListener {
            flipCamera()
        }

        binding.flipCameraButton2.setOnClickListener {
            flipCamera()
        }

        binding.startBroadcastButton.setOnClickListener {
            startBroadcast()
        }

        binding.broadcastTitle.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) {
                binding.startBroadcastButton.isEnabled = s.isNotBlank()

                binding.startBroadcastButton.backgroundTintList =
                    requireContext().resources.getColorStateList(
                        if (s.isNotBlank()) R.color.buttonEnabledBg else R.color.buttonDisabledBg
                    )
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {}
        })

        openCamera(Camera.CameraInfo.CAMERA_FACING_BACK)
        return binding.root
    }

    private fun startBroadcast() {
        binding.preStartBroadcast.visibility = View.GONE
        binding.broadcastStarted.visibility = View.VISIBLE
        binding.flipCameraButton2.visibility = View.VISIBLE
        binding.surfaceView.visibility = View.VISIBLE
    }

    private fun flipCamera() {
        val id =
            if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Camera.CameraInfo.CAMERA_FACING_FRONT
            } else {
                Camera.CameraInfo.CAMERA_FACING_BACK
            }
        openCamera(id)
    }


    private fun openCamera(id: Int): Boolean {
        var result = false
        cameraId = id
        releaseCamera()
        try {
            camera = Camera.open(cameraId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (camera != null) {
            try {
                setUpCamera(camera!!)
                camera!!.setErrorCallback { _, _ -> }
                camera!!.setPreviewDisplay(surfaceHolder)
                camera!!.startPreview()
                result = true
            } catch (e: IOException) {
                e.printStackTrace()
                result = false
                releaseCamera()
            }
        }
        return result
    }

    private fun setUpCamera(c: Camera) {
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, info)
        rotation = requireActivity().windowManager.defaultDisplay.rotation
        var degree = 0
        when (rotation) {
            Surface.ROTATION_0 -> degree = 0
            Surface.ROTATION_90 -> degree = 90
            Surface.ROTATION_180 -> degree = 180
            Surface.ROTATION_270 -> degree = 270
            else -> {
            }
        }
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            // frontFacing
            rotation = (info.orientation + degree) % 330
            rotation = (360 - rotation) % 360
        } else {
            // Back-facing
            rotation = (info.orientation - degree + 360) % 360
        }
        c.setDisplayOrientation(rotation)
        val params: Camera.Parameters = c.parameters
        params.setRotation(rotation)
    }

    private fun releaseCamera() {
        try {
            if (camera != null) {
                camera!!.setPreviewCallback(null)
                camera!!.setErrorCallback(null)
                camera!!.stopPreview()
                camera!!.release()
                camera = null
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.e("error", e.toString())
            camera = null
        }
    }

    private fun closeFragment() {
        if (!isBroadcastStarted) {
            requireActivity().onBackPressed()
            return
        }

        val alertDialog: AlertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Вы уверены, что хотите прервать трансляцию?")
            .setMessage("Это приведет к удалению введенных вами данных")
            .setPositiveButton("Да, я уверен") { _, _ ->
                requireActivity().onBackPressed()
            }.setNegativeButton("Нет, остаться") { dialog, _ ->
                dialog.cancel()
            }.create()
        alertDialog.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}