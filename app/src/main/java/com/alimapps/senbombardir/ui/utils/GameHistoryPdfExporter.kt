package com.alimapps.senbombardir.ui.utils

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.ui.model.GameHistoryEntryUiModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val PAGE_WIDTH = 595f // A4 at 72dpi
private const val PAGE_HEIGHT = 842f
private const val MARGIN = 32f

fun shareGameHistoryAsPdf(context: Context, gameHistory: List<GameHistoryEntryUiModel>) {
    if (gameHistory.isEmpty()) return

    val file = writeGameHistoryPdf(context, gameHistory)
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.settings_item_share)))
}

private fun writeGameHistoryPdf(context: Context, gameHistory: List<GameHistoryEntryUiModel>): File {
    val document = PdfDocument()

    val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 16f
        isFakeBoldText = true
    }
    val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 13f
        isFakeBoldText = true
    }
    val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        textSize = 11f
    }
    val smallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        textSize = 9f
    }
    val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY
        strokeWidth = 1f
    }

    var pageNumber = 1
    var page = document.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH.toInt(), PAGE_HEIGHT.toInt(), pageNumber).create())
    var canvas = page.canvas
    var y = MARGIN

    fun startNewPage() {
        document.finishPage(page)
        pageNumber++
        page = document.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH.toInt(), PAGE_HEIGHT.toInt(), pageNumber).create())
        canvas = page.canvas
        y = MARGIN
    }

    fun ensureSpace(requiredHeight: Float) {
        if (y + requiredHeight > PAGE_HEIGHT - MARGIN) {
            startNewPage()
        }
    }

    canvas.drawText(context.getString(R.string.function_history), MARGIN, y, titlePaint)
    y += 28f

    gameHistory.forEach { entry ->
        ensureSpace(70f)

        canvas.drawText(context.getString(R.string.game_history_game_number, entry.gameNumber), MARGIN, y, headerPaint)
        entry.durationFormatted?.let { duration ->
            val durationText = context.getString(R.string.game_history_duration, duration)
            canvas.drawText(durationText, PAGE_WIDTH - MARGIN - smallPaint.measureText(durationText), y, smallPaint)
        }
        y += 18f

        val scoreText = "${entry.leftTeamName}   ${entry.leftTeamGoals} - ${entry.rightTeamGoals}   ${entry.rightTeamName}"
        canvas.drawText(scoreText, MARGIN, y, bodyPaint)
        y += 16f

        val resultText = if (entry.winnerTeamName.isEmpty()) {
            context.getString(R.string.game_history_draw)
        } else {
            "${entry.winnerTeamName} — ${context.getString(R.string.game_history_winner)}"
        }
        canvas.drawText(resultText, MARGIN, y, smallPaint)
        y += 18f

        entry.actionEvents.forEach { event ->
            ensureSpace(14f)
            val numberPrefix = event.playerNumber?.let { "#$it " }.orEmpty()
            val actionText = "${event.elapsedFormatted}   $numberPrefix${event.playerName} (${event.teamName}) — ${actionTypeLabel(context, event.actionType)}"
            canvas.drawText(actionText, MARGIN + 8f, y, smallPaint)
            y += 13f
        }

        y += 12f
        ensureSpace(1f)
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint)
        y += 24f
    }

    document.finishPage(page)

    val dateFormatted = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date())
    val pdfDir = File(context.cacheDir, "pdfs").apply { mkdirs() }
    val file = File(pdfDir, "game_history_${dateFormatted}_${System.currentTimeMillis()}.pdf")
    FileOutputStream(file).use { output ->
        document.writeTo(output)
    }
    document.close()

    return file
}

private fun actionTypeLabel(context: Context, actionType: String): String = when (actionType) {
    "goal" -> context.getString(R.string.text_goal)
    "assist" -> context.getString(R.string.text_assist)
    "save" -> context.getString(R.string.text_save)
    "tackle" -> context.getString(R.string.text_tackle)
    "dribble" -> context.getString(R.string.text_dribble)
    "pass" -> context.getString(R.string.text_pass)
    "shot" -> context.getString(R.string.text_shot)
    "yellowCard" -> context.getString(R.string.text_yellow_card)
    "redCard" -> context.getString(R.string.text_red_card)
    else -> actionType
}
