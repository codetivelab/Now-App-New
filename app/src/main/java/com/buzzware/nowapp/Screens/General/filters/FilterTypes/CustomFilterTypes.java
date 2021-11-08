package com.buzzware.nowapp.Screens.General.filters.FilterTypes;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.util.Log;
//
//import com.buzzware.nowapp.R;
//import com.buzzware.nowapp.Screens.UserScreens.FilterAdjuster;
//import com.buzzware.nowapp.Screens.UserScreens.FilterType;
//import com.buzzware.nowapp.Screens.UserScreens.filter.GlBitmapOverlaySample;
//import com.daasuu.gpuv.egl.filter.GlBilateralFilter;
//import com.daasuu.gpuv.egl.filter.GlBoxBlurFilter;
//import com.daasuu.gpuv.egl.filter.GlBrightnessFilter;
//import com.daasuu.gpuv.egl.filter.GlBulgeDistortionFilter;
//import com.daasuu.gpuv.egl.filter.GlCGAColorspaceFilter;
//import com.daasuu.gpuv.egl.filter.GlContrastFilter;
//import com.daasuu.gpuv.egl.filter.GlCrosshatchFilter;
//import com.daasuu.gpuv.egl.filter.GlExposureFilter;
//import com.daasuu.gpuv.egl.filter.GlFilter;
//import com.daasuu.gpuv.egl.filter.GlFilterGroup;
//import com.daasuu.gpuv.egl.filter.GlGammaFilter;
//import com.daasuu.gpuv.egl.filter.GlGaussianBlurFilter;
//import com.daasuu.gpuv.egl.filter.GlGrayScaleFilter;
//import com.daasuu.gpuv.egl.filter.GlHalftoneFilter;
//import com.daasuu.gpuv.egl.filter.GlHazeFilter;
//import com.daasuu.gpuv.egl.filter.GlHighlightShadowFilter;
//import com.daasuu.gpuv.egl.filter.GlHueFilter;
//import com.daasuu.gpuv.egl.filter.GlInvertFilter;
//import com.daasuu.gpuv.egl.filter.GlLookUpTableFilter;
//import com.daasuu.gpuv.egl.filter.GlLuminanceFilter;
//import com.daasuu.gpuv.egl.filter.GlLuminanceThresholdFilter;
//import com.daasuu.gpuv.egl.filter.GlMonochromeFilter;
//import com.daasuu.gpuv.egl.filter.GlOpacityFilter;
//import com.daasuu.gpuv.egl.filter.GlPixelationFilter;
//import com.daasuu.gpuv.egl.filter.GlPosterizeFilter;
//import com.daasuu.gpuv.egl.filter.GlRGBFilter;
//import com.daasuu.gpuv.egl.filter.GlSaturationFilter;
//import com.daasuu.gpuv.egl.filter.GlSepiaFilter;
//import com.daasuu.gpuv.egl.filter.GlSharpenFilter;
//import com.daasuu.gpuv.egl.filter.GlSolarizeFilter;
//import com.daasuu.gpuv.egl.filter.GlSphereRefractionFilter;
//import com.daasuu.gpuv.egl.filter.GlSwirlFilter;
//import com.daasuu.gpuv.egl.filter.GlToneCurveFilter;
//import com.daasuu.gpuv.egl.filter.GlToneFilter;
//import com.daasuu.gpuv.egl.filter.GlVibranceFilter;
//import com.daasuu.gpuv.egl.filter.GlVignetteFilter;
//import com.daasuu.gpuv.egl.filter.GlWatermarkFilter;
//import com.daasuu.gpuv.egl.filter.GlWeakPixelInclusionFilter;
//import com.daasuu.gpuv.egl.filter.GlWhiteBalanceFilter;
//import com.daasuu.gpuv.egl.filter.GlZoomBlurFilter;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Arrays;
//import java.util.List;

