package alejandro.developer.zonaroja.ui.screens.comparisonresults

import alejandro.developer.domain.models.DangerZoneComparisonModel
import alejandro.developer.domain.models.RiskLevel
import alejandro.developer.domain.models.UserPreferencesModel
import alejandro.developer.domain.models.ZoneComparisonBarChartUiModel
import alejandro.developer.domain.models.ZoneComparisonChartsUiModel
import alejandro.developer.domain.models.ZoneComparisonMetricType
import alejandro.developer.domain.models.ZoneComparisonRiskChartUiModel
import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.ui.common.format.formatCompactPricePerSquareMeter
import alejandro.developer.zonaroja.ui.common.format.formatPricePerSquareMeter
import alejandro.developer.zonaroja.ui.theme.RedZoneColor
import alejandro.developer.zonaroja.ui.theme.badgeContainerColor
import alejandro.developer.zonaroja.ui.theme.badgeContentColor
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val PDF_PAGE_WIDTH = 1240
private const val PDF_PAGE_HEIGHT = 1754
private const val PDF_MARGIN = 72f
private const val PDF_CARD_RADIUS = 28f
private const val PDF_SECTION_GAP = 28f
private const val PDF_SUMMARY_CARD_HEIGHT = 380f
private const val PDF_RISK_CARD_HEIGHT = 420f
private const val PDF_METRIC_CARD_HEIGHT = 420f

suspend fun createComparisonReportPdf(
    context: Context,
    firstZone: DangerZoneComparisonModel,
    secondZone: DangerZoneComparisonModel,
    charts: ZoneComparisonChartsUiModel,
    userPreferences: UserPreferencesModel
): Uri = withContext(Dispatchers.IO) {
    val appContext = context.applicationContext
    val outputDir = File(appContext.cacheDir, "comparison-pdf").apply { mkdirs() }
    pruneOldReports(outputDir)

    val file = File(
        outputDir,
        buildReportFileName(firstZone = firstZone, secondZone = secondZone)
    )

    val document = PdfDocument()
    try {
        val painter = ComparisonPdfPainter(
            context = appContext,
            userPreferences = userPreferences
        )

        painter.addOverviewPage(
            document = document,
            firstZone = firstZone,
            secondZone = secondZone,
            riskChart = charts.riskChart
        )
        painter.addMetricChartsPage(
            document = document,
            charts = charts.metricCharts
        )

        FileOutputStream(file).use(document::writeTo)
    } finally {
        document.close()
    }

    FileProvider.getUriForFile(
        appContext,
        "${appContext.packageName}.fileprovider",
        file
    )
}

@Throws(ActivityNotFoundException::class)
fun shareComparisonReportPdf(
    context: Context,
    pdfUri: Uri
) {
    val chooserIntent = Intent.createChooser(
        Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, pdfUri)
            putExtra(
                Intent.EXTRA_SUBJECT,
                context.getString(R.string.comparison_result_share_subject)
            )
            clipData = ClipData.newUri(
                context.contentResolver,
                context.getString(R.string.comparison_result_share_button),
                pdfUri
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        },
        context.getString(R.string.comparison_result_share_chooser)
    )

    if (context !is android.app.Activity) {
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    context.startActivity(chooserIntent)
}

private fun pruneOldReports(directory: File) {
    directory.listFiles()
        ?.sortedByDescending(File::lastModified)
        ?.drop(10)
        ?.forEach(File::delete)
}

private fun buildReportFileName(
    firstZone: DangerZoneComparisonModel,
    secondZone: DangerZoneComparisonModel
): String {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val firstSlug = firstZone.zoneName.asFileSlug()
    val secondSlug = secondZone.zoneName.asFileSlug()
    return "comparativa_${firstSlug}_vs_${secondSlug}_$timestamp.pdf"
}

private fun String.asFileSlug(): String {
    return lowercase(Locale.ROOT)
        .replace(Regex("[^a-z0-9]+"), "_")
        .trim('_')
        .ifBlank { "zona" }
}

