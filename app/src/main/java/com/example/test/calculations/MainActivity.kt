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
import com.example.test.databinding.ActivityMainBinding
import com.example.test.databinding.DialogExitBinding
import com.example.test.databinding.DialogResultBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    // Binding
    private lateinit var binding: ActivityMainBinding
    private var isPlayed = false
    private var firstRandomNumber: Int? = null
    private var secondRandomNumber: Int? = null
    private var timerJob: Job? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            etAnswer.addTextChangedListener {
                // Limit input to 4 digits
                if (it != null && it.length > 4) {
                    it.delete(4, it.length)
                }

                val answer = firstRandomNumber!! + secondRandomNumber!!
                if (!it.isNullOrEmpty() && it.toString().toIntOrNull() == answer) {
                    // Correct answer
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
            btnEnter.setOnClickListener {
                try {
                    val answer = firstRandomNumber!! + secondRandomNumber!!
                    val userAnswer = etAnswer.text.toString().toIntOrNull()
                    if (userAnswer != null && userAnswer == answer) {
                        // Correct answer logic
                        tvScore.text = (tvScore.text.toString().toInt() + 1).toString()
                        etAnswer.setText("")
                        getRandomNumbers()
                    } else {
                        // Incorrect answer logic
                        Toast.makeText(this@MainActivity, "Incorrect answer, try again!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
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
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getRandomNumbers() {
        firstRandomNumber = Random.nextInt(2, 99)
        secondRandomNumber = Random.nextInt(2, 99)
        binding.tvQuestionNumber.text = "$firstRandomNumber + $secondRandomNumber"
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
                val intent = Intent(this@MainActivity, MenuActivity::class.java)
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