public enum CustomFilterTypes {
//
//    SATURATION,
//    CONTRAST,
//    BRIGHTNESS,
//    SEPIA,
//    SOLARIZE,
//    HAZE,
//    TONE_CURVE_SAMPLE,
//    VIBRANCE,
//    EXPOSURE,
//    LUMINANCE;
//
//
//    public static List<CustomFilterTypes> createFilterList() {
//        return Arrays.asList(CustomFilterTypes.values());
//    }
//
//    public static GlFilter createGlFilter(CustomFilterTypes filterType, Context context) {
//        switch (filterType) {
//            case BRIGHTNESS:
//                GlBrightnessFilter glBrightnessFilter = new GlBrightnessFilter();
//                glBrightnessFilter.setBrightness(0.2f);
//                return glBrightnessFilter;
//            case CONTRAST:
//                GlContrastFilter glContrastFilter = new GlContrastFilter();
//                glContrastFilter.setContrast(2.5f);
//                return glContrastFilter;
//            case HAZE:
//                GlHazeFilter glHazeFilter = new GlHazeFilter();
//                glHazeFilter.setSlope(-0.5f);
//                return glHazeFilter;
//            case LUMINANCE:
//                return new GlLuminanceFilter();
//            case SATURATION:
//                return new GlSaturationFilter();
//            case SEPIA:
//                return new GlSepiaFilter();
//            case SOLARIZE:
//                return new GlSolarizeFilter();
//            case TONE_CURVE_SAMPLE:
//                try {
//                    InputStream is = context.getAssets().open("acv/tone_cuver_sample.acv");
//                    return new GlToneCurveFilter(is);
//                } catch (IOException e) {
//                    Log.e("FilterType", "Error");
//                }
//                return new GlFilter();
//            case VIBRANCE:
//                GlVibranceFilter glVibranceFilter = new GlVibranceFilter();
//                glVibranceFilter.setVibrance(3f);
//                return glVibranceFilter;
//            default:
//                return new GlFilter();
//        }
//    }
//
//    public static FilterAdjuster createFilterAdjuster(CustomFilterTypes filterType) {
//        switch (filterType) {
//            case BRIGHTNESS:
//                return new FilterAdjuster() {
//                    @Override
//                    public void adjust(GlFilter filter, int percentage) {
//                        ((GlBrightnessFilter) filter).setBrightness(range(percentage, -1.0f, 1.0f));
//                    }
//                };
//            case CONTRAST:
//                return new FilterAdjuster() {
//                    @Override
//                    public void adjust(GlFilter filter, int percentage) {
//                        ((GlContrastFilter) filter).setContrast(range(percentage, 0.0f, 2.0f));
//                    }
//                };
//
//            case HAZE:
//                return new FilterAdjuster() {
//                    @Override
//                    public void adjust(GlFilter filter, int percentage) {
//                        ((GlHazeFilter) filter).setDistance(range(percentage, -0.3f, 0.3f));
//                        ((GlHazeFilter) filter).setSlope(range(percentage, -0.3f, 0.3f));
//                    }
//                };
//
//            case LUMINANCE:
//                return new FilterAdjuster() {
//                    @Override
//                    public void adjust(GlFilter filter, int percentage) {
////                        if (percentage > 0 && percentage < 100)
////                            ((GlLuminanceThresholdFilter) filter).setThreshold(range(percentage, 0.0f, 1.0f));
//                    }
//                };
//            case SOLARIZE:
//                return new FilterAdjuster() {
//                    @Override
//                    public void adjust(GlFilter filter, int percentage) {
//                        ((GlSolarizeFilter) filter).setThreshold(range(percentage, 0.0f, 1.0f));
//                    }
//                };
//            case VIBRANCE:
//                return new FilterAdjuster() {
//                    @Override
//                    public void adjust(GlFilter filter, int percentage) {
//                        ((GlVibranceFilter) filter).setVibrance(range(percentage, -1.2f, 1.2f));
//                    }
//                };
//            default:
//                return null;
//        }
//    }
//
//    private static float range(int percentage, float start, float end) {
//        return (end - start) * percentage / 100.0f + start;
//    }
}
