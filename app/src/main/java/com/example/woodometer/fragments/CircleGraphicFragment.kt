package com.example.woodometer.fragments

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.woodometer.R
import com.example.woodometer.databinding.FragmentCircleGraphicBinding
import com.example.woodometer.utils.CircleDotsView
import com.example.woodometer.utils.GlobalUtils.rotateMode
import com.example.woodometer.viewmodels.KrugViewModel
class CircleGraphicFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentCircleGraphicBinding? = null
    private val binding get() = _binding!!

    private var rotate = false

    private lateinit var krugVM : KrugViewModel
    private lateinit var sensorManager: SensorManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCircleGraphicBinding.inflate(inflater, container, false)
        krugVM = ViewModelProvider(requireActivity())[KrugViewModel::class.java]
        krugVM.stablaKruga.observe(viewLifecycleOwner) { stabla ->
            if (stabla != null) {
                binding.circleDotsView.dots = stabla
            }
        }

        binding.rotateButton.setOnClickListener {
            rotate = !rotate
            if (!rotate) {
                binding.rotateButton.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.baseline_lock_open_24))
                binding.circleLayout.rotation = 0f
                binding.azimuthTextView.visibility = View.GONE
            }else{
                binding.rotateButton.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.baseline_lock_24))
                binding.azimuthTextView.visibility = View.VISIBLE
            }
        }
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        setupHideLegendButton()

        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager



        return binding.root
    }

    private fun setupHideLegendButton() {
        val hideButton = binding.colorsLegend.findViewById<ImageButton>(R.id.hideLegendButton)
        hideButton.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.baseline_keyboard_arrow_down_24))
        hideButton.setOnClickListener { binding.colorsLegend.visibility = View.GONE }
    }


    override fun onResume() {
        super.onResume()
        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        rotationSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (!rotate) return

        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

            val orientationAngles = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientationAngles)

            val azimuthRadians = orientationAngles[0]
            var azimuthDegrees = Math.toDegrees(azimuthRadians.toDouble()).toFloat()

            // Normalize to [0, 360)
            azimuthDegrees = (azimuthDegrees + 360) % 360

            // Apply rotation (negate to rotate view clockwise)
            binding.circleLayout.rotation = -azimuthDegrees

            // Show normal azimuth (no inversion)
            binding.azimuthTextView.text = azimuthDegrees.toInt().toString()
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (rotateMode){
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }else{
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }
}
