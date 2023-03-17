package com.example.textview_detect

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Layout
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var textView : EditText
    private lateinit var addBallsButton : Button
    private lateinit var ball : ImageView
    private lateinit var slideButton : SlideButton
    var currentXPosition = 0f
    var currentYPosition = 0f
    var isHorizontallyMoved = false
    var isVerticallyMoved = false
    var line = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // replace "my_textview" with your TextView's ID
        slideButton = findViewById(R.id.unlockButton)
        textView = findViewById(R.id.myTextView)
        addBallsButton = findViewById(R.id.addBallsButton)
        ball = findViewById(R.id.ball)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        currentXPosition = Random.nextInt(0,500).toFloat()
        currentYPosition = 0f
        var nextSpace = 0f
        var horizontalAnimator : ObjectAnimator
        var moveToNextLineAnimator : ObjectAnimator
        Log.d("currentScreen","H "+height+" W "+width)

        ball.x = currentXPosition
        ball.y = currentYPosition

        textView.apply {
            val letterSpacing = 0f // adjust the value as needed
            setLetterSpacing(letterSpacing)

            val wordSpacing = 16f // adjust the value as needed
            setLineSpacing(wordSpacing, 1f)

        }
        slideButton.setSlideButtonListener(object : SlideButtonListener{
            override fun handleSlide(event: MotionEvent) {
                slideButton.isClickable = false
                currentXPosition = event.x
                currentYPosition = 0f
                ball.x = currentXPosition
                ball.y = currentYPosition
                line = 0
                Log.d("ballHeight",ball.height.toString()+" width "+ball.width.toString())

                // Find the indices of all spaces in the text.
                val spacePattern = Regex("\\s+")
                if(line<textView.layout.lineCount){
                    val spaceIndices = getSpaceIndicesOnLine(textView.text.toString(), textView.layout, line)
                    var spaceIndex = -1
//            USED FOR WORD COUNT
                    val count = textView.text.trim().split("\\s+".toRegex()).size
                    val message = "The text in this TextView has $count words"
//            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()


                    if (spaceIndices.isNotEmpty()) {
                        Log.d("currentPos","X "+ball.translationX+" Y "+ball.translationY)
                        // Randomly select a space to place the ball.
                        // Get the x and y coordinates of the selected space.
                        var layout = textView.layout
                        val y = layout.getLineBaseline(line) - ball.height
                        val verticalAnimator = ObjectAnimator.ofFloat(ball, "translationY", currentYPosition, currentYPosition+textView.y+ball.height/2)
                        verticalAnimator.duration = 1600
                        verticalAnimator.addListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator) {
                                // Animation start logic
                            }

                            override fun onAnimationEnd(animation: Animator) {
                                // Animation end logic
                                currentYPosition = ball.y
                                currentXPosition = ball.x
                                var selectedSpaceIndex = spaceIndices[Random.nextInt(spaceIndices.size)]
                                var x = layout.getPrimaryHorizontal(selectedSpaceIndex).toInt()
                                horizontalAnimator = ObjectAnimator.ofFloat(ball,"translationX", currentXPosition,x.toFloat()+ball.width/2)
                                horizontalAnimator.duration = 1700
                                horizontalAnimator.start()
                                Log.d("currentPosUpdated","x ${ball.x} y ${ball.y}")
                                horizontalAnimator.addListener(object : Animator.AnimatorListener{
                                    override fun onAnimationStart(p0: Animator) {
                                    }

                                    override fun onAnimationEnd(p0: Animator) {
                                        if(line<textView.layout.lineCount){
                                            currentXPosition = ball.x
                                            currentYPosition = ball.y
                                            var layout = textView.layout
                                            val y = layout.getLineBaseline(line)+ball.height + ball.height
                                            Log.d("lineheight","line $line "+y.toString())
                                            var moveToNextLineAnimator = ObjectAnimator.ofFloat(ball,"translationY",currentYPosition, y.toFloat()+ball.height/2)
                                            moveToNextLineAnimator.duration = 900
                                            moveToNextLineAnimator.start()
                                            Log.d("currentPosUpdatedAfterHorizontal","x $currentXPosition y $currentYPosition")
                                            moveToNextLineAnimator.addListener(object : Animator.AnimatorListener{
                                                override fun onAnimationStart(p0: Animator) {
                                                    Log.d("jtest","onStartWorking")
                                                }
                                                override fun onAnimationEnd(p0: Animator) {
                                                    if(line<textView.layout.lineCount){
                                                        currentYPosition = ball.y
                                                        currentXPosition = ball.x
                                                        val spaceIndices = getSpaceIndicesOnLine(textView.text.toString(), layout, line)
                                                        var selectedSpaceIndex = spaceIndices[Random.nextInt(spaceIndices.size)]
                                                        var x = layout.getPrimaryHorizontal(selectedSpaceIndex).toInt()+ball.width/2
                                                        horizontalAnimator.apply {
                                                            setFloatValues(currentXPosition,x.toFloat())
                                                            duration = 1600
                                                        }
                                                        horizontalAnimator.apply {
                                                            cancel()
                                                            target = ball
                                                            start()
                                                        }
                                                        line++

                                                        Log.d("currentPosUpdatedAfterNextLine"," line NUmber $line  x $currentXPosition y $currentYPosition")

                                                    }else{
                                                        moveVertically(ball)
                                                        slideButton.isClickable = true

                                                    }

                                                }

                                                override fun onAnimationCancel(p0: Animator) {
                                                    Log.d("jtest","onCancelWorking")
                                                }

                                                override fun onAnimationRepeat(p0: Animator) {
                                                    Log.d("jtest","onRepeatWorking")
                                                }
                                            })
                                        }else{
                                            moveVertically(ball)
                                        }

                                    }

                                    override fun onAnimationCancel(p0: Animator) {

                                    }

                                    override fun onAnimationRepeat(p0: Animator) {

                                    }
                                })
                            }

                            override fun onAnimationCancel(animation: Animator) {
                                // Animation cancel logic
                            }

                            override fun onAnimationRepeat(animation: Animator) {
                                // Animation repeat logic
                            }
                        })
                        verticalAnimator.start()
                        ball.visibility = View.VISIBLE
                    }
                    else {
                        Toast.makeText(applicationContext, "No spaces found in text.", Toast.LENGTH_SHORT).show()
                    }
                    line++
                }else{

                }            }

        })


        addBallsButton.setOnClickListener {
            currentXPosition = Random.nextInt(0,500).toFloat()
            currentYPosition = 0f
            ball.x = currentXPosition
            ball.y = currentYPosition
            line = 0
            Log.d("ballHeight",ball.height.toString()+" width "+ball.width.toString())

            // Find the indices of all spaces in the text.
            val spacePattern = Regex("\\s+")
            if(line<textView.layout.lineCount){
                val spaceIndices = getSpaceIndicesOnLine(textView.text.toString(), textView.layout, line)
                var spaceIndex = -1
//            USED FOR WORD COUNT
                val count = textView.text.trim().split("\\s+".toRegex()).size
                val message = "The text in this TextView has $count words"
//            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                if (spaceIndices.isNotEmpty()) {
                    Log.d("currentPos","X "+ball.translationX+" Y "+ball.translationY)
                    // Randomly select a space to place the ball.
                    // Get the x and y coordinates of the selected space.
                    var layout = textView.layout
                    val y = layout.getLineBaseline(line) - ball.height
                    val verticalAnimator = ObjectAnimator.ofFloat(ball, "translationY", currentYPosition, currentYPosition+textView.y+ball.height/2)
                    verticalAnimator.duration = 1600
                    verticalAnimator.addListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {
                            // Animation start logic
                        }

                        override fun onAnimationEnd(animation: Animator) {
                            // Animation end logic
                            currentYPosition = ball.y
                            currentXPosition = ball.x
                            var selectedSpaceIndex = spaceIndices[Random.nextInt(spaceIndices.size)]
                            var x = layout.getPrimaryHorizontal(selectedSpaceIndex).toInt()
                            horizontalAnimator = ObjectAnimator.ofFloat(ball,"translationX", currentXPosition,x.toFloat()+ball.width/2)
                            horizontalAnimator.duration = 1700
                            horizontalAnimator.start()
                            Log.d("currentPosUpdated","x ${ball.x} y ${ball.y}")
                            horizontalAnimator.addListener(object : Animator.AnimatorListener{
                                override fun onAnimationStart(p0: Animator) {
                                }

                                override fun onAnimationEnd(p0: Animator) {
                                    if(line<textView.layout.lineCount){
                                        currentXPosition = ball.x
                                        currentYPosition = ball.y
                                        var layout = textView.layout
                                        val y = layout.getLineBaseline(line)+ball.height + ball.height/2
                                        Log.d("lineheight","line $line "+y.toString())
                                        var moveToNextLineAnimator = ObjectAnimator.ofFloat(ball,"translationY",currentYPosition, y.toFloat()+ball.height*2)
                                        moveToNextLineAnimator.duration = 900
                                        moveToNextLineAnimator.start()
                                        Log.d("currentPosUpdatedAfterHorizontal","x $currentXPosition y $currentYPosition")
                                        moveToNextLineAnimator.addListener(object : Animator.AnimatorListener{
                                            override fun onAnimationStart(p0: Animator) {
                                                Log.d("jtest","onStartWorking")
                                            }
                                            override fun onAnimationEnd(p0: Animator) {
                                                if(line<textView.layout.lineCount){
                                                    currentYPosition = ball.y
                                                    currentXPosition = ball.x
                                                    val spaceIndices = getSpaceIndicesOnLine(textView.text.toString(), layout, line)
                                                    var selectedSpaceIndex = spaceIndices[Random.nextInt(spaceIndices.size)]
                                                    var x = layout.getPrimaryHorizontal(selectedSpaceIndex).toInt()+ball.width/2
                                                    horizontalAnimator.apply {
                                                        setFloatValues(currentXPosition,x.toFloat())
                                                        duration = 1600
                                                    }
                                                    horizontalAnimator.apply {
                                                        cancel()
                                                        target = ball
                                                        start()
                                                    }
                                                    line++

                                                    Log.d("currentPosUpdatedAfterNextLine"," line NUmber $line  x $currentXPosition y $currentYPosition")

                                                }else{
                                                    moveVertically(ball)
                                                }

                                            }

                                            override fun onAnimationCancel(p0: Animator) {
                                                Log.d("jtest","onCancelWorking")
                                            }

                                            override fun onAnimationRepeat(p0: Animator) {
                                                Log.d("jtest","onRepeatWorking")
                                            }
                                        })
                                    }else{
                                        moveVertically(ball)
                                    }

                                }

                                override fun onAnimationCancel(p0: Animator) {

                                }

                                override fun onAnimationRepeat(p0: Animator) {

                                }
                            })
                        }

                        override fun onAnimationCancel(animation: Animator) {
                            // Animation cancel logic
                        }

                        override fun onAnimationRepeat(animation: Animator) {
                            // Animation repeat logic
                        }
                    })
                    verticalAnimator.start()
                    ball.visibility = View.VISIBLE
                }
                else {
                    Toast.makeText(this, "No spaces found in text.", Toast.LENGTH_SHORT).show()
                }
                line++
            }else{

            }
        }
    }

    private fun moveVertically(ball: ImageView) {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        ball.animate().translationYBy(height.toFloat()).setDuration(3500)
        currentXPosition = ball.translationX
        currentYPosition = ball.translationY
        isHorizontallyMoved = false
        isVerticallyMoved = true
        line++
    }
    private fun getSpaceIndicesOnLine(text: String, layout: Layout, line: Int): List<Int> {
        val lineStart = layout.getLineStart(line)
        val lineEnd = layout.getLineEnd(line)
        val spacePattern = Regex("\\s+")
        return spacePattern.findAll(text.substring(lineStart, lineEnd)).map { it.range.first + lineStart }.toList()
    }
}






