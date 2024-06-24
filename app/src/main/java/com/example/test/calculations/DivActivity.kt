package com.example.test.calculations

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.test.MenuActivity
import com.example.test.R
import com.example.test.databinding.ActivityDivBinding
import com.example.test.databinding.DialogExitBinding
import com.example.test.databinding.DialogResultBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlin.random.Random

class DivActivity : AppCompatActivity() {
    // Binding
    private lateinit var binding: ActivityDivBinding
    private var isPlayed = false
    private var dividend: Int? = null
    private var divisor: Int? = null
    private var timerJob: Job? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDivBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup number buttons
        setupNumberButtons()

        binding.apply {
            btnStartOrNext.setOnClickListener {
                try {
                    if (isPlayed) {
                        // Next Question
                        getRandomNumbers()
                        tvScore.text = (tvScore.text.toString().toInt() - 1).toString()
                    } else {
                        // Start the Game
                        isPlayed = true
                        btnStartOrNext.text = "Next!"
                        cardQuestion.visibility = View.VISIBLE
                        cardScore.visibility = View.VISIBLE
                        btns.visibility = View.VISIBLE // Make number buttons visible
                        getRandomNumbers()
                        runTimer()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@DivActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            etAnswer.addTextChangedListener {
                // Limit input to 4 digits
                if (it != null && it.length > 4) {
                    it.delete(4, it.length)
                }

                val answer = dividend!! / divisor!!
                val userAnswer = it.toString().toIntOrNull()

                if (userAnswer != null && userAnswer == answer) {
                    // Correct answer logic
                    tvScore.text = (tvScore.text.toString().toInt() + 1).toString()
                    etAnswer.setText("")
                    getRandomNumbers()
                }
            }
        }
    }

    private fun setupNumberButtons() {
        binding.apply {
            btn0.setOnClickListener {
                etAnswer.append("0")
            }
            btn1.setOnClickListener {
                etAnswer.append("1")
            }
            btn2.setOnClickListener {
                etAnswer.append("2")
            }
            btn3.setOnClickListener {
                etAnswer.append("3")
            }
            btn4.setOnClickListener {
                etAnswer.append("4")
            }
            btn5.setOnClickListener {
                etAnswer.append("5")
            }
            btn6.setOnClickListener {
                etAnswer.append("6")
            }
            btn7.setOnClickListener {
                etAnswer.append("7")
            }
            btn8.setOnClickListener {
                etAnswer.append("8")
            }
            btn9.setOnClickListener {
                etAnswer.append("9")
            }
            btnDel.setOnClickListener {
                val currentText = etAnswer.text.toString()
                if (currentText.isNotEmpty()) {
                    etAnswer.setText(currentText.substring(0, currentText.length - 1))
                }
            }
            // Remove the click listener for btnEnter since it's not needed anymore
            btnEnter.setOnClickListener {
                // Do nothing
            }
        }
    }

    private fun runTimer() {
        timerJob = lifecycleScope.launch(Dispatchers.IO) {
            try {
                (1..29).asFlow().onStart {
                    binding.constraintLayout.transitionToEnd()
                }.onCompletion {
                    // Game finished. Show dialog to user
                    runOnUiThread {
                        binding.cardQuestion.visibility = View.GONE
                        if (!isFinishing) {
                            showResultDialog()
                        }
                    }
                }.collect { delay(1000) }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@DivActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getRandomNumbers() {
        divisor = Random.nextInt(1, 10)  // divisor will be between 1 and 9
        val result = Random.nextInt(1, 10)  // result will be between 1 and 9
        dividend = divisor!! * result  // ensures dividend is a multiple of divisor
        binding.tvQuestionNumber.text = "$dividend / $divisor"
    }

    override fun onBackPressed() {
        showExitDialog()
    }

    private fun showExitDialog() {
        if (!isFinishing) {
            val dialogBinding = DialogExitBinding.inflate(layoutInflater)
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(dialogBinding.root)
            dialog.setCancelable(false)
            dialog.show()
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialogBinding.btnBack.setOnClickListener {
                dialog.dismiss()
                finish() // Exit the activity
            }
            dialogBinding.btnContinue.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun showResultDialog() {
        if (!isFinishing) {
            val dialogBinding = DialogResultBinding.inflate(layoutInflater)
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(dialogBinding.root)
            dialog.setCancelable(false)
            dialog.show()
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // Set dialog score
            dialogBinding.tvDialogScore.text = binding.tvScore.text

            // Click listeners for dialog buttons
            dialogBinding.btnMenu.setOnClickListener {
                dialog.dismiss()
                val intent = Intent(this@DivActivity, MenuActivity::class.java)
                startActivity(intent)
                finish()
            }
            dialogBinding.btnTryAgain.setOnClickListener {
                dialog.dismiss()
                binding.apply {
                    btnStartOrNext.text = getString(R.string.start_game)
                    cardQuestion.visibility = View.GONE
                    cardScore.visibility = View.GONE
                    btns.visibility = View.GONE
                    isPlayed = false
                    constraintLayout.setTransition(R.id.start, R.id.end)
                    constraintLayout.transitionToEnd()
                    tvScore.text = "0"
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel() // Cancel any running coroutines
    }
}