private class ComparisonPdfPainter(
    private val context: Context,
    private val userPreferences: UserPreferencesModel
) {
    private val locale = context.resources.configuration.locales[0] ?: Locale.getDefault()
    private val contentWidth = PDF_PAGE_WIDTH - (PDF_MARGIN * 2f)
    private val pageCount = 2

    private val colorRed = RedZoneColor.toArgb()
    private val colorDarkText = android.graphics.Color.parseColor("#4A3A3D")
    private val colorMutedText = android.graphics.Color.parseColor("#7B666D")
    private val colorSurface = android.graphics.Color.WHITE
    private val colorOutline = android.graphics.Color.parseColor("#D6BCC2")
    private val colorTrack = android.graphics.Color.parseColor("#EDE3E5")
    private val colorLow = android.graphics.Color.parseColor("#2E7D32")
    private val colorMedium = android.graphics.Color.parseColor("#F9A825")
    private val colorHigh = android.graphics.Color.parseColor("#E53935")
    private val colorSecondZone = android.graphics.Color.parseColor("#DADADA")

    private val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorRed
        textSize = 44f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    private val subtitlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorMutedText
        textSize = 22f
    }
    private val sectionTitlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorDarkText
        textSize = 30f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    private val cardTitlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorDarkText
        textSize = 28f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    private val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorDarkText
        textSize = 22f
    }
    private val valuePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorDarkText
        textSize = 24f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    private val metaPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorMutedText
        textSize = 18f
    }
    private val footerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorMutedText
        textSize = 16f
        textAlign = Paint.Align.RIGHT
    }
    private val centeredValuePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorDarkText
        textSize = 18f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    private val rightValuePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorDarkText
        textSize = 20f
        textAlign = Paint.Align.RIGHT
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    private val datePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorMutedText
        textSize = 18f
        textAlign = Paint.Align.LEFT
    }
    private val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorSurface
        style = Paint.Style.FILL
    }
    private val cardBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorOutline
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorTrack
        style = Paint.Style.FILL
    }
    private val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorDarkText
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }
    private val dashedGuidePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorMutedText
        strokeWidth = 2f
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(14f, 14f), 0f)
    }

    fun addOverviewPage(
        document: PdfDocument,
        firstZone: DangerZoneComparisonModel,
        secondZone: DangerZoneComparisonModel,
        riskChart: ZoneComparisonRiskChartUiModel
    ) {
        val page = document.startPage(
            PdfDocument.PageInfo.Builder(PDF_PAGE_WIDTH, PDF_PAGE_HEIGHT, 1).create()
        )
        val canvas = page.canvas

        var currentY = PDF_MARGIN
        currentY = drawReportHeader(
            canvas = canvas,
            subtitle = "${firstZone.zoneName}  vs  ${secondZone.zoneName}",
            top = currentY
        )

        currentY += 10f
        currentY += drawTextBlock(
            canvas = canvas,
            text = context.getString(R.string.comparison_pdf_overview),
            paint = subtitlePaint,
            left = PDF_MARGIN,
            top = currentY,
            width = contentWidth.toInt()
        )
        currentY += PDF_SECTION_GAP
        currentY += drawSectionTitle(
            canvas = canvas,
            title = context.getString(R.string.comparison_pdf_summary_title),
            top = currentY
        )
        currentY += 16f

        val gap = 24f
        val cardWidth = (contentWidth - gap) / 2f
        drawSummaryCard(canvas, firstZone, PDF_MARGIN, currentY, cardWidth, PDF_SUMMARY_CARD_HEIGHT)
        drawSummaryCard(
            canvas,
            secondZone,
            PDF_MARGIN + cardWidth + gap,
            currentY,
            cardWidth,
            PDF_SUMMARY_CARD_HEIGHT
        )

        currentY += PDF_SUMMARY_CARD_HEIGHT + PDF_SECTION_GAP
        val riskCardHeight = riskChartCardHeight(riskChart)
        drawRiskChartCard(
            canvas = canvas,
            chart = riskChart,
            left = PDF_MARGIN,
            top = currentY,
            width = contentWidth,
            height = riskCardHeight
        )

        drawFooter(canvas, 1)
        document.finishPage(page)
    }

    fun addMetricChartsPage(
        document: PdfDocument,
        charts: List<ZoneComparisonBarChartUiModel>
    ) {
        val page = document.startPage(
            PdfDocument.PageInfo.Builder(PDF_PAGE_WIDTH, PDF_PAGE_HEIGHT, 2).create()
        )
        val canvas = page.canvas

        var currentY = PDF_MARGIN
        currentY = drawReportHeader(
            canvas = canvas,
            subtitle = context.getString(R.string.comparison_pdf_metrics_title),
            top = currentY,
            compact = true
        )

        charts.forEachIndexed { index, chart ->
            drawMetricChartCard(
                canvas = canvas,
                chart = chart,
                left = PDF_MARGIN,
                top = currentY,
                width = contentWidth,
                height = PDF_METRIC_CARD_HEIGHT
            )
            currentY += PDF_METRIC_CARD_HEIGHT
            if (index != charts.lastIndex) currentY += 24f
        }

        drawFooter(canvas, 2)
        document.finishPage(page)
    }

    private fun drawReportHeader(
        canvas: android.graphics.Canvas,
        subtitle: String,
        top: Float,
        compact: Boolean = false
    ): Float {
        val titleHeight = drawTextBlock(
            canvas = canvas,
            text = context.getString(R.string.comparison_result_share_subject),
            paint = titlePaint,
            left = PDF_MARGIN,
            top = top,
            width = contentWidth.toInt()
        )
        val subtitleTop = top + titleHeight + if (compact) 8f else 12f
        val subtitleHeight = drawTextBlock(
            canvas = canvas,
            text = subtitle,
            paint = subtitlePaint,
            left = PDF_MARGIN,
            top = subtitleTop,
            width = contentWidth.toInt()
        )

        if (!compact) {
            val dateText = "${context.getString(R.string.comparison_pdf_generated_on)}: ${formattedNow()}"
            canvas.drawText(dateText, PDF_MARGIN, subtitleTop + subtitleHeight + 28f, datePaint)
            return subtitleTop + subtitleHeight + 40f
        }

        return subtitleTop + subtitleHeight + 10f
    }

    private fun drawSummaryCard(
        canvas: android.graphics.Canvas,
        zone: DangerZoneComparisonModel,
        left: Float,
        top: Float,
        width: Float,
        height: Float
    ) {
        drawCard(canvas, RectF(left, top, left + width, top + height))

        val padding = 28f
        val contentLeft = left + padding
        val contentWidth = width - (padding * 2f)
        var currentY = top + 28f

        currentY += drawTextBlock(
            canvas = canvas,
            text = zone.zoneName,
            paint = cardTitlePaint,
            left = contentLeft,
            top = currentY,
            width = contentWidth.toInt(),
            maxLines = 2
        )
        currentY += 10f
        canvas.drawText(zone.city, contentLeft, currentY + 18f, metaPaint)
        currentY += 36f
        currentY += drawRiskBadge(canvas, contentLeft, currentY, zone.riskLevel)
        currentY += 20f

        currentY = drawMetricRow(
            canvas,
            context.getString(R.string.comparison_metric_poverty),
            formatPercent(zone.povertyRiskRate),
            contentLeft,
            currentY,
            contentWidth
        )
        currentY = drawMetricRow(
            canvas,
            context.getString(R.string.comparison_metric_unemployment),
            formatPercent(zone.unemploymentRate),
            contentLeft,
            currentY,
            contentWidth
        )
        drawMetricRow(
            canvas,
            context.getString(R.string.comparison_metric_price),
            formatPricePerSquareMeter(zone.priceSquareMeter, userPreferences),
            contentLeft,
            currentY,
            contentWidth
        )
    }

    private fun drawRiskChartCard(
        canvas: android.graphics.Canvas,
        chart: ZoneComparisonRiskChartUiModel,
        left: Float,
        top: Float,
        width: Float,
        height: Float
    ) {
        drawCard(canvas, RectF(left, top, left + width, top + height))

        val innerLeft = left + 28f
        val innerWidth = width - 56f
        var currentY = top + 34f

        currentY += drawTextBlock(
            canvas = canvas,
            text = context.getString(R.string.comparison_metric_risk_level),
            paint = sectionTitlePaint,
            left = innerLeft,
            top = currentY,
            width = innerWidth.toInt(),
            maxLines = 1
        )
        currentY += 20f

        chart.entries.forEach { entry ->
            currentY += drawTextBlock(
                canvas = canvas,
                text = entry.label,
                paint = bodyPaint,
                left = innerLeft,
                top = currentY,
                width = (innerWidth * 0.68f).toInt(),
                maxLines = 1
            )
            canvas.drawText(entry.city, innerLeft, currentY + 40f, metaPaint)
            canvas.drawText(
                riskLabel(entry.riskLevel),
                left + width - 28f,
                currentY + 22f,
                rightValuePaint.apply { color = riskColor(entry.riskLevel) }
            )
            rightValuePaint.color = colorDarkText
            currentY += 54f

            val trackRect = RectF(innerLeft, currentY, innerLeft + innerWidth, currentY + 20f)
            canvas.drawRoundRect(trackRect, 20f, 20f, trackPaint)

            val ratio = (entry.score / chart.maxScore).coerceIn(0f, 1f)
            canvas.drawRoundRect(
                RectF(innerLeft, currentY, innerLeft + (innerWidth * ratio), currentY + 20f),
                20f,
                20f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply { color = riskColor(entry.riskLevel) }
            )
            currentY += 52f
        }

        drawRiskLegend(canvas, innerLeft, currentY + 28f)
    }

    private fun drawMetricChartCard(
        canvas: android.graphics.Canvas,
        chart: ZoneComparisonBarChartUiModel,
        left: Float,
        top: Float,
        width: Float,
        height: Float
    ) {
        drawCard(canvas, RectF(left, top, left + width, top + height))

        val innerLeft = left + 28f
        val innerWidth = width - 56f
        drawTextBlock(
            canvas = canvas,
            text = metricTitle(chart.metricType),
            paint = sectionTitlePaint,
            left = innerLeft,
            top = top + 32f,
            width = innerWidth.toInt(),
            maxLines = 1
        )

        val chartTop = top + 96f
        val chartBottom = top + height - 136f
        val axisLeft = innerLeft + 98f
        val axisRight = left + width - 36f
        val plotHeight = chartBottom - chartTop
        val middleGuideY = chartBottom - (plotHeight * 0.5f)

        canvas.drawLine(axisLeft, chartTop, axisLeft, chartBottom, axisPaint)
        canvas.drawLine(axisLeft, chartBottom, axisRight, chartBottom, axisPaint)
        canvas.drawLine(axisLeft, middleGuideY, axisRight, middleGuideY, dashedGuidePaint)
        canvas.drawText(
            middleValueLabel(chart),
            axisLeft - 16f,
            middleGuideY - 10f,
            TextPaint(metaPaint).apply { textAlign = Paint.Align.RIGHT }
        )

        val barCenters = drawBars(canvas, chart, axisLeft, axisRight, chartBottom, plotHeight)
        drawLegendRow(
            canvas = canvas,
            chart = chart,
            barCenters = barCenters,
            top = chartBottom + 34f,
            maxItemWidth = 240f
        )
    }

    private fun drawBars(
        canvas: android.graphics.Canvas,
        chart: ZoneComparisonBarChartUiModel,
        axisLeft: Float,
        axisRight: Float,
        chartBottom: Float,
        plotHeight: Float
    ): List<Float> {
        val availableWidth = axisRight - axisLeft
        val barWidth = availableWidth / 5f
        val barSpacing = barWidth * 0.7f
        val totalBarsWidth =
            (barWidth * chart.entries.size) + (barSpacing * (chart.entries.size - 1).coerceAtLeast(0))
        val startX = axisLeft + ((availableWidth - totalBarsWidth) / 2f)
        val maxValue = chart.maxValue.coerceAtLeast(1f)
        val barCenters = mutableListOf<Float>()

        chart.entries.forEachIndexed { index, entry ->
            val barLeft = startX + index * (barWidth + barSpacing)
            val barCenter = barLeft + (barWidth / 2f)
            val barHeight = plotHeight * (entry.value / maxValue).coerceIn(0f, 1f)
            val barTop = chartBottom - barHeight
            barCenters += barCenter
            canvas.drawRoundRect(
                RectF(barLeft, barTop, barLeft + barWidth, chartBottom),
                10f,
                10f,
                gradientPaint(chartColor(index))
            )
            canvas.drawText(
                entryValueLabel(chart, entry.value),
                barCenter,
                barTop - 12f,
                centeredValuePaint
            )
        }

        return barCenters
    }

    private fun drawLegendRow(
        canvas: android.graphics.Canvas,
        chart: ZoneComparisonBarChartUiModel,
        barCenters: List<Float>,
        top: Float,
        maxItemWidth: Float
    ) {
        chart.entries.forEachIndexed { index, entry ->
            val centerX = barCenters.getOrNull(index) ?: return@forEachIndexed
            val itemLeft = centerX - (maxItemWidth / 2f)
            canvas.drawCircle(
                centerX,
                top + 8f,
                7f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply { color = chartColor(index) }
            )
            drawTextBlock(
                canvas = canvas,
                text = entry.label,
                paint = bodyPaint,
                left = itemLeft,
                top = top + 18f,
                width = maxItemWidth.toInt(),
                maxLines = 1,
                alignment = Layout.Alignment.ALIGN_CENTER
            )
            drawTextBlock(
                canvas = canvas,
                text = entry.city,
                paint = metaPaint,
                left = itemLeft,
                top = top + 48f,
                width = maxItemWidth.toInt(),
                maxLines = 1,
                alignment = Layout.Alignment.ALIGN_CENTER
            )
        }
    }

    private fun drawRiskLegend(canvas: android.graphics.Canvas, left: Float, top: Float) {
        val items = listOf(
            colorLow to context.getString(R.string.low),
            colorMedium to context.getString(R.string.medium),
            colorHigh to context.getString(R.string.high)
        )
        var currentX = left
        items.forEach { (color, label) ->
            canvas.drawCircle(
                currentX + 6f,
                top + 6f,
                6f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color }
            )
            canvas.drawText(label, currentX + 20f, top + 12f, metaPaint)
            currentX += 120f
        }
    }

    private fun drawSectionTitle(
        canvas: android.graphics.Canvas,
        title: String,
        top: Float
    ): Float {
        return drawTextBlock(
            canvas = canvas,
            text = title,
            paint = sectionTitlePaint,
            left = PDF_MARGIN,
            top = top,
            width = contentWidth.toInt(),
            maxLines = 1
        )
    }

    private fun drawCard(canvas: android.graphics.Canvas, rect: RectF) {
        canvas.drawRoundRect(rect, PDF_CARD_RADIUS, PDF_CARD_RADIUS, cardPaint)
        canvas.drawRoundRect(rect, PDF_CARD_RADIUS, PDF_CARD_RADIUS, cardBorderPaint)
    }

    private fun drawMetricRow(
        canvas: android.graphics.Canvas,
        label: String,
        value: String,
        left: Float,
        top: Float,
        width: Float
    ): Float {
        val labelHeight = drawTextBlock(canvas, label, metaPaint, left, top, width.toInt(), 1)
        val valueTop = top + labelHeight + 6f
        val valueHeight = drawTextBlock(
            canvas = canvas,
            text = value,
            paint = valuePaint,
            left = left,
            top = valueTop,
            width = width.toInt(),
            maxLines = 2
        )
        return valueTop + valueHeight + 18f
    }

    private fun drawRiskBadge(
        canvas: android.graphics.Canvas,
        left: Float,
        top: Float,
        riskLevel: RiskLevel
    ): Float {
        val text = riskLabel(riskLevel)
        val badgeTextColor = riskLevel.badgeContentColor().toArgb()
        val badgeContainerColor = riskLevel.badgeContainerColor().toArgb()
        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = badgeTextColor
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val textWidth = textPaint.measureText(text)
        canvas.drawRoundRect(
            RectF(left, top, left + textWidth + 34f, top + 34f),
            24f,
            24f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = badgeContainerColor }
        )
        canvas.drawText(text, left + 17f, top + 23f, textPaint)
        return 34f
    }

    private fun drawTextBlock(
        canvas: android.graphics.Canvas,
        text: String,
        paint: TextPaint,
        left: Float,
        top: Float,
        width: Int,
        maxLines: Int = Int.MAX_VALUE,
        alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL
    ): Float {
        val layout = StaticLayout.Builder
            .obtain(text, 0, text.length, paint, width)
            .setAlignment(alignment)
            .setIncludePad(false)
            .setEllipsize(TextUtils.TruncateAt.END)
            .setMaxLines(maxLines)
            .build()

        canvas.save()
        canvas.translate(left, top)
        layout.draw(canvas)
        canvas.restore()
        return layout.height.toFloat()
    }

    private fun drawFooter(canvas: android.graphics.Canvas, pageNumber: Int) {
        canvas.drawText(
            "$pageNumber/$pageCount",
            PDF_PAGE_WIDTH - PDF_MARGIN,
            PDF_PAGE_HEIGHT - 30f,
            footerPaint
        )
    }

    private fun formattedNow(): String {
        return SimpleDateFormat("dd/MM/yyyy HH:mm", locale).format(Date())
    }

    private fun formatPercent(value: Double): String {
        return String.format(locale, "%.1f%%", value)
    }

    private fun riskLabel(riskLevel: RiskLevel): String {
        return when (riskLevel) {
            RiskLevel.LOW -> context.getString(R.string.low)
            RiskLevel.MEDIUM -> context.getString(R.string.medium)
            RiskLevel.HIGH -> context.getString(R.string.high)
        }
    }

    private fun riskColor(riskLevel: RiskLevel): Int {
        return when (riskLevel) {
            RiskLevel.LOW -> colorLow
            RiskLevel.MEDIUM -> colorMedium
            RiskLevel.HIGH -> colorHigh
        }
    }

    private fun metricTitle(metricType: ZoneComparisonMetricType): String {
        return when (metricType) {
            ZoneComparisonMetricType.POVERTY_RISK -> context.getString(R.string.comparison_metric_poverty)
            ZoneComparisonMetricType.UNEMPLOYMENT -> context.getString(R.string.comparison_metric_unemployment)
            ZoneComparisonMetricType.PRICE_SQUARE_METER -> context.getString(R.string.comparison_metric_price)
        }
    }

    private fun middleValueLabel(chart: ZoneComparisonBarChartUiModel): String {
        val middleValue = chart.maxValue / 2f
        return when (chart.metricType) {
            ZoneComparisonMetricType.POVERTY_RISK,
            ZoneComparisonMetricType.UNEMPLOYMENT -> String.format(locale, "%.0f%%", middleValue)
            ZoneComparisonMetricType.PRICE_SQUARE_METER -> formatCompactPricePerSquareMeter(
                amountInEuro = middleValue,
                preferences = userPreferences,
                compactDecimals = 0,
                unitDecimals = 0,
                symbolOverride = if (userPreferences.selectedCurrency.code == "MXN") "$" else null
            )
        }
    }

    private fun entryValueLabel(chart: ZoneComparisonBarChartUiModel, value: Float): String {
        return when (chart.metricType) {
            ZoneComparisonMetricType.POVERTY_RISK,
            ZoneComparisonMetricType.UNEMPLOYMENT -> String.format(locale, "%.1f%%", value)
            ZoneComparisonMetricType.PRICE_SQUARE_METER -> formatCompactPricePerSquareMeter(
                amountInEuro = value,
                preferences = userPreferences
            )
        }
    }

    private fun chartColor(index: Int): Int {
        return if (index == 0) colorRed else colorSecondZone
    }

    private fun riskChartCardHeight(chart: ZoneComparisonRiskChartUiModel): Float {
        return maxOf(PDF_RISK_CARD_HEIGHT, 140f + (chart.entries.size * 138f))
    }

    private fun gradientPaint(baseColor: Int): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f,
                0f,
                0f,
                PDF_METRIC_CARD_HEIGHT,
                baseColor,
                Color(baseColor).darker().toArgb(),
                Shader.TileMode.CLAMP
            )
            style = Paint.Style.FILL
        }
    }
}

private fun Color.darker(factor: Float = 0.75f): Color {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(toArgb(), hsv)
    hsv[2] *= factor
    return Color(android.graphics.Color.HSVToColor(hsv))
}
