package hys.hmonkeyys.stopsmoking.screen.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import hys.hmonkeyys.stopsmoking.databinding.DialogBodyChangeBinding

class BodyChangeDialog(

) : DialogFragment() {

    private lateinit var binding: DialogBodyChangeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return DialogBodyChangeBinding.inflate(layoutInflater).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding.cancelView.setOnClickListener {
            dismiss()
        }
        binding.checkButton.setOnClickListener {
            dismiss()
        }
    }

}