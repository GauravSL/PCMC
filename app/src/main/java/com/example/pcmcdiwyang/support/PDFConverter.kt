package com.example.pcmcdiwyang.support

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.example.pcmcdiwyang.R
import com.example.pcmcdiwyang.data.model.ApplicantData
import java.io.File
import java.io.FileOutputStream
import java.util.*

class PDFConverter {

    private fun createBitmapFromView(
        context: Context,
        view: View,
        activity: Activity
    ): Bitmap {
        return createBitmap(context, view, activity)
    }

    private fun createBitmap(
        context: Context,
        view: View,
        activity: Activity,
    ): Bitmap {
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display?.getRealMetrics(displayMetrics)
            displayMetrics.densityDpi
        } else {
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        }
        view.measure(
            View.MeasureSpec.makeMeasureSpec(
                displayMetrics.widthPixels, View.MeasureSpec.EXACTLY
            ),
            View.MeasureSpec.makeMeasureSpec(
                displayMetrics.heightPixels, View.MeasureSpec.EXACTLY
            )
        )
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight, Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        view.draw(canvas)
        //return Bitmap.createScaledBitmap(bitmap, 595, 842, true)
        return Bitmap.createScaledBitmap(bitmap, view.measuredWidth, view.measuredHeight, true)
    }

    private fun convertBitmapToPdf(bitmap: Bitmap, context: Context) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        page.canvas.drawBitmap(bitmap, 0F, 0F, null)
        pdfDocument.finishPage(page)
        val filePath = File(context.getExternalFilesDir(null), "bitmapPdf.pdf")
        pdfDocument.writeTo(FileOutputStream(filePath))
        pdfDocument.close()
        renderPdf(context, filePath)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun createPdf(
        context: Context,
        activity: Activity,
        applicantData: ApplicantData?
    ) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.pdf_layout, null)
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val tvLine1 = view.findViewById<TextView>(R.id.tvLine1)
        val tvLine2 = view.findViewById<TextView>(R.id.tvLine2)
        val tvLine3 = view.findViewById<TextView>(R.id.tvLine3)
        val tvLine4 = view.findViewById<TextView>(R.id.tvLine4)
        val tvLine5 = view.findViewById<TextView>(R.id.tvLine5)
        val tvLine6 = view.findViewById<TextView>(R.id.tvLine6)
        val tvLine7 = view.findViewById<TextView>(R.id.tvLine7)
        val tvLine8 = view.findViewById<TextView>(R.id.tvLine8)
        val tvLine9 = view.findViewById<TextView>(R.id.tvLine9)
        val tvLine10 = view.findViewById<TextView>(R.id.tvLine10)

        tvLine1.text = String.format(context.getString(R.string.txt_pdf_line_1), applicantData?.city, applicantData?.zipcode)
        val name ="${applicantData?.firstName} ${applicantData?.middleName} ${applicantData?.lastName}"
        tvLine2.text = String.format(context.getString(R.string.txt_pdf_line_2), name, applicantData?.DOB)
        tvLine3.text = String.format(context.getString(R.string.txt_pdf_line_3), applicantData?.address)
        tvLine4.text = String.format(context.getString(R.string.txt_pdf_line_4), applicantData?.addhar, applicantData?.mobile)
        tvLine5.text = String.format(context.getString(R.string.txt_pdf_line_5), applicantData?.handicapType)
        tvLine6.text = String.format(context.getString(R.string.txt_pdf_line_6), applicantData?.pName)
        tvLine7.text = String.format(context.getString(R.string.txt_pdf_line_7), applicantData?.pAddress)
        tvLine8.text = String.format(context.getString(R.string.txt_pdf_line_8), applicantData?.pAddhar, applicantData?.pMobile)
        tvLine9.text = String.format(context.getString(R.string.txt_pdf_line_9), "Pune")
        tvLine10.text = String.format(context.getString(R.string.txt_pdf_line_10), "$day-${month+1}-$year", name)

       // tvLine6.text = Html.fromHtml(String.format(context.getString(R.string.txt_pdf_line_6), applicantData?.address), Html.FROM_HTML_MODE_COMPACT)


        val bitmap = createBitmapFromView(context, view, activity)
        convertBitmapToPdf(bitmap, activity)
    }


    private fun renderPdf(context: Context, filePath: File) {
        val uri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            filePath
        )
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri, "application/pdf")

        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {

        }
    }
}