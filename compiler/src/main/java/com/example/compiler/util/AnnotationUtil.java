package com.example.compiler.util;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;


public class AnnotationUtil {
    public static String getPackageName(ProcessingEnvironment mProcessingEnv, Element varElement) {
        return mProcessingEnv.getElementUtils().getPackageOf(varElement).getQualifiedName().toString();
    }
}
