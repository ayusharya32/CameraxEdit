package com.easycodingg.cameraxedit

import jp.co.cyberagent.android.gpuimage.filter.*

object Utilities {

    const val FILE_NAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    const val SAVED_IMAGES_DIR = "Saved Images"
    const val FILE_PROVIDER_AUTHORITY = "com.easycodingg.cameraxedit.provider"

    private val vintageColorMatrix by lazy {
        arrayOf(
                1F, 0F, 0F, 0F, 0F,
                -0.4F, 1.3F, -0.4F, 0.2F, -0.1F,
                0F, 0F, 1F, 0F, 0F,
                0F, 0F, 0F, 1F, 0F
        ).toFloatArray()
    }

    private val sepiumColorMatrix by lazy {
        arrayOf(
                1.3F, -0.3F, 1.1F, 0F, 0F,
                0F, 1.3F, 0.2F, 0F, 0F,
                0F, 0F, 0.8F, 0.2F, 0F,
                0F, 0F, 0F, 1F, 0F
        ).toFloatArray()
    }

    private val coldColorMatrix by lazy {
        arrayOf(
                1F, 0F, 0F, 0F, 0F,
                0F, 1F, 0F, 0F, 0F,
                0.2F, 0.2F, 0.1F, 0.4F, 0F,
                0F, 0F, 0F, 1F, 0F
        ).toFloatArray()
    }

    val filterList by lazy {
        listOf(
                GPUImageFilter(
                        GPUImageFilter.NO_FILTER_VERTEX_SHADER,
                        GPUImageFilter.NO_FILTER_FRAGMENT_SHADER
                ),
                GPUImageSepiaToneFilter(),
                GPUImageExposureFilter(),
                GPUImageHazeFilter(),
                GPUImageGrayscaleFilter(),
                GPUImageColorMatrixFilter(0.3F, vintageColorMatrix),
                GPUImageColorMatrixFilter(0.3F, sepiumColorMatrix),
                GPUImageColorMatrixFilter(0.5F, coldColorMatrix),
                GPUImageSketchFilter(),
                GPUImagePosterizeFilter()
        )
    }

    val filterNameList by lazy {
        listOf("Original", "Sepia", "Exposure", "Haze" ,
                "Grayscale", "Vintage", "Sepium" ,"Cold", "Sketch", "Poster")
    }
}