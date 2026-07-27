package com.example.graphicsapp

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.View

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set our custom GraphicsView as the entire content of this Activity
        setContentView(GraphicsView(this))
    }
}

/**
 * A custom view responsible for rendering basic graphical primitives.
 */
class GraphicsView(context: Context) : View(context) {

    // Initialize a Paint object with anti-aliasing for smooth edges
    private val paint = Paint().apply {
        isAntiAlias = true
        strokeWidth = 8f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Fill the background
        canvas.drawColor(Color.parseColor("#F5F5F5"))

        // --- 1. TEXT ---
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        paint.textSize = 70f
        canvas.drawText("Graphical Primitives", 50f, 120f, paint)

        // Reset text size for labels
        paint.textSize = 45f

        // --- 2. LINE ---
        paint.color = Color.RED
        canvas.drawLine(50f, 220f, 350f, 220f, paint)

        paint.color = Color.BLACK
        canvas.drawText("Line", 420f, 235f, paint)

        // --- 3. RECTANGLE ---
        paint.color = Color.BLUE
        paint.style = Paint.Style.STROKE // Only draw the outline
        canvas.drawRect(50f, 320f, 350f, 470f, paint)

        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        canvas.drawText("Rectangle", 420f, 415f, paint)

        // --- 4. CIRCLE ---
        paint.color = Color.GREEN
        paint.style = Paint.Style.FILL // Fill the shape
        canvas.drawCircle(200f, 650f, 100f, paint)

        paint.color = Color.BLACK
        canvas.drawText("Circle", 420f, 665f, paint)

        // --- 5. OVAL ---
        paint.color = Color.MAGENTA
        // An oval is defined by the bounding rectangle it fits inside
        val ovalRect = RectF(50f, 850f, 350f, 1000f)
        canvas.drawOval(ovalRect, paint)

        paint.color = Color.BLACK
        canvas.drawText("Oval", 420f, 945f, paint)

        // --- 6. ARC ---
        paint.color = Color.CYAN
        // An arc is also defined by a bounding rectangle
        val arcRect = RectF(50f, 1100f, 350f, 1300f)
        // startAngle: 0, sweepAngle: 270, useCenter: true (draws a pie wedge)
        canvas.drawArc(arcRect, 0f, 270f, true, paint)

        paint.color = Color.BLACK
        canvas.drawText("Arc", 420f, 1220f, paint)
    }
}